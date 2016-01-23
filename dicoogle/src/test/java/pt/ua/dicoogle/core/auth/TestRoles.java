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
}
