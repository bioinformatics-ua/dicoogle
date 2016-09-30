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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.dcm4che2.data.UID;
import pt.ua.dicoogle.server.SOPList;
import pt.ua.dicoogle.server.TransfersStorage;
import pt.ua.dicoogle.core.settings.ServerSettings;
import pt.ua.dicoogle.sdk.settings.InvalidSettingValueException;
import pt.ua.dicoogle.sdk.settings.types.CheckboxWithHint;
import pt.ua.dicoogle.sdk.settings.types.ComboBox;
import pt.ua.dicoogle.sdk.settings.types.RangeInteger;
import pt.ua.dicoogle.sdk.settings.types.ServerDirectoryPath;
import pt.ua.dicoogle.sdk.settings.types.DataTable;
import pt.ua.dicoogle.sdk.settings.types.StaticDataTable;
import static pt.ua.dicoogle.sdk.settings.Utils.parseCheckBoxValue;

/**
 * A wrapper used to manage the Dicoogle Settings remotely through the web environment/app.
 */
public class Dicoogle
{
	/**
	 * The current instance of this class.
	 */
	private static Dicoogle instance;

	/**
	 * Pointer to a object that maintain the ServerSettings configuration.
	 */
	private static ServerSettings cfg;

	/**
	 * Indexing.
	 */

	/**
	 * The name of the setting that indicates the Dicoogle Directory Monitorization.
	 */
	public static final String SETTING_DIRECTORY_MONITORIZATION_NAME = "Dicoogle Directory Monitorization";
	/**
	 * The name of the setting that indicates the Dicoogle Enable Directory Watcher.
	 */
	public static final String SETTING_DIRECTORY_WATCHER_NAME = "Enable Dicoogle Directory Watcher";
	/**
	 * The name of the setting that indicates the Indexing Effort.
	 */
	public static final String SETTING_INDEXING_EFFORT_NAME = "Indexing Effort";
	/**
	 * The name of the setting that indicates the Thumbnails Size.
	 */
	public static final String SETTING_THUMBNAILS_SIZE_NAME = "Thumbnails Size";
	/**
	 * The name of the setting that indicates if Thumbnails are to be Saved.
	 */
	public static final String SETTING_SAVE_THUMBNAILS_NAME = "Save Thumbnails";
	/**
	 * The name of the setting that indicates if ZIP files are to be Indexed.
	 */
	public static final String SETTING_INDEX_ZIP_FILES_NAME = "Index ZIP Files";

	/**
	 * Access List.
	 */

	/**
	 * The name of the setting that indicates the Server AE Title.
	 */
	public static final String SETTING_SERVER_AE_TITLE_NAME = "Server AE Title";
	/**
	 * The name of the setting that indicates the Permit All AE Titles.
	 */
	public static final String SETTING_PERMIT_ALL_AE_TITLES_NAME = "Permit All AE Titles";
	/**
	 * The name of the setting that indicates the Client Permit Access List.
	 */
	public static final String SETTING_CLIENT_PERMIT_ACCESS_LIST_NAME = "Client Permit Access List";

	/**
	 * SOP Class/Transfer Storage.
	 */

	/**
	 * The name of the setting that indicates if Accept All SOP Classes.
	 */
	public static final String SETTING_SOP_CLASS_ACCEPT_ALL_NAME = "Accept All";
	/**
	 * The name of the setting that indicates the Global Transfer Storage settings.
	 */
	public static final String SETTING_SOP_CLASS_GLOBAL_TRANSFER_STORAGE_NAME = "Global Transfer Storage";

	/**
	 * Indexing Help.
	 */

	/**
	 * The help of the Indexing Effort setting.
	 */
	public static final String SETTING_INDEXING_EFFORT_HELP = "How agressive should the indexing effort be.\n\n0 - Lower\n100 - Intensive";

	/**
	 * Access List Help.
	 */

	/**
	 * The help for the setting that indicates the Server AE Title.
	 */
	public static final String SETTING_SERVER_AE_TITLE_HELP = "Setting an AE Title will force the server to only accept connections adressed to the given AE Title.";
	/**
	 * The help of the setting that indicates the Permit All AE Titles.
	 */
	public static final String SETTING_PERMIT_ALL_AE_TITLES_HELP = "Overrides the Client Permit Access List settings, making the server accept connection from clients with any AE Title.";
	/**
	 * The help for the setting that indicates the Client Permit Access List.
	 */
	public static final String SETTING_CLIENT_PERMIT_ACCESS_LIST_HELP = "Adding an AE Title to the Access Control List will force the server to only accept connections from clients with those AE Titles.";

	private Dicoogle()
	{
		cfg = ServerSettings.getInstance();
	}

	/**
	 * Returns the current instance of this class.
	 *
	 * @return the current instance of this class.
	 */
	public synchronized static Dicoogle getInstance()
	{
		if (instance == null)
			instance = new Dicoogle();

		return instance;
	}

	/**
	 * Indexing.
	 */

	/**
	 * Returns a Map containing the current settings.
	 *
	 * @return a Map containing the current settings.
	 */
	public HashMap<String, Object> getIndexingSettings()
	{
		HashMap<String, Object> settings = new LinkedHashMap<String, Object>();

		settings.put(SETTING_DIRECTORY_WATCHER_NAME, new Boolean(cfg.isMonitorWatcher()));
		settings.put(SETTING_DIRECTORY_MONITORIZATION_NAME, new ServerDirectoryPath(cfg.getDicoogleDir()));
		settings.put(SETTING_INDEX_ZIP_FILES_NAME, cfg.isIndexZIPFiles());
		settings.put(SETTING_INDEXING_EFFORT_NAME, new RangeInteger(0, 100, cfg.getIndexerEffort()));
		settings.put(SETTING_SAVE_THUMBNAILS_NAME, cfg.getSaveThumbnails());
		settings.put(SETTING_THUMBNAILS_SIZE_NAME, cfg.getThumbnailsMatrix()); // TODO add more descritive type

		return settings;
	}

	/**
	 * Returns the settings help.
	 *
	 * @return a HashMap containing the setting help.
	 */
	public HashMap<String, String> getIndexingSettingsHelp()
	{
		HashMap<String, String> settings = new HashMap<String, String>();

		// right now this is the only settingg that needs some help describing it
		settings.put(SETTING_INDEXING_EFFORT_NAME, SETTING_INDEXING_EFFORT_HELP);

		return settings;
	}

	/**
	 * Validates the settings supplied and checks if they are valid ones.
	 *
	 * @param map a Map containing the settings.
	 * @return true if all the settings are valid.
	 * @throws InvalidSettingValueException if at least one of the settings is invalid.
	 */
	public boolean tryIndexingSettings(HashMap<String, Object> map) throws InvalidSettingValueException
	{
		// TODO remmember to check for null, all the params are supplied, all have valid types, and oly then all have valid values

		return true;
	}

	/**
	 * Tries to set the supplied settings as the new settings.
	 *
	 * @param settings a Map containing the new settings.
	 * @return true if all settings were successfully changed to the ones supplied, false otherwise (no settings changed).
	 */
	public boolean setIndexingSettings(HashMap<String, Object> settings)
	{
		// validate the settings
		boolean valid = false;
		try
		{
			valid = tryIndexingSettings(settings);
		}
		catch (InvalidSettingValueException ex)
		{
			// at least one of the setting is invalid, abort
			return false;
		}
		if (! valid) //settings not valid due to unknown reason, also abort
			return false;

		// all the settings were correctly validated, so apply them all
		cfg.setDicoogleDir(((ServerDirectoryPath) settings.get(SETTING_DIRECTORY_MONITORIZATION_NAME)).getPath());
		cfg.setMonitorWatcher(((Boolean) settings.get(SETTING_DIRECTORY_WATCHER_NAME)).booleanValue());
		cfg.setIndexerEffort(((RangeInteger) settings.get(SETTING_INDEXING_EFFORT_NAME)).getValue());
		cfg.setThumbnailsMatrix((String) settings.get(SETTING_THUMBNAILS_SIZE_NAME));
		cfg.setSaveThumbnails(((Boolean) settings.get(SETTING_SAVE_THUMBNAILS_NAME)).booleanValue());
		cfg.setIndexZIPFiles(((Boolean) settings.get(SETTING_INDEX_ZIP_FILES_NAME)).booleanValue());

		// return success
		return true;
	}

	/**
	 * Access List.
	 */

	/**
	 * Returns a Map containing the current settings.
	 *
	 * @return a Map containing the current settings.
	 */
	public HashMap<String, Object> getAccessListSettings()
	{
		HashMap<String, Object> settings = new LinkedHashMap<String, Object>();

		settings.put(SETTING_SERVER_AE_TITLE_NAME, cfg.getAE());
		settings.put(SETTING_PERMIT_ALL_AE_TITLES_NAME, new Boolean(cfg.getPermitAllAETitles()));
		String[] caet = cfg.getCAET();
		DataTable clientPermitACL = new DataTable(1, caet.length);
		clientPermitACL.setColumnName(0, "Client AE Title");
		if (caet.length < 1)
		{
			clientPermitACL.addRow();
			clientPermitACL.setCellData(0, 0, "");
		}
		else
		{
			for (int i = 0; i < caet.length; i++)
				clientPermitACL.setCellData(i, 0, caet[i]);
		}
		settings.put(SETTING_CLIENT_PERMIT_ACCESS_LIST_NAME, clientPermitACL);

		return settings;
	}

	/**
	 * Returns the settings help.
	 *
	 * @return a HashMap containing the setting help.
	 */
	public HashMap<String, String> getAccessListSettingsHelp()
	{
		HashMap<String, String> settings = new HashMap<String, String>();

		settings.put(SETTING_SERVER_AE_TITLE_NAME, SETTING_SERVER_AE_TITLE_HELP);
		settings.put(SETTING_PERMIT_ALL_AE_TITLES_NAME, SETTING_PERMIT_ALL_AE_TITLES_HELP);
		settings.put(SETTING_CLIENT_PERMIT_ACCESS_LIST_NAME, SETTING_CLIENT_PERMIT_ACCESS_LIST_HELP);

		return settings;
	}

	/**
	 * Validates the settings supplied and checks if they are valid ones.
	 *
	 * @param map a Map containing the settings.
	 * @return true if all the settings are valid.
	 * @throws InvalidSettingValueException if at least one of the settings is invalid.
	 */
	public boolean tryAccessListSettings(HashMap<String, Object> map) throws InvalidSettingValueException
	{
		// TODO remmember to check for null, all the params are supplied, all have valid types, and oly then all have valid values

		return true;
	}

	/**
	 * Tries to set the supplied settings as the new settings.
	 *
	 * @param settings a Map containing the new settings.
	 * @return true if all settings were successfully changed to the ones supplied, false otherwise (no settings changed).
	 */
	public boolean setAccessListSettings(HashMap<String, Object> settings)
	{
		// validate the settings
		boolean valid = false;
		try
		{
			valid = tryAccessListSettings(settings);
		}
		catch (InvalidSettingValueException ex)
		{
			// at least one of the setting is invalid, abort
			return false;
		}
		if (! valid) //settings not valid due to unknown reason, also abort
			return false;

		// all the settings were correctly validated, so apply them all
		cfg.setAE((String) settings.get(SETTING_SERVER_AE_TITLE_NAME));
		cfg.setPermitAllAETitles(((Boolean) settings.get(SETTING_PERMIT_ALL_AE_TITLES_NAME)).booleanValue());
		DataTable clientPermitACL = (DataTable) settings.get(SETTING_CLIENT_PERMIT_ACCESS_LIST_NAME);
		LinkedList<String> caet = new LinkedList<String>();
		if ((clientPermitACL.getRowCount() != 1) || (clientPermitACL.getCellData(0, 0) != ""))
			for (int i = 0; i < clientPermitACL.getRowCount(); i++)
				caet.add((String) clientPermitACL.getCellData(i, 0));
		cfg.setCAET(caet.toArray(new String[caet.size()]));

		// return success
		return true;
	}

	/**
	 * SOP Classes / Transfer Storage.
	 */

	/**
	 * Returns a Map containing the current settings.
	 *
	 * @return a Map containing the current settings.
	 */
	public HashMap<String, Object> getSOPClassGlobalSettings()
	{
		HashMap<String, Object> settings = new LinkedHashMap<String, Object>();

		Object[] set = getTransferSettingsForAllSOPClasses();

		ComboBox tristate = new ComboBox();
		tristate.addElement("As Is (No Change)", "");
		tristate.addElement("Allow All", "On"); // try to emulate a regular checkbox :)
		tristate.addElement("Forbid All", "Off");
		tristate.setCurrentByValue(""); // FIXME default is "no change", always (for the time being)
		settings.put(SETTING_SOP_CLASS_ACCEPT_ALL_NAME, tristate);
		settings.put(SETTING_SOP_CLASS_GLOBAL_TRANSFER_STORAGE_NAME, set[1]);

		return settings;
	}

	/**
	 * Returns the settings help.
	 *
	 * @return a HashMap containing the setting help.
	 */
	public HashMap<String, String> getSOPClassGlobalSettingsHelp()
	{
		HashMap<String, String> settings = new HashMap<String, String>();

		// TODO

		return settings;
	}

	/**
	 * Validates the settings supplied and checks if they are valid ones.
	 *
	 * @param map a Map containing the settings.
	 * @return true if all the settings are valid.
	 * @throws InvalidSettingValueException if at least one of the settings is invalid.
	 */
	public boolean trySOPClassGlobalSettings(HashMap<String, Object> map) throws InvalidSettingValueException
	{
		// TODO remmember to check for null, all the params are supplied, all have valid types, and oly then all have valid values

		return true;
	}

	/**
	 * Tries to set the supplied settings as the new settings.
	 *
	 * @param settings a Map containing the new settings.
	 * @return true if all settings were successfully changed to the ones supplied, false otherwise (no settings changed).
	 */
	public boolean setSOPClassGlobalSettings(HashMap<String, Object> settings)
	{
		// validate the settings
		boolean valid = false;
		try
		{
			valid = tryAccessListSettings(settings);
		}
		catch (InvalidSettingValueException ex)
		{
			// at least one of the setting is invalid, abort
			return false;
		}
		if (! valid) //settings not valid due to unknown reason, also abort
			return false;

		// all the settings were correctly validated, so apply them all
		ComboBox tristate = (ComboBox) settings.get(SETTING_SOP_CLASS_ACCEPT_ALL_NAME);
		Boolean accepted = null;
		if (! tristate.getCurrentValue().isEmpty()) // NOTE: empty ("") means no change, see the getter for this settings
			accepted = new Boolean(parseCheckBoxValue(tristate.getCurrentValue()));
		StaticDataTable table = (StaticDataTable) settings.get(SETTING_SOP_CLASS_GLOBAL_TRANSFER_STORAGE_NAME);

		setTransferSettingsForAllSOPClasses(accepted, table);

		// return success
		return true;
	}

	/**
	 * Holds and provides information about the accepted SOP Classes and
	 * Transfer Storages allowed within Dicoogle.
	 */
	public static class SOPClassSettings
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

	/**
	 * Returns a StaticDataTable containing the TransferStorages for a
	 * specific SOP Class UID.
	 *
	 * @param sopClassUID a SOP Class UID.
	 * @return an Object array containing as first elements a Boolean object
	 * indicating if this SOP Class is accepted and as second element a
	 * StaticDataTable that contains the TransferStorages for this specific
	 * SOP Class UID.
	 */
	public Object[] getTransferSettingsForSOPClass(String sopClassUID)
	{
		SOPClassSettings sops = SOPClassSettings.getInstance();
		TransfersStorage ts = sops.getSOPClassSettings(sopClassUID);

		// this settings will be returned on a StaticDataTable, to guarantee that the user does not change the entry count and only their values
		StaticDataTable table = new StaticDataTable(1, sops.getTransferSettings().size());
		// for each setting on the transfer storage settings...
		int i = 0;
		for (Map.Entry<String, String> entry : sops.getTransferSettings().entrySet())
		{
			// get its UID, "visual/regular" name and index (relative to the TS settings array)
			String uid = entry.getKey();
			String name = entry.getValue();
			int index = sops.getTransferSettingsIndex().get(uid).intValue();

			// create a HintedCheckbox with the info gotten above and the current value of the setting
			CheckboxWithHint box = new CheckboxWithHint(ts.getTS()[index], name, uid);
			// and add the checkbox to the data table
			table.setCellData(i, 0, box);

			i++;
		}

		// mount the return object array
		Object[] result = new Object[2];
		// and add the proper objects to it
		result[0] = new Boolean(ts.getAccepted());
		result[1] = table;

		return result;
	}

	/**
	 * Returns a StaticDataTable containing the TransferStorages settings
	 * for all SOP Classes UIDs.
	 *
	 * @return an Object array containing as first elements a Boolean object
	 * indicating if all SOP Classes are accepted and as second element a
	 * StaticDataTable that contains the TransferStorages for all SOP Classes
	 * UIDs.
	 */
	public Object[] getTransferSettingsForAllSOPClasses()
	{
		SOPClassSettings sops = SOPClassSettings.getInstance();

		// get the first SOP Class UID // FIXME using the first SOP Class settings as global does not accurately represent the common settings for all SOP classes, but it's close enough (if they get applied :) )
		String sopClassUID = sops.getSOPClasses().entrySet().iterator().next().getKey();
		// and its TransferStorage settings
		TransfersStorage ts = sops.getSOPClassSettings(sopClassUID);

		// this settings will be returned on a StaticDataTable, to guarantee that the user does not change the entry count and only their values
		StaticDataTable table = new StaticDataTable(1, sops.getTransferSettings().size());
		table.setColumnName(0, "Transfer Storage");
		// for each setting on the transfer storage settings...
		int i = 0;
		for (Map.Entry<String, String> entry : sops.getTransferSettings().entrySet())
		{
			// get its UID, "visual/regular" name and index (relative to the TS settings array)
			String uid = entry.getKey();
			String name = entry.getValue();
			int index = sops.getTransferSettingsIndex().get(uid).intValue();

			// create a HintedCheckbox with the info gotten above and the current value of the setting
			CheckboxWithHint box = new CheckboxWithHint(ts.getTS()[index], name, uid);
			// and add the checkbox to the data table
			table.setCellData(i, 0, box);

			i++;
		}

		// mount the return object array
		Object[] result = new Object[2];
		// and add the proper objects to it
		result[0] = new Boolean(ts.getAccepted());
		result[1] = table;

		return result;
	}

	/**
	 * Applies the Transfer Storage settings present on a StaticDataTable to
	 * a specific SOP Class UID.
	 *
	 * @param sopClassUID the target SOP Class UID.
	 * @param accepted if the SOP Class UID is accepted. Can be null and if so, the target accepted value will remain the same.
	 * @param table a StaticDataTable with the Transfer Storage settings.
	 */
	public void setTransferSettingsForSOPClass(String sopClassUID, Boolean accepted, StaticDataTable table)
	{
		SOPClassSettings sops = SOPClassSettings.getInstance();
		TransfersStorage ts = sops.getSOPClassSettings(sopClassUID);

		// for each transfer storage setting on the datatable...
		for (int i = 0 ; i < table.getRowCount(); i++)
		{
			// get the current row/cell setting
			CheckboxWithHint box = (CheckboxWithHint) table.getCellData(i, 0);

			// get its UID and index (relative to the TS settings array)
			String uid = box.getHint();
			int index = sops.getTransferSettingsIndex().get(uid).intValue();

			// set the transfer storge setting value
			ts.setTS(box.isChecked(), index);
			if (accepted != null)
				ts.setAccepted(accepted.booleanValue());
		}
	}

	/**
	 * Applies the Transfer Storage settings present on a StaticDataTable to
	 * all SOP Classes UIDs.
	 *
	 * @param accepted if all SOP Classes UIDs are accepted. Can be null and if so, the target accepted value will remain the same.
	 * @param table a StaticDataTable with the Transfer Storage settings.
	 */
	public void setTransferSettingsForAllSOPClasses(Boolean accepted, StaticDataTable table)
	{
		SOPClassSettings sops = SOPClassSettings.getInstance();

		HashMap<String, Boolean> allowed = new HashMap<String, Boolean>();

		// for each transfer storage setting on the datatable...
		for (int i = 0 ; i < table.getRowCount(); i++)
		{
			// get the current row/cell setting
			CheckboxWithHint box = (CheckboxWithHint) table.getCellData(i, 0);

			// get its UID
			String uid = box.getHint();

			allowed.put(uid, new Boolean(box.isChecked()));
		}

		// set the transfer storage settings
		sops.setAllSOPClassesSettings(accepted, allowed);
	}
}
