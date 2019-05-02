# P2P
IP Project 1

This Project has been implemented in Java.
This project has been implemented and tested on Java JDK 1.8 in the VCL instance for CSC573+jdk image. This is available on VCL and we strongly recommend using this image as it has been tested on that environment.
We recommend using two vcl VMs to test this, however you can also test on a single instance.
Steps to follow:
1. Extract the folder
2. Start Server: (On server VM)
	- cd src/
	- java TCPServer
3. Start Client: (On client VM)
	- cd src/
	- java Client

The Centralized server waits for connections from the peers on the well-known port 7734.
When Client is run it creates a peer who wishes to join the system, it first instantiates an upload server process listening to any available local
port. It then creates a connection to the server at the well-known port 7734 and passes information about itself.
There are three methods:
• ADD, to add a locally available RFC to the server’s index,
• LOOKUP, to find peers that have the specified RFC, and
• LIST, to request the whole index of RFCs from the server.
