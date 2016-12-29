package net.aokv.railway.message;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class MessageShould
{
	private Message sut;

	@Test
	public void beCreatedWithAnErrorMessageFromAString()
	{
		sut = Message.withError("This is the error");
		assertThat(sut.getCode(), is(not(0)));
		assertThat(sut.getLevel(), is(MessageLevel.ERROR));
		assertThat(sut.getIndex(), is(0));
		assertThat(sut.getSource(), is("beCreatedWithAnErrorMessageFromAString"));
		assertThat(sut.getText(), is("This is the error"));
	}

	@Test
	public void beComparableToASimilarMessage()
	{
		assertThat(Message.withError("This is the error"), is(Message.withError("This is the error")));
		assertThat(
				Message.create()
						.withCode(1)
						.withDetails("Details")
						.withLevel(MessageLevel.INFO)
						.withIndex(2)
						.withSource("Source")
						.withText("Text")
						.build(),
				is(Message.create()
						.withCode(1)
						.withDetails("Details")
						.withLevel(MessageLevel.INFO)
						.withIndex(2)
						.withSource("Source")
						.withText("Text")
						.build()));
	}

	@Test
	public void beCreatedFromAMessageBuilder()
	{
		sut = Message.create()
				.withCode(1)
				.withLevel(MessageLevel.ERROR)
				.withIndex(2)
				.withSource("The source")
				.withText("The text")
				.build();
		assertThat(sut.getCode(), is(1));
		assertThat(sut.getLevel(), is(MessageLevel.ERROR));
		assertThat(sut.getIndex(), is(2));
		assertThat(sut.getSource(), is("The source"));
		assertThat(sut.getText(), is("The text"));
	}

	@Test
	public void useDefaultValuesIfNoneAreGiven()
	{
		sut = Message.create().build();
		assertThat(sut.getCode(), is(1));
		assertThat(sut.getLevel(), is(MessageLevel.ERROR));
		assertThat(sut.getIndex(), is(0));
		assertThat(sut.getSource(), is("useDefaultValuesIfNoneAreGiven"));
		assertThat(sut.getText(), is("No text"));
		assertThat(sut.getDetails(), is("No details"));
	}

	@Test
	public void beFormattedAsAString()
	{
		final Exception exception = new Exception("Inner exception");
		sut = Message.create()
				.withCode(1)
				.withLevel(MessageLevel.ERROR)
				.withIndex(0)
				.withSource("Test")
				.withText("The error")
				.withDetails(exception)
				.build();
		assertThat(sut.toString(), containsString("ERROR (1, Test, 0): \"The error\""));
		assertThat(sut.toString(), containsString("Inner exception"));
		assertThat(sut.toString(), containsString("beFormattedAsAString"));
	}

	@Test
	public void beIdentifiableWithACode()
	{
		sut = Message.create().withCode(1234).build();
		assertThat(sut.hasCode(1234), is(true));
		assertThat(sut.hasCode(1235), is(false));
	}

}
