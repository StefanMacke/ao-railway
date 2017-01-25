package net.aokv.railway.message;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import com.google.common.base.Objects;

/**
 * A Message. Immutable. Is created by a <code>MessageBuilder</code>.
 */
public final class Message implements Serializable
{
	private static final long serialVersionUID = 8128659250371079506L;
	private static final int POSITION_OF_CALLER_IN_CALL_STACK = 7;

	private MessageLevel level;
	private int code;
	private String source;
	private int index;
	private String text;
	private String details;

	/**
	 * A builder (pattern) for a Message.
	 */
	public static class MessageBuilder
	{
		private final Message message;

		private MessageBuilder()
		{
			message = new Message();
		}

		/**
		 * Creates the final Message.
		 *
		 * @return The final Message.
		 */
		public Message build()
		{
			return message;
		}

		/**
		 * Sets the new Message's code.
		 *
		 * @param code The new Message's code.
		 * @return The builder.
		 */
		public MessageBuilder withCode(final int code)
		{
			message.code = code;
			return this;
		}

		/**
		 * Sets the new Message's MessageLevel.
		 *
		 * @param level The new Message's MessageLevel.
		 * @return The builder.
		 */
		public MessageBuilder withLevel(final MessageLevel level)
		{
			message.level = level;
			return this;
		}

		/**
		 * Sets the new Message's index.
		 *
		 * @param index The new Message's index.
		 * @return The builder.
		 */
		public MessageBuilder withIndex(final int index)
		{
			message.index = index;
			return this;
		}

		/**
		 * Sets the new Message's text.
		 *
		 * @param text The new Message's text.
		 * @return The builder.
		 */
		public MessageBuilder withText(final String text)
		{
			message.text = text;
			return this;
		}

		/**
		 * Sets the new Message's source.
		 *
		 * @param source The new Message's source.
		 * @return The builder.
		 */
		public MessageBuilder withSource(final String source)
		{
			message.source = source;
			return this;
		}

		/**
		 * Sets the new Message's details.
		 *
		 * @param details The new Message's details.
		 * @return The builder.
		 */
		public MessageBuilder withDetails(final String details)
		{
			message.details = details;
			return this;
		}

		/**
		 * Sets the new Message's details from an error.
		 *
		 * @param throwable The error for the Message's details.
		 * @return The builder.
		 */
		public MessageBuilder withDetails(final Throwable throwable)
		{
			final StringWriter stringWriter = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(stringWriter);
			throwable.printStackTrace(printWriter);
			message.details = stringWriter.toString();
			return this;
		}
	}

	/**
	 * Creates a new MessageBuilder for a message.
	 *
	 * @return The new MessageBuilder for a message.
	 */
	public static MessageBuilder create()
	{
		return new MessageBuilder();
	}

	/**
	 * Creates a new MessageBuilder for an error message.
	 *
	 * @return The new MessageBuilder for an error message.
	 */
	public static MessageBuilder createError()
	{
		return new MessageBuilder()
				.withLevel(MessageLevel.ERROR);
	}

	/**
	 * Creates a new MessageBuilder for an error message.
	 *
	 * @param text The error message's text.
	 * @return The new MessageBuilder for an error message.
	 */
	public static Message withError(final String text)
	{
		return new MessageBuilder()
				.withLevel(MessageLevel.ERROR)
				.withText(text)
				.build();
	}

	private Message()
	{
		level = MessageLevel.ERROR;
		code = 1;
		source = getSourceFromCallStack();
		index = 0;
		text = "No text";
		details = "No details";
	}

	private String getSourceFromCallStack()
	{
		final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		if (stack.length >= POSITION_OF_CALLER_IN_CALL_STACK + 1)
		{
			return stack[POSITION_OF_CALLER_IN_CALL_STACK].getMethodName();
		}
		return "No source";
	}

	/**
	 * Returns the Message's code.
	 *
	 * @return The Message's code.
	 */
	public int getCode()
	{
		return code;
	}

	/**
	 * Returns the Message's level.
	 *
	 * @return The Message's level.
	 */
	public MessageLevel getLevel()
	{
		return level;
	}

	/**
	 * Returns the Message's index.
	 *
	 * @return The Message's index.
	 */
	public int getIndex()
	{
		return index;
	}

	/**
	 * Returns the Message's source.
	 *
	 * @return The Message's source.
	 */
	public String getSource()
	{
		return source;
	}

	/**
	 * Returns the Message's text.
	 *
	 * @return The Message's text.
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * Returns the Message's details.
	 *
	 * @return The Message's details.
	 */
	public String getDetails()
	{
		return details;
	}

	/**
	 * Checks whether the Message has the given code.
	 *
	 * @param code The code to check for.
	 * @return Whether the Message's code matches the given code.
	 */
	public boolean hasCode(final int code)
	{
		return getCode() == code;
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(getCode(),
				getLevel(),
				getIndex(),
				getSource(),
				getText(),
				getDetails());
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final Message other = (Message) obj;
		return Objects.equal(getCode(), other.getCode())
				&& Objects.equal(getLevel(), other.getLevel())
				&& Objects.equal(getIndex(), other.getIndex())
				&& Objects.equal(getSource(), other.getSource())
				&& Objects.equal(getText(), other.getText())
				&& Objects.equal(getDetails(), other.getDetails());
	}

	/**
	 * Returns the Message as a string.
	 *
	 * <pre>
	 * ERROR (1, Test, 0): "The error" (details: "java.lang.Exception: Inner exception")
	 * </pre>
	 *
	 * @return The Message as a string.
	 */
	@Override
	public String toString()
	{
		return String.format("%s (%s, %s, %s): \"%s\" (details: \"%s\")",
				getLevel(), getCode(), getSource(), getIndex(), getText(), getDetails());
	}
}
