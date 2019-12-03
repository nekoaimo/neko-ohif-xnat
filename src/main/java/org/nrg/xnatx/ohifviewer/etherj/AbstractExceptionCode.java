package org.nrg.xnatx.ohifviewer.etherj;

/**
 * Default implementation of ExceptionCode, to be inherited by subclasses.
 */
public abstract class AbstractExceptionCode implements ExceptionCode
{
	private String code;
	private String message;

	/**
	 * Constructs a new instance of AbstractExceptionCode
	 * @param message the message
	 * @param code the code
	 */
	public AbstractExceptionCode(String message, String code)
	{
		if ((code == null) || (code.length() != 5))
		{
			throw new IllegalArgumentException("Code must have 5 letters");
		}
		this.message = message;
		this.code = code;
	}

	@Override
	public String getCode()
	{
		return code;
	}

	@Override
	public String getMessage()
	{
		return message;
	}
}
