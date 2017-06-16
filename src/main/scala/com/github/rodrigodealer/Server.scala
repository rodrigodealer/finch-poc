import java.util.UUID

import com.github.rodrigodealer.controllers.TodoController
import com.github.rodrigodealer.todo.Todo
import com.netflix.governator.guice.LifecycleInjector
import com.twitter.app.Flag
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import io.circe.generic.auto._
import io.finch._
import io.finch.circe._

object Main extends TwitterServer {

  private val injector = LifecycleInjector.builder()
    .usingBasePackages("controllers", "service")
    .build().createInjector()

  private val todoController = injector.getInstance(classOf[TodoController])

  private val port: Flag[Int] = flag("port", 8081, "TCP port for HTTP server")

  private val api: Service[Request, Response] = {
    todoController.routes
  }.handle({
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