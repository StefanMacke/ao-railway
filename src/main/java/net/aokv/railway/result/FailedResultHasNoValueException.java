package net.aokv.railway.result;

public class FailedResultHasNoValueException extends RuntimeException
{
	private static final long serialVersionUID = -8674147111084040578L;

	private final Object error;

	public FailedResultHasNoValueException(final Object error)
	{
		super();
		this.error = error;
	}

	public Object getError()
	{
		return error;
	}

	@Override
	public String toString()
	{
		return "FailedResultHasNoValueException [error=" + getError() + "]";
	}
}
