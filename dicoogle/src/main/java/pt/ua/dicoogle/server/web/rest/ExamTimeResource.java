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
package pt.ua.dicoogle.server.web.rest;

import java.io.File;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import pt.ua.dicoogle.server.web.rest.elements.ExamTimeCore;

/**
 *
 * @author samuelcampos
 */
public class ExamTimeResource extends ServerResource {

    @Get
    public Representation represent() {
        String action = getRequest().getResourceRef().getQueryAsForm().getValues("action");

        if (action == null) {
            return new StringRepresentation("", MediaType.TEXT_ALL);
        }

        ExamTimeCore examTime = ExamTimeCore.getInstance();


        if (action.equalsIgnoreCase("getState")) {
            ExamTimeCore.ExamTimeState state = examTime.getState();

            return new StringRepresentation(state.name(), MediaType.TEXT_ALL);
        }
        
        if (action.equalsIgnoreCase("percentage")) {
            return new StringRepresentation(examTime.getPercentage() + "", MediaType.TEXT_ALL);
        }

        if (action.equalsIgnoreCase("startCalc")) {
            examTime.startThread();

            return new StringRepresentation("OK", MediaType.TEXT_ALL);
        }

        if (action.equalsIgnoreCase("stopCalc")) {
            examTime.stopThread();

            return new StringRepresentation("OK", MediaType.TEXT_ALL);
        }

        if (action.equalsIgnoreCase("download")) {
            File file = examTime.getFile();
            
            if(file == null){
                return new StringRepresentation("ERROR", MediaType.TEXT_ALL);
            }
            
            FileRepresentation FRepre = new FileRepresentation(file, MediaType.TEXT_CSV);
            FRepre.getDisposition().setType(Disposition.TYPE_ATTACHMENT);
            FRepre.getDisposition().setFilename(file.getName());

            return FRepre;
        }


        return new StringRepresentation("UNKNOW_ACTION", MediaType.TEXT_ALL);
    }
}
