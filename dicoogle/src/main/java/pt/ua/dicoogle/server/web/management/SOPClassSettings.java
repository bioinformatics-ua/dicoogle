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
package pt.ua.dicoogle.server.web.management;

import org.dcm4che3.data.UID;
import pt.ua.dicoogle.core.settings.ServerSettingsManager;
import pt.ua.dicoogle.sdk.datastructs.AdditionalSOPClass;
import pt.ua.dicoogle.sdk.datastructs.AdditionalTransferSyntax;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;
import pt.ua.dicoogle.server.SOPList;
import pt.ua.dicoogle.server.TransfersStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Holds and provides information about the accepted SOP Classes and
 * Transfer Storages allowed within Dicoogle.
 */
public class SOPClassSettings {
    /**
     * Holds a pointer to the SOPList that holds the SOP Classes settings.
     */
    private SOPList sopList;

    /**
     * Holds the list of SOP Classes UIDs and their respective "regular" name.
     */
    private HashMap<String, String> sopClasses;
    /**
     * Holds the list of Transfer Storage UIDs and their respective "regular" name.
     */
    private HashMap<String, String> transferSettings;
    /**
     * Holds the list of Transfer Storage UIDs and their respective index on the TS object.
     */
    private HashMap<String, Integer> transferSettingsIndex;

    /**
     * Holds the current instance of this class.
     */
    private static SOPClassSettings instance;

    private SOPClassSettings() {
        sopList = SOPList.getInstance();

        sopClasses = new HashMap<String, String>();
        transferSettings = new HashMap<String, String>();
        transferSettingsIndex = new HashMap<String, Integer>();

        ServerSettings.DicomServices dicomServices = ServerSettingsManager.getSettings().getDicomServicesSettings();

        sopClasses.put(UID.BasicStudyContentNotification, "BasicStudyContentNotification (Retired)");
        sopClasses.put(UID.StoredPrintStorage, "StoredPrintStorage (Retired)");
        sopClasses.put(UID.HardcopyGrayscaleImageStorage, "HardcopyGrayscaleImageStorage (Retired)");
        sopClasses.put(UID.HardcopyColorImageStorage, "HardcopyColorImageStorage (Retired)");
        sopClasses.put(UID.ComputedRadiographyImageStorage, "ComputedRadiographyImageStorage");
        sopClasses.put(UID.DigitalXRayImageStorageForPresentation, "DigitalXRayImageStorageForPresentation");
        sopClasses.put(UID.DigitalXRayImageStorageForProcessing, "DigitalXRayImageStorageForProcessing");
        sopClasses.put(UID.DigitalMammographyXRayImageStorageForPresentation,
                "DigitalMammographyXRayImageStorageForPresentation");
        sopClasses.put(UID.DigitalMammographyXRayImageStorageForProcessing,
                "DigitalMammographyXRayImageStorageForProcessing");
        sopClasses.put(UID.DigitalIntraOralXRayImageStorageForPresentation,
                "DigitalIntraoralXRayImageStorageForPresentation");
        sopClasses.put(UID.DigitalIntraOralXRayImageStorageForProcessing,
                "DigitalIntraoralXRayImageStorageForProcessing");
        sopClasses.put(UID.StandaloneModalityLUTStorage, "StandaloneModalityLUTStorage (Retired)");
        sopClasses.put(UID.EncapsulatedPDFStorage, "EncapsulatedPDFStorage");
        sopClasses.put(UID.StandaloneVOILUTStorage, "StandaloneVOILUTStorage (Retired)");
        sopClasses.put(UID.GrayscaleSoftcopyPresentationStateStorage,
                "GrayscaleSoftcopyPresentationStateStorage");
        sopClasses.put(UID.ColorSoftcopyPresentationStateStorage, "ColorSoftcopyPresentationStateStorage");
        sopClasses.put(UID.PseudoColorSoftcopyPresentationStateStorage,
                "PseudoColorSoftcopyPresentationStateStorage");
        sopClasses.put(UID.BlendingSoftcopyPresentationStateStorage,
                "BlendingSoftcopyPresentationStateStorage");
        sopClasses.put(UID.XRayAngiographicImageStorage, "XRayAngiographicImageStorage");
        sopClasses.put(UID.EnhancedXAImageStorage, "EnhancedXAImageStorage");
        sopClasses.put(UID.XRayRadiofluoroscopicImageStorage, "XRayRadiofluoroscopicImageStorage");
        sopClasses.put(UID.EnhancedXRFImageStorage, "EnhancedXRFImageStorage");
        sopClasses.put(UID.XRayAngiographicBiPlaneImageStorage, "XRayAngiographicBiPlaneImageStorage (Retired)");
        sopClasses.put(UID.PositronEmissionTomographyImageStorage, "PositronEmissionTomographyImageStorage");
        sopClasses.put(UID.StandalonePETCurveStorage, "StandalonePETCurveStorage (Retired)");
        sopClasses.put(UID.CTImageStorage, "CTImageStorage");
        sopClasses.put(UID.EnhancedCTImageStorage, "EnhancedCTImageStorage");
        sopClasses.put(UID.NuclearMedicineImageStorage, "NuclearMedicineImageStorage");
        sopClasses.put(UID.UltrasoundMultiFrameImageStorageRetired, "UltrasoundMultiframeImageStorage (Retired)");
        sopClasses.put(UID.UltrasoundMultiFrameImageStorage, "UltrasoundMultiframeImageStorage");
        sopClasses.put(UID.MRImageStorage, "MRImageStorage");
        sopClasses.put(UID.EnhancedMRImageStorage, "EnhancedMRImageStorage");
        sopClasses.put(UID.MRSpectroscopyStorage, "MRSpectroscopyStorage");
        sopClasses.put(UID.RTImageStorage, "RTImageStorage");
        sopClasses.put(UID.RTDoseStorage, "RTDoseStorage");
        sopClasses.put(UID.RTStructureSetStorage, "RTStructureSetStorage");
        sopClasses.put(UID.RTBeamsTreatmentRecordStorage, "RTBeamsTreatmentRecordStorage");
        sopClasses.put(UID.RTPlanStorage, "RTPlanStorage");
        sopClasses.put(UID.RTBrachyTreatmentRecordStorage, "RTBrachyTreatmentRecordStorage");
        sopClasses.put(UID.RTTreatmentSummaryRecordStorage, "RTTreatmentSummaryRecordStorage");
        sopClasses.put(UID.NuclearMedicineImageStorageRetired, "NuclearMedicineImageStorage (Retired)");
        sopClasses.put(UID.UltrasoundImageStorageRetired, "UltrasoundImageStorage (Retired)");
        sopClasses.put(UID.UltrasoundImageStorage, "UltrasoundImageStorage");
        sopClasses.put(UID.RawDataStorage, "RawDataStorage");
        sopClasses.put(UID.SpatialRegistrationStorage, "SpatialRegistrationStorage");
        sopClasses.put(UID.SpatialFiducialsStorage, "SpatialFiducialsStorage");
        sopClasses.put(UID.RealWorldValueMappingStorage, "RealWorldValueMappingStorage");
        sopClasses.put(UID.SecondaryCaptureImageStorage, "SecondaryCaptureImageStorage");
        sopClasses.put(UID.MultiFrameSingleBitSecondaryCaptureImageStorage,
                "MultiframeSingleBitSecondaryCaptureImageStorage");
        sopClasses.put(UID.MultiFrameGrayscaleByteSecondaryCaptureImageStorage,
                "MultiframeGrayscaleByteSecondaryCaptureImageStorage");
        sopClasses.put(UID.MultiFrameGrayscaleWordSecondaryCaptureImageStorage,
                "MultiframeGrayscaleWordSecondaryCaptureImageStorage");
        sopClasses.put(UID.MultiFrameTrueColorSecondaryCaptureImageStorage,
                "MultiframeTrueColorSecondaryCaptureImageStorage");
        sopClasses.put(UID.VLImageStorageTrial, "VLImageStorage (Retired)");
        sopClasses.put(UID.VLEndoscopicImageStorage, "VLEndoscopicImageStorage");
        sopClasses.put(UID.VideoEndoscopicImageStorage, "VideoEndoscopicImageStorage");
        sopClasses.put(UID.VLMicroscopicImageStorage, "VLMicroscopicImageStorage");
        sopClasses.put(UID.VideoMicroscopicImageStorage, "VideoMicroscopicImageStorage");
        sopClasses.put(UID.VLSlideCoordinatesMicroscopicImageStorage, "VLSlideCoordinatesMicroscopicImageStorage");
        sopClasses.put(UID.VLPhotographicImageStorage, "VLPhotographicImageStorage");
        sopClasses.put(UID.VideoPhotographicImageStorage, "VideoPhotographicImageStorage");
        sopClasses.put(UID.OphthalmicPhotography8BitImageStorage, "OphthalmicPhotography8BitImageStorage");
        sopClasses.put(UID.OphthalmicPhotography16BitImageStorage, "OphthalmicPhotography16BitImageStorage");
        sopClasses.put(UID.StereometricRelationshipStorage, "StereometricRelationshipStorage");
        sopClasses.put(UID.VLMultiFrameImageStorageTrial, "VLMultiframeImageStorage (Retired)");
        sopClasses.put(UID.StandaloneOverlayStorage, "StandaloneOverlayStorage (Retired)");
        sopClasses.put(UID.BasicTextSRStorage, "BasicTextSR");
        sopClasses.put(UID.EnhancedSRStorage, "EnhancedSR");
        sopClasses.put(UID.ComprehensiveSRStorage, "ComprehensiveSR");
        sopClasses.put(UID.ProcedureLogStorage, "ProcedureLogStorage");
        sopClasses.put(UID.MammographyCADSRStorage, "MammographyCADSR");
        sopClasses.put(UID.KeyObjectSelectionDocumentStorage, "KeyObjectSelectionDocument");
        sopClasses.put(UID.ChestCADSRStorage, "ChestCADSR");
        sopClasses.put(UID.StandaloneCurveStorage, "StandaloneCurveStorage (Retired)");
        sopClasses.put(UID.GeneralECGWaveformStorage, "GeneralECGWaveformStorage");
        sopClasses.put(UID.AmbulatoryECGWaveformStorage, "AmbulatoryECGWaveformStorage");
        sopClasses.put(UID.HemodynamicWaveformStorage, "HemodynamicWaveformStorage");
        sopClasses.put(UID.CardiacElectrophysiologyWaveformStorage, "CardiacElectrophysiologyWaveformStorage");
        sopClasses.put(UID.BasicVoiceAudioWaveformStorage, "BasicVoiceAudioWaveformStorage");
        sopClasses.put(UID.HangingProtocolStorage, "HangingProtocolStorage");
        sopClasses.put(UID.VLWholeSlideMicroscopyImageStorage, "VLWholeSlideMicroscopyImageStorage");
        sopClasses.put(UID.BreastTomosynthesisImageStorage, "BreastTomosynthesisImageStorage");
        sopClasses.put(UID.XRayRadiationDoseSRStorage, "XRayRadiationDoseSRStorage");
        // sopClasses: put the UIDs and their alias as it is in confs/server.xml's <additional-sop-classes>
        // Get setting's additional SOPs (uid and alias) not already existing in sopClasses list
        Collection<AdditionalSOPClass> additionalSOPClasses = dicomServices.getAdditionalSOPClasses();
        additionalSOPClasses = additionalSOPClasses.stream()
                .filter(additionalSOPClass -> !sopClasses.containsKey(additionalSOPClass.getUid()))
                .collect(Collectors.toList());
        // Add additional SOPs to hashMap sopClasses
        additionalSOPClasses.forEach(elem -> sopClasses.put(elem.getUid(), elem.getAlias()));


        transferSettings.put(UID.ImplicitVRLittleEndian, "Implicit VR Little Endian");
        transferSettings.put(UID.ExplicitVRLittleEndian, "Explicit VR Little Endian");
        transferSettings.put(UID.DeflatedExplicitVRLittleEndian, "Deflated Explicit VR Little Endian");
        transferSettings.put(UID.ExplicitVRBigEndian, "Explicit VR Big Endian");
        transferSettings.put(UID.JPEGLossless, "JPEG Lossless");
        transferSettings.put(UID.JPEGLSLossless, "JPEG-LS Lossless");
        transferSettings.put(UID.JPEGLosslessSV1, "JPEG Lossless, Non-Hierarchical (Process 14), First Order Prediction");
        transferSettings.put(UID.JPEG2000Lossless, "JPEG2000 Lossless Only");
        transferSettings.put(UID.JPEGBaseline8Bit, "JPEG Baseline (Process 1)");
        transferSettings.put(UID.JPEGExtended12Bit, "JPEG Extended (Process 2 & 4)");
        transferSettings.put(UID.JPEGLSNearLossless, "JPEG-LS Near Lossless");
        transferSettings.put(UID.JPEG2000, "JPEG 2000");
        transferSettings.put(UID.RLELossless, "RLE Lossless");
        transferSettings.put(UID.MPEG2MPML, "MPEG2 Main Profile / Main Level");
        // transferSettings: put the UIDs and their alias as it is in confs/server.xml's <additional-transfer-syntaxes>
        Collection<AdditionalTransferSyntax> additionalTransferSyntaxes = dicomServices.getAdditionalTransferSyntaxes();
        // Get additional TSs not already present in the hardcoded list (transferSettings)
        additionalTransferSyntaxes = additionalTransferSyntaxes.stream()
                .filter(additionalTransferSyntax -> !transferSettings.containsKey(additionalTransferSyntax.getUid()))
                .collect(Collectors.toList());
        // Add all
        additionalTransferSyntaxes.forEach(elem -> transferSettings.put(elem.getUid(), elem.getAlias()));


        transferSettingsIndex.put(UID.ImplicitVRLittleEndian, 0);
        transferSettingsIndex.put(UID.ExplicitVRLittleEndian, 1);
        transferSettingsIndex.put(UID.DeflatedExplicitVRLittleEndian, 2);
        transferSettingsIndex.put(UID.ExplicitVRBigEndian, 3);
        transferSettingsIndex.put(UID.JPEGLossless, 4);
        transferSettingsIndex.put(UID.JPEGLSLossless, 5);
        transferSettingsIndex.put(UID.JPEGLosslessSV1, 6);
        transferSettingsIndex.put(UID.JPEG2000Lossless, 7);
        transferSettingsIndex.put(UID.JPEGBaseline8Bit, 8);
        transferSettingsIndex.put(UID.JPEGExtended12Bit, 9);
        transferSettingsIndex.put(UID.JPEGLSNearLossless, 10);
        transferSettingsIndex.put(UID.JPEG2000, 11);
        transferSettingsIndex.put(UID.RLELossless, 12);
        transferSettingsIndex.put(UID.MPEG2MPML, 13);
        // transferSettingsIndex: put the TSs in some adequate order and sequential index
        int index = 13;
        for (AdditionalTransferSyntax elem : additionalTransferSyntaxes) {
            transferSettingsIndex.put(elem.getUid(), ++index);
        }
    }

    /**
     * Returns the SOP Classes UID and "regular" name translation list.
     *
     * @return the SOP Classes UID and "regular" name translation list.
     */
    public HashMap<String, String> getSOPClasses() {
        return sopClasses;
    }

    /**
     * Returns the TransferStorage UID and "regular" name translation list.
     *
     * @return the TransferStorage UID and "regular" name translation list.
     */
    public HashMap<String, String> getTransferSettings() {
        return transferSettings;
    }

    /**
     * Returns the TransferStorage UID and their index on the TS object translation list.
     *
     * @return the TransferStorage UID and their index on the TS object translation list.
     */
    public HashMap<String, Integer> getTransferSettingsIndex() {
        return transferSettingsIndex;
    }

    /**
     * Returns the current instance of this class.
     *
     * @return the current instance of this class.
     */
    public static synchronized SOPClassSettings getInstance() {
        if (instance == null)
            instance = new SOPClassSettings();

        return instance;
    }

    /**
     * Returns the TransferStorage settings fo a SOP Class UID.
     *
     * @param sopClassUID the SOP Class UID.
     * @return the TransferStorage settings fo a SOP Class UID.
     */
    public synchronized TransfersStorage getSOPClassSettings(String sopClassUID) {
        return sopList.getTS(sopClassUID);
    }

    /**
     * Defines all the settings values related to all SOP Classes UIDs.
     *
     * @param accepted if this SOP Class is accepted. Can be null and if so, the target accepted value will remain the same.
     * @param allowedTransStore a HashMap indicating which TransferStore UIDs are accepted.
     */
    public synchronized void setAllSOPClassesSettings(Boolean accepted, HashMap<String, Boolean> allowedTransStore) {
        // for all the sop classes
        List<String> keys = sopList.getKeys();
        for (int i = 0; i < keys.size(); i++) {
            // get the sop class uid
            String sopClassUID = (String) keys.get(i);

            // apply the settings
            setSOPClassSettings(sopClassUID, accepted, allowedTransStore);
        }
    }

    /**
     * Defines all the settings values related to a SOP Class UID.
     *
     * @param sopClassUID the SOP Class UID.
     * @param accepted if this SOP Class is accepted. Can be null and if so, the target accepted value will remain the same.
     * @param allowedTransStore a HashMap indicating which TransferStore UIDs are accepted.
     */
    public synchronized void setSOPClassSettings(String sopClassUID, Boolean accepted,
            HashMap<String, Boolean> allowedTransStore) {
        // get the transfer storage object for this sop class uid
        TransfersStorage trans = sopList.getTS(sopClassUID);

        // apply the settings
        setTransferStorageSettings(trans, accepted, allowedTransStore);
    }

    /**
     * Defines all the settings values related to a TransferStorage object.
     *
     * @param trans the TransferStorage object.
     * @param accepted if this SOP Class is accepted. Can be null and if so, the target accepted value will remain the same.
     * @param allowedTransStore a HashMap indicating which TransferStore UIDs are accepted.
     */
    public synchronized void setTransferStorageSettings(TransfersStorage trans, Boolean accepted,
            HashMap<String, Boolean> allowedTransStore) {
        // accept this sop class, if necessary
        if (accepted != null)
            trans.setAccepted(accepted.booleanValue());
        // accept all the transfer storage settings
        for (String transStoreUID : transferSettings.keySet()) {
            // skip this transfer store uid if it is not referenced on the allowedTransStore // FIXME is this right, or should non-defined entries be set as
            // false?!?
            if (!allowedTransStore.containsKey(transStoreUID))
                continue;

            trans.setTS(allowedTransStore.get(transStoreUID).booleanValue(),
                    transferSettingsIndex.get(transStoreUID).intValue());
        }
    }
}
