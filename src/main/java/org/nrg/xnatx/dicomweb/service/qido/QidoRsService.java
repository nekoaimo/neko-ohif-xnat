package org.nrg.xnatx.dicomweb.service.qido;

import lombok.extern.slf4j.Slf4j;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xnatx.dicomweb.conf.AttributeSet;
import org.nrg.xnatx.dicomweb.conf.DicomwebDeviceConfiguration;
import org.nrg.xnatx.dicomweb.service.hibernate.DicomwebDataService;
import org.nrg.xnatx.dicomweb.toolkit.DicomwebUtils;
import org.nrg.xnatx.dicomweb.toolkit.MediaTypeUtils;
import org.nrg.xnatx.dicomweb.toolkit.MediaTypes;
import org.nrg.xnatx.dicomweb.toolkit.query.QIDO;
import org.nrg.xnatx.dicomweb.toolkit.query.QueryAttributes;
import org.nrg.xnatx.plugin.PluginCode;
import org.nrg.xnatx.plugin.PluginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Service
@Slf4j
public class QidoRsService
{
	private final DicomwebDataService dwDataService;

	@Autowired
	public QidoRsService(final DicomwebDataService dwDataService)
	{
		this.dwDataService = dwDataService;
	}

	public ResponseEntity<StreamingResponseBody> search(
		XnatImagesessiondata sessionData,
		final HttpServletRequest request,
		final MultiValueMap<String,String> httpQueryParams, QidoRsModel model,
		String studyInstanceUid, String seriesInstanceUid, QIDO qido)
		throws PluginException
	{
		StringBuffer requestUrl = request.getRequestURL();
		QidoRsOutput output = selectOutput(request, httpQueryParams);

		try
		{
			Map<String,AttributeSet> attributeSetMap =
				DicomwebDeviceConfiguration.getAttributeSet(AttributeSet.Type.QIDO_RS);
			QueryAttributes queryAttrs = new QueryAttributes(httpQueryParams,
				attributeSetMap);
			QidoRsContext ctx = newQueryContext(httpQueryParams, queryAttrs, model,
				studyInstanceUid, seriesInstanceUid, qido);
			ctx.setXnatIds(DicomwebUtils.getXnatIds(sessionData));
			dwDataService.runQidoQuery(ctx);
		}
		catch (Exception e)
		{
			throw new PluginException(PluginCode.HttpInternalError, e);
		}

		return null;
	}

	private QidoRsContext newQueryContext(
		MultiValueMap<String,String> httpQueryParams, QueryAttributes queryAttrs,
		QidoRsModel model, String studyInstanceUid, String seriesInstanceUid,
		QIDO qido) throws Exception
	{
		QidoRsContext ctx = new QidoRsContext();

		ctx.setQueryRetrieveLevel(model.getQueryRetrieveLevel());

		// ToDo - NOTE: limit, size and fuzzymatching are not supported
		int offset = DicomwebUtils.parseInt(httpQueryParams.getFirst("offset"),
			"0|([1-9]\\d{0,4})");
		ctx.setOffset(offset);

		int limit = DicomwebUtils.parseInt(httpQueryParams.getFirst("limit"),
			"[1-9]\\d{0,4}");
		ctx.setLimit(limit);

		boolean fuzzymatching = Boolean.parseBoolean(
			httpQueryParams.getFirst("fuzzymatching"));
		ctx.setFuzzymatching(fuzzymatching);

		Attributes queryKeys = queryAttrs.getQueryKeys();
		ctx.setQueryKeys(queryKeys);

		ctx.setOrderByTags(queryAttrs.getOrderByTags());
		ctx.setReturnPrivate(queryAttrs.isIncludePrivate());
		ctx.setReturnKeys(queryAttrs.isIncludeAll()
												? null
												: queryKeys.isEmpty()
														? queryAttrs.getReturnKeys(qido.includedTags)
														: queryKeys);

		if (studyInstanceUid != null)
		{
			queryKeys.setString(Tag.StudyInstanceUID, VR.UI, studyInstanceUid);
		}
		if (seriesInstanceUid != null)
		{
			queryKeys.setString(Tag.SeriesInstanceUID, VR.UI, seriesInstanceUid);
		}

		return ctx;
	}

	private QidoRsOutput selectOutput(final HttpServletRequest request,
		final MultiValueMap<String,String> httpQueryParams) throws PluginException
	{
		List<String> headers = Collections.list(request.getHeaders("accept"));
		List<String> accept = httpQueryParams.get("accept");

		List<MediaType> acceptableMediaTypes = MediaTypeUtils.acceptableMediaTypesOf(
			headers, accept);

		if (acceptableMediaTypes.stream().anyMatch(
			((Predicate<MediaType>) MediaTypes.APPLICATION_DICOM_JSON::isCompatibleWith)
				.or(MediaType.APPLICATION_JSON::isCompatibleWith)))
		{
			return QidoRsOutput.JSON;
		}

		throw new PluginException("Media type in request is not acceptable",
			PluginCode.HttpMethodNotAcceptable);
	}
}
