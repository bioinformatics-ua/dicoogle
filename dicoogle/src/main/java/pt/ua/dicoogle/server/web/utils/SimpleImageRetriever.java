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
/**
 */

package pt.ua.dicoogle.server.web.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.StorageInputStream;
import pt.ua.dicoogle.sdk.StorageInterface;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;
import pt.ua.dicoogle.server.web.dicom.Convert2PNG;

/**
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class SimpleImageRetriever implements ImageRetriever {

    private static final Logger logger = LoggerFactory.getLogger(SimpleImageRetriever.class);
    
    @Override
    public InputStream getByURI(String uri, int frame, boolean thumbnail) {
        throw new UnsupportedOperationException("Not implemented yet."); // TODO
    }

    private static StorageInputStream fromUid(String sopInstanceUID) throws IOException {
        // TODO use only DIM source
        JointQueryTask qt = new JointQueryTask() {
            @Override
            public void onCompletion() {
            }
            @Override
            public void onReceive(Task<Iterable<SearchResult>> e) {
            }
        };
        try {
            Iterator<SearchResult> it = PluginController.getInstance()
                    .queryAll(qt, "SOPInstanceUID:" + sopInstanceUID).get().iterator();
            if (!it.hasNext()) {
                throw new IOException("No such image of SOPInstanceUID " + sopInstanceUID);
            }
            SearchResult res = it.next();
            return fromURI(res.getURI());
        } catch (InterruptedException | ExecutionException ex) {
            throw new IOException(ex);
        }
        
    }

    private static StorageInputStream fromURI(URI uri) throws IOException {
        // TODO use only DIM source
        StorageInterface storage = PluginController.getInstance().getStorageForSchema(uri);
        StorageInputStream stream;
        Iterator<StorageInputStream> store = storage.at(uri).iterator();
        if (!store.hasNext()) {
            throw new IOException("No storage item found at the given URI");
        }
        return store.next();
    }

    private ByteArrayOutputStream getPNGStream(StorageInputStream imgFile, int frame, boolean thumbnail) throws IOException {
        ByteArrayOutputStream pngStream;
        if (thumbnail) {
            int thumbSize;
            try {
                // retrieve thumbnail dimension settings
                thumbSize = Integer.parseInt(ServerSettings.getInstance().getThumbnailsMatrix());
            } catch (NumberFormatException ex) {
                logger.warn("Failed to parse ThumbnailMatrix, using default thumbnail size");
                thumbSize = 64;
            }
            pngStream = Convert2PNG.DICOM2ScaledPNGStream(imgFile, frame, thumbSize, thumbSize);
        } else {
            pngStream = Convert2PNG.DICOM2PNGStream(imgFile, frame);
        }
        return pngStream;
    }}
