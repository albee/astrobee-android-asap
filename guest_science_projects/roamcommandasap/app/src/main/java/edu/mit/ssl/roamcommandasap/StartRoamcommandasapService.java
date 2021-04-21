
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
              TDStatus.put("td_flight_mode", roam_node.td_flight_mode);
              TDStatus.put("td_control_mode", roam_node.td_control_mode);
              TDStatus.put("slam_activate", roam_node.slam_activate);
              TDStatus.put("chaser_regulate_finished", roam_node.chaser_regulate_finished);
              TDStatus.put("target_regulate_finished", roam_node.target_regulate_finished);
              TDStatus.put("motion_plan_wait_time", roam_node.motion_plan_wait_time);
              TDStatus.put("motion_plan_finished", roam_node.motion_plan_finished);
              TDStatus.put("default_control", roam_node.default_control);
              TDStatus.put("role", roam_node.role);
              sendData(MessageType.JSON, "TDStatus", TDStatus.toString());
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
                // StopTest
                case "StopTest":
                    roam_node.sendCommand(-1);
                    break;

                // UnitTests
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

                // StandardTests
                case "Test111":
                    roam_node.sendCommand(111);
                    break;
                case "Test112":
                    roam_node.sendCommand(112);
                    break;
                case "Test113":
                    roam_node.sendCommand(113);
                    break;
                case "Test121":
                    roam_node.sendCommand(121);
                    break;
                case "Test122":
                    roam_node.sendCommand(122);
                    break;
                case "Test123":
                    roam_node.sendCommand(123);
                    break;
                case "Test131":
                    roam_node.sendCommand(131);
                    break;
                case "Test132":
                    roam_node.sendCommand(132);
                    break;
                case "Test133":
                    roam_node.sendCommand(133);
                    break;
                case "Test141":
                    roam_node.sendCommand(141);
                    break;
                case "Test142":
                    roam_node.sendCommand(142);
                    break;
                case "Test143":
                    roam_node.sendCommand(143);
                    break;

                case "Test211":
                    roam_node.sendCommand(211);
                    break;
                case "Test212":
                    roam_node.sendCommand(212);
                    break;
                case "Test213":
                    roam_node.sendCommand(213);
                    break;
                case "Test221":
                    roam_node.sendCommand(221);
                    break;
                case "Test222":
                    roam_node.sendCommand(222);
                    break;
                case "Test223":
                    roam_node.sendCommand(223);
                    break;
                case "Test231":
                    roam_node.sendCommand(231);
                    break;
                case "Test232":
                    roam_node.sendCommand(232);
                    break;
                case "Test233":
                    roam_node.sendCommand(233);
                    break;
                case "Test241":
                    roam_node.sendCommand(241);
                    break;
                case "Test242":
                    roam_node.sendCommand(242);
                    break;
                case "Test243":
                    roam_node.sendCommand(243);
                    break;

                case "Test311":
                    roam_node.sendCommand(311);
                    break;
                case "Test312":
                    roam_node.sendCommand(312);
                    break;
                case "Test313":
                    roam_node.sendCommand(313);
                    break;
                case "Test321":
                    roam_node.sendCommand(321);
                    break;
                case "Test322":
                    roam_node.sendCommand(322);
                    break;
                case "Test323":
                    roam_node.sendCommand(323);
                    break;
                case "Test331":
                    roam_node.sendCommand(331);
                    break;
                case "Test332":
                    roam_node.sendCommand(332);
                    break;
                case "Test333":
                    roam_node.sendCommand(333);
                    break;
                case "Test341":
                    roam_node.sendCommand(341);
                    break;
                case "Test342":
                    roam_node.sendCommand(342);
                    break;
                case "Test343":
                    roam_node.sendCommand(343);
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
            }

            // Send data to the GS manager to be shown on the Ground Data System.
            sendData(MessageType.JSON, "command", sCommand);
            //sendData(MessageType.JSON, "data", jResult.toString());
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
