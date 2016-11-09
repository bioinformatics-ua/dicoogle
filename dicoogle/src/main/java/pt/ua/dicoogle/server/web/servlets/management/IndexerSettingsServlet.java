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

import pt.ua.dicoogle.core.settings.ServerSettingsManager;

/**
 *
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class IndexerSettingsServlet extends HttpServlet {

    public enum SettingsType {

        all,path, zip, effort, thumbnail,thumbnailSize, watcher;
    }
    private final SettingsType type;

    public IndexerSettingsServlet(SettingsType type) {
        this.type = type;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	
    	String param = null;
    	String path = null;
    	boolean watcher=false, saveT = false;
    	int ieffort = 0;
    	int thumbnailSize = -1;
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
    		saveT = Boolean.parseBoolean(req.getParameter("saveThumbnail"));
    		ieffort = Integer.parseInt(req.getParameter("effort"));
    		thumbnailSize = Integer.parseInt(req.getParameter("thumbnailSize"));
    	}

        switch (type) {
            case path:
                ServerSettingsManager.getSettings().getArchiveSettings().setWatchDirectory(param);
                resp.getWriter().append("Dir set to: " + param);
                break;
            case zip:
                resp.getWriter().append("indexZipFiles is no longer supported");
                break;
            case effort:
                //TODO: CHECK ACCPTABLE RANGE
                int effort = Integer.parseInt(param);
                ServerSettingsManager.getSettings().getArchiveSettings().setIndexerEffort(effort);
                resp.getWriter().append("index effort set to: " + effort);
                break;
            case thumbnail:
            	boolean saveThumbanail = Boolean.parseBoolean(param);
            	ServerSettingsManager.getSettings().getArchiveSettings().setSaveThumbnails(saveThumbanail);
            	break;
            case thumbnailSize:
            	//TODO: Should be a int
            	//int thumbSize = Integer.parseInt(param);
            	ServerSettingsManager.getSettings().getArchiveSettings().setThumbnailSize(Integer.parseInt(param));
            	break;
            case watcher:
            	ServerSettingsManager.getSettings().getArchiveSettings().setDirectoryWatcherEnabled(Boolean.parseBoolean(param));
            	break;
            case all:
            	 ServerSettingsManager.getSettings().getArchiveSettings().setWatchDirectory(path);
                 if (ieffort > 0 && ieffort <= 100) {
                     ServerSettingsManager.getSettings().getArchiveSettings().setIndexerEffort(ieffort);
                 }
            	 ServerSettingsManager.getSettings().getArchiveSettings().setSaveThumbnails(saveT);
                 if (thumbnailSize > 0) {
                     ServerSettingsManager.getSettings().getArchiveSettings().setThumbnailSize(thumbnailSize);
                 }
            	 ServerSettingsManager.getSettings().getArchiveSettings().setDirectoryWatcherEnabled(watcher);
            	break;
            	
        }
        ServerSettingsManager.saveSettings();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String result = "";
        resp.setContentType("application/json");
        switch (type) {
            case path:
                result = ServerSettingsManager.getSettings().getArchiveSettings().getMainDirectory();
                break;
            case zip:
                result = "false"; // String.valueOf(ServerSettingsManager.getSettings().isGzipStorage());
                break;
            case effort:
                result = String.valueOf(ServerSettingsManager.getSettings().getArchiveSettings().getIndexerEffort());
                break;
            case thumbnail:
            	result = String.valueOf(ServerSettingsManager.getSettings().getArchiveSettings().getSaveThumbnails());
            	break;
            case thumbnailSize:
            	result = String.valueOf(ServerSettingsManager.getSettings().getArchiveSettings().getThumbnailSize());
            	break;
            case watcher:
            	result = String.valueOf(ServerSettingsManager.getSettings().getArchiveSettings().isDirectoryWatcherEnabled());
            	break;
            case all:
            	JSONObject allresponse = new JSONObject();
            	allresponse.put("path", ServerSettingsManager.getSettings().getArchiveSettings().getMainDirectory());
            	allresponse.put("zip", "false"); // ServerSettingsManager.getSettings().isGzipStorage());
            	allresponse.put("effort", String.valueOf(ServerSettingsManager.getSettings().getArchiveSettings().getIndexerEffort()));
            	allresponse.put("thumbnail", ServerSettingsManager.getSettings().getArchiveSettings().getSaveThumbnails());
            	allresponse.put("thumbnailSize", String.valueOf(ServerSettingsManager.getSettings().getArchiveSettings().getThumbnailSize()));
            	allresponse.put("watcher", ServerSettingsManager.getSettings().getArchiveSettings().isDirectoryWatcherEnabled());
            	
            	resp.getWriter().write(allresponse.toString());
            	break;
        }

        resp.setContentType("application/json");
        resp.getWriter().append(result);
    }

}
