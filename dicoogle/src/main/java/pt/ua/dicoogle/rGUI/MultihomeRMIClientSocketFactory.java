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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.ua.dicoogle.rGUI;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.rmi.RMISecurityManager;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Deprecated
public class MultihomeRMIClientSocketFactory
        implements RMIClientSocketFactory, Serializable {
    private static final long serialVersionUID = 7033753601964541325L;

    private final RMIClientSocketFactory factory;

    public MultihomeRMIClientSocketFactory() {
        this.factory = RMISocketFactory.getSocketFactory();
       /* System.setProperty("java.security.policy","java.policy");
        System.setProperty("java.rmi.server.codebase", "file:/c:/pluginClasses/");*/
    }

    @Override
    public Socket createSocket(String hostString, int port) throws IOException {
        //System.setProperty("java.security.policy", "my.policy");
        final String[] hosts = hostString.split("!");
        final int nhosts = hosts.length;
        
        if (nhosts < 2)
            return factory().createSocket(hostString, port);

        List<IOException> exceptions = new ArrayList<IOException>();
        Selector selector = Selector.open();
        for (String host : hosts) {
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_CONNECT);
            SocketAddress addr = new InetSocketAddress(host, port);

            try{
                channel.connect(addr);
            }catch(SocketException e){
                //System.err.println("Sockect Exception: "+ e.getMessage() + ", Host: " + host + ", Port: "+ port);
            }
        }
        SocketChannel connectedChannel = null;

        connect:
        while (true) {
            if (selector.keys().isEmpty()) {
                throw new IOException("Connection failed for " + hostString +
                        ": " + exceptions);
            }
            selector.select();  // you can addMoveDestination a timeout parameter in millseconds
            Set<SelectionKey> keys = selector.selectedKeys();
            if (keys.isEmpty()) {
                throw new IOException("Selection keys unexpectedly empty for " +
                        hostString + "[exceptions: " + exceptions + "]");
            }
            for (SelectionKey key : keys) {
                SocketChannel channel = (SocketChannel) key.channel();
                key.cancel();
                try {
                    channel.configureBlocking(true);
                    channel.finishConnect();
                    connectedChannel = channel;
                    break connect;
                } catch (IOException e) {
                    exceptions.add(e);
                }
            }
        }

        assert connectedChannel != null;

        // Close the channels that didn't connect
        for (SelectionKey key : selector.keys()) {
            Channel channel = key.channel();
            if (channel != connectedChannel)
                channel.close();
        }

        final Socket socket = connectedChannel.socket();
        if (factory == null && RMISocketFactory.getSocketFactory() == null)
            return socket;

        // We've determined that we can connect to this host but we didn't use
        // the right factory so we have to reconnect with the factory.
        String host = socket.getInetAddress().getHostAddress();
        socket.close();
        return factory().createSocket(host, port);
    }

    private RMIClientSocketFactory factory() {
        if (factory != null)
            return factory;
        RMIClientSocketFactory f = RMISocketFactory.getSocketFactory();
        if (f != null)
            return f;
        return RMISocketFactory.getDefaultSocketFactory();
    }

    // Thanks to "km" for the reminder that I need these:
    @Override
    public boolean equals(Object x) {
        if (x.getClass() != this.getClass())
            return false;
        MultihomeRMIClientSocketFactory f = (MultihomeRMIClientSocketFactory) x;
        return ((factory == null) ?
                (f.factory == null) :
                (factory.equals(f.factory)));
    }

    @Override
    public int hashCode() {
        int h = getClass().hashCode();
        if (factory != null)
            h += factory.hashCode();
        return h;
    }
}
