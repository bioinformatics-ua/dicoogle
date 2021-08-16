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

import pt.ua.dicoogle.server.web.utils.ResponseUtil;
import pt.ua.dicoogle.taskManager.RunningIndexTasks;

/**
*
* @author Frederico Silva<fredericosilva@ua.pt>
*/
public class RunningTasksServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.getWriter().write(RunningIndexTasks.getInstance().toJson());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action != null && !action.equals("delete")) {
            resp.sendError(400, "action param needed: only delete is supported");
        }

        String type = req.getParameter("type");
        String taskUid = req.getParameter("uid");
        if (type == null) {
            resp.sendError(400, "type param not existent");
        }
        if (type.equals("close"))
            ResponseUtil.simpleResponse(resp, "removed", RunningIndexTasks.getInstance().removeTask(taskUid));
        else if (type.equals("stop"))
            ResponseUtil.simpleResponse(resp, "stopped", RunningIndexTasks.getInstance().stopTask(taskUid));
        else
            ResponseUtil.simpleResponse(resp, "error", true);
    }


}
