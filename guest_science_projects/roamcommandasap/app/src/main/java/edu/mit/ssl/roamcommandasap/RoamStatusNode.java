package edu.mit.ssl.roamcommandasap;

import org.apache.commons.logging.Log;
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

public class RoamStatusNode extends AbstractNodeMain {

    private ParameterTree rosparam = null;
    public static RoamStatusNode instance = null;
    // // An example publisher, not currently needed
    // Publisher<JointState> mPublisher;

    //data instance field used in the sendData function
    public java.lang.String global_gds_param_count=  "";
    public java.lang.String test_num=  "";
    public java.lang.String td_flight_mode=  "";
    public java.lang.String td_control_mode=  "";
    public java.lang.String slam_activate= "";
    public java.lang.String target_regulate_finished= "";
    public java.lang.String chaser_regulate_finished= "";
    public java.lang.String motion_plan_finished=  "";
    public java.lang.String motion_plan_wait_time=  "";
    public java.lang.String default_control=  "";
    public java.lang.String my_role=  "";
    public java.lang.String test_LUT=  "";
    public java.lang.String test_tumble_type=  "";
    public java.lang.String test_control_mode=  "";
    public java.lang.String test_state_mode=  "";
    public java.lang.String dlr_LUT_param=  "";
    public java.lang.String traj_gen_dlr_activate=  "";
    public java.lang.String uc_bound_activate=  "";
    public java.lang.String slam_converged=  "";
    public java.lang.String inertia_estimated=  "";
    public java.lang.String uc_bound_finished=  "";
    public java.lang.String mrpi_finished=  "";
    public java.lang.String traj_finished=  "";
    public java.lang.String test_finished=  "";
    public java.lang.String td_state_mode=  "";
    public java.lang.String casadi_on_target=  "";

    // td/status subscriber
    //Subscriber<std_msgs.String> mSubscriber;

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

      //initialize parameters
      ParameterTree my_param = connectedNode.getParameterTree();
      rosparam = my_param;  // need to do this, don't change...
      rosparam.set("/td/gds_apk_status", "started");
      rosparam.set("/td/gds_sim", "hardware");
      rosparam.set("/td/gds_ground", "false");

      // rosparam.addParameterListener("/td/chaser/gds_telem", new ParameterListener() {
      //   @Override
      //   public void onNewValue(Object value) {
      //     List<java.lang.String> gds_telem_string = (List<java.lang.String>)value;
      //     rosparam.set("/td/gds/debug", gds_telem_string.get(0));
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

      //creating the subscriber for the /td/status rostopic
      // mSubscriber = connectedNode.newSubscriber("/td/test_sub", std_msgs.String._TYPE);
      // mSubscriber.addMessageListener(new MessageListener<std_msgs.String>() {
      //     @Override
      //     public void onNewMessage(std_msgs.String message) {
      //         rosparam.set("/debug", message.getData());
      //         test_number = message.getData(); //this should set the data instance field to a java.lang.String message
      //     }
      // });

      instance = this;
    }

    public static RoamStatusNode getInstance(){
      return instance;
    }

    public void updateParams() {
      List<?> gds_telem = rosparam.getList("/td/chaser/gds_telem");
      List<java.lang.String> gds_telem_string = (List<java.lang.String>)gds_telem;
      global_gds_param_count= gds_telem_string.get(0);
      test_num= gds_telem_string.get(1);
      td_flight_mode= gds_telem_string.get(2);
      td_control_mode= gds_telem_string.get(3);
      slam_activate= gds_telem_string.get(4);
      target_regulate_finished= gds_telem_string.get(5);
      chaser_regulate_finished= gds_telem_string.get(6);
      motion_plan_finished= gds_telem_string.get(7);
      motion_plan_wait_time= gds_telem_string.get(8);
      default_control= gds_telem_string.get(9);
      my_role= gds_telem_string.get(10);
      test_LUT= gds_telem_string.get(11);
      test_tumble_type= gds_telem_string.get(12);
      test_control_mode= gds_telem_string.get(13);
      test_state_mode= gds_telem_string.get(14);
      dlr_LUT_param= gds_telem_string.get(15);
      traj_gen_dlr_activate= gds_telem_string.get(16);
      uc_bound_activate= gds_telem_string.get(17);
      slam_converged= gds_telem_string.get(18);
      inertia_estimated= gds_telem_string.get(19);
      uc_bound_finished= gds_telem_string.get(20);
      mrpi_finished= gds_telem_string.get(21);
      traj_finished= gds_telem_string.get(22);
      test_finished= gds_telem_string.get(23);
      td_state_mode= gds_telem_string.get(24);
      casadi_on_target= gds_telem_string.get(25);
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
      rosparam.set("/td/gds_test_num", command_number);
    }

    public void setRole(java.lang.String role){
      /* Send out a rosparam for the Astrobee's role.
      */
      rosparam.set("/td/role_from_GDS", role);
    }

    public void setGround(){
      /* Send out a rosparam for the Astrobee's ground status.
      */
      rosparam.set("/td/gds_ground", "true");
    }

    public void setISS(){
      /* Send out a rosparam for the Astrobee's world status.
      */
      rosparam.set("/td/gds_ground", "false");
    }

    public void setRoamBagger(java.lang.String enabled){
      /* Send out a rosparam for the Astrobee's ROAM bag recording.
      */
      rosparam.set("/td/gds_roam_bagger", enabled);
    }

    public void sendStopped(){
      /* Send when the APK is stopped.
      */
      rosparam.set("/td/gds_apk_status", "stopped");
    }
}
