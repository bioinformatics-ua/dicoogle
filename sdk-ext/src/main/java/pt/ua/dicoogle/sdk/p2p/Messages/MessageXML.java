/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle-sdk-ext.
 *
 * Dicoogle/dicoogle-sdk-ext is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle-sdk-ext is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.sdk.p2p.Messages;

import java.io.ByteArrayInputStream;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Carlos Ferreira
 * @author Pedro Bento
 */
public class MessageXML implements MessageI<byte[]>
{
    private String Message;

    public MessageXML(byte[] message)
    {
        this.Message = new String(message);
    }

    public String getType()
    {
        SAXReader saxReader = new SAXReader();
        ByteArrayInputStream input = new ByteArrayInputStream(Message.getBytes());
        Document document = null;
        try
        {
            document = saxReader.read(input);
        } catch (DocumentException ex)
        {
            ex.printStackTrace(System.out);
        }
        Element root = document.getRootElement();
        Element tmp = root.element(MessageFields.MESSAGE_TYPE);
        return tmp.getText();
    }

    public byte[] getMessage()
    {
        return this.Message.getBytes();
    }

    @Override
    public String toString()
    {
        return this.Message;
    }
}
