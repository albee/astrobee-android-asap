package edu.mit.ssl.roamcommandasap;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
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
import std_msgs.String;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

public class RoamStatusNode extends AbstractNodeMain {


    public static RoamStatusNode instance = null;

    Publisher<JointState> mPublisher;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("gecko_perching_gripper");
    }

    @Override
    public void onStart(ConnectedNode connectedNode){
        mPublisher = connectedNode.newPublisher(
                "joint_goals",
                sensor_msgs.JointState._TYPE);
        instance = this;
    }

    public static RoamStatusNode getInstance(){
        return instance;
    }
    public void sendTestMsg(){
        sensor_msgs.JointState msg = mPublisher.newMessage();
        java.util.List<java.lang.String> msg_name = new java.util.ArrayList<java.lang.String>();
        double[] msg_pos = new double[1];

        msg_name.add("test_publish");
        msg_pos[0]=0.0;
        msg.setName(msg_name);
        msg.setPosition(msg_pos);


        mPublisher.publish(msg);
    }
    
}
