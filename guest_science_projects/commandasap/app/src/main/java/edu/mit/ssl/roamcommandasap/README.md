# roamcommandasap

This is the ROAM main commanding interface for GDS commands.

# Usage

A set of possible commands are specified in `../../../../res/xml/commands.xml`

These commands are parsed in Java using the standard `onGuestScienceCustomCmd`
from `./StartRoamcommandasapService.java`. Note that commands contain JSON that
is received as a String.

`./RoamStatusNode.java` is used for actually handling commands. Note that a rosjava node
is NOT currently used (ROS parameters do not require a node to be instantiated.)
A ROS node is available if publishers or subscribers are needed.
