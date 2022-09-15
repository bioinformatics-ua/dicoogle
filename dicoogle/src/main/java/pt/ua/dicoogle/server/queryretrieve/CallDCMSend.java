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
package pt.ua.dicoogle.server.queryretrieve;

import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.StorageInputStream;
import pt.ua.dicoogle.sdk.StorageInterface;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class CallDCMSend {
    private static final Logger logger = LoggerFactory.getLogger(CallDCMSend.class);

    public CallDCMSend(List<URI> files, int port, String hostname, String AETitle, String cmoveID) throws Exception {

        DicoogleDcmSend dcmsnd = new DicoogleDcmSend();

        dcmsnd.setRemoteHost(hostname);
        dcmsnd.setRemotePort(port);

        for (URI uri : files) {
            StorageInterface plugin = PluginController.getInstance().getStorageForSchema(uri);
            logger.debug("uri: {}", uri);

            if (plugin != null) {
                logger.debug("Retrieving {}", uri);
                Iterable<StorageInputStream> it = plugin.at(uri);

                for (StorageInputStream file : it) {
                    dcmsnd.addFile(file);
                    logger.debug("Added file to DcmSend: {}", uri);
                }
            }

        }
        dcmsnd.setCalledAET(AETitle);

        dcmsnd.configureTransferCapability();

        dcmsnd.setMoveOriginatorMessageID(cmoveID);
        dcmsnd.start();
        dcmsnd.open();
        try {
            dcmsnd.send();
        } finally {
            dcmsnd.close();
        }
    }
}
