package challenge

import org.scalatest.{GivenWhenThen, WordSpec}

import scala.language.postfixOps

object DirectedGraphSpec {
  val graph = new LabeledDirectedGraphImpl
  val n1 = graph Node "start"
  val n2 = graph Node "n2"
  val n3 = graph Node "n3"
  val n4 = graph Node "n4"
  val n5 = graph Node "n5"
  val n6 = graph Node "end"
}

class DirectedGraphSpec extends WordSpec with GivenWhenThen {

  import DirectedGraphSpec._

  "A Labeled Directed Graph" when {
    "partial filled in" should {

      "have 6 nodes" in {
        assert(graph.graphStore.size == 6)
      }

      n1 --> n2 weigth = 2
      n1 --> n3 weigth = 1
      n2 --> n4 weigth = 1
      n3 --> n4 weigth = 3
      n2 --> n5 weigth = 1
      n4 --> n6 weigth = 1
      n5 --> n6 weigth = 3

      "show in node labeled 'start' 2 links to n2 and n3 and since it's starting point it has no references." in {
        assert(graph.graphStore.get(n1).contains(graph.NodeAttribs("start", Set(graph.EdgeArrow(n2), graph.EdgeArrow(n3)), Set())))
      }

      "show in node labeled 'n2' 2 links to n4 and n5, it's only referenced by node n1" in {
        assert(graph.graphStore.get(n2).contains(graph.NodeAttribs("n2", Set(graph.EdgeArrow(n4), graph.EdgeArrow(n5)), Set(n1))))
      }


      "show in node labeled 'n3' 1 link to n4 only, it's only referenced by node n1" in {
        assert(graph.graphStore.get(n3).contains(graph.NodeAttribs("n3", Set(graph.EdgeArrow(n4)), Set(n1))))
      }


      "show in node labeled 'n4' 1 link to n6 only, it's referenced by nodes n2 and n3" in {
        assert(graph.graphStore.get(n4).contains(graph.NodeAttribs("n4", Set(graph.EdgeArrow(n6)), Set(n2, n3))))
      }

      "show in node labeled 'n5' 1 link to n6 only,, it's referenced by nodes n2 and n6" in {
        assert(graph.graphStore.get(n5).contains(graph.NodeAttribs("n5", Set(graph.EdgeArrow(n6)), Set(n2))))
      }

      "show in node labeled 'end' no link at all, since it's mend to be the ending one, it's referenced by nodes n4 and n5" in {
        assert(graph.graphStore.get(n6).contains(graph.NodeAttribs("end", Set(), Set(n4, n5))))
      }
    }
  }
  "and some nodes are deleted" when {
    val graph0 = new LabeledDirectedGraphImpl
    val n1 = graph0.Node("start")
    val n2 = graph0.Node("n2")
    val n3 = graph0.Node("n3")
    val n4 = graph0.Node("n4")
    val n5 = graph0.Node("n5")
    val n6 = graph0.Node("end")

    n1 --> n2 weigth = 2
    n1 --> n3 weigth = 1
    n2 --> n4 weigth = 1
    n3 --> n4 weigth = 3
    n2 --> n5 weigth = 1
    n4 --> n6 weigth = 1
    n5 --> n6 weigth = 3

    "the beginning node is deleted" should {

      graph0.removeNode(n1.uuid)

      "show in node labeled 'n2' 2 links to n4 and n5, it's only referenced by node n1" in {
        assert(graph0.graphStore.get(n2).contains(graph0.NodeAttribs("n2", Set(graph0.EdgeArrow(n4), graph0.EdgeArrow(n5)), Set())))

      }


      "show in node labeled 'n3' 1 link to n4 only, it's only referenced by node n1" in {
        assert(graph0.graphStore.get(n3).contains(graph0.NodeAttribs("n3", Set(graph0.EdgeArrow(n4)), Set())))
      }


      "show in node labeled 'n4' 1 link to n6 only, it's referenced by nodes n2 and n3" in {
        assert(graph0.graphStore.get(n4).contains(graph0.NodeAttribs("n4", Set(graph0.EdgeArrow(n6)), Set(n2, n3))))
      }

      "show in node labeled 'n5' 1 link to n6 only,, it's referenced by nodes n2 and n6" in {
        assert(graph0.graphStore.get(n5).contains(graph0.NodeAttribs("n5", Set(graph0.EdgeArrow(n6)), Set(n2))))
      }

      "show in node labeled 'end' no link at all, since it's mend to be the ending one, it's referenced by nodes n4 and n5" in {
        assert(graph0.graphStore.get(n6).contains(graph0.NodeAttribs("end", Set(), Set(n4, n5))))
      }

    }

    "starting and ending nodes are deleted" should {
      val graph0 = new LabeledDirectedGraphImpl
      val n1 = graph0.Node("start")
      val n2 = graph0.Node("n2")
      val n3 = graph0.Node("n3")
      val n4 = graph0.Node("n4")
      val n5 = graph0.Node("n5")
      val n6 = graph0.Node("end")

      n1 --> n2 weigth = 2
      n1 --> n3 weigth = 1
      n2 --> n4 weigth = 1
      n3 --> n4 weigth = 3
      n2 --> n5 weigth = 1
      n4 --> n6 weigth = 1
      n5 --> n6 weigth = 3

      graph0.removeNode(n6.uuid)
      graph0.removeNode(n1.uuid)

      "show in node labeled 'n2' 2 links to n4 and n5, it's only referenced by node n1" in {
        assert(graph0.graphStore.get(n2).contains(graph0.NodeAttribs("n2", Set(graph0.EdgeArrow(n4), graph0.EdgeArrow(n5)), Set())))
      }


      "show in node labeled 'n3' 1 link to n4 only, it's only referenced by node n1" in {
        assert(graph0.graphStore.get(n3).contains(graph0.NodeAttribs("n3", Set(graph0.EdgeArrow(n4)), Set())))
      }


      "show in node labeled 'n4' 1 link to n6 only, it's referenced by nodes n2 and n3" in {
        assert(graph0.graphStore.get(n4).contains(graph0.NodeAttribs("n4", Set(), Set(n2, n3))))
      }

      "show in node labeled 'n5' 1 link to n6 only,, it's referenced by nodes n2 and n6" in {
        assert(graph0.graphStore.get(n5).contains(graph0.NodeAttribs("n5", Set(), Set(n2))))
      }
    }


  }

  "a link is being deleted" when {
    val graph0 = new LabeledDirectedGraphImpl
    val n1 = graph0.Node("start")
    val n2 = graph0.Node("n2")
    val n3 = graph0.Node("n3")
    val n4 = graph0.Node("n4")
    val n5 = graph0.Node("n5")
    val n6 = graph0.Node("end")

    n1 --> n2 weigth = 2
    n1 --> n3 weigth = 1
    n2 --> n4 weigth = 1
    n3 --> n4 weigth = 3
    n2 --> n5 weigth = 1
    n4 --> n6 weigth = 1
    n5 --> n6 weigth = 3

    "with a link is deleted between n2 and 4" should {
      graph0.removeLink(n2.uuid, n4.uuid)
      "show in node labeled 'n2' 2 links to n4 and n5, it's only referenced by node n1" in {
        assert(graph0.graphStore.get(n2).contains(graph0.NodeAttribs("n2", Set(graph0.EdgeArrow(n5)), Set(n1))))
      }


      "show in node labeled 'n3' 1 link to n4 only, it's only referenced by node n1" in {
        assert(graph0.graphStore.get(n3).contains(graph0.NodeAttribs("n3", Set(graph0.EdgeArrow(n4)), Set(n1))))
      }

      "show in node labeled 'n4' 1 link to n6 only, it's referenced by nodes n2 and n3" in {
        assert(graph0.graphStore.get(n4).contains(graph0.NodeAttribs("n4", Set(graph0.EdgeArrow(n6)), Set(n3))))
      }

      "show in node labeled 'n5' 1 link to n6 only,, it's referenced by nodes n2 and n6" in {
        assert(graph0.graphStore.get(n5).contains(graph0.NodeAttribs("n5", Set(graph0.EdgeArrow(n6)), Set(n2))))
      }

      "show in node labeled 'end' no link at all, since it's mend to be the ending one, it's referenced by nodes n4 and n5" in {
        assert(graph0.graphStore.get(n6).contains(graph0.NodeAttribs("end", Set(), Set(n4, n5))))
      }

    }

    "non-existing link is attempted to be deleted" should {
      val graph0 = new LabeledDirectedGraphImpl
      val n1 = graph0.Node("start")
      val n2 = graph0.Node("n2")
      val n3 = graph0.Node("n3")
      val n4 = graph0.Node("n4")
      val n5 = graph0.Node("n5")
      val n6 = graph0.Node("end")

      n1 --> n2 weigth = 2
      n1 --> n3 weigth = 1
      n2 --> n4 weigth = 1
      n3 --> n4 weigth = 3
      n2 --> n5 weigth = 1
      n4 --> n6 weigth = 1
      n5 --> n6 weigth = 3

      graph0.removeLink(n2.uuid, n3.uuid)

      "show in node labeled 'n2' 2 links to n4 and n5, it's only referenced by node n1" in {
        assert(graph0.graphStore.get(n2).contains(graph0.NodeAttribs("n2", Set(graph0.EdgeArrow(n4), graph0.EdgeArrow(n5)), Set(n1))))
      }


      "show in node labeled 'n3' 1 link to n4 only, it's only referenced by node n1" in {
        assert(graph0.graphStore.get(n3).contains(graph0.NodeAttribs("n3", Set(graph0.EdgeArrow(n4)), Set(n1))))
      }


      "show in node labeled 'n4' 1 link to n6 only, it's referenced by nodes n2 and n3" in {
        assert(graph0.graphStore.get(n4).contains(graph0.NodeAttribs("n4", Set(graph0.EdgeArrow(n6)), Set(n2, n3))))
      }

      "show in node labeled 'n5' 1 link to n6 only,, it's referenced by nodes n2 and n6" in {
        assert(graph0.graphStore.get(n5).contains(graph0.NodeAttribs("n5", Set(graph0.EdgeArrow(n6)), Set(n2))))
      }
    }

    "a link is attempted to be deleted but the nodes are in the reversed order" should {
      val graph0 = new LabeledDirectedGraphImpl
      val n1 = graph0.Node("start")
      val n2 = graph0.Node("n2")
      val n3 = graph0.Node("n3")
      val n4 = graph0.Node("n4")
      val n5 = graph0.Node("n5")
      val n6 = graph0.Node("end")

      n1 --> n2 weigth = 2
      n1 --> n3 weigth = 1
      n2 --> n4 weigth = 1
      n3 --> n4 weigth = 3
      n2 --> n5 weigth = 1
      n4 --> n6 weigth = 1
      n5 --> n6 weigth = 3

      graph0.removeLink(n4.uuid, n2.uuid)

      "show in node labeled 'n2' 2 links to n4 and n5, it's only referenced by node n1" in {
        assert(graph0.graphStore.get(n2).contains(graph0.NodeAttribs("n2", Set(graph0.EdgeArrow(n4), graph0.EdgeArrow(n5)), Set(n1))))
      }


      "show in node labeled 'n3' 1 link to n4 only, it's only referenced by node n1" in {
        assert(graph0.graphStore.get(n3).contains(graph0.NodeAttribs("n3", Set(graph0.EdgeArrow(n4)), Set(n1))))
      }


      "show in node labeled 'n4' 1 link to n6 only, it's referenced by nodes n2 and n3" in {
        assert(graph0.graphStore.get(n4).contains(graph0.NodeAttribs("n4", Set(graph0.EdgeArrow(n6)), Set(n2, n3))))
      }

      "show in node labeled 'n5' 1 link to n6 only,, it's referenced by nodes n2 and n6" in {
        assert(graph0.graphStore.get(n5).contains(graph0.NodeAttribs("n5", Set(graph0.EdgeArrow(n6)), Set(n2))))
      }
    }
  }
}