
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

import android.os.Handler;
import android.os.Looper;

import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

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
    @Override
    public void onGuestScienceCustomCmd(String command) {
        /* Inform the Guest Science Manager (GSM) and the Ground Data System (GDS)
         * that this app received a command. */
        sendReceivedCustomCommand("info");
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

            // This variable will contain the result of the last successful or unsuccessful movement
            Result result;

            switch (sCommand) {
                case "gecko_gripper_open":
                    float data = 0x00;
                    mSimpleNode.sendMessage(sCommand, data);
                    break;
                case "gecko_gripper_close":
                    float data = 0x00;
                    mSimpleNode.sendMessage(sCommand, data);
                    break;
                case "gecko_gripper_engage":
                    float data = 0x00;
                    mSimpleNode.sendMessage(sCommand, data);
                    break;
                case "gecko_gripper_disengage":
                    float data = 0x00;
                    mSimpleNode.sendMessage(sCommand, data);
                    break;
                case "gecko_gripper_lock":
                    float data = 0x00;
                    mSimpleNode.sendMessage(sCommand, data);
                    break;
                case "gecko_gripper_unlock":
                    float data = 0x00;
                    mSimpleNode.sendMessage(sCommand, data);
                    break;
                case "gecko_gripper_enable_auto":
                    float data = 0x00;
                    mSimpleNode.sendMessage(sCommand, data);
                    break;
                case "gecko_gripper_disable_auto":
                    float data = 0x00;
                    mSimpleNode.sendMessage(sCommand, data);
                    break;
                case "gecko_gripper_toggle_auto":
                    // TODO(acauligi): return "cmd not implemented"
                    break;
                case "gecko_gripper_mark_gripper":
                    float IDX = (float) jCommand.get("IDX");
                    mSimpleNode.sendMessage(sCommand, IDX);
                    break;
                case "gecko_gripper_set_delay":
                    float DL = (float) jCommand.get("DL");
                    mSimpleNode.sendMessage(sCommand, DL);
                    break;
                case "gecko_gripper_open_exp":
                    float IDX = (float) jCommand.get("IDX");
                    mSimpleNode.sendMessage(sCommand, IDX);
                    break;
                case "gecko_gripper_next_record":
                    float SKIP = (float) jCommand.get("SKIP");
                    mSimpleNode.sendMessage(sCommand, SKIP);
                    break;
                case "gecko_gripper_seek_record":
                    float RN = (float) jCommand.get("RN");
                    mSimpleNode.sendMessage(sCommand, RN);
                    break;
                case "gecko_gripper_close_exp":
                    float data = 0x00;
                    mSimpleNode.sendMessage(sCommand, data);
                    break;
                case "gecko_gripper_status":
                    float data = 0x00;
                    mSimpleNode.sendMessage(sCommand, data);
                    break;
                case "gecko_gripper_record":
                    float data = 0x00;
                    mSimpleNode.sendMessage(sCommand, data);
                    break;
                case "gecko_gripper_exp":
                    float data = 0x00;
                    mSimpleNode.sendMessage(sCommand, data);
                    break;
                case "gecko_gripper_delay":
                    float data = 0x00;
                    mSimpleNode.sendMessage(sCommand, data);
                    break;
                default:
                    // Inform GS Manager and GDS, then stop execution.
                    sendData(MessageType.JSON, "data", "ERROR: Unrecognized command");
                    return;
            }

            if (result == null) {
                // There were no points to loop
                jResult.put("Summary", new JSONObject()
                        .put("Status", "ERROR")
                        .put("Message", "Trajectory was not defined"));
            } else if (!result.hasSucceeded()) {
                // If a goal point failed.
                jResult.put("Summary", new JSONObject()
                        .put("Status", result.getStatus())
                        .put("Message", result.getMessage()));
            } else {
                // Success!
                jResult.put("Summary", new JSONObject()
                        .put("Status", result.getStatus())
                        .put("Message", "DONE!"));
            }

            // Send data to the GS manager to be shown on the Ground Data System.
            sendData(MessageType.JSON, "data", jResult.toString());

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
        // Get a unique instance of the Astrobee API in order to command the robot.
        api = ApiCommandImplementation.getInstance();

        GeckoGripperStatusNode geckoGripperNode = new GeckoGripperStatusNode();
        // microphoneNode.publishSound(ps);
        // mSimpleNode = new SimpleNode();
        // mSimpleNode.setListener(new SimpleNode.OnMessageListener() {
        //     @Override
        //     public void onMessage(final String msg) {
        //         mMainHandler.post(new Runnable() {
        //             @Override
        //             public void run() {
        //                 mRecvText.setText(msg);
        //             }
        //         });
        //     }
        // });

        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(getRosHostname());
        nodeConfiguration.setMasterUri(getMasterUri());

        node.execute(mSimpleNode, nodeConfiguration);

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

        // Inform the GS manager and the GDS that this app stopped.
        sendStopped("info");

        // Destroy all connection with the GS Manager.
        terminate();
    }
}
