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

import pt.ua.dicoogle.core.settings.ServerSettingsManager;

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
        this(Paths.get("."));
    }

    public UserFileHandle(Path baseConfDir) throws IOException {
        // The following cases will be tested, in this order:
        // 1. Users file `users.xml.enc` and key file users.key (encrypted)
        // 2. Users file `users` and key file users.key (encrypted)
        // 3. Users file `users.xml` (not encrypted)
        // 4. Users file `users` (not encrypted)
        // 5. No users files, encrypt-users-file enabled (generate new key and users.xml.enc)
        // 6. No files, encrypt-users-file disabled (generate plain new users.xml, ignore eventual users.key)

        Path usersXmlEnc = baseConfDir.resolve("users.xml.enc");
        Path usersXml = baseConfDir.resolve("users.xml");
        Path users = baseConfDir.resolve("users");

        keyFile = baseConfDir.resolve("users.key");

        boolean encryptUsersFile = ServerSettingsManager.getSettings().getArchiveSettings().isEncryptUsersFile();

        // check whether we have a key file
        final boolean keyExists = Files.exists(keyFile);

        final boolean shouldEncrypt;

        if (keyExists) {
            if (Files.exists(usersXmlEnc)) {
                // (1) users.xml.enc, encrypted
                filename = usersXmlEnc;
                shouldEncrypt = true;
            } else if (Files.exists(users)) {
                // (2) users, encrypted
                logger.warn("File `users` will be interpreted as an encrypted users file. "
                        + "If this is correct, please rename to `users.xml.enc`");
                filename = users;
                shouldEncrypt = true;
            } else if (Files.exists(usersXml)) {
                // (3) users.xml
                logger.warn("Users file `users.xml` is not encrypted, encryption key will be ignored");
                filename = usersXml;
                shouldEncrypt = false;
            } else if (encryptUsersFile) {
                // (5) users.xml.enc, use existing key
                logger.debug("Using existing key to create a new encrypted users file");
                filename = usersXmlEnc;
                shouldEncrypt = true;
            } else {
                logger.warn("Users file encryption is disabled, existing key will be ignored");
                // (6) users.xml
                filename = usersXml;
                shouldEncrypt = false;
            }
        } else {
            if (Files.exists(usersXmlEnc)) {
                logger.warn("No user encryption key, the file `users.xml.enc` will be ignored");
            }
            if (Files.exists(users)) {
                // (4) users, no encryption
                logger.warn("File `users` will be interpreted as an plain XML users file. "
                        + "If this is correct, please rename to `users.xml`");
                filename = users;
                shouldEncrypt = false;
            } else if (encryptUsersFile) {
                // (5) users.xml.enc, new key
                filename = usersXmlEnc;
                shouldEncrypt = true;
            } else {
                // (6) users.xml, no encryption
                filename = usersXml;
                shouldEncrypt = false;
            }
        }

        if (!shouldEncrypt) {
            // stop here
            this.encrypt = false;
            return;
        }

        boolean doEncrypt = true;

        // If there is neither a key file nor users file,
        // the program will generate a new key.
        // If there is no key file for an existing users file,
        // or the creation of a new key fails,
        // encryption is disabled and an error is logged.
        try {
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(keyFile))) {
                key = (Key) in.readObject();
                // encryption key read succesfully
            } catch (NoSuchFileException ex) {
                if (!Files.exists(filename)) {
                    logger.info("Generating new user credential encryption key...");
                    KeyGenerator gen = KeyGenerator.getInstance("AES");

                    gen.init(128, new SecureRandom());
                    key = gen.generateKey();

                    try (ObjectOutputStream out =
                            new ObjectOutputStream(Files.newOutputStream(keyFile, StandardOpenOption.CREATE_NEW))) {
                        out.writeObject(key);
                    }
                    try {
                        // set read-only permissions to owner for security reasons
                        Set<PosixFilePermission> perms = Collections.singleton(PosixFilePermission.OWNER_READ);
                        Files.setPosixFilePermissions(keyFile, perms);
                    } catch (UnsupportedOperationException ex2) {
                        logger.warn(
                                "Local file system does not support POSIX file attributes, leaving encryption key file with default permissions",
                                ex2);
                    }

                    // a new key was successfully created
                } else {
                    logger.error("No key to decrypt users file, encryption disabled");
                    key = null;
                    doEncrypt = false;
                }
            }
        } catch (NoSuchAlgorithmException | ClassNotFoundException | ClassCastException ex) {
            logger.error("Failed to get encryption key, user file encryption disabled", ex);
            key = null;
            doEncrypt = false;
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
            } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
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
            logger.info("No users file \"{}\", will create one with default settings.", filename);
        } catch (IllegalBlockSizeException ex) {
            logger.error("Users file \"{}\" is corrupted, will override it with default settings.", filename, ex);
        } catch (InvalidKeyException ex) {
            logger.error("Invalid Key to decrypt users file! Please contact your system administator.");
            System.exit(1);
        } catch (BadPaddingException ex) {
            logger.error("Invalid Key to decrypt users file! Please contact your system administator.");
            System.exit(2);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
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
