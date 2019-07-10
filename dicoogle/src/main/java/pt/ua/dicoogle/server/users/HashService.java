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

/**
 * This class provides hashing service to passwords with the SHA-256 algoritm
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class HashService {
    /**
     * Encrypt one password with the SHA-256 algorithm
     *
     * @param plaintext
     * @return the hash of the password
     */
    public static String getSHA256Hash(String plaintext) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(plaintext.getBytes("UTF-8"));
            byte[] raw = md.digest();
            byte[] asbase64 = Base64.encodeBase64(raw);
            String hash = new String(asbase64, Charset.forName("UTF-8"));
            return hash;

        } catch (UnsupportedEncodingException|NoSuchAlgorithmException ex) {
            LoggerFactory.getLogger(HashService.class).error("Failed to encode text", ex);
        }

        return null;
    }

}
