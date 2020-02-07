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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.LoggerFactory;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

/**
 * This class provides hashing service to passwords with the SHA-256 algoritm
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class HashService {

    /**
     * Hash a password.
     *
     * @return the hash of the password
     */
    public static String hashPassword(String password) {
        return hashPassword(password.toCharArray());
    }

    /**
     * Hash a password.
     *
     * @return the hash of the password
     */
    public static String hashPassword(char[] password) {
        // Use Argon2 hashing algorithm
        Argon2 argon2 = Argon2Factory.create();
        try {
            return argon2.hash(
                10, // nr of iterations
                65536, // nr of kiBs of memory
                1, // nr of threads
                password);
        } finally {
            argon2.wipeArray(password);
        }
    }

    /**
     * Verify whether the password matches the hash.
     */
    public static boolean verifyPassword(String hash, String password) {
        return verifyPassword(hash, password.toCharArray());
    }

    /**
     * Verify whether the password matches the hash.
     */
    public static boolean verifyPassword(String hash, char[] password) {
        Argon2 argon2 = Argon2Factory.create();
        try {
            return argon2.verify(hash, password);
        } finally {
            argon2.wipeArray(password);
        }
    }
}
