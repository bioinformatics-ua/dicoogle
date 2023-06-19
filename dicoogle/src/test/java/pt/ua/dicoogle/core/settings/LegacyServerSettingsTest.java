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

import org.junit.ComparisonFailure;
import org.junit.Test;
import pt.ua.dicoogle.sdk.datastructs.MoveDestination;
import pt.ua.dicoogle.sdk.datastructs.SOPClass;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;
import pt.ua.dicoogle.server.SOPList;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class LegacyServerSettingsTest {

    private final URL legacyConfig = this.getClass().getResource("test-config-legacy.xml");

    @Test
    public void test() throws IOException {
        // read the settings from our test config file
        ServerSettings settings = ServerSettingsManager.loadLegacySettingsAt(this.legacyConfig);

        assertTrue(settings instanceof LegacyServerSettings);

        ServerSettings.Archive a = settings.getArchiveSettings();

        // assertions follow
        assertEquals("/opt/dicoogle/repository", a.getMainDirectory());
        assertEquals("/tmp", a.getWatchDirectory());
        assertEquals(97, a.getIndexerEffort());
        assertEquals("dicoogle-old", a.getNodeName());
        assertEquals(true, a.isEncryptUsersFile());

        assertEquals("TEST-STORAGE", settings.getDicomServicesSettings().getAETitle());

        // QR settings
        assertEquals(106, settings.getDicomServicesSettings().getQueryRetrieveSettings().getPort());
        assertSameContent(Collections.singleton("any"),
                settings.getDicomServicesSettings().getAllowedLocalInterfaces());
        assertSameContent(Collections.singleton("any"), settings.getDicomServicesSettings().getAllowedHostnames());
        assertEquals(3, settings.getDicomServicesSettings().getQueryRetrieveSettings().getRspDelay());
        assertEquals(50, settings.getDicomServicesSettings().getQueryRetrieveSettings().getDIMSERspTimeout());
        assertEquals(50, settings.getDicomServicesSettings().getQueryRetrieveSettings().getIdleTimeout());
        assertEquals(50, settings.getDicomServicesSettings().getQueryRetrieveSettings().getAcceptTimeout());
        assertEquals(50, settings.getDicomServicesSettings().getQueryRetrieveSettings().getConnectionTimeout());
        // assertEquals("1.2.840.10008.5.1.4.1.2.1.1", settings.getSOPClasses());
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

        List<MoveDestination> destinations =
                Arrays.asList(new MoveDestination("ADESTINATION", "192.168.42.42", 4444, true, "Our test destination"));
        assertSameContent(destinations, settings.getDicomServicesSettings().getMoveDestinations());
    }

    private static void assertSameContent(Collection<?> o1, Collection<?> o2) {
        for (Object o : o1) {
            if (!o2.contains(o)) {
                throw new ComparisonFailure("Collections do not have the same content", String.valueOf(o1),
                        String.valueOf(o2));
            }
        }
        for (Object o : o2) {
            if (!o1.contains(o)) {
                throw new ComparisonFailure("Collections do not have the same content", String.valueOf(o1),
                        String.valueOf(o2));
            }
        }
    }

    @Test
    public void testMigrate() throws IOException {
        // clean up SOP list from past tests
        SOPList.getInstance().reset();

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
        assertSameContent(Collections.singleton("any"),
                settings.getDicomServicesSettings().getAllowedLocalInterfaces());
        assertSameContent(Collections.singleton("any"), settings.getDicomServicesSettings().getAllowedHostnames());
        assertEquals(3, settings.getDicomServicesSettings().getQueryRetrieveSettings().getRspDelay());
        assertEquals(50, settings.getDicomServicesSettings().getQueryRetrieveSettings().getDIMSERspTimeout());
        assertEquals(50, settings.getDicomServicesSettings().getQueryRetrieveSettings().getIdleTimeout());
        assertEquals(50, settings.getDicomServicesSettings().getQueryRetrieveSettings().getAcceptTimeout());
        assertEquals(50, settings.getDicomServicesSettings().getQueryRetrieveSettings().getConnectionTimeout());
        // assertEquals("1.2.840.10008.5.1.4.1.2.1.1", settings.getSOPClasses());
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


        // SOP Classes

        Collection<SOPClass> sopClasses = Arrays.asList(
                new SOPClass("1.2.840.10008.5.1.4.1.1.88.40",
                        Arrays.asList("1.2.840.10008.1.2", "1.2.840.10008.1.2.1", "1.2.840.10008.1.2.4.80",
                                "1.2.840.10008.1.2.4.50")),
                new SOPClass("1.2.840.10008.5.1.4.1.1.77.1.1",
                        Arrays.asList("1.2.840.10008.1.2", "1.2.840.10008.1.2.1", "1.2.840.10008.1.2.4.80",
                                "1.2.840.10008.1.2.4.50")),
                new SOPClass("1.2.840.10008.5.1.1.30", Arrays.asList("1.2.840.10008.1.2", "1.2.840.10008.1.2.1",
                        "1.2.840.10008.1.2.4.80", "1.2.840.10008.1.2.4.50")));

        Collection<SOPClass> sopClassesFromSettings = settings.getDicomServicesSettings().getSOPClasses().stream()
                .filter(c -> !c.getTransferSyntaxes().isEmpty()).collect(Collectors.toList());

        assertSameContent(sopClasses, sopClassesFromSettings);

        // clean up
        Files.delete(newconf);
    }
}
