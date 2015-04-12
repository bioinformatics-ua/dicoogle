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
import pt.ua.dicoogle.rGUI.client.windows.Logs;
import pt.ua.dicoogle.rGUI.interfaces.signals.ILogsSignal;

/**
 * This class is responsible for handle with callbacks of Log Server
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class LogsSignal implements ILogsSignal{
    private Logs log;

    public LogsSignal(Logs log) throws NullPointerException{
        if (log == null)
            throw new NullPointerException("log can't be null");
        
        this.log = log;
    }


    /**
     *
     * @param flag
     *              0 - DICOM Log
     *              1 - Server Log
     *              2 - User Sessions Log
     * @throws RemoteException
     */
    @Override
    public void sendLogSignal(int flag) throws RemoteException {
        //System.out.println("Recebeu Flag: " + flag);
        switch(flag){
            case 0:
                log.getDICOMLog();
                break;
            case 1:
                log.getServerLog();
                break;
            case 2:
                log.getSessionsLog();
                break;
        }
    }
}
