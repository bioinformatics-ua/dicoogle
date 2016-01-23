package pt.ua.dicoogle.core.auth;

import org.junit.Test;
import pt.ua.dicoogle.server.users.*;
import pt.ua.dicoogle.server.web.auth.Authentication;
import pt.ua.dicoogle.server.web.auth.LoggedIn;

/**
 * Created by bastiao on 23/01/16.
 */
public class TestRoles {


    @Test
    public void testRoles() {

        UsersStruct users = UsersStruct.getInstance();
        UsersXML usersXML = new UsersXML();
        users = usersXML.getXML();
        RolesXML rolesXML = new RolesXML();
        RolesStruct rolesStruct = rolesXML.getXML();
        System.out.println(rolesStruct.getRoles());
    }

    @Test
    public void testUserRoles() {

        RolesXML rolesXML = new RolesXML();
        RolesStruct rolesStruct = rolesXML.getXML();

        UsersStruct users = UsersStruct.getInstance();
        UsersXML usersXML = new UsersXML();
        users = usersXML.getXML();
        System.out.println(rolesStruct.getRoles());
        for (User u : users.getUsers())
        {
            System.out.println(u.getUsername());
            for (Role  r : u.getRoles())
            {
                System.out.println(r);
            }
        }



    }
}
