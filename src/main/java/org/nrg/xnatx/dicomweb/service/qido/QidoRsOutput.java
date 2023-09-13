package org.nrg.xnatx.dicomweb.service.qido;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.VR;
import org.dcm4che3.json.JSONWriter;
import org.nrg.xnatx.dicomweb.toolkit.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.json.Json;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import java.util.EnumSet;
import java.util.List;

public enum QidoRsOutput
{
	JSON
		{
			@Override
			Object write(List<Attributes> matches) throws Exception
			{
				return (StreamingResponseBody) out -> {
					JsonGenerator gen = Json.createGenerator(out);
					JSONWriter writer = encodeAsJSONNumber(new JSONWriter(gen));
					gen.writeStartArray();
					for (Attributes match : matches)
						writer.write(match);
					gen.writeEnd();
					gen.flush();
				};
			}

			@Override
			MediaType type()
			{
				return MediaTypes.APPLICATION_DICOM_JSON;
			}
		};

	private static final EnumSet<VR> encodeAsJSONNumber = EnumSet.noneOf(
		VR.class);

	private static JSONWriter encodeAsJSONNumber(JSONWriter writer)
	{
		encodeAsJSONNumber.forEach(
			vr -> writer.setJsonType(vr, JsonValue.ValueType.NUMBER));
		return writer;
	}

	abstract MediaType type();

	abstract Object write(List<Attributes> matches) throws Exception;
}
