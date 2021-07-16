/*
StatusNode.java, a part of the ASAP commanding interface.

Gets rosparam data for GDS telemetry and sets test numbers.

Keenan Albee, Charles Oestreich, Phillip Johnson, Abhi Cauligi
*/
package edu.mit.ssl.commandasap;

// import org.apache.commons.logging.Log;
import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.parameter.ParameterTree;
import org.ros.node.parameter.ParameterListener;
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

import android.util.Log;

public class StatusNode extends AbstractNodeMain {

    private ParameterTree rosparam = null;
    public static StatusNode instance = null;
    // // An example publisher, not currently needed
    // Publisher<JointState> mPublisher;

    //data instance field used in the sendData function
    public java.lang.String global_gds_param_count=  "";
    public java.lang.String test_num=  "";
    public java.lang.String flight_mode=  "";

    public java.lang.String test_finished = "";
    public java.lang.String coord_ok = "";

    public java.lang.String control_mode = "";
    public java.lang.String regulate_finished = "";
    public java.lang.String uc_bound_activated = "";
    public java.lang.String uc_bound_finished = "";
    public java.lang.String mrpi_finished = "";
    public java.lang.String traj_sent = "";
    public java.lang.String traj_finished = "";
    public java.lang.String gain_mode = "";
    public java.lang.String lqrrrt_activated = "";
    public java.lang.String lqrrrt_finished = "";
    public java.lang.String info_traj_send = "";
    public java.lang.String solver_status = "";
    public java.lang.String cost_value = "";
    public java.lang.String kkt_value = "";
    public java.lang.String sol_time = "";

    // asap/status subscriber
    //Subscriber<std_msgs.String> mSubscriber;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("commandasap");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode){
      /* Called on APK startup. Receives a node via connectedNode.
      */
      // // Example of creating a publisher
      // mPublisher = connectedNode.newPublisher(
      //         "joint_goals",
      //         sensor_msgs.JointState._TYPE);

      //initialize parameters
      ParameterTree my_param = connectedNode.getParameterTree();
      rosparam = my_param;  // need to do this, don't change...
      rosparam.set("/asap/gds_apk_status", "started");
      rosparam.set("/asap/gds_sim", "hardware");
      rosparam.set("/asap/gds_ground", "false");

      // rosparam.addParameterListener("/asap/chaser/gds_telem", new ParameterListener() {
      //   @Override
      //   public void onNewValue(Object value) {
      //     List<java.lang.String> gds_telem_string = (List<java.lang.String>)value;
      //     rosparam.set("/asap/debug", gds_telem_string.get(0));
      //     td_flight_mode = gds_telem_string.get(0);
      //     td_control_mode = gds_telem_string.get(1);
      //     slam_activate = gds_telem_string.get(2);
      //     chaser_regulate_finished = gds_telem_string.get(3);
      //     target_regulate_finished = gds_telem_string.get(4);
      //     motion_plan_wait_time = gds_telem_string.get(5);
      //     motion_plan_finished = gds_telem_string.get(6);
      //     default_control = gds_telem_string.get(7);
      //     role = gds_telem_string.get(8);
      //   }
      // });

      //creating the subscriber for the /asap/status rostopic
      // mSubscriber = connectedNode.newSubscriber("/asap/test_sub", std_msgs.String._TYPE);
      // mSubscriber.addMessageListener(new MessageListener<std_msgs.String>() {
      //     @Override
      //     public void onNewMessage(std_msgs.String message) {
      //         rosparam.set("/debug", message.getData());
      //         test_number = message.getData(); //this should set the data instance field to a java.lang.String message
      //     }
      // });

      instance = this;
    }

    public static StatusNode getInstance(){
      return instance;
    }

    public void updateParams() {
      try {
        List<?> gds_telem = rosparam.getList("/asap/gds_telem");
        List<java.lang.String> gds_telem_string = (List<java.lang.String>)gds_telem;
        global_gds_param_count = gds_telem_string.get(0);
        test_num = gds_telem_string.get(1);
        flight_mode = gds_telem_string.get(2);
        test_finished = gds_telem_string.get(3);
        coord_ok = gds_telem_string.get(4);
        control_mode = gds_telem_string.get(5);
        regulate_finished = gds_telem_string.get(6);
        uc_bound_activated = gds_telem_string.get(7);
        uc_bound_finished = gds_telem_string.get(8);
        mrpi_finished = gds_telem_string.get(9);
        traj_sent = gds_telem_string.get(10);
        traj_finished = gds_telem_string.get(11);
        gain_mode = gds_telem_string.get(12);
        lqrrrt_activated = gds_telem_string.get(13);
        lqrrrt_finished = gds_telem_string.get(14);
        info_traj_send = gds_telem_string.get(15);
        solver_status = gds_telem_string.get(16);
        cost_value = gds_telem_string.get(17);
        kkt_value = gds_telem_string.get(18);
        sol_time = gds_telem_string.get(19);
      }
      catch (Exception ex) {
        Log.e("edu.mit.ssl.commandasap", "exception", ex);  // no data yet available, or telem vector wrong size
        // sendData(MessageType.JSON, "data", "Unrecognized ERROR");
      }
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

      //java.lang.String cmd="Command"+Integer.toString(command_number);
      rosparam.set("/asap/gds_test_num", command_number);
    }

    public void setRole(java.lang.String role){
      /* Send out a rosparam for the Astrobee's role.
      */
      rosparam.set("/asap/gds_role", role);
    }

    public void setGround(){
      /* Send out a rosparam for the Astrobee's ground status.
      */
      rosparam.set("/asap/gds_ground", "true");
    }

    public void setISS(){
      /* Send out a rosparam for the Astrobee's world status.
      */
      rosparam.set("/asap/gds_ground", "false");
    }

    public void setRoamBagger(java.lang.String enabled){
      /* Send out a rosparam for the Astrobee's ROAM bag recording.
      */
      rosparam.set("/asap/gds_roam_bagger", enabled);
    }

    public void sendStopped(){
      /* Send when the APK is stopped.
      */
      rosparam.set("/asap/gds_apk_status", "stopped");
    }
}
