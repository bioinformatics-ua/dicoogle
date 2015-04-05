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
package pt.ua.dicoogle.rGUI.client.UIHelper;

import java.rmi.RemoteException;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.rGUI.MultihomeRMIClientSocketFactory;
import pt.ua.dicoogle.rGUI.client.AdminRefs;
import pt.ua.dicoogle.rGUI.client.signals.PendingMessagesSignal;
import pt.ua.dicoogle.rGUI.client.windows.FileAlreadyIndexed;
import pt.ua.dicoogle.rGUI.interfaces.signals.IPendingMessagesSignal;

/**
 * This class deals with the server messages to the administrator
 * For now it deals only with file Already Indexed message
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class ServerMessagesManager {

    private static PendingMessagesSignal pendingMessagesSignal;

    private static ServerMessagesManager instance;

    public static synchronized ServerMessagesManager getInstance() {
        if (instance == null) {
            instance = new ServerMessagesManager();
        }

        return instance;
    }

    private ServerMessagesManager(){
        try {

            pendingMessagesSignal = new PendingMessagesSignal(this);
            IPendingMessagesSignal pendingMessagesSignalStub = (IPendingMessagesSignal) UnicastRemoteObject.exportObject(pendingMessagesSignal, 0, new MultihomeRMIClientSocketFactory(), RMISocketFactory.getDefaultSocketFactory());

            AdminRefs.getInstance().getPendingMessages().RegisterSignalBack(pendingMessagesSignalStub);
            
        } catch (RemoteException ex) {
            LoggerFactory.getLogger(ServerMessagesManager.class).error(ex.getMessage(), ex);
        }
    }

    public void newFileAlreadyIndexedMessage(){
        FileAlreadyIndexed.getInstance().getList();
    }
}
