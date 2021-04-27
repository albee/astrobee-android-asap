
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

package edu.mit.ssl.roamcommandasap;

import org.json.JSONException;
import org.json.JSONObject;
import org.ros.node.DefaultNodeMainExecutor;

import java.net.URI;

import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.ros.node.parameter.ParameterTree;

import gov.nasa.arc.astrobee.android.gs.MessageType;
import gov.nasa.arc.astrobee.android.gs.StartGuestScienceService;

import android.util.Log;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class StartRoamcommandasapService extends StartGuestScienceService {
    // The API implementation
    private ApiCommandImplementation api = null;

    private RoamStatusNode roam_node = null;
    private boolean stop = false;
    private final AtomicBoolean running = new AtomicBoolean(true);

    NodeMainExecutor nodeMainExecutor;
    private static final URI ROS_MASTER_URI = URI.create("http://llp:11311");
    private static final java.lang.String ROS_HOSTNAME = "hlp";

    private Runnable r = new Runnable() {
      @Override
      public void run() {
          while(running.get()) {
            try {
              TimeUnit.SECONDS.sleep(5);
            }
            catch(Exception ex){
            }

            roam_node.updateParams();
            try {
              JSONObject TDStatus = new JSONObject();
              TDStatus.put("test_num", roam_node.test_num);
              TDStatus.put("td_flight_mode", roam_node.td_flight_mode);
              TDStatus.put("td_control_mode", roam_node.td_control_mode);
              TDStatus.put("slam_activate", roam_node.slam_activate);
              TDStatus.put("target_regulate_finished", roam_node.target_regulate_finished);
              TDStatus.put("chaser_regulate_finished", roam_node.chaser_regulate_finished);
              TDStatus.put("motion_plan_finished", roam_node.motion_plan_finished);
              TDStatus.put("motion_plan_wait_time", roam_node.motion_plan_wait_time);
              TDStatus.put("default_control", roam_node.default_control);
              TDStatus.put("my_role", roam_node.my_role);
              TDStatus.put("test_LUT", roam_node.test_LUT);
              TDStatus.put("test_tumble_type", roam_node.test_tumble_type);
              TDStatus.put("test_control_mode", roam_node.test_control_mode);
              TDStatus.put("test_state_mode", roam_node.test_state_mode);
              TDStatus.put("dlr_LUT_param", roam_node.dlr_LUT_param);
              TDStatus.put("traj_gen_dlr_activate", roam_node.traj_gen_dlr_activate);
              TDStatus.put("uc_bound_activate", roam_node.uc_bound_activate);
              TDStatus.put("slam_converged", roam_node.slam_converged);
              TDStatus.put("inertia_estimated", roam_node.inertia_estimated);
              TDStatus.put("uc_bound_finished", roam_node.uc_bound_finished);
              TDStatus.put("mrpi_finished", roam_node.mrpi_finished);
              TDStatus.put("traj_finished", roam_node.traj_finished);
              TDStatus.put("test_finished", roam_node.test_finished);
              TDStatus.put("td_state_mode", roam_node.td_state_mode);
              TDStatus.put("casadi_on_target", roam_node.casadi_on_target);
              sendData(MessageType.JSON, "TDStatus", TDStatus.toString());

              JSONObject TelemID = new JSONObject();
              TelemID.put("TelemID", roam_node.global_gds_param_count);
              sendData(MessageType.JSON, "TelemID", TelemID.toString());
            } catch (JSONException e) {
              // Send an error message to the GSM and GDS
              e.printStackTrace();
              sendData(MessageType.JSON, "data", "ERROR parsing TDStatus JSON");
            }
          }
        }
    };

    /**
     * This function is called when the GS manager starts your apk.
     * Put all of your start up code in here.
     */
    @Override
    public void onGuestScienceStart() {
        // Get a unique instance of the Astrobee API in order to command the robot.
        api = ApiCommandImplementation.getInstance();

        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(ROS_HOSTNAME);
        nodeConfiguration.setMasterUri(ROS_MASTER_URI);

        roam_node = new RoamStatusNode();  // this is the custom node used for commanding

        nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        nodeMainExecutor.execute(roam_node, nodeConfiguration);

        // Inform the GS Manager and the GDS that the app has been started.
        sendStarted("info");

        // in the background, periodically check for params and do a sendData
        new Thread(r).start();
    }

    /**
     * This function is called when the GS manager stops your apk.
     * Put all of your clean up code in here. You should also call the terminate helper function
     * at the very end of this function.
     */
    @Override
    public void onGuestScienceStop() {
        //set rosparam roamcommand to stopped
        roam_node.sendStopped();

        // stop the telemetry down
        running.set(false);

        // Stop the API
        api.shutdownFactory();

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
    public void onGuestScienceCustomCmd(String command) {
        /* Inform the Guest Science Manager (GSM) and the Ground Data System (GDS)
         * that this app received a command. */
        sendReceivedCustomCommand("info");

        try {
            // Transform the String command into a JSON object so we can read it.
            JSONObject jCommand = new JSONObject(command);

            // Get the name of the command we received. See commands.xml files in res folder.
            String sCommand = jCommand.getString("name");

            // JSON object that will contain the data we will send back to the GSM and GDS
            JSONObject jResult = new JSONObject();

            // Handle incoming commands
            switch (sCommand) {
                // StopTest
                case "StopTest":
                    roam_node.sendCommand(-1);
                    break;

                // Role and Scenario Setting
                case "SetRoleChaser":
                    roam_node.setRole("chaser");
                    break;
                case "SetRoleTarget":
                    roam_node.setRole("target");
                    break;
                case "SetRoleFromHardware":
                    roam_node.setRole("robot_name");
                    break;
                case "SetGround":
                    roam_node.setGround();
                    break;
                case "SetISS":
                    roam_node.setISS();
                    break;
                case "EnableRoamBagger":
                    roam_node.setRoamBagger("enabled");
                    break;
                case "DisableRoamBagger":
                    roam_node.setRoamBagger("disabled");
                    break;

                // Test commanding and unknown commands
                default:
                    // Note: test handling is performed on the Python/C++ side
                    if (sCommand.startsWith("Test")) {
                      java.lang.String str_test_code = sCommand.substring(4, sCommand.length());
                      try {
                        Integer test_code = Integer.valueOf(str_test_code);
                        roam_node.sendCommand(test_code);
                        Log.w("edu.mit.ssl.roamcommandasap", str_test_code);
                      }
                      catch (Exception ex) {
                        // Inform GS Manager and GDS, then stop execution.
                        jResult.put("Summary", new JSONObject()
                            .put("Status", "ERROR")
                            .put("Message", "Invalid test number"));
                      }
                    }
                    else {
                      // Inform GS Manager and GDS, then stop execution.
                      jResult.put("Summary", new JSONObject()
                          .put("Status", "ERROR")
                          .put("Message", "Unrecognized command"));
                    }
            }

            // Send data to the GS manager to be shown on the Ground Data System.
            jResult.put("command", sCommand);
            sendData(MessageType.JSON, "data", jResult.toString());
        } catch (JSONException e) {
            // Send an error message to the GSM and GDS
            sendData(MessageType.JSON, "data", "ERROR parsing JSON");
        } catch (Exception ex) {
            // Send an error message to the GSM and GDS
            Log.e("edu.mit.ssl.roamcommandasap", "exception", ex);
            sendData(MessageType.JSON, "data", "Unrecognized ERROR");
        }
    }
  }
