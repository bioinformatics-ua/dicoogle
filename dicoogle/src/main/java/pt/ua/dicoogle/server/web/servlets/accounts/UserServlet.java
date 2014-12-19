/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.dicoogle.server.web.servlets.accounts;

import java.io.IOException;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pt.ua.dicoogle.rGUI.server.users.HashService;
import pt.ua.dicoogle.rGUI.server.users.User;
import pt.ua.dicoogle.rGUI.server.users.UsersStruct;
import pt.ua.dicoogle.server.web.management.Dicoogle;
import pt.ua.dicoogle.server.web.utils.ResponseUtil;

/**
 * User Servlet for create, remove and consult user accounts
 *
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class UserServlet extends HttpServlet {

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String usernameToRemove = req.getParameter("username");

        boolean isRemoved = false;
        if (usernameToRemove != null && !usernameToRemove.equals("")) {
            isRemoved = UsersStruct.getInstance().removeUser(usernameToRemove);
        }

        ResponseUtil.simpleResponse(resp, isRemoved);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user = req.getParameter("username");
        String pass = req.getParameter("password");
        boolean admin = Boolean.parseBoolean(req.getParameter("admin"));
        //System.out.println("ADD USER: " + user + "\npass: " + pass + "\nadmin: " + admin);

        String passHash = HashService.getSHA1Hash(pass);             //password Hash
        String Hash = HashService.getSHA1Hash(user + admin + passHash);   //user Hash

        boolean wasAdded = UsersStruct.getInstance().addUser(new User(user, Hash, admin));
        ResponseUtil.simpleResponse(resp, wasAdded );
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Set<String> users = UsersStruct.getInstance().getUsernames();

        JSONObject jsonObject = new JSONObject();
        JSONArray usersArray = new JSONArray();
        for (String user : users) {
            JSONObject u = new JSONObject();
            u.put("username", user);
            usersArray.add(u);
        }

        jsonObject.put("users", usersArray);

        jsonObject.write(resp.getWriter());

    }

}
