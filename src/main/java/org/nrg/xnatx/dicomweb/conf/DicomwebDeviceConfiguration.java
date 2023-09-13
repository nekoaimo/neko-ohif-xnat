package org.nrg.xnatx.dicomweb.conf;

import org.dcm4che3.data.UID;
import org.dcm4che3.io.BasicBulkDataDescriptor;
import org.dcm4che3.io.BulkDataDescriptor;
import org.dcm4che3.util.StringUtils;

import java.util.*;

public class DicomwebDeviceConfiguration
{
	public static final String BULK_DATA_DESCRIPTOR_ID = "default";
	public static final String BULK_DATA_LENGTH_THRESHOLD = "DS,FD,FL,IS,LT,OB,OD,OF,OL,OW,UC,UN,UR,UT=1024";
	public static final String DICOMWEB_DATA_REVISION = "1";
	// ToDo - Note: QIDO_MAX_NUMBER_OF_RESULTS and QUERY_FETCH_SIZE are not used
	public static final int QIDO_MAX_NUMBER_OF_RESULTS = 100;
	public static final int QUERY_FETCH_SIZE = 100;

	private static final EnumMap<Entity,AttributeFilter> attributeFilters = new EnumMap<>(
		Entity.class);
	private static final Map<AttributeSet.Type,Map<String,AttributeSet>> attributeSets = new EnumMap<>(
		AttributeSet.Type.class);
	private static final BasicBulkDataDescriptor bulkDataDescriptor;
	private static final Set<String> validModalities = new HashSet<>();
	private static final Set<String> validSopClassUids = new HashSet<>();

	static
	{
		bulkDataDescriptor = new BasicBulkDataDescriptor(
			BULK_DATA_DESCRIPTOR_ID);
		bulkDataDescriptor.setLengthsThresholdsFromStrings(
			BULK_DATA_LENGTH_THRESHOLD);

		createAttributeFilters();
		createAttributeSets();
		setValidModalities();
		setValidSopClassUids();
	}

	public static AttributeFilter getAttributeFilter(Entity entity)
	{
		AttributeFilter filter = attributeFilters.get(entity);
		if (filter == null)
		{
			throw new IllegalArgumentException(
				"No Attribute Filter for " + entity);
		}

		return filter;
	}

	public static Map<String,AttributeSet> getAttributeSet(AttributeSet.Type type)
	{
		return StringUtils.maskNull(attributeSets.get(type),
			Collections.emptyMap());
	}

	public static BulkDataDescriptor getBulkDataDescriptor()
	{
		return bulkDataDescriptor;
	}

	public static boolean isDicomwebModality(String modality)
	{
		return validModalities.contains(modality);
	}

	public static boolean isDicomwebSopClass(String uid)
	{
		return validSopClassUids.contains(uid);
	}

	private static void addAttributeSet(AttributeSet tags)
	{
		Map<String,AttributeSet> map = new LinkedHashMap<>();
		map.put(tags.getID(), tags);
		attributeSets.put(tags.getType(), map);
	}

	private static void createAttributeFilters()
	{
		attributeFilters.put(Entity.Patient,
			new AttributeFilter(EntityAttributes.PATIENT_ATTRS));
		attributeFilters.put(
			Entity.Study,
			new AttributeFilter(EntityAttributes.STUDY_ATTRS));
		attributeFilters.put(
			Entity.Series,
			new AttributeFilter(EntityAttributes.SERIES_ATTRS));
		attributeFilters.put(
			Entity.Instance,
			new AttributeFilter(EntityAttributes.INSTANCE_ATTRS));
	}

	private static void createAttributeSets()
	{
		addAttributeSet(newAttributeSet(AttributeSet.Type.QIDO_RS,
			0, "study",
			"Sample Study Attribute Set",
			null,
			ServiceAttributes.QIDO_STUDY_ATTRS));
		addAttributeSet(newAttributeSet(AttributeSet.Type.WADO_RS,
			0, "AttributeFilters",
			"Attribute Filters",
			null,
			ServiceAttributes.WADO_RS));
	}

	private static AttributeSet newAttributeSet(
		AttributeSet.Type type, int number, String id, String title,
		String desc, int[] tags, String... props)
	{
		AttributeSet attributeSet = new AttributeSet();
		attributeSet.setType(type);
		attributeSet.setID(id);
		attributeSet.setTitle(title);
		attributeSet.setNumber(number);
		attributeSet.setDescription(desc);
		attributeSet.setSelection(tags);
		attributeSet.setProperties(props);
		return attributeSet;
	}

	private static void setValidModalities()
	{
		validModalities.add("SM");
	}

	private static void setValidSopClassUids()
	{
		validSopClassUids.add(UID.VLWholeSlideMicroscopyImageStorage);
	}
}
