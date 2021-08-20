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
package pt.ua.dicoogle.server.web.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.sdk.StorageInputStream;

/**
 * A utility library for loading images.
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class ImageLoader {

    private ImageLoader() {}

    static {
        ImageIO.scanForPlugins();
    }

    /**
     * Obtain an image from an ordinary input stream. This method will attempt to automatically use the
     * appropriate image reader for the image's format, including DICOM.
     *
     * @param inputStream the input stream to retrieve the image from
     * @return a buffered image
     * @throws IOException if the image format is not supported or another IO issue occurred
     */
    public static BufferedImage loadImage(InputStream inputStream) throws IOException {
        BufferedImage image;
        try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream)) {

            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInputStream);
            if (!readers.hasNext()) {
                throw new IOException("Unsupported image format");
            }
            ImageReader reader = readers.next();
            reader.setInput(imageInputStream, false);
            if (reader.getFormatName().equalsIgnoreCase("DICOM")) {
                ImageReadParam param = reader.getDefaultReadParam();
                image = reader.read(0, param);
            } else {
                image = reader.read(0);
            }
        } catch (org.dcm4che2.data.ConfigurationError | IOException ex) {
            LoggerFactory.getLogger(ImageLoader.class)
                    .debug("Failed to load image reader, attempting special DICOM reading mechanism", ex);
            image = loadDICOMImage(inputStream);
        }
        return image;
    }

    /**
     * Obtain an image from a Dicoogle storage input stream. This method will attempt to automatically use the
     * appropriate image reader for the image's format, including DICOM.
     *
     * @param imageFromStorage the storage input stream to retrieve the image from
     * @return a buffered image
     * @throws IOException if the image format is not supported or another IO issue occurred
     */
    public static BufferedImage loadImage(StorageInputStream imageFromStorage) throws IOException {
        return loadImage(imageFromStorage.getInputStream());
    }

    /**
     * Obtain a DICOM image from an orginary input stream. This method will attempt to read the file in
     * storage as a DICOM file only.
     *
     * @param inputStream the input stream to retrieve the DICOM image from
     * @return a buffered image
     * @throws IOException if the image format is not DICOM or another IO issue occurred
     */
    public static BufferedImage loadDICOMImage(InputStream inputStream) throws IOException {
        try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream)) {
            Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("DICOM");
            ImageReader reader = iter.next();
            ImageReadParam param = reader.getDefaultReadParam();
            reader.setInput(imageInputStream, false);
            BufferedImage img = reader.read(0, param);
            return img;
        }
    }

    /**
     * Obtain a DICOM from a Dicoogle storage input stream. This method will attempt to read the file in
     * storage as a DICOM file only.
     *
     * @param imageFromStorage the storage input stream to retrieve the DICOM image from
     * @return a buffered image
     * @throws IOException if the image format is not DICOM or another IO issue occurred
     */
    public static BufferedImage loadDICOMImage(StorageInputStream imageFromStorage) throws IOException {
        return loadDICOMImage(imageFromStorage.getInputStream());
    }
}
