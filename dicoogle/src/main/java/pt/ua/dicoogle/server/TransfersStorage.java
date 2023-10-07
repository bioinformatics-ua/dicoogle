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
package pt.ua.dicoogle.server;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.dcm4che2.data.TransferSyntax;
import org.dcm4che2.data.UID;
import pt.ua.dicoogle.core.settings.ServerSettingsManager;
import pt.ua.dicoogle.sdk.datastructs.AdditionalTransferSyntax;
import pt.ua.dicoogle.server.web.management.SOPClassSettings;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Marco Pereira
 * @author Frederico Silva
 */
public class TransfersStorage {
    private boolean accepted;
    private boolean[] TS;

    /*  [0] ImplicitVRLittleEndian
     *  [1] ExplicitVRLittleEndian
     *  [2] DeflatedExplicitVRLittleEndian
     *  [3] ExplicitVRBigEndian
     *  [4] JPEGLossless
     *  [5] JPEGLSLossless
     *  [6] JPEGLosslessNonHierarchical14
     *  [7] JPEG2000LosslessOnly
     *  [8] JPEGBaseline1
     *  [9] JPEGExtended24
     * [10] JPEGLSLossyNearLossless
     * [11] JPEG2000
     * [12] RLELossless
     * [13] MPEG2
     * [14-] Additional transfer syntaxes (added in target/confs/server.xml)
     */

    private static Map<Integer, String> globalTransferMap;
    private static Map<String, String> globalTransferUIDsMap;

    public static Map<Integer, String> getGlobalTransferMap() {
        return globalTransferMap;
    }

    public static Map<String, String> getGlobalTransferUIDsMap() {
        return globalTransferUIDsMap;
    }

    private static final Map<Integer, String> aMap = new HashMap<>();
    private static final Map<String, String> uidsNameMapping = new HashMap<>();
    // Map reverse of uidsNameMapping. This introduces some redundancy with uidsNameMapping, for its double direction,
    // but for now the HashMap is not to be deprecated for the reason of possible compatibility breaks.
    private static BidiMap namesUidMapping;
    static {

        aMap.put(0, "ImplicitVRLittleEndian");
        aMap.put(1, "ExplicitVRLittleEndian");
        aMap.put(2, "DeflatedExplicitVRLittleEndian");
        aMap.put(3, "ExplicitVRBigEndian");
        aMap.put(4, "JPEGLossless");
        aMap.put(5, "JPEGLSLossless");
        aMap.put(6, "JPEGLosslessNonHierarchical14");
        aMap.put(7, "JPEG2000LosslessOnly");
        aMap.put(8, "JPEGBaseline1");
        aMap.put(9, "JPEGExtended24");
        aMap.put(10, "JPEGLSLossyNearLossless");
        aMap.put(11, "JPEG2000");
        aMap.put(12, "RLELossless");
        aMap.put(13, "MPEG2");

        globalTransferMap = Collections.unmodifiableMap(aMap);

        uidsNameMapping.put(UID.ImplicitVRLittleEndian, "ImplicitVRLittleEndian");
        uidsNameMapping.put(UID.ExplicitVRLittleEndian, "ExplicitVRLittleEndian");
        uidsNameMapping.put(UID.DeflatedExplicitVRLittleEndian, "DeflatedExplicitVRLittleEndian");
        uidsNameMapping.put(UID.ExplicitVRBigEndian, "ExplicitVRBigEndian");
        uidsNameMapping.put(UID.JPEGLossless, "JPEGLossless");
        uidsNameMapping.put(UID.JPEGLSLossless, "JPEGLSLossless");
        uidsNameMapping.put(UID.JPEGLosslessNonHierarchical14, "JPEGLosslessNonHierarchical14");
        uidsNameMapping.put(UID.JPEG2000LosslessOnly, "JPEG2000LosslessOnly");
        uidsNameMapping.put(UID.JPEGBaseline1, "JPEGBaseline1");
        uidsNameMapping.put(UID.JPEGExtended24, "JPEGExtended24");
        uidsNameMapping.put(UID.JPEGLSLossyNearLossless, "JPEGLSLossyNearLossless");
        uidsNameMapping.put(UID.JPEG2000, "JPEG2000");
        uidsNameMapping.put(UID.RLELossless, "RLELossless");
        uidsNameMapping.put(UID.MPEG2, "MPEG2");

        globalTransferUIDsMap = Collections.unmodifiableMap(uidsNameMapping);
        namesUidMapping = new DualHashBidiMap(uidsNameMapping);

    }

    public TransfersStorage() {
        accepted = false;
        // objects pre-completion and post-completion have different number of TSs
        TS = new boolean[globalTransferMap == null ? aMap.size() : globalTransferMap.size()];
    }

    public void setAccepted(boolean status) {
        accepted = status;
    }

    public boolean getAccepted() {
        return accepted;
    }

    public int setTS(boolean[] status) {
        int i;

        if (status.length != TS.length) {
            return -1;
        }

        for (i = 0; i < status.length; i++) {
            TS[i] = status[i];
        }

        return 0;
    }

    public int setTS(boolean status, int index) {
        int i;

        if (index < 0 || index > TS.length - 1) {
            return -1;
        }
        TS[index] = status;

        return 0;
    }

    public boolean[] getTS() {
        return TS;
    }

    public void setDefaultSettings() {
        TS[0] = true;
        TS[1] = true;
        TS[4] = true;
        TS[5] = true;
        TS[8] = true;
        accepted = true;

    }

    public String[] getVerboseTS() {

        int i, count = 0;

        String[] return_value = new String[0];
        for (i = 0; i < TS.length; i++) {
            if (TS[i]) {
                count++;
            }
        }
        if (count > 0) {
            i = 0;
            return_value = new String[count];
            for (int j = 0; j < TS.length; j++) {
                if (TS[j]) {
                    return_value[i] = (String) (namesUidMapping.getKey(globalTransferMap.get(j)));
                    i++;
                }
            }
        }
        return return_value;
    }

    public static String convertTsNameToUID(String name) {
        return namesUidMapping.getKey(name).toString();
    }

    public List<String> asList() {
        return Arrays.asList(this.getVerboseTS());
    }

    /** Read the active server settings and record additional transfer syntaxes. */
    public static void completeList() {

        // Get additional transfer syntaxes (not present in the hardcoded list)
        Collection<AdditionalTransferSyntax> additionalTransferSyntaxes =
                ServerSettingsManager.getSettings().getDicomServicesSettings().getAdditionalTransferSyntaxes();
        additionalTransferSyntaxes =
                additionalTransferSyntaxes
                        .stream().filter(additionalTransferSyntax -> SOPClassSettings.getInstance()
                                .getTransferSettings().containsKey(additionalTransferSyntax.getUid()))
                        .collect(Collectors.toList());
        // --
        // Extras (indexed AdditionalTransferSyntaxes)
        int index = aMap.size();
        for (AdditionalTransferSyntax elem : additionalTransferSyntaxes) {
            aMap.put(index++, elem.getAlias());
        }

        // Extras (AdditionalTransferSyntaxes with aliases)
        additionalTransferSyntaxes.forEach(elem -> {
            // Collect the additional transfer syntaxes statically
            TransferSyntax.add(Objects.requireNonNull(elem.toTransferSyntax()));
            uidsNameMapping.put(elem.getUid(), elem.getAlias());
        });

        // Assign to globalTS the map vars, locking the list
        globalTransferMap = Collections.unmodifiableMap(aMap);
        globalTransferUIDsMap = Collections.unmodifiableMap(uidsNameMapping);
        namesUidMapping = new DualHashBidiMap(uidsNameMapping);

    }
}
