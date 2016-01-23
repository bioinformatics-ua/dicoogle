package pt.ua.dicoogle.server.users;

import java.util.List;

/**
 * Created by bastiao on 23/01/16.
 */
public interface RoleManager {

    public boolean hasRole(User user, Role r);
    public List<Role> getRoles();
    public void addRole(Role r);
}
