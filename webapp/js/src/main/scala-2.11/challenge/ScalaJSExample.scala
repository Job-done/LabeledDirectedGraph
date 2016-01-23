package challenge

import java.util.UUID

import autowire._
import org.scalajs.dom
import upickle.Js
import upickle.default._

import scala.collection.mutable
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scala.util.{Failure, Success}

object Client extends autowire.Client[Js.Value, Reader, Writer] {

  override def doCall(req: Request): Future[Js.Value] = dom.ext.Ajax.post(
    url =  req.path.mkString("/api/","/",""),
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

  def addLink(pSource: js.Dynamic, pTarget: js.Dynamic) = {
    links += js.Dynamic.literal(source = pSource, target = pTarget, left = false, right = true)
  }

  /**
    * Conditional update or creation of a node
    *
    * If refering to a existing node try to update with its name
    * A name could be present or absent
    * If the node doesn't exist than create it
    */
  @JSExport
  def GraphSetUp() = {
    def updateOrCreate(uuid: String, name: String) = {

      val finding = nodes.find(_.uuid == uuid)

      if (finding.isDefined) {
        val index = nodes.indexOf(finding.get)
        if (name != "") nodes(index).id = name // (Re-)set id
        nodes(index)
      } else {
        val newNode = js.Dynamic.literal(id = name, reflexive = false, uuid = uuid)
        nodes += newNode
        newNode
      }

    }

    Client[Api].readGraph().call().onComplete {
      case Success(value) =>
        value.foreach { case (uuid: UUID, name: String, links: mutable.Set[UUID]) =>
          val newSource = updateOrCreate(uuid.toString, name)

          // Before adding a link a conditional update or create is called
          links.foreach(linkedNode => addLink(newSource, updateOrCreate(linkedNode.toString, "")))
        }
        js.Dynamic.global.restart()
      case Failure(e) => e.printStackTrace()
    }

  }

  @JSExport
  def linkCreated(pSource: js.Dynamic, pTarget: js.Dynamic, pLeft: Boolean, pRight: Boolean) = {
    Client[Api].linkCreate(pSource.uuid.toString, pTarget.uuid.toString).call().onComplete {
      case Success(value) =>
        links += js.Dynamic.literal(source = pSource, target = pTarget, left = false, right = true)
        js.Dynamic.global.restart()
      case Failure(e) => e.printStackTrace()
    }
  }

  @JSExport
  def nodeCreated(x0: Double, y0: Double) = {

    Client[Api].nodeCreate("").call().onComplete {
      case Success(value) =>
        nodes += js.Dynamic.literal(id = value._2, reflexive = false,reflexive = true,  x = x0, y = y0, uuid = value._1.toString)
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
