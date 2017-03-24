package net.aokv.railway.result.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import net.aokv.railway.message.Message;
import net.aokv.railway.result.Result;

public class IsFailureWithMessage<TSuccess, TFailure>
		extends TypeSafeDiagnosingMatcher<Result<TSuccess, TFailure>>
{
	private final Message message;

	public IsFailureWithMessage(final Message message)
	{
		this.message = message;
	}

	@Override
	public void describeTo(final Description description)
	{
		description.appendText("A failed Result with Message <")
				.appendValue(message)
				.appendText(">");
	}

	@Override
	protected boolean matchesSafely(final Result<TSuccess, TFailure> result, final Description description)
	{
		description.appendText("was ")
				.appendValue(result.toString());
		return result.getError()
				.equals(message);
	}
}