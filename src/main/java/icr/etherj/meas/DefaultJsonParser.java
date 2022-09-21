/*********************************************************************
 * Copyright (c) 2022, Institute of Cancer Research
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
package icr.etherj.meas;

import com.google.gson.*;

import icr.etherj.JsonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author mo.alsad
 */
public class DefaultJsonParser
{
    private static final Logger logger = LoggerFactory.getLogger(DefaultJsonParser.class);
    private static final Gson gson = new Gson();

    public MeasurementCollection parse(String path) throws JsonException, IOException, IllegalArgumentException
    {
        return this.parse(new File(path));
    }

    public MeasurementCollection parse(File file) throws JsonException, IOException, IllegalArgumentException
    {
        return this.parse(new FileInputStream(file), file.getAbsolutePath());
    }

    public MeasurementCollection parse(InputStream stream) throws JsonException, IOException, IllegalArgumentException
    {
        return this.parse(stream, "");
    }

    public MeasurementCollection parse(InputStream stream, String path) throws JsonException, IOException, IllegalArgumentException
    {
        MeasurementCollection measc = null;

        try
        {
            JsonParser parser = new JsonParser();
            JsonElement rootNode = parser.parse(new InputStreamReader(stream));
            JsonObject meascjo = rootNode.getAsJsonObject();

            measc = new MeasurementCollection();
            if (path != null && !path.isEmpty()) {
                measc.setPath(path);
                logger.debug("Parsing MeasurementCollection from: {}", path);
            }
            measc.setUuid(meascjo.get("uuid").getAsString());
            measc.setName(meascjo.get("name").getAsString());
            measc.setDescription(meascjo.get("description").getAsString());
            measc.setCreated(meascjo.get("created").getAsString());
            measc.setModified(meascjo.get("modified").getAsString());
            measc.setRevision(meascjo.get("revision").getAsInt());

            measc.setUser(gson.fromJson(meascjo.get("user"), User.class));
            measc.setSubject(gson.fromJson(meascjo.get("subject"), Subject.class));
            measc.setEquipment(gson.fromJson(meascjo.get("equipment"), Equipment.class));

            measc.setImageReference(gson.fromJson(meascjo.get("imageReference"), CollectionImageReference.class));

            JsonArray imja = meascjo.get("imageMeasurements").getAsJsonArray();
            for (int i = 0; i < imja.size(); i++)
            {
                ImageMeasurement im = parseImageMeasurement(imja.get(i).getAsJsonObject());
                measc.addImageMeasurement(im);
            }

        } catch (JsonParseException ex)
        {
            throw new JsonException(ex);
        }

        return measc;
    }

    private ImageMeasurement parseImageMeasurement(JsonObject imjo)
    {
        ImageMeasurement im = new ImageMeasurement();

        im.setUuid(imjo.get("uuid").getAsString());
        im.setToolType(imjo.get("toolType").getAsString());
        im.setName(imjo.get("name").getAsString());
        im.setDescription(imjo.get("description").getAsString());

        im.setColor(imjo.get("color").getAsString());
        im.setLineThickness(imjo.get("lineThickness").getAsInt());
        im.setDashedLine(imjo.get("dashedLine").getAsBoolean());
        im.setVisible(imjo.get("visible").getAsBoolean());

        im.setImageReference(gson.fromJson(imjo.get("imageReference"), ImageReference.class));

        im.setCodingSequence(imjo.get("codingSequence").toString());
        im.setViewport(imjo.get("viewport").toString());
        im.setData(imjo.get("data").toString());

        JsonArray mja = imjo.get("measurements").getAsJsonArray();
        for (int i = 0; i < mja.size(); i++)
        {
            Measurement meas = gson.fromJson(mja.get(i), Measurement.class);
            im.addMeasurement(meas);
        }

        return im;
    }
}
