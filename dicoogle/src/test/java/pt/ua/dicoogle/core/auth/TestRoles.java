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
package pt.ua.dicoogle.core.auth;

import org.junit.Ignore;
import org.junit.Test;
import pt.ua.dicoogle.server.users.*;

import java.util.Collection;

/**
 * Created by bastiao on 23/01/16.
 */
public class TestRoles {

    @Test
    @Ignore // needs isolation
    public void testRoles() {

        UsersStruct users = UsersStruct.getInstance();
        RolesXML rolesXML = new RolesXML();
        RolesStruct rolesStruct = rolesXML.getXML();
        System.out.println(rolesStruct.getRoles());
    }

    @Test
    @Ignore // needs isolation
    public void testUserRoles() {

        RolesXML rolesXML = new RolesXML();
        RolesStruct rolesStruct = rolesXML.getXML();

        UsersStruct usersStruct = UsersStruct.getInstance();
        Collection<User> users = usersStruct.getUsers();

        System.out.println(rolesStruct.getRoles());
        for (User u : users) {
            System.out.println(u.getUsername());
            for (Role r : u.getRoles()) {
                System.out.println(r);
            }
        }



    }
}
