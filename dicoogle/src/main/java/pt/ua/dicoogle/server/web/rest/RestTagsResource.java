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

import java.util.ArrayList;
import java.util.HashMap;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import pt.ua.dicoogle.sdk.utils.DictionaryAccess;

/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class RestTagsResource extends ServerResource {

    @Get
    public Representation representXML() {
        StringRepresentation sr;
        
        HashMap<String, Integer> tagList = DictionaryAccess.getInstance().getTagList();
        
        ArrayList<String> list = new ArrayList<String>();
        Element tags = new Element("tags");
        for(String tag : tagList.keySet()){
               int value = tagList.get(tag);
               
               Element e = new Element("tag");
               e.setAttribute( "id", Integer.toString(value));
               e.setAttribute("name", tag);
               tags.addContent(e);
        } 
        
        tags.setAttribute("count", Integer.toString(tags.getChildren().size()));
        Document xmlDoc = new Document(tags);
        
        XMLOutputter out = new XMLOutputter(Format.getCompactFormat());
        String str = out.outputString(xmlDoc);
        
        StringRepresentation rep = new StringRepresentation( str, MediaType.APPLICATION_XML);
       
        return rep;
    }
}
