/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.dicoogle.server.web.servlets.management;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import pt.ua.dicoogle.core.ServerSettings;

/**
 *
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class SettingsServlet extends HttpServlet {

    public enum SettingsType {

        path, zip, effort;
    }
    private SettingsType type;

    public SettingsServlet(SettingsType type) {
        this.type = type;
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String param = req.getParameter(type.toString());
        if (StringUtils.isEmpty(param)) {
            resp.sendError(400, "Invalid path");
        }

        switch (type) {
            case path:
                ServerSettings.getInstance().setDicoogleDir(param);
                resp.getWriter().append("Dir set to: " + param);
                break;
            case zip:
                boolean gzip = Boolean.parseBoolean(param);
                ServerSettings.getInstance().setGzipStorage(gzip);
                resp.getWriter().append("Storage/Index Files compressed  set to: " + param);
                break;
            case effort:
                //TODO: CHECK ACCPTABLE RANGE
                int effort = Integer.parseInt(param);
                ServerSettings.getInstance().setIndexerEffort(effort);
                resp.getWriter().append("index effort set to: " + effort);
                break;
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String result = "";
        switch (type) {
            case path:
                result = ServerSettings.getInstance().getDicoogleDir();
                break;
            case zip:
                result = String.valueOf(ServerSettings.getInstance().isGzipStorage());
                break;
            case effort:
                result = String.valueOf(ServerSettings.getInstance().getIndexerEffort());
                break;
        }

        resp.getWriter().append(result);
    }

}
