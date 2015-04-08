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
package pt.ua.dicoogle.server.users;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;


/**
 * This class stores the information about one active user
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class UserON implements Serializable {
    private int userID;
    private String username;
    private String host;

    private Date loginTime;

    //private User userfer;

    public UserON(int userID, String username, String host){
        this.userID = userID;
        this.username = username;
        this.host = host;

        loginTime = new Date();
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the userID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    public Date getLoginTime(){
        return loginTime;
    }


    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }

        if (other == this) {
            return true;
        }

        UserON tmp = (UserON) other;

        if (userID == tmp.userID && username.equals(tmp.username) && host.equals(tmp.host)) {
            return true;
        }

        return false;
    }

    @Override
    public String toString(){
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal.setTime(loginTime);
        cal2.setTime(new Date());

        long milliseconds1 = cal.getTimeInMillis();
        long milliseconds2 = cal2.getTimeInMillis();

        long diff = milliseconds2 - milliseconds1;

        long diffHours = diff / (60 * 60 * 1000);
        diff = diff % (60 * 60 * 1000);
        long diffMinutes = diff / (60 * 1000);
        diff = diff % (60 * 1000);
        long diffSeconds = diff / 1000;

        return host + " - " + username + ", Login Time: " + diffHours + ":" + diffMinutes + ":" + diffSeconds ;
    }
    
}
