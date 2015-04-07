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

package pt.ua.dicoogle.utils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class KeysManager
{
    private static final String LOCAL_PATH = "Server_Keystore";
    
    public static String getServerKeyPath()
    {
        File keyPath = new File(LOCAL_PATH);
        if(keyPath.exists() && keyPath.canRead()){
            return keyPath.getAbsolutePath();        
        }
        
        //COPY FILE FROM JAR
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("Server_Keystore");
        if (stream!=null)
        {            
            try
            {
                DataOutputStream out = new DataOutputStream(
                        new BufferedOutputStream(
                        new FileOutputStream(keyPath)));
                int c;
                while((c = stream.read()) != -1)
                {
                        out.writeByte(c);
                }
                stream.close();
                out.close();
            }
            catch(IOException e)
            {
                System.err.println("Error Writing/Reading Streams.");
            }
        }
        else
        {
            //DebugManager.getInstance().debug("Missing the KeyStore file that contains the SSL keys of server");
            System.err.println("Missing the KeyStore file that contains the SSL keys of server");
        }
    
        return keyPath.getAbsolutePath();
}


    public static String getClientKeyPath()
    {
        File keyPath = new File(LOCAL_PATH);
        if(keyPath.exists() && keyPath.canRead()){
            return keyPath.getAbsolutePath();        
        }

        //Last shot
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("Client_Truststore");
            
        if (stream!=null)
        {
            try
            {
                DataOutputStream out = new DataOutputStream(
                        new BufferedOutputStream(
                        new FileOutputStream(keyPath)));
                int c;
                while((c = stream.read()) != -1)
                {
                        out.writeByte(c);
                }
                stream.close();
                out.close();

            }
            catch(IOException e)
            {
                    System.err.println("Error Writing/Reading Streams.");
            }
        }
        else
        {
            //DebugManager.getInstance().debug("Missing the TrustStore file that confirms the server certificate");
            System.err.println("Missing the TrustStore file that confirms the server certificate");
            System.exit(-3);
        }
    

    return keyPath.getAbsolutePath();
    }
}
