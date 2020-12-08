
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

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;
import org.ros.node.topic.Publisher;
import org.ros.message.MessageListener;
import org.ros.node.NodeConfiguration;
import org.ros.message.Time;
import android.util.Log;

import java.nio.*;
import java.util.*;
import java.util.Random;
import java.io.File;
import java.io.IOException;

import sensor_msgs.JointState; 
import std_msgs.Header;
import std_msgs.String;

import edu.stanford.asl.geckoperchinggripper.types.GeckoGripperState;

public class GeckoGripperStatusNode extends AbstractNodeMain {

    public static GeckoGripperStatusNode instance = null;

    GeckoGripperState gripperState;
    Publisher<JointState> mPublisher;
    Subscriber<JointState> mSubscriber;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("gecko_perching_gripper");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        // Creating subscribers for ROS Topics and adding message listeners
        mPublisher = connectedNode.newPublisher(
                "joint_goals",
                sensor_msgs.JointState._TYPE);

        mSubscriber = connectedNode.newSubscriber(
                "joint_states", JointState._TYPE);
        mSubscriber.addMessageListener(new MessageListener<JointState>() {
            @Override
            public void onNewMessage(JointState jointState) {
                gripperState = updateGripperState(jointState);
                instance = GeckoGripperStatusNode.this;
            }
        }, 10);

        instance = this;
    }

    public static GeckoGripperStatusNode getInstance() {
        return instance;
    }

    private GeckoGripperState updateGripperState(JointState jointState) {
        GeckoGripperState gripperState = new GeckoGripperState();

        // TODO(acauligi): determine how data is unpacked from jointState
        double[] position = jointState.getPosition();
        int gpg_n_bytes = 6;

        byte[] byte_data;
        byte_data = ByteBuffer.allocate(Double.BYTES).putDouble(position[7]).array();

        // Check if leading bytes in header
        if (byte_data[7] == (byte)0xff && byte_data[6] == (byte)0xff && byte_data[5] == (byte)0xfd) {
          gripperState.setValidity(true);
        } else {
          // Bad data
          gripperState.setValidity(false);
          return gripperState;
        }

        // Unpack rest of header bytes
        byte ID = byte_data[4]; // 0x00 for status packet, 0x01 for science packet
        byte ERR_L = byte_data[3];
        byte ERR_H = byte_data[2];
        byte TIME_L = byte_data[1];
        byte TIME_H = byte_data[0];

        boolean overtemperatureFlag = false;
        boolean experimentInProgress = false;
        boolean fileIsOpen = false;
        boolean automaticModeEnable = false;
        boolean wristLock = false;
        boolean adhesiveEngage = false;

        if (ID == (byte)0x01) {
          // science packet
          // Android app doesn't support reading science data
          gripperState.setValidity(false);
          return gripperState;
        }

        // Start reading data bytes
        byte_data = ByteBuffer.allocate(Double.BYTES).putDouble(position[8]).array();

        // TODO(acauligi): how to make reading bytes be endian-agnostic?
        byte STATUS_H = (byte)byte_data[7];
        byte STATUS_L = (byte)byte_data[6];
        byte DL_H = (byte)byte_data[5];
        byte DL_L = (byte)byte_data[4];
        byte IDX_H = (byte)byte_data[3];
        byte IDX_L = (byte)byte_data[2];

        int newStatus = (STATUS_H << 8) | STATUS_L;
        gripperState.updateStatus(newStatus);

        byte mask = (byte)0x80;
        overtemperatureFlag = (STATUS_H & mask) != 0;
        gripperState.setOverTemperatureFlag(overtemperatureFlag);

        mask = (byte)0x01;
        experimentInProgress = (STATUS_H & mask) != 0;
        gripperState.setExperimentInProgress(experimentInProgress);

        mask = (byte)0x20;
        fileIsOpen = (STATUS_L & mask) != 0;
        gripperState.setFileIsOpen(fileIsOpen);

        mask = (byte)0x08;
        automaticModeEnable = (STATUS_L & mask) != 0;
        gripperState.setAutomaticModeEnable(automaticModeEnable);

        mask = (byte)0x02;
        wristLock = (STATUS_L & mask) != 0;
        gripperState.setWristLock(wristLock);

        mask = (byte)0x01;
        adhesiveEngage = (STATUS_L & mask) != 0;
        gripperState.setAdhesiveEngage(adhesiveEngage);

        short expIdx = (short)(((int)IDX_H << 8) | (int)IDX_L);
        gripperState.setExpIdx(expIdx);

        short delay = (short)(((int)DL_H << 8) | (int)DL_L);
        gripperState.setDelay(delay);

        gripperState.setValidity(true);
        return gripperState;
    }
}
