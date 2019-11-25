package org.nrg.xnatx.ohifviewer.etherj;

public interface ExceptionCode
{
	/**
	 * Returns the code associated with this ExceptionCode.
	 * @return the code
	 */
	String getCode();

	/**
	 * Returns the message associated with this ExceptionCode.
	 * @return the message
	 */
	String getMessage();
}
