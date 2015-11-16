package challenge

import java.util.UUID

case class Link(from: UUID, to: UUID)

trait Api {
  def list(path: String): Seq[String]

  def nodeCreate(id: String): UUID
  def nodeRead(uuid: UUID): Option[(Int, String)] //
  def nodeDelete(uuid: String) : Unit

  def linkCreate(start: String, stop: String): (UUID, UUID)
//  def linkRead(line: Link): Option[Link]
  def linkDelete(start: String, stop: String)

}