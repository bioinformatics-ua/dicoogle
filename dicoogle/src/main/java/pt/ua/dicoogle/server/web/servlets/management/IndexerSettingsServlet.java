/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle.
 *
 * Dicoogle/dicoogle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */

package pt.ua.dicoogle.server.web.servlets.management;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.core.XMLSupport;

/**
 *
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class IndexerSettingsServlet extends HttpServlet {

    public enum SettingsType {

        all, path, zip, effort, thumbnail, thumbnailSize, watcher;
    }
    private final SettingsType type;

    public IndexerSettingsServlet(SettingsType type) {
        this.type = type;
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    	String param = null;
    	String path = null;
    	boolean watcher=false, zip = false, saveT = false;
    	int ieffort = 0;
    	String tumbnailSize = null;
    	if(type != SettingsType.all)
    	{
    		param = req.getParameter(type.toString());

    		if (StringUtils.isEmpty(param)) {
                resp.sendError(400, "Invalid " + type.toString());
            }
    	}
    	else{
    		path = req.getParameter("path");
    		watcher = Boolean.parseBoolean(req.getParameter("watcher"));
    		zip = Boolean.parseBoolean(req.getParameter("zip"));
    		saveT = Boolean.parseBoolean(req.getParameter("saveThumbnail"));
    		ieffort = Integer.parseInt(req.getParameter("effort"));
    		tumbnailSize = req.getParameter("thumbnailSize");
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
            case thumbnail:
            	boolean saveThumbanail = Boolean.parseBoolean(param);
            	ServerSettings.getInstance().setSaveThumbnails(saveThumbanail);
            	break;
            case thumbnailSize:
            	//TODO: Should be a int
            	//int thumbSize = Integer.parseInt(param);
            	ServerSettings.getInstance().setThumbnailsMatrix(param);
            	break;
            case watcher:
            	ServerSettings.getInstance().setMonitorWatcher(Boolean.parseBoolean(param));
            	break;
            case all:
            	 ServerSettings.getInstance().setDicoogleDir(path);
            	 ServerSettings.getInstance().setGzipStorage(zip);
            	 ServerSettings.getInstance().setIndexerEffort(ieffort);
            	 ServerSettings.getInstance().setSaveThumbnails(saveT);
            	 ServerSettings.getInstance().setThumbnailsMatrix(tumbnailSize);
            	 ServerSettings.getInstance().setMonitorWatcher(watcher);
            	break;

        }
        new XMLSupport().printXML();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String result = "";
        resp.setContentType("application/json");
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
            case thumbnail:
            	result = String.valueOf(ServerSettings.getInstance().getSaveThumbnails());
            	break;
            case thumbnailSize:
            	result = String.valueOf(ServerSettings.getInstance().getThumbnailsMatrix());
            	break;
            case watcher:
            	result = String.valueOf(ServerSettings.getInstance().isMonitorWatcher());
            	break;
            case all:
            	JSONObject allresponse = new JSONObject();
            	allresponse.put("path", ServerSettings.getInstance().getDicoogleDir());
            	allresponse.put("zip", ServerSettings.getInstance().isGzipStorage());
            	allresponse.put("effort", String.valueOf(ServerSettings.getInstance().getIndexerEffort()));
            	allresponse.put("thumbnail", ServerSettings.getInstance().getSaveThumbnails());
            	allresponse.put("thumbnailSize", String.valueOf(ServerSettings.getInstance().getThumbnailsMatrix()));
            	allresponse.put("watcher", ServerSettings.getInstance().isMonitorWatcher());

            	resp.getWriter().write(allresponse.toString());
            	break;
        }

        resp.setContentType("application/json");
        resp.getWriter().append(result);
    }

}
