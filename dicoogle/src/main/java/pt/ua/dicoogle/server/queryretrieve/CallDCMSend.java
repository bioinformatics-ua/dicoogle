/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 * <p>
 * This file is part of Dicoogle/dicoogle.
 * <p>
 * Dicoogle/dicoogle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Dicoogle/dicoogle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.server.queryretrieve;

import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.dcm4che2.io.DicomInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.StorageInputStream;
import pt.ua.dicoogle.sdk.StorageInterface;

/**
 *
 * This entity is responsable to send files to a specific Dicom Node.
 *
 * @author Luís A. Bastião Silva <bastiao@bmd-software.com>
 */
public class CallDCMSend {

    private static final Logger logger = LoggerFactory.getLogger(CallDCMSend.class);

    // To be used in other services.
    public CallDCMSend(ArrayList<File> files, int port, String hostname, String AETitle, String cmoveID) throws Exception {

        DcmSnd dcmsnd = new DcmSnd();


        dcmsnd.setRemoteHost(hostname);
        dcmsnd.setRemotePort(port);

        for (File fx : files) {
            File f = fx;
            dcmsnd.addFile(f);
        }
        dcmsnd.setCalledAET(AETitle);

        dcmsnd.configureTransferCapability();


        dcmsnd.setMoveOriginatorMessageID(cmoveID);
        dcmsnd.start();
        dcmsnd.open();
        dcmsnd.send();
        dcmsnd.close();


    }

    // To be used in C-MOVE
    public CallDCMSend(List<URI> files, int port, String hostname, String AETitle, String cmoveID) throws Exception {

        DcmSndV2 dcmsnd = new DcmSndV2();


        dcmsnd.setRemoteHost(hostname);
        dcmsnd.setRemotePort(port);

        for (URI rui : files) {
            logger.debug("Entered Retrieving: " + rui.toString());
            StorageInterface plugin = PluginController.getInstance().getStorageForSchema(rui);
            logger.debug("Plugin: " + plugin);



            if (plugin != null) {
                try {
                    logger.debug("Retrieving: " + rui.toString());
                    Iterable<StorageInputStream> it = plugin.at(rui);

                    for (StorageInputStream iStream : it) {
                        byte[] byteArr = ByteStreams.toByteArray(new DicomInputStream(iStream.getInputStream()));
                        dcmsnd.addFile(ByteBuffer.wrap(byteArr));
                        logger.debug("Added NewFile: " + rui.toString());
                    }

                } catch (IOException ex) {
                    logger.error("Problem to read/access file " + rui.toString(), ex);
                }
            }
        }
        dcmsnd.setCalledAET(AETitle);

        dcmsnd.configureTransferCapability();
        dcmsnd.setMoveOriginatorMessageID(cmoveID);
        dcmsnd.start();
        dcmsnd.open();
        dcmsnd.send();
        dcmsnd.close();


    }


}
