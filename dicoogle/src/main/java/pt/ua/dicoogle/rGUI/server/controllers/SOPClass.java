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
package pt.ua.dicoogle.rGUI.server.controllers;

import java.rmi.RemoteException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dcm4che2.data.UID;
import pt.ua.dicoogle.rGUI.interfaces.controllers.ISOPClass;
import pt.ua.dicoogle.server.SOPList;
import pt.ua.dicoogle.server.TransfersStorage;

/**
 * Controller of SOP Class Settings
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class SOPClass implements ISOPClass {

    private SOPList list;
    private TransfersStorage localTS;
    
    private ArrayList<String> SOPClassList;
    private ArrayList<SimpleEntry<String, String>> TS;
    private static Semaphore sem = new Semaphore(1, true);
    private static SOPClass instance = null;

    public static synchronized SOPClass getInstance() {
        try {
            sem.acquire();
            if (instance == null) {
                instance = new SOPClass();
            }
            sem.release();
        } catch (InterruptedException ex) {
            LoggerFactory.getLogger(QRServers.class).error(ex.getMessage(), ex);
        }
        return instance;
    }

    private SOPClass() {

        list = SOPList.getInstance();
        
        initSOPClassList();
        initTS();
    }

    private void initTS() {
        TS = new ArrayList<SimpleEntry<String, String>>();

        TS.add(new SimpleEntry(UID.ImplicitVRLittleEndian, "ImplicitVRLittleEndian"));
        TS.add(new SimpleEntry(UID.ExplicitVRLittleEndian, "ExplicitVRLittleEndian"));
        TS.add(new SimpleEntry(UID.DeflatedExplicitVRLittleEndian, "DeflatedExplicitVRLittleEndian"));
        TS.add(new SimpleEntry(UID.ExplicitVRBigEndian, "ExplicitVRBigEndian"));
        TS.add(new SimpleEntry(UID.JPEGLossless, "JPEG Lossless"));
        TS.add(new SimpleEntry(UID.JPEGLSLossless, "JPEG Lossless LS"));
        TS.add(new SimpleEntry(UID.JPEGLosslessNonHierarchical14, "JPEG Lossless, Non-Hierarchical (Process 14) "));
        TS.add(new SimpleEntry(UID.JPEG2000LosslessOnly, "JPEG2000 Lossless Only"));
        TS.add(new SimpleEntry(UID.JPEGBaseline1, "JPEG Baseline 1"));
        TS.add(new SimpleEntry(UID.JPEGExtended24, "JPEG Extended (Process 2 & 4)"));
        TS.add(new SimpleEntry(UID.JPEGLSLossyNearLossless, "JPEG LS Lossy Near Lossless"));
        TS.add(new SimpleEntry(UID.JPEG2000, "JPEG2000"));
        TS.add(new SimpleEntry(UID.RLELossless, "RLE Lossless"));
        TS.add(new SimpleEntry(UID.MPEG2, "MPEG2"));
    }

    private void initSOPClassList() {
        SOPClassList = new ArrayList<String>();

        SOPClassList.add("BasicStudyContentNotification (Retired)");
        SOPClassList.add("StoredPrintStorage (Retired)");
        SOPClassList.add("HardcopyGrayscaleImageStorage (Retired)");
        SOPClassList.add("HardcopyColorImageStorage (Retired)");
        SOPClassList.add("ComputedRadiographyImageStorage");
        SOPClassList.add("DigitalXRayImageStorageForPresentation");
        SOPClassList.add("DigitalXRayImageStorageForProcessing");
        SOPClassList.add("DigitalMammographyXRayImageStorageForPresentation");
        SOPClassList.add("DigitalMammographyXRayImageStorageForProcessing");
        SOPClassList.add("DigitalIntraoralXRayImageStorageForPresentation");
        SOPClassList.add("DigitalIntraoralXRayImageStorageForProcessing");
        SOPClassList.add("StandaloneModalityLUTStorage (Retired)");
        SOPClassList.add("EncapsulatedPDFStorage");
        SOPClassList.add("StandaloneVOILUTStorage (Retired)");
        SOPClassList.add("GrayscaleSoftcopyPresentationStateStorage");
        SOPClassList.add("ColorSoftcopyPresentationStateStorage");
        SOPClassList.add("PseudoColorSoftcopyPresentationStateStorage");
        SOPClassList.add("BlendingSoftcopyPresentationStateStorage");
        SOPClassList.add("XRayAngiographicImageStorage");
        SOPClassList.add("EnhancedXAImageStorage");
        SOPClassList.add("XRayRadiofluoroscopicImageStorage");
        SOPClassList.add("EnhancedXRFImageStorage");
        SOPClassList.add("XRayAngiographicBiPlaneImageStorage (Retired)");
        SOPClassList.add("PositronEmissionTomographyImageStorage");
        SOPClassList.add("StandalonePETCurveStorage (Retired)");
        SOPClassList.add("CTImageStorage");
        SOPClassList.add("EnhancedCTImageStorage");
        SOPClassList.add("NuclearMedicineImageStorage");
        SOPClassList.add("UltrasoundMultiframeImageStorage (Retired)");
        SOPClassList.add("UltrasoundMultiframeImageStorage");
        SOPClassList.add("MRImageStorage");
        SOPClassList.add("EnhancedMRImageStorage");
        SOPClassList.add("MRSpectroscopyStorage");
        SOPClassList.add("RTImageStorage");
        SOPClassList.add("RTDoseStorage");
        SOPClassList.add("RTStructureSetStorage");
        SOPClassList.add("RTBeamsTreatmentRecordStorage");
        SOPClassList.add("RTPlanStorage");
        SOPClassList.add("RTBrachyTreatmentRecordStorage");
        SOPClassList.add("RTTreatmentSummaryRecordStorage");
        SOPClassList.add("NuclearMedicineImageStorage (Retired)");
        SOPClassList.add("UltrasoundImageStorage (Retired)");
        SOPClassList.add("UltrasoundImageStorage");
        SOPClassList.add("RawDataStorage");
        SOPClassList.add("SpatialRegistrationStorage");
        SOPClassList.add("SpatialFiducialsStorage");
        SOPClassList.add("RealWorldValueMappingStorage");
        SOPClassList.add("SecondaryCaptureImageStorage");
        SOPClassList.add("MultiframeSingleBitSecondaryCaptureImageStorage");
        SOPClassList.add("MultiframeGrayscaleByteSecondaryCaptureImageStorage");
        SOPClassList.add("MultiframeGrayscaleWordSecondaryCaptureImageStorage");
        SOPClassList.add("MultiframeTrueColorSecondaryCaptureImageStorage");
        SOPClassList.add("VLImageStorage (Retired)");
        SOPClassList.add("VLEndoscopicImageStorage");
        SOPClassList.add("VideoEndoscopicImageStorage");
        SOPClassList.add("VLMicroscopicImageStorage");
        SOPClassList.add("VideoMicroscopicImageStorage");
        SOPClassList.add("VLSlideCoordinatesMicroscopicImageStorage");
        SOPClassList.add("VLPhotographicImageStorage");
        SOPClassList.add("VideoPhotographicImageStorage");
        SOPClassList.add("OphthalmicPhotography8BitImageStorage");
        SOPClassList.add("OphthalmicPhotography16BitImageStorage");
        SOPClassList.add("StereometricRelationshipStorage");
        SOPClassList.add("VLMultiframeImageStorage (Retired)");
        SOPClassList.add("StandaloneOverlayStorage (Retired)");
        SOPClassList.add("BasicTextSR");
        SOPClassList.add("EnhancedSR");
        SOPClassList.add("ComprehensiveSR");
        SOPClassList.add("ProcedureLogStorage");
        SOPClassList.add("MammographyCADSR");
        SOPClassList.add("KeyObjectSelectionDocument");
        SOPClassList.add("ChestCADSR");
        SOPClassList.add("StandaloneCurveStorage (Retired)");
        SOPClassList.add("12leadECGWaveformStorage");
        SOPClassList.add("GeneralECGWaveformStorage");
        SOPClassList.add("AmbulatoryECGWaveformStorage");
        SOPClassList.add("HemodynamicWaveformStorage");
        SOPClassList.add("CardiacElectrophysiologyWaveformStorage");
        SOPClassList.add("BasicVoiceAudioWaveformStorage");
        SOPClassList.add("HangingProtocolStorage");
        SOPClassList.add("SiemensCSANonImageStorage");
        SOPClassList.add("XRayRadiationDoseSRStorage");
    }

    /**
     *
     * @return the list with the names of SOP Classes
     * @throws RemoteException
     */
    @Override
    public ArrayList<String> getSOPClassList() throws RemoteException {
        return SOPClassList;
    }

    @Override
    public ArrayList<SimpleEntry<String, String>> getTransferSyntax() throws RemoteException {
        return TS;
    }

    /**
     * Get the UID from SOP Class Name
     * @param sopClass
     * @return UID corresponding to the SOP Class Name
     * @throws RemoteException
     */
    @Override
    public String getUID(String sopClass) throws RemoteException {
        if(sopClass.equals("BasicStudyContentNotification (Retired)"))
            return UID.BasicStudyContentNotificationSOPClassRetired;

        if(sopClass.equals("StoredPrintStorage (Retired)"))
            return UID.StoredPrintStorageSOPClassRetired;

        if(sopClass.equals("HardcopyGrayscaleImageStorage (Retired)"))
            return UID.HardcopyGrayscaleImageStorageSOPClassRetired;

        if(sopClass.equals("HardcopyColorImageStorage (Retired)"))
            return UID.HardcopyColorImageStorageSOPClassRetired;

        if(sopClass.equals("ComputedRadiographyImageStorage"))
            return UID.ComputedRadiographyImageStorage;

        if(sopClass.equals("DigitalXRayImageStorageForPresentation"))
            return UID.DigitalXRayImageStorageForPresentation;


        if(sopClass.equals("DigitalXRayImageStorageForProcessing"))
            return UID.DigitalXRayImageStorageForProcessing;

        if(sopClass.equals("DigitalMammographyXRayImageStorageForPresentation"))
            return UID.DigitalMammographyXRayImageStorageForPresentation;

        if(sopClass.equals("DigitalMammographyXRayImageStorageForProcessing"))
            return UID.DigitalMammographyXRayImageStorageForProcessing;

        if(sopClass.equals("DigitalIntraoralXRayImageStorageForPresentation"))
            return UID.DigitalIntraOralXRayImageStorageForPresentation;

        if(sopClass.equals("DigitalIntraoralXRayImageStorageForProcessing"))
            return UID.DigitalIntraOralXRayImageStorageForProcessing;

        if(sopClass.equals("StandaloneModalityLUTStorage (Retired)"))
            return UID.StandaloneModalityLUTStorageRetired;

        if(sopClass.equals("EncapsulatedPDFStorage"))
            return UID.EncapsulatedPDFStorage;

        if(sopClass.equals("StandaloneVOILUTStorage (Retired)"))
            return UID.StandaloneVOILUTStorageRetired;

        if(sopClass.equals("GrayscaleSoftcopyPresentationStateStorage"))
            return UID.GrayscaleSoftcopyPresentationStateStorageSOPClass;

        if(sopClass.equals("ColorSoftcopyPresentationStateStorage"))
            return UID.ColorSoftcopyPresentationStateStorageSOPClass;
        
        if(sopClass.equals("PseudoColorSoftcopyPresentationStateStorage"))
            return UID.PseudoColorSoftcopyPresentationStateStorageSOPClass;

        if(sopClass.equals("BlendingSoftcopyPresentationStateStorage"))
            return UID.BlendingSoftcopyPresentationStateStorageSOPClass;

        if(sopClass.equals("XRayAngiographicImageStorage"))
            return UID.XRayAngiographicImageStorage;

        if(sopClass.equals("EnhancedXAImageStorage"))
            return UID.EnhancedXAImageStorage;

        if(sopClass.equals("XRayRadiofluoroscopicImageStorage"))
            return UID.XRayRadiofluoroscopicImageStorage;

        if(sopClass.equals("EnhancedXRFImageStorage"))
            return UID.EnhancedXRFImageStorage;

        if(sopClass.equals("XRayAngiographicBiPlaneImageStorage (Retired)"))
            return UID.XRayAngiographicBiPlaneImageStorageRetired;

        if(sopClass.equals("PositronEmissionTomographyImageStorage"))
            return UID.PositronEmissionTomographyImageStorage;

        if(sopClass.equals("StandalonePETCurveStorage (Retired)"))
            return UID.StandalonePETCurveStorageRetired;

        if(sopClass.equals("CTImageStorage"))
            return UID.CTImageStorage;

        if(sopClass.equals("EnhancedCTImageStorage"))
            return UID.EnhancedCTImageStorage;

        if(sopClass.equals("NuclearMedicineImageStorage"))
            return UID.NuclearMedicineImageStorage;

        if(sopClass.equals("UltrasoundMultiframeImageStorage (Retired)"))
            return UID.UltrasoundMultiFrameImageStorageRetired;

        if(sopClass.equals("UltrasoundMultiframeImageStorage"))
            return UID.UltrasoundMultiFrameImageStorage;

        if(sopClass.equals("MRImageStorage"))
            return UID.MRImageStorage;

        if(sopClass.equals("EnhancedMRImageStorage"))
            return UID.EnhancedMRImageStorage;

        if(sopClass.equals("MRSpectroscopyStorage"))
            return UID.MRSpectroscopyStorage;

        if(sopClass.equals("RTImageStorage"))
            return UID.RTImageStorage;

        if(sopClass.equals("RTDoseStorage"))
            return UID.RTDoseStorage;

        if(sopClass.equals("RTStructureSetStorage"))
            return UID.RTStructureSetStorage;

        if(sopClass.equals("RTBeamsTreatmentRecordStorage"))
            return UID.RTBeamsTreatmentRecordStorage;

        if(sopClass.equals("RTPlanStorage"))
            return UID.RTPlanStorage;

        if(sopClass.equals("RTBrachyTreatmentRecordStorage"))
            return UID.RTBrachyTreatmentRecordStorage;

        if(sopClass.equals("RTTreatmentSummaryRecordStorage"))
            return UID.RTTreatmentSummaryRecordStorage;

        if(sopClass.equals("NuclearMedicineImageStorage (Retired)"))
            return UID.NuclearMedicineImageStorageRetired;

        if(sopClass.equals("UltrasoundImageStorage (Retired)"))
            return UID.UltrasoundImageStorageRetired;

        if(sopClass.equals("UltrasoundImageStorage"))
            return UID.UltrasoundImageStorage;

        if(sopClass.equals("RawDataStorage"))
            return UID.RawDataStorage;

        if(sopClass.equals("SpatialRegistrationStorage"))
            return UID.SpatialRegistrationStorage;

        if(sopClass.equals("SpatialFiducialsStorage"))
            return UID.SpatialFiducialsStorage;

        if(sopClass.equals("RealWorldValueMappingStorage"))
            return UID.RealWorldValueMappingStorage;

        if(sopClass.equals("SecondaryCaptureImageStorage"))
            return UID.SecondaryCaptureImageStorage;

        if(sopClass.equals("MultiframeSingleBitSecondaryCaptureImageStorage"))
            return UID.MultiFrameSingleBitSecondaryCaptureImageStorage;

        if(sopClass.equals("MultiframeGrayscaleByteSecondaryCaptureImageStorage"))
            return UID.MultiFrameGrayscaleByteSecondaryCaptureImageStorage;

        if(sopClass.equals("MultiframeGrayscaleWordSecondaryCaptureImageStorage"))
            return UID.MultiFrameGrayscaleWordSecondaryCaptureImageStorage;

        if(sopClass.equals("MultiframeTrueColorSecondaryCaptureImageStorage"))
            return UID.MultiFrameTrueColorSecondaryCaptureImageStorage;

        if(sopClass.equals("VLImageStorage (Retired)"))
            ///return UID.VLImageStorageRetired);
            /** Update to new version from dcm4che */
            return UID.VLImageStorageTrialRetired;

        if(sopClass.equals("VLEndoscopicImageStorage"))
            return UID.VLEndoscopicImageStorage;

        if(sopClass.equals("VideoEndoscopicImageStorage"))
            return UID.VideoEndoscopicImageStorage;

        if(sopClass.equals("VLMicroscopicImageStorage"))
            return UID.VLMicroscopicImageStorage;

        if(sopClass.equals("VideoMicroscopicImageStorage"))
            return UID.VideoMicroscopicImageStorage;

        if(sopClass.equals("VLSlideCoordinatesMicroscopicImageStorage"))
            return UID.VLSlideCoordinatesMicroscopicImageStorage;

        if(sopClass.equals("VLPhotographicImageStorage"))
            return UID.VLPhotographicImageStorage;

        if(sopClass.equals("VideoPhotographicImageStorage"))
            return UID.VideoPhotographicImageStorage;

        if(sopClass.equals("OphthalmicPhotography8BitImageStorage"))
            return UID.OphthalmicPhotography8BitImageStorage;

        if(sopClass.equals("OphthalmicPhotography16BitImageStorage"))
            return UID.OphthalmicPhotography16BitImageStorage;

        if(sopClass.equals("StereometricRelationshipStorage"))
            return UID.StereometricRelationshipStorage;

        if(sopClass.equals("VLMultiframeImageStorage (Retired)"))
            ///return UID.VLMultiframeImageStorageRetired);
            /** Update to new version from dcm4che */
            return UID.VLMultiFrameImageStorageTrialRetired;

        if(sopClass.equals("StandaloneOverlayStorage (Retired)"))
            return UID.StandaloneOverlayStorageRetired;

        if(sopClass.equals("BasicTextSR"))
            ///return UID.BasicTextSR);
            /* update to new version from dcm4che */
            return UID.BasicTextSRStorage;

        if(sopClass.equals("EnhancedSR"))
            ///return UID.EnhancedSR);
            /* update to new version from dcm4che */
            return UID.EnhancedSRStorage;

        if(sopClass.equals("ComprehensiveSR"))
            ///return UID.ComprehensiveSR);
            /* update to new version from dcm4che */
            return UID.ComprehensiveSRStorage;

        if(sopClass.equals("ProcedureLogStorage"))
            return UID.ProcedureLogStorage;

        if(sopClass.equals("MammographyCADSR"))
            ///return UID.MammographyCADSR);
            /* update to new version from dcm4che */
            return UID.MammographyCADSRStorage;

        if(sopClass.equals("KeyObjectSelectionDocument"))
            ///return UID.KeyObjectSelectionDocument);
            /* update to new version from dcm4che */
            return UID.MammographyCADSRStorage;

        if(sopClass.equals("ChestCADSR"))
            ///return UID.ChestCADSR);
            /* update to new version from dcm4che */
            return UID.ChestCADSRStorage;

        if(sopClass.equals("StandaloneCurveStorage (Retired)"))
            return UID.StandaloneCurveStorageRetired;

        //if(sopClass.equals("12leadECGWaveformStorage"))
            //return UID._12leadECGWaveformStorage;

        if(sopClass.equals("GeneralECGWaveformStorage"))
            return UID.GeneralECGWaveformStorage;

        if(sopClass.equals("AmbulatoryECGWaveformStorage"))
            return UID.AmbulatoryECGWaveformStorage;

        if(sopClass.equals("HemodynamicWaveformStorage"))
            return UID.HemodynamicWaveformStorage;

        if(sopClass.equals("CardiacElectrophysiologyWaveformStorage"))
            return UID.CardiacElectrophysiologyWaveformStorage;

        if(sopClass.equals("BasicVoiceAudioWaveformStorage"))
            return UID.BasicVoiceAudioWaveformStorage;

        if(sopClass.equals("HangingProtocolStorage"))
            return UID.HangingProtocolStorage;

        if(sopClass.equals("SiemensCSANonImageStorage"))
            return UID.SiemensCSANonImageStorage;
        if(sopClass.equals("XRayRadiationDoseSRStorage"))
            return UID.XRayRadiationDoseSRStorage;

        return null;
    }

    @Override
    public boolean[] getTS(String UID) throws RemoteException {
        localTS = list.getTS(UID);
        return localTS.getTS();
    }

    @Override
    public boolean getAccepted(String UID) throws RemoteException {
        localTS = list.getTS(UID);
        return localTS.getAccepted();
    }

    @Override
    public boolean[] setDefault() throws RemoteException {
       localTS.setAccepted(true);
       localTS.setTS(true,0);
       localTS.setTS(true,1);
       localTS.setTS(false,2);
       localTS.setTS(false,3);
       localTS.setTS(true,4);
       localTS.setTS(false,5);
       localTS.setTS(false,6);
       localTS.setTS(false,7);
       localTS.setTS(true,8);
       localTS.setTS(false,9);
       localTS.setTS(false,10);
       localTS.setTS(false,11);
       localTS.setTS(false,12);
       localTS.setTS(false,13);

       return localTS.getTS();
    }

    @Override
    public boolean[] clearAll() throws RemoteException {
       localTS.setAccepted(false);
       localTS.setTS(false,0);
       localTS.setTS(false,1);
       localTS.setTS(false,2);
       localTS.setTS(false,3);
       localTS.setTS(false,4);
       localTS.setTS(false,5);
       localTS.setTS(false,6);
       localTS.setTS(false,7);
       localTS.setTS(false,8);
       localTS.setTS(false,9);
       localTS.setTS(false,10);
       localTS.setTS(false,11);
       localTS.setTS(false,12);
       localTS.setTS(false,13);

       return localTS.getTS();
    }

    @Override
    public boolean[] setAll() throws RemoteException {
       localTS.setAccepted(true);
       localTS.setTS(true,0);
       localTS.setTS(true,1);
       localTS.setTS(true,2);
       localTS.setTS(true,3);
       localTS.setTS(true,4);
       localTS.setTS(true,5);
       localTS.setTS(true,6);
       localTS.setTS(true,7);
       localTS.setTS(true,8);
       localTS.setTS(true,9);
       localTS.setTS(true,10);
       localTS.setTS(true,11);
       localTS.setTS(true,12);
       localTS.setTS(true,13);

       return localTS.getTS();
    }

    @Override
    public void setTS(String UID, boolean status, int number) throws RemoteException {
        localTS = list.getTS(UID);

        localTS.setTS(status, number);
    }

    @Override
    public void saveLocalTS(String UID) throws RemoteException {
        list.updateTS(UID, localTS.getTS(), localTS.getAccepted());
    }

    @Override
    public void saveAllTS() throws RemoteException {
        List l = list.getKeys();
        for (int i = 0; i < l.size(); i++) {
            list.updateTS(l.get(i).toString(), localTS.getTS(), localTS.getAccepted());
        }
    }

    @Override
    public void setAccepted(String UID, boolean value) throws RemoteException {
        localTS = list.getTS(UID);
        localTS.setAccepted(value);
    }
}
