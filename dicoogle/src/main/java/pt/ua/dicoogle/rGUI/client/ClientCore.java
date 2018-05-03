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
package pt.ua.dicoogle.rGUI.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.JOptionPane;
import pt.ua.dicoogle.Main;
import pt.ua.dicoogle.rGUI.client.UIHelper.ServerMessagesManager;
import pt.ua.dicoogle.rGUI.client.windows.MainWindow;
import pt.ua.dicoogle.rGUI.interfaces.IAdmin;
import pt.ua.dicoogle.rGUI.interfaces.IUser;

/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated

public class ClientCore {

    private IUser user = null;
    private IAdmin admin = null;

    private InetAddress serverAddr;
    private boolean localServer = false;

    private static int timeoutTime = 8000;  //5 seconds (needs to be minor than 15 seconds)
    private static int startTime = 1000;    //starts the keepAlive only 1 second after start
    
    private AdminKeepAliveThread adminKeep;
    private UserKeepAliveThread userKeep;
    
    private static ClientCore instance;
    private static Semaphore sem = new Semaphore(1, true);

    public static synchronized ClientCore getInstance() {
        try {
            sem.acquire();
            if (instance == null) {
                instance = new ClientCore();
            }
            sem.release();
        } catch (InterruptedException ex) {
            LoggerFactory.getLogger(ClientCore.class).error(ex.getMessage(), ex);
        }
        return instance;
    }

    private ClientCore() {
      
    }

    public void setUser(IUser user) {
        try {
            this.user = user;

            if(Main.isFixedClient())
                user.shtudownTimeout(Main.randomInteger);
            if (!(Main.isFixedClient() && user.shtudownTimeout(Main.randomInteger))) {
                userKeep = new UserKeepAliveThread(user);
                userKeep.start();
            }

    
            
                //userKeep = new UserKeepAliveThread(user);
                //userKeep.start();

            
            UserRefs.getUserRefs(user);
        } catch (RemoteException ex) {
            LoggerFactory.getLogger(ClientCore.class).error(ex.getMessage(), ex);
        }
    }

    public boolean isUser() {
        return user != null;
    }

    public IUser getUser() {
        return user;
    }

    public void setAdmin(IAdmin admin) {
        try {
            this.admin = admin;
            
            if(Main.isFixedClient())
                admin.shtudownTimeout(Main.randomInteger);
            if (!(Main.isFixedClient() && admin.shtudownTimeout(Main.randomInteger))) {
                adminKeep = new AdminKeepAliveThread(admin);
                adminKeep.start();
            }

            AdminRefs.getAdminRefs(admin);          
            ServerMessagesManager.getInstance();
        } catch (RemoteException ex) {
            LoggerFactory.getLogger(ClientCore.class).error(ex.getMessage(), ex);
        }
    }

    public boolean isAdmin() {
        return AdminRefs.getInstance() != null;
    }

    public void setServerAddress(InetAddress serverAddr){
        this.serverAddr = serverAddr;

        try {
            //Verify if the remote server is in the local pc
            if (serverAddr.equals(InetAddress.getByName("localhost"))){
                System.out.println(" localServer True");
                localServer = true;
                return;
            }
            
            InetAddress in = InetAddress.getLocalHost();
            InetAddress[] all = InetAddress.getAllByName(in.getHostName());
            
            for (int i = 0; i < all.length; i++)
                if (serverAddr.equals(all[i])){
                    localServer = true;
                    return;
                }
            System.out.println(" localServer False");
            localServer = false;
        } catch (UnknownHostException ex) {
            //LoggerFactory.getLogger(ClientCore.class).error(ex.getMessage(), ex);
        }
    }

    public InetAddress getServerAddress(){
        return serverAddr;
    }
    
    public boolean isLocalServer(){        
        return localServer;
    }

    public void stopKeepAlives(){
        //DebugManager.getSettings().log("Stopping KeepAlive messages between server and client");
        if(isAdmin() && adminKeep != null){
            adminKeep.timer.cancel();
            adminKeep = null;
        }

        if(userKeep != null){
            userKeep.timer.cancel();
            userKeep = null;
        }
    }

    private void closeDicoogle(int source) {
        String message = "The connection with the GUI server is lost!\n";
        
        if(source == 1)
            message += "User KeepAlive failed.\n";
        else
            message += "Admin KeepAlive failed.\n";

        message += "Dicoolge client is closing.";
        
        JOptionPane.showMessageDialog(MainWindow.getInstance(), message,
                "Connection Lost", JOptionPane.ERROR_MESSAGE);

        System.exit(2);
    }

    /**
     * Private classes, they are used only within this parent class
     */
   

    private class UserKeepAliveThread extends Thread{
        private Timer timer;
        private TimerTask userTask;
        private IUser userRef;

        public UserKeepAliveThread(IUser user){
            timer = new Timer();
            userRef = user;
        }

        @Override
        public void run(){
            userTask = new UserKeepAlive();
            timer.schedule(userTask, startTime, timeoutTime);
        }

        private class UserKeepAlive extends TimerTask {

            @Override
            public void run() {
                try {
                    userRef.KeepAlive();
                } catch (Exception ex) {
                    closeDicoogle(1);
                }
            }
        }
    }

    private class AdminKeepAliveThread extends Thread{
        private Timer timer;
        private TimerTask adminTask;
        private IAdmin adminRef;

        public AdminKeepAliveThread(IAdmin admin){
            timer = new Timer();
            adminRef = admin;
        }


        @Override
        public void run(){
            adminTask = new AdminKeepAlive();
            timer.schedule(adminTask, startTime, timeoutTime);
        }

        private class AdminKeepAlive extends TimerTask {

            @Override
            public void run() {
                try {
                    adminRef.KeepAlive();
                } catch (Exception ex) {
                    closeDicoogle(2);
                }
            }
        }
    }
}
