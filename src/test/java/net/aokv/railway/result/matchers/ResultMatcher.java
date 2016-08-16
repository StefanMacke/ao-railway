package net.aokv.railway.result.matchers;

import net.aokv.railway.result.Message;

public class ResultMatcher
{
	public static <T> IsSuccess<T> isSuccess()
	{
		return new IsSuccess<T>();
	}

	public static <T> IsFailure<T> isFailure()
	{
		return new IsFailure<T>();
	}

	public static <T> IsFailureWithMessageText<T> isFailureWithMessageText(final String messageText)
	{
		return new IsFailureWithMessageText<T>(messageText);
	}

	public static <T> IsFailureWithMessage<T> isFailureWithMessage(final Message message)
	{
		return new IsFailureWithMessage<T>(message);
	}

	public static <T> HasValue<T> hasValue(final T value)
	{
		return new HasValue<T>(value);
	}
}
