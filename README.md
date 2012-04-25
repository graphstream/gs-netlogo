# gs-netlogo

gs-netlogo is a [NetLogo](http://ccl.northwestern.edu/netlogo/index.shtml) extension connecting NetLogo to other applications supporting the [NetStream](https://github.com/graphstream/gs-netstream) protocol, for example [GraphStream](http://graphstream-project.org/)-based applications. NetLogo agents can use its primitives to send graph events. By receiving and processing these events, the external application can maintain a dynamic graph view of a NetLogo simulation. The nodes of this graph correspond to the turtles and its edges are the links between them. The external application can create a feedback loop by injecting input back to the NetLogo simulation.

Check the [wiki](https://github.com/sbalev/gs-netlogo/wiki) for more information about this extension.

