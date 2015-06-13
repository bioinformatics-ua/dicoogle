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
package pt.ua.dicoogle.server.web.servlets;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletOutputStream;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.StorageInputStream;
import pt.ua.dicoogle.sdk.StorageInterface;
import pt.ua.dicoogle.server.web.dicom.Convert2PNG;
import pt.ua.dicoogle.server.web.dicom.Information;
import pt.ua.dicoogle.server.web.utils.LocalImageCache;

/**
 * Handles the requests for DICOM frames, returning them as PNG images.
 * Also maintains a cache of the images already served to speed-up the next requests (minimizing server load by doing way less conversions).
 *
 * @author Antonio
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class ImageServlet extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(ImageServlet.class);
	private static final long serialVersionUID = 1L;

	public static final int BUFFER_SIZE = 1500; // byte size for read-write ring bufer, optimized for regular TCP connection windows

	private final LocalImageCache cache;
	
    /**
	 * Creates an image servlet.
	 *
	 * @param cache the local image caching system, can be null and if so no caching mechanism will be used.
	 */
	public ImageServlet(LocalImageCache cache) {
        this.cache = cache;
	}
    
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String sopInstanceUID = request.getParameter("SOPInstanceUID");
		String uri = request.getParameter("uri");
        boolean thumbnail = Boolean.valueOf(request.getParameter("thumbnail"));
        
		if (sopInstanceUID == null) {
            if (uri == null) {
                response.sendError(400, "URI or SOP Instance UID not provided");
                return;
            }
        } else if (sopInstanceUID.trim().isEmpty()) {
			response.sendError(400, "Invalid SOP Instance UID!");
			return;
		}
		String[] providerArray = request.getParameterValues("provider");
        List<String> providers = providerArray == null ? null : Arrays.asList(providerArray);
		String sFrame = request.getParameter("frame");
        int frame;
		if (sFrame == null) {
            frame = 0;
        } else {
            frame = Integer.parseInt(sFrame);
        }
        
        StorageInputStream imgFile;
        if (sopInstanceUID != null) {
            // get the image file for that SOP Instance UID
            imgFile = Information.getFileFromSOPInstanceUID(sopInstanceUID, providers);
            // if no .dcm file was found tell the client
            if ((imgFile == null)) {
                response.sendError(404, "No image file for supplied SOP Instance UID!");
                return;
            }
        } else {
            try {
                // get the image file by the URI
                URI imgUri = new URI(uri);
                StorageInterface storageInt = PluginController.getInstance().getStorageForSchema(imgUri);
                Iterator<StorageInputStream> storages = storageInt.at(imgUri).iterator();
                // take the first valid storage
                if (!storages.hasNext()) {
                    response.sendError(404, "No image file for supplied URI!");
                    return;
                }
                imgFile = storages.next();
                 
            } catch (URISyntaxException ex) {
                response.sendError(400, "Bad URI syntax");
                return;
            }
        }

        // the temporary file containing the cache contents for this DICOM image
		File cf;
		// if there is a cache available then use it
		if ((cache != null) && (cache.isRunning())) {
            String instanceName = sopInstanceUID != null ? sopInstanceUID : uri.replace('/', '_');
            cf = cache.getFile(instanceName, frame, thumbnail);

			// if the cache was able to get a file to cache to
			if (cf != null) {
				// we synchronize this bit so that no thread is reading while we write the contents to this file, paralelized reading is still enabled, once the writing is finished ofcourse
				synchronized (cf) {
					// check if the file already has contents in it, and if it's empty, cache the converted image contents to it
					
					if (cf.length() < 1)
					{
                        try {
                            // get the requested frame as a PNG stream
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

                            // write the stream to the file
                            FileOutputStream fos = new FileOutputStream(cf);
                            fos.write(pngStream.toByteArray());
                            fos.close();
                        } catch (IOException ex) {
                            logger.error("Could not load cached image", ex);
                            response.sendError(500);
                            return;
                        } finally {
    						// finally tell the local cache manager that we are finished writing to the file,
                            // so that other threads don't need to synchronize on it
                            // (even if the synch occurred we would still be able to read the contents in
                            // parallel across multiple threads, this is just an optimization for a cache
                            // that is designed to run for a very long time, to keep the linked list of files
                            // from being used empty for most of the time)
                            cache.finishedUsingFile(cf);
                        }
					}
				}
			} else {
				response.sendError(500, "Unable to get cached contents!");
				return;
			}
		} else { // if the cache is invalid or not running convert the image and return it "on-the-fly"

            // get the requested frame as a PNG stream
            try {
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
                response.setContentType("image/png"); // set the appropriate type for the PNG image
                response.setContentLength(pngStream.size()); // set the image size
                // write the PNG stream to the response output
                try (ServletOutputStream out = response.getOutputStream()) {
                    pngStream.writeTo(out);
                }
            } catch (IOException ex) {
                logger.error("Could not convert the image", ex);
                response.sendError(500, "Could not convert the image");
                return;
            }
			return;
		}
		
		// if we reach this point, then we are sure that there is a valid cache file and that its content is the cache of the requested image/frame, so read it and return it

		response.setContentType("image/png"); // set the appropriate type for the PNG image
		response.setContentLength((int) cf.length()); // set the image size
        
		// write the cache file contents to the response output
		try(FileInputStream in = new FileInputStream(cf); ServletOutputStream out = response.getOutputStream()) {
            int bytesRead = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            bytesRead = in.read(buffer, 0, buffer.length);
            while (bytesRead != -1)
            {
                out.write(buffer);
                bytesRead = in.read(buffer, 0, buffer.length);
            }
        }

		// finally tell the local cache manager that we are finished reading the file
		cache.finishedUsingFile(cf);
    }

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String sop = req.getParameter("SOPInstanceUID");
		if(sop == null){
			resp.sendError(500, "No SOPInstanceUID in Request");
			return;
		}

		float frameRate = Information.getFrameRateFromImage(sop);
		if(frameRate == -1){
			resp.sendError(500, "Cannot Locate the image File.");
			return;
		}
			
		
		int nFrames = Information.getNumberOfFramesInFile(sop);
		
		JSONObject r = new JSONObject();
		r.put("SOPInstanceUID", sop);
		r.put("NumberOfFrames", nFrames);
		r.put("FrameRate", frameRate);
		
		
		resp.setContentType("application/json");
		
		PrintWriter wr = resp.getWriter();
		wr.print(r.toString());	
	}
	
	
}
