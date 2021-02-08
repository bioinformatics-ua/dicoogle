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
package pt.ua.dicoogle.plugins;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import metal.utils.fileiterator.FileIterator;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.DicomInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ua.dicoogle.sdk.PluginBase;
import pt.ua.dicoogle.sdk.StorageInputStream;
import pt.ua.dicoogle.sdk.StorageInterface;

public class DefaultFileStoragePlugin extends PluginBase implements StorageInterface {

    private static final Logger logger = LoggerFactory.getLogger(DefaultFileStoragePlugin.class);

    private final String defaultScheme = "file";

    public DefaultFileStoragePlugin() {
        super();
        super.storagePlugins.add(this);
    }

    @Override
    public boolean enable() {
        return true;
    }

    @Override
    public boolean disable() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getScheme() {
        return defaultScheme;
    }

    @Override
    public boolean handles(URI location) {
        if (location.getScheme() == null)
            return true;
        return location.getScheme().equals(defaultScheme);
    }

    private Iterator<StorageInputStream> createIterator(URI location) {
        if (!handles(location)) {
            logger.error("Cannot Handle: " + location.toString());
            return Collections.emptyIterator();
        }

        logger.debug("Stared creating Iterator in: " + location.getSchemeSpecificPart());
        File parent = new File(location.getSchemeSpecificPart());
        Iterator<File> fileIt;

        if (parent.isDirectory()) {
            logger.debug("Location is a directory: " + location.getSchemeSpecificPart());
            fileIt = new FileIterator(parent);
        } else {
            List<File> files = new ArrayList<>(1);
            files.add(parent);
            fileIt = files.iterator();
        }

        logger.debug("Finished assembling Iterator: " + location.getSchemeSpecificPart());
        return new MyIterator(fileIt);
    }

    @Override
    public Iterable<StorageInputStream> at(URI location, Object... args) {
        return new MyIterable(location);
    }

    @Override
    public URI store(String calledAET, DicomObject dicomObject, Object... args) {
        return null;
    }

    @Override
    public URI store(String calledAET, DicomInputStream inputStream, Object... args) throws IOException {
        return null;
    }

    @Override
    public void remove(URI location) {
        if (!location.getScheme().equals(defaultScheme)) {
            return;
        }

        String path = location.getSchemeSpecificPart();
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
    }

    @Override
    public Stream<URI> list(URI location) throws IOException {
        if (!location.getScheme().equals(defaultScheme)) {
            return Stream.empty();
        }

        Path base = Paths.get(location);
        return Files.list(base).map(Path::toUri);
    }

    @Override
    public String getName() {
        return "default-filesystem-plugin";
    }

    private class MyIterable implements Iterable<StorageInputStream> {

        private URI baseLocation;

        public MyIterable(URI baseLocation) {
            super();
            this.baseLocation = baseLocation;
        }

        @Override
        public Iterator<StorageInputStream> iterator() {
            // TODO Auto-generated method stub
            return createIterator(baseLocation);
        }

    }

    private static class MyIterator implements Iterator<StorageInputStream> {

        private Iterator<File> it;

        public MyIterator(Iterator<File> it) {
            super();
            this.it = it;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public StorageInputStream next() {
            if (!it.hasNext())
                return null;
            File f = it.next();
            logger.debug("Added File: " + f.toURI());
            MyDICOMInputString stream = new MyDICOMInputString(f);
            return stream;
        }

        @Override
        public void remove() {
            // TODO Auto-generated method stub

        }
    }

    private static class MyDICOMInputString implements StorageInputStream {

        private File file;

        public MyDICOMInputString(File file) {
            super();
            this.file = file;
        }

        @Override
        public URI getURI() {
            return file.toURI();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            // TODO Auto-generated method stub
            return new BufferedInputStream(new FileInputStream(file));
        }

        @Override
        public long getSize() throws IOException {
            // TODO Auto-generated method stub
            return file.length();
        }

    }

}
