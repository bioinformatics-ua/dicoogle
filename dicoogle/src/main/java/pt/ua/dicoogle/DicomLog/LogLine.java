/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle.
 *
 * Dicoogle/dicoogle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.ua.dicoogle.DicomLog;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A log line representing a DICOM event.
 */
public class LogLine implements Serializable {
    static final long serialVersionUID = 1L;

    private final String type;
    @Deprecated
    private final String date;
    private final String ae;
    private final String add;
    private final String params;

    /**
     * @deprecated Discard the `date` parameter.
     */
    @Deprecated
    public LogLine(String type, String date, String ae, String add, String params) {
        this.type = type;
        this.date = date;
        this.ae = ae;
        this.add = add;
        this.params = params;
    }

    /**
     * @param type the type of event
     * @param ae the peer AE title
     * @param add the additional information about the operation
     * @param params the parameters of the operation
     */
    public LogLine(String type, String ae, String add, String params) {
        this.type = type;
        this.date = getDateTime();
        this.ae = ae;
        this.add = add;
        this.params = params;
    }

    private static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the date and time of the event
     * 
     * @deprecated Will be removed in Dicoogle 4 in favor of
     * letting the logger record the date and time of the event.
     */
    @Deprecated
    public String getDate() {
        return date;
    }

    /**
     * @return the AE title
     */
    public String getAe() {
        return ae;
    }

    /**
     * @return the additional information
     */
    public String getAdd() {
        return add;
    }

    /**
     * @return the additional parameters
     */
    public String getParams() {
        return params;
    }
}
