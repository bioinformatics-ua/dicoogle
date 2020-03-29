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
package pt.ua.dicoogle.server.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Set;

/**
 * This class provides encryption to the file that saves users information
 * Uses the AES algorithm with 128 bit key
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class UserFileHandle {

    private static final Logger logger = LoggerFactory.getLogger(UserFileHandle.class);

    private final Path filename;
    private final Path keyFile;

    private Key key;
    private final boolean encrypt;

    public UserFileHandle() throws IOException {
        filename = Paths.get("users");
        keyFile = Paths.get("users.key");
        boolean doEncrypt = false;
        // user file encryption is on by default. If the users file already exists, the program
        // will not generate a new key. if there is no key file, or the creation of a new key
        // fails, encryption is disabled.
        try {
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(keyFile))) {
                key = (Key) in.readObject();
                // encryption key read succesfully, encryption is enabled
                doEncrypt = true;
            } catch (NoSuchFileException ex) {
                if (!Files.exists(filename)) {
                    logger.info("Generating new user credential encryption key...");
                    KeyGenerator gen = KeyGenerator.getInstance("AES");

                    gen.init(128, new SecureRandom());
                    key = gen.generateKey();

                    try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(keyFile, StandardOpenOption.CREATE_NEW))) {
                        out.writeObject(key);
                    }
                    try {
                        // set read-only permissions to owner for security reasons
                        Set<PosixFilePermission> perms = Collections.singleton(PosixFilePermission.OWNER_READ);
                        Files.setPosixFilePermissions(keyFile, perms);
                    } catch (UnsupportedOperationException ex2) {
                        logger.warn("Local file system does not support POSIX file attributes, leaving encryption key file with default permissions", ex2);
                    }
                    
                    // a new key was successfully created
                    doEncrypt = true;
                }
            }
        } catch (NoSuchAlgorithmException | ClassNotFoundException | ClassCastException ex) {
            logger.error("Failed to get encryption key", ex);
            key = null;
        }
        this.encrypt = doEncrypt;
    }

    /**
     * Print one byte array in File
     * Encrypt that file with the key
     *
     * @param bytes
     */
    public void printFile(byte[] bytes) throws IOException {
        if (encrypt) {
            byte[] encryptedBytes = new byte[0];

            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, key);
                encryptedBytes = cipher.doFinal(bytes);
            } catch (BadPaddingException e) {
                logger.error("Invalid Key to decrypt users file.", e);
            } catch (IllegalBlockSizeException e) {
                logger.error("Users file \"{}\" is corrupted.", filename, e);
            } catch (InvalidKeyException e) {
                logger.error("Invalid Key to decrypt users file.", e);
            } catch (NoSuchAlgorithmException|NoSuchPaddingException e) {
                throw new RuntimeException(e);
            }

            printFileAux(encryptedBytes);
        } else {
            printFileAux(bytes);
        }
    }

    /**
     * Retrieve the contents of the users configuration file.
     *
     * @return a byte array, or null if the configuration file is not available or corrupted
     * @throws IOException on a failed attempt to read the file
     */
    public byte[] getFileContent() {
        try {
            byte[] data = Files.readAllBytes(filename);

            if (encrypt) {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, key);
                data = cipher.doFinal(data);
            }

            return data;

        } catch (NoSuchFileException ex) {
            logger.info("No such users file \"{}\", will create one with default settings.", filename);
        } catch (IllegalBlockSizeException ex) {
            logger.error("Users file \"{}\" is corrupted, will override it with default settings.", filename, ex);
        } catch (InvalidKeyException ex) {
            logger.error("Invalid Key to decrypt users file! Please contact your system administator.");
            System.exit(1);
        } catch (BadPaddingException ex) {
            logger.error("Invalid Key to decrypt users file! Please contact your system administator.");
            System.exit(2);
        } catch (NoSuchAlgorithmException|NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IOException ex) {
            logger.error("Error writing file \"{}\".", filename, ex);
        }

        return null;
    }

    private void printFileAux(byte[] bytes) throws IOException {
        try (InputStream in = new ByteArrayInputStream(bytes)) {
            Files.copy(in, filename, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
