
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
/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class StartRoamcommandasapService extends StartGuestScienceService {
    // The API implementation
    private ApiCommandImplementation api = null;

    private RoamStatusNode roam_node = null;
    private boolean stop = false;

    NodeMainExecutor nodeMainExecutor;
    private static final URI ROS_MASTER_URI = URI.create("http://llp:11311");
    private static final java.lang.String ROS_HOSTNAME = "hlp";

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

        // while (true) {
        //   sendStarted("info");
        //   try {
        //     Thread.sleep(2000);
        //   }
        //   catch (Exception e) {
        //     e.printStackTrace();
        //   }
        // }

        try {
            // Transform the String command into a JSON object so we can read it.
            JSONObject jCommand = new JSONObject(command);

            // Get the name of the command we received. See commands.xml files in res folder.
            String sCommand = jCommand.getString("name");

            // JSON object that will contain the data we will send back to the GSM and GDS
            JSONObject jResult = new JSONObject();

            switch (sCommand) {
                // You may handle your commands here
                default:
                    // Inform GS Manager and GDS, then stop execution.
                    jResult.put("Summary", new JSONObject()
                        .put("Status", "ERROR")
                        .put("Message", "Unrecognized command"));
                case "StopTest":
                    roam_node.sendCommand(-1);
                    break;
                case "Test1":
                    roam_node.sendCommand(1);
                    break;
                case "Test2":
                    roam_node.sendCommand(2);
                    break;
                case "Test3":
                    roam_node.sendCommand(3);
                    break;
                case "Test4":
                    roam_node.sendCommand(4);
                    break;
                case "Test5":
                    roam_node.sendCommand(5);
                    break;
                case "Test6":
                    roam_node.sendCommand(6);
                    break;
                case "Test7":
                    roam_node.sendCommand(7);
                    break;
                case "Test8":
                    roam_node.sendCommand(8);
                    break;
                case "Test9":
                    roam_node.sendCommand(9);
                    break;
                case "Test10":
                    roam_node.sendCommand(10);
                    break;
                case "Test11":
                    roam_node.sendCommand(11);
                    break;
                case "Test12":
                    roam_node.sendCommand(12);
                    break;
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
            }

            // Send data to the GS manager to be shown on the Ground Data System.
            sendData(MessageType.JSON, "command", sCommand);
            //sendData(MessageType.JSON, "data", jResult.toString());
        } catch (JSONException e) {
            // Send an error message to the GSM and GDS
            sendData(MessageType.JSON, "data", "ERROR parsing JSON");
        } catch (Exception ex) {
            // Send an error message to the GSM and GDS
            sendData(MessageType.JSON, "data", "Unrecognized ERROR");
        }
    }


    /*
    int32   test_number
    string  test_LUT
    string  test_tumble_type
    string  test_control_mode
    string  test_state_mode
    int32   dlr_LUT_param
    bool    chaser_coord_ok
    bool    target_coord_ok
    bool    slam_activate
    bool    traj_gen_dlr_activate
    bool    uc_bound_activate
    bool    chaser_regulate_finished
    bool    target_regulate_finished
    bool    slam_converged
    bool    inertia_estimated
    bool    motion_plan_finished
    float64 motion_plan_wait_time
    bool    uc_bound_finished
    bool    mrpi_finished
    bool    traj_finished
    bool    test_finished
    bool    default_control
    string  td_control_mode
    string  td_state_mode
    string  td_flight_mode
    bool    casadi_on_target
    */
    public void printTDStatus() {
      String test_number = roam_node.test_number;
      try{
        JSONObject binaryStatus = new JSONObject();
        binaryStatus.put("test_number", test_number);
                    // .put("test_LUT", test_LUT)
                    // .put("test_tumble_type", test_tumble_type)
                    // .put("test_control_mode", test_control_mode)
                    // .put("test_state_mode", test_state_mode)
                    // .put("dlr_LUT_param", dlr_LUT_param)
                    // .put("chaser_coord_ok", chaser_coord_ok)
                    // .put("target_coord_ok", target_coord_ok)
                    // .put("slam_activate", slam_activate)
                    // .put("traj_gen_dlr_activ", traj_gen_dlr_activ)
                    // .put("uc_bound_activate", uc_bound_activate)
                    // .put("chaser_regulate_fi", chaser_regulate_fi)
                    // .put("target_regulate_fi", target_regulate_fi)
                    // .put("slam_converged", slam_converged)
                    // .put("inertia_estimated", inertia_estimated)
                    // .put("motion_plan_finish", motion_plan_finish)
                    // .put("motion_plan_wait_t", motion_plan_wait_t)
                    // .put("uc_bound_finished", uc_bound_finished)
                    // .put("mrpi_finished", mrpi_finished)
                    // .put("traj_finished", traj_finished)
                    // .put("test_finished", test_finished)
                    // .put("default_control", default_control)
                    // .put("td_control_mode", td_control_mode)
                    // .put("td_state_mode", td_state_mode)
                    // .put("td_flight_mode", td_flight_mode)
                    // .put("casadi_on_target", casadi_on_target);
      } catch (JSONException e) {
          // Send an error message to the GSM and GDS
          e.printStackTrace();
          sendData(MessageType.JSON, "data", "ERROR parsing TDStatus JSON");
      }
    }
}
