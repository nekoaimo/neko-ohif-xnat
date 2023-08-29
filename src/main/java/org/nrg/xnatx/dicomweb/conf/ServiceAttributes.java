package org.nrg.xnatx.dicomweb.conf;

import org.dcm4che3.data.Tag;

import java.util.*;

public class ServiceAttributes
{
	static public final int[] QIDO_STUDY_ATTRS = {
		Tag.StudyDate,
		Tag.StudyTime,
		Tag.AccessionNumber,
		Tag.ModalitiesInStudy,
		Tag.ReferringPhysicianName,
		Tag.PatientName,
		Tag.PatientID,
		Tag.PatientBirthDate,
		Tag.PatientSex,
		Tag.StudyID,
		Tag.StudyInstanceUID,
		Tag.StudyDescription,
		Tag.NumberOfStudyRelatedSeries,
		Tag.NumberOfStudyRelatedInstances
	};

	static public final int[] WADO_RS;

	static {
		WADO_RS = union(
			EntityAttributes.PATIENT_ATTRS,
			EntityAttributes.STUDY_ATTRS,
			EntityAttributes.SERIES_ATTRS,
			EntityAttributes.INSTANCE_ATTRS);
	}

	private static int[] union(int[]... attrs)
	{
		// Store unique tags only
		Set<Integer> c = new TreeSet<>();
		for (int[] attr : attrs)
			for (int i : attr)
				c.add(i);

		int[] a = new int[c.size()];
		int j = 0;
		for (int i : c)
			a[j++] = i;

		return a;
	}
}
