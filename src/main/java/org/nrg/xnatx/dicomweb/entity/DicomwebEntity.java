package org.nrg.xnatx.dicomweb.entity;

import org.dcm4che3.data.Attributes;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;
import org.nrg.xnatx.dicomweb.conf.DicomwebDeviceConfiguration;
import org.nrg.xnatx.dicomweb.toolkit.DicomwebUtils;

import javax.persistence.*;
import java.io.IOException;

@MappedSuperclass
public abstract class DicomwebEntity extends AbstractHibernateEntity
{
	private byte[] encodedAttributes;
	private String revision;
	@Transient
	private Attributes cachedAttributes;

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		final AbstractHibernateEntity that = (AbstractHibernateEntity) o;
		return getId() == that.getId();
	}

	@Transient
	public Attributes getAttributes() throws IOException
	{
		if (cachedAttributes == null)
		{
			cachedAttributes = DicomwebUtils.decodeAttributes(encodedAttributes);
		}

		return cachedAttributes;
	}

	@Transient
	protected void setAttributes(Attributes attrs) throws IOException
	{
		cachedAttributes = new Attributes(attrs);
//		cachedAttributes.removeAllBulkData();
		encodedAttributes = DicomwebUtils.encodeAttributes(cachedAttributes);

		revision = DicomwebDeviceConfiguration.DICOMWEB_DATA_REVISION;
	}

	@Transient
	public abstract Attributes getQueryAttributes();

	@Basic(optional = false)
	public String getRevision()
	{
		return revision;
	}

	public void setRevision(String revision)
	{
		this.revision = revision;
	}

	public abstract void setData(Attributes attrs) throws IOException;

	@Basic(optional = false)
	@Column(name = "attrs")
	private byte[] getEncodedAttributes()
	{
		return encodedAttributes;
	}

	private void setEncodedAttributes(byte[] encodedAttributes)
	{
		this.encodedAttributes = encodedAttributes;
	}
}
