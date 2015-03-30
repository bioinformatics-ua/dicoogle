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
import org.apache.commons.lang3.StringUtils;
import pt.ua.dicoogle.core.ServerSettings;

/**
 *
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class IndexerSettingsServlet extends HttpServlet {

    public enum SettingsType {

        path, zip, effort, thumbnail,thumbnailSize;
    }
    private SettingsType type;

    public IndexerSettingsServlet(SettingsType type) {
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
            case thumbnail:
            	boolean saveThumbanail = Boolean.parseBoolean(param);
            	ServerSettings.getInstance().setSaveThumbnails(saveThumbanail);
            	break;
            case thumbnailSize:
            	//TODO: Should be a int
            	//int thumbSize = Integer.parseInt(param);
            	ServerSettings.getInstance().setThumbnailsMatrix(param);
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
            case thumbnail:
            	result = String.valueOf(ServerSettings.getInstance().getSaveThumbnails());
            	break;
            case thumbnailSize:
            	result = String.valueOf(ServerSettings.getInstance().getThumbnailsMatrix());
            	break;
        }

        resp.getWriter().append(result);
    }

}
