package net.aokv.railway.valueobjects;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ValueObjectShould
{
	@SuppressWarnings("serial")
	private static class MyStringValueObject extends ValueObject<String>
	{
		public MyStringValueObject(final String wert)
		{
			super(wert);
		}
	}

	@SuppressWarnings("serial")
	private static class MyIntegerValueObject extends ValueObject<Integer>
	{
		public MyIntegerValueObject(final Integer wert)
		{
			super(wert);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> ValueObject<T> createValueObject(final T value)
	{
		if (value instanceof String)
		{
			return (ValueObject<T>) new MyStringValueObject((String) value);
		}
		return (ValueObject<T>) new MyIntegerValueObject((Integer) value);
	}

	@Test
	public void knowItsValue()
	{
		assertThat(createValueObject("The string").getValue(), is("The string"));
		assertThat(createValueObject(123).getValue(), is(123));
	}

	@Test
	public void computeTheSameHashCodeForIdenticalValues()
	{
		assertThat(createValueObject("The string").hashCode(),
				is(createValueObject("The string").hashCode()));
		assertThat(createValueObject(123).hashCode(),
				is(createValueObject(123).hashCode()));
	}

	@Test
	public void computeDifferentHashCodeForDifferentValues()
	{
		assertThat(createValueObject("The string").hashCode(),
				is(not(createValueObject("Another string").hashCode())));
		assertThat(createValueObject(123).hashCode(),
				is(not(createValueObject(124).hashCode())));
	}

	@Test
	public void beEqualToValueObjectWithSameValue()
	{
		assertThat(createValueObject("The string"),
				is(createValueObject("The string")));
		assertThat(createValueObject(123),
				is(createValueObject(123)));
	}

	@Test
	public void notBeEqualToValueObjectWithDifferentValue()
	{
		assertThat(createValueObject("The string"),
				is(not(createValueObject("Another string"))));
		assertThat(createValueObject(123),
				is(not(createValueObject(124))));
	}

	@Test
	public void throwExceptionIfValueIsNull()
	{
		try
		{
			new MyStringValueObject(null);
			fail("ValueObject should not be created with null as a value.");
		}
		catch (final IllegalArgumentException e)
		{
			assertThat(e.getMessage(), is("Value of MyStringValueObject may not be null."));
		}
		try
		{
			new MyIntegerValueObject(null);
			fail("ValueObject should not be created with null as a value.");
		}
		catch (final IllegalArgumentException e)
		{
			assertThat(e.getMessage(), is("Value of MyIntegerValueObject may not be null."));
		}
	}
}
