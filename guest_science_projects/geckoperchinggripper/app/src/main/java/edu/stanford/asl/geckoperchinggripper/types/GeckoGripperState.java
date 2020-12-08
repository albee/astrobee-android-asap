
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

package edu.stanford.asl.geckoperchinggripper.types;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class GeckoGripperState implements Serializable {

    private int lastStatusReadTime;
    private int errorStatus;
    private boolean adhesiveEngage;
    private boolean wristLock;
    private boolean automaticModeEnable;
    private boolean experimentInProgress;
    private boolean overtemperatureFlag;
    private boolean fileIsOpen;
    private short expIdx;
    private short delay;
    private boolean validData;
    private int lastStatus;
    private boolean newStatusReceived;

    public GeckoGripperState() {
        lastStatusReadTime = -1;
        errorStatus = 0;
        adhesiveEngage = false;
        wristLock = false;
        automaticModeEnable = false;
        fileIsOpen = false;
        experimentInProgress = false;
        overtemperatureFlag = false;
        expIdx = -1;
        delay = -1;
        validData = false;
        lastStatus = 0x0000;
        newStatusReceived = false;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("Last Status Read Time", lastStatusReadTime)
                .put("Validity", validData)
                .put("Error Status", errorStatus)
                .put("Adhesive Engage", adhesiveEngage)
                .put("Wrist Lock", wristLock)
                .put("Automatic Mode Enable", automaticModeEnable)
                .put("File is Open", fileIsOpen)
                .put("Experiment in Progress", experimentInProgress)
                .put("Overtemperature Flag", overtemperatureFlag)
                .put("Experiment Idx", expIdx)
                .put("Delay", delay);

        return json;
    }

    public int getLastStatusReadTime() {
        return lastStatusReadTime;
    }

    public boolean getNewStatusReceived() {
        return newStatusReceived;
    }

    public void setNewStatusReceived(boolean newStatusReceived) {
        this.newStatusReceived = newStatusReceived;
    }

    public void setLastStatusReadTime(int lastStatusReadTime) {
        this.lastStatusReadTime = lastStatusReadTime;
    }

    public int getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(int errorStatus) {
        this.errorStatus = errorStatus;
    }

    public boolean getAdhesiveEngage() {
        return adhesiveEngage;
    }

    public void setAdhesiveEngage(boolean adhesiveEngage) {
        this.adhesiveEngage = adhesiveEngage;
    }

    public boolean getWristLock() {
        return wristLock;
    }

    public void setWristLock(boolean wristLock) {
        this.wristLock = wristLock;
    }

    public boolean getAutomaticModeEnable() {
        return automaticModeEnable;
    }

    public void setAutomaticModeEnable(boolean automaticModeEnable) {
        this.automaticModeEnable = automaticModeEnable;
    }

    public boolean getExperimentInProgress() {
        return experimentInProgress;
    }

    public void setExperimentInProgress(boolean experimentInProgress) {
        this.experimentInProgress = experimentInProgress;
    }

    public boolean getOverTemperatureFlag() {
        return overtemperatureFlag;
    }

    public void setOverTemperatureFlag(boolean overtemperatureFlag) {
        // no temperature sensor on gripper control board
        this.overtemperatureFlag = false;
        this.overtemperatureFlag = overtemperatureFlag;
    }

    public boolean getFileIsOpen() {
        return fileIsOpen;
    }

    public void setFileIsOpen(boolean fileIsOpen) {
        this.fileIsOpen = fileIsOpen;
    }

    public int getExpIdx() {
        return expIdx;
    }

    public void setExpIdx(short expIdx) {
        this.expIdx = expIdx;
    }

    public short getDelay() {
        return delay;
    }

    public void setDelay(short delay) {
        this.delay = delay;
    }

    public void setValidity(boolean validData) {
        this.validData = validData;
    }

    public boolean isDataValid() {
        return this.validData; 
    }

    public void updateStatus(int newStatus) {
      if (this.newStatusReceived) {
        return;
      } else if (newStatus != this.lastStatus) {
        this.newStatusReceived = true;
        this.lastStatus = newStatus;
      } else {
        this.newStatusReceived = false;
      }
    }
}
