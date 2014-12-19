/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.dicoogle.server.web.utils;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

/**
 * Common simple responses of success and failure
 * 
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class ResponseUtil {

    public static void simpleResponse(HttpServletResponse resp, boolean success) throws IOException {
        JSONObject object = new JSONObject();
        object.put("success", success);

        object.write(resp.getWriter());
    }
}
