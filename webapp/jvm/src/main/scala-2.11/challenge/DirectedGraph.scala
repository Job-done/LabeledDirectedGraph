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

  // The sole label of vertex/node with its referencing vertexes AND starting edges/links
  case class NodeAttribs(label: String, forward: mutable.Set[EdgeArrow], backward: mutable.Set[Vertex])

  class VertexImpl extends VertexIntf {
    self: Vertex =>

    def connectWith(node: Vertex): Edge = {
      val edge = newEdge(self, node)
      val tempAttribsEnd = graphStore.getOrElseUpdate(self, NodeAttribs("", mutable.Set(), mutable.Set()))
      val tempAttribsStart = graphStore.getOrElseUpdate(node, NodeAttribs("", mutable.Set(), mutable.Set()))

      // Make a updated set of originated vertexes and destinated ones, and the other way around.
      graphStore(self) = NodeAttribs(tempAttribsEnd.label,
        tempAttribsEnd.forward + new EdgeArrow(node),
        tempAttribsEnd.backward)

      graphStore(node) = NodeAttribs(tempAttribsStart.label,
        tempAttribsStart.forward,
        tempAttribsStart.backward + self)

      edge
    }
  }

  protected def newEdge(from: Vertex, to: Vertex): Edge
}

trait DataExt {
  var weigth = Double.PositiveInfinity
}

/**
  * Final implementation
  * Based on a store Map[Vertex, NodeAttribs]
  * where in NodeAtribs label, set of destinations and a set of origins.
  * Origins are used to backtrack Node e.g. for deletion
  */
class LabeledDirectedGraphImpl extends DirectedGraph {
  type Vertex = Node
  type Edge = LinkX

  case class Node(uuid: UUID) extends VertexImpl {
    override def connectWith(node: Vertex): Edge = super.connectWith(node)

    def -->(n2: Vertex): Edge = connectWith(n2)

    override def toString = graphStore.getOrElse(this, NodeAttribs("nix", mutable.Set(), mutable.Set())).label
  }

  /**
    * Companion object Node
    * Apply methode creates a named or unnamed Node
    */
  object Node {
    // Creation of a named node and maintaining label uniqueness
    def apply(label: String) = {
      // Called by HttpServer.nodeCreate
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
      graphStore(node) = NodeAttribs(lbl, mutable.Set(), mutable.Set())
      // Update the list for unique labels
      if (lbl != "") uniqueLabels(lbl) = node
      node
    }
  }

  // Link with an extension
  class LinkX(from: Node, to: Node) extends /*EdgeImpl(from, to) with */ DataExt {
    // override def toString = from.toString + " --> " + to.toString + " w:" + weigth
  }

  override def toString = graphStore.toString()

  // Remove the node from the connected node list
  def removeStartingEnds(nod: Node, node2removed: Node) =
    if (graphStore.contains(nod)) graphStore(nod).forward.remove(new EdgeArrow(node2removed))

  // Remove the node from the connected node list
  def removeEndingEnds(nod: Node, node2removed: Node) =
    if (graphStore.contains(nod)) graphStore(nod).backward.remove(node2removed)

  /**
    * Delete a link
    */
  def removeLink(arrowTail: UUID, arrowHead: UUID) = {
    // Called by HttpServer.linkDelete
    val (head, tail) = (Node(arrowTail), Node(arrowHead))
    removeStartingEnds(head, tail)
    removeEndingEnds(tail, head)
  }

  /**
    * Delete a Node
    */
  def removeNode(uuid: UUID) = {
    // Called by nodeDelete
    val node2remove = new Node(uuid)
    val nodeAttr = graphStore.remove(node2remove)
    if (nodeAttr.isDefined) {
      // Remove all reference in the connectedNode sets
      nodeAttr.get.backward.foreach { nod => removeStartingEnds(nod, node2remove) }
      nodeAttr.get.forward.foreach { nod => removeEndingEnds(nod.endNode, node2remove) }
    }

  }

  /**
    * Creation of a link
    */
  def createLink(start: String, stop: String): (UUID, UUID) = {
    // Called HttpServer.linkCreate
    val (from, to) = (UUID.fromString(start), UUID.fromString(stop))
    Node(from) --> Node(to)
    (from, to)
  }

  def readGraph(): Iterable[(UUID, String, mutable.Set[UUID])] = {
    for (key <- graphStore.keys;
         value = graphStore(key)
    )
      yield (key.uuid, value.label, value.forward.map { nod => nod.endNode.uuid })
  }

  protected def newEdge(from: Node, to: Node) = new LinkX(from, to)
}