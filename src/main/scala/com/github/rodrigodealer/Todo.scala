package com.github.rodrigodealer

import java.util.UUID

import scala.collection.mutable

case class Todo(id: UUID, title: String, completed: Boolean, order: Int)

object Todo {
  private[this] val db: mutable.Map[UUID, Todo] = mutable.Map.empty[UUID, Todo]

  def get(id: UUID): Option[Todo] = synchronized { db.get(id) }
  def list(): List[Todo] = synchronized { db.values.toList }
  def save(t: Todo): Unit = synchronized { db += (t.id -> t) }
  def delete(id: UUID): Unit = synchronized { db -= id }
}
