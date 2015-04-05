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

package pt.ua.dicoogle.rGUI.fileTransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class FileSender extends Thread {
    private File file;
    private InetAddress client;
    private int timeout = 2000; //2 seconds of timeout

    private ServerSocket ssocket;
    private Socket socket;

    public FileSender(File file, InetAddress client) throws IOException{
        ServerSocketFactory ssocketFactory = SSLServerSocketFactory.getDefault();
        ssocket = ssocketFactory.createServerSocket();
        ssocket.bind(null);
        ssocket.setSoTimeout(timeout);

        this.file = file;
        this.client = client;
    }

    @Override
    public void run(){
        long sizeTransfered;

        try {
            sizeTransfered = sender();
            
            //if(sizeTransfered != -1)
               // DebugManager.getInstance().debug("Transfer complete! File: " + file.getName());
            //else
                //(DebugManager.getInstance().debug("There was an error transfering the file: " + file.getName());

        } catch (IllegalAccessException ex) {
            LoggerFactory.getLogger(FileSender.class).error(ex.getMessage(), ex);
        }
        
        return;
    }

    /**
     *
     * @return the listener port
     */
    public int getListenerPort(){
        return ssocket.getLocalPort();
    }


    /**
     *
     * @return the number of bytes transfered or -1 in error case
     * @throws IllegalAccessException
     */
    private long sender() throws IllegalAccessException{
        long transferedBytes = 0;
        byte data[] = new byte[1024];

        try {
            try{
                socket = ssocket.accept();
            } catch(SocketTimeoutException ex){
                //DebugManager.getInstance().debug("Timeout Transfering File: " + file.getName());

                return -1;
            }

            /*
            boolean illegalAccess = true;

            InetAddress[] all = InetAddress.getAllByName(client.getHostName());

            for (int i = 0; i < all.length; i++){
                if (socket.getInetAddress().equals(all[i])){
                    illegalAccess = false;
                    break;
                }
            }

            if(illegalAccess && socket.getInetAddress().equals(InetAddress.getByName("0.0.0.0")))
                illegalAccess = false;

            if(illegalAccess){
                ssocket.close();
                socket.close();

                throw new IllegalAccessException("The IP that tried to establish the connection is not the client!\n");
            }
            */
            
            ssocket.close();   // close the listening server socket

            FileInputStream in = new FileInputStream(file);
            OutputStream out = socket.getOutputStream();

            // Transfer the file
            int size;
            while ((size = in.read(data)) != -1) {
                transferedBytes += size;

                out.write(data, 0, size);
                out.flush();
            }

            // Freeing resources
            out.close();
            in.close();
            socket.close();

        } catch (Exception ex) {
           // LoggerFactory.getLogger(FileSender.class).error(ex.getMessage(), ex);

            return -1;
        }

        return transferedBytes;
    }
}
