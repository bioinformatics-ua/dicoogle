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
package pt.ua.dicoogle.server.web;

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

import javax.servlet.ServletOutputStream;




import net.sf.json.JSONObject;
import pt.ua.dicoogle.sdk.StorageInputStream;
import pt.ua.dicoogle.server.web.dicom.Convert2PNG;
import pt.ua.dicoogle.server.web.dicom.Information;
import pt.ua.dicoogle.server.web.utils.LocalImageCache;

/**
 * Handles the requests for DICOM frames, returning them as PNG images.
 * Also maintains a cache of the images already served to speed-up the next requests (minimizing server load by doing way less conversions).
 *
 * @author Antonio
 */
public class ImageServlet extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int BUFFER_SIZE = 1500; // byte size for read-write ring bufer, optimized for regular TCP connection windows

	private LocalImageCache cache;
	
        /**
	 * Creates a image servlet.
	 *
	 * @param cache the local image caching system, can be null and if so no caching mechanism will be used.
	 */
	public ImageServlet(LocalImageCache cache)
	{
		this.cache = cache;
	}

	

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String sopInstanceUID = request.getParameter("SOPInstanceUID");
		if (sopInstanceUID == null || sopInstanceUID.trim().isEmpty()) // make sure that the SOPInstanceUID param is supplied
		{
			response.sendError(400, "Invalid SOP Instance UID!");
			return;
		}
		String sFrame = request.getParameter("frame");
		if (sFrame == null)
			sFrame = "0";
		int frame = Integer.parseInt(sFrame);

		// get the .dcm file for that SOP Instance UID
		//File dcmFile = Information.getFileFromSOPInstanceUID(sopInstanceUID);
		StorageInputStream dcmFile = Information.getFileFromSOPInstanceUID(sopInstanceUID);
		
		// if no .dcm file was found tell the client
		if ((dcmFile == null))
		{
			response.sendError(404, "No DICOM file for supplied SOP Instance UID!");
			return;
		}

		// the temporary file containing the cache contents for this DICOM image
		File cf = null;
				
		// if there is a cache available then use it
		if ((cache != null) && (cache.isRunning()))
		{
			cf = cache.getFileFromName(sopInstanceUID + "_" + frame + ".png");

			// if the cache was able to get a file to cache to
			if (cf != null)
			{				
				// we synchronize this bit so that no thread is reading while we write the contents to this file, paralelized reading is still enabled, once the writing is finished ofcourse
				synchronized (cf)
				{
					// check if the file already has contents in it, and if it's empty, cache the converted image contents to it
					
					if (cf.length() < 1)
					{
						// get the requested frame as a PNG stream
						ByteArrayOutputStream pngStream = Convert2PNG.DICOM2PNGStream(dcmFile, frame);
						
						if (pngStream == null)
						{
							response.sendError(500, "Could not convert DICOM to PNG!");
							return;
						}

						// write the stream to the file
						FileOutputStream fos = new FileOutputStream(cf);
						fos.write(pngStream.toByteArray());
						fos.close();
						

						// finally tell the local cache manager that we are finished writing to the file, so that other threads don't need to synchronize on it even (if the synch occurred we would still be able to read the contents in parallel across multiple threads, this is just a optimization for a cache that is designed to run for a very long time, to keep the linked list of files being used empty for most of the time)
						cache.finishedUsingFile(cf);
						
					}
				}
			}
			else
			{
				response.sendError(500, "Unable to get cached contents!");
				return;
			}
		}
		else // if the cache is invalid or not running convert the image and return it "on-the-fly"
		{
			// get the requested frame as a PNG stream
			ByteArrayOutputStream pngStream = Convert2PNG.DICOM2PNGStream(dcmFile, frame);
			if (pngStream == null)
			{
				response.sendError(500, "Could not convert DICOM to PNG!");
				return;
			}
			
			response.setContentType("image/png"); // set the appropriate type for the PNG image
			response.setContentLength(pngStream.size()); // set the image size

			// write the PNG stream to the response output
			ServletOutputStream out = response.getOutputStream();
			pngStream.writeTo(out);
			out.close();
			
			return;
		}
		
		// if we reach this point, then we are sure that there is a valid cache file and that its content is the cache of the requested image/frame, so read it and return it

		response.setContentType("image/png"); // set the appropriate type for the PNG image
		response.setContentLength((int) cf.length()); // set the image size

		// write the cache file contents to the response output
		FileInputStream in = new FileInputStream(cf);
		ServletOutputStream out = response.getOutputStream();
		int bytesRead = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		bytesRead = in.read(buffer, 0, buffer.length);
		while (bytesRead != -1)
		{
			out.write(buffer);
			bytesRead = in.read(buffer, 0, buffer.length);
		}
		out.close();
		in.close();

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
