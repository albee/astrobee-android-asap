/*
StartcommandasapService.java, a part of the ASAP commanding interface.

Starts up HLP pass-through node.

Keenan Albee, Charles Oestreich, Phillip Johnson, Abhi Cauligi
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

public class StartcommandasapService extends StartGuestScienceService {
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
              ASAPStatus.put("flight_mode", status_node.flight_mode);
              ASAPStatus.put("test_finished", status_node.test_finished);
              ASAPStatus.put("coord_ok", status_node.coord_ok);
              ASAPStatus.put("control_mode", status_node.control_mode);
              ASAPStatus.put("regulate_finished", status_node.regulate_finished);
              ASAPStatus.put("uc_bound_activated", status_node.uc_bound_activated);
              ASAPStatus.put("uc_bound_finished", status_node.uc_bound_finished);
              ASAPStatus.put("mrpi_finished", status_node.mrpi_finished);
              ASAPStatus.put("traj_sent", status_node.traj_sent);
              ASAPStatus.put("traj_finished", status_node.traj_finished);
              ASAPStatus.put("gain_mode", status_node.gain_mode);
              ASAPStatus.put("lqrrrt_activated", status_node.lqrrrt_activated);
              ASAPStatus.put("lqrrrt_finished", status_node.lqrrrt_finished);
              ASAPStatus.put("info_traj_send", status_node.info_traj_send);
              ASAPStatus.put("solver_status", status_node.solver_status);
              ASAPStatus.put("cost_value", status_node.cost_value);
              ASAPStatus.put("kkt_value", status_node.kkt_value);
              ASAPStatus.put("sol_time", status_node.sol_time);
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
                    status_node.setRole("primary");
                    break;
                case "SetRoleTarget":
                    status_node.setRole("secondary");
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
