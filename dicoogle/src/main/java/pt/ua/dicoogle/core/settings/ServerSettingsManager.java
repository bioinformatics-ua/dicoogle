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
package pt.ua.dicoogle.core.settings;

import org.apache.logging.log4j.core.jmx.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.*;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class ServerSettingsManager {
    private static final Logger logger = LoggerFactory.getLogger(ServerSettingsManager.class);

    private static JAXBContext CONTEXT;

    static class MySchemaOutputResolver extends SchemaOutputResolver {

        public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
            File file = new File(suggestedFileName);
            StreamResult result = new StreamResult(file);
            result.setSystemId(file.toURI().toURL().toString());
            return result;
        }

    }

    static {
        init();
    }

    private static void init() {
        try {
            CONTEXT = JAXBContext.newInstance(ServerSettings.class);
            SchemaOutputResolver sor = new ServerSettingsManager.MySchemaOutputResolver();
            CONTEXT.generateSchema(sor);
        } catch (JAXBException e) {
            logger.error("Failed to create a serialization context for server settings", e);
            logger.error("Reading and writing settings from the server will fail!");
            CONTEXT = null;
        } catch (IOException e) {
            logger.error("Fail", e);
            CONTEXT = null;
        }
    }

    public static ServerSettings getSettingsAt(URL url) throws IOException {
        if (CONTEXT == null) throw new IllegalStateException();
        try {
            Unmarshaller unmarshaller = CONTEXT.createUnmarshaller();
            unmarshaller.setAdapter(new SettingsAdapters.MoveDestinationAdapter());
            return (ServerSettings) unmarshaller.unmarshal(url);
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    public static ServerSettings getSettingsAt(File file) throws IOException {
        if (CONTEXT == null) throw new IllegalStateException();
        try {
            Unmarshaller unmarshaller = CONTEXT.createUnmarshaller();
            //unmarshaller.setAdapter(new SettingsAdapters.MoveDestinationAdapter());
            return (ServerSettings) unmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    public static ServerSettings getSettingsAt(String filePath) throws IOException {
        if (CONTEXT == null) throw new IllegalStateException();
        return getSettingsAt(new File(filePath));
    }

    public static void saveSettingsTo(ServerSettings settings, String path) throws IOException {
        if (CONTEXT == null) throw new IllegalStateException();
        try {
            Marshaller marshaller = CONTEXT.createMarshaller();
            marshaller.setAdapter(new SettingsAdapters.MoveDestinationAdapter());
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            try (PrintStream p = new PrintStream(new FileOutputStream(path))) {
                marshaller.marshal(settings, p);
            }
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }


    public ServerSettings getGlobalInstance() {
        return ServerSettings.getInstance();
    }
}
