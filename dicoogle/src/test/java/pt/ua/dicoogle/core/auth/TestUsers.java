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

import static org.junit.Assert.assertEquals;
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

    //@Test
    public void testUsers() {

        UsersStruct users = UsersStruct.getInstance();
        UsersXML usersXML = new UsersXML();
        users = usersXML.getXML();

        String username = "nat";
        boolean admin = false;
        String passPlainText = "123";
        String passHash = HashService.getSHA1Hash(passPlainText);             //password Hash
        String hash = HashService.getSHA1Hash(username + admin + passHash);
        System.out.println(hash);
        System.out.println(passHash);
        User u = new User("nat",hash, admin);
        users.addUser(u);


        for (String uu : users.getUsernames())
        {
            System.out.println(users.getUser(uu));
        }

        Authentication auth = Authentication.getInstance();
        try {
            LoggedIn loggedIn = auth.login("nat", "123");
            System.out.println(loggedIn.getUserName());
            System.out.println(loggedIn.isAdmin());
        }
        catch(Exception e)
        {
            System.out.println("Error in the test");
        }

    }

}
