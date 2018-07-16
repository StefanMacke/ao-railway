package net.aokv.railway.result;

import static net.aokv.railway.result.matchers.ResultMatcher.hasValue;
import static net.aokv.railway.result.matchers.ResultMatcher.isSuccess;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

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

	@Test
	public void notHaveAnError()
	{
		assertThrows(SuccessfulResultHasNoErrorException.class, () -> THE_RESULT.getError());
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

	@Test
	public void notRunConsumerOnItsValue()
	{
		assertThrows(EmptyResultHasNoValueException.class, () -> THE_RESULT.onSuccess(v -> System.out.println(v)));
	}

	@Test
	public void notMapItsValue()
	{
		assertThrows(EmptyResultHasNoValueException.class, () -> THE_RESULT.map(v -> v.getClass()));
	}

	@Test
	public void notFlatMapItsValue()
	{
		assertThrows(EmptyResultHasNoValueException.class, () -> THE_RESULT.flatMap(wert -> THE_RESULT));
	}

	@Test
	public void notReturnItsValue()
	{
		assertThrows(EmptyResultHasNoValueException.class, () -> THE_RESULT.getValue());
	}

	@Test
	public void notCheckItsValue()
	{
		assertThrows(EmptyResultHasNoValueException.class, () -> THE_RESULT.ensure(wert -> false, THE_ERROR));
	}
}
