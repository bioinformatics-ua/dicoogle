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
package pt.ua.dicoogle.rGUI.client.signals;

import java.rmi.RemoteException;
import pt.ua.dicoogle.rGUI.client.UIHelper.ServerMessagesManager;
import pt.ua.dicoogle.rGUI.interfaces.signals.IPendingMessagesSignal;

/**
 * Class that implements the signal object related to the penging messages
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class PendingMessagesSignal implements IPendingMessagesSignal {

    private ServerMessagesManager sMM;

    public PendingMessagesSignal(ServerMessagesManager sMM){
        if (sMM == null)
            throw new NullPointerException("ServerMessagesManager can't be null");

        this.sMM = sMM;
    }

    /**
     *
     * @param flag
     *              0 - File Already Indexed
     * @throws RemoteException
     */
    @Override
    public void sendPendingMessagesSignal(int flag) throws RemoteException {
        switch(flag){
            case 0:
                sMM.newFileAlreadyIndexedMessage();
                
        }
    }

}
