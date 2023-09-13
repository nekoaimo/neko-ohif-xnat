/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at https://github.com/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Agfa Healthcare.
 * Portions created by the Initial Developer are Copyright (C) 2011-2014
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.nrg.xnatx.dicomweb.toolkit;

import org.dcm4che3.data.UID;

import org.springframework.http.MediaType;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 	@author mo.alsad
 *  @author Gunter Zeilinger <gunterze@gmail.com>
 *
 */
@SuppressWarnings("unused")
public class MediaTypes {

	/**
	 * "application/dicom"
	 */
	public final static String APPLICATION_DICOM_VALUE = "application/dicom";

	/**
	 * "application/dicom"
	 */
	public final static MediaType APPLICATION_DICOM =
		new MediaType("application", "dicom");

	/**
	 * "application/dicom+xml"
	 */
	public final static String APPLICATION_DICOM_XML_VALUE = "application/dicom+xml";

	/**
	 * "application/dicom+xml"
	 */
	public final static MediaType APPLICATION_DICOM_XML =
		new MediaType("application", "dicom+xml");

	/**
	 * "application/dicom+json"
	 */
	public final static String APPLICATION_DICOM_JSON_VALUE = "application/dicom+json";

	/**
	 * "application/dicom+json"
	 */
	public final static MediaType APPLICATION_DICOM_JSON =
		new MediaType("application", "dicom+json");

	/**
	 * "image/*"
	 */
	public final static String IMAGE_WILDCARD_VALUE = "image/*";

	/**
	 * "image/*"
	 */
	public final static MediaType IMAGE_WILDCARD =
		new MediaType("image", "*");

	/**
	 * "image/gif"
	 */
	public final static String IMAGE_GIF_VALUE = "image/gif";

	/**
	 * "image/gif"
	 */
	public final static MediaType IMAGE_GIF =
		new MediaType("image", "gif");


	/**
	 * "image/png"
	 */
	public final static String IMAGE_PNG_VALUE = "image/png";

	/**
	 * "image/png"
	 */
	public final static MediaType IMAGE_PNG =
		new MediaType("image", "png");

	/**
	 * "image/jpeg"
	 */
	public final static String IMAGE_JPEG_VALUE = "image/jpeg";

	/**
	 * "image/jpeg"
	 */
	public final static MediaType IMAGE_JPEG =
		new MediaType("image", "jpeg");

	/**
	 * "image/jls"
	 */
	public final static String IMAGE_JLS_VALUE = "image/jls";

	/**
	 * "image/jls"
	 */
	public final static MediaType IMAGE_JLS =
		new MediaType("image", "jls");

	/**
	 * "image/jp2"
	 */
	public final static String IMAGE_JP2_VALUE = "image/jp2";

	/**
	 * "image/jp2"
	 */
	public final static MediaType IMAGE_JP2 =
		new MediaType("image", "jp2");

	/**
	 * "image/jpx"
	 */
	public final static String IMAGE_JPX_VALUE = "image/jpx";

	/**
	 * "image/dicom+jpeg-jpx"
	 */
	public final static MediaType IMAGE_JPX =
		new MediaType("image", "jpx");

	/**
	 * "image/dicom-rle"
	 */
	public final static String IMAGE_DICOM_RLE_VALUE = "image/dicom-rle";

	/**
	 * "image/dicom-rle"
	 */
	public final static MediaType IMAGE_DICOM_RLE =
		new MediaType("image", "dicom-rle");

	/**
	 * "video/*"
	 */
	public final static String VIDEO_WILDCARD_VALUE = "video/*";

	/**
	 * "video/*"
	 */
	public final static MediaType VIDEO_WILDCARD =
		new MediaType("video", "*");

	/**
	 * "video/mpeg"
	 */
	public final static String VIDEO_MPEG_VALUE = "video/mpeg";

	/**
	 * "video/mpeg"
	 */
	public final static MediaType VIDEO_MPEG =
		new MediaType("video", "mpeg");

	/**
	 * "video/mp4"
	 */
	public final static String VIDEO_MP4_VALUE = "video/mp4";

	/**
	 * "video/mp4"
	 */
	public final static MediaType VIDEO_MP4 =
		new MediaType("video", "mp4");

	/**
	 * "video/quicktime"
	 */
	public final static String VIDEO_QUICKTIME_VALUE = "video/quicktime";

	/**
	 * "video/quicktime"
	 */
	public final static MediaType VIDEO_QUICKTIME =
		new MediaType("video", "quicktime");

	/**
	 * "application/pdf"
	 */
	public final static String APPLICATION_PDF_VALUE = "application/pdf";

	/**
	 * "application/pdf"
	 */
	public final static MediaType APPLICATION_PDF =
		new MediaType("application", "pdf");

	/**
	 * "text/rtf"
	 */
	public final static String TEXT_RTF_VALUE = "text/rtf";

	/**
	 * "text/rtf"
	 */
	public final static MediaType TEXT_RTF =
		new MediaType("text", "rtf");

	/**
	 * "text/csv"
	 */
	public final static String TEXT_CSV_VALUE = "text/csv";

	/**
	 * "text/csv"
	 */
	public final static MediaType TEXT_CSV =
		new MediaType("text", "csv");

	/**
	 * "text/csv;charset=utf-8"
	 */
	public final static String TEXT_CSV_UTF8_VALUE = "text/csv;charset=utf-8";

	/**
	 * "text/csv;charset=utf-8"
	 */
	public final static MediaType TEXT_CSV_UTF8 =
		new MediaType("text", "csv", Charset.defaultCharset());

	/**
	 * "application/zip"
	 */
	public final static String APPLICATION_ZIP_VALUE = "application/zip";

	/**
	 * "application/zip"
	 */
	public final static MediaType APPLICATION_ZIP =
		new MediaType("application", "zip");

	/**
	 * "multipart/related"
	 */
	public final static String MULTIPART_RELATED_VALUE = "multipart/related";

	/**
	 * "multipart/related"
	 */
	public final static MediaType MULTIPART_RELATED =
		new MediaType("multipart", "related");

	/**
	 * "multipart/related;type=\"application/dicom\""
	 */
	public final static String MULTIPART_RELATED_APPLICATION_DICOM_VALUE = "multipart/related;type=\"application/dicom\"";

	/**
	 * "multipart/related;type=\"application/dicom\""
	 */
	public final static MediaType MULTIPART_RELATED_APPLICATION_DICOM =
		new MediaType("multipart", "related", Collections.singletonMap("type", APPLICATION_DICOM_VALUE));

	/**
	 * "multipart/related;type=\"application/dicom+xml\""
	 */
	public final static String MULTIPART_RELATED_APPLICATION_DICOM_XML_VALUE = "multipart/related;type=\"application/dicom+xml\"";

	/**
	 * "multipart/related;type=\"application/dicom+xml\""
	 */
	public final static MediaType MULTIPART_RELATED_APPLICATION_DICOM_XML =
		new MediaType("multipart", "related", Collections.singletonMap("type", APPLICATION_DICOM_XML_VALUE));

	/**
	 * "model/stl"
	 */
	public final static String MODEL_STL_VALUE = "model/stl";

	/**
	 * "model/stl"
	 */
	public final static MediaType MODEL_STL =
		new MediaType("model", "stl");

	/**
	 * "model/x.stl-binary"
	 */
	public final static String MODEL_X_STL_BINARY_VALUE = "model/x.stl-binary";

	/**
	 * "model/x.stl-binary"
	 */
	public final static MediaType MODEL_X_STL_BINARY =
		new MediaType("model", "x.stl-binary");

	/**
	 * "application/sla"
	 */
	public final static String APPLICATION_SLA_VALUE = "application/sla";

	/**
	 * "application/sla"
	 */
	public final static MediaType APPLICATION_SLA =
		new MediaType("application", "sla");

	/**
	 * "model/obj"
	 */
	public final static String MODEL_OBJ_VALUE = "model/obj";

	/**
	 * "model/obj"
	 */
	public final static MediaType MODEL_OBJ =
		new MediaType("model", "obj");

	/**
	 * "model/mtl"
	 */
	public final static String MODEL_MTL_VALUE = "model/mtl";

	/**
	 * "model/mtl"
	 */
	public final static MediaType MODEL_MTL =
		new MediaType("model", "mtl");

	/**
	 * "application/vnd.genozip"
	 */
	public final static String APPLICATION_VND_GENOZIP_VALUE = "application/vnd.genozip";

	/**
	 * "application/vnd.genozip"
	 */
	public final static MediaType APPLICATION_VND_GENOZIP =
		new MediaType("application", "vnd.genozip");


	public static MediaType forTransferSyntax(String ts) {
		MediaType type;
		switch (ts) {
			case UID.ExplicitVRLittleEndian:
			case UID.ImplicitVRLittleEndian:
				return MediaType.APPLICATION_OCTET_STREAM;
			case UID.JPEGLosslessSV1:
				return IMAGE_JPEG;
			case UID.JPEGLSLossless:
				return IMAGE_JLS;
			case UID.JPEG2000Lossless:
				return IMAGE_JP2;
			case UID.JPEG2000MCLossless:
				return IMAGE_JPX;
			case UID.RLELossless:
				return IMAGE_DICOM_RLE;
			case UID.JPEGBaseline8Bit:
			case UID.JPEGExtended12Bit:
			case UID.JPEGLossless:
				type = IMAGE_JPEG;
				break;
			case UID.JPEGLSNearLossless:
				type = IMAGE_JLS;
				break;
			case UID.JPEG2000:
				type = IMAGE_JP2;
				break;
			case UID.JPEG2000MC:
				type = IMAGE_JPX;
				break;
			case UID.MPEG2MPML:
			case UID.MPEG2MPHL:
				type = VIDEO_MPEG;
				break;
			case UID.MPEG4HP41:
			case UID.MPEG4HP41BD:
				type = VIDEO_MP4;
				break;
			default:
				throw new IllegalArgumentException("ts: " + ts);
		}
		return new MediaType(type.getType(), type.getSubtype(), Collections.singletonMap("transfer-syntax", ts));
	}

	public static String transferSyntaxOf(MediaType bulkdataMediaType) {
		String tsuid = bulkdataMediaType.getParameters().get("transfer-syntax");
		if (tsuid != null)
			return tsuid;

		String type = bulkdataMediaType.getType().toLowerCase();
		String subtype = bulkdataMediaType.getSubtype().toLowerCase();
		switch (type) {
			case "image":
				switch (subtype) {
					case "jpeg":
						return UID.JPEGLosslessSV1;
					case "jls":
					case "x-jls":
						return UID.JPEGLSLossless;
					case "jp2":
						return UID.JPEG2000Lossless;
					case "jpx":
						return UID.JPEG2000MCLossless;
					case "x-dicom-rle":
					case "dicom-rle":
						return UID.RLELossless;
				}
			case "video":
				switch (subtype) {
					case "mpeg":
						return UID.MPEG2MPML;
					case "mp4":
					case "quicktime":
						return UID.MPEG4HP41;
				}
		}
		return UID.ExplicitVRLittleEndian;
	}

	public static String sopClassOf(MediaType bulkdataMediaType) {
		String type = bulkdataMediaType.getType().toLowerCase();
		return type.equals("image") ? UID.SecondaryCaptureImageStorage
						 : type.equals("video") ? UID.VideoPhotographicImageStorage
								 : equalsIgnoreParameters(bulkdataMediaType, APPLICATION_PDF) ? UID.EncapsulatedPDFStorage
										 : equalsIgnoreParameters(bulkdataMediaType, MediaType.APPLICATION_XML) ? UID.EncapsulatedCDAStorage
												 : isSTLType(bulkdataMediaType) ? UID.EncapsulatedSTLStorage
														 : equalsIgnoreParameters(bulkdataMediaType, MODEL_OBJ) ? UID.EncapsulatedOBJStorage
																 : equalsIgnoreParameters(bulkdataMediaType, MODEL_MTL) ? UID.EncapsulatedMTLStorage
																		 : null;
	}

	public static boolean isSTLType(MediaType mediaType) {
		return equalsIgnoreParameters(mediaType, MODEL_STL)
						 || equalsIgnoreParameters(mediaType, MODEL_X_STL_BINARY)
						 || equalsIgnoreParameters(mediaType, APPLICATION_SLA);
	}

	public static boolean isSTLType(String type) {
		return MODEL_STL_VALUE.equalsIgnoreCase(type)
						 || MODEL_X_STL_BINARY_VALUE.equalsIgnoreCase(type)
						 || APPLICATION_SLA_VALUE.equalsIgnoreCase(type);
	}

	public static boolean equalsIgnoreParameters(MediaType type1, MediaType type2) {
		return type1.getType().equalsIgnoreCase(type2.getType())
						 &&  type1.getSubtype().equalsIgnoreCase(type2.getSubtype());
	}

	public static MediaType getMultiPartRelatedType(MediaType mediaType) {
		if (!MediaTypes.MULTIPART_RELATED.isCompatibleWith(mediaType))
			return null;

		String type = mediaType.getParameters().get("type");
		if (type == null)
			return MediaType.ALL;

		MediaType partType = MediaType.valueOf(type);
		if (mediaType.getParameters().size() > 1) {
			Map<String, String> params = new HashMap<>(mediaType.getParameters());
			params.remove("type");
			partType = new MediaType(partType.getType(), partType.getSubtype(), params);
		}
		return partType;
	}

	public static String getTransferSyntax(MediaType type) {
		return type != null && equalsIgnoreParameters(APPLICATION_DICOM, type)
						 ? type.getParameters().get("transfer-syntax")
						 : null;
	}

	public static MediaType applicationDicomWithTransferSyntax(String tsuid) {
		return new MediaType("application", "dicom", Collections.singletonMap("transfer-syntax", tsuid));
	}
}

