package challenge

import java.util.UUID

import scala.annotation.tailrec
import scala.collection.mutable
import scala.language.postfixOps

/**
 * Abstraction of a normal graph with its vertexes and edges.
 * At the concrete level these are named nodes and links.
 */
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
/**
 * Partial implementation and Specialization of the graph with directed lines
 */
abstract class DirectedGraph() extends Graph {
  // Store for the whole graph inclusive the edges included in the `NodeAttribs` class
  val graphStore = mutable.Map[Vertex, NodeAttribs]()
  // Index to keep the vertex labels unique
  val uniqueLabels = mutable.Map[String, Vertex]()
  // Part of a edge, the endpoint
  case class EdgeArrow(endNode: Vertex /*, EdgeAttribs*/)
  // Part of a edge, the starting point
  case class EdgeTail(endNode: Vertex /*, EdgeAttribs*/)
  // The sole label of vertex/node with its starting edges/links
  case class NodeAttribs(label: String, connectedNodes: Set[EdgeArrow], backTrack: Set[EdgeTail])

  class VertexImpl extends VertexIntf {
    self: Vertex =>

    def connectWith(node: Vertex): Edge = {
      val edge = newEdge(self, node)
      val tempAttribs = graphStore.getOrElseUpdate(self, NodeAttribs("", Set(),Set()))

      graphStore(self) = NodeAttribs(tempAttribs.label, tempAttribs.connectedNodes + EdgeArrow(node),Set())
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

/**
 * Final implementation as a trait
 */
trait LabeledDirectedGraphImpl extends DirectedGraph {
  type Vertex = Node
  type Edge = LinkX

  class Node(uuid: UUID) extends VertexImpl {
    override def connectWith(node: Vertex): Edge = super.connectWith(node)

    def -->(n2: Vertex): Edge = connectWith(n2)

    override def toString = graphStore.getOrElse(this, NodeAttribs("nix", Set(),Set())).label
  }
  // Companion object Node
  object Node {
    // Creation of a new node and maintaining label uniqueness
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
      graphStore(node) = NodeAttribs(lbl, Set(),Set())
      // Update the list for unique labels
      if (lbl != "") uniqueLabels(lbl) = node
      node
    }
  }

  // Link with an extension
  class LinkX(from: Node, to: Node) extends /*EdgeImpl(from, to) with */ DataExt {
    // override def toString = from.toString + " --> " + to.toString + " w:" + costs
  }

  def addNewNode(label: String): Node = super.addNode(label)

  override def toString = graphStore.toString()

  // TODO remove elements
  def removeLink (arrowTail: UUID, arrowHead: UUID) = ???

  def removeNode(uuid: UUID) = {
    val node2remove = new Node(uuid)
    val nodeAttr = graphStore.remove(node2remove)
    if (nodeAttr.isDefined) {
      // Remove all reference in the connectedNode sets
      nodeAttr.get.backTrack.foreach{ // Remove the node from the connected node list
        nod => graphStore.getOrElse(nod.endNode, NodeAttribs("nix", Set(),Set())).connectedNodes}
    }

  }

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