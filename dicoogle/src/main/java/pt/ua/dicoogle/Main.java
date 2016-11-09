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

import org.dcm4che2.data.TransferSyntax;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import pt.ua.dicoogle.DicomLog.LogDICOM;
import pt.ua.dicoogle.DicomLog.LogXML;
import pt.ua.dicoogle.core.AsyncIndex;
import pt.ua.dicoogle.core.TagsXML;
import pt.ua.dicoogle.core.settings.ServerSettingsManager;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.Utils.Platform;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;
import pt.ua.dicoogle.sdk.utils.TagsStruct;
import pt.ua.ieeta.emailreport.Configuration;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Main class for Dicoogle
 * @author Filipe Freitas
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @author Samuel Campos <samuelcampos@ua.pt>
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class Main
{
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static boolean optimizeMemory = true;
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
    	//System.setProperty("log4j.configurationFile", "log4j-2.xml");
        //PropertyConfigurator.configure("log4j.properties");
        if (Platform.getMode() == Platform.MODE.BUNDLE)
        {
            File homeDir = new File(Platform.homePath());
            if (!homeDir.exists())
            {
                homeDir.mkdir();
            }
        }
        
        if (args.length == 0)
        {
            LaunchDicoogle();
            LaunchWebApplication();
        } else {

            if (args[0].equals("-v"))
            {
                //DebugManager.getSettings().setDebug(true);
                LaunchDicoogle();
                //DebugManager.getSettings().debug("Starting Client Thread");
                LaunchWebApplication();
            }
            if (args[0].equals("-s"))
            {
                LaunchDicoogle();
            }
            else if (args[0].equals("-w") || args[0].equals("--web") || args[0].equals("--webapp")) {
                // open browser
                LaunchDicoogle();
                LaunchWebApplication();
            }
            else if (args[0].equals("-h") || args[0].equals("--h") || args[0].equals("-help") || args[0].equals("--help"))
            {
                System.out.println("Dicoogle PACS: help");
                System.out.println("-s : Start the server");
                System.out.println("-w : Start the server and load web application in default browser (default)");
                System.out.println("-g : [deprecated] Start the server and run the desktop client application");
                System.out.println("-c : [deprecated] Run the desktop client application");
            }
            else
            {
                System.out.println("Wrong arguments!");
            }
        }
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
    
    public static boolean LaunchWebApplication() {
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            logger.warn("Desktop browsing is not supported in this machine! "
                    + "Request to open web application ignored.");
            return false;
        } else {
            try {
                ServerSettings settings = ServerSettingsManager.getSettings();
                URI uri = URI.create("http://localhost:" + settings.getWebServerSettings().getPort());
                Desktop.getDesktop().browse(uri);
                return true;
            } catch (IOException ex) {
                logger.warn("Request to open web application ignored", ex);
                return false;
            }
        }
    }

    public static void LaunchDicoogle()
    {
        try
        {
            //System.out.println("\n"+addressString(localAddresses()) + "\n");
            System.setProperty("java.rmi.server.hostname", addressString(localAddresses()));
            System.setProperty("java.net.preferIPv4Stack", "true");
        } catch (SocketException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        
        logger.debug("Starting Dicoogle");
        /* This logic will be not neccessary. This should be sent to the Plugins.
        try
        {
            Anonymous.getSettings().start();
        } catch(Exception e) {
            logger.warn("Could not start Anonimize service", e);
        }*/
        
        
        logger.debug("Loading configuration file: {}", Platform.homePath());
        Configuration.initInstance(Platform.homePath());
        try {
            Configuration.getInstance().readValues();
            Configuration.getInstance().setProps(new Properties());
        } catch (FileNotFoundException ex) {
            //DebugManager.getSettings().log("Missing crash report configuration (sender)\n");
            logger.info("No configuration file");
        }

        /* Load all Server Settings */
        try {
            ServerSettingsManager.init();
        } catch (IOException e) {
            logger.error("A critical error occurred: cannot initialize server settings.", e);
            System.exit(-1);
        }
        ServerSettings settings = ServerSettingsManager.getSettings();

        try
        {
            TagsStruct _tags = new TagsXML().getXML();

            //load DICOM Services Log
            LogDICOM ll = new LogXML().getXML();

        } catch (SAXException | IOException ex) {
            logger.error(ex.getMessage(), ex);
        }

        TransferSyntax.add(new TransferSyntax("1.2.826.0.1.3680043.2.682.1.40", false,false, false, true));
        TransferSyntax.add(new TransferSyntax("1.2.840.10008.1.2.4.70", true,false, false, true));
        TransferSyntax.add(new TransferSyntax("1.2.840.10008.1.2.5.50", false,false, false, true));    

        PluginController PController = PluginController.getInstance();

        // Start the Initial Services of Dicoogle
        pt.ua.dicoogle.server.ControlServices.getInstance();

        // Launch Async Index
        // It monitors a folder, and when a file is touched an event
        // triggers and index is updated.
        if (ServerSettingsManager.getSettings().getArchiveSettings().isDirectoryWatcherEnabled()) {
            AsyncIndex asyncIndex = new AsyncIndex();
        }
    }

    @Deprecated
    public static boolean isFixedClient()
    {
        return false;
    }

    private static Set<InetAddress> localAddresses() throws SocketException
    {
        Set<InetAddress> localAddrs = new HashSet<>();

        /*Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();

        while (ifaces.hasMoreElements()) {
        NetworkInterface iface = ifaces.nextElement();
        Enumeration<InetAddress> addrs = iface.getInetAddresses();
        while (addrs.hasMoreElements())
        localAddrs.addMoveDestination(addrs.nextElement());
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
            logger.error(ex.getMessage(), ex);
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
}
