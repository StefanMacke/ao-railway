package net.aokv.railway.result;

import static net.aokv.railway.result.matchers.ResultMatcher.hasValue;
import static net.aokv.railway.result.matchers.ResultMatcher.isSuccess;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.aokv.railway.message.Message;

public class SuccessfulResultWithoutValueShould
{
	private static final Result<Void, Message> THE_RESULT = Result.withoutValue();
	private static final Message THE_ERROR = Message.withError("The error");

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
					return Result.withValue("Success");
				}), hasValue("Success"));
	}

	@Test
	public void notRunRunnableOnFailure()
	{
		final Container c = new Container("");
		assertThat(THE_RESULT.onFailure(() -> c.setString("Failure")), isSuccess());
		assertThat(c.getString(), is(""));
	}

	@Test
	public void notRunErrorConsumerOnFailure()
	{
		final Container c = new Container("");
		assertThat(THE_RESULT.onFailure(e -> c.setString(e.getText())), isSuccess());
		assertThat(c.getString(), is(""));
	}

	public void notRunErrorConsumerWithPredicateOnFailure()
	{
		final Container c = new Container("");
		THE_RESULT.onFailure(e -> e.getCode() == 1, e -> c.setString(e.getText()));
		assertThat(c.getString(), is(""));
	}

	@Test
	public void runConsumerOnBoth()
	{
		final Container c = new Container("");
		assertThat(THE_RESULT.onBoth(result -> c.setString("Success")), isSuccess());
		assertThat(c.getString(), is("Success"));
	}

	@Test(expected = EmptyResultHasNoValueException.class)
	public void notRunConsumerOnItsValue()
	{
		THE_RESULT.onSuccess(v -> System.out.println(v));
	}

	@Test(expected = EmptyResultHasNoValueException.class)
	public void notMapItsValue()
	{
		THE_RESULT.map(v -> v.getClass());
	}

	@Test(expected = EmptyResultHasNoValueException.class)
	public void notFlatMapItsValue()
	{
		THE_RESULT.flatMap(wert -> THE_RESULT);
	}

	@Test(expected = EmptyResultHasNoValueException.class)
	public void notReturnItsValue()
	{
		THE_RESULT.getValue();
	}

	@Test(expected = EmptyResultHasNoValueException.class)
	public void notCheckItsValue()
	{
		THE_RESULT.ensure(wert -> false, THE_ERROR);
	}
}
