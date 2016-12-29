package net.aokv.railway.result;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Failure<TSuccess, TFailure> extends Result<TSuccess, TFailure>
{
	private final TFailure error;

	public Failure(final TFailure error)
	{
		this.error = error;
	}

	@Override
	public boolean isFailure()
	{
		return true;
	}

	@Override
	public TSuccess getValue()
	{
		throw new FailedResultHasNoValueException(getError());
	}

	@Override
	public TFailure getError()
	{
		return error;
	}

	@Override
	public String toString()
	{
		final StringBuilder result = new StringBuilder("Result (");
		result.append("Error: ");
		result.append(getError());
		result.append(')');
		return result.toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <TResult extends Result<?, TFailure>> TResult combine(
			final TResult otherResult)
	{
		return (TResult) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <TResult extends Result<T, TFailure>, T> TResult onSuccess(
			final Supplier<TResult> function)
	{
		return (TResult) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <TResult extends Result<T, TFailure>, T> TResult onSuccess(
			final Supplier<T> function, final Class<T> clazz)
	{
		return (TResult) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <TResult extends Result<TSuccess, TFailure>> TResult ensure(
			final Predicate<TSuccess> predicate, final TFailure error)
	{
		return (TResult) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <TResult extends Result<T, TFailure>, T> TResult flatMap(final Function<TSuccess, TResult> function)
	{
		return (TResult) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <TResult extends Result<T, TFailure>, T> TResult map(final Function<TSuccess, T> function)
	{
		return (TResult) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <TResult extends Result<T, TFailure>, T> TResult ifValueIsPresent(
			final Class<T> innerValue, final TFailure error)
	{
		return (TResult) new Failure<>(getError());
	}
}
