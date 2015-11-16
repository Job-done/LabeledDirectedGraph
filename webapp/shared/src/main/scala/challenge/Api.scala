package challenge

import java.util.UUID

trait Api {
  // def readGraph() :
  def nodeCreate(id: String): UUID
  def nodeDelete(uuid: String) : Unit

  def linkCreate(start: String, stop: String): (UUID, UUID)
  def linkDelete(start: String, stop: String)
}