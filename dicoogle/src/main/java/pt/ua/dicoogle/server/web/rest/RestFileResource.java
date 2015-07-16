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

import java.io.*;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.StreamRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.StorageInputStream;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;
import pt.ua.dicoogle.server.web.rest.elements.FileDownloadUtils;

/**
 *
 * @author psytek
 *
 * WARNING: This will return *any* file on the host! The best way to correct
 * this will be to change the generated XML in order to produce SOPInstanceUID,
 * and let them act as a tag for the file. This will imply a new query every
 * time we wanto to download a new file, but that's probably fine...
 *
 */
public class RestFileResource extends ServerResource {

    private PluginController core;

    public RestFileResource() {
        this.core = PluginController.getInstance();
    }
    
    @Get
    public StreamRepresentation represent() {
        String SOPInstanceUID = getRequest().getResourceRef().getQueryAsForm().getValues("uid");
        if (SOPInstanceUID == null) {
            return null;
        }
        return FileDownloadUtils.gerFileRepresentation(SOPInstanceUID);
        /*File file = new File(queryResultList.get(0).getOrigin());
        
        File temp = file;
        if (file.getAbsolutePath().endsWith(".gz"))
        {
            try {
                temp = File.createTempFile(SOPInstanceUID, Platform.homePath() + ".dcm");
            } catch (IOException ex) {
                LoggerFactory.getLogger(RestWADOResource.class).error(ex.getMessage(), ex);
            }
            temp.deleteOnExit();
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(temp);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                GZIPInputStream gz = new GZIPInputStream(new BufferedInputStream(new FileInputStream(file)));
                int byte_;
                while ((byte_ = gz.read()) != -1)
                    bos.write(byte_);
                bos.close();
                gz.close();
            } catch (Exception ex) {
                LoggerFactory.getLogger(RestWADOResource.class).error(ex.getMessage(), ex);
            }

        }*/
    }
    
     private static class MyHolder extends JointQueryTask {
         
        @Override
        public void onCompletion() {
        }

        @Override
        public void onReceive(Task<Iterable<SearchResult>> e) {
        }
    }    
     
     private static class MyOutput extends OutputRepresentation{

         private StorageInputStream stream;

        public MyOutput(StorageInputStream stream, MediaType mediaType) {
            super(mediaType);
            this.stream = stream;
        }
        @Override
        public void write(OutputStream out) throws IOException  {
            try {
                IOUtils.copy(stream.getInputStream(), out);
            } catch (IOException ex) {
                LoggerFactory.getLogger(RestFileResource.class).error(ex.getMessage(), ex);
                ex.printStackTrace();
                throw ex;
            }
        }
     }
}
