package org.nrg.xnatx.dicomweb.testsRemoveMe;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.BulkData;
import org.dcm4che3.data.Tag;
//import org.dcm4che3.imageio.codec.Transcoder;
import org.dcm4che3.io.BulkDataCreator;
import org.dcm4che3.io.BulkDataDescriptor;
import org.dcm4che3.io.DicomInputStream;
import org.hibernate.cache.ehcache.management.impl.BeanUtils;
import org.nrg.xnatx.dicomweb.conf.DicomwebDeviceConfiguration;
import org.nrg.xnatx.dicomweb.entity.DwInstance;
import org.nrg.xnatx.dicomweb.entity.DwPatient;
import org.nrg.xnatx.dicomweb.entity.util.EntityProperties;
import org.nrg.xnatx.dicomweb.toolkit.DicomwebUtils;
import org.nrg.xnatx.dicomweb.toolkit.IcrDicomFileReader;
import org.nrg.xnatx.dicomweb.wado.CompressedFramesOutput;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.Map;


public class ParsingTestChe3
{
	public static void main(String[] args) throws IOException
	{
		DwPatient patient = new DwPatient();
		Map<String,Object> props = EntityProperties.newPropsMap(
			new String[]{"subjectId","patientId","numberOfStudies"},
			new Object[]{"SUB-001","Patient X",3});
		EntityProperties.setExampleProps(patient, props);

		DwInstance instance2 = new DwInstance();
		Class<?> clazz = instance2.getClass();
		Field[] field = clazz.getDeclaredFields();
		int x = 10;

		/*
		String xnatScanPath = "D:/docker/xnat/1.8.7.1/xnat-data/archive/proj1/arc001/SUBJ-001_CT_1/SCANS";
		String sessionId = "XNAT_E00001";
		String xnatExperimentScanUrl = "/data/experiments/XNAT_E00001/scans/";

		Map<String, String> seriesUidToScanIdMap = new LinkedHashMap<>();
		seriesUidToScanIdMap.put(
			"2.16.840.1.113662.2.12.0.3057.1241703565.43", "2");
		seriesUidToScanIdMap.put(
			"1.2.246.352.71.2.320687012.27257.20090508140213", "3");
		seriesUidToScanIdMap.put(
			"1.2.246.352.71.2.320687012.27353.20090508165851", "4");
		seriesUidToScanIdMap.put(
			"1.2.246.352.71.2.320687012.28240.20090603082420", "14");
		seriesUidToScanIdMap.put(
			"2.25.962878583333520700174526601067280308721", "99");

		DicomwebToolkit dwService = new DicomwebToolkit();

		PathScan<Attributes> dwPathScan = dwService.createPathScan();
		DicomwebPathScanContext dwScanContext = new DicomwebPathScanContext(
			sessionId, xnatExperimentScanUrl, seriesUidToScanIdMap);
		dwPathScan.addContext(dwScanContext);
		dwPathScan.scan(xnatScanPath);
		*/

		/*
		String nameStr = "Dave^Brown";
		PersonName pn = new PersonName(nameStr, true);

		String Alphabetic = pn.toString(PersonName.Group.Alphabetic, false);
		String Ideographic = pn.toString(PersonName.Group.Ideographic, false);
		String Phonetic = pn.toString(PersonName.Group.Phonetic, false);

		String FamilyName = pn.get(PersonName.Group.Alphabetic, PersonName.Component.FamilyName);
		String GivenName = pn.get(
				PersonName.Group.Alphabetic, PersonName.Component.GivenName);
		String MiddleName = pn.get(
				PersonName.Group.Alphabetic, PersonName.Component.MiddleName);

		*/

		/*

/*
		// Look for in dcm2che3
			TransferSyntaxType => pixeldataEncapsulated, frameSpanMultipleFragments
			Transcoder => compressPixelData() & decompressPixelData()
			ImageDescriptor => transcoder.getImageDescriptor();
				(ARC) StoreServiceImpl => private ArchiveCompressionRule selectCompressionRule
			(ARC) WadoRs private methods: - all referenced in private enum Output {}
				writeBulkdata
				writeFrames
					writeUncompressedFrames
					writeCompressedFrames
					writeDecompressedFrames
					writeCompressedMultiFrameImage
				writeDICOM
				writeZIP
			(ARC) WadoRs private enum Target
				Study(WadoRS::dicomOrBulkdataOrZIP),
				Series(WadoRS::dicomOrBulkdataOrZIP),
				Instance(WadoRS::dicomOrBulkdataOrZIP),
				Frame(WadoRS::bulkdataFrame),
				Bulkdata(WadoRS::bulkdataPath),
				StudyMetadata(WadoRS::metadataJSONorXML),
				SeriesMetadata(WadoRS::metadataJSONorXML),
				InstanceMetadata(WadoRS::metadataJSONorXML),
				RenderedStudy(WadoRS::render),
				RenderedSeries(WadoRS::render),
				RenderedInstance(WadoRS::render),
				RenderedFrame(WadoRS::renderFrame),
				StudyThumbnail(WadoRS::thumbnail)
			(ARC) ObjectType.java =>
				UncompressedSingleFrameImage

		*/

		String spoolDirname = "D:\\test\\dicomweb\\out";
		Path spoolDir = Paths.get(spoolDirname);
		String fname = "D:\\dev\\xnat-dicom-web\\dicom-web-2022.04.19\\dicom-web-2022.04.19\\0010\\2.16.840.1.113995.3.110.3.0.10118.2000002.657111.4.dcm";
//		String fname = "D:\\test\\sarcopenia\\output\\report_1.3.6.1.4.1.14519.5.2.1.7695.4007.115512319570807352125051359179.dcm";
//		String fname = "D:\\test\\dicomweb\\in\\mf_CT_Explicit VR LE.dcm";
//		String fname = "D:\\test\\dicomweb\\in\\sf_CT1_J2KI.dcm";
		Path inst = Paths.get(fname);

		String bulkDataFilename = "D:\\test\\dicomweb\\out\\bd";


		////////////////////////////////////
//		BulkDataDescriptor bulkDataDescriptor = DicomwebDeviceConfiguration.getBulkDataDescriptor();
//		Transcoder transcoder = new Transcoder(inst.toFile());
//		transcoder.setIncludeBulkData(DicomInputStream.IncludeBulkData.URI);
//		transcoder.setBulkDataDescriptor(bulkDataDescriptor);
//		transcoder.setPixelDataBulkDataURI("");
//		transcoder.setConcatenateBulkDataFiles(true);
//		transcoder.setBulkDataDirectory(spoolDir.toFile());
//		transcoder.setIncludeFileMetaInformation(true);
//		transcoder.setDeleteBulkDataFiles(false);
//		transcoder.transcode(new TranscoderHandler(bulkDataFilename));


		////////////////////////////////////
		IcrDicomFileReader idfr = new IcrDicomFileReader(inst.toFile());
		try
		{
			Attributes at = idfr.read();
			DwInstance instance = new DwInstance();

//			instance.setMetadata(at);
//			int removed = at.removeAllBulkData();

			byte[] encAttrs1 = DicomwebUtils.encodeAttributes(at);
			System.out.println("Encoded1 size = " + encAttrs1.length);

			byte[] encAttrs2 = DicomwebUtils.encodeMetadata(at);
			System.out.println("Encoded2 size = " + encAttrs2.length);

			Attributes decAttrs2 = DicomwebUtils.decodeMetadata(encAttrs2);

			int yx = 10;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		/////////////////////////////////////


		//==> Use this to store metadata////////////////////////
		BulkDataDescriptor bulkDataDescriptor = DicomwebDeviceConfiguration.getBulkDataDescriptor();
		DicomInputStream dis = new DicomInputStream(inst.toFile());
		dis.setURI("/File");
		dis.setIncludeBulkData(DicomInputStream.IncludeBulkData.URI);
		dis.setBulkDataDescriptor(bulkDataDescriptor);
		dis.setConcatenateBulkDataFiles(true);
		dis.setBulkDataDirectory(spoolDir.toFile());

//		dis.setBulkDataCreator(new BulkDataCreator()
//		{
//			@Override
//			public BulkData createBulkData(DicomInputStream dis)
//				throws IOException
//			{
//				dis.skipFully(dis.length());
//				return new BulkData(null, "", dis.bigEndian());
//			}
//		});


		Attributes opAttrs = new Attributes();
		dis.readAllAttributes(opAttrs);

//		opAttrs = dis.readDataset(-1, Tag.PixelData);
		if (opAttrs.contains(Tag.PixelData)) {
			opAttrs.setValue(Tag.PixelData, opAttrs.getVR(Tag.PixelData), new BulkData(null, "", dis.bigEndian()));
		}

		//// <===END use this ............


		int xu = 10;

		// dis.getBulkDataFiles
		// dis.createBulkData


		////////////////////////////////////
//		IcrDicomFileReader idfr = new IcrDicomFileReader(inst.toFile());
//		try
//		{
//			Attributes at = idfr.read();
//			int removed = at.removeAllBulkData();
//			int x = 10;
//		}
//		catch (IOException e)
//		{
//			throw new RuntimeException(e);
//		}

		/////////////////////////////////////
/*		DicomInputStream dis = null;
		EncapsulatedPixelDataImageInputStream encapsulatedPixelData = null;
		try {
			dis = new DicomInputStream(inst.toFile());
			BulkDataDescriptor v1 = dis.getBulkDataDescriptor();
			File v2 = dis.getBulkDataDirectory();
			List<File> v3 = dis.getBulkDataFiles();
			String v4 = dis.getBulkDataFilePrefix();
			String v5 = dis.getBulkDataFileSuffix();
			DicomInputStream.IncludeBulkData v6 = dis.getIncludeBulkData();


			Attributes attrs = dis.readDatasetUntilPixelData();
			if (dis.tag() != Tag.PixelData || dis.length() != -1)
				throw new IOException("No or incorrect encapsulated compressed pixel data in requested object");

			ImageDescriptor imageDescriptor = new ImageDescriptor(attrs);
			String tsuid = dis.getTransferSyntax();
			TransferSyntaxType tsType = TransferSyntaxType.forUID(tsuid);

			encapsulatedPixelData = new EncapsulatedPixelDataImageInputStream(dis, imageDescriptor, tsType);

			int numFrames = attrs.getInt(Tag.NumberOfFrames, 0);
			for (int i = 0; i < numFrames; i++) {
				encapsulatedPixelData.seekNextFrame();
			}

		} catch (IOException e) {
			int x = 10;
		} finally {
			SafeClose.close(encapsulatedPixelData);
			SafeClose.close(dis);
		}
*/

		////////////////////////////////////

		int[] frameList = {2, 1, 4};
		try
		{
//			DecompressFramesOutput outputHandler = new DecompressFramesOutput(inst, frameList, spoolDir);
			CompressedFramesOutput outputHandler = new CompressedFramesOutput(
				inst, frameList, spoolDir);
			for (int frame : frameList)
			{
				Path framefn = Paths.get("frame-" + frame + ".jpg");
				Path framePath = spoolDir.resolve(framefn);
				FileOutputStream output = new FileOutputStream(
					framePath.toFile());
				outputHandler.writeTo(output);
			}
		}
		catch (Exception ex)
		{
			int y = 10;
		}


//		DicomInputStream dcmIS = new DicomInputStream(new File(fname));
//		Attributes attrs = dcmIS.readDatasetUntilPixelData();
//		System.out.print(attrs);
//
//		if (dcmIS.tag() != Tag.PixelData || dcmIS.length() != -1 || !dcmIS.readItemHeader()) {
//			System.out.print("No or incorrect encapsulated compressed pixel data.");
//		} else {
//			dcmIS.skipFully(dcmIS.length());
//		}
//
//		dcmIS.close();

		/*
		#	retrieveURL
		 */

		/*
		# org.dcm4chee.arc.query classes
		# QIDORS::private Response search QueryAttributes
			1.	splitAndDecode(info.getQueryParameters(false)
			2.	public QueryAttributes(MultivaluedMap<String, String> map, Map<String, AttributeSet> attributeSetMap)
			3.	ctx.setReturnKeys => public Attributes getReturnKeys
			4.	ctx.setReturnPrivate(queryAttrs.isIncludePrivate());
			5. try (Query query = model.createQuery(service, ctx))
			6. query.executeQuery(fetchSize, ...
			7. private Object writeJSON
					private Attributes adjust() -> void addRetrieveURL


		 */

		/*
		#	fuzzy search
			fuzzyAlgorithmClass = org.dcm4che3.soundex.ESoundex



		#	AttributeFilter
			public class AttributeFilter

			PatientMgtContextImpl
				this.attributeFilter
				this.studyAttributeFilter
				this.fuzzyStr

			ArchiveDeviceFactory
				private static void addArchiveDeviceExtension
					ext.setAttributeFilter(Entity.Patient ...

					Also check
						QIDO_STUDY_ATTRS
						DIFF_STUDY_ATTRS, DIFF_PAT_ATTRS ...

			ArchiveDeviceExtension
				private final EnumMap<Entity,AttributeFilter> attributeFilters
				public AttributeFilter getAttributeFilter(Entity entity)
		 */
	}

//	private static final class TranscoderHandler implements Transcoder.Handler
//	{
//
//		private final String bulkDataFilename;
//
//		private TranscoderHandler(String bulkDataFilename)
//		{
//			this.bulkDataFilename = bulkDataFilename;
//		}
//
//		@Override
//		public OutputStream newOutputStream(Transcoder transcoder,
//			Attributes dataset) throws IOException
//		{
//			FileOutputStream stream = new FileOutputStream(
//				bulkDataFilename);//Files.newOutputStream(outputPath, new OpenOption[]{ StandardOpenOption.CREATE_NEW });
//
//			return stream;
//		}
//	}
}
