package net.aokv.railway.valueobjects;

import java.io.Serializable;

import com.google.common.base.Objects;

public abstract class ValueObject<T extends Object> implements Serializable
{
	private static final long serialVersionUID = 1524173187994925966L;

	private final T value;

	/**
	 * Creates a new ValueObject from the given value.
	 *
	 * @param value The value.
	 * @throws IllegalArgumentException If value is null.
	 */
	protected ValueObject(final T value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException(
					String.format("Value of %s may not be null.", getClass().getSimpleName()));
		}
		this.value = value;
	}

	/**
	 * Returns the value.
	 *
	 * @return The value.
	 */
	public final T getValue()
	{
		return value;
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(getValue());
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final ValueObject<T> other = (ValueObject<T>) obj;
		return Objects.equal(getValue(), other.getValue());
	}

	@Override
	public String toString()
	{
		return String.format("%s (%s)", getClass().getSimpleName(), String.valueOf(value));
	}
}
