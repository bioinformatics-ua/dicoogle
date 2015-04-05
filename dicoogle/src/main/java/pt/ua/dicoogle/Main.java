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
package pt.ua.dicoogle;

import java.awt.Desktop;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JOptionPane;

import org.dcm4che2.data.TransferSyntax;
import org.xml.sax.SAXException;

import pt.ieeta.anonymouspatientdata.core.Anonymous;


import pt.ua.dicoogle.core.AsyncIndex;
import pt.ua.dicoogle.core.ClientSettings;
import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.core.XMLSupport;
import pt.ua.dicoogle.core.LogDICOM;
import pt.ua.dicoogle.core.LogXML;
import pt.ua.dicoogle.core.TagsXML;
import pt.ua.dicoogle.core.XMLClientSupport;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.rGUI.server.GUIServer;
import pt.ua.dicoogle.rGUI.client.windows.ConnectWindow;
import pt.ua.dicoogle.sdk.Utils.Platform;
import pt.ua.dicoogle.sdk.utils.TagsStruct;
import pt.ua.ieeta.emailreport.Configuration;

/**
 * Main class for Dicoogle
 * @author Filipe Freitas
 * @author Lu??s A. Basti??o Silva <bastiao@ua.pt>
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class Main
{

    private static boolean optimizeMemory = true;
    //static Logger logger = LoggerFactory.getLogger(Main.class);
    private static boolean isGUIServer = false;
    //indicates whether the client and the server were pulled at the same time
    private static boolean isFixedClient = false;
    private static int remoteGUIPort = 0;
    public static boolean MAC_OS_X = (System.getProperty("os.name").toLowerCase().startsWith("mac os x"));
    /**
     * Generate a random integer number between 0 and 100
     *
     * This is used to verify if the RMI client is in the same
     * process as the RMI server.
     * The client as 1/1000 probability to hit the correct number
     * if it is not in the same process.
     */
    public static int randomInteger = (new Random()).nextInt(1001);

    /**
     * Starts the graphical user interface for Dicoogle
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
    	System.setProperty("log4j.configurationFile", "log4j-2.xml");
        //PropertyConfigurator.configure("log4j.properties");
        if (Platform.getMode() == Platform.MODE.BUNDLE)
        {
            File homeDir = new File(Platform.homePath());
            if (!homeDir.exists())
            {
                homeDir.mkdir();
            }
        }
        try
        {
            //System.out.println("\n"+addressString(localAddresses()) + "\n");
            System.setProperty("java.rmi.server.hostname", addressString(localAddresses()));
        } catch (SocketException ex)
        {
            LoggerFactory.getLogger(Main.class.getName()).error((String)null, ex);
        }
        System.setProperty("log4j.configurationFile", "log4j-2.xml");
        switch (args.length)
        {
            case 0:
                isFixedClient = true;

                LaunchDicoogle();
                //DebugManager.getInstance().debug("Starting Client Thread");
                LaunchGUIClient();
                break;

            case 1:
                
                if (args[0].equals("-v"))
                {
                    isFixedClient = true;
                    //DebugManager.getInstance().setDebug(true);
                    LaunchDicoogle();
                    //DebugManager.getInstance().debug("Starting Client Thread");
                    LaunchGUIClient();
                }
                if (args[0].equals("-s"))
                {
                    LaunchDicoogle();
                } else if (args[0].equals("-c"))
                {
                    LaunchGUIClient();
                }
                else if (args[0].equals("-w") || args[0].equals("--web") || args[0].equals("--webapp")) {
                    // open browser
                    LaunchDicoogle();
                    if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        System.err.println("Desktop browsing is not supported in this machine!\n"
                                + "Request to open web application ignored.");
                    } else {
                        try {
                            ServerSettings settings = ServerSettings.getInstance();
                            URI uri = new URI("http://localhost:" + settings.getWeb().getServerPort());
                            Desktop.getDesktop().browse(uri);
                        } catch (IOException | URISyntaxException ex) {
                            LoggerFactory.getLogger(Main.class.getName()).error((String)null, ex);
                            System.err.println("Request to open web application ignored: " + ex.getMessage());
                        }
                    }
                }
                else if (args[0].equals("-h") || args[0].equals("--h") || args[0].equals("-help") || args[0].equals("--help"))
                {
                    
                    System.out.println("Dicoogle PACS: help");
                    System.out.println("-c : Run Client version");
                    System.out.println("-s : Run Server version");
                    System.out.println("-w : Run Server version and load web application in default browser");
                    System.out.println("without any option run a standalone version.");  
                }
                
                else
                {
                    System.out.println("Wrong arguments!");
                }

                break;

            default:
                System.out.println("Wrong arguments!");
        }
        System.setProperty("log4j.configurationFile", "log4j-2.xml");
        /** Register System Exceptions Hook */
        ExceptionHandler.registerExceptionHandler();

        optimizeMemoryUsage();
    }

    /**
     * This function creates a TimerTask to periodically run Garbage Colector
     *
     */
    private static void optimizeMemoryUsage()
    {
        if (optimizeMemory)
        {
            class FreeMemoryTask extends TimerTask
            {

                @Override
                public void run()
                {
                    Runtime.getRuntime().gc();
                    System.out.println("\nFree Memory: " + Runtime.getRuntime().freeMemory() / 1024 / 1024
                            + "\nTotal Memory: " + Runtime.getRuntime().totalMemory() / 1024 / 1024
                            + "\nMax Memory: " + Runtime.getRuntime().maxMemory() / 1024 / 1024);
                }
            }

            //FreeMemoryTask freeMemTask = new FreeMemoryTask();
            //Timer timer = new Timer();
            //timer.schedule(freeMemTask, 5000, 5000);
        }
    }

    public static void LaunchDicoogle()
    {
        //DebugManager.getInstance().log("Starting Dicoogle\n");
        try
        {
            Anonymous.getInstance().start();
        }
        catch(Exception e)
        {
            //DebugManager.getInstance().log("Missing databases to Anonymize\n");
        }
        //System.out.println("Loading conf:"+Platform.homePath());
        Configuration.initInstance(Platform.homePath());
        try {
            Configuration.getInstance().readValues();
            Configuration.getInstance().setProps(new Properties());
        } catch (FileNotFoundException ex) {
            //DebugManager.getInstance().log("Missing crash report configuration (sender)\n");
        }

        /* Load all Server Settings from XML */
        ServerSettings settings = new XMLSupport().getXML();

        try
        {
            TagsStruct _tags = new TagsXML().getXML();

            //load DICOM Services Log
            LogDICOM ll = new LogXML().getXML();

        } catch (SAXException | IOException ex) {
            LoggerFactory.getLogger(Main.class.getName()).error((String)null, ex);
        }

        /***
         * Connect to P2P
         */
        /** Verify if it have a defined node */
        if (!settings.isNodeNameDefined())
        {
            String hostname = "Dicoogle";

            try
            {
                InetAddress addr = InetAddress.getLocalHost();

                // Get hostname
                hostname = addr.getHostName();
            } catch (UnknownHostException e)
            {
            }

            String response = (String) JOptionPane.showInputDialog(null,
                    "What is the name of machine?",
                    "Enter Node name",
                    JOptionPane.QUESTION_MESSAGE, null, null,
                    hostname);

            settings.setNodeName(response);
            settings.setNodeNameDefined(true);

            // Save Settings in XML
            XMLSupport _xml = new XMLSupport();
            _xml.printXML();
        }

        //DebugManager.getInstance().debug("-----> NAME: " + ServerSettings.getInstance().getNodeName());

        
        TransferSyntax.add(new TransferSyntax("1.2.826.0.1.3680043.2.682.1.40", false,false, false, true));
        TransferSyntax.add(new TransferSyntax("1.2.840.10008.1.2.4.70", true,false, false, true));
        TransferSyntax.add(new TransferSyntax("1.2.840.10008.1.2.5.50", false,false, false, true));    


        PluginController PController = PluginController.getInstance();

        // Start the Inicial Services of Dicoogle
        pt.ua.dicoogle.rGUI.server.controllers.ControlServices.getInstance();



        // Lauch Async Index 
        // It monitors a folder, and when a file is touched an event
        // triggers and index is updated.

        AsyncIndex asyncIndex = new AsyncIndex();

        

        //Signals that this application is GUI Server
        isGUIServer = true;

        GUIServer GUIserv = new GUIServer();



    }

    private static void LaunchGUIClient()
    {

        /* Load all Client Settings from XML */
        ClientSettings settings = new XMLClientSupport().getXML();

        if (isGUIServer)
        {
            remoteGUIPort = ServerSettings.getInstance().getRemoteGUIPort();

            RunClient rc = new RunClient();
            ((Thread) rc).run();
        } else
        {
            ConnectWindow.getInstance();
        }

    }

    public static boolean isFixedClient()
    {
        return isFixedClient;
    }

    public static int getRemoteGUIPort()
    {
        return remoteGUIPort;
    }

    private static Set<InetAddress> localAddresses() throws SocketException
    {
        Set<InetAddress> localAddrs = new HashSet<InetAddress>();

        /*Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();

        while (ifaces.hasMoreElements()) {
        NetworkInterface iface = ifaces.nextElement();
        Enumeration<InetAddress> addrs = iface.getInetAddresses();
        while (addrs.hasMoreElements())
        localAddrs.add(addrs.nextElement());
        }
         *
         */
        InetAddress in;
        try
        {
            in = InetAddress.getLocalHost();
            InetAddress[] all = InetAddress.getAllByName(in.getHostName());
            localAddrs.addAll(Arrays.asList(all));

        } catch (UnknownHostException ex)
        {
            LoggerFactory.getLogger(Main.class.getName()).error((String)null, ex);
        }

        return localAddrs;
    }

    /**
     *
     * @param addrs
     * @return
     */
    private static String addressString(Collection<InetAddress> addrs)
    {
        String s = "";
        for (InetAddress addr : addrs)
        {
            if (addr.isLoopbackAddress())
            {
                continue;
            }
            if (s.length() > 0)
            {
                s += "!";
            }
            s += addr.getHostAddress();
        }
        return s;
    }

    private static class RunClient extends Thread
    {

        @Override
        public void run()
        {
            ConnectWindow.getInstance();
        }
    }
}
