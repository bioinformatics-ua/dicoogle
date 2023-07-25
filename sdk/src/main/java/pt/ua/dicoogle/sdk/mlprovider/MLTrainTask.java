/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle-sdk.
 *
 * Dicoogle/dicoogle-sdk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle-sdk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.sdk.mlprovider;

/**
 * Object to map training tasks.
 * Training tasks have an associated life cycle and unique identifier.
 * Additional information such as current epoch and iteration, as well as training details can be mapped in this object too.
 */
public class MLTrainTask {

    public enum TRAINING_STATUS {
        SUBMITTED,
        RUNNING,
        CANCELLED,
        BUSY,
        REJECTED
    }

    public MLTrainTask(String taskID, TRAINING_STATUS status) {
        this.taskID = taskID;
        this.status = status;
    }

    private String taskID;

    private TRAINING_STATUS status;

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public TRAINING_STATUS getStatus() {
        return status;
    }

    public void setStatus(TRAINING_STATUS status) {
        this.status = status;
    }
}
