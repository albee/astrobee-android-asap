package edu.mit.ssl.roamcommandasap;

import org.apache.commons.logging.Log;
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

import java.nio.*;
import java.util.*;
import java.util.Random;
import java.io.File;
import java.io.IOException;

import ff_msgs.ControlActionFeedback;
import sensor_msgs.JointState;
import std_msgs.Header;

import gov.nasa.arc.astrobee.android.gs.MessageType;
import gov.nasa.arc.astrobee.android.gs.StartGuestScienceService;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import std_msgs.String;

public class RoamStatusNode extends AbstractNodeMain {

    private ParameterTree rosparam = null;
    public static RoamStatusNode instance = null;
    // // An example publisher, not currently needed
    // Publisher<JointState> mPublisher;

    //data instance field used in the sendData function
    private java.lang.String data=null;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("roamcommandasap");
    }


    Subscriber<std_msgs.String> mSubscriber;

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
      ParameterTree my_rosparam = connectedNode.getParameterTree();
      rosparam = my_rosparam;
      rosparam.set("/td/gds_apk_status", "started");
      rosparam.set("/td/gds_sim", "hardware");
      rosparam.set("/td/gds_ground", "false");


      //creating the subscriber for the /td/status rostopic
      mSubscriber = connectedNode.newSubscriber("/td/status", std_msgs.String._TYPE);
      mSubscriber.addMessageListener(new MessageListener<std_msgs.String>() {
            @Override
            public void onNewMessage(std_msgs.String message) {
                /**rosparam.set("/test_data_sub","entered onNewMessage");
                rosparam.set("/test_data_sub",message.getData());**/ //used for debugging
                data=message.getData(); //this should set the data instance field to a java.lang.String message
            }
        });

    }

    public static RoamStatusNode getInstance(){
      return instance;
    }


    public java.lang.String getData(){
        /**rosparam.set("/test_data_sub","entered getData");
        rosparam.set("/test_data_sub",data);**/ //these are used for debugging
        return data;

    }
    /**public void setMessageTest(java.lang.String message){
        rosparam.set("/td/message_output",message);
    }*/ //used for testing the output of the message from the publisher

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

      java.lang.String cmd="Command"+Integer.toString(command_number);
      rosparam.set("/td/gds_test_num", command_number);
    }

    public void setRole(java.lang.String role){
      /* Send out a rosparam for the Astrobee's role.
      */

      rosparam.set("/td/role_from_GDS", role);
    }

    public void setGround(){
      /* Send out a rosparam for the Astrobee's role.
      */
      rosparam.set("/td/gds_ground", "true");
    }

    public void sendStopped(){
      /* Send when the APK is stopped.
      */
      rosparam.set("/td/gds_apk_status", "stopped");
    }
}
