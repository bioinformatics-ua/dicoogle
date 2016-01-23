package pt.ua.dicoogle.server.users;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by bastiao on 23/01/16.
 */
public interface RoleManager {

    public boolean hasRole(User user, Role r);
    public Collection<Role> getRoles();
    public void addRole(Role r);
    public Role getRole(String name);

}
