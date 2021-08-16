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
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.ua.dicoogle.server.web.rest.elements;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.StorageInputStream;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;
import pt.ua.dicoogle.server.web.rest.RestDimResource;
import pt.ua.dicoogle.server.web.rest.RestFileResource;

/**
 *
 * @author tiago
 */
public class FileDownloadUtils {

    public static OutputRepresentation gerFileRepresentation(String SOPInstanceUID) {
        String query = "SOPInstanceUID:" + SOPInstanceUID;

        HashMap<String, String> extraFields = new HashMap<>();
        extraFields.put("SOPInstanceUID", "SOPInstanceUID");

        PluginController pc = PluginController.getInstance();
        JointQueryTask task = new MyHolder();
        pc.queryAll(task, query, extraFields);
        Iterable<SearchResult> queryResults = null;
        try {
            queryResults = task.get();
        } catch (InterruptedException | ExecutionException ex) {
            LoggerFactory.getLogger(RestFileResource.class).error(ex.getMessage(), ex);
            return null;
        }

        if (!queryResults.iterator().hasNext())
            return null;

        URI fileURI = null;
        for (SearchResult r : queryResults) {
            LoggerFactory.getLogger(RestDimResource.class).error(r.getURI().toString());
            fileURI = r.getURI();
        }

        Iterable<StorageInputStream> str = pc.resolveURI(fileURI);
        if (!str.iterator().hasNext())
            return null;

        for (StorageInputStream s : str) {
            MyOutput out = new MyOutput(s, MediaType.register("application/dicom", "dicom medical data file"));

            System.out.println("Setting File Type");
            Disposition d = new Disposition(Disposition.TYPE_ATTACHMENT);
            String name = FilenameUtils.getName(s.getURI().getPath());
            if (name.isEmpty())
                name = "Downloaded.dcm";
            d.setFilename(name);


            out.setDisposition(d);

            return out;
        }
        return null;
    }

    private static class MyHolder extends JointQueryTask {

        @Override
        public void onCompletion() {}

        @Override
        public void onReceive(Task<Iterable<SearchResult>> e) {}
    }

    private static class MyOutput extends OutputRepresentation {

        private final StorageInputStream stream;

        public MyOutput(StorageInputStream stream, MediaType mediaType) {
            super(mediaType);
            this.stream = stream;
        }

        @Override
        public void write(OutputStream out) throws IOException {
            try {
                IOUtils.copy(stream.getInputStream(), out);
            } catch (IOException ex) {
                LoggerFactory.getLogger(RestFileResource.class).error(ex.getMessage(), ex);
                throw ex;
            }

        }
    }

}
