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

    public User(String username, String hash, boolean admin){
        this.username = username;
        this.admin = admin;
        this.hash = hash;
    }

    public static User create(String username, boolean admin, char[] password) {
        return new User(
            username,
            HashService.hashPassword(password),
            admin);
    }

    public static User create(String username, boolean admin, String password) {
        return User.create(username, admin, password.toCharArray());
    }

    public String getUsername(){
        return username;
    }

    public boolean isAdmin(){
        return admin;
    }

    /** Verify the given password agains the user's  password hash.
     * @param password the password in plain text, cleared automatically
     * @return whether the password is verified successfully
     */
    public boolean verifyPassword(char[] password) {
        return HashService.verifyPassword(this.hash, password);
    }

    /** Verify the given password agains the user's  password hash.
     * @param password the password in plain text
     * @return whether the password is verified successfully
     */
    public boolean verifyPassword(String password) {
        return this.verifyPassword(password.toCharArray());
    }

    public void addRole(Role r)
    {
        this.roles.add(r);
    }

    public boolean hasRole(Role r)
    {
        return this.roles.contains(r);
    }

    /** Verify the current password and assign a new password to this user.
     * @param oldPassword the current password as plain text, automatically
     * cleared
     * @param newPasssword the new password as plain text, automatically
     * cleared; must not be empty
     * @return whether the password was changed successfully
     */
    public boolean changePassword(char[] oldPassword, char[] newPassword) {
        if(!this.verifyPassword(oldPassword))
            return false;

        return this.resetPassword(newPassword);
    }

    protected String getPasswordHash() {
        return hash;
    }

    /** Verify the current password and assign a new password to this user.
     * @param oldPassword the current password as plain text, automatically
     * cleared
     * @param newPasssword the new password as plain text; must not be empty
     * @return whether the password was changed successfully
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        return this.changePassword(oldPassword.toCharArray(), newPassword.toCharArray());
    }

    /** Assign a new password to this user.
     * @param passsword the password as plain text, automatically cleared
     * after the operation; must not be empty
     * @return whether the password was changed successfully
     */
    public boolean resetPassword(char[] password){
        if(password.length == 0)
            return false;

        this.hash = HashService.hashPassword(password);
        return true;
    }

    /** Assign a new password to this user.
     * @param passsword the password as plain text
     * @return whether the password was changed successfully
     */
    public boolean resetPassword(String password) {
        return this.resetPassword(password.toCharArray());
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
