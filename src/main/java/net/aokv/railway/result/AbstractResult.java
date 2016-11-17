package net.aokv.railway.result;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Result of a computation or any other action. Can be successful and contain a value or failed and
 * contain an error (TFailure).
 *
 * @param <TSuccess> The type of the contained value.
 * @param <TFailure> The type of the error object in case of a failure.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractResult<TSuccess, TFailure>
{
	protected abstract <TResult extends AbstractResult<Void, TFailure>> TResult emptyResult();

	protected abstract <TResult extends AbstractResult<T, TFailure>, T> TResult successfulResult(T value);

	protected abstract <TResult extends AbstractResult<T, TFailure>, T> TResult failedResult(TFailure error);

	private final Optional<TSuccess> value;
	private final Optional<TFailure> error;

	protected AbstractResult(final TSuccess value, final TFailure error)
	{
		this.value = Optional.ofNullable(value);
		this.error = Optional.ofNullable(error);
	}

	protected static void assertParameterNotNull(final Object parameter, final String name)
	{
		if (parameter == null)
		{
			throw new IllegalArgumentException(
					String.format("%s may not be null.", name));
		}
	}

	/**
	 * Checks whether the Result is failed.
	 *
	 * @return Whether the Result is failed.
	 */
	public boolean isFailure()
	{
		return error.isPresent();
	}

	/**
	 * Checks whether the Result is successful.
	 *
	 * @return Whether the Result is successful.
	 */
	public boolean isSuccess()
	{
		return !isFailure();
	}

	/**
	 * Returns the Result's value.
	 *
	 * @return The value.
	 * @throws FailedResultHasNoValueException If Result is failed.
	 * @throws EmptyResultHasNoValueException If Result does not have a value.
	 */
	public TSuccess getValue()
	{
		if (isFailure())
		{
			throw new FailedResultHasNoValueException(getError());
		}
		if (!value.isPresent())
		{
			throw new EmptyResultHasNoValueException();
		}
		return value.get();
	}

	/**
	 * Returns the Result's error.
	 *
	 * @return The error.
	 * @throws SuccessfulResultHasNoErrorException If Result is successful.
	 */
	public TFailure getError()
	{
		if (isSuccess())
		{
			throw new SuccessfulResultHasNoErrorException();
		}
		return error.get();
	}

	/**
	 * Returns the Result as a string.
	 *
	 * <pre>
	 * Result (Success with value &lt;The value&gt;)
	 * </pre>
	 *
	 * <pre>
	 * Result (Error: The error)
	 * </pre>
	 *
	 * @return The Result as a string.
	 */
	@Override
	public String toString()
	{
		final StringBuilder result = new StringBuilder("Result (");
		if (isSuccess())
		{
			result.append("Success");
			if (value.isPresent())
			{
				result.append(" with value <");
				result.append(getValue());
				result.append('>');
			}
		}
		else
		{
			result.append("Error: ");
			result.append(getError());
		}
		result.append(')');
		return result.toString();
	}

	/**
	 * Combines multiple Results. Returns the first failed Result or a successful Result without a
	 * value, if all Results are successful.
	 *
	 * @param results The Results to combine.
	 * @return Result of the combination.
	 */
	public <TResult extends AbstractResult<?, TFailure>> TResult combine(final TResult otherResult)
	{
		if (isFailure())
		{
			return (TResult) this;
		}
		if (otherResult.isFailure())
		{
			return otherResult;
		}
		return (TResult) emptyResult();
	}

	/**
	 * Runs the given function, if the Result is successful.
	 *
	 * @param function The function to run.
	 * @return Result of the function.
	 */
	public <TResult extends AbstractResult<T, TFailure>, T> TResult onSuccess(
			final Supplier<TResult> function)
	{
		if (isFailure())
		{
			return failedResult(getError());
		}
		return function.get();
	}

	/**
	 * Runs the given function and wraps its return value in a Result, if the Result is successful.
	 *
	 * @param function The function to run.
	 * @param clazz The return value of the function.
	 * @return Return value of the function wrapped in a Result.
	 */
	public <TResult extends AbstractResult<T, TFailure>, T> TResult onSuccess(
			final Supplier<T> function, final Class<T> clazz)
	{
		return onSuccess(() -> successfulResult(function.get()));
	}

	/**
	 * Runs the given function, if the Result is successful.
	 *
	 * @param function The function to run.
	 * @return The current Result.
	 */
	public <TResult extends AbstractResult<TSuccess, TFailure>> TResult onSuccess(final Consumer<TSuccess> function)
	{
		if (!isFailure())
		{
			function.accept(getValue());
		}
		return (TResult) this;
	}

	/**
	 * Runs the given function, if the Result is failed.
	 *
	 * @param function The function to run.
	 * @return The current Result.
	 */
	public <TResult extends AbstractResult<?, TFailure>> TResult onFailure(final Runnable function)
	{
		if (isFailure())
		{
			function.run();
		}
		return (TResult) this;
	}

	/**
	 * Runs the given function, regardless of the Result's outcome.
	 *
	 * @param function The function to run.
	 * @return The current Result.
	 */
	public <TResult extends AbstractResult<?, ?>> TResult onBoth(
			final Consumer<? super AbstractResult<TSuccess, TFailure>> function)
	{
		function.accept(this);
		return (TResult) this;
	}

	/**
	 * Runs the given predicate, if the Result is successful.
	 *
	 * @param predicate The predicate to run.
	 * @param error Error, if the predicate returns false.
	 * @return Result with checked value or failed Result.
	 * @throws EmptyResultHasNoValueException If the Result does not have a value.
	 */
	public <TResult extends AbstractResult<TSuccess, TFailure>> TResult ensure(final Predicate<TSuccess> predicate,
			final TFailure error)
	{
		if (isFailure())
		{
			return (TResult) this;
		}
		try
		{
			if (!predicate.test(getValue()))
			{
				return failedResult(error);
			}
		}
		catch (final EmptyResultHasNoValueException exception)
		{
			throw exception;
		}
		catch (final Exception exception)
		{
			return failedResult(error);
		}
		return (TResult) this;
	}

	/**
	 * Extracts a <code>Result&lt;T&gt;</code> from a <code>Result&lt;Result&lt;T&gt;&gt;</code>, if
	 * the Result is successful.
	 *
	 * @param function A function that returns a <code>Result&lt;Result&lt;T&gt;&gt;</code>.
	 * @return The extracted <code>Result&lt;T&gt;</code>.
	 */
	public <TResult extends AbstractResult<T, TFailure>, T> TResult flatMap(final Function<TSuccess, TResult> function)
	{
		if (isFailure())
		{
			return failedResult(getError());
		}
		return function.apply(getValue());
	}

	/**
	 * Maps the Result to a Result with another value, if the Result is successful.
	 *
	 * @param function A function that returns the new value.
	 * @return The Result of the function's value or a failed Result.
	 */
	public <TResult extends AbstractResult<T, TFailure>, T> TResult map(final Function<TSuccess, T> function)
	{
		return flatMap(function.andThen(value -> successfulResult(value)));
	}

	/**
	 * Extracts the inner value from an Optional value of the Result.
	 *
	 * @param innerValue Type of the Optional's value.
	 * @param error Error if inner value cannot be extracted.
	 * @return Result of the inner value of the Optional or a failed Result.
	 */
	public <TResult extends AbstractResult<T, TFailure>, T> TResult ifValueIsPresent(
			final Class<T> innerValue, final TFailure error)
	{
		if (isFailure())
		{
			return failedResult(getError());
		}
		if (!(getValue() instanceof Optional))
		{
			return failedResult(error);
		}
		final Optional<T> optional = (Optional<T>) getValue();
		if (!optional.isPresent())
		{
			return failedResult(error);
		}
		return successfulResult(optional.get());
	}
}
