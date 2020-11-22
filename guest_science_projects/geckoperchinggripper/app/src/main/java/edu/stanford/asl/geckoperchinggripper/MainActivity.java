
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

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.net.URI;

public class MainActivity extends RosActivity {
    // IP Address ROS Master
    private static final URI ROS_MASTER_URI = URI.create("http://llp:11311");

    // ROS - Android Node
    private GeckoGripperStatusNode geckoGripperStatusNode = null;

    boolean isNodeExecuting = false;

    /*
     * Handler and Runnable for permanent interface updating
     */
    Handler handler;

    public MainActivity() {
        super("Battery Monitor", "Battery Monitor Service", ROS_MASTER_URI);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Android stuff. Activities history, layouts, etc. Actually we won't use an interface.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stopping service and handler
        nodeMainExecutorService.stopSelf();
        handler.removeCallbacksAndMessages(null);

        // Log.i("LOG", "ONDESTROY FINISHED!");
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        geckoGripperStatusNode = new GeckoGripperStatusNode();

        // Setting configurations for ROS-Android Node
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic("hlp");
        nodeConfiguration.setMasterUri(getMasterUri());

        nodeMainExecutor.execute(geckoGripperStatusNode, nodeConfiguration);

        // Log.i("LOG", "NODE EXECUTING!");
        isNodeExecuting = true;
    }
}
