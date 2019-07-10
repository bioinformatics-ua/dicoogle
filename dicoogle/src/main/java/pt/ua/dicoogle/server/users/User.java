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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class that saves information about one user
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 * @author Luís Bastião Silva <bastiao@bmd-software.com>
 */
public class User implements UserRoleManager{

    private final String username;
    private String hash;        //stores the Hash of this user (username + admin + passwordHash)
    private final boolean admin;

    private List<Role> roles = new ArrayList<>();

    public User(String username, String Hash, boolean admin){
        this.username = username;
        this.admin = admin;
        this.hash = Hash;
    }

    public String getUsername(){
        return username;
    }

    public boolean isAdmin(){
        return admin;
    }

    public boolean verifyPassword(String passwordHash){
        String tempHash = HashService.getSHA256Hash(username + admin + passwordHash);

        return this.hash.equals(tempHash);
    }

    public void addRole(Role r)
    {
        this.roles.add(r);
    }

    public boolean hasRole(Role r)
    {
        return this.roles.contains(r);
    }

    public boolean changePassword(String oldPassHash, String newPassHash){
        String tempHash = HashService.getSHA256Hash(username + admin + oldPassHash);

        if(!hash.equals(tempHash))
            return false;


        tempHash = HashService.getSHA256Hash(username + admin + newPassHash);

        hash = tempHash;
        return true;
    }

    protected String getPasswordHash(){
        return hash;
    }

    public boolean resetPassword(String newPassHash){
        if(newPassHash == null || newPassHash.equals(""))
            return false;

        String tempHash = HashService.getSHA256Hash(username + admin + newPassHash);

        hash = tempHash;

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

        if (username.equals(tmp.username) && hash.equals(tmp.hash)
                && admin == tmp.admin) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.username);
        hash = 67 * hash + Objects.hashCode(this.hash);
        hash = 67 * hash + (this.admin ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "User{" + username + (admin ? ", admin" : "") + '}';
    }

    public List<Role> getRoles() {
        return roles;
    }
}
