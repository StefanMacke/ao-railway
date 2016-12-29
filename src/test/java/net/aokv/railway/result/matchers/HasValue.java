package net.aokv.railway.result.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import net.aokv.railway.result.Result;

public class HasValue<TSuccess, TFailure> extends TypeSafeDiagnosingMatcher<Result<TSuccess, TFailure>>
{
	private final TSuccess value;

	public HasValue(final TSuccess value)
	{
		this.value = value;
	}

	@Override
	public void describeTo(final Description description)
	{
		description.appendText("A successful Result with value <")
				.appendValue(value)
				.appendText(">");
	}

	@Override
	protected boolean matchesSafely(final Result<TSuccess, TFailure> result, final Description description)
	{
		description.appendText("was ")
				.appendValue(result.toString());
		return result.getValue().equals(value);
	}
}