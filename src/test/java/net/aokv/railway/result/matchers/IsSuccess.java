package net.aokv.railway.result.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import net.aokv.railway.result.AbstractResult;

public class IsSuccess<TSuccess, TFailure> extends TypeSafeDiagnosingMatcher<AbstractResult<TSuccess, TFailure>>
{
	@Override
	public void describeTo(final Description description)
	{
		description.appendText("A successful Result");
	}

	@Override
	protected boolean matchesSafely(final AbstractResult<TSuccess, TFailure> result, final Description description)
	{
		description.appendText("was ")
				.appendValue(result.toString());
		return result.isSuccess();
	}
}