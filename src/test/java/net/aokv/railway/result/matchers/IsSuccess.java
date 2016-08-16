package net.aokv.railway.result.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import net.aokv.railway.result.Result;

public class IsSuccess<T> extends TypeSafeDiagnosingMatcher<Result<T>>
{
	@Override
	public void describeTo(final Description description)
	{
		description.appendText("A successful Result");
	}

	@Override
	protected boolean matchesSafely(final Result<T> result, final Description description)
	{
		description.appendText("was ")
				.appendValue(result.toString());
		return result.isSuccess();
	}
}