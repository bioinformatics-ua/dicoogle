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
import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.LongPasswordStrategies;

/**
 * This class provides a password hashing service.
 */
public class HashService {

    /**
     * Hardcoded cost for the password hashing algorithm.
     */
    private static final int HASH_STRENGTH = 10;

    /**
     * Hash a password.
     *
     * @param password the password in plain text
     * @return the hash of the password
     */
    public static String hashPassword(String password) {
        return hashPassword(password.toCharArray());
    }

    /**
     * Hash a password.
     *
     * @param password the password in plain text as an array of characters,
     * filled with `'\0'`s automatically.
     * @return the hash of the password
     */
    public static String hashPassword(char[] password) {
        try {
            return BCrypt
                    .with(LongPasswordStrategies.hashSha512(BCrypt.Version.VERSION_2B))
                    .hashToString(HASH_STRENGTH, password);
        } finally {
            for (int i = 0; i < password.length; i++) {
                password[i] = '\0';
            }
        }
    }

    /**
     * Verify whether the password matches the hash.
     * 
     * @param hash the stored password hash
     * @param password the password in plain text
     * @return true iff the password is successfully verified
     */
    public static boolean verifyPassword(String hash, String password) {
        return verifyPassword(hash, password.toCharArray());
    }

    /**
     * Verify whether the password matches the hash.
     * 
     * @param hash the stored password hash
     * @param password the password in plain text as an array of characters,
     * filled with `'\0'`s automatically.
     * @return true iff the password is successfully verified
     */
    public static boolean verifyPassword(String hash, char[] password) {
        try {
            BCrypt.Result result = BCrypt.verifyer().verify(password, hash);
            return result.verified;
        } finally {
            for (int i = 0; i < password.length; i++) {
                password[i] = '\0';
            }
        }
    }
}
