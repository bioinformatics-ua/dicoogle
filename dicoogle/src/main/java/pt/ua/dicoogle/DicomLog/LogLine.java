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
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class LogLine implements Serializable
{
    static final long serialVersionUID = 1L;

    private String type;
    private String date;
    private String ae;
    private String add;
    private String params;

    public LogLine(String type, String date, String ae, String add, String params)
    {
        this.type = type ; 
        this.date = date ; 
        this.ae = ae ; 
        this.add = add ;
        this.params = params;
    }

     public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }



    /**
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * @return the date
     */
    public String getDate()
    {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date)
    {
        this.date = date;
    }

    /**
     * @return the ae
     */
    public String getAe()
    {
        return ae;
    }

    /**
     * @param ae the ae to set
     */
    public void setAe(String ae)
    {
        this.ae = ae;
    }

    /**
     * @return the addMoveDestination
     */
    public String getAdd()
    {
        return add;
    }

    /**
     * @param add the addMoveDestination to set
     */
    public void setAdd(String add)
    {
        this.add = add;
    }


    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
