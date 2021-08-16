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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * This class saves the list of users to XML
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class UsersXML extends DefaultHandler {
    private static final Logger logger = LoggerFactory.getLogger(UsersXML.class);
    Collection<User> users;
    private boolean isUsers = false;

    private String username;
    private String Hash;
    private boolean admin;
    private String roles;


    public UsersXML() {
        username = "";
        Hash = "";
        roles = "";
        admin = false;

        users = new LinkedList<>();
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attribs) {
        if (localName.equals("Users")) {
            isUsers = true;
        } else if (this.isUsers && localName.equals("user")) {
            this.username = this.resolveAttrib("username", attribs, "xp");
            this.Hash = this.resolveAttrib("hash", attribs, "xp");

            String temp = this.resolveAttrib("admin", attribs, "xp");
            if (temp.equals("true"))
                this.admin = true;
            else
                this.admin = false;
            this.roles = this.resolveAttrib("roles", attribs, "xp");
        }
    }


    @Override
    public void endElement(String uri, String localName, String qName) {
        if (localName.equals("Users")) {
            isUsers = false;
        } else if (localName.equals("user")) {
            User u = new User(username, Hash, admin);
            users.add(u);
            if (roles != null) {
                String[] rolesTmp = roles.split(",");
                for (int i = 0; i < rolesTmp.length; i++) {
                    Role role = RolesStruct.getInstance().getRole(rolesTmp[i]);
                    if (role != null)
                        u.addRole(role);
                }
            }
        }
    }

    private String resolveAttrib(String attr, Attributes attribs, String defaultValue) {
        String tmp = attribs.getValue(attr);
        return (tmp != null) ? (tmp) : (defaultValue);
    }


    public Collection<User> getXML() {
        try {
            UserFileHandle file = new UserFileHandle();
            byte[] xml = file.getFileContent();

            if (xml == null) {
                users = new LinkedList<>(UsersStruct.getDefaults());
                printXML(users);

                return users;
            }

            // Never throws the exception cause file not exists so need try catch
            InputSource src = new InputSource(new ByteArrayInputStream(xml));
            XMLReader r = XMLReaderFactory.createXMLReader();
            r.setContentHandler(this);
            r.parse(src);
            return users;
        } catch (SAXException | IOException ex) {
            logger.error("Error reading users XML configuration file.", ex);
        }
        return null;
    }

    /**
     * Print the users information to the XML file
     */
    public void printXML(Collection<User> users) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();
        // SAX2.0 ContentHandler.
        TransformerHandler hd = null;
        try {
            hd = tf.newTransformerHandler();
        } catch (TransformerConfigurationException ex) {
            logger.error("Failed creating configuration object", ex);
        }

        Transformer serializer = hd.getTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        serializer.setOutputProperty(OutputKeys.METHOD, "xml");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.STANDALONE, "yes");

        try (PrintWriter pw = new PrintWriter(out)) {
            StreamResult streamResult = new StreamResult(pw);
            hd.setResult(streamResult);
            hd.startDocument();

            AttributesImpl atts = new AttributesImpl();
            // root element
            hd.startElement("", "", "Users", atts);

            Iterator<User> us = users.iterator();

            atts.clear();
            while (us.hasNext()) {
                User user = us.next();

                atts.addAttribute("", "", "username", "", user.getUsername());
                atts.addAttribute("", "", "hash", "", user.getPasswordHash());

                String temp = "false";
                if (user.isAdmin())
                    temp = "true";
                if (user.getRoles() != null && user.getRoles().size() > 0) {
                    String roles = user.getRoles().stream().map(Role::getName).collect(Collectors.joining(","));

                    atts.addAttribute("", "", "roles", "", roles);
                }

                atts.addAttribute("", "", "admin", "", temp);

                hd.startElement("", "", "user", atts);
                atts.clear();
                hd.endElement("", "", "user");
            }
            hd.endElement("", "", "Users");

            hd.endDocument();
        } catch (SAXException ex) {
            logger.error("Failed to parse XML file", ex);
        } finally {
            try {
                UserFileHandle file = new UserFileHandle();
                file.printFile(out.toByteArray());
            } catch (IOException e) {
                logger.error("Failed to write user roles file", e);
            }
        }
    }
}
