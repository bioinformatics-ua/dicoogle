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
package pt.ua.dicoogle.server.web.rest;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.zip.GZIPInputStream;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class RestImageResource extends ServerResource {

    @Get
    public Representation represent() {/*
        String SOPInstanceUID = getRequest().getResourceRef().getQueryAsForm().getValues("uid");
        if (SOPInstanceUID == null) {
            return null;
        }

        String heightString = getRequest().getResourceRef().getQueryAsForm().getValues("height");
        int height = 0;

        if (heightString != null) {
            try {
                height = Integer.valueOf(heightString);
            } catch (NumberFormatException ex) {
            };
        }

        IndexEngine core = IndexEngine.getInstance();
        ArrayList<String> extra = new ArrayList<String>();
        extra.addMoveDestination("SOPInstanceUID");
        String query = "SOPInstanceUID:" + SOPInstanceUID;
        List<SearchResult> queryResultList = core.search(query, extra);
        if (queryResultList.size() < 1) {
            return null;//TODO:Throw exception
        }
        for (SearchResult r : queryResultList) {
            LoggerFactory.getLogger(RestDimResource.class.getName()).error(r.getOrigin());
        }
        File file = new File(queryResultList.get(0).getOrigin());

        return new DynamicFileRepresentation(MediaType.IMAGE_JPEG, file, height);
    }

    public class DynamicFileRepresentation extends OutputRepresentation {

        private File dicomImage;
        private int height;

        public DynamicFileRepresentation(MediaType mediaType, File dicomImage, int height) {
            super(mediaType);
            
            this.dicomImage = dicomImage;
            this.height = height;
            
            
            
            if (dicomImage.getAbsolutePath().endsWith(".gz"))
            {
                File temp = null;
                try {
                    temp = File.createTempFile(dicomImage.getName(), Platform.homePath() + ".dcm");
                } catch (IOException ex) {
                    LoggerFactory.getLogger(RestWADOResource.class).error(ex.getMessage(), ex);
                }
                temp.deleteOnExit();
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(temp);
                    BufferedOutputStream bos = new BufferedOutputStream(fos, 256);
                    GZIPInputStream gz = new GZIPInputStream(new BufferedInputStream(new FileInputStream(dicomImage), 256));
                    int byte_;
                    while ((byte_ = gz.read()) != -1)
                        bos.write(byte_);
                    bos.close();
                    gz.close();
                } catch (Exception ex) {
                    LoggerFactory.getLogger(RestWADOResource.class).error(ex.getMessage(), ex);
                }
                dicomImage = temp;
            }*/
        throw new UnsupportedOperationException();
        }

        /*@Override
        public void write(OutputStream outputStream) throws IOException {
            Dicom2JPEG.convertDicom2Jpeg(dicomImage, outputStream, height);
        }*/
    //}
}
