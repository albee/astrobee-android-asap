
/* Copyright (c) 2017, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 *
 * All rights reserved.
 *
 * The Astrobee platform is licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package edu.stanford.asl.geckoperchinggripper;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import android.os.Handler;
import android.os.Looper;
import android.content.Intent;

import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.topic.Publisher;

import sensor_msgs.JointState;
import std_msgs.String;
import java.net.URI;

import gov.nasa.arc.astrobee.android.gs.MessageType;
import gov.nasa.arc.astrobee.android.gs.StartGuestScienceService;
import gov.nasa.arc.astrobee.PendingResult;
import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.Robot;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class StartGeckoperchinggripperService extends StartGuestScienceService {

    // The API implementation
    private ApiCommandImplementation api = null;

     /**
     * This function is called when the GS manager starts your apk.
     * Put all of your start up code in here.
     */
    NodeMainExecutor nodeMainExecutor;
    private GeckoGripperStatusNode gecko_gripper_node = null;

    /*
     * Handler and Runnable for permanent interface updating
     */
    public final long QUERY_WAIT_MS   = 1000;
    public final long STATUS_WAIT_MS  = 1000;
    public final long FILE_WAIT_MS    = 1500;
    public final long DL_WAIT_MS      = 1500;
    Handler handler;

    private Runnable queryRefresh = new Runnable() {
        @Override
        public void run() {
            sendQueryMsg();
        }
    };

    private Runnable allRefresh = new Runnable() {
        @Override
        public void run() {
            printAll();
        }
    };

    private Runnable statusRefresh = new Runnable() {
        @Override
        public void run() {
            printBinaryFlags();
        }
    };
    
    private Runnable fileRefresh = new Runnable() {
        @Override
        public void run() {
            printFileStatus();
        }
    };

    private Runnable delayRefresh = new Runnable() {
        @Override
        public void run() {
            printDelayStatus();
        }
    };

    // IP Address ROS Master and Hostname
    private static final URI ROS_MASTER_URI = URI.create("http://llp:11311");
    private static final java.lang.String ROS_HOSTNAME = "hlp";

    @Override
    public void onGuestScienceStart() {
        // Get a unique instance of the Astrobee API in order to command the robot.
        api = ApiCommandImplementation.getInstance();

        // Setting configurations for ROS-Android Node
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(ROS_HOSTNAME);
        nodeConfiguration.setMasterUri(ROS_MASTER_URI);

        gecko_gripper_node = new GeckoGripperStatusNode();

        nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        nodeMainExecutor.execute(gecko_gripper_node, nodeConfiguration);

        // Handler for interface updating
        this.handler = new Handler();

        // Inform the GS Manager and the GDS that the app has been started.
        sendStarted("info");
    }

    /**
     * This function is called when the GS manager stops your apk.
     * Put all of your clean up code in here. You should also call the terminate helper function
     * at the very end of this function.
     */
    @Override
    public void onGuestScienceStop() {
        // Stop the API
        api.shutdownFactory();

        handler.removeCallbacksAndMessages(null);

        // Inform the GS manager and the GDS that this app stopped.
        sendStopped("info");

        // Destroy all connection with the GS Manager.
        terminate();
    }

    /**
     * This function is called when the GS manager sends a custom command to your apk.
     * Please handle your commands in this function.
     *
     * @param command
     */
    @Override
    public void onGuestScienceCustomCmd(java.lang.String command) {
        /* Inform the Guest Science Manager (GSM) and the Ground Data System (GDS)
         * that this app received a command. */
        sendReceivedCustomCommand("info");

        try {
            // Transform the String command into a JSON object so we can read it.
            JSONObject jCommand = new JSONObject(command);

            // Get the name of the command we received. See commands.xml files in res folder.
            java.lang.String sCommand = jCommand.getString("name");

            // JSON object that will contain the data we will send back to the GSM and GDS
            JSONObject jResult = new JSONObject();
            
            java.util.List<java.lang.String> msg_name = new java.util.ArrayList<java.lang.String>();
            sensor_msgs.JointState msg = gecko_gripper_node.mPublisher.newMessage();

            double[] msg_pos = new double[1];
            msg_pos[0] = 0.;

            switch (sCommand) {
                // You may handle your commands here
                case "gecko_gripper_open":
                    msg_name.add(sCommand);
                    break;
                case "gecko_gripper_close":
                    msg_name.add(sCommand);
                    break;
                case "gecko_gripper_engage":
                    msg_name.add(sCommand);

                    handler.postDelayed(statusRefresh, STATUS_WAIT_MS);
                    break;
                case "gecko_gripper_disengage":
                    msg_name.add(sCommand);

                    handler.postDelayed(statusRefresh, STATUS_WAIT_MS);
                    break;
                case "gecko_gripper_lock":
                    msg_name.add(sCommand);

                    handler.postDelayed(statusRefresh, STATUS_WAIT_MS);
                    break;
                case "gecko_gripper_unlock":
                    msg_name.add(sCommand);

                    handler.postDelayed(statusRefresh, STATUS_WAIT_MS);
                    break;
                case "gecko_gripper_engage_lock":
                    msg_name.add("gecko_gripper_engage");
                    msg_name.add("gecko_gripper_lock");

                    handler.postDelayed(statusRefresh, STATUS_WAIT_MS);
                    break;
                case "gecko_gripper_enable_auto":
                    if (!gecko_gripper_node.feedbackPerchingEnable) {
                      msg_name.add(sCommand);
                      handler.postDelayed(statusRefresh, STATUS_WAIT_MS);
                    }
                    break;
                case "gecko_gripper_disable_auto":
                    msg_name.add(sCommand);

                    handler.postDelayed(statusRefresh, STATUS_WAIT_MS);
                    break;
                case "gecko_gripper_toggle_auto":
                    try {
                      JSONObject toggleAutoJson = new JSONObject();
                      toggleAutoJson.put("ERROR ", "toggle_auto not implemented!");
                      sendData(MessageType.JSON, "Toggle Auto", toggleAutoJson.toString());
                    } catch (JSONException e) {
                        // Send an error message to the GSM and GDS
                        e.printStackTrace();
                        sendData(MessageType.JSON, "data", "ERROR parsing JSON");
                    }

                    break;
                case "gecko_gripper_mark_gripper":
                    msg_name.add(sCommand);
                    msg_pos[0] = Float.parseFloat(jCommand.getString("IDX"));

                    handler.postDelayed(queryRefresh, QUERY_WAIT_MS);
                    handler.postDelayed(fileRefresh, FILE_WAIT_MS);

                    break;
                case "gecko_gripper_set_delay":
                    msg_name.add(sCommand);
                    msg_pos[0] = Float.parseFloat(jCommand.getString("DL"));

                    handler.postDelayed(queryRefresh, QUERY_WAIT_MS);
                    handler.postDelayed(delayRefresh, DL_WAIT_MS);

                    break;
                case "gecko_gripper_open_exp":
                    msg_name.add(sCommand);
                    msg_pos[0] = Float.parseFloat(jCommand.getString("IDX"));

                    handler.postDelayed(queryRefresh, QUERY_WAIT_MS);
                    handler.postDelayed(fileRefresh, FILE_WAIT_MS);
                    break;
                case "gecko_gripper_next_record":
                    msg_name.add(sCommand);
                    msg_pos[0] = Float.parseFloat(jCommand.getString("SKIP"));
                    break;
                case "gecko_gripper_seek_record":
                    msg_name.add(sCommand);
                    msg_pos[0] = Float.parseFloat(jCommand.getString("RN"));
                    break;
                case "gecko_gripper_close_exp":
                    msg_name.add(sCommand);

                    handler.postDelayed(queryRefresh, QUERY_WAIT_MS);
                    handler.postDelayed(fileRefresh, FILE_WAIT_MS);
                    break;
                case "gecko_gripper_status":
                    msg_name.add(sCommand);
                    break;
                case "gecko_gripper_record":
                    msg_name.add(sCommand);
                    break;
                case "gecko_gripper_exp":
                    msg_name.add(sCommand);
                    break;
                case "gecko_gripper_delay":
                    msg_name.add(sCommand);
                    break;
                case "gecko_gripper_print_status":
                    msg_name.add("gecko_gripper_delay");
                    msg_name.add("gecko_gripper_exp");

                    handler.postDelayed(allRefresh, STATUS_WAIT_MS);

                    try {
                      JSONObject json = new JSONObject();
                      json.put("Summary", new JSONObject()
                          .put("Status", "PENDING")
                          .put("Message", "Sent gripper status query command"));
                      sendData(MessageType.JSON, "", json.toString());
                    } catch (JSONException e) {
                        // Send an error message to the GSM and GDS
                        e.printStackTrace();
                        sendData(MessageType.JSON, "data", "ERROR parsing JSON");
                    }
                    break;
                case "gecko_gripper_print_status_now":
                    printAll();
                    break;
                case "gecko_gripper_reset_gripper":
                    // After perching experiment, resets gripper
                    msg_name.add("gecko_gripper_disable_auto");
                    msg_name.add("gecko_gripper_disengage");
                    msg_name.add("gecko_gripper_unlock");
                    msg_name.add("gecko_gripper_delay");
                    msg_name.add("gecko_gripper_exp");

                    handler.postDelayed(statusRefresh, STATUS_WAIT_MS);
                    break;
                case "gecko_gripper_enable_auto_feedback":
                    gecko_gripper_node.feedbackPerchingEnable = true;
                    msg_name.add("gecko_gripper_disable_auto");
                    msg_name.add("gecko_gripper_disengage");
                    msg_name.add("gecko_gripper_unlock");

                    handler.postDelayed(statusRefresh, STATUS_WAIT_MS);
                    break;
                case "gecko_gripper_disable_auto_feedback":
                    gecko_gripper_node.feedbackPerchingEnable = false;
                    break;
                case "gecko_gripper_set_feedback_tol":
                    gecko_gripper_node.errorTol = 0.01*Double.parseDouble(jCommand.getString("TOL"));
                    gecko_gripper_node.feedbackPerchingEnable = false;

                    jResult.put("Feedback", "Feedback tolerance updated");
                    break;
                case "gecko_gripper_arm_deploy":
                    try {
                      JSONObject armDeployJson= new JSONObject();
                      Result armDeployResult = api.armDeploy();

                      if (armDeployResult.hasSucceeded()) {
                        armDeployJson.put("Status", "Arm deployment succeeded");
                      } else {
                        armDeployJson.put("Status", "Arm deployment failed");
                      }
                      sendData(MessageType.JSON, "Arm deploy ", armDeployJson.toString());
                    } catch (JSONException e) {
                        // Send an error message to the GSM and GDS
                        e.printStackTrace();
                        sendData(MessageType.JSON, "data", "ERROR parsing JSON");
                    }

                    break;
                case "gecko_gripper_perch_pan_test":
                    boolean pan_test_succeeded = api.perchPanTest();
                    JSONObject perchPanResult = new JSONObject();
                    try {
                      if (pan_test_succeeded) {
                        perchPanResult.put("Status", "Perch pan test executed!");
                      } else {
                        perchPanResult.put("Status", "Perch pan test failed!");
                      }
                      sendData(MessageType.JSON, "Perch pan test ", perchPanResult.toString());
                    } catch (JSONException e) {
                        // Send an error message to the GSM and GDS
                        e.printStackTrace();
                        sendData(MessageType.JSON, "data", "ERROR parsing JSON");
                    }
                    break;

                default:
                    // Inform GS Manager and GDS, then stop execution.
                    jResult.put("Summary", new JSONObject()
                        .put("Status", "ERROR")
                        .put("Message", "Unrecognized command"));
            }

            if (msg_name.size() > 0) {
              msg.setName(msg_name);
              msg.setPosition(msg_pos);
              gecko_gripper_node.mPublisher.publish(msg);
            }
        } catch (JSONException e) {
            // Send an error message to the GSM and GDS
            e.printStackTrace();
            sendData(MessageType.JSON, "data", "ERROR parsing JSON");
        } catch (Exception ex) {
            // Send an error message to the GSM and GDS
            ex.printStackTrace();
            sendData(MessageType.JSON, "data", "Unrecognized ERROR");
        }
    }

    public void printAll() {
      try{
        JSONObject jsonGripperState = gecko_gripper_node.gripperState.toJSON();
        sendData(MessageType.JSON, "Gripper status ", jsonGripperState.toString());
      } catch (JSONException e) {
          // Send an error message to the GSM and GDS
          e.printStackTrace();
          sendData(MessageType.JSON, "data", "ERROR parsing JSON");
      }
    }

    public void printBinaryFlags() {
      boolean adhesiveEngage = gecko_gripper_node.gripperState.getAdhesiveEngage();
      boolean wristLock = gecko_gripper_node.gripperState.getWristLock();
      boolean automaticModeEnable = gecko_gripper_node.gripperState.getAutomaticModeEnable();
      boolean fileIsOpen = gecko_gripper_node.gripperState.getFileIsOpen();

      try{
        JSONObject binaryStatus = new JSONObject();
        binaryStatus.put("Adhesive Engage", adhesiveEngage)
                  .put("Wrist Lock", wristLock)
                  .put("Automatic Mode Enable", automaticModeEnable)
                  .put("File is Open", fileIsOpen);
        sendData(MessageType.JSON, "Flags ", binaryStatus.toString());
      } catch (JSONException e) {
          // Send an error message to the GSM and GDS
          e.printStackTrace();
          sendData(MessageType.JSON, "data", "ERROR parsing JSON");
      }
    }

    public void printFileStatus() {
      boolean fileIsOpen = gecko_gripper_node.gripperState.getFileIsOpen();
      int expIdx = gecko_gripper_node.gripperState.getExpIdx();

      try{
        JSONObject json = new JSONObject();
        json.put("File is Open", fileIsOpen).put("Experiment Idx", expIdx);
        sendData(MessageType.JSON, "File status ", json.toString());
      } catch (JSONException e) {
          // Send an error message to the GSM and GDS
          e.printStackTrace();
          sendData(MessageType.JSON, "data", "ERROR parsing JSON");
      }
    }

    public void printDelayStatus() {
      int DL = gecko_gripper_node.gripperState.getDelay();

      try{
        JSONObject json = new JSONObject();
        json.put("Current delay in ms is ", DL);
        sendData(MessageType.JSON, "DL ", json.toString());
      } catch (JSONException e) {
          // Send an error message to the GSM and GDS
          e.printStackTrace();
          sendData(MessageType.JSON, "data", "ERROR parsing JSON");
      }
    }

    public void sendQueryMsg() {
      sensor_msgs.JointState msg = gecko_gripper_node.mPublisher.newMessage();
      java.util.List<java.lang.String> msg_name = new java.util.ArrayList<java.lang.String>();
      double[] msg_pos = new double[2];
      msg_pos[0] = 0.;
      msg_pos[1] = 0.;

      msg_name.add("gecko_gripper_delay");
      msg_name.add("gecko_gripper_exp");

      msg.setName(msg_name);
      msg.setPosition(msg_pos);
      gecko_gripper_node.mPublisher.publish(msg);
      return;
    }
}
