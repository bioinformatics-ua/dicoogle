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
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.server.web.utils.ImageLoader;

/**
 * Handles conversion between the images (formats) found inside a DICOM file
 * and the PNG image format, for proper lossless web view.
 *
 * @author António Novo <antonio.novo@ua.pt>
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class Convert2PNG
{	
    private static final Logger logger = LoggerFactory.getLogger(Convert2PNG.class);
    
    private synchronized static ImageReader createDICOMImageReader() {
        Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("DICOM"); // gets the first registered ImageReader that can read DICOM data
        
        ImageReader sReader = it.next();
        return sReader;
    }
    
    private synchronized static ImageWriter createPNGImageWriter() {
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("PNG"); // gets the first registered ImageWriter that can write PNG data
        
        ImageWriter sWriter = it.next();
        return sWriter;
    }
    
	// Transformations that can be applied on each conversion.
	/**
	 * No transform applied.
	 */
    @Deprecated
	public static final int TRANSFORM_NONE = 0;
	/**
	 * Scale image based on a width target.
	 */
    @Deprecated
	public static final int TRANSFORM_SCALE_BY_WIDTH = 1;
	/**
	 * Scale image based on a height target.
	 */
    @Deprecated
	public static final int TRANSFORM_SCALE_BY_HEIGHT = 2;
	/**
	 * Scale image based on a percentage value.
	 */
    @Deprecated
	public static final int TRANSFORM_SCALE_BY_PERCENT = 3;
        
	/**
	 * Reads an input DICOM file, applies a transformation to it if any, and returns
     * the desired frame as a PNG-encoded memory stream.
	 *
	 * @param dcmStream The Dicoogle storage input Stream for the DICOM File.
	 * @param frameIndex the index of the frame wanted (zero based).
	 * @param transformType the transform to execute.
	 * @param transformParam1 the param used on width and height based scales.
	 * @param transformParam2 the param used on percentage based scales.
	 * @return the frame encoded in a PNG memory stream.
	 */
    @Deprecated
	public static ByteArrayOutputStream DICOM2PNGStream(StorageInputStream dcmStream, int frameIndex, int transformType, int transformParam1, float transformParam2)
	{	
		// setup the DICOM reader
        ImageReader reader = createDICOMImageReader();                
		if (reader == null) // if no valid reader was found abort
			return null;
		
		DicomImageReadParam readParams = (DicomImageReadParam) reader.getDefaultReadParam(); // and set the default params for it (height, weight, alpha) // FIXME is this even needed?
		
		// setup the PNG writer
		ImageWriter writer = createPNGImageWriter(); // gets the first registered ImageWriter that can save PNG data
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
			ImageInputStream inStream = ImageIO.createImageInputStream(dcmStream.getInputStream());

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
	 * Reads an input DICOM file and returns the desired frame as a PNG-encoded memory stream.
	 *
	 * @param dcmStream The Dicoogle storage input Stream for the DICOM File.
	 * @param frameIndex the index of the frame wanted (starting with #0).
	 * @return the frame encoded in a PNG memory stream.
     * @throws IOException if the I/O operations on the images fail
	 */
	public static ByteArrayOutputStream DICOM2PNGStream(StorageInputStream dcmStream, int frameIndex) throws IOException {
        return DICOM2PNGStream(dcmStream.getInputStream(), frameIndex);
	}

    /**
	 * Reads an input DICOM file and returns the desired frame as a PNG-encoded memory stream.
	 *
	 * @param iStream an input stream for the DICOM File.
	 * @param frameIndex the index of the frame wanted (starting with #0).
	 * @return the frame encoded in a PNG memory stream.
     * @throws IOException if the I/O operations on the images fail
	 */
	public static ByteArrayOutputStream DICOM2PNGStream(InputStream iStream, int frameIndex) throws IOException {
		// setup the PNG writer
		ImageWriter writer = createPNGImageWriter();

		ImageWriteParam writeParams = writer.getDefaultWriteParam(); // and set the default params for it (height, weight, alpha)
		writeParams.setProgressiveMode(ImageWriteParam.MODE_DEFAULT); // activate progressive mode (adam7), best for low bandwidth connections
		
        BufferedImage image = ImageLoader.loadImage(iStream);

        // mount the resulting memory stream
        ByteArrayOutputStream result = new ByteArrayOutputStream();

        // write the specified frame to the resulting stream
        try (ImageOutputStream outStream = ImageIO.createImageOutputStream(result)) {
            writer.setOutput(outStream);
            writer.write(image);
        }

        return result;
	}

	/**
	 * Reads an input DICOM file, scales it to fit in the given dimensions and returns the
     * desired frame as a PNG-encoded memory stream.
	 *
	 * @param inStream The Dicoogle storage input Stream for the DICOM File.
	 * @param frameIndex the index of the frame wanted (starting with #0).
     * @param width the maximum width of the resulting image
     * @param height the maximum height of the resulting image
	 * @return the frame encoded in a PNG memory stream.
     * @throws IOException if the I/O operations on the images fail
	 */
	public static ByteArrayOutputStream DICOM2ScaledPNGStream(InputStream inStream, int frameIndex, int width, int height) throws IOException {
        if (inStream == null) {
            throw new NullPointerException("dcmStream");
        }
        if (frameIndex < 0) {
            throw new IllegalArgumentException("bad frameIndex");
        }
        if (width <= 0) {
            throw new IllegalArgumentException("bad width");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("bad height");
        }
        
		// setup the PNG writer
        BufferedImage image = ImageLoader.loadImage(inStream);
        
        image = scaleImage(image, width, height);

        // mount the resulting memory stream
        ByteArrayOutputStream result = new ByteArrayOutputStream();

        // write the specified frame to the resulting stream
        try (ImageOutputStream outStream = ImageIO.createImageOutputStream(result)) {
            ImageWriter writer = createPNGImageWriter();
            ImageWriteParam writeParams = writer.getDefaultWriteParam(); // and set the default params for it
            writeParams.setProgressiveMode(ImageWriteParam.MODE_DEFAULT); // activate progressive mode (adam7), best for low bandwidth connections
            writer.setOutput(outStream);
            writer.write(image);
        }

        return result;
	}

	/**
	 * Reads an input DICOM file, scales it to fit in the given dimensions and returns the
     * desired frame as a PNG-encoded memory stream.
	 *
	 * @param dcmStream The Dicoogle storage input Stream for the DICOM File.
	 * @param frameIndex the index of the frame wanted (starting with #0).
     * @param width the maximum width of the resulting image
     * @param height the maximum height of the resulting image
	 * @return the frame encoded in a PNG memory stream.
     * @throws IOException if the I/O operations on the images fail
	 */
	public static ByteArrayOutputStream DICOM2ScaledPNGStream(StorageInputStream dcmStream, int frameIndex, int width, int height) throws IOException {
        return DICOM2ScaledPNGStream(dcmStream.getInputStream(), frameIndex, width, height);
	}
    
	/**
	 * Retrieve the number of frames from an input DICOM file.
	 *
	 * @param dcmFile The Dicoogle storage input Stream for the DICOM File.
	 * @return an integer representing the number of frames in the image,
     *   or <tt>-1</tt> if the operation fails
	 */
	public static int getNumberOfFrames(StorageInputStream dcmFile)
	{	
		// setup the DICOM reader
        ImageReader reader = createDICOMImageReader();                
	
		try (ImageInputStream inStream = ImageIO.createImageInputStream(dcmFile.getInputStream())) {

			reader.setInput(inStream);
			
			// make sure that we will read a frame within bounds
			int frameCount = reader.getNumImages(true); 

			return frameCount;

		} catch (IOException e) {
            logger.error("Failed to read DICOM image", e);
		}
		return -1;
	}

	/**
	 * Resize a BufferedImage to the specified width, preserving the original image aspect ratio.
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
	 * Resize a BufferedImage to the specified height, preserving the original image aspect ratio.
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
	 * Resize a BufferedImage based on the specified percentage, preserving the original image aspect ratio.
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

    /**
	 * Resize a BufferedImage to fit the specified dimensions, while preserving the original image aspect ratio.
     * The image scaling procedure will guarantee that at least one of the dimensions will be equal to
     * the maximum target.
	 *
	 * @param image the original image to scale.
	 * @param width the maximum width of the target
	 * @param height the maximum height of the target
	 * @return a copy of the image, scaled to fit into the given dimensions
     * @throws IllegalArgumentException on bad width and height dimensions
     * @throws NullPointerException on null image
	 */
	public static BufferedImage scaleImage(BufferedImage image, int width, int height)
	{
        if (image == null) {
            throw new NullPointerException("image is null");
        }
		if (width <= 0) {
            throw new IllegalArgumentException("illegal width dimension: " + width);
        }
		if (height <= 0) {
            throw new IllegalArgumentException("illegal height dimension: " + height);
        }
        
        if (width < height) {
            return scaleImageByHeight(image, height);
        } else {
            return scaleImageByWidth(image, width);
        }
	}
}
