package pt.ua.dicoogle.server.users;

/**
 * Created by bastiao on 23/01/16.
 */
public interface UserRoleManager {

    public boolean hasRole(Role r);
    public void addRole(Role r);
}
