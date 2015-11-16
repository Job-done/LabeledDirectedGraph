package challenge

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
  val nodes = js.Array[js.Dynamic](
  js.Dynamic.literal(id= 0, reflexive= false, uuid =  "0"),
  js.Dynamic.literal(id= 1, reflexive= true, uuid =  "1" ))

  @JSExport
  val links = /*new js.Array[js.Dynamic] */
    js.Array(
      js.Dynamic.literal(source = nodes(0), target = nodes(1), left = false, right = true /*, weight = 0*/))
  @JSExport
  var lastNodeId = 1

  @JSExport
  def GraphSetUp() ={

  }


  @JSExport
  def linkCreated(pSource: js.Dynamic, pTarget: js.Dynamic, pLeft: Boolean, pRight: Boolean) = {
    println(s"${pSource.uuid} ,${pTarget.uuid}")
    val a = Client[Api].linkCreate(pSource.uuid.toString, pTarget.uuid.toString).call().onComplete {
      case Success(value) =>
        println(s"Send       :  ${pSource.uuid.toString},${pTarget.uuid.toString}")
        println(s"server says: $value")
      // links += js.Dynamic.literal(source = pSource, target = pTarget, left = pLeft, right = pRight)

      // TODO  restart()
      case Failure(e) => e.printStackTrace()
    }
  }


  @JSExport
  def nodeCreated(x0: Double, y0: Double) = {
    lastNodeId += 1

    Client[Api].nodeCreate(lastNodeId.toString).call().onComplete {
      case Success(value) =>
        nodes += js.Dynamic.literal(id = lastNodeId, reflexive = false, x = x0, y = y0, uuid = value.toString)

      // TODO  restart()
      case Failure(e) => e.printStackTrace()
    }
  }

  @JSExport
  def nodeDeleted(uuid: String) = {
    Client[Api].nodeDelete(uuid).call().onComplete {
      case Success(value) =>

      // TODO  restart()
      case Failure(e) => e.printStackTrace()
    }
  }

  @JSExport
  def linkDeleted(uuid1: String, uuid2: String) = {
    Client[Api].linkDelete(uuid1, uuid2).call().onComplete {
      case Success(value) =>
        println("Link deleted")
      // TODO  restart()
      case Failure(e) => e.printStackTrace()
    }
  }
}
