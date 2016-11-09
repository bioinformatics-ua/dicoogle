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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.dicoogle.DicomLog;

import java.io.*;
// SAX classes.

//JAXP 
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.sax.*;


import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.*;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class LogXML extends DefaultHandler
{

    //private final String filename = Platform.homePath() + "DICOM_Services_Log.xml";
    private final String filename = "./DICOM_Services_Log.xml";

    private LogDICOM logs = null;
    private boolean logOn = false;
    private String type = "";
    private String date = "";
    private String ae = "";
    private String add = "";
    private String params = "";

    public LogXML()
    {
        logs = LogDICOM.getInstance();

    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attr)
    {
        if (localName.equals("log"))
        {
            this.logOn = true;
        } else if (this.logOn && !localName.equals(""))
        {
            //...
            this.type = localName;
            this.ae = this.resolveAttrib("ae", attr, localName);
            this.date = this.resolveAttrib("date", attr, localName);
            this.add = this.resolveAttrib("addMoveDestination", attr, localName);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
    {

        if (localName.equals("log"))
        {
            this.logOn = false;
        } else if (!localName.equals(""))
        {
            logs.addLine(new LogLine(type, date, ae, add, params));
        }
    }

    private String resolveAttrib(String attr, Attributes attribs, String defaultValue)
    {
        String tmp = attribs.getValue(attr);
        return (tmp != null) ? (tmp) : (defaultValue);
    }

    public LogDICOM getXML()
    {
        try
        {
            File file = new File(filename);
            if (!file.exists())
            {

                return logs;
            }

            InputSource src = new InputSource(new FileInputStream(file));
            XMLReader r = null;
            try
            {
                r = XMLReaderFactory.createXMLReader();
            } catch (SAXException ex)
            {
            }
            r.setContentHandler(this);
            r.parse(src);
            return logs;
        } catch (IOException ex)
        {
        } catch (SAXException ex)
        {
        }
        return null;
    }

    public void printXML() throws TransformerConfigurationException
    {


        FileOutputStream out = null;

        try
        {
            out = new FileOutputStream(filename);
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


            //root element
            hd.startElement("", "", "log", atts);

            ArrayList<LogLine> list = logs.getLl();
            atts.clear();
            for (LogLine l : list)
            {
                atts.addAttribute("", "", "date", "", l.getDate());
                atts.addAttribute("", "", "ae", "", l.getAe());
                atts.addAttribute("", "", "addMoveDestination", "", l.getAdd());
                atts.addAttribute("","","params","",l.getParams());

                hd.startElement("", "", l.getType(), atts);
                atts.clear();

                hd.endElement("", "", l.getType());

            }
            hd.endElement("", "", "log");

            hd.endDocument();

        } catch (TransformerConfigurationException ex)
        {
        } catch (SAXException ex)
        {
        } catch (FileNotFoundException ex)
        {
        } finally
        {
            try
            {
                out.close();
            } catch (IOException ex)
            {
            }
        }

    }
}
