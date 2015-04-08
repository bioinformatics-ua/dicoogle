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
package pt.ua.dicoogle.rGUI.interfaces.controllers;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * avaliable methods in Services window
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */

/*
 * Considerar a possibilidade de retirar os métodos que indicam o estado de cada
 * serviço individualmente e adicionar um método que devolve o estado de todos
 * os serviços (num objecto "EstadoServiços").
 * 
 * Apesar da serialização deste objecto, iria diminuir o número de comuniações
 * entre o servidor e o cliente.
 *
 */
@Deprecated
public interface IServices extends Remote {

    boolean stopAllServices() throws RemoteException;


    void startPlugin(String pluginName) throws RemoteException;
    void stopPlugin(String pluginName) throws RemoteException;
    boolean pluginIsRunning(String pluginName) throws RemoteException;


    void startQueryRetrieve() throws RemoteException;
    void stopQueryRetrieve() throws RemoteException;
    boolean queryRetrieveIsRunning() throws RemoteException;


    /**
     *
     * @return   0 - if everything is fine and the service was started
     *           1 - if the server's storage path is not defined
     *           2 - service is already running
     *
     * @throws IOException
     */
    int startStorage() throws IOException, RemoteException;
    void stopStorage() throws RemoteException;
    boolean storageIsRunning() throws RemoteException;


    boolean webServerIsRunning() throws RemoteException;


    void startWebServices() throws IOException, RemoteException;
    void stopWebServices() throws IOException, RemoteException;
    boolean webServicesIsRunning() throws RemoteException;
    
    void startWebServer() throws RemoteException;
    void stopWebServer() throws RemoteException;
    
}
