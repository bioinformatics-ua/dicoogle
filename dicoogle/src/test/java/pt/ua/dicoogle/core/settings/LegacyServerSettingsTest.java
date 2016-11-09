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
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.sdk.datastructs.MoveDestination;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class LegacyServerSettingsTest {

    private URL testConfig;

    @Before
    public void init() {
        this.testConfig = this.getClass().getResource("test-config.xml");
    }

    @Test
    public void test() throws IOException {
        // read the settings from our test config file
        ServerSettings settings = ServerSettingsManager.loadLegacySettingsAt(this.testConfig);

        assertTrue(settings instanceof LegacyServerSettings);

        ServerSettings.Archive a = settings.getArchiveSettings();

        // assertions follow
        assertEquals("/opt/dicoogle/repository", a.getMainDirectory());
        assertEquals("/tmp", a.getWatchDirectory());
        assertEquals(100, a.getIndexerEffort());

        assertEquals("TEST-STORAGE", settings.getDicomServicesSettings().getAETitle());

        // QR settings
        assertEquals(106, settings.getDicomServicesSettings().getQueryRetrieveSettings().getPort());
        assertSameContent(Collections.singleton("any"), settings.getDicomServicesSettings().getAllowedLocalInterfaces());
        assertSameContent(Collections.singleton("any"), settings.getDicomServicesSettings().getAllowedHostnames());
        assertEquals(3, settings.getDicomServicesSettings().getQueryRetrieveSettings().getRspDelay());
        assertEquals(50, settings.getDicomServicesSettings().getQueryRetrieveSettings().getDIMSERspTimeout());
        assertEquals(50, settings.getDicomServicesSettings().getQueryRetrieveSettings().getIdleTimeout());
        assertEquals(50, settings.getDicomServicesSettings().getQueryRetrieveSettings().getAcceptTimeout());
        assertEquals(50, settings.getDicomServicesSettings().getQueryRetrieveSettings().getConnectionTimeout());
        //assertEquals("1.2.840.10008.5.1.4.1.2.1.1", settings.getSOPClasses());
        assertEquals(22, settings.getDicomServicesSettings().getQueryRetrieveSettings().getMaxClientAssoc());
        assertEquals(16360, settings.getDicomServicesSettings().getQueryRetrieveSettings().getMaxPDULengthSend());
        assertEquals(16360, settings.getDicomServicesSettings().getQueryRetrieveSettings().getMaxPDULengthReceive());

        // DICOM Storage settings
        assertFalse(settings.getDicomServicesSettings().getStorageSettings().isAutostart());
        assertEquals(6666, settings.getDicomServicesSettings().getStorageSettings().getPort());

        // Web server settings
        ServerSettings.WebServer web = settings.getWebServerSettings();
        assertTrue(web.isAutostart());
        assertEquals(8484, web.getPort());
        assertEquals("test.dicoogle.com", web.getAllowedOrigins());

    /* // Doesn't work
        Map<String, String> modalityCFind = new HashMap<>();
        modalityCFind.put("find", "Study Root Query/Retrieve Info Model");
        modalityCFind.put("1.2.840.10008.5.1.4.1.2.1.1", "Patient Root Query/Retrieve Info Model");
        modalityCFind.put("1.2.840.10008.5.1.4.1.2.2.1", "Study Root Query/Retrieve Info Model");
        assertEquals(modalityCFind, settings.getModalityFind());
    */

        List<MoveDestination> destinations = Arrays.asList(
                new MoveDestination("ADESTINATION", "192.168.42.42", 4444, true, "Our test destination"));
        assertSameContent(destinations, settings.getDicomServicesSettings().getMoveDestinations());
    }

    private static void assertSameContent(Collection o1, Collection o2) {
        for (Object o : o1) {
            if (!o2.contains(o)) {
                throw new ComparisonFailure(null, String.valueOf(o1), String.valueOf(o2));
            }
        }
        for (Object o : o2) {
            if (!o1.contains(o)) {
                throw new ComparisonFailure(null, String.valueOf(o1), String.valueOf(o2));
            }
        }
    }

}
