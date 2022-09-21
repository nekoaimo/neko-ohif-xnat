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

import icr.etherj.AbstractDisplayable;
import icr.etherj.Uids;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mo.alsad
 */
public class MeasurementCollection extends AbstractDisplayable
{
    private final Map<String, ImageMeasurement> imageMeasurements = new LinkedHashMap<>();
    private String created = "";
    private String description = "";
    private Equipment equipment = null;
    private CollectionImageReference imageReference = null;
    private String modified = "";
    private String name = "";
    private transient String path = "";
    private int revision = 1;
    private Subject subject = null;
    private User user = null;
    private String uuid = Uids.generateDicomUid();

    public MeasurementCollection()
    {
    }

    public ImageMeasurement addImageMeasurement(ImageMeasurement measurement)
    {
        return this.imageMeasurements.put(measurement.getUuid(), measurement);
    }

    public void display(PrintStream ps, String indent, boolean recurse)
    {
        ps.println(indent + getClass().getName());
        String pad = indent + "  * ";
        ps.println(pad + "UUID: " + uuid);
        ps.println(pad + "Name: " + name);
        ps.println(pad + "Description: " + description);
        ps.println(pad + "Created: " + created);
        ps.println(pad + "Modified: " + modified);
        ps.println(pad + "Revision: " + revision);

        if (subject != null) {
            subject.display(ps, indent + "  ");
        }

        if (user != null) {
            user.display(ps, indent + "  ");
        }

        if (equipment != null) {
            equipment.display(ps, indent + "  ");
        }

        if (imageReference != null) {
            imageReference.display(ps, indent + "  ");
        }

        ps.println(pad + "ImageMeasurements:");
        for (ImageMeasurement imm : this.getImageMeasurementList()) {
            imm.display(ps, indent + "    ");
        }
    }

    public String getCreated()
    {
        return created;
    }

    public String getDescription()
    {
        return description;
    }

    public Equipment getEquipment()
    {
        return equipment;
    }

    public ImageMeasurement getIamgeMeasurement(String uuid)
    {
        return this.imageMeasurements.get(uuid);
    }

    public int getIamgeMeasurementCount()
    {
        return this.imageMeasurements.size();
    }

    public List<ImageMeasurement> getImageMeasurementList()
    {
        return new ArrayList<>(this.imageMeasurements.values());
    }

    public CollectionImageReference getImageReference()
    {
        return imageReference;
    }

    public String getModified()
    {
        return modified;
    }

    public String getName()
    {
        return name;
    }

    public String getPath()
    {
        return path;
    }

    public int getRevision()
    {
        return revision;
    }

    public Subject getSubject()
    {
        return subject;
    }

    public User getUser()
    {
        return user;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setCreated(String created)
    {
        this.created = created;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setEquipment(Equipment equipment)
    {
        this.equipment = equipment;
    }

    public void setImageReference(CollectionImageReference imageReference)
    {
        this.imageReference = imageReference;
    }

    public void setModified(String modified)
    {
        this.modified = modified;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public void setRevision(int revision)
    {
        this.revision = revision;
    }

    public void setSubject(Subject subject)
    {
        this.subject = subject;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public void setUuid(String uuid)
    {
        if (uuid != null && !uuid.isEmpty())
        {
            this.uuid = uuid;
        } else
        {
            throw new IllegalArgumentException("UUID must not be null or empty");
        }
    }
}
