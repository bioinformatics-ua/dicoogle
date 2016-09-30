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
package pt.ua.dicoogle.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

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

import pt.ua.dicoogle.core.settings.ClientSettings;
import pt.ua.dicoogle.sdk.Utils.Platform;

/**
 *
 * @author Samuel da Costa Campos <samuelcampos@ua.pt>
 */
public class XMLClientSupport extends DefaultHandler {

    private String filePath;

    private ClientSettings cs;

    private boolean isViewer = false;
    private boolean isHost = false;
    private boolean isPort = false;
    private boolean isTempDir = false;
    private boolean isUsername = false;
    private boolean isPassword = false;
    private boolean isAutoConnect = false;

    public XMLClientSupport() {
        filePath = Platform.homePath() + "clientConfig.xml";
        
        cs = ClientSettings.getInstance();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attribs) {
        if (localName.equals("ExternalViewer")) {
            isViewer = true;
        }
        else if(localName.equals("DefaultServerHost")){
            isHost = true;
        }
        else if(localName.equals("DefaultServerPort")){
            isPort = true;
        }
        else if(localName.equals("DefaultUsername")){
            isUsername = true;
        }
        else if(localName.equals("DefaultPassword")){
            isPassword = true;
        }
        else if(localName.equals("TempFilesDir")){
            isTempDir = true;
        }
        else if(localName.equals("AutoConnect")){
            isAutoConnect = true;
        }


    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (localName.equals("ExternalViewer")) {
            isViewer = false;
        }
        else if(localName.equals("DefaultServerHost")){
            isHost = false;
        }
        else if(localName.equals("DefaultServerPort")){
            isPort = false;
        }
        else if(localName.equals("DefaultUsername")){
            isUsername = false;
        }
        else if(localName.equals("DefaultPassword")){
            isPassword = false;
        }
        else if(localName.equals("TempFilesDir")){
            isTempDir = false;
        }
        else if(localName.equals("AutoConnect")){
            isAutoConnect = false;
        }
    }

    @Override
    public void characters(char[] data, int start, int length) {
        if (isViewer) {
            String sView = new String(data, start, length);
            cs.setExtV(sView);
            return;
        }
        if(isHost){
            String sView = new String(data, start, length);
            cs.setDefaultServerHost(sView);
            return;
        }
        if(isPort){
            String sPort = new String(data, start, length);
            cs.setDefaultServerPort(Integer.parseInt(sPort));
            return;
        }
        if(isUsername){
            String sUsername = new String(data, start, length);
            cs.setDefaultUserName(sUsername);
            return;
        }
        if(isPassword){
            String sPassword = new String(data, start, length);
            cs.setDefaultPassword(sPassword);
            return;
        }
        if(isTempDir){
            String sDir = new String(data, start, length);
            cs.setTempFilesDir(sDir);
            return;
        }
        if(isAutoConnect)
        {
             String sView = new String(data, start, length);
             boolean result = false;
             if (sView.compareToIgnoreCase("true") == 0)
                result = true;
             cs.setAutoConnect(result);
             return;
         }
    }

    public ClientSettings getXML()
    {
        try
        {
            File file = new File(filePath);
            if (!file.exists())
            {
                cs.setDefaultSettings();
                printXML();
                return cs;
            }
            InputSource src = new InputSource( new FileInputStream(file) );
            XMLReader r = XMLReaderFactory.createXMLReader();
            r.setContentHandler(this);
            r.parse(src);
            return cs;
        }
        catch (IOException ex)
        {

        }
        catch (SAXException ex)
        {

        }
        return null;
    }

    public void printXML() {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            PrintWriter pw = new PrintWriter(out);
            StreamResult streamResult = new StreamResult(pw);
            SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();
            //      SAX2.0 ContentHandler.
            TransformerHandler hd = tf.newTransformerHandler();
            Transformer serializer = hd.getTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            serializer.setOutputProperty(OutputKeys.METHOD, "xml");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            hd.setResult(streamResult);
            hd.startDocument();

            //Get a processing instruction
            //hd.processingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"mystyle.xsl\"");
            AttributesImpl atts = new AttributesImpl();

            String curTitle;

            //root element
            hd.startElement("", "", "Config", atts);  

            hd.startElement("", "", "DirectorySettings", atts);

            curTitle = cs.getExtV();
            //external viewer
            hd.startElement("", "", "ExternalViewer", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "ExternalViewer");


            curTitle = cs.getTempFilesDir();
            //external viewer
            hd.startElement("", "", "TempFilesDir", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "TempFilesDir");

            hd.endElement("", "", "DirectorySettings");

            hd.startElement("", "", "DefaultGUIServer", atts);

            curTitle = cs.getDefaultServerHost();
            //external viewer
            hd.startElement("", "", "DefaultServerHost", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "DefaultServerHost");

            curTitle = String.valueOf(cs.getDefaultServerPort());
            //external viewer
            hd.startElement("", "", "DefaultServerPort", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "DefaultServerPort");

            curTitle = cs.getDefaultUserName();
            //external viewer
            hd.startElement("", "", "DefaultUsername", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "DefaultUsername");

            curTitle = cs.getDefaultPassword();
            //external viewer
            hd.startElement("", "", "DefaultPassword", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "DefaultPassword");

            if (cs.getAutoConnect())
                curTitle = "true";
            else
                curTitle = "false";

            //Enable P2P
            hd.startElement("", "", "AutoConnect", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "AutoConnect");

            hd.endElement("", "", "DefaultGUIServer");

            hd.endElement("", "", "Config");

            hd.endDocument();

        } catch (TransformerConfigurationException ex) {
        } catch (SAXException ex) {
        } catch (FileNotFoundException ex) {
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
            }
        }
    }
}
