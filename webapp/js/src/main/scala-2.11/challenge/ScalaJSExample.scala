package challenge

import java.util.UUID

import autowire._
import org.scalajs.dom
import upickle.Js
import upickle.default._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scala.util.{Failure, Success}

object Client extends autowire.Client[Js.Value, Reader, Writer] {

  override def doCall(req: Request): Future[Js.Value] = dom.ext.Ajax.post(
    url = "/api/" + req.path.mkString("/"),
    data = upickle.json.write(Js.Obj(req.args.toSeq: _*))
  ).map(_.responseText)
    .map(upickle.json.read)

  def read[Result: Reader](p: Js.Value) = readJs[Result](p)

  def write[Result: Writer](r: Result) = writeJs(r)
}

@JSExport
object ScalaJSExample {
  // set up initial nodes and links
  //  - nodes are known by 'id', not by index in array.
  //  - reflexive edges are indicated on the node (as a bold black circle).
  //  - links are always source < target; edge directions are set by 'left' and 'right'.


  @JSExport
  val (nodes, links) = (js.Array[js.Dynamic](), js.Array[js.Dynamic]())

  @JSExport
  def GraphSetUp() = {
    def updateOrCreate(nod: (UUID, String, Set[UUID])) = {

      val finding = nodes.find(_.uuid == nod._1.toString)

      if (finding.isDefined) {
        val index = nodes.indexOf(finding.get)
        if (nod._2 != "") nodes(index).id = nod._2 // (Re-)set id
        nodes(index)
      } else {
        val newNode = js.Dynamic.literal(id = nod._2, reflexive = false, uuid = nod._1.toString)
        nodes += newNode
        newNode
      }
    }

    Client[Api].readGraph().call().onComplete {
      case Success(value) =>
        value.foreach(nod => {
          val newSource = updateOrCreate(nod)

          nod._3.foreach(linkedNode => {
            val target = updateOrCreate((linkedNode, "", Set()))
            links += js.Dynamic.literal(source = newSource, target = target, left = true, right = false)
          }
          )
        }
        )
        js.Dynamic.global.restart()
      case Failure(e) => e.printStackTrace()
    }

  }

  @JSExport
  def linkCreated(pSource: js.Dynamic, pTarget: js.Dynamic, pLeft: Boolean, pRight: Boolean) = {
    Client[Api].linkCreate(pSource.uuid.toString, pTarget.uuid.toString).call().onComplete {
      case Success(value) =>
        links += js.Dynamic.literal(source = pSource, target = pTarget, left = pLeft, right = pRight)
        js.Dynamic.global.restart()
      case Failure(e) => e.printStackTrace()
    }
  }

  @JSExport
  def nodeCreated(x0: Double, y0: Double) = {

    Client[Api].nodeCreate("").call().onComplete {
      case Success(value) =>
        nodes += js.Dynamic.literal(id = value._2, reflexive = false, x = x0, y = y0, uuid = value._1.toString)
        js.Dynamic.global.restart()
      case Failure(e) => e.printStackTrace()
    }
  }

  @JSExport
  def nodeDeleted(uuid: String) = {
    Client[Api].nodeDelete(uuid).call().onComplete {
      case Success(value) => // Will be processed in global app.js
      case Failure(e) => e.printStackTrace()
    }
  }

  @JSExport
  def linkDeleted(uuid1: String, uuid2: String) = {
    Client[Api].linkDelete(uuid1, uuid2).call().onComplete {
      case Success(value) => // Will be processed in global app.js
      case Failure(e) => e.printStackTrace()
    }
  }
}
