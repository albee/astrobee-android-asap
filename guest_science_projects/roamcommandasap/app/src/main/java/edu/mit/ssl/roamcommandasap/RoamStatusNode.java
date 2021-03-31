package edu.mit.ssl.roamcommandasap;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.parameter.ParameterTree;
import org.ros.node.topic.Subscriber;
import org.ros.node.topic.Publisher;
import org.ros.message.MessageListener;
import org.ros.node.NodeConfiguration;
import org.ros.message.Time;
import android.util.Log;

import java.nio.*;
import java.util.*;
import java.util.Random;
import java.io.File;
import java.io.IOException;

import ff_msgs.ControlActionFeedback;
import sensor_msgs.JointState;
import std_msgs.Header;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

public class RoamStatusNode extends AbstractNodeMain {

    private ParameterTree params = null;
    public static RoamStatusNode instance = null;
    // // An example publisher, not currently needed
    // Publisher<JointState> mPublisher;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("roamcommandasap");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode){
      /* Called on APK startup. Receives a node via connectedNode.
      */
      // // Example of creating a publisher
      // mPublisher = connectedNode.newPublisher(
      //         "joint_goals",
      //         sensor_msgs.JointState._TYPE);
      instance = this;

      //initializes the /roamcommandasap parameter to null on startup to reflect that no commands have run
      ParameterTree params1 = connectedNode.getParameterTree();
      params = params1;
      params.set("/roamcommand", "started");
    }

    public static RoamStatusNode getInstance(){
      return instance;
    }

    public void sendCommand(Integer command_number){
      /* Send out a rosparam according to command_number.
      */

      // // Example of publishing a msg
      // sensor_msgs.JointState msg = mPublisher.newMessage();
      // java.util.List<java.lang.String> msg_name = new java.util.ArrayList<java.lang.String>();
      // double[] msg_pos = new double[1];
      // msg_name.add(cmd);
      // msg_pos[0]=0.0;
      // msg.setName(msg_name);
      // msg.setPosition(msg_pos);
      // mPublisher.publish(msg);

      String cmd="Command"+Integer.toString(command_number);
      params.set("/roamcommand", command_number);
    }

    public void resetParam(){
        /* Send out a rosparam for a "reset" command.
        */
        params.set("/roamcommand","reset");
    }

    public void sendStopped(){
      /* Send when the APK is stopped.
      */
      params.set("/roamcommand","stopped"); }
}
