package net.aokv.railway.result;

import static net.aokv.railway.result.matchers.ResultMatcher.hasValue;
import static net.aokv.railway.result.matchers.ResultMatcher.isFailure;
import static net.aokv.railway.result.matchers.ResultMatcher.isFailureWithMessage;
import static net.aokv.railway.result.matchers.ResultMatcher.isSuccess;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.junit.Test;

public class ResultShould
{
	private static final String THE_VALUE = "The value";
	private static final String OTHER_VALUE = "Other value";
	private static final Message THE_ERROR = Message.withError("The error");

	private static final AbstractResult<String, Message> THE_RESULT = AbstractResult.withValue(THE_VALUE);
	private static final AbstractResult<String, Message> OTHER_RESULT = AbstractResult.withValue(OTHER_VALUE);
	private static final AbstractResult<String, Message> FAILED_RESULT = AbstractResult.withError(THE_ERROR);
	private static final AbstractResult<Void, Message> RESULT_WITHOUT_VALUE = AbstractResult.withoutValue();

	@Test(expected = IllegalArgumentException.class)
	public void notAcceptNullAsValue()
	{
		AbstractResult.withValue(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notAcceptNullAsError()
	{
		final Message e = null;
		AbstractResult.withError(e);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notAcceptNullAsErrorMessage()
	{
		final String e = null;
		AbstractResult.withError(e);
	}

	@Test
	public void beCreatedFromOptional()
	{
		final AbstractResult<String, Message> result = AbstractResult.with(Optional.of(THE_VALUE), THE_ERROR);
		assertThat(result, isSuccess());
		assertThat(result, hasValue(THE_VALUE));
	}

	@Test
	public void beCreatedFromEmptyOptional()
	{
		final AbstractResult<String, Message> ergebnis = AbstractResult.with(Optional.empty(), THE_ERROR);
		assertThat(ergebnis, isFailure());
		assertThat(ergebnis, isFailureWithMessage(THE_ERROR));
	}

	@Test
	public void beCreatedFromValue()
	{
		final AbstractResult<String, Message> result = AbstractResult.with(THE_VALUE, THE_ERROR);
		assertThat(result, isSuccess());
		assertThat(result, hasValue(THE_VALUE));
	}

	@Test
	public void beCreatedFromNonExistingValue()
	{
		final AbstractResult<String, Message> ergebnis = AbstractResult.with(null, THE_ERROR);
		assertThat(ergebnis, isFailure());
		assertThat(ergebnis, isFailureWithMessage(THE_ERROR));
	}

	@Test
	public void beCreatedFromNonExistingOptional()
	{
		final Optional<String> o = null;
		final AbstractResult<String, Message> ergebnis = AbstractResult.with(o, THE_ERROR);
		assertThat(ergebnis, isFailure());
		assertThat(ergebnis, isFailureWithMessage(THE_ERROR));
	}

	@Test
	public void extractValueFromOptional()
	{
		assertThat(
				AbstractResult.withValue(Optional.of("Value"))
						.ifValueIsPresent(String.class, THE_ERROR),
				hasValue("Value"));
		assertThat(
				AbstractResult.withValue(Optional.empty())
						.ifValueIsPresent(String.class, THE_ERROR),
				isFailureWithMessage(THE_ERROR));
		assertThat(
				AbstractResult.withValue("NotAnOptional")
						.ifValueIsPresent(String.class, THE_ERROR),
				isFailureWithMessage(THE_ERROR));
	}

	@Test
	public void beCombinedWithSuccessfulResults()
	{
		assertThat(THE_RESULT.combine(OTHER_RESULT), isSuccess());
		assertThat(THE_RESULT.combine(RESULT_WITHOUT_VALUE), isSuccess());
		assertThat(OTHER_RESULT.combine(THE_RESULT), isSuccess());
		assertThat(RESULT_WITHOUT_VALUE.combine(THE_RESULT), isSuccess());
	}

	@Test
	public void beCombinedWithFailedResults()
	{
		assertThat(FAILED_RESULT.combine(THE_RESULT), isFailure());
		assertThat(FAILED_RESULT.combine(RESULT_WITHOUT_VALUE), isFailure());
		assertThat(FAILED_RESULT.combine(FAILED_RESULT), isFailure());
		assertThat(THE_RESULT.combine(FAILED_RESULT), isFailure());
		assertThat(RESULT_WITHOUT_VALUE.combine(FAILED_RESULT), isFailure());
	}
}
