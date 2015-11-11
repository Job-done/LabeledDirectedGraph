package challenge

import java.util.UUID

import scala.annotation.tailrec
import scala.collection.mutable
import scala.language.postfixOps

abstract class Graph() {
  type Edge
  type Vertex <: VertexIntf

  abstract class VertexIntf() {
    def connectWith(node: Vertex): Edge
  }

  def addNode(lbl: String): Vertex

  protected def newVertex(lbl: String): Vertex

  protected def newEdge(from: Vertex, to: Vertex): Edge
}

abstract class DirectedGraph() extends Graph {
  val graphStore = mutable.Map[Vertex, NodeAttribs]()
  val uniqueLabels = mutable.Map[String, Vertex]()

  case class EdgeArrow(endNode: Vertex /*, EdgeAttribs*/)

  case class NodeAttribs(label: String, connectedNodes: Set[EdgeArrow])

  class VertexImpl extends VertexIntf {
    self: Vertex =>

    def connectWith(node: Vertex): Edge = {
      val edge = newEdge(self, node)
      val tempAttribs = graphStore.getOrElseUpdate(self, NodeAttribs("", Set()))

      graphStore(self) = NodeAttribs(tempAttribs.label, tempAttribs.connectedNodes + EdgeArrow(node))
      edge
    }
  }

  protected def newVertex(lbl: String): Vertex

  protected def newEdge(from: Vertex, to: Vertex): Edge

  def addNode(lbl: String): Vertex = newVertex(lbl)
}


trait DataExt {
  var costs = Double.PositiveInfinity
}

trait LabeledDirectedGraphImpl extends DirectedGraph {
  type Vertex = Node
  type Edge = LinkX

  class Node(uuid: UUID) extends VertexImpl {
    override def connectWith(node: Vertex): Edge = super.connectWith(node)

    def -->(n2: Vertex): Edge = connectWith(n2)

    override def toString = graphStore.getOrElse(this, NodeAttribs("nix", Set())).label
  }

  object Node {

    def apply(label: String) = {
      def mkUnqLabel(lbl: String): String = {
        @tailrec
        def mkUnqInner(lbl: String, count: Int): String = {
          if (uniqueLabels.contains(lbl)) mkUnqInner(s"$lbl${if (count == 0) "" else s" [$count]"}", count + 1)
          else lbl
        }
        mkUnqInner(lbl, 0)
      }


      val node = new Node(UUID.randomUUID())
      val lbl = mkUnqLabel(label)
      graphStore(node) = NodeAttribs(lbl, Set())
      if (lbl != "") uniqueLabels(lbl) = node
      node
    }
  }

  class LinkX(from: Node, to: Node) extends /*EdgeImpl(from, to) with */ DataExt {
    // override def toString = from.toString + " --> " + to.toString + " w:" + costs
  }

  def addNewNode(label: String): Node = super.addNode(label)

  override def toString = graphStore.toString()

  protected def newEdge(from: Node, to: Node) = new LinkX(from, to)

  protected def newVertex(lbl: String) = Node(lbl)
}

object Example  {

  val graph = new LabeledDirectedGraphImpl {}

  val n1 = graph addNewNode "start"
  val n2 = graph addNewNode "n2"
  val n3 = graph addNewNode "n3"
  val n4 = graph addNewNode "n4"
  val n5 = graph addNewNode "n5"
  val n6 = graph addNewNode "end"

  n1 --> n2 costs = 2
  n1 --> n3 costs = 1
  n2 --> n4 costs = 1
  n3 --> n4 costs = 3
  n2 --> n5 costs = 1
  n4 --> n6 costs = 1
  n5 --> n6 costs = 3
  println(graph)
}