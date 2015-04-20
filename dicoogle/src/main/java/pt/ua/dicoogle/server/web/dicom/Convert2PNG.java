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
package pt.ua.dicoogle.server.web.dicom;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.dcm4che2.imageio.plugins.dcm.DicomImageReadParam;

import pt.ua.dicoogle.sdk.StorageInputStream;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

/**
 * Handles conversion between the images (formats) found inside a DICOM file
 * and the PNG image format, for proper lossless web view.
 *
 * @author António Novo <antonio.novo@ua.pt>
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class Convert2PNG
{	
    private static ImageReader sReader;
    private static ImageWriter sWriter;
    
    private static ImageReader getImageReader() {
        if(sReader != null)
            return sReader; 
        
        ImageIO.scanForPlugins();
        
        Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("DICOM"); // gets the first registered ImageReader that can read DICOM data
        
        sReader = it.next();
        
        return sReader;
    }
    
    private static ImageWriter getImageWriter() {
        if(sWriter != null)
            return sWriter; 
        
        ImageIO.scanForPlugins();
        
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("PNG"); // gets the first registered ImageReader that can read DICOM data
        
        sWriter = it.next();
        
        return sWriter;
    }
    
	// Transformations that can be applied on each conversion.
	/**
	 * No transform applied.
	 */
	public static final int TRANSFORM_NONE = 0;
	/**
	 * Scale image based on a width target.
	 */
	public static final int TRANSFORM_SCALE_BY_WIDTH = 1;
	/**
	 * Scale image based on a height target.
	 */
	public static final int TRANSFORM_SCALE_BY_HEIGHT = 2;
	/**
	 * Scale image based on a percentage value.
	 */
	public static final int TRANSFORM_SCALE_BY_PERCENT = 3;

	/**
	 * Reads an input DICOM file and returns the frame wanted as an original sized PNG encoded memory stream.
	 *
	 * @param dcmFile the already open DICOM file.
	 * @param frameIndex the index of the frame wanted (zero based).
	 * @return the frame encoded in a PNG memory stream.
	 */
	@Deprecated
	public static ByteArrayOutputStream DICOM2PNGStream(File dcmFile, int frameIndex)
	{
		return DICOM2PNGStream(dcmFile, frameIndex, TRANSFORM_NONE, 0, 0.0F);
	}
	
	/**
	 * Reads an input DICOM file and returns the frame wanted as an original sized PNG encoded memory stream.
	 *
	 * @param dcmFile The input stream for the DICOM File.
	 * @param frameIndex the index of the frame wanted (zero based).
	 * @return the frame encoded in a PNG memory stream.
	 */
	public static ByteArrayOutputStream DICOM2PNGStream(StorageInputStream dcmFile, int frameIndex)
	{
		return DICOM2PNGStream(dcmFile, frameIndex, TRANSFORM_NONE, 0, 0.0F);
	}

	/**
	 * Reads an input DICOM file, applies a trasnform to it if any, and returns the frame wanted as a PNG encoded memory stream.
	 *
	 * @param dcmFile the already open DICOM file.
	 * @param frameIndex the index of the frame wanted (zero based).
	 * @param transformType the transform to execute.
	 * @param transformParam1 the param used on width and height based scales.
	 * @param transformParam2 the param used on percentage based scales.
	 * @return the frame encoded in a PNG memory stream.
	 */
	@Deprecated
	public static ByteArrayOutputStream DICOM2PNGStream(File dcmFile, int frameIndex, int transformType, int transformParam1, float transformParam2)
	{
		// setup the DICOM reader
		ImageReader reader = ImageIO.getImageReadersByFormatName("DICOM").next(); // gets the first registered ImageReader that can read DICOM data
		if (reader == null) // if no valid reader was found abort
			return null;
		DicomImageReadParam readParams = (DicomImageReadParam) reader.getDefaultReadParam(); // and set the default params for it (height, weight, alpha) // FIXME is this even needed?

		// setup the PNG writer
		ImageWriter writer = ImageIO.getImageWritersByFormatName("PNG").next(); // gets the first registered ImageWriter that can save PNG data
		if (writer == null) // if no valid writer was found abort
			return null;
		ImageWriteParam writeParams = writer.getDefaultWriteParam(); // and set the default params for it (height, weight, alpha)
//		writeParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT); // activate PNG compression to save on bandwidth
//		writeParams.setCompressionType("Deflater"); // overall best compressor for black and white pictures
//		writeParams.setCompressionQuality(0.0F); // best compression ratio (but longer [de]compression time)
// NOTE the above compression params are not currently needed because ImageIO already automatically compresses PNG by default, it's depicted on http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4829970 as a bug but it's more of a "can't set the compression level" problem as it uses the Deflater.BEST_COMPRESSION as default (see line 144 of com.sun.imageio.plugins.png.PNGImageWriter), the code remains comment so if in the future ImageIO allows for compression level set we can set it to the best compression level again
		writeParams.setProgressiveMode(ImageWriteParam.MODE_DEFAULT); // activate progressive mode (adam7), best for low bandwidth connections

		try
		{
			ImageInputStream inStream = ImageIO.createImageInputStream(dcmFile);
			reader.setInput(inStream);

			// make sure that we will read a frame within bounds
			int frameCount = reader.getNumImages(true); // get the number of avalable frame in the DICOM file
			if ((frameCount < 1) || (frameIndex < 0) || (frameIndex >= frameCount)) // if teither the file has no frames or the frame wanted is outside of bounds abort
				return null;

			// read the specified frame from the file
			BufferedImage image = reader.read(frameIndex, readParams);

			inStream.close();

			// if no frame was read abort
			if (image == null)
				return null;

			// if there is a transform to be applied to the image, this is the right place to do it
			switch (transformType)
			{
				case TRANSFORM_SCALE_BY_WIDTH:
					image = scaleImageByWidth(image, transformParam1);
				break;
				case TRANSFORM_SCALE_BY_HEIGHT:
					image = scaleImageByHeight(image, transformParam1);
				break;
				case TRANSFORM_SCALE_BY_PERCENT:
					image = scaleImageByPercent(image, transformParam2);
				break;
			}

			// mount the resulting memory stream
			ByteArrayOutputStream result = new ByteArrayOutputStream();

			// write the specified frame to the resulting stream
			ImageOutputStream outStream = ImageIO.createImageOutputStream(result);
			writer.setOutput(outStream);
			writer.write(image);
			outStream.close();

			return result;
		}
		catch (IOException e)
		{
			System.out.println("\nError: couldn't read dicom image!" + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Reads an input DICOM file, applies a trasnform to it if any, and returns the frame wanted as a PNG encoded memory stream.
	 *
	 * @param dcmFile The input Stream for the DICOM File.
	 * @param frameIndex the index of the frame wanted (zero based).
	 * @param transformType the transform to execute.
	 * @param transformParam1 the param used on width and height based scales.
	 * @param transformParam2 the param used on percentage based scales.
	 * @return the frame encoded in a PNG memory stream.
	 */
	public static ByteArrayOutputStream DICOM2PNGStream(StorageInputStream dcmFile, int frameIndex, int transformType, int transformParam1, float transformParam2)
	{	
		// setup the DICOM reader
                ImageReader reader = getImageReader();                
		if (reader == null) // if no valid reader was found abort
			return null;
		
		DicomImageReadParam readParams = (DicomImageReadParam) reader.getDefaultReadParam(); // and set the default params for it (height, weight, alpha) // FIXME is this even needed?
		
		// setup the PNG writer
		ImageWriter writer = getImageWriter(); // gets the first registered ImageWriter that can save PNG data
		if (writer == null) // if no valid writer was found abort
			return null;

		ImageWriteParam writeParams = writer.getDefaultWriteParam(); // and set the default params for it (height, weight, alpha)
//		writeParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT); // activate PNG compression to save on bandwidth
//		writeParams.setCompressionType("Deflater"); // overall best compressor for black and white pictures
//		writeParams.setCompressionQuality(0.0F); // best compression ratio (but longer [de]compression time)
// NOTE the above compression params are not currently needed because ImageIO already automatically compresses PNG by default, it's depicted on http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4829970 as a bug but it's more of a "can't set the compression level" problem as it uses the Deflater.BEST_COMPRESSION as default (see line 144 of com.sun.imageio.plugins.png.PNGImageWriter), the code remains comment so if in the future ImageIO allows for compression level set we can set it to the best compression level again
		writeParams.setProgressiveMode(ImageWriteParam.MODE_DEFAULT); // activate progressive mode (adam7), best for low bandwidth connections
		
		try
		{			
			ImageInputStream inStream = ImageIO.createImageInputStream(dcmFile.getInputStream());

			reader.setInput(inStream);
			
			// make sure that we will read a frame within bounds
			int frameCount = reader.getNumImages(true); // get the number of avalable frame in the DICOM file
			if ((frameCount < 1) || (frameIndex < 0) || (frameIndex >= frameCount)) // if teither the file has no frames or the frame wanted is outside of bounds abort
				return null;

			// read the specified frame from the file
			BufferedImage image = reader.read(frameIndex, readParams);
			
			inStream.close();
			

			// if no frame was read abort
			if (image == null)
				return null;
			
			// if there is a transform to be applied to the image, this is the right place to do it
			switch (transformType)
			{
				case TRANSFORM_SCALE_BY_WIDTH:
					image = scaleImageByWidth(image, transformParam1);
				break;
				case TRANSFORM_SCALE_BY_HEIGHT:
					image = scaleImageByHeight(image, transformParam1);
				break;
				case TRANSFORM_SCALE_BY_PERCENT:
					image = scaleImageByPercent(image, transformParam2);
				break;
			}
			// mount the resulting memory stream
			ByteArrayOutputStream result = new ByteArrayOutputStream();

			// write the specified frame to the resulting stream
			ImageOutputStream outStream = ImageIO.createImageOutputStream(result);
			writer.setOutput(outStream);
			writer.write(image);
			outStream.close();
						
			return result;
		}
		catch (IOException e)
		{
			System.out.println("\nError: couldn't read dicom image!" + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Reads an input DICOM file, applies a trasnform to it if any, and returns the frame wanted as a PNG encoded memory stream.
	 *
	 * @param dcmFile The input Stream for the DICOM File.
	 * @param frameIndex the index of the frame wanted (zero based).
	 * @param transformType the transform to execute.
	 * @param transformParam1 the param used on width and height based scales.
	 * @param transformParam2 the param used on percentage based scales.
	 * @return the frame encoded in a PNG memory stream.
	 */
	public static int getNumberOfFrames(StorageInputStream dcmFile)
	{	
		// setup the DICOM reader
        ImageReader reader = getImageReader();                
		if (reader == null) // if no valid reader was found abort
			return -1;
	
		try {
			ImageInputStream inStream = ImageIO.createImageInputStream(dcmFile.getInputStream());

			reader.setInput(inStream);
			
			// make sure that we will read a frame within bounds
			int frameCount = reader.getNumImages(true); 

			inStream.close();
			
			return frameCount;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Resized a BufferedImage to the specified width, preserving the original image aspect ratio.
	 *
	 * @param image the original image to scale.
	 * @param width the target width.
	 * @return a BufferedImage scaled to the target width.
	 */
	public static BufferedImage scaleImageByWidth(BufferedImage image, int width)
	{
		if (width <= 0)
			return image;

		Image scaledImage = image.getScaledInstance(width, -1, Image.SCALE_SMOOTH); // scale the input, smooth scaled
		BufferedImage result = new BufferedImage(scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_RGB); // create the output image

		result.getGraphics().drawImage(scaledImage, 0, 0, null); // draw the scaled image onto the output

		return result;
	}

	/**
	 * Resized a BufferedImage to the specified height, preserving the original image aspect ratio.
	 *
	 * @param image the original image to scale.
	 * @param height the target height.
	 * @return a BufferedImage scaled to the target height.
	 */
	public static BufferedImage scaleImageByHeight(BufferedImage image, int height)
	{
		if (height <= 0)
			return image;

		Image scaledImage = image.getScaledInstance(-1, height, Image.SCALE_SMOOTH); // scale the input, smooth scaled
		BufferedImage result = new BufferedImage(scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_RGB); // create the output image

		result.getGraphics().drawImage(scaledImage, 0, 0, null); // draw the scaled image onto the output

		return result;
	}

	/**
	 * Resized a BufferedImage based on the specified percentage, preserving the original image aspect ratio.
	 *
	 * @param image the original image to scale.
	 * @param percent the percentage to scale to, 1.0 is original, 0.5 is half, 2.0 is double, etc.
	 * @return a BufferedImage scaled to the target percentage.
	 */
	public static BufferedImage scaleImageByPercent(BufferedImage image, float percent)
	{
		if ((percent <= 0.0F) || (percent == 1.0F)) // if the scale is either null, negative or none at all return the original image
			return image;

		Image scaledImage = image.getScaledInstance((int) (percent * image.getWidth()), -1, Image.SCALE_SMOOTH); // scale the input, smooth scaled
		BufferedImage result = new BufferedImage(scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_RGB); // create the output image

		result.getGraphics().drawImage(scaledImage, 0, 0, null); // draw the scaled image onto the output

		return result;
	}
}
