package net.aokv.railway.result.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import net.aokv.railway.message.Message;
import net.aokv.railway.result.Result;

public class IsFailureWithMessageText<TSuccess>
		extends TypeSafeDiagnosingMatcher<Result<TSuccess, Message>>
{
	private final String messageText;

	public IsFailureWithMessageText(final String messageText)
	{
		this.messageText = messageText;
	}

	@Override
	public void describeTo(final Description description)
	{
		description.appendText("A failed Result with message text <")
				.appendValue(messageText)
				.appendText(">");
	}

	@Override
	protected boolean matchesSafely(final Result<TSuccess, Message> result, final Description description)
	{
		description.appendText("was ")
				.appendValue(result.toString());
		return result.getError().getText().equals(messageText);
	}
}