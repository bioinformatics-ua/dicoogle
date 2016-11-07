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
package pt.ua.dicoogle.core.settings;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class ServerSettingsTest {

    private URL testConfig;

    @Before
    public void init() {
        this.testConfig = this.getClass().getResource("test-config.xml");
    }

    @Test
    public void test() throws IOException {
        // read the settings from our test config file
        ServerSettingsManager settings = ServerSettingsManager.loadSettingsAt(this.testConfig);

        // assertions follow
        assertEquals("/tmp", settings.getPath());
        assertEquals(100, settings.getIndexerEffort());
        assertEquals("TEST-STORAGE", settings.getAE());
        assertEquals("Dicoogle", settings.getLocalAETName());
        assertFalse(settings.isGzipStorage());
        assertFalse(settings.isIndexAnonymous());
        assertTrue(settings.isIndexZIPFiles());
        assertTrue(settings.isEncryptUsersFile());

        // QR settings
        assertEquals(106, settings.getWlsPort());
        assertEquals("any", settings.getPermitedLocalInterfaces());
        assertEquals("any", settings.getPermitedRemoteHostnames());
        assertEquals(3, settings.getRspDelay());
        assertEquals(50, settings.getDIMSERspTimeout());
        assertEquals(50, settings.getIdleTimeout());
        assertEquals(50, settings.getAcceptTimeout());
        assertEquals(50, settings.getConnectionTimeout());
        assertEquals("1.2.840.10008.1.2.2|1.2.840.10008.1.2.1", settings.getTransfCap());
        assertEquals("1.2.840.10008.5.1.4.1.2.1.1", settings.getSOPClass());
        assertEquals(22, settings.getMaxClientAssoc());
        assertEquals(16360, settings.getMaxPDULenghtSend());
        assertEquals(16360, settings.getMaxPDULengthReceive());

        // DICOM Storage settings
        assertFalse(settings.isStorage());
        assertEquals(6666, settings.getStoragePort());

        // Web server settings
        ServerSettingsManager.Web web = settings.getWeb();
        assertTrue(web.isWebServer());
        assertEquals(8484, web.getServerPort());
        assertEquals("test.dicoogle.com", web.getAllowedOrigins());
    /*
        Map<String, String> modalityCFind = new HashMap<>();
        modalityCFind.put("find", "Study Root Query/Retrieve Info Model");
        modalityCFind.put("1.2.840.10008.5.1.4.1.2.1.1", "Patient Root Query/Retrieve Info Model");
        modalityCFind.put("1.2.840.10008.5.1.4.1.2.2.1", "Study Root Query/Retrieve Info Model");
        assertEquals(modalityCFind, settings.getModalityFind());

        // complex stuff
        List<MoveDestination> destinations = Arrays.asList(
                new MoveDestination("ADESTINATION", "192.168.42.42", 4444, true, "Our test destination"));
        assertEquals(destinations, settings.getMoves());
    */
    }
}
