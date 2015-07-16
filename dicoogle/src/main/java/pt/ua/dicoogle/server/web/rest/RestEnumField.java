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

import java.io.StringWriter;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import pt.ua.dicoogle.server.web.rest.elements.JaxbStrList;

/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class RestEnumField extends ServerResource{

    
    
    @Get
    public Representation represent(){
        StringRepresentation sr;
        /*
        IndexEngine core = IndexEngine.getInstance();
        String field = getRequest().getResourceRef().getQueryAsForm().getValues("field");
        String type = getRequest().getResourceRef().getQueryAsForm().getValues("type");

        if(field == null){
            return new StringRepresentation("", MediaType.APPLICATION_XML);
        }
        
        if(type == null){
            type = "text";
        }
        
        Set<String> enumList = null;
        
        if(type.equalsIgnoreCase("text")){
            enumList = core.enumField(field, false);
        }
        else if(type.equalsIgnoreCase("float")){
            enumList = core.enumField(field, true);
        }

        JaxbStrList jaxbList = new JaxbStrList(enumList);

        StringWriter sw = new StringWriter();

        try {
            JAXBContext context = JAXBContext.newInstance(JaxbStrList.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            m.marshal(jaxbList, sw);

            sr = new StringRepresentation(sw.toString(), MediaType.APPLICATION_XML);

        } catch (Exception ex) {
            LoggerFactory.getLogger(RestTagsResource.class).error(ex.getMessage(), ex);
            
            sr = new StringRepresentation("", MediaType.APPLICATION_XML);
        }

        return sr;*/
        throw new UnsupportedOperationException();
    }
}
