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
        )
      )
}

object AutowireServer extends autowire.Server[Js.Value, Reader, Writer] {
  def read[Result: Reader](p: Js.Value) = upickle.default.readJs[Result](p)

  def write[Result: Writer](r: Result) = upickle.default.writeJs(r)
}

object HttpServer extends SimpleRoutingApp with Api {
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
                AutowireServer.route[Api](HttpServer)(
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

  var lastNodeNr = 0

  def nodeCreate(id: String): (UUID, String) = {
    val ids = if (id == "") {
      lastNodeNr += 1; lastNodeNr.toString
    } else id
    val ret = graph.Node(ids).uuid
    println("HttpServer: Node created " + graph)
    (ret, ids)
  }

  def nodeDelete(uuid: String): Unit = {
    graph.removeNode(UUID.fromString(uuid))
    println("HttpServer: Node deleted " + graph)
  }

  def linkCreate(start: String, stop: String): (UUID, UUID) = {
    val ret = graph.createLink(start, stop)
    println("HttpServer: Link created " + graph)
    ret
  }

  def linkDelete(uuid1: String, uuid2: String) = {
    graph.removeLink(UUID.fromString(uuid1), UUID.fromString(uuid2))
    println("HttpServer: Link deleted " + graph)
  }

  def readGraph() = graph.readGraph()
}
