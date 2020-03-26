[![security status](https://www.meterian.io/badge/gh/matteobaccan/JavaPortListener/security)](https://www.meterian.io/report/gh/matteobaccan/JavaPortListener)
[![stability status](https://www.meterian.io/badge/gh/matteobaccan/JavaPortListener/stability)](https://www.meterian.io/report/gh/matteobaccan/JavaPortListener)
[![DepShield Badge](https://depshield.sonatype.org/badges/matteobaccan/JavaPortListener/depshield.svg)](https://depshield.github.io)

# JavaPortListener
Java port Listener is a command line port listener written in Java

I think that this program may also works with all JDK version, you must only
recompile it.

I have written this program to allow programmer to have one port listener to
use in all operating system that use, and not one port listener for every
system.

Use this program in Linux, Windows, AIX, AS/400 or all environment you want.

How To use:
-----------
Usage: java portListener [\<ip\>]

<ip>

Is possible to specifie an ip where lock the port

ex.

java portListener 194.168.0.1

java portListener localhost

History
-------
0.10
First release

0.11
Decrease the number of concurrent client for each port from 50 to 5

1.00
GitHub release with GraalVM Build and Native Image
