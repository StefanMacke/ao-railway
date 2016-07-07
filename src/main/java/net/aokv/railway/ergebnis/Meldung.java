package net.aokv.railway.ergebnis;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import com.google.common.base.Objects;

/**
 * Eine Meldung. Immutable. Wird von <code>MeldungBuilder</code> erzeugt.
 *
 * @author macke
 */
public final class Meldung implements Serializable
{
	private static final long serialVersionUID = 8128659250371079506L;

	private Meldungsebene ebene;
	private int code;
	private String quelle;
	private int index;
	private String text;
	private String details;

	/**
	 * Der Builder für eine Meldung.
	 *
	 * @author macke
	 *
	 */
	public static class MeldungBuilder
	{
		private final Meldung meldung;

		private MeldungBuilder()
		{
			meldung = new Meldung();
		}

		/**
		 * Erzeugt die fertige Meldung.
		 *
		 * @return Die fertige Meldung.
		 */
		public Meldung baue()
		{
			return meldung;
		}

		/**
		 * Setzt den Code der neuen Meldung.
		 *
		 * @param code Der Code der neuen Meldung.
		 * @return Der Builder zum Weiterbauen.
		 */
		public MeldungBuilder mitCode(final int code)
		{
			meldung.code = code;
			return this;
		}

		/**
		 * Setzt die Ebene der neuen Meldung.
		 *
		 * @param ebene Die Ebene der neuen Meldung.
		 * @return Der Builder zum Weiterbauen.
		 */
		public MeldungBuilder mitEbene(final Meldungsebene ebene)
		{
			meldung.ebene = ebene;
			return this;
		}

		/**
		 * Setzt den Index der neuen Meldung.
		 *
		 * @param index Der Index der neuen Meldung.
		 * @return Der Builder zum Weiterbauen.
		 */
		public MeldungBuilder mitIndex(final int index)
		{
			meldung.index = index;
			return this;
		}

		/**
		 * Setzt den Text der neuen Meldung.
		 *
		 * @param text Der Text der neuen Meldung.
		 * @return Der Builder zum Weiterbauen.
		 */
		public MeldungBuilder mitText(final String text)
		{
			meldung.text = text;
			return this;
		}

		/**
		 * Setzt die Quelle der neuen Meldung.
		 *
		 * @param quelle Die Quelle der neuen Meldung.
		 * @return Der Builder zum Weiterbauen.
		 */
		public MeldungBuilder mitQuelle(final String quelle)
		{
			meldung.quelle = quelle;
			return this;
		}

		/**
		 * Setzt die Details der neuen Meldung.
		 *
		 * @param details Die Details der neuen Meldung.
		 * @return Der Builder zum Weiterbauen.
		 */
		public MeldungBuilder mitDetails(final String details)
		{
			meldung.details = details;
			return this;
		}

		/**
		 * Setzt die Details der neuen Meldung aus einem Fehler.
		 *
		 * @param throwable Der Fehler für die Details der neuen Meldung.
		 * @return Der Builder zum Weiterbauen.
		 */
		public MeldungBuilder mitDetails(final Throwable throwable)
		{
			final StringWriter stringWriter = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(stringWriter);
			throwable.printStackTrace(printWriter);
			meldung.details = stringWriter.toString();
			return this;
		}
	}

	/**
	 * Erzeugt einen neuen Builder für eine Meldung.
	 *
	 * @return Der Builder für eine Meldung.
	 */
	public static MeldungBuilder neu()
	{
		return new MeldungBuilder();
	}

	/**
	 * Erzeugt einen neuen Builder für eine Fehlermeldung.
	 *
	 * @return Der Builder für eine Fehlermeldung.
	 */
	public static MeldungBuilder neuerFehler()
	{
		return new MeldungBuilder()
				.mitEbene(Meldungsebene.FEHLER);
	}

	/**
	 * Erzeugt einen neuen Builder für eine Fehlermeldung.
	 *
	 * @param text Der Text der Fehlermeldung.
	 * @return Der Builder für eine Fehlermeldung.
	 */
	public static Meldung mitFehler(final String text)
	{
		return new MeldungBuilder()
				.mitEbene(Meldungsebene.FEHLER)
				.mitText(text)
				.baue();
	}

	private Meldung()
	{
		ebene = Meldungsebene.FEHLER;
		code = 1;
		quelle = ermittleQuelleAusCallStack();
		index = 0;
		text = "Kein Text";
		details = "Keine weiteren Informationen";
	}

	private String ermittleQuelleAusCallStack()
	{
		final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		if (stack.length >= 8)
		{
			return stack[7].getMethodName();
		}
		return "Keine Quelle";
	}

	/**
	 * Gibt den Code der Meldung zurück.
	 *
	 * @return Der Code der Meldung.
	 */
	public int getCode()
	{
		return code;
	}

	/**
	 * Gibt den Code der Meldung zurück.
	 *
	 * @return Der Code der Meldung.
	 */
	public Meldungsebene getEbene()
	{
		return ebene;
	}

	/**
	 * Gibt den Index der Meldung zurück.
	 *
	 * @return Der Index der Meldung.
	 */
	public int getIndex()
	{
		return index;
	}

	/**
	 * Gibt die Quelle der Meldung zurück.
	 *
	 * @return Die Quelle der Meldung.
	 */
	public String getQuelle()
	{
		return quelle;
	}

	/**
	 * Gibt den Text der Meldung zurück.
	 *
	 * @return Der Text der Meldung.
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * Gibt die Dateils der Meldung zurück.
	 *
	 * @return Die Dateils der Meldung.
	 */
	public String getDetails()
	{
		return details;
	}

	/**
	 * Prüft, ob die Meldung den angegebenen Code hat.
	 *
	 * @param code Der zu prüfende Code.
	 * @return Ob der Code übereinstimmt.
	 */
	public boolean hatCode(final int code)
	{
		return getCode() == code;
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(getCode(),
				getEbene(),
				getIndex(),
				getQuelle(),
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
		final Meldung other = (Meldung) obj;
		return Objects.equal(getCode(), other.getCode())
				&& Objects.equal(getEbene(), other.getEbene())
				&& Objects.equal(getIndex(), other.getIndex())
				&& Objects.equal(getQuelle(), other.getQuelle())
				&& Objects.equal(getText(), other.getText())
				&& Objects.equal(getDetails(), other.getDetails());
	}

	/**
	 * Gibt die Meldung als String aus.
	 *
	 * <pre>
	 * FEHLER (1, Test, 0): "Der Fehler" (Details: "java.lang.Exception: Innere Exception")
	 * </pre>
	 *
	 * @return Die Meldung als String.
	 */
	@Override
	public String toString()
	{
		return String.format("%s (%s, %s, %s): \"%s\" (Details: \"%s\")",
				getEbene(), getCode(), getQuelle(), getIndex(), getText(), getDetails());
	}
}
