package net.aokv.railway.result;

import java.util.Optional;

/**
 * Result of a computation or any other action that contains value of type TSuccess or a Message in
 * case of a failure.
 *
 * @param <TSuccess> The type of the contained value.
 */
@SuppressWarnings("unchecked")
public final class Result<TSuccess> extends AbstractResult<TSuccess, Message>
{
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
		return new Result<Void>(null, null);
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

	protected Result(final TSuccess value, final Message error)
	{
		super(value, error);
	}

	@Override
	protected <TResult extends AbstractResult<Void, Message>> TResult emptyResult()
	{
		return (TResult) Result.withoutValue();
	}

	@Override
	protected <TResult extends AbstractResult<T, Message>, T> TResult successfulResult(final T value)
	{
		return (TResult) Result.withValue(value);
	}

	@Override
	protected <TResult extends AbstractResult<T, Message>, T> TResult failedResult(final Message error)
	{
		return (TResult) Result.withError(error);
	}
}
