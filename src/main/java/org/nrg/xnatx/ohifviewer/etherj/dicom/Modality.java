/*********************************************************************
 * Copyright (c) 2017, Institute of Cancer Research
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * (2) Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *
 * (3) Neither the name of the Institute of Cancer Research nor the
 *     names of its contributors may be used to endorse or promote
 *     products derived from this software without specific prior
 *     written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *********************************************************************/
package org.nrg.xnatx.ohifviewer.etherj.dicom;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Modalities from the DICOM IOD.
 * @author jamesd
 */
public class Modality
{
	/** Modality from DICOM IOD: Audio. */
	public static final String AU = "AU";
	/** Modality from DICOM IOD: Biomagnetic Imaging. */
	public static final String BI = "BI";
	/** Modality from DICOM IOD: Color Flow Doppler. */
	public static final String CD = "CD";
	/** Modality from DICOM IOD: Computed Radiography. */
	public static final String CR = "CR";
	/** Modality from DICOM IOD: Computed Tomography. */
	public static final String CT = "CT";
	/** Modality from DICOM IOD: Duplex Doppler. */
	public static final String DD = "DD";
	/** Modality from DICOM IOD: Diaphanography. */
	public static final String DG = "DG";
	/** Modality from DICOM IOD: Digital Subtraction Angiography. */
	public static final String DSA = "DSA";
	/** Modality from DICOM IOD: Digital Radiography. */
	public static final String DX = "DX";
	/** Modality from DICOM IOD: Electrocardiography. */
	public static final String ECG = "ECG";
	/** Modality from DICOM IOD: Cardiac Electrophysiology. */
	public static final String EPS = "EPS";
	/** Modality from DICOM IOD: Endoscopy. */
	public static final String ES = "ES";
	/** Modality from DICOM IOD: General Microscopy. */
	public static final String GM = "GM";
	/** Modality from DICOM IOD: Hard Copy. */
	public static final String HC = "HC";
	/** Modality from DICOM IOD: Hemodynamic Waveform. */
	public static final String HD = "HD";
	/** Modality from DICOM IOD: Intra-Oral Radiography. */
	public static final String IO = "IO";
	/** Modality from DICOM IOD: Intravascular Ultrasound. */
	public static final String IVUS = "IVUS";
	/** Modality from DICOM IOD: Laser Surface Scan. */
	public static final String LS = "LS";
	/** Modality from DICOM IOD: Mammography. */
	public static final String MG = "MG";
	/** Modality from DICOM IOD: Magnetic Resonance. */
	public static final String MR = "MR";
	/** Modality from DICOM IOD: Nuclear Medicine. */
	public static final String NM = "NM";
	/** Modality from DICOM IOD: Optical Coherence Tomography. */
	public static final String OCT = "OCT";
	/** Modality from DICOM IOD: Ophthalmic Photography. */
	public static final String OP = "OP";
	/** Modality from DICOM IOD: Ophthalmic Mapping. */
	public static final String OPM = "OPM";
	/** Modality from DICOM IOD: Ophthalmic Refraction. */
	public static final String OPR = "OPR";
	/** Modality from DICOM IOD: Ophthalmic Visual Field. */
	public static final String OPV = "OPV";
	/** Modality from DICOM IOD: Other. */
	public static final String OT = "OT";
	/** Modality from DICOM IOD: Presentation State. */
	public static final String PR = "PR";
	/** Modality from DICOM IOD: Positron Emission Tomography. */
	public static final String PET = "PET";
	/** Modality from DICOM IOD: Panoramic X-Ray. */
	public static final String PX = "PX";
	/** Modality from DICOM IOD: Registration. */
	public static final String REG = "REG";
	/** Modality from DICOM IOD: Radio Fluoroscopy. */
	public static final String RF = "RF";
	/** Modality from DICOM IOD: Radiographic Imaging. */
	public static final String RG = "RG";
	/** Modality from DICOM IOD: Radiotherapy Dose. */
	public static final String RTDOSE = "RTDOSE";
	/** Modality from DICOM IOD: Radiotherapy Image. */
	public static final String RTIMAGE = "RTIMAGE";
	/** Modality from DICOM IOD: Radiotherapy Plan. */
	public static final String RTPLAN = "RTPLAN";
	/** Modality from DICOM IOD: RT Treatment Record. */
	public static final String RTRECORD = "RTRECORD";
	/** Modality from DICOM IOD: Radiotherapy Structure Set. */
	public static final String RTSTRUCT = "RTSTRUCT";
	/** Modality from DICOM IOD: Segmentation. */
	public static final String SEG = "SEG";
	/** Modality from DICOM IOD: Slide Microscopy. */
	public static final String SM = "SM";
	/** Modality from DICOM IOD: Stereometric Relationship. */
	public static final String SMR = "SMR";
	/** Modality from DICOM IOD: SR Document. */
	public static final String SR = "SR";
	/** Modality from DICOM IOD: Single-Photon Emission Computed Tomography. */
	public static final String ST = "ST";
	/** Modality from DICOM IOD: Thermography. */
	public static final String TG = "TG";
	/** Modality from DICOM IOD: Ultrasound. */
	public static final String US = "US";
	/** Modality from DICOM IOD: X-Ray Angiography. */
	public static final String XA = "XA";
	/** Modality from DICOM IOD: External-Camera Photography. */
	public static final String XC = "XC";

	private static final long AU_MASK = 1L;
	private static final long BI_MASK = 1L << 1;
	private static final long CD_MASK = 1L << 2;
	private static final long CR_MASK = 1L << 3;
	private static final long CT_MASK = 1L << 4;
	private static final long DD_MASK = 1L << 5;
	private static final long DG_MASK = 1L << 6;
	private static final long DSA_MASK = 1L << 7;
	private static final long DX_MASK = 1L << 8;
	private static final long ECG_MASK = 1L << 9;
	private static final long EPS_MASK = 1L << 10;
	private static final long ES_MASK = 1L << 11;
	private static final long GM_MASK = 1L << 12;
	private static final long HC_MASK = 1L << 13;
	private static final long HD_MASK = 1L << 14;
	private static final long IO_MASK = 1L << 15;
	private static final long IVUS_MASK = 1L << 16;
	private static final long LS_MASK = 1L << 17;
	private static final long MG_MASK = 1L << 18;
	private static final long MR_MASK = 1L << 19;
	private static final long NM_MASK = 1L << 20;
	private static final long OCT_MASK = 1L << 21;
	private static final long OP_MASK = 1L << 22;
	private static final long OPM_MASK = 1L << 23;
	private static final long OPR_MASK = 1L << 24;
	private static final long OPV_MASK = 1L << 25;
	private static final long OT_MASK = 1L << 26;
	private static final long PR_MASK = 1L << 27;
	private static final long PET_MASK = 1L << 28;
	private static final long PX_MASK = 1L << 29;
	private static final long REG_MASK = 1L << 30;
	private static final long RF_MASK = 1L << 31;
	private static final long RG_MASK = 1L << 32;
	private static final long RTDOSE_MASK = 1L << 33;
	private static final long RTIMAGE_MASK = 1L << 34;
	private static final long RTPLAN_MASK = 1L << 35;
	private static final long RTRECORD_MASK = 1L << 36;
	private static final long RTSTRUCT_MASK = 1L << 37;
	private static final long SEG_MASK = 1L << 38;
	private static final long SM_MASK = 1L << 39;
	private static final long SMR_MASK = 1L << 40;
	private static final long SR_MASK = 1L << 41;
	private static final long ST_MASK = 1L << 42;
	private static final long TG_MASK = 1L << 43;
	private static final long US_MASK = 1L << 44;
	private static final long XA_MASK = 1L << 45;
	private static final long XC_MASK = 1L << 46;
	private static final Map<String,Long> stringToMask = new HashMap<>();
	private static final Map<Long,String> maskToString = new HashMap<>();
	private static final Map<String,String> stringToDesc = new HashMap<>();

	static
	{
		stringToMask.put(AU, AU_MASK);
		stringToMask.put(BI, BI_MASK);
		stringToMask.put(CD, CD_MASK);
		stringToMask.put(CR, CR_MASK);
		stringToMask.put(CT, CT_MASK);
		stringToMask.put(DD, DD_MASK);
		stringToMask.put(DG, DG_MASK);
		stringToMask.put(DSA, DSA_MASK);
		stringToMask.put(DX, DX_MASK);
		stringToMask.put(ECG, ECG_MASK);
		stringToMask.put(EPS, EPS_MASK);
		stringToMask.put(ES, ES_MASK);
		stringToMask.put(GM, GM_MASK);
		stringToMask.put(HC, HC_MASK);
		stringToMask.put(HD, HD_MASK);
		stringToMask.put(IO, IO_MASK);
		stringToMask.put(IVUS, IVUS_MASK);
		stringToMask.put(LS, LS_MASK);
		stringToMask.put(MG, MG_MASK);
		stringToMask.put(MR, MR_MASK);
		stringToMask.put(NM, NM_MASK);
		stringToMask.put(OCT, OCT_MASK);
		stringToMask.put(OP, OP_MASK);
		stringToMask.put(OPM, OPM_MASK);
		stringToMask.put(OPR, OPR_MASK);
		stringToMask.put(OPV, OPV_MASK);
		stringToMask.put(OT, OT_MASK);
		stringToMask.put(PR, PR_MASK);
		stringToMask.put(PET, PET_MASK);
		stringToMask.put(PX, PX_MASK);
		stringToMask.put(REG, REG_MASK);
		stringToMask.put(RF, RF_MASK);
		stringToMask.put(RG, RG_MASK);
		stringToMask.put(RTDOSE, RTDOSE_MASK);
		stringToMask.put(RTIMAGE, RTDOSE_MASK);
		stringToMask.put(RTPLAN, RTPLAN_MASK);
		stringToMask.put(RTRECORD, RTRECORD_MASK);
		stringToMask.put(RTSTRUCT, RTSTRUCT_MASK);
		stringToMask.put(SEG, SEG_MASK);
		stringToMask.put(SM, SM_MASK);
		stringToMask.put(SMR, SMR_MASK);
		stringToMask.put(SR, SR_MASK);
		stringToMask.put(ST, ST_MASK);
		stringToMask.put(TG, TG_MASK);
		stringToMask.put(US, US_MASK);
		stringToMask.put(XA, XA_MASK);
		stringToMask.put(XC, XC_MASK);

		maskToString.put(AU_MASK, AU);
		maskToString.put(BI_MASK, BI);
		maskToString.put(CD_MASK, CD);
		maskToString.put(CR_MASK, CR);
		maskToString.put(CT_MASK, CT);
		maskToString.put(DD_MASK, DD);
		maskToString.put(DG_MASK, DG);
		maskToString.put(DSA_MASK, DSA);
		maskToString.put(DX_MASK, DX);
		maskToString.put(ECG_MASK, ECG);
		maskToString.put(EPS_MASK, EPS);
		maskToString.put(ES_MASK, ES);
		maskToString.put(GM_MASK, GM);
		maskToString.put(HC_MASK, HC);
		maskToString.put(HD_MASK, HD);
		maskToString.put(IO_MASK, IO);
		maskToString.put(IVUS_MASK, IVUS);
		maskToString.put(LS_MASK, LS);
		maskToString.put(MG_MASK, MG);
		maskToString.put(MR_MASK, MR);
		maskToString.put(NM_MASK, NM);
		maskToString.put(OCT_MASK, OCT);
		maskToString.put(OP_MASK, OP);
		maskToString.put(OPM_MASK, OPM);
		maskToString.put(OPR_MASK, OPR);
		maskToString.put(OPV_MASK, OPV);
		maskToString.put(OT_MASK, OT);
		maskToString.put(PR_MASK, PR);
		maskToString.put(PET_MASK, PET);
		maskToString.put(PX_MASK, PX);
		maskToString.put(REG_MASK, REG);
		maskToString.put(RF_MASK, RF);
		maskToString.put(RG_MASK, RG);
		maskToString.put(RTDOSE_MASK, RTDOSE);
		maskToString.put(RTIMAGE_MASK, RTDOSE);
		maskToString.put(RTPLAN_MASK, RTPLAN);
		maskToString.put(RTRECORD_MASK, RTRECORD);
		maskToString.put(RTSTRUCT_MASK, RTSTRUCT);
		maskToString.put(SEG_MASK, SEG);
		maskToString.put(SM_MASK, SM);
		maskToString.put(SMR_MASK, SMR);
		maskToString.put(SR_MASK, SR);
		maskToString.put(ST_MASK, ST);
		maskToString.put(TG_MASK, TG);
		maskToString.put(US_MASK, US);
		maskToString.put(XA_MASK, XA);
		maskToString.put(XC_MASK, XC);

		stringToDesc.put(AU, "Audio");
		stringToDesc.put(BI, "Biomagnetic Imaging");
		stringToDesc.put(CD, "Color Flow Doppler");
		stringToDesc.put(CR, "Computed Radiography");
		stringToDesc.put(CT, "Computed Tomography");
		stringToDesc.put(DD, "Duplex Doppler");
		stringToDesc.put(DG, "Diaphanography");
		stringToDesc.put(DSA, "Digital Subtraction Angiography");
		stringToDesc.put(DX, "Digital Radiography");
		stringToDesc.put(ECG, "Electrocardiography");
		stringToDesc.put(EPS, "Cardiac Electrophysiology");
		stringToDesc.put(ES, "Endoscopy");
		stringToDesc.put(GM, "General Microscopy");
		stringToDesc.put(HC, "Hard Copy");
		stringToDesc.put(HD, "Hemodynamic Waveform");
		stringToDesc.put(IO, "Intra-Oral Radiography");
		stringToDesc.put(IVUS, "Intravascular Ultrasound");
		stringToDesc.put(LS, "Laser Surface Scan");
		stringToDesc.put(MG, "Mammography");
		stringToDesc.put(MR, "Magnetic Resonance");
		stringToDesc.put(NM, "Nuclear Medicine");
		stringToDesc.put(OCT, "Optical Coherence Tomography");
		stringToDesc.put(OP, "Ophthalmic Photography");
		stringToDesc.put(OPM, "Ophthalmic Mapping");
		stringToDesc.put(OPR, "Ophthalmic Refraction");
		stringToDesc.put(OPV, "Ophthalmic Visual Field");
		stringToDesc.put(OT, "Other");
		stringToDesc.put(PR, "Presentation State");
		stringToDesc.put(PET, "Positron Emission Tomography");
		stringToDesc.put(PX, "Panoramic X-Ray");
		stringToDesc.put(REG, "Registration");
		stringToDesc.put(RF, "Radio Fluoroscopy");
		stringToDesc.put(RG, "Radiographic Imaging");
		stringToDesc.put(RTDOSE, "Radiotherapy Dose");
		stringToDesc.put(RTIMAGE, "Radiotherapy Image");
		stringToDesc.put(RTPLAN, "Radiotherapy Plan");
		stringToDesc.put(RTRECORD, "RT Treatment Record");
		stringToDesc.put(RTSTRUCT, "Radiotherapy Structure Set");
		stringToDesc.put(SEG, "Segmentation");
		stringToDesc.put(SM, "Slide Microscopy");
		stringToDesc.put(SMR, "Stereometric Relationship");
		stringToDesc.put(SR, "SR Document");
		stringToDesc.put(ST, "Single-Photon Emission Computed Tomography");
		stringToDesc.put(TG, "Thermography");
		stringToDesc.put(US, "Ultrasound");
		stringToDesc.put(XA, "X-Ray Angiography");
		stringToDesc.put(XC, "External-Camera Photography");
	}

	/**
	 * Returns the bitmask corresponding to the modality.
	 * @param modality the modality
	 * @return the bitmask or null if unknown
	 */
	public static long bitmask(String modality)
	{
		Long value = stringToMask.get(modality);
		return (value == null) ? 0L : value;
	}

	/**
	 * Returns the description of the modality.
	 * @param modality the modality
	 * @return the description
	 */
	public static String description(String modality)
	{
		return stringToDesc.get(modality);
	}

	/**
	 * Returns the modality corresponding to the bitmask.
	 * @param bitmask the bitmask
	 * @return the modality
	 */
	public static String string(long bitmask)
	{
		return maskToString.get(bitmask);
	}

	/**
	 * Returns a comma-separated list of modalities present in the bitmask.
	 * @param bitmask the bitmask
	 * @return the modalities
	 */
	public static String allStrings(long bitmask)
	{
		String value = "";
		Set<Long> keys = maskToString.keySet();
		for (long key : keys)
		{
			key &= bitmask;
			String strValue = maskToString.get(key);
			if (strValue != null)
			{
				value += strValue+",";
			}
		}
		return value.isEmpty()? value : value.substring(0, value.length()-1);
	}

	// Prevent instantiation
	private Modality()
	{}
}
