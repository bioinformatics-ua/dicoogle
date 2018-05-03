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

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pt.ua.dicoogle.sdk.utils.TagValue;
import pt.ua.dicoogle.sdk.utils.TagsStruct;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class DimTagsServlet extends HttpServlet {
    private final TagsStruct tagsTruct;

    public DimTagsServlet(TagsStruct tagsTruct) {
        this.tagsTruct = tagsTruct;
    }

    /** Reply with value tags and modalities.
     *
     * Query string parameters:
     *
     *  - type [default: "all"] : can be "all", "dim", "private" or "other" ; The type of tag values to include.
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pType = req.getParameter("type");
        String type = pType != null ? pType : "all";

        Collection<TagValue> tags;
        switch (type) {
            case "all":
                tags = this.tagsTruct.getAllFields();
                break;
            case "dim":
                tags = this.tagsTruct.getDIMFields();
                break;
            case "private":
                tags = this.tagsTruct.getPrivateFields();
                break;
            case "other":
                tags = this.tagsTruct.getOtherFields();
                break;
            default:
                resp.setStatus(400);
                JSONObject errorObj = new JSONObject();
                errorObj.element("error", "Invalid tag value type");
                resp.getWriter().write(errorObj.toString());
                return;
        }

        JSONObject o = new JSONObject();
        o.element("tags", serialize(tags));
        o.element("modalities", this.tagsTruct.getModalities());
        resp.getWriter().write(o.toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(501); // TODO
    }

    public static JSON serialize(Collection<TagValue> tags) {
        JSONArray array = new JSONArray();
        for (TagValue t : tags) {
            array.add(serialize(t));
        }
        return array;
    }
    public static JSON serialize(TagValue tagValue) {
        JSONObject obj = new JSONObject();
        obj.element("id", tagValue.getTagID());
        obj.element("alias", tagValue.getAlias());
        if (tagValue.getVR() != null) {
            obj.element("vr", tagValue.getVR());
        }
        return obj;
    }
}
