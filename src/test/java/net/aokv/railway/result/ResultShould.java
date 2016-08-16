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

	private static final Result<String> THE_RESULT = Result.withValue(THE_VALUE);
	private static final Result<String> OTHER_RESULT = Result.withValue(OTHER_VALUE);
	private static final Result<String> FAILED_RESULT = Result.withError(THE_ERROR);
	private static final Result<Void> RESULT_WITHOUT_VALUE = Result.withoutValue();

	@Test(expected = IllegalArgumentException.class)
	public void notAcceptNullAsValue()
	{
		Result.withValue(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notAcceptNullAsError()
	{
		final Message e = null;
		Result.withError(e);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notAcceptNullAsErrorMessage()
	{
		final String e = null;
		Result.withError(e);
	}

	@Test
	public void beCreatedFromOptional()
	{
		final Result<String> result = Result.with(Optional.of(THE_VALUE), THE_ERROR);
		assertThat(result, isSuccess());
		assertThat(result, hasValue(THE_VALUE));
	}

	@Test
	public void beCreatedFromEmptyOptional()
	{
		final Result<String> ergebnis = Result.with(Optional.empty(), THE_ERROR);
		assertThat(ergebnis, isFailure());
		assertThat(ergebnis, isFailureWithMessage(THE_ERROR));
	}

	@Test
	public void beCreatedFromValue()
	{
		final Result<String> result = Result.with(THE_VALUE, THE_ERROR);
		assertThat(result, isSuccess());
		assertThat(result, hasValue(THE_VALUE));
	}

	@Test
	public void beCreatedFromNonExistingValue()
	{
		final Result<String> ergebnis = Result.with(null, THE_ERROR);
		assertThat(ergebnis, isFailure());
		assertThat(ergebnis, isFailureWithMessage(THE_ERROR));
	}

	@Test
	public void beCreatedFromNonExistingOptional()
	{
		final Optional<String> o = null;
		final Result<String> ergebnis = Result.with(o, THE_ERROR);
		assertThat(ergebnis, isFailure());
		assertThat(ergebnis, isFailureWithMessage(THE_ERROR));
	}

	@Test
	public void extractValueFromOptional()
	{
		assertThat(
				Result.withValue(Optional.of("Value"))
						.ifValueIsPresent(String.class, THE_ERROR),
				hasValue("Value"));
		assertThat(
				Result.withValue(Optional.empty())
						.ifValueIsPresent(String.class, THE_ERROR),
				isFailureWithMessage(THE_ERROR));
		assertThat(
				Result.withValue("NotAnOptional")
						.ifValueIsPresent(String.class, THE_ERROR),
				isFailureWithMessage(THE_ERROR));
	}

	@Test
	public void beCombinedWithSuccessfulResults()
	{
		assertThat(Result.combine(THE_RESULT, OTHER_RESULT), isSuccess());
		assertThat(Result.combine(THE_RESULT, RESULT_WITHOUT_VALUE), isSuccess());
		assertThat(Result.combine(OTHER_RESULT, THE_RESULT), isSuccess());
		assertThat(Result.combine(RESULT_WITHOUT_VALUE, THE_RESULT), isSuccess());
	}

	@Test
	public void beCombinedWithFailedResults()
	{
		assertThat(Result.combine(FAILED_RESULT, THE_RESULT), isFailure());
		assertThat(Result.combine(FAILED_RESULT, RESULT_WITHOUT_VALUE), isFailure());
		assertThat(Result.combine(FAILED_RESULT, FAILED_RESULT), isFailure());
		assertThat(Result.combine(THE_RESULT, FAILED_RESULT), isFailure());
		assertThat(Result.combine(RESULT_WITHOUT_VALUE, FAILED_RESULT), isFailure());
	}
}
