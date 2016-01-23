package challenge

import java.util.UUID

trait Api {
  def readGraph(): Iterable[(UUID,
    String,
    scala.collection.mutable.Set[UUID])]

  def nodeCreate(id: String): (UUID, String)
  def nodeDelete(uuid: String): Unit

  def linkCreate(start: String, stop: String): (UUID, UUID)
  def linkDelete(start: String, stop: String)
}