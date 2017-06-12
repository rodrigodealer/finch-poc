import java.util.UUID

import com.github.rodrigodealer.Todo
import com.twitter.app.Flag
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.stats.Counter
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import io.circe.generic.auto._
import io.finch._
import io.finch.circe._

object Main extends TwitterServer {

  val port: Flag[Int] = flag("port", 8081, "TCP port for HTTP server")

  val todos: Counter = statsReceiver.counter("todos")

  def postedTodo: Endpoint[Todo] = jsonBody[UUID => Todo].map(_(UUID.randomUUID()))

  def postTodo: Endpoint[Todo] = post("todos" :: postedTodo) { t: Todo =>
    todos.incr()
    Todo.save(t)

    Created(t)
  }

  def patchedTodo: Endpoint[Todo => Todo] = jsonBody[Todo => Todo]

  def patchTodo: Endpoint[Todo] =
    patch("todos" :: path[UUID] :: patchedTodo) { (id: UUID, pt: Todo => Todo) =>
      Todo.get(id) match {
        case Some(currentTodo) =>
          val newTodo: Todo = pt(currentTodo)
          Todo.delete(id)
          Todo.save(newTodo)

          Ok(newTodo)
        case None => throw new IllegalStateException("Not found")
      }
    }

  def getTodos: Endpoint[List[Todo]] = get("todos") {
    Ok(Todo.list())
  }

  def deleteTodo: Endpoint[Todo] = delete("todos" :: path[UUID]) { id: UUID =>
    Todo.get(id) match {
      case Some(t) => Todo.delete(id); Ok(t)
      case None => throw new IllegalStateException("Not found")
    }
  }

  def deleteTodos: Endpoint[List[Todo]] = delete("todos") {
    val all: List[Todo] = Todo.list()
    all.foreach(t => Todo.delete(t.id))

    Ok(all)
  }

  val api: Service[Request, Response] = (
    getTodos :+: postTodo :+: deleteTodo :+: deleteTodos :+: patchTodo
    ).handle({
    case e: Exception => NotFound(e)
  }).toServiceAs[Application.Json]

  def main(): Unit = {
    log.info("Serving the Todo application")

    val server = Http.server
      .withStatsReceiver(statsReceiver)
      .serve(s":${port()}", api)

    onExit { server.close() }

    Await.ready(adminHttpServer)
  }
}