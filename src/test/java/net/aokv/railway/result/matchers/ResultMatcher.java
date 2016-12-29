package net.aokv.railway.result.matchers;

import net.aokv.railway.message.Message;

public class ResultMatcher
{
	public static <TSuccess, TFailure> IsSuccess<TSuccess, TFailure> isSuccess()
	{
		return new IsSuccess<TSuccess, TFailure>();
	}

	public static <TSuccess, TFailure> IsFailure<TSuccess, TFailure> isFailure()
	{
		return new IsFailure<TSuccess, TFailure>();
	}

	public static <TSuccess> IsFailureWithMessageText<TSuccess> isFailureWithMessageText(
			final String messageText)
	{
		return new IsFailureWithMessageText<>(messageText);
	}

	public static <TSuccess, TFailure> IsFailureWithMessage<TSuccess, TFailure> isFailureWithMessage(
			final Message message)
	{
		return new IsFailureWithMessage<TSuccess, TFailure>(message);
	}

	public static <TSuccess, TFailure> HasValue<TSuccess, TFailure> hasValue(final TSuccess value)
	{
		return new HasValue<TSuccess, TFailure>(value);
	}
}
