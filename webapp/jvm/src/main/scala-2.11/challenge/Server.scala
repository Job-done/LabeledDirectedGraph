package challenge

import java.util.UUID

import akka.actor.ActorSystem
import spray.http.{HttpEntity, MediaTypes}
import spray.routing.SimpleRoutingApp
import upickle.Js
import upickle.default._

import scala.concurrent.ExecutionContext.Implicits.global

object Template {

  import scalatags.Text.all._
  import scalatags.Text.tags2.title

  val txt =
    "<!DOCTYPE html>" +
      html(
        head(
          meta(httpEquiv := "Content-Type", content := "text/html; charset=UTF-8"),
          title("Labeled Directed Graph Challenge"),
          script(`type` := "text/javascript", src := "/client-fastopt.js"),
          //          script(`type` := "text/javascript", src := "//localhost:12345/workbench.js"),
          script(`type` := "text/javascript", src := "http://d3js.org/d3.v3.min.js"),
          link(
            rel := "stylesheet",
            `type` := "text/css",
            href := "app.css"
          )
        ),
        body(margin := 0)(
          script(`type` := "text/javascript", src := "app.js")
          /*script("challenge.ScalaJSExample().main()")*/
        )
      )
}

object AutowireServer extends autowire.Server[Js.Value, Reader, Writer] {
  def read[Result: Reader](p: Js.Value) = upickle.default.readJs[Result](p)

  def write[Result: Writer](r: Result) = upickle.default.writeJs(r)
}

object Server extends SimpleRoutingApp with Api {
  val graph = new LabeledDirectedGraphImpl

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    startServer("0.0.0.0", port = 8080) {
      get {
        pathSingleSlash {
          complete {
            HttpEntity(
              MediaTypes.`text/html`,
              Template.txt
            )
          }
        } ~
          getFromResourceDirectory("")
      } ~
        post {
          path("api" / Segments) { s =>
            extract(_.request.entity.asString) { e =>
              complete {
                AutowireServer.route[Api](Server)(
                autowire.Core.Request(s,
                  upickle.json.read(e).asInstanceOf[Js.Obj].value.toMap
                )
              ).map(upickle.json.write)
              }
            }
          }
        }
    }
  }

  def list(path: String) = {
    val chunks = path.split("/", -1)
    val prefix = "./" + chunks.init.mkString("/")
    val files = Option(new java.io.File(prefix).list()).toSeq.flatten
    files.filter(_.startsWith(chunks.last))
  }

  def nodeCreate(): UUID = {
    UUID.randomUUID()
  }

  def nodeCreate(id: String): UUID = {
    val ret = graph.Node(id).uuid
    println("Server:" + graph)
    ret
  }

  def nodeRead(uuid: UUID): Option[(Int, String)] = {
    Some(1, "")
  }

  def nodeUpdate(uuid: UUID): UUID = {
    UUID.randomUUID()
  }

  def nodeDelete(uuid: String): Unit = {
    graph.removeNode(UUID.fromString(uuid))
  }

  def linkCreate(start: String, stop: String): (UUID, UUID) = {
    val ret =graph.createLink(start, stop)
    println("Server: Link added " + graph)
    ret
  }

//  def linkRead(line: Link): Option[Link] = Some(Link.dummyLink())

  def linkDelete(uuid1: String, uuid2: String) = {
    graph.removeLink(UUID.fromString(uuid1), UUID.fromString(uuid2))
    println("Server: Link deleted " + graph)
  }

}
