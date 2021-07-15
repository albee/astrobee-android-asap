
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

package edu.mit.ssl.commandasap;

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

public class StartCommandAsapService extends StartGuestScienceService {
    // The API implementation
    private ApiCommandImplementation api = null;

    private StatusNode status_node = null;
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

            status_node.updateParams();
            try {
              JSONObject ASAPStatus = new JSONObject();
              ASAPStatus.put("test_num", status_node.test_num);
              ASAPStatus.put("td_flight_mode", status_node.td_flight_mode);
              ASAPStatus.put("td_control_mode", status_node.td_control_mode);
              ASAPStatus.put("slam_activate", status_node.slam_activate);
              ASAPStatus.put("target_regulate_finished", status_node.target_regulate_finished);
              ASAPStatus.put("chaser_regulate_finished", status_node.chaser_regulate_finished);
              ASAPStatus.put("motion_plan_finished", status_node.motion_plan_finished);
              ASAPStatus.put("motion_plan_wait_time", status_node.motion_plan_wait_time);
              ASAPStatus.put("default_control", status_node.default_control);
              ASAPStatus.put("my_role", status_node.my_role);
              ASAPStatus.put("test_LUT", status_node.test_LUT);
              ASAPStatus.put("test_tumble_type", status_node.test_tumble_type);
              ASAPStatus.put("test_control_mode", status_node.test_control_mode);
              ASAPStatus.put("test_state_mode", status_node.test_state_mode);
              ASAPStatus.put("dlr_LUT_param", status_node.dlr_LUT_param);
              ASAPStatus.put("traj_gen_dlr_activate", status_node.traj_gen_dlr_activate);
              ASAPStatus.put("uc_bound_activate", status_node.uc_bound_activate);
              ASAPStatus.put("slam_converged", status_node.slam_converged);
              ASAPStatus.put("inertia_estimated", status_node.inertia_estimated);
              ASAPStatus.put("uc_bound_finished", status_node.uc_bound_finished);
              ASAPStatus.put("mrpi_finished", status_node.mrpi_finished);
              ASAPStatus.put("traj_finished", status_node.traj_finished);
              ASAPStatus.put("test_finished", status_node.test_finished);
              ASAPStatus.put("td_state_mode", status_node.td_state_mode);
              ASAPStatus.put("casadi_on_target", status_node.casadi_on_target);
              sendData(MessageType.JSON, "ReswarmStatus", ASAPStatus.toString());  // modify to your payload

              JSONObject TelemID = new JSONObject();
              TelemID.put("TelemID", status_node.global_gds_param_count);
              sendData(MessageType.JSON, "TelemID", TelemID.toString());
            } catch (JSONException e) {
              // Send an error message to the GSM and GDS
              e.printStackTrace();
              sendData(MessageType.JSON, "data", "ERROR parsing ASAPStatus JSON");
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

        status_node = new StatusNode();  // this is the custom node used for commanding

        nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        nodeMainExecutor.execute(status_node, nodeConfiguration);

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
        //set rosparam asapcommand to stopped
        status_node.sendStopped();

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
                    status_node.sendCommand(-1);
                    break;

                // Role and Scenario Setting
                case "SetRoleChaser":
                    status_node.setRole("chaser");
                    break;
                case "SetRoleTarget":
                    status_node.setRole("target");
                    break;
                case "SetRoleFromHardware":
                    status_node.setRole("robot_name");
                    break;
                case "SetGround":
                    status_node.setGround();
                    break;
                case "SetISS":
                    status_node.setISS();
                    break;
                case "EnableRoamBagger":
                    status_node.setRoamBagger("enabled");
                    break;
                case "DisableRoamBagger":
                    status_node.setRoamBagger("disabled");
                    break;

                // Test commanding and unknown commands
                default:
                    // Note: test handling is performed on the Python/C++ side
                    if (sCommand.startsWith("Test")) {
                      java.lang.String str_test_code = sCommand.substring(4, sCommand.length());
                      try {
                        Integer test_code = Integer.valueOf(str_test_code);
                        status_node.sendCommand(test_code);
                        Log.w("edu.mit.ssl.commandasap", str_test_code);
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
            Log.e("edu.mit.ssl.commandasap", "exception", ex);
            sendData(MessageType.JSON, "data", "Unrecognized ERROR");
        }
    }
  }
