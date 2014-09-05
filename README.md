ImageStreamBW
=============
Author : Roberto Belli
=============
This project generates and convert a stream of colored images in Black And White.
It's java based, and it uses the Skandium and JOCL libraries.

Skandium is a Java based Algorithmic Skeleton library for high-level parallel programming of multi-core architectures.
Skandium provides basic nestable parallelism patterns, which can be composed to program more complex applications.
The supported parallelism patterns are: farm (master-slave), pipe, for, while, if, map, fork, and divide and conquer.

JOCL instead is a java wrapper of the OpenCL library.

The Project tries to exploit as much as parallelism as it can,
also enabling multiple threads for reading as fast as possible images from the disk. 
