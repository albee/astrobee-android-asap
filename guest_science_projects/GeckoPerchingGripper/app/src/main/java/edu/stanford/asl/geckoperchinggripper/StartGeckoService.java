
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Looper;
import android.content.Intent;

import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Publisher;

import sensor_msgs.JointState;
import std_msgs.String;

import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.android.gs.MessageType;
import gov.nasa.arc.astrobee.android.gs.StartGuestScienceService;

public class StartGeckoService extends StartGuestScienceService {
    /**
     * This function is called when the GS manager sends a custom command to your apk.
     * Please handle your commands in this function.
     *
     * @param command
     */
    private GeckoGripperStatusNode gecko_gripper_node;

    @Override
    public void onGuestScienceCustomCmd(java.lang.String command) {
        /* Inform the Guest Science Manager (GSM) and the Ground Data System (GDS)
         * that this app received a command. */
        sendReceivedCustomCommand("info");

        try {
            JSONObject obj = new JSONObject(command);
            java.lang.String commandStr = obj.getString("name");

            JSONObject jResponse = new JSONObject();

            // This variable will contain the result of the last successful or unsuccessful movement
            Result result;

            java.util.List<java.lang.String> msg_name = new java.util.ArrayList<java.lang.String>();


            sensor_msgs.JointState msg = gecko_gripper_node.mPublisher.newMessage();


            switch (commandStr) {
                case "gecko_gripper_open":
                    msg_name.add("perching_gecko_gripper_open");
                    jResponse.put("Summary", "perching_gecko_gripper_open");
                    break;
                case "gecko_gripper_close":
                    msg_name.add("perching_gecko_gripper_close");
                    jResponse.put("Summary", "perching_gecko_gripper_close");
                    break;
                case "gecko_gripper_engage":
                    msg_name.add("perching_gecko_gripper_engage");
                    jResponse.put("Summary", "perching_gecko_gripper_engage");
                    break;
                case "gecko_gripper_disengage":
                    msg_name.add("perching_gecko_gripper_disengage");
                    jResponse.put("Summary", "perching_gecko_gripper_disengage");
                    break;
                case "gecko_gripper_lock":
                    msg_name.add("perching_gecko_gripper_lock");
                    jResponse.put("Summary", "perching_gecko_gripper_lock");
                    break;
                case "gecko_gripper_unlock":
                    msg_name.add("perching_gecko_gripper_unlock");
                    jResponse.put("Summary", "perching_gecko_gripper_unlock");
                    break;
                case "gecko_gripper_enable_auto":
                    msg_name.add("perching_gecko_gripper_enable_auto");
                    jResponse.put("Summary", "perching_gecko_gripper_enable_auto");
                    break;
                case "gecko_gripper_disable_auto":
                    msg_name.add("perching_gecko_gripper_disable_auto");
                    jResponse.put("Summary", "perching_gecko_gripper_disable_auto");
                    break;
                case "gecko_gripper_toggle_auto":
                    // TODO(acauligi): return "cmd not implemented"
                    msg_name.add("perching_gecko_gripper_toggle_auto");
                    jResponse.put("Summary", "perching_gecko_gripper_toggle_auto");
                    break;
                case "gecko_gripper_mark_gripper":
                    // float IDX = (float) jCommand.get("IDX");
                    msg_name.add("perching_gecko_gripper_mark_gripper");
                    jResponse.put("Summary", "perching_gecko_gripper_mark_gripper");
                    break;
                case "gecko_gripper_set_delay":
                    // float DL = (float) jCommand.get("DL");
                    msg_name.add("perching_gecko_gripper_set_delay");
                    jResponse.put("Summary", "perching_gecko_gripper_set_delay");
                    break;
                case "gecko_gripper_open_exp":
                    // float IDX = (float) jCommand.get("IDX");
                    msg_name.add("perching_gecko_gripper_open_exp");
                    jResponse.put("Summary", "perching_gecko_gripper_open_exp");
                    break;
                case "gecko_gripper_next_record":
                    // float SKIP = (float) jCommand.get("SKIP");
                    msg_name.add("perching_gecko_gripper_next_record");
                    jResponse.put("Summary", "perching_gecko_gripper_next_record");
                    break;
                case "gecko_gripper_seek_record":
                    // float RN = (float) jCommand.get("RN");
                    msg_name.add("perching_gecko_gripper_seek_record");
                    jResponse.put("Summary", "perching_gecko_gripper_seek_record");
                    break;
                case "gecko_gripper_close_exp":
                    // float data = 0x00;
                    msg_name.add("perching_gecko_gripper_close_exp");
                    jResponse.put("Summary", "perching_gecko_gripper_close_exp");
                    break;
                case "gecko_gripper_status":
                    msg_name.add("perching_gecko_gripper_status");
                    jResponse.put("Summary", "perching_gecko_gripper_status");
                    break;
                case "gecko_gripper_record":
                    // float data = 0x00;
                    msg_name.add("perching_gecko_record");
                    jResponse.put("Summary", "perching_gecko_gripper_record");
                    break;
                case "gecko_gripper_exp":
                    // float data = 0x00;
                    msg_name.add("perching_gecko_gripper_exp");
                    jResponse.put("Summary", "perching_gecko_gripper_exp");
                    break;
                case "gecko_gripper_delay":
                    // float data = 0x00;
                    msg_name.add("perching_gecko_gripper_delay");
                    jResponse.put("Summary", "perching_gecko_gripper_delay");
                    break;
                default:
                    // Inform GS Manager and GDS, then stop execution.
                    sendData(MessageType.JSON, "data", "ERROR: Unrecognized command");
                    return;
            }

            msg.setName(msg_name);
            gecko_gripper_node.mPublisher.publish(msg);

            sendData(MessageType.JSON, "data", jResponse.toString());
        } catch (JSONException e) {
            // Send an error message to the GSM and GDS
            sendData(MessageType.JSON, "data", "ERROR parsing JSON");
        } catch (Exception ex) {
            // Send an error message to the GSM and GDS
            sendData(MessageType.JSON, "data", "Unrecognized ERROR");
        }
    }

    /**
     * This function is called when the GS manager starts your apk.
     * Put all of your start up code in here.
     */
    @Override
    public void onGuestScienceStart() {
        // Start the interface
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

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
        // Inform the GS manager and the GDS that this app stopped.
        sendStopped("info");

        // Destroy all connection with the GS Manager.
        terminate();
    }
}
