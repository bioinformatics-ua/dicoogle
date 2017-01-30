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
import java.nio.file.Files;
import java.nio.file.Path;
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
    private URL legacyConfig;

    @Before
    public void init() {
        this.testConfig = this.getClass().getResource("test-config-new.xml");
        this.legacyConfig = this.getClass().getResource("test-config.xml");
    }

    @Test
    public void test() throws IOException {
        // read the settings from our test config file
        ServerSettings settings = ServerSettingsManager.loadSettingsAt(this.testConfig);

        assertTrue(settings instanceof ServerSettingsImpl);

        // assertions follow

        final ServerSettings.Archive ar = settings.getArchiveSettings();
        assertEquals("/opt/my-data", ar.getMainDirectory());
        assertEquals(98, ar.getIndexerEffort());
        assertEquals("/opt/my-data/watched", ar.getWatchDirectory());
        assertEquals("dicoogle01", ar.getNodeName());

        assertSameContent(Collections.singleton("lucene"), ar.getDIMProviders());
        assertSameContent(Collections.singleton("filestorage"), ar.getDefaultStorage());

        assertEquals("TEST-STORAGE", settings.getDicomServicesSettings().getAETitle());

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
        assertEquals(6777, settings.getDicomServicesSettings().getStorageSettings().getPort());

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
    public void testDefaultSettings() throws IOException {
        // test that all default settings are ok

        // create default settings
        ServerSettings settings = ServerSettingsImpl.createDefault();

        assertTrue(settings instanceof ServerSettingsImpl);

        // assertions follow

        final ServerSettings.Archive ar = settings.getArchiveSettings();
        assertEquals(null, ar.getMainDirectory());
        assertEquals(100, ar.getIndexerEffort());
        assertEquals("", ar.getWatchDirectory());
        assertEquals(null, ar.getNodeName());

        assertEquals("DICOOGLE-STORAGE", settings.getDicomServicesSettings().getAETitle());

        // QR settings
        assertTrue(settings.getDicomServicesSettings().getQueryRetrieveSettings().isAutostart());
        assertEquals(1045, settings.getDicomServicesSettings().getQueryRetrieveSettings().getPort());
        assertSameContent(Collections.singleton("any"), settings.getDicomServicesSettings().getAllowedLocalInterfaces());
        assertSameContent(Collections.singleton("any"), settings.getDicomServicesSettings().getAllowedHostnames());
        assertEquals(0, settings.getDicomServicesSettings().getQueryRetrieveSettings().getRspDelay());
        assertEquals(60, settings.getDicomServicesSettings().getQueryRetrieveSettings().getDIMSERspTimeout());
        assertEquals(60, settings.getDicomServicesSettings().getQueryRetrieveSettings().getIdleTimeout());
        assertEquals(60, settings.getDicomServicesSettings().getQueryRetrieveSettings().getAcceptTimeout());
        assertEquals(60, settings.getDicomServicesSettings().getQueryRetrieveSettings().getConnectionTimeout());
        assertEquals(20, settings.getDicomServicesSettings().getQueryRetrieveSettings().getMaxClientAssoc());
        assertEquals(16364, settings.getDicomServicesSettings().getQueryRetrieveSettings().getMaxPDULengthSend());
        assertEquals(16364, settings.getDicomServicesSettings().getQueryRetrieveSettings().getMaxPDULengthReceive());
        assertSameContent(Arrays.asList("1.2.840.10008.1.2", "1.2.840.10008.1.2.2", "1.2.840.10008.1.2.1"),
                settings.getDicomServicesSettings().getQueryRetrieveSettings().getTransferCapabilities());
        assertSameContent(Arrays.asList("1.2.840.10008.5.1.4.1.2.2.1", "1.2.840.10008.5.1.4.1.2.1.1"),
                settings.getDicomServicesSettings().getQueryRetrieveSettings().getSOPClass());

        // DICOM Storage settings
        assertTrue(settings.getDicomServicesSettings().getStorageSettings().isAutostart());
        assertEquals(6666, settings.getDicomServicesSettings().getStorageSettings().getPort());

        // Web server settings
        ServerSettings.WebServer web = settings.getWebServerSettings();
        assertTrue(web.isAutostart());
        assertEquals(8080, web.getPort());
        assertEquals(null, web.getAllowedOrigins());
    /*
        Map<String, String> modalityCFind = new HashMap<>();
        modalityCFind.put("find", "Study Root Query/Retrieve Info Model");
        modalityCFind.put("1.2.840.10008.5.1.4.1.2.1.1", "Patient Root Query/Retrieve Info Model");
        modalityCFind.put("1.2.840.10008.5.1.4.1.2.2.1", "Study Root Query/Retrieve Info Model");
        assertEquals(modalityCFind, settings.getModalityFind());
    */
        assertTrue(settings.getDicomServicesSettings().getMoveDestinations().isEmpty());
    }


    @Test
    public void testSave() throws IOException {
        Path target = Files.createTempFile("conf", ".xml");

        // Create default settings
        ServerSettings settings = ServerSettingsImpl.createDefault();

        assertTrue(settings instanceof ServerSettingsImpl);

        settings.getArchiveSettings().setNodeName("dicoogle17");
        settings.getDicomServicesSettings().getStorageSettings().setPort(6767);

        ServerSettingsManager.saveSettingsTo(settings, target);

        // read from file

        settings = ServerSettingsManager.loadSettingsAt(target);
        assertEquals("dicoogle17", settings.getArchiveSettings().getNodeName());
        assertEquals(6767, settings.getDicomServicesSettings().getStorageSettings().getPort());

        // clean up
        Files.delete(target);
    }

    @Test
    public void testMigrate() throws IOException {
        Path newconf = Files.createTempFile("conf", ".xml");

        // load legacy server settings
        ServerSettings settings = ServerSettingsManager.loadLegacySettingsAt(this.legacyConfig);
        assertTrue(settings instanceof LegacyServerSettings);

        // save as new
        ServerSettingsManager.saveSettingsTo(settings, newconf);

        // check new file
        settings = ServerSettingsManager.loadSettingsAt(newconf);
        assertTrue(settings instanceof ServerSettingsImpl);
        ServerSettings.Archive a = settings.getArchiveSettings();

        // assertions follow
        assertEquals("/opt/dicoogle/repository", a.getMainDirectory());
        assertEquals("/tmp", a.getWatchDirectory());
        assertEquals(97, a.getIndexerEffort());
        assertEquals("dicoogle-old", a.getNodeName());

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

        // clean up
        Files.delete(newconf);
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
