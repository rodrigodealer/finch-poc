package com.github.rodrigodealer.controllers

import java.util.UUID

import com.github.rodrigodealer.todo.Todo
import io.circe.generic.auto._
import io.finch.circe._
import io.finch.{Created, Endpoint, Ok, delete, get, jsonBody, patch, path, post}

class TodoController {

  def postedTodo: Endpoint[Todo] = jsonBody[UUID => Todo].map(_(UUID.randomUUID()))

  def postTodo: Endpoint[Todo] = post("todos" :: postedTodo) { t: Todo =>
    Todo.save(t)

    Created(t)
  }

  def getTodos: Endpoint[List[Todo]] = get("todos") {
    Ok(Todo.list())
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

  def routes = {
    getTodos :+: postTodo :+: deleteTodo :+: deleteTodos :+: patchTodo
  }

}
