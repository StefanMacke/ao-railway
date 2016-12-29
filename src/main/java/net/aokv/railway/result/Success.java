package net.aokv.railway.result;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Success<TSuccess, TFailure> extends Result<TSuccess, TFailure>
{
	private final Optional<TSuccess> value;

	public Success(final TSuccess value)
	{
		this.value = Optional.ofNullable(value);
	}

	@Override
	public boolean isFailure()
	{
		return false;
	}

	@Override
	public TSuccess getValue()
	{
		if (value.isPresent())
		{
			return value.get();
		}
		throw new EmptyResultHasNoValueException();
	}

	@Override
	public TFailure getError()
	{
		throw new SuccessfulResultHasNoErrorException();
	}

	@Override
	public String toString()
	{
		final StringBuilder result = new StringBuilder("Result (");
		result.append("Success");
		if (value.isPresent())
		{
			result.append(" with value <");
			result.append(getValue());
			result.append('>');
		}
		result.append(')');
		return result.toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <TResult extends Result<?, TFailure>> TResult combine(
			final TResult otherResult)
	{
		if (otherResult.isFailure())
		{
			return otherResult;
		}
		return (TResult) this;
	}

	@Override
	public <TResult extends Result<T, TFailure>, T> TResult onSuccess(
			final Supplier<TResult> function)
	{
		return function.get();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <TResult extends Result<T, TFailure>, T> TResult onSuccess(
			final Supplier<T> function, final Class<T> clazz)
	{
		return onSuccess(() -> (TResult) new Success<>(function.get()));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <TResult extends Result<TSuccess, TFailure>> TResult ensure(
			final Predicate<TSuccess> predicate, final TFailure error)
	{
		try
		{
			if (!predicate.test(getValue()))
			{
				return (TResult) new Failure<>(error);
			}
		}
		catch (final EmptyResultHasNoValueException exception)
		{
			throw exception;
		}
		catch (final Exception exception)
		{
			return (TResult) new Failure<>(error);
		}
		return (TResult) this;
	}

	@Override
	public <TResult extends Result<T, TFailure>, T> TResult flatMap(final Function<TSuccess, TResult> function)
	{
		return function.apply(getValue());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <TResult extends Result<T, TFailure>, T> TResult map(final Function<TSuccess, T> function)
	{
		return flatMap(function.andThen(value -> (TResult) new Success<>(value)));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <TResult extends Result<T, TFailure>, T> TResult ifValueIsPresent(
			final Class<T> innerValue, final TFailure error)
	{
		if (!(getValue() instanceof Optional))
		{
			return (TResult) new Failure<>(error);
		}
		final Optional<T> optional = (Optional<T>) getValue();
		if (!optional.isPresent())
		{
			return (TResult) new Failure<>(error);
		}
		return (TResult) new Success<>(optional.get());
	}
}
