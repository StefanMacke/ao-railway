package net.aokv.railway.result;

public class InvalidValueException extends Exception
{
	private static final long serialVersionUID = -1592103915586236058L;

	public InvalidValueException(final String message)
	{
		super(message);
	}
}
