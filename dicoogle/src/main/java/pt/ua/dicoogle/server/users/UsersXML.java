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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This class saves the list of users to XML
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class UsersXML extends DefaultHandler
{
    private UsersStruct users = UsersStruct.getInstance();
    private boolean isUsers = false ;

    private String username;
    private String Hash;
    private boolean admin;
    private String roles;


    public UsersXML()
    {
        username = "";
        Hash = "";
        roles = "";
        admin = false;
    }


    @Override
    public void startElement( String uri, String localName, String qName,
            Attributes attribs )
    {



        if (localName.equals("Users"))
        {
            isUsers = true ;
        }
        else if (this.isUsers && localName.equals("user"))
        {
            this.username = this.resolveAttrib("username", attribs, "xp");
            this.Hash = this.resolveAttrib("hash", attribs, "xp");

            String temp = this.resolveAttrib("admin", attribs, "xp");
            if(temp.equals("true"))
                this.admin = true;
            else
                this.admin = false;
            this.roles = this.resolveAttrib("roles", attribs, "xp");

        }
        
    }


    @Override
    public void endElement( String uri, String localName, String qName )
    {

        if (localName.equals("Users"))
        {
            isUsers = false ;
        }
        else if( localName.equals( "user" ) )
        {

            User u = new User(username, Hash, admin);
            users.addUser(u);
            if (roles!=null)
            {
                String [] rolesTmp = roles.split(",");
                for (int i = 0; i<rolesTmp.length; i++)
                {
                    Role role = RolesStruct.getInstance().getRole(rolesTmp[i]);
                    u.addRole(role);
                }

            }
        }
        
    }

     private String resolveAttrib( String attr, Attributes attribs, String defaultValue) {
         String tmp = attribs.getValue(attr);
         return (tmp!=null)?(tmp):(defaultValue);
     }


    public UsersStruct getXML()
    {
        users.reset();
        
        try
        {
            UserFileHandle file = new UserFileHandle();
            byte[] xml = file.getFileContent();
            
            if (xml == null)
            {
                //DebugManager.getSettings().debug("Setting users default, writing a file with the default information!");
                users.setDefaults();
                printXML();
                return users;
            }


            // Never throws the exception cause file not exists so need try catch
            InputSource src = new InputSource( new ByteArrayInputStream(xml) );
            XMLReader r = XMLReaderFactory.createXMLReader();
            r.setContentHandler(this);
            r.parse(src);
            return users;
        }
        catch (SAXException | IOException ex)
        {
            LoggerFactory.getLogger(UsersXML.class).error(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Print the users information to the XML file
     */
    public void printXML()
    {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
       

        PrintWriter pw = new PrintWriter(out);
        StreamResult streamResult = new StreamResult(pw);
        SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();
        //      SAX2.0 ContentHandler.
        TransformerHandler hd = null;
        try
        {
            hd = tf.newTransformerHandler();
        } catch (TransformerConfigurationException ex)
        {
            LoggerFactory.getLogger(UsersXML.class).error(ex.getMessage(), ex);
        }
        
        Transformer serializer = hd.getTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        serializer.setOutputProperty(OutputKeys.METHOD, "xml");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.STANDALONE, "yes");   
        try
        {
            hd.setResult(streamResult);
            hd.startDocument();
        } catch (SAXException ex)
        {
            LoggerFactory.getLogger(UsersXML.class).error(ex.getMessage(), ex);
        }

        AttributesImpl atts = new AttributesImpl();
        try
        {
            //root element
            hd.startElement("", "", "Users", atts);

            Iterator<User> us = UsersStruct.getInstance().getUsers().iterator();

            atts.clear();
            while (us.hasNext())
            {
                User user = us.next();
                
                atts.addAttribute("", "", "username", "", user.getUsername());
                atts.addAttribute("", "", "hash", "", user.getPasswordHash()) ;

                String temp = "false";
                if(user.isAdmin())
                    temp = "true";
                if (user.getRoles()!=null&&user.getRoles().size()>0)
                {
                    String roles = "";
                    for (Role r : user.getRoles())
                    {
                        roles+=r.getName()+",";
                    }
                    StringUtils.removeEnd(roles, ",");


                    atts.addAttribute("", "", "roles", "", roles ) ;
                }

                atts.addAttribute("", "", "admin", "", temp) ;

                hd.startElement("", "", "user", atts);
                atts.clear();
                hd.endElement("", "", "user");
            }
            hd.endElement("", "", "Users");


            hd.endDocument();
        } catch (SAXException ex)
        {
            LoggerFactory.getLogger(UsersXML.class).error(ex.getMessage(), ex);
        }
        finally {
            try {
                out.close();
                
                UserFileHandle file = new UserFileHandle();
                file.printFile(out.toByteArray());
            } catch (Exception ex) {
                  LoggerFactory.getLogger(UsersXML.class).error(ex.getMessage(), ex);
            }
        }


    }
}
