/*
ApiCommandImplementation.java, a part of the ASAP commanding interface.

Misc. node interfacing from Stanford ASL interface.
TODO: needed for future releases?

Keenan Albee, Charles Oestreich, Phillip Johnson, Abhi Cauligi
*/
package edu.mit.ssl.commandasap;

import android.util.Log;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import gov.nasa.arc.astrobee.AstrobeeException;
import gov.nasa.arc.astrobee.Kinematics;
import gov.nasa.arc.astrobee.PendingResult;
import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.Robot;
import gov.nasa.arc.astrobee.RobotFactory;
import gov.nasa.arc.astrobee.ros.DefaultRobotFactory;
import gov.nasa.arc.astrobee.ros.RobotConfiguration;
import gov.nasa.arc.astrobee.types.PlannerType;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

/**
 * A simple API implementation class that provides an easier way to work with the Astrobee API
 */

public class ApiCommandImplementation {

    // Constants that represent the axis in a 3D world
    public static final int X_AXIS = 2;
    public static final int Y_AXIS = 3;
    public static final int Z_AXIS = 4;

    // Center of US Lab
    public static final Point CENTER_US_LAB = new Point(2, 0, 4.8);

    // Default values for the Astrobee's arm
    public static final float ARM_TILT_DEPLOYED_VALUE = 0f;
    public static final float ARM_TILT_STOWED_VALUE = 180f;
    public static final float ARM_PAN_DEPLOYED_VALUE = 0f;
    public static final float ARM_PAN_STOWED_VALUE = 0f;

    // Constants needed to connect with ROS master
    private static final URI ROS_MASTER_URI = URI.create("http://llp:11311");
    private static final String EMULATOR_ROS_HOSTNAME = "hlp";

    // Set the name of the app as the node name
    private static final String NODE_NAME = "commandasap";

    // The instance to access this class
    private static ApiCommandImplementation instance = null;

    // Configuration that will keep data to connect with ROS master
    private RobotConfiguration robotConfiguration = new RobotConfiguration();

    // Instance that will create a robot with the given configuration
    private RobotFactory factory;

    // The robot itself
    private Robot robot;

    // The planner to be used (QP, TRAPEZOIDAL)
    private PlannerType plannerType = null;

    /**
     * Private constructor that prevents other objects from creating instances of this class.
     * Instances of this class must be provided by a static function (Singleton).
     *
     * DO NOT call any Astrobee API function inside this method since the API might not be ready
     * to issue commands.
     */
    private ApiCommandImplementation() {
        // Set up ROS configuration
        configureRobot();

        // Get the factory in order to access the robot.
        factory = new DefaultRobotFactory(robotConfiguration);

        try {
            // Get the robot
            robot = factory.getRobot();

        } catch (AstrobeeException e) {
            Log.e("LOG", "Error with Astrobee");
        } catch (InterruptedException e) {
            Log.e("LOG", "Connection Interrupted");
        }
    }

    /**
     * Static method that provides a unique instance of this class
     *
     * @return A unique instance of this class ready to use
     */
    public static ApiCommandImplementation getInstance() {
        if (instance == null) {
            instance = new ApiCommandImplementation();
        }
        return instance;
    }

    /**
     * This method sets a default configuration for the robot
     */
    private void configureRobot() {
        // Populating robot configuration
        robotConfiguration.setMasterUri(ROS_MASTER_URI);
        robotConfiguration.setHostname(EMULATOR_ROS_HOSTNAME);
        robotConfiguration.setNodeName(NODE_NAME);
    }

    /**
     * This method shutdown the robot factory in order to allow java to close correctly.
     */
    public void shutdownFactory() {
        factory.shutdown();
    }

    /**
     * Method to get the robot from this API Implementation.
     * @return
     */
    public Robot getRobot() {
        return robot;
    }
}
