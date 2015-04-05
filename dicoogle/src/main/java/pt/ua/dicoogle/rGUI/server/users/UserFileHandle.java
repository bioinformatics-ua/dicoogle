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
package pt.ua.dicoogle.rGUI.server.users;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.sdk.Utils.Platform;

/**
 * This class provides encryptation to the file that saves users information
 * Uses the AES algorithim with 128 bit key
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class UserFileHandle {

    private String filename;
    private String keyFile;

    private Cipher cipher;
    private Key key;
    private boolean encrypt;

    public UserFileHandle() throws Exception {
        filename = Platform.homePath() + "users.xml";
        encrypt = ServerSettings.getInstance().isEncryptUsersFile();

        if(encrypt){
            keyFile = "users.key";

            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(keyFile));
                key = (Key) in.readObject();
                in.close();

            } catch (FileNotFoundException ex) {
                KeyGenerator gen = KeyGenerator.getInstance("AES");

                gen.init(128, new SecureRandom());
                key = gen.generateKey();

                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(keyFile));
                out.writeObject(key);
                out.close();
            }

            cipher = Cipher.getInstance("AES");
        }

    }

    /**
     * Print one byte array in File
     * Encrypt that file with the key
     * @param xml
     */
    public void printFile(byte[] bytes) throws Exception {
        InputStream in;

        if(encrypt){
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedBytes = cipher.doFinal(bytes);
            in = new ByteArrayInputStream(encryptedBytes);
        }
        else
            in = new ByteArrayInputStream(bytes);

        FileOutputStream out = new FileOutputStream(filename);
        

        byte[] input = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(input)) != -1) {
            out.write(input, 0, bytesRead);
            out.flush();
        }

        out.close();
        in.close();
    }

    public byte[] getFileContent() throws Exception {
        try {
            FileInputStream fin = null;
            ByteArrayOutputStream out = null;
            try {
                fin = new FileInputStream(filename);
                out = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                int bytesRead;

                while ((bytesRead = fin.read(data)) != -1) {
                    out.write(data, 0, bytesRead);
                    out.flush();
                }

                out.close();
                fin.close();
                
                if(encrypt){
                    cipher.init(Cipher.DECRYPT_MODE, key);
                    byte[] Bytes = cipher.doFinal(out.toByteArray());
                    return Bytes;
                }
                else
                    return out.toByteArray();

                
            } catch (IOException ex) {
                //LoggerFactory.getLogger(UserFileHandle.class).error(ex.getMessage(), ex);
            } finally {
                if (fin != null) {
                    fin.close();
                }
            }
            
            return null;
        } catch (InvalidKeyException ex) {
            System.out.println("Invalid Key to decrypt users file! Please contact your system administator.");
            System.exit(1);

            return null;
        }
        catch(BadPaddingException ex){
            System.out.println("Invalid Key to decrypt users file! Please contact your system administator.");
            System.exit(2);

            return null;
        }
    }
}
