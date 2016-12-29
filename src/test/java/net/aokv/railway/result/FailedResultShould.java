package net.aokv.railway.result;

import static net.aokv.railway.result.matchers.ResultMatcher.isFailure;
import static net.aokv.railway.result.matchers.ResultMatcher.isFailureWithMessage;
import static net.aokv.railway.result.matchers.ResultMatcher.isFailureWithMessageText;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import org.junit.Test;

import net.aokv.railway.message.Message;

public class FailedResultShould
{
	private static final Message THE_ERROR = Message.withError("The error");
	private static final Result<String, Message> THE_RESULT = Result.withError(THE_ERROR);

	@Test
	public void beFailed()
	{
		assertThat(THE_RESULT, isFailure());
	}

	@Test
	public void notBeSuccessful()
	{
		assertThat(THE_RESULT.isSuccess(), is(false));
	}

	@Test
	public void returnItsError()
	{
		assertThat(THE_RESULT, isFailureWithMessage(THE_ERROR));
	}

	@Test(expected = FailedResultHasNoValueException.class)
	public void notHaveValue()
	{
		THE_RESULT.getValue();
	}

	@Test
	public void notRunSupplierOnSuccess()
	{
		assertThat(THE_RESULT.onSuccess(() ->
		{
			return Result.withValue("Success");
		}), isFailureWithMessageText(THE_ERROR.getText()));

		final Function<String, String> function = value -> value.toUpperCase();
		assertThat(
				THE_RESULT.map(function),
				isFailureWithMessageText(THE_ERROR.getText()));
	}

	@Test
	public void notRunConsumerOnSuccess()
	{
		final Container c = new Container("");
		assertThat(THE_RESULT
				.onSuccess(v -> c.setString("Success")), isFailureWithMessage(THE_ERROR));
		assertThat(c.getString(), is(""));
	}

	@Test
	public void runRunnableOnFailure()
	{
		final Container c = new Container("");
		assertThat(THE_RESULT.onFailure(() -> c.setString("Failure")), isFailure());
		assertThat(c.getString(), is("Failure"));
	}

	@Test
	public void runErrorConsumerOnFailure()
	{
		final Container c = new Container("");
		assertThat(THE_RESULT.onFailure(e -> c.setString(e.getText())), isFailure());
		assertThat(c.getString(), is(THE_ERROR.getText()));
	}

	@Test
	public void runErrorConsumerOnFailureIfPredicateMatches()
	{
		final Container c = new Container("");
		assertThat(THE_RESULT.onFailure(e -> true, e -> c.setString(e.getText())), isFailure());
		assertThat(c.getString(), is(THE_ERROR.getText()));
	}

	@Test
	public void notRunErrorConsumerOnFailureIfPredicateDoesNotMatch()
	{
		final Container c = new Container("");
		assertThat(THE_RESULT.onFailure(e -> false, e -> c.setString(e.getText())), isFailure());
		assertThat(c.getString(), is(""));
	}

	@Test
	public void runConsumerOnBoth()
	{
		final Container c = new Container("");
		assertThat(THE_RESULT.onBoth(result -> c.setString("Both")), isFailure());
		assertThat(c.getString(), is("Both"));
	}

	@Test
	public void notCheckItsValue()
	{
		final Result<String, Message> result = THE_RESULT
				.ensure(value -> value.length() > 1, Message.withError("Error"));
		assertThat(result, isFailure());
		assertThat(result, isFailureWithMessage(THE_ERROR));
	}

	@Test
	public void notMapItsValue()
	{
		assertThat(THE_RESULT.map(value -> value + "2"),
				isFailureWithMessage(THE_ERROR));
	}

	@Test
	public void notFlatMapItsValue()
	{
		assertThat(THE_RESULT.flatMap(value -> Result.withValue("Value")),
				isFailureWithMessage(THE_ERROR));
	}

	@Test
	public void notExtractOptional()
	{
		assertThat(
				THE_RESULT.ifValueIsPresent(String.class, THE_ERROR),
				isFailureWithMessageText(THE_ERROR.getText()));
	}
}
