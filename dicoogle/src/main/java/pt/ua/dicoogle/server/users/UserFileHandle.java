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
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * This class provides encryption to the file that saves users information
 * Uses the AES algorithm with 128 bit key
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class UserFileHandle {

    private static final Logger logger = LoggerFactory.getLogger(UserFileHandle.class);

    private final String filename;
    private final String keyFile;

    private Key key;
    private final boolean encrypt;

    public UserFileHandle() throws IOException {
        filename = "users.xml";
        keyFile = "users.key";
        encrypt = true; //ServerSettingsManager.getSettings().isEncryptUsersFile();
        try {
            if (encrypt) {

                try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(keyFile))) {
                    key = (Key) in.readObject();
                } catch (FileNotFoundException ex) {
                    KeyGenerator gen = KeyGenerator.getInstance("AES");

                    gen.init(128, new SecureRandom());
                    key = gen.generateKey();

                    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(keyFile))) {
                        out.writeObject(key);
                    }
                }

            } else {
                key = null;
            }

        } catch (NoSuchAlgorithmException | ClassNotFoundException ex) {
            logger.error("Failed to get encryption key.", ex);
        }
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
        try (FileInputStream fin = new FileInputStream(filename);
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] data = new byte[1024];
            int bytesRead;

            while ((bytesRead = fin.read(data)) != -1) {
                out.write(data, 0, bytesRead);
                out.flush();
            }

            if (encrypt) {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, key);
                byte[] Bytes = cipher.doFinal(out.toByteArray());
                return Bytes;
            }

            return out.toByteArray();

        } catch (FileNotFoundException ex) {
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
            Files.copy(in, Paths.get(filename), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
