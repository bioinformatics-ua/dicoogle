package pt.ua.dicoogle.server.users;

import java.util.*;

/**
 * Created by bastiao on 23/01/16.
 */
public class RolesStruct implements RoleManager{

    private static RolesStruct instance = null ;

    private Set<Role> roles = new HashSet<>();
    private Map<String, Role> rolesMap = new HashMap<String, Role>();

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
    public boolean hasRole(User user, Role r) {
        return UsersStruct.getInstance().getUser(user.getUsername()).hasRole(r);
    }

    @Override
    public Set<Role> getRoles() {
        return this.roles;
    }

    @Override
    public void addRole(Role r) {
        this.roles.add(r);
        this.rolesMap.put(r.getName(), r);

    }

    @Override
    public Role getRole(String name) {

        return this.rolesMap.get(name);
    }
}
