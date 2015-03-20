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

package pt.ua.dicoogle.webservices;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.dcm4che2.imageio.plugins.dcm.DicomImageReadParam;
import org.restlet.data.MediaType;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import pt.ua.dicoogle.plugins.PluginController;

/**
 *
 * @author Frederico Valente <fmvalente@ua.pt>
 *
 * WARNING: lame workaround implementation to get some images
 * lets not even discuss security
 *
 */
public class RestDCMImageResource extends ServerResource {
    static final Logger logger = Logger.getLogger("dicoogle");
    private PluginController core;

    public RestDCMImageResource() {
        this.core = PluginController.get();
    }
    
    @Get("image/jpeg")
    public void getJPEG() {
        BufferedImage dicomImage=null;
        String imgPath = getRequest().getResourceRef().getQueryAsForm().getValues("path");
        if (imgPath == null) {return;}
        
        int indexExt = imgPath.lastIndexOf('.');
        if(indexExt == -1) imgPath += ".dcm"; //a lot of dicom files have no extension
        
        String extension = imgPath.substring(indexExt);
        switch(extension.toLowerCase()){
            case ".jpg":    //these are not indexed
            case ".png":
            case ".gif":
            case ".bmp":
            case ".tiff":
            case ".jpeg":{
                File file = new File(imgPath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    BufferedImage img = ImageIO.read(file);
                    ImageIO.write(img, "jpg", baos);
                    baos.flush();
                    ByteArrayRepresentation bar = new ByteArrayRepresentation(baos.toByteArray(), MediaType.IMAGE_JPEG);
                    getResponse().setEntity(bar);
                    baos.close();
                }
                catch (IOException e) {
                    logger.throwing("RestDCMImageResource", "getJPEG", e);
                    return;
                }
                break;
            }
                
            
            case ".dicom":  //these are
            case ".dcm":{
                File file = new File(imgPath);
                //todo:if not file...

                Iterator<ImageReader> iterator =ImageIO.getImageReadersByFormatName("DICOM");
                while (iterator.hasNext()) {
                    ImageReader imageReader = (ImageReader) iterator.next();
                    DicomImageReadParam dicomImageReadParam = (DicomImageReadParam) imageReader.getDefaultReadParam();
                    try {
                        ImageInputStream iis = ImageIO.createImageInputStream(file);
                        imageReader.setInput(iis,false);
                        dicomImage = imageReader.read(0, dicomImageReadParam);
                        iis.close();
                        if(dicomImage == null){
                            System.err.println("Could not read image!!");
                        }
                    }
                    catch (IOException e) {
                       logger.throwing("RestDCMImageResource", "getJPEG", e);
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try{
                        ImageIO.write(dicomImage, "jpg", baos);
                        baos.flush();
                        ByteArrayRepresentation bar = new ByteArrayRepresentation(baos.toByteArray(), MediaType.IMAGE_JPEG);
                        getResponse().setEntity(bar);
                        baos.close();
                    }
                    catch(IOException e){
                        logger.throwing("RestDCMImageResource", "getJPEG", e);
                        return;
                    }
                }
                break;
            }
        }
    }     
}

    


    

