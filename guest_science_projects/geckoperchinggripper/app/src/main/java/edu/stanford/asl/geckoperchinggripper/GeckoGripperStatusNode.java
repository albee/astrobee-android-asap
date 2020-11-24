
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
import java.nio.ByteBuffer;

import sensor_msgs.JointState; 
import std_msgs.Header;
import std_msgs.String;

import edu.stanford.asl.geckoperchinggripper.types.GeckoGripperState;


public class GeckoGripperStatusNode extends AbstractNodeMain {

    public static GeckoGripperStatusNode instance = null;

    private GeckoGripperState gripperState;
    Publisher<JointState> mPublisher;

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

        instance = this;
    }

    public static GeckoGripperStatusNode getInstance() {
        return instance;
    }
}
