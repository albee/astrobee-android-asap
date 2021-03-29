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

    Publisher<JointState> mPublisher;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("roamcommandasap");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode){
        mPublisher = connectedNode.newPublisher(
                "joint_goals",
                sensor_msgs.JointState._TYPE);
        instance = this;

        //initializes the /roamcommandasap parameter to null on startup to reflect that no commands have run
        ParameterTree params1 = connectedNode.getParameterTree();

        params=params1;

        params.set("/roamcommand","started");


    }
    public static RoamStatusNode getInstance(){
        return instance;
    }
    public void sendStopped(){ params.set("/roamcommand","stopped"); }

    public void sendCommand(Integer command_number){
        sensor_msgs.JointState msg = mPublisher.newMessage();
        java.util.List<java.lang.String> msg_name = new java.util.ArrayList<java.lang.String>();
        double[] msg_pos = new double[1];

        String cmd="Command"+Integer.toString(command_number);

        msg_name.add(cmd);
        msg_pos[0]=0.0;
        msg.setName(msg_name);
        msg.setPosition(msg_pos);

        params.set("/roamcommand", command_number);
    }

    public void resetParam(){
        sensor_msgs.JointState msg = mPublisher.newMessage();
        java.util.List<java.lang.String> msg_name = new java.util.ArrayList<java.lang.String>();
        double[] msg_pos = new double[1];

        msg_name.add("Reset param");
        msg_pos[0]=0.0;
        msg.setName(msg_name);
        msg.setPosition(msg_pos);

        params.set("/roamcommand","none");

        mPublisher.publish(msg);
    }
}
