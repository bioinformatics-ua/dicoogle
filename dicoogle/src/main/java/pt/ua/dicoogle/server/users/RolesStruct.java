package pt.ua.dicoogle.server.users;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bastiao on 23/01/16.
 */
public class RolesStruct implements RoleManager{

    private static RolesStruct instance = null ;

    private List<Role> roles = new ArrayList<>();

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
        roles = new ArrayList<>();
    }

    @Override
    public boolean hasRole(User user, Role r) {
        return UsersStruct.getInstance().getUser(user.getUsername()).hasRole(r);
    }

    @Override
    public List<Role> getRoles() {
        return this.roles;
    }

    @Override
    public void addRole(Role r) {
        this.roles.add(r);
    }
}
