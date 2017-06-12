package com.github.rodrigodealer

import java.util.UUID

case class TodoNotFound(id: UUID) extends Exception {
  override def getMessage: String = s"Todo(${id.toString}) not found."
}