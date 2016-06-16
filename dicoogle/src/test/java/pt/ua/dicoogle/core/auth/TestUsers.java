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
import pt.ua.dicoogle.server.users.*;
import pt.ua.dicoogle.server.web.auth.Authentication;
import pt.ua.dicoogle.server.web.auth.LoggedIn;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

/**
 * Created by bastiao on 23/01/16.
 */
public class TestUsers {

    private String filename;

    @Before
    public void init() throws IOException, URISyntaxException {
        // copy test users.xml in resources into a temporary file
        URI res = TestUsers.class.getResource("users.xml").toURI();
        Path tmpUsers = Files.createTempFile("dicoogle-test-users", ".xml");
        this.filename = tmpUsers.toString();
        Files.copy(Paths.get(res), tmpUsers, StandardCopyOption.REPLACE_EXISTING);

        // initialize global role registry with 2 possible roles
        RolesStruct.getInstance().addRole("Pens");
        RolesStruct.getInstance().addRole("Healthcare");
    }

    @Test
    public void testUsers() throws IOException {

        UsersXML usersXML = new UsersXML();
        UsersStruct users = usersXML.loadConfiguration(this.filename);

        Assert.assertEquals(2, users.getUsers().size());

        User user = users.getUser("nat");
        Assert.assertNotNull(user);
        Assert.assertEquals("nat", user.getUsername());
        Assert.assertFalse(user.isAdmin());
        Assert.assertTrue(user.verifyPassword(HashService.getSHA1Hash("123")));
        Assert.assertEquals(new HashSet<>(Arrays.asList("Pens", "Healthcare")), user.getRoles());

        User user2 = users.getUser("dicoogle");
        Assert.assertNotNull(user2);
        Assert.assertEquals("dicoogle", user2.getUsername());
        Assert.assertTrue(user2.isAdmin());
        Assert.assertTrue(user2.verifyPassword(HashService.getSHA1Hash("dicoogle")));
        Assert.assertEquals(Collections.singleton("Healthcare"), user2.getRoles());
    }

    public void misc() {

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
