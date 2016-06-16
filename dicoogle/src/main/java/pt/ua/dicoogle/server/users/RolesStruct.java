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

import java.util.*;

/**
 * Created by bastiao on 23/01/16.
 */
public class RolesStruct implements RoleManager{

    private static RolesStruct instance = null;

    private Set<String> roles = new HashSet<>();

    public static synchronized RolesStruct getInstance() {
        if (instance == null) {
            instance = new RolesStruct();
        }

        return instance;
    }
    private RolesStruct(){
        reset();
    }

    public void reset(){
        roles = new HashSet<>();
    }

    @Override
    public boolean hasRole(User user, String rolename) {
        if (user==null || rolename==null)
            return false;
        return UsersStruct.getInstance().getUser(user.getUsername()).hasRole(rolename);
    }

    @Override
    public Set<String> getRoles() {
        return this.roles;
    }

    @Override
    public void addRole(String rolename) {
        this.roles.add(rolename);
    }

    @Override
    public boolean isAvailable(String name) {
        return this.roles.contains(name);
    }
}
