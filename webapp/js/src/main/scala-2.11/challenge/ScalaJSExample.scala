package challenge

import java.util.UUID

import autowire._
import org.scalajs.dom
import org.scalajs.dom.MouseEvent
import upickle.Js
import upickle.default._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

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
  @JSExport
  def nodeCreated(y: Int): Future[UUID] = {
    def a: Future[UUID] = Client[Api].nodeCreate(y.toString).call()
    a
  }

  @JSExport
  def main(): Unit = {
    val inputBox = input.render
    val outputBox = div.render
    val actionButton = button("Do something").render

    def updateOutput() = {
      Client[Api].list(inputBox.value).call().foreach { paths =>
        outputBox.innerHTML = ""
        outputBox.appendChild(
          ul(
            for (path <- paths) yield {
              li(path)
            }
          ).render
        )
      }
    }

    inputBox.onkeyup = { (e: dom.Event) =>
      updateOutput()
    }

    actionButton.onclick =
      (_: MouseEvent) => {
        // Client[Api].nodeCreate().call().foreach{item => outputBox.innerHTML = item.toString}

      }

    updateOutput()
    dom.document.body.appendChild(
      div(
        cls := "container",
        h1("File Browser"),
        p("Enter a file path to s"),
        inputBox,

        actionButton,
        outputBox
      ).render
    )
  }
}
