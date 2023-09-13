package org.nrg.xnatx.dicomweb.service.qido;

import org.dcm4che3.data.Attributes;
import org.nrg.xnatx.dicomweb.toolkit.QueryRetrieveLevel2;
import org.nrg.xnatx.dicomweb.toolkit.query.OrderByTag;

import java.util.List;
import java.util.Map;

public class QidoRsContext
{
	private Map<String,String> xnatIds;
	private QueryRetrieveLevel2 queryRetrieveLevel;
	private Attributes queryKeys;
	private Attributes returnKeys;
	private boolean returnPrivate;
	private List<OrderByTag> orderByTags;
	// ToDo - NOTE: limit, size and fuzzymatching are not supported
	private int offset;
	private int limit;
	private boolean fuzzymatching;
	//
	private List<Attributes> matchingResults;

	public int getLimit()
	{
		return limit;
	}

	public void setLimit(int limit)
	{
		this.limit = limit;
	}

	public List<Attributes> getMatchingResults()
	{
		return matchingResults;
	}

	public void setMatchingResults(List<Attributes> matchingResults)
	{
		this.matchingResults = matchingResults;
	}

	public int getOffset()
	{
		return offset;
	}

	public void setOffset(int offset)
	{
		this.offset = offset;
	}

	public List<OrderByTag> getOrderByTags()
	{
		return orderByTags;
	}

	public void setOrderByTags(
		List<OrderByTag> orderByTags)
	{
		this.orderByTags = orderByTags;
	}

	public QueryRetrieveLevel2 getQueryRetrieveLevel()
	{
		return queryRetrieveLevel;
	}

	public void setQueryRetrieveLevel(QueryRetrieveLevel2 queryRetrieveLevel)
	{
		this.queryRetrieveLevel = queryRetrieveLevel;
	}

	public Attributes getQueryKeys()
	{
		return queryKeys;
	}

	public void setQueryKeys(Attributes queryKeys)
	{
		this.queryKeys = queryKeys;
	}

	public Attributes getReturnKeys()
	{
		return returnKeys;
	}

	public void setReturnKeys(Attributes returnKeys)
	{
		this.returnKeys = returnKeys;
	}

	public Map<String,String> getXnatIds()
	{
		return xnatIds;
	}

	public void setXnatIds(Map<String,String> xnatIds)
	{
		this.xnatIds = xnatIds;
	}

	public boolean isFuzzymatching()
	{
		return fuzzymatching;
	}

	public void setFuzzymatching(boolean fuzzymatching)
	{
		this.fuzzymatching = fuzzymatching;
	}

	public boolean isReturnPrivate()
	{
		return returnPrivate;
	}

	public void setReturnPrivate(boolean returnPrivate)
	{
		this.returnPrivate = returnPrivate;
	}
}
