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

import org.junit.*;
import pt.ua.dicoogle.server.users.HashService;
import pt.ua.dicoogle.server.users.User;
import pt.ua.dicoogle.server.users.UsersStruct;
import pt.ua.dicoogle.server.users.UsersXML;
import pt.ua.dicoogle.server.web.auth.Authentication;
import pt.ua.dicoogle.server.web.auth.LoggedIn;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pt.ua.dicoogle.server.web.servlets.webui.WebUIServlet.camelize;

/**
 * Created by bastiao on 23/01/16.
 */
public class TestUsers {
    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void authentication() {
        String passwd = "123";
        User u = User.create("nat", false, passwd);
        assertTrue(u.verifyPassword(passwd));
        
        passwd = "correctHorseBatteryStaple";
        assertTrue(u.changePassword("123", passwd));
        assertTrue(u.verifyPassword(passwd));

        passwd = "VeryStrongPassword!!11";
        assertTrue(u.resetPassword(passwd));
        assertTrue(u.verifyPassword(passwd));
    }

}
