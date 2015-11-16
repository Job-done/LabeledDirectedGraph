package challenge

import java.util.UUID

case class Link(from: UUID, to: UUID)

trait Api {
  def nodeCreate(id: String): UUID
  def nodeDelete(uuid: String) : Unit

  def linkCreate(start: String, stop: String): (UUID, UUID)
  def linkDelete(start: String, stop: String)
}