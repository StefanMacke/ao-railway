package net.aokv.railway.result.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import net.aokv.railway.result.Message;
import net.aokv.railway.result.Result;

public class IsFailureWithMessage<T> extends TypeSafeDiagnosingMatcher<Result<T>>
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
	protected boolean matchesSafely(final Result<T> ergebnis, final Description description)
	{
		description.appendText("was ")
				.appendValue(ergebnis.toString());
		return ergebnis.getError().equals(message);
	}
}