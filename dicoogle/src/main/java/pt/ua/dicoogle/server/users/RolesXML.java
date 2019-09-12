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
package pt.ua.dicoogle.server.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

/**
 * This class saves the list of roles to XML
 *
 * @author Luís Bastião Silva <bastiao@bmd-software.com>
 */
public class RolesXML extends DefaultHandler
{
    private static Logger logger = LoggerFactory.getLogger(RolesXML.class);
    private RolesStruct roles = RolesStruct.getInstance();
    private boolean isRoles = false ;

    private String rolename;



    public RolesXML()
    {
        rolename = "";
    }


    @Override
    public void startElement( String uri, String localName, String qName,
            Attributes attribs )
    {



        if (localName.equals("Roles"))
        {
            isRoles = true ;
        }
        else if (this.isRoles && localName.equals("role"))
        {
            this.rolename = this.resolveAttrib("name", attribs, "xp");
        }
        
    }


    @Override
    public void endElement( String uri, String localName, String qName )
    {

        if (localName.equals("Users"))
        {
            isRoles = false ;
        }
        else if( localName.equals( "role" ) )
        {
            roles.addRole(new Role(rolename));
        }
        
    }

     private String resolveAttrib( String attr, Attributes attribs, String defaultValue) {
         String tmp = attribs.getValue(attr);
         return (tmp!=null)?(tmp):(defaultValue);
     }


    public RolesStruct getXML()
    {
        roles.reset();        

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Files.copy(Paths.get("roles.xml"), out);
            byte[] xml = out.toByteArray();
            
            // Never throws the exception cause file not exists so need try catch
            InputSource src = new InputSource( new ByteArrayInputStream(xml) );
            XMLReader r = XMLReaderFactory.createXMLReader();
            r.setContentHandler(this);
            r.parse(src);
            return roles;
        }
        catch (NoSuchFileException e) {
            logger.info("User roles file not found, a new empty list will be created.");
            printXML();
            return roles;
        }
        catch (SAXException | IOException ex)
        {
            logger.error("Failed to read user roles file", ex);
            return null;
        }
    }

    /**
     * Print the roles information to the XML file
     */
    public void printXML()
    {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(out))
        {
            StreamResult streamResult = new StreamResult(pw);

            SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();
            TransformerHandler hd = null;
            hd = tf.newTransformerHandler();
    
            Transformer serializer = hd.getTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            serializer.setOutputProperty(OutputKeys.METHOD, "xml");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.STANDALONE, "yes");   

            hd.setResult(streamResult);
            hd.startDocument();

            AttributesImpl atts = new AttributesImpl();

            //root element
            hd.startElement("", "", "Roles", atts);

            Iterator<Role> us = RolesStruct.getInstance().getRoles().iterator();

            atts.clear();
            while (us.hasNext())
            {
                Role role = us.next();
                
                atts.addAttribute("", "", "name", "", role.getName());

                hd.startElement("", "", "role", atts);
                atts.clear();
                hd.endElement("", "", "role");
            }

            hd.endElement("", "", "Roles");
            hd.endDocument();
        } catch (TransformerConfigurationException|SAXException ex)
        {
            logger.error("Failed to build user roles file", ex);
        }
        
        try {
            printFile(out.toByteArray());
        } catch (IOException ex) {
            logger.error("Failed to write user roles file", ex);

        }
    }
    /**
     * Print one byte array in File
     * @param bytes
     */
    private void printFile(byte[] bytes) throws IOException {
        try (InputStream in = new ByteArrayInputStream(bytes)) {
            Files.copy(in, Paths.get("roles.xml"), StandardCopyOption.REPLACE_EXISTING);
        }
    }

}
