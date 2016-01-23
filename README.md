## Labeled Directed Graph Challenge

A web application with Spray server and ScalaJS client.
Featuring a mathematical graph, creating and editing.
Using Spray Autowire RPC.

Follow these steps to get started:

1. Git-clone [this repository](http://github.com/Job-done/LabeledDirectedGraph/).

        $ git clone git://github.com/Job-done/LabeledDirectedGraph.git my-project

2. Change directory into your clone:

        $ cd my-project

3. Launch SBT:

        $ sbt

4. Compile optional everything and run all tests:

        > test

5. To start the application:

        > reStart

6. Browse to [http://localhost:8080/](http://localhost:8080/)
    1. Call your friends or colleagues to access your server if you know how to access your computer by IP address.
    

7. Stop the application:

        > reStop

8. [Learn](http://lihaoyi.github.io/hands-on-scala-js/#Autowire) about Autowire in Hands-on Scala.js.
## Client usage

Click in the open space to add a node, drag from one node to another to add an edge. 
Ctrl-drag a node to move the graph layout. 
Click a node or an edge to select it.

When a node is selected, Delete removes the node. 
When an edge is selected: L(eft), R(ight) change direction, Delete removes the edge.
** Build Configuration
The `crossProject` function provided by the Scala.js plugin provides to set up two projects: one in the `webapp/js/` folder and one in the `webapp/jvm/` folder.
The common definition of the API is shared between them in the `shared` folder.