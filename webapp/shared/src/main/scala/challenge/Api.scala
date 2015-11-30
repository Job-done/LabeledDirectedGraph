package challenge

import java.util.UUID

trait Api {
  def readGraph(): Iterable[(java.util.UUID, String, scala.collection.mutable.Set[java.util.UUID])]

  def nodeCreate(id: String): (UUID, String)
  def nodeDelete(uuid: String): Unit

  def linkCreate(start: String, stop: String): (UUID, UUID)
  def linkDelete(start: String, stop: String)
}