package pt.ua.dicoogle.server.web.management;

import org.dcm4che2.data.UID;
import pt.ua.dicoogle.server.SOPList;
import pt.ua.dicoogle.server.TransfersStorage;

import java.util.HashMap;
import java.util.List;

/**
 * Holds and provides information about the accepted SOP Classes and
 * Transfer Storages allowed within Dicoogle.
 */
public class SOPClassSettings
{
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

    private SOPClassSettings()
    {
        sopList = SOPList.getInstance();

        sopClasses = new HashMap<String, String>();
        transferSettings = new HashMap<String, String>();
        transferSettingsIndex = new HashMap<String, Integer>();

        sopClasses.put(UID.BasicStudyContentNotificationSOPClassRetired, "BasicStudyContentNotification (Retired)");
        sopClasses.put(UID.StoredPrintStorageSOPClassRetired, "StoredPrintStorage (Retired)");
        sopClasses.put(UID.HardcopyGrayscaleImageStorageSOPClassRetired, "HardcopyGrayscaleImageStorage (Retired)");
        sopClasses.put(UID.HardcopyColorImageStorageSOPClassRetired, "HardcopyColorImageStorage (Retired)");
        sopClasses.put(UID.ComputedRadiographyImageStorage, "ComputedRadiographyImageStorage");
        sopClasses.put(UID.DigitalXRayImageStorageForPresentation, "DigitalXRayImageStorageForPresentation");
        sopClasses.put(UID.DigitalXRayImageStorageForProcessing, "DigitalXRayImageStorageForProcessing");
        sopClasses.put(UID.DigitalMammographyXRayImageStorageForPresentation, "DigitalMammographyXRayImageStorageForPresentation");
        sopClasses.put(UID.DigitalMammographyXRayImageStorageForProcessing, "DigitalMammographyXRayImageStorageForProcessing");
        sopClasses.put(UID.DigitalIntraOralXRayImageStorageForPresentation, "DigitalIntraoralXRayImageStorageForPresentation");
        sopClasses.put(UID.DigitalIntraOralXRayImageStorageForProcessing, "DigitalIntraoralXRayImageStorageForProcessing");
        sopClasses.put(UID.StandaloneModalityLUTStorageRetired, "StandaloneModalityLUTStorage (Retired)");
        sopClasses.put(UID.EncapsulatedPDFStorage, "EncapsulatedPDFStorage");
        sopClasses.put(UID.StandaloneVOILUTStorageRetired, "StandaloneVOILUTStorage (Retired)");
        sopClasses.put(UID.GrayscaleSoftcopyPresentationStateStorageSOPClass, "GrayscaleSoftcopyPresentationStateStorage");
        sopClasses.put(UID.ColorSoftcopyPresentationStateStorageSOPClass, "ColorSoftcopyPresentationStateStorage");
        sopClasses.put(UID.PseudoColorSoftcopyPresentationStateStorageSOPClass, "PseudoColorSoftcopyPresentationStateStorage");
        sopClasses.put(UID.BlendingSoftcopyPresentationStateStorageSOPClass, "BlendingSoftcopyPresentationStateStorage");
        sopClasses.put(UID.XRayAngiographicImageStorage, "XRayAngiographicImageStorage");
        sopClasses.put(UID.EnhancedXAImageStorage, "EnhancedXAImageStorage");
        sopClasses.put(UID.XRayRadiofluoroscopicImageStorage, "XRayRadiofluoroscopicImageStorage");
        sopClasses.put(UID.EnhancedXRFImageStorage, "EnhancedXRFImageStorage");
        sopClasses.put(UID.XRayAngiographicBiPlaneImageStorageRetired, "XRayAngiographicBiPlaneImageStorage (Retired)");
        sopClasses.put(UID.PositronEmissionTomographyImageStorage, "PositronEmissionTomographyImageStorage");
        sopClasses.put(UID.StandalonePETCurveStorageRetired, "StandalonePETCurveStorage (Retired)");
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
        sopClasses.put(UID.MultiFrameSingleBitSecondaryCaptureImageStorage, "MultiframeSingleBitSecondaryCaptureImageStorage");
        sopClasses.put(UID.MultiFrameGrayscaleByteSecondaryCaptureImageStorage, "MultiframeGrayscaleByteSecondaryCaptureImageStorage");
        sopClasses.put(UID.MultiFrameGrayscaleWordSecondaryCaptureImageStorage, "MultiframeGrayscaleWordSecondaryCaptureImageStorage");
        sopClasses.put(UID.MultiFrameTrueColorSecondaryCaptureImageStorage, "MultiframeTrueColorSecondaryCaptureImageStorage");
        sopClasses.put(UID.VLImageStorageTrialRetired, "VLImageStorage (Retired)");
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
        sopClasses.put(UID.VLMultiFrameImageStorageTrialRetired, "VLMultiframeImageStorage (Retired)");
        sopClasses.put(UID.StandaloneOverlayStorageRetired, "StandaloneOverlayStorage (Retired)");
        sopClasses.put(UID.BasicTextSRStorage, "BasicTextSR");
        sopClasses.put(UID.EnhancedSRStorage, "EnhancedSR");
        sopClasses.put(UID.ComprehensiveSRStorage, "ComprehensiveSR");
        sopClasses.put(UID.ProcedureLogStorage, "ProcedureLogStorage");
        sopClasses.put(UID.MammographyCADSRStorage, "MammographyCADSR");
        sopClasses.put(UID.KeyObjectSelectionDocumentStorage, "KeyObjectSelectionDocument");
        sopClasses.put(UID.ChestCADSRStorage,  "ChestCADSR");
        sopClasses.put(UID.StandaloneCurveStorageRetired, "StandaloneCurveStorage (Retired)");
        sopClasses.put(UID.GeneralECGWaveformStorage, "GeneralECGWaveformStorage");
        sopClasses.put(UID.AmbulatoryECGWaveformStorage,  "AmbulatoryECGWaveformStorage");
        sopClasses.put(UID.HemodynamicWaveformStorage, "HemodynamicWaveformStorage");
        sopClasses.put(UID.CardiacElectrophysiologyWaveformStorage, "CardiacElectrophysiologyWaveformStorage");
        sopClasses.put(UID.BasicVoiceAudioWaveformStorage, "BasicVoiceAudioWaveformStorage");
        sopClasses.put(UID.HangingProtocolStorage, "HangingProtocolStorage");
        sopClasses.put(UID.SiemensCSANonImageStorage, "SiemensCSANonImageStorage");
sopClasses.put(UID.VLWholeSlideMicroscopyImageStorage, "VLWholeSlideMicroscopyImageStorage");
sopClasses.put(UID.BreastTomosynthesisImageStorage, "BreastTomosynthesisImageStorage");

        transferSettings.put(UID.ImplicitVRLittleEndian, "ImplicitVRLittleEndian");
        transferSettings.put(UID.ExplicitVRLittleEndian, "ExplicitVRLittleEndian");
        transferSettings.put(UID.DeflatedExplicitVRLittleEndian, "DeflatedExplicitVRLittleEndian");
        transferSettings.put(UID.ExplicitVRBigEndian, "ExplicitVRBigEndian");
        transferSettings.put(UID.JPEGLossless, "JPEG Lossless");
        transferSettings.put(UID.JPEGLSLossless, "JPEG Lossless LS");
        transferSettings.put(UID.JPEGLosslessNonHierarchical14, "JPEG Lossless, Non-Hierarchical (Process 14) ");
        transferSettings.put(UID.JPEG2000LosslessOnly, "JPEG2000 Lossless Only");
        transferSettings.put(UID.JPEGBaseline1, "JPEG Baseline 1");
        transferSettings.put(UID.JPEGExtended24, "JPEG Extended (Process 2 & 4)");
        transferSettings.put(UID.JPEGLSLossyNearLossless, "JPEG LS Lossy Near Lossless");
        transferSettings.put(UID.JPEG2000, "JPEG2000");
        transferSettings.put(UID.RLELossless, "RLE Lossless");
        transferSettings.put(UID.MPEG2, "MPEG2");

        transferSettingsIndex.put(UID.ImplicitVRLittleEndian, 0);
        transferSettingsIndex.put(UID.ExplicitVRLittleEndian, 1);
        transferSettingsIndex.put(UID.DeflatedExplicitVRLittleEndian, 2);
        transferSettingsIndex.put(UID.ExplicitVRBigEndian, 3);
        transferSettingsIndex.put(UID.JPEGLossless, 4);
        transferSettingsIndex.put(UID.JPEGLSLossless, 5);
        transferSettingsIndex.put(UID.JPEGLosslessNonHierarchical14, 6);
        transferSettingsIndex.put(UID.JPEG2000LosslessOnly, 7);
        transferSettingsIndex.put(UID.JPEGBaseline1, 8);
        transferSettingsIndex.put(UID.JPEGExtended24, 9);
        transferSettingsIndex.put(UID.JPEGLSLossyNearLossless, 10);
        transferSettingsIndex.put(UID.JPEG2000, 11);
        transferSettingsIndex.put(UID.RLELossless, 12);
        transferSettingsIndex.put(UID.MPEG2, 13);
    }

    /**
     * Returns the SOP Classes UID and "regular" name translation list.
     *
     * @return the SOP Classes UID and "regular" name translation list.
     */
    public HashMap<String, String> getSOPClasses()
    {
        return sopClasses;
    }

    /**
     * Returns the TransferStorage UID and "regular" name translation list.
     *
     * @return the TransferStorage UID and "regular" name translation list.
     */
    public HashMap<String, String> getTransferSettings()
    {
        return transferSettings;
    }

    /**
     * Returns the TransferStorage UID and their index on the TS object translation list.
     *
     * @return the TransferStorage UID and their index on the TS object translation list.
     */
    public HashMap<String, Integer> getTransferSettingsIndex()
    {
        return transferSettingsIndex;
    }

    /**
     * Returns the current instance of this class.
     *
     * @return the current instance of this class.
     */
    public static synchronized SOPClassSettings getInstance()
    {
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
    public synchronized TransfersStorage getSOPClassSettings(String sopClassUID)
    {
        return sopList.getTS(sopClassUID);
    }

    /**
     * Defines all the settings values related to all SOP Classes UIDs.
     *
     * @param accepted if this SOP Class is accepted. Can be null and if so, the target accepted value will remain the same.
     * @param allowedTransStore a HashMap indicating which TransferStore UIDs are accepted.
     */
    public synchronized void setAllSOPClassesSettings(Boolean accepted, HashMap<String, Boolean> allowedTransStore)
    {
        // for all the sop classes
        List keys = sopList.getKeys();
        for (int i = 0; i < keys.size(); i++)
        {
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
    public synchronized void setSOPClassSettings(String sopClassUID, Boolean accepted, HashMap<String, Boolean> allowedTransStore)
    {
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
    public synchronized void setTransferStorageSettings(TransfersStorage trans, Boolean accepted, HashMap<String, Boolean> allowedTransStore)
    {
        // accept this sop class, if necessary
        if (accepted != null)
            trans.setAccepted(accepted.booleanValue());
        // accept all the transfer storage settings
        for (String transStoreUID : transferSettings.keySet())
        {
            // skip this transfer store uid if it is not referenced on the allowedTransStore // FIXME is this right, or should non-defined entries be set as false?!?
            if (! allowedTransStore.containsKey(transStoreUID))
                continue;

            trans.setTS(allowedTransStore.get(transStoreUID).booleanValue(), transferSettingsIndex.get(transStoreUID).intValue());
        }
    }
}
