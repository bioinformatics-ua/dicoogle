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
import org.junit.Ignore;
import org.junit.Test;
import pt.ua.dicoogle.sdk.datastructs.MoveDestination;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class ServerSettingsTest {

    private URL testConfig;

    @Before
    public void init() {
        this.testConfig = this.getClass().getResource("test-config.json");
    }

    @Test
    public void test() throws IOException {
        // read the settings from our test config file
        ServerSettings settings = ServerSettingsManager.loadSettingsAt(this.testConfig);

        assertTrue(settings instanceof ServerSettingsImpl);

        // assertions follow
        assertEquals("/opt/my-data", settings.getArchiveSettings().getMainDirectory());
        assertEquals(100, settings.getArchiveSettings().getIndexerEffort());
        assertEquals("TEST-STORAGE", settings.getDicomServicesSettings().getAETitle());
        assertEquals("/opt/my-data/watched", settings.getArchiveSettings().getWatchDirectory());

        // QR settings
        assertFalse(settings.getDicomServicesSettings().getQueryRetrieveSettings().isAutostart());
        assertEquals(1033, settings.getDicomServicesSettings().getQueryRetrieveSettings().getPort());
        assertSameContent(Collections.singleton("any"), settings.getDicomServicesSettings().getAllowedLocalInterfaces());
        assertSameContent(Collections.singleton("any"), settings.getDicomServicesSettings().getAllowedHostnames());
        assertEquals(1, settings.getDicomServicesSettings().getQueryRetrieveSettings().getRspDelay());
        assertEquals(50, settings.getDicomServicesSettings().getQueryRetrieveSettings().getDIMSERspTimeout());
        assertEquals(51, settings.getDicomServicesSettings().getQueryRetrieveSettings().getIdleTimeout());
        assertEquals(52, settings.getDicomServicesSettings().getQueryRetrieveSettings().getAcceptTimeout());
        assertEquals(53, settings.getDicomServicesSettings().getQueryRetrieveSettings().getConnectionTimeout());
        assertEquals(25, settings.getDicomServicesSettings().getQueryRetrieveSettings().getMaxClientAssoc());
        assertEquals(16374, settings.getDicomServicesSettings().getQueryRetrieveSettings().getMaxPDULengthSend());
        assertEquals(16374, settings.getDicomServicesSettings().getQueryRetrieveSettings().getMaxPDULengthReceive());
        assertSameContent(Arrays.asList("1.2.840.10008.1.2.2", "1.2.840.10008.1.2.1"),
                settings.getDicomServicesSettings().getQueryRetrieveSettings().getTransferCapabilities());
        assertSameContent(Collections.singleton("1.2.840.10008.5.1.4.1.2.1.1"),
                settings.getDicomServicesSettings().getQueryRetrieveSettings().getSOPClass());

        // DICOM Storage settings
        assertTrue(settings.getDicomServicesSettings().getStorageSettings().isAutostart());
        assertEquals(6666, settings.getDicomServicesSettings().getStorageSettings().getPort());

        // Web server settings
        ServerSettings.WebServer web = settings.getWebServerSettings();
        assertTrue(web.isAutostart());
        assertEquals(8282, web.getPort());
        assertEquals("test.dicoogle.com", web.getAllowedOrigins());
    /*
        Map<String, String> modalityCFind = new HashMap<>();
        modalityCFind.put("find", "Study Root Query/Retrieve Info Model");
        modalityCFind.put("1.2.840.10008.5.1.4.1.2.1.1", "Patient Root Query/Retrieve Info Model");
        modalityCFind.put("1.2.840.10008.5.1.4.1.2.2.1", "Study Root Query/Retrieve Info Model");
        assertEquals(modalityCFind, settings.getModalityFind());
    */
        // complex stuff
        List<MoveDestination> destinations = Arrays.asList(
                new MoveDestination("ANOTHER-STORAGE", "192.168.42.42", 6666, false, "Our test storage"));
        assertSameContent(destinations, settings.getDicomServicesSettings().getMoveDestinations());
    }

    @Test
    @Ignore("Not implemented yet!")
    public void testDefaultSettings() throws IOException {
        // TODO test that all default settings are ok
    }


    @Test
    @Ignore("Not implemented yet!")
    public void testSave() throws IOException {
        // TODO test that saving is ok
    }

    private static void assertSameContent(Collection o1, Collection o2) {
        assertNotNull("left-hand collection is null", o1);
        assertNotNull("right-hand collection is null", o2);
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
