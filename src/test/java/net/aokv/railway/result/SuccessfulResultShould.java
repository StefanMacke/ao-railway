package net.aokv.railway.result;

import static net.aokv.railway.result.matchers.ResultMatcher.hasValue;
import static net.aokv.railway.result.matchers.ResultMatcher.isFailure;
import static net.aokv.railway.result.matchers.ResultMatcher.isFailureWithMessage;
import static net.aokv.railway.result.matchers.ResultMatcher.isSuccess;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.aokv.railway.message.Message;

public class SuccessfulResultShould
{
	private static final String THE_VALUE = "The value";
	private static final String OTHER_VALUE = "Other value";
	private static final Message THE_ERROR = Message.withError("The error");

	private static final Result<String, Message> THE_RESULT = Result.withValue(THE_VALUE);
	private static final Result<String, Message> OTHER_RESULT = Result.withValue(OTHER_VALUE);

	@Test
	public void beSuccessful()
	{
		assertThat(THE_RESULT, isSuccess());
	}

	@Test
	public void notBeFailed()
	{
		assertThat(THE_RESULT.isFailure(), is(false));
	}

	@Test
	public void returnItsValue()
	{
		assertThat(THE_RESULT, hasValue(THE_VALUE));
	}

	@Test(expected = SuccessfulResultHasNoErrorException.class)
	public void notHaveAnError()
	{
		THE_RESULT.getError();
	}

	@Test
	public void runSupplierOnSuccess()
	{
		assertThat(
				THE_RESULT.onSuccess(() ->
				{
					return Result.withValue("new value");
				}), hasValue("new value"));
		assertThat(
				THE_RESULT
						.map(value -> value.toUpperCase()),
				hasValue("THE VALUE"));
	}

	public void notRunRunnableOnFailure()
	{
		final Container c = new Container("");
		THE_RESULT.onFailure(() -> c.setString("Failure"));
		assertThat(c.getString(), is(""));
	}

	public void notRunErrorConsumerOnFailure()
	{
		final Container c = new Container("");
		THE_RESULT.onFailure(e -> c.setString(e.getText()));
		assertThat(c.getString(), is(""));
	}

	public void notRunErrorConsumerWithPredicateOnFailure()
	{
		final Container c = new Container("");
		THE_RESULT.onFailure(e -> e.getCode() == 1, e -> c.setString(e.getText()));
		assertThat(c.getString(), is(""));
	}

	@Test
	public void runConsumerOnSuccess()
	{
		final Container c = new Container("");
		assertThat(THE_RESULT
				.onSuccess((final String w) -> c.setString(w)), hasValue(THE_VALUE));
		assertThat(c.getString(), is(THE_VALUE));
	}

	@Test
	public void runConsumerOnBoth()
	{
		final Container c = new Container("");
		assertThat(THE_RESULT
				.onBoth(r -> c.setString(r.getValue().toString())), isSuccess());
		assertThat(c.getString(), is(THE_VALUE));
	}

	@Test
	public void checkItsValue()
	{
		Result<String, Message> result = THE_RESULT
				.ensure(value -> value.length() > THE_VALUE.length() - 1, THE_ERROR);
		assertThat(result, isSuccess());
		assertThat(result, hasValue(THE_VALUE));

		result = THE_RESULT
				.ensure(wert -> wert.length() > THE_VALUE.length() + 1, THE_ERROR);
		assertThat(result, isFailure());
		assertThat(result, isFailureWithMessage(THE_ERROR));
	}

	@Test
	public void checkItsValueRaisingAnException()
	{
		final Result<String, Message> result = THE_RESULT
				.ensure(value -> Long.parseLong(value) > 1, THE_ERROR);
		assertThat(result, isFailure());
		assertThat(result, isFailureWithMessage(THE_ERROR));
	}

	@Test
	public void mapItsValue()
	{
		assertThat(THE_RESULT.map(value -> value.length()), hasValue(THE_VALUE.length()));
		assertThat(THE_RESULT.map(value -> value + "2"), hasValue(THE_VALUE + "2"));
	}

	@Test
	public void flatMapAnotherResult()
	{
		assertThat(
				THE_RESULT.flatMap(w -> OTHER_RESULT),
				hasValue(OTHER_VALUE));
		assertThat(THE_RESULT.flatMap(value -> Result.withValue(value.length())),
				hasValue(THE_VALUE.length()));
	}
}
