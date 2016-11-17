package net.aokv.railway.result;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Result of a computation or any other action. Can be successful and contain a value or failed and
 * contain an error (Message).
 *
 * @param <TSuccess> The type of the contained value.
 */
public final class Result<TSuccess>
{
	private final Optional<TSuccess> value;
	private final Optional<Message> error;

	/**
	 * Creates a new Result with the given error message.
	 *
	 * @param error The error message.
	 * @return Failed Result.
	 */
	public static <T> Result<T> withError(final Message error)
	{
		assertParameterNotNull(error, "Error");
		return new Result<T>(null, error);
	}

	/**
	 * Creates a new Result with an error message from the given text.
	 *
	 * @param error The error message.
	 * @return Failed Result.
	 */
	public static <T> Result<T> withError(final String error)
	{
		assertParameterNotNull(error, "Error");
		return withError(Message.withError(error));
	}

	/**
	 * Creates a new Result without a value.
	 *
	 * @return Successful Result.
	 */
	public static Result<Void> withoutValue()
	{
		return new Result<Void>();
	}

	/**
	 * Creates a successful Result with the given value. The value may not be null.
	 *
	 * @param value The value.
	 * @return Successful Result with the given value.
	 * @throws IllegalArgumentException If value is null.
	 */
	public static <T> Result<T> withValue(final T value)
	{
		assertParameterNotNull(value, "Value");
		return new Result<T>(value, null);
	}

	/**
	 * Creates a Result with an optional value. If the value is null, a failed Result will be
	 * created.
	 *
	 * @param value The value.
	 * @param error The error to set, if value is null.
	 * @return Successful Result with value or failed Result.
	 */
	public static <T> Result<T> with(final T value, final Message error)
	{
		return Result.with(Optional.ofNullable(value), error);
	}

	/**
	 * Creates a Result with an optional value. If the value is null or an empty Optional, a failed
	 * Result is created.
	 *
	 * @param valueOrNothing The optional value.
	 * @param error The error to set, if value is null or an empty Optional.
	 * @return Successful Result with value or failed Result.
	 */
	public static <T> Result<T> with(final Optional<T> valueOrNothing, final Message error)
	{
		if (valueOrNothing == null || !valueOrNothing.isPresent())
		{
			return Result.withError(error);
		}
		return Result.withValue(valueOrNothing.get());
	}

	private static void assertParameterNotNull(final Object parameter, final String name)
	{
		if (parameter == null)
		{
			throw new IllegalArgumentException(
					String.format("%s may not be null.", name));
		}
	}

	private Result()
	{
		value = Optional.empty();
		error = Optional.empty();
	}

	private Result(final TSuccess value, final Message error)
	{
		this.value = Optional.ofNullable(value);
		this.error = Optional.ofNullable(error);
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
	public Message getError()
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
	 * Result (Failure: ERROR (1, withError, 0): "the error" (Details: "No details"))
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
			result.append("Failure: ");
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
	@SafeVarargs
	public static Result<?> combine(final Result<?>... results)
	{
		return Arrays.stream(results)
				.filter(result -> result.isFailure())
				.findFirst()
				.orElse(Result.withoutValue());
	}

	private <T> Optional<Result<T>> resultIfError()
	{
		return isFailure()
				? Optional.of(Result.withError(getError()))
				: Optional.empty();
	}

	/**
	 * Runs the given function, if the Result is successful.
	 *
	 * @param function The function to run.
	 * @return Result of the function.
	 */
	public <T> Result<T> onSuccess(final Supplier<Result<T>> function)
	{
		final Optional<Result<T>> result = resultIfError();
		return result.orElseGet(function);
	}

	/**
	 * Runs the given function and wraps its return value in a Result, if the Result is successful.
	 *
	 * @param function The function to run.
	 * @param clazz The return value of the function.
	 * @return Return value of the function wrapped in a Result.
	 */
	public <T> Result<T> onSuccess(final Supplier<T> function, final Class<T> clazz)
	{
		return onSuccess(() -> Result.withValue(function.get()));
	}

	/**
	 * Runs the given function, if the Result is successful.
	 *
	 * @param function The function to run.
	 * @return The current Result.
	 */
	public Result<TSuccess> onSuccess(final Consumer<TSuccess> function)
	{
		if (!isFailure())
		{
			function.accept(getValue());
		}
		return this;
	}

	/**
	 * Extracts the inner value from an Optional value of the Result.
	 *
	 * @param innerValue Type of the Optional's value.
	 * @param error Error if inner value cannot be extracted.
	 * @return Result of the inner value of the Optional or a failed Result.
	 */
	public <T> Result<T> ifValueIsPresent(final Class<T> innerValue, final Message error)
	{
		if (isFailure())
		{
			return Result.withError(getError());
		}
		if (!(getValue() instanceof Optional))
		{
			return Result.withError(error);
		}
		@SuppressWarnings("unchecked")
		final Optional<T> optional = (Optional<T>) getValue();
		if (!optional.isPresent())
		{
			return Result.withError(error);
		}
		return Result.withValue(optional.get());
	}

	/**
	 * Runs the given function, if the Result is failed.
	 *
	 * @param function The function to run.
	 * @return The current Result.
	 */
	public Result<?> onFailure(final Runnable function)
	{
		if (isFailure())
		{
			function.run();
		}
		return this;
	}

	/**
	 * Runs the given function, regardless of the Result's outcome.
	 *
	 * @param function The function to run.
	 * @return The current Result.
	 */
	public Result<TSuccess> onBoth(final Consumer<Result<TSuccess>> function)
	{
		function.accept(this);
		return this;
	}

	/**
	 * Runs the given predicate, if the Result is successful.
	 *
	 * @param predicate The predicate to run.
	 * @param error Error, if the predicate returns false.
	 * @return Result with checked value or failed Result.
	 * @throws EmptyResultHasNoValueException If the Result does not have a value.
	 */
	public Result<TSuccess> ensure(final Predicate<TSuccess> predicate, final Message error)
	{
		if (isFailure())
		{
			return this;
		}
		try
		{
			if (!predicate.test(getValue()))
			{
				return Result.withError(error);
			}
		}
		catch (final EmptyResultHasNoValueException exception)
		{
			throw exception;
		}
		catch (final Exception exception)
		{
			return Result.withError(error);
		}
		return this;
	}

	/**
	 * Extracts a <code>Result&lt;T&gt;</code> from a <code>Result&lt;Result&lt;T&gt;&gt;</code>, if
	 * the Result is successful.
	 *
	 * @param function A function that returns a <code>Result&lt;Result&lt;T&gt;&gt;</code>.
	 * @return The extracted <code>Result&lt;T&gt;</code>.
	 */
	public <T> Result<T> flatMap(final Function<TSuccess, Result<T>> function)
	{
		final Optional<Result<T>> result = resultIfError();
		return result.orElseGet(() -> function.apply(getValue()));
	}

	/**
	 * Maps the Result to a Result with another value, if the Result is successful.
	 *
	 * @param function A function that returns the new value.
	 * @return The Result of the function's value or a failed Result.
	 */
	public <T> Result<T> map(final Function<TSuccess, T> function)
	{
		return flatMap(function.andThen(value -> Result.withValue(value)));
	}
}
