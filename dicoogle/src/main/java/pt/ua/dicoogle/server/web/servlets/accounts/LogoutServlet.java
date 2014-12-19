/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.dicoogle.server.web.servlets.accounts;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import pt.ua.dicoogle.server.web.auth.Session;
import pt.ua.dicoogle.server.web.utils.ResponseUtil;

/**
 *
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class LogoutServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       
        boolean logout = Session.logout(req);
        
        ResponseUtil.simpleResponse(resp, logout);
              
        
        
        
    }
    
}
