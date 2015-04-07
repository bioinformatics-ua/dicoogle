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
package pt.ua.dicoogle.utils;

//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.dcm4che2.imageio.plugins.dcm.DicomImageReadParam;

/**
 *
 * @author Carlos Costa
 */
public class Dicom2JPEG {
    
    public static boolean convertDicom2Jpeg(File dcmFile, File jpgFile){
        return convertDicom2Jpeg(dcmFile, jpgFile, 0);
    }
    
    public static boolean convertDicom2Jpeg(File dcmFile, File jpgFile, int scaleHeight){
        try {
            return convertDicom2Jpeg(dcmFile, new BufferedOutputStream(new FileOutputStream(jpgFile)), scaleHeight);
        } catch (FileNotFoundException ex) {
            System.out.println("\nError: can not write to JPG file!"+ ex.getMessage());
        }
        
        return false;
    }
    
    public static boolean convertDicom2Jpeg(File dcmFile, OutputStream jpgStream){
        return convertDicom2Jpeg(dcmFile, jpgStream, 0);
    }
    
    public static boolean convertDicom2Jpeg(File dcmFile, OutputStream jpgStream, int scaleHeight){
        boolean result = false;
        ByteArrayOutputStream jpgMem = Dicom2MemJPEG(dcmFile, scaleHeight);
        
        if (jpgMem != null){
            try {
                jpgMem.writeTo(jpgStream);
                jpgStream.close();
                result = true;
            } 
            catch(IOException e) {
                System.out.println("\nError: can not write to JPG file!"+ e.getMessage());
            }
        }
        
        return result;
    }
    
    
    
    public static ByteArrayOutputStream Dicom2MemJPEG(File dcmFile){
        return Dicom2MemJPEG(dcmFile, 0);
    }
        
    public static ByteArrayOutputStream Dicom2MemJPEG(File dcmFile, int scaleHeight){
        //File myDicomFile = new File("c:/dicomImage.dcm");
        BufferedImage myJpegImage = null;
        
        // returns an Iterator containing all currently registered ImageReaders 
        // that claim to be able to decode the named format 
        // (e.g., "DICOM", "jpeg", "tiff")
        Iterator iter = ImageIO.getImageReadersByFormatName("DICOM");  
        ImageReader reader = (ImageReader) iter.next();
        DicomImageReadParam param = (DicomImageReadParam) reader.getDefaultReadParam();
        
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(dcmFile);
            reader.setInput(iis, false); 
            myJpegImage = reader.read(0, param); 
            iis.close();

            if (myJpegImage == null) {
                System.out.println("\nError: couldn't read dicom image!");
                return null;
            }
            

            // Resize Image -> Thumbnails....
            if (scaleHeight > 0){
                if (scaleHeight < 24) 
                    scaleHeight = 24; // minimum
                myJpegImage = getScaledImageWithHeight(myJpegImage, scaleHeight);           
            }
                    
            //OutputStream output = new BufferedOutputStream(new FileOutputStream(jpgFile));
            ByteArrayOutputStream jpgArray = new ByteArrayOutputStream();
            OutputStream output = new BufferedOutputStream(jpgArray); 
            
            //JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(output);
            //encoder.encode(myJpegImage);
            output.close();             // Has no effect to ByteArrayOutputStream
            
            return jpgArray;
        } 
        catch(IOException e) {
            System.out.println("\nError: couldn't read dicom image!"+ e.getMessage());
            return null;
        }
        catch(Exception e) {
            System.out.println("\nError: "+ e.getMessage());
            return null;
        }
    }
    
    
    /**A method that scales a Buffered image and takes the required height as a refference point**/
    public static BufferedImage getScaledImageWithHeight(BufferedImage image, int height) throws java.lang.Exception {
        int width = (int) (((float) image.getWidth() / (float) image.getHeight()) * height);

        Image scaledImage = image.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
        BufferedImage outImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) outImage.createGraphics();
        g2.drawImage(scaledImage, 0, 0, null);

        return outImage;
    }
    
}
