
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

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class StartGeckoperchinggripperService extends StartGuestScienceService {

    // The API implementation
    // private ApiCommandImplementation api = null;

     /**
     * This function is called when the GS manager starts your apk.
     * Put all of your start up code in here.
     */
    NodeMainExecutor nodeMainExecutor;
    private GeckoGripperStatusNode gecko_gripper_node = null;

    // IP Address ROS Master and Hostname
    private static final URI ROS_MASTER_URI = URI.create("http://llp:11311");
    private static final java.lang.String ROS_HOSTNAME = "hlp";

    @Override
    public void onGuestScienceStart() {
        // Get a unique instance of the Astrobee API in order to command the robot.
        // api = ApiCommandImplementation.getInstance();

        // Setting configurations for ROS-Android Node
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(ROS_HOSTNAME);
        nodeConfiguration.setMasterUri(ROS_MASTER_URI);

        gecko_gripper_node = new GeckoGripperStatusNode();
        
        nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        nodeMainExecutor.execute(gecko_gripper_node, nodeConfiguration);

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
        // api.shutdownFactory();

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
                    break;
                case "gecko_gripper_close":
                    break;
                case "gecko_gripper_engage":
                    break;
                case "gecko_gripper_disengage":
                    break;
                case "gecko_gripper_lock":
                    break;
                case "gecko_gripper_unlock":
                    break;
                case "gecko_gripper_enable_auto":
                    break;
                case "gecko_gripper_disable_auto":
                    break;
                case "gecko_gripper_toggle_auto":
                    // TODO(acauligi): return "cmd not implemented"
                    break;
                case "gecko_gripper_mark_gripper":
                    msg_pos[0] = Float.parseFloat(jCommand.getString("IDX"));
                    break;
                case "gecko_gripper_set_delay":
                    msg_pos[0] = Float.parseFloat(jCommand.getString("DL"));
                    break;
                case "gecko_gripper_open_exp":
                    msg_pos[0] = Float.parseFloat(jCommand.getString("IDX"));
                    break;
                case "gecko_gripper_next_record":
                    msg_pos[0] = Float.parseFloat(jCommand.getString("SKIP"));
                    break;
                case "gecko_gripper_seek_record":
                    msg_pos[0] = Float.parseFloat(jCommand.getString("RN"));
                    break;
                case "gecko_gripper_close_exp":
                    break;
                case "gecko_gripper_status":
                    break;
                case "gecko_gripper_record":
                    break;
                case "gecko_gripper_exp":
                    break;
                case "gecko_gripper_delay":
                    break;
                case "gecko_gripper_print_status":
                    // Make sure to query delay and exp idx each
                    // time status is printed (by default)
                    msg_name.clear();
                    msg_name.add("gecko_gripper_delay");
                    msg.setName(msg_name);
                    msg.setPosition(msg_pos);
                    gecko_gripper_node.mPublisher.publish(msg);

                    msg_name.clear();
                    msg_name.add("gecko_gripper_exp");
                    msg.setName(msg_name);
                    msg.setPosition(msg_pos);
                    gecko_gripper_node.mPublisher.publish(msg);

                    msg_name.clear();

                    JSONObject jsonGripperState= gecko_gripper_node.gripperState.toJSON();
                    sendData(MessageType.JSON, "gripper state", jsonGripperState.toString());
                    gecko_gripper_node.gripperState.setNewStatusReceived(false);    // clear new status received flag
                    break;
                case "gecko_gripper_reset_gripper":
                    // After perching experiment, resets gripper
                    msg_name.clear();
                    msg_name.add("gecko_gripper_disable_auto");
                    msg.setName(msg_name);
                    msg.setPosition(msg_pos);
                    gecko_gripper_node.mPublisher.publish(msg);

                    msg_name.clear();
                    msg_name.add("gecko_gripper_disengage");
                    msg.setName(msg_name);
                    msg.setPosition(msg_pos);
                    gecko_gripper_node.mPublisher.publish(msg);

                    msg_name.clear();
                    msg_name.add("gecko_gripper_unlock");
                    msg.setName(msg_name);
                    msg.setPosition(msg_pos);
                    gecko_gripper_node.mPublisher.publish(msg);

                    msg_name.clear();
                    msg_name.add("gecko_gripper_delay");
                    msg.setName(msg_name);
                    msg.setPosition(msg_pos);
                    gecko_gripper_node.mPublisher.publish(msg);

                    msg_name.clear();
                    msg_name.add("gecko_gripper_exp");
                    msg.setName(msg_name);
                    msg.setPosition(msg_pos);
                    gecko_gripper_node.mPublisher.publish(msg);

                    msg_name.clear();
                    break;

                default:
                    // Inform GS Manager and GDS, then stop execution.
                    jResult.put("Summary", new JSONObject()
                        .put("Status", "ERROR")
                        .put("Message", "Unrecognized command"));
            }

            jResult.put("Summary", sCommand);
            msg_name.add(sCommand);
            msg.setName(msg_name);
            msg.setPosition(msg_pos);
            gecko_gripper_node.mPublisher.publish(msg);

            // Send data to the GS manager to be shown on the Ground Data System.
            sendData(MessageType.JSON, "data", jResult.toString());
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
}
