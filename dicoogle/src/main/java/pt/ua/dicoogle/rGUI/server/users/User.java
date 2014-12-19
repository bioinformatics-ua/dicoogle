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

package pt.ua.dicoogle.rGUI.server.users;

/**
 * Class that saves information about one user
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class User {
    private String username;
    private String Hash;        //stores the Hash of this user (username + admin + passwordHash)
    private boolean admin;

    public User(String username, String Hash, boolean admin){
        this.username = username;
        this.admin = admin;
        this.Hash = Hash;
    }

    public String getUsername(){
        return username;
    }

    public boolean isAdmin(){
        return admin;
    }

    public boolean verifyPassword(String passwordHash){
        String tempHash = HashService.getSHA1Hash(username + admin + passwordHash);

        return this.Hash.equals(tempHash);
    }

    public boolean changePassword(String oldPassHash, String newPassHash){
        String tempHash = HashService.getSHA1Hash(username + admin + oldPassHash);

        if(!Hash.equals(tempHash))
            return false;


        tempHash = HashService.getSHA1Hash(username + admin + newPassHash);

        Hash = tempHash;
        return true;
    }

    protected String getPasswordHash(){
        return Hash;
    }

    public boolean resetPassword(String newPassHash){
        if(newPassHash == null || newPassHash.equals(""))
            return false;

        String tempHash = HashService.getSHA1Hash(username + admin + newPassHash);

        Hash = tempHash;

        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }

        if (other == this) {
            return true;
        }

        User tmp = (User) other;

        if (username.equals(tmp.username) && Hash.equals(tmp.Hash)
                && admin == tmp.admin) {
            return true;
        }

        return false;
    }
}
