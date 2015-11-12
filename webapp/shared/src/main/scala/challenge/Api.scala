package challenge

import java.util.UUID

case class Link(from: UUID, to: UUID)

object Link {
  def dummyLink() = {
    Link(UUID.randomUUID(), UUID.randomUUID())
  }
}

trait Api {
  def list(path: String): Seq[String]

  def nodeCreate(id: String): UUID
  def nodeRead(uuid: UUID): Option[(Int, String)]
  def nodeUpdate(uuid: UUID): UUID
  def nodeDelete(uuid: UUID)

  def linkCreate(line: Link): Option[Link]
  def linkRead(line: Link): Option[Link]
  def linkUpdate(oldLine: Link, newLine: Link): Option[Link]
  def linkDelete(line: Link)

}