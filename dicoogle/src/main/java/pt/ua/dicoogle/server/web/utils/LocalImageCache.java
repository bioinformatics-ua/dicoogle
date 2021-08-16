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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.codec.digest.DigestUtils;
import pt.ua.dicoogle.server.web.dicom.Convert2PNG;

/**
 * Handles the caching of PNG images generated by the Image Servlet.
 * The cached images are kept inside a temporary directory created inside the user (or system) temporary directory.
 * These are maintained for a maximum period of time if they are not used, and deleted on a regular basis to save disk space while not hurting the cache performance.
 *
 * @author António Novo <antonio.novo@ua.pt>
 */
public class LocalImageCache extends Thread implements ImageRetriever {
    /**
     * The number of milliseconds to wait between pool cache directory pooling.
     */
    private volatile int interval;
    /**
     * The number of milliseconds that a file can stay in the cache without being used/read.
     */
    private volatile int maxAge;

    private final File cacheFolder;
    private volatile boolean running;

    private final ConcurrentMap<String, File> filesBeingWritten;

    private final ImageRetriever under;

    /**
     * Creates a local image cache that pools its cache directory at interval rates and deletes files older than maxAge.
     *
     * @param name the name of the cache directory.
     * @param interval the number of seconds to wait between pool cache directory pooling.
     * @param maxAge the number of seconds that a file can stay in the cache without being used/read.
     * @param under the underlying image retriever
     */
    public LocalImageCache(String name, int interval, int maxAge, ImageRetriever under) {
        super("cache-" + name);
        Objects.requireNonNull(under);

        if (interval < 1) {
            this.interval = 1;
        } else {
            this.interval = interval;
        }
        this.interval *= 1000;

        if (maxAge < 1) {
            throw new IllegalArgumentException("Illegal maxAge");
        }
        this.maxAge = maxAge * 1000;

        filesBeingWritten = new ConcurrentSkipListMap<>();
        running = false;

        this.setDaemon(true);
        this.under = under;

        // create the temporary directory
        File sysTmpDir = new File(System.getProperty("java.io.tmpdir"));
        cacheFolder = new File(sysTmpDir, name);
    }

    /**
     * Deletes a directory and all its contents.
     *
     * @param dir the directory to delete.
     */
    private static void deleteDirectory(File dir) {
        if ((dir == null) || (!dir.exists())) {
            return;
        }

        // delete all the files and folders inside this folder
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                // if it's a sub-directory then delete its content
                deleteDirectory(f);
            } else {
                f.delete();
            }
        }

        // remove the folder
        dir.delete();
    }

    @Override
    public void start() {
        // abort if we couldn't make the temp dir
        if (cacheFolder == null)
            return;
        if (!cacheFolder.exists())
            if (!cacheFolder.mkdirs())
                return;
        cacheFolder.deleteOnExit();

        // start running
        super.start();
    }

    @Override
    public void run() {
        // if the cache isn't setup abort
        if (!cacheFolder.exists())
            return;

        running = true;
        do {
            // check if there are any files worh deleting and if so do it
            checkAndRemoveOldFiles();

            // wait for the defined interval to be over
            try {
                Thread.sleep(interval);
            } catch (InterruptedException ex) {
                // do nothing
            }
        } while (isRunning());
    }

    /**
     * Stop this cache from checking for old files.
     */
    public synchronized void terminate() {
        this.running = false;

        // if needed wake the thread from its sleeping state
        this.interrupt();

        // clear and delete the temporary folder
        deleteDirectory(cacheFolder);
    }

    /**
     * Loops through the cache folder and tries to removes old/un-used files.
     */
    private synchronized void checkAndRemoveOldFiles() {
        // if the cache isn't setup abort
        if (!cacheFolder.exists())
            return;

        long currentTime = System.currentTimeMillis();

        // go through all the files inside the temp folder and check their last access
        File[] files = cacheFolder.listFiles();
        if (files == null) {
            cacheFolder.mkdirs();
            return;
        }
        for (File f : files) {
            // skip if the current file is a folder
            if (f.isDirectory())
                continue;

            // check the last access done to the file, and if it's "past its due" tries to delete it
            if (currentTime - f.lastModified() > maxAge)
                f.delete();
        }
    }

    /**
     * @return the interval
     */
    public int getInterval() {
        return interval;
    }

    /**
     * @param interval the interval to set
     */
    public void setInterval(int interval) {
        if (interval < 1) {
            this.interval = 1;
        } else {
            this.interval = interval;
        }
        this.interval *= 1000;
    }

    /**
     * @return the maxAge
     */
    public int getMaxAge() {
        return maxAge;
    }

    /**
     * @param maxAge the maxAge to set
     */
    public void setMaxAge(int maxAge) {
        if (maxAge > 1) {
            this.maxAge = maxAge;
        }
    }

    protected static String toFileName(String imageUri, int frameNumber, boolean thumbnail) {
        String filecode = imageUri + ':' + frameNumber + ':' + (thumbnail ? '1' : '0');
        return DigestUtils.sha256Hex(filecode) + ".png";
    }

    @Override
    public InputStream get(URI uri, final int frameNumber, final boolean thumbnail) throws IOException {
        File f = this.implGetFile(toFileName(uri.toString(), frameNumber, thumbnail));
        synchronized (f) {
            if (f.exists()) {
                return new FileInputStream(f);
            }
        }
        this.implCreateFile(f);
        byte[] imageArray;
        synchronized (f) {
            InputStream istream = this.under.get(uri, frameNumber, thumbnail);
            ByteArrayOutputStream result = Convert2PNG.DICOM2PNGStream(istream, frameNumber);
            imageArray = result.toByteArray();

            // create new cache file with the converted image
            try (FileOutputStream fout = new FileOutputStream(f)) {
                fout.write(imageArray);
            }
            filesBeingWritten.remove(f.getAbsolutePath());
        }

        // and return it
        return new ByteArrayInputStream(imageArray);
    }

    private File implGetFile(String fileName) throws IOException {
        // check if the file is currently being written to
        String thatFilePath = new File(fileName).getAbsolutePath();
        File f = this.filesBeingWritten.get(thatFilePath);
        if (f == null) {
            f = new File(cacheFolder, fileName);
        }
        return f;
    }

    private File implCreateFile(File f) throws IOException {
        // f.createNewFile();
        f.deleteOnExit();
        // if it does not exist add it to the list of files being written (because new content is going to be written to it outside this class) and create the
        // empty file
        filesBeingWritten.put(f.getAbsolutePath(), f);
        // return the common handle to the currently created and empty file
        return f;
    }

    /**
     * @return if the caching mechanism for checking and removing old/un-used cache files is still running
     */
    public boolean isRunning() {
        return running;
    }
}
