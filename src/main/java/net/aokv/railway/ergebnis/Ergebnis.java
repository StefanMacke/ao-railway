package net.aokv.railway.ergebnis;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Ergebnis einer Aktion. Kann fehlerhaft oder erfolgreich sein. In letzterem Fall kann ein Wert des
 * angegebenen Typs W enthalten sein.
 *
 * @author macke
 *
 * @param <W> Der Typ des enthaltenen Werts.
 */
public final class Ergebnis<W>
{
	/**
	 * Erzeugt ein neues Ergebnis mit der angegebenen Fehlermeldung.
	 *
	 * @param fehler Die Fehlermeldung.
	 * @return Fehlerhaftes Ergebnis.
	 */
	public static <W> Ergebnis<W> mitFehler(final Meldung fehler)
	{
		stelleSicherDassParameterNichtNullIst(fehler, "Fehler");
		return new Ergebnis<W>(null, fehler);
	}

	/**
	 * Erzeugt ein neues Ergebnis mit einer Fehlermeldung aus dem angegebenen Text.
	 *
	 * @param fehler Der Fehlertext.
	 * @return Fehlerhaftes Ergebnis.
	 */
	public static <W> Ergebnis<W> mitFehler(final String fehler)
	{
		stelleSicherDassParameterNichtNullIst(fehler, "Fehler");
		return mitFehler(Meldung.mitFehler(fehler));
	}

	/**
	 * Erzeugt ein erfolgreiches Ergebnis ohne Wert.
	 *
	 * @return Erfolgreiches Ergebnis.
	 */
	public static Ergebnis<Void> ohneWert()
	{
		return new Ergebnis<Void>();
	}

	/**
	 * Erzeugt ein erfolgreiches Ergebnis mit dem angegebenen Wert. Der Wert darf nicht null sein.
	 *
	 * @param wert Der enthaltene Wert.
	 * @return Erfolgreiches Ergebnis mit Wert.
	 * @throws IllegalArgumentException Wenn Wert null ist.
	 */
	public static <W> Ergebnis<W> mitWert(final W wert)
	{
		stelleSicherDassParameterNichtNullIst(wert, "Wert");
		return new Ergebnis<W>(wert, null);
	}

	/**
	 * Erzeugt ein Ergebnis mit dem optional angegebenen Wert. Wenn der Wert null ist, wird ein
	 * fehlerhaftes Ergebnis erzeugt.
	 *
	 * @param wert Der Wert.
	 * @param fehler Der zu setzende Fehler falls Wert nicht vorhanden.
	 * @return Erfolgreiches Ergebnis mit Wert oder fehlerhaftes Ergebnis.
	 */
	public static <W> Ergebnis<W> aus(final W wert, final Meldung fehler)
	{
		return Ergebnis.aus(Optional.ofNullable(wert), fehler);
	}

	/**
	 * Erzeugt ein Ergebnis mit dem optional angegebenen Wert. Wenn der Wert null oder ein leeres
	 * Optional ist, wird ein fehlerhaftes Ergebnis erzeugt.
	 *
	 * @param wertOderNichts Der optionale Wert.
	 * @param fehler Der zu setzende Fehler falls Wert nicht vorhanden.
	 * @return Erfolgreiches Ergebnis mit Wert oder fehlerhaftes Ergebnis.
	 */
	public static <W> Ergebnis<W> aus(final Optional<W> wertOderNichts, final Meldung fehler)
	{
		if (wertOderNichts == null || !wertOderNichts.isPresent())
		{
			return Ergebnis.mitFehler(fehler);
		}
		return Ergebnis.mitWert(wertOderNichts.get());
	}

	private static void stelleSicherDassParameterNichtNullIst(final Object parameter, final String name)
	{
		if (parameter == null)
		{
			throw new IllegalArgumentException(
					String.format("%s darf nicht null sein.", name));
		}
	}

	private final Optional<W> wert;
	private final Optional<Meldung> fehler;

	private Ergebnis()
	{
		wert = Optional.empty();
		fehler = Optional.empty();
	}

	private Ergebnis(final W wert, final Meldung fehler)
	{
		this.wert = Optional.ofNullable(wert);
		this.fehler = Optional.ofNullable(fehler);
	}

	/**
	 * Prüft, ob das Ergebnis fehlerhaft ist.
	 *
	 * @return Ob das Ergebnis fehlerhaft ist.
	 */
	public boolean istFehler()
	{
		return fehler.isPresent();
	}

	/**
	 * Prüft, ob das Ergebnis erfolgreich ist.
	 *
	 * @return Ob das Ergebnis erfolgreich ist.
	 */
	public boolean istErfolg()
	{
		return !istFehler();
	}

	/**
	 * Gibt den Wert des Ergebnisses zurück.
	 *
	 * @return Der Wert des Ergebnisses.
	 * @throws FehlerhaftesErgebnisHatKeinenWertException Falls Ergebnis fehlerhaft ist.
	 * @throws LeeresErgebnisHatKeinenWertException Falls Ergebnis keinen Wert hat.
	 */
	public W getWert()
	{
		if (istFehler())
		{
			throw new FehlerhaftesErgebnisHatKeinenWertException(getFehler());
		}
		if (!wert.isPresent())
		{
			throw new LeeresErgebnisHatKeinenWertException();
		}
		return wert.get();
	}

	/**
	 * Gibt den Fehler des Ergebnisses zurück.
	 *
	 * @return Der Fehler des Ergebnisses.
	 * @throws ErfolgreichesErgebnisHatKeinenFehlerException Falls Ergebnis erfolgreich ist.
	 */
	public Meldung getFehler()
	{
		if (istErfolg())
		{
			throw new ErfolgreichesErgebnisHatKeinenFehlerException();
		}
		return fehler.get();
	}

	/**
	 * Gibt das Ergebnis als String zurück.
	 *
	 * <pre>
	 * Ergebnis (Erfolg mit Wert der Wert)
	 * </pre>
	 *
	 * <pre>
	 * Ergebnis (Fehler: FEHLER (1, mitFehler, 0): "der Fehler" (Details: "Keine weiteren Informationen"))
	 * </pre>
	 *
	 * @return Das Ergebnis als String.
	 */
	@Override
	public String toString()
	{
		final StringBuilder ergebnis = new StringBuilder("Ergebnis (");
		if (istErfolg())
		{
			ergebnis.append("Erfolg");
			if (wert.isPresent())
			{
				ergebnis.append(" mit Wert ");
				ergebnis.append(getWert());
			}
		}
		else
		{
			ergebnis.append("Fehler: ");
			ergebnis.append(getFehler());
		}
		ergebnis.append(')');
		return ergebnis.toString();
	}

	/**
	 * Kombiniert mehrere Ergebnisse zu einem Gesamtergebnis. Liefert ein erfolgreiches Ergebnis,
	 * falls alle zu kombinierenden Ergebnisse erfolgreich sind, oder das erste fehlerhafte
	 * Ergebnis.
	 *
	 * @param ergebnisse Die zu kombinierenden Ergebnisse.
	 * @return Ergebnis der Kombination.
	 */
	@SafeVarargs
	public static Ergebnis<?> kombiniere(final Ergebnis<?>... ergebnisse)
	{
		return Arrays.stream(ergebnisse)
				.filter(ergebnis -> ergebnis.istFehler())
				.findFirst()
				.orElse(Ergebnis.ohneWert());
	}

	private <T> Optional<Ergebnis<T>> ergebnisWennFehler()
	{
		return istFehler()
				? Optional.of(Ergebnis.mitFehler(getFehler()))
				: Optional.empty();
	}

	/**
	 * Führt die angegebene Funktion aus, falls das Ergebnis erfolgreich ist.
	 *
	 * @param funktion Die auszuführende Funktion.
	 * @return Ergebnis der Funktion.
	 */
	public <T> Ergebnis<T> beiErfolg(final Supplier<Ergebnis<T>> funktion)
	{
		final Optional<Ergebnis<T>> ergebnis = ergebnisWennFehler();
		return ergebnis.orElseGet(funktion);
	}

	/**
	 * Führt die angegebene Funktion aus, falls das Ergebnis erfolgreich ist.
	 *
	 * @param funktion Die auszuführende Funktion.
	 * @param clazz Der Rückgabetyp der Funktion.
	 * @return Ergebnis der Funktion.
	 */
	public <T> Ergebnis<T> beiErfolg(final Supplier<T> funktion, final Class<T> clazz)
	{
		return beiErfolg(() -> Ergebnis.mitWert(funktion.get()));
	}

	/**
	 * Führt die angegebene Funktion aus, falls das Ergebnis erfolgreich ist.
	 *
	 * @param funktion Die auszuführende Funktion.
	 * @return Das bisherige Ergebnis.
	 */
	public Ergebnis<W> beiErfolg(final Consumer<W> funktion)
	{
		if (!istFehler())
		{
			funktion.accept(getWert());
		}
		return this;
	}

	/**
	 * Extrahiert das Element aus dem optionalen Wert des Ergebnisses.
	 *
	 * @param element Typ des optionalen Wertes.
	 * @param fehler Fehler, falls Wert nicht extrahiert werden kann.
	 * @return Ergebnis mit Typ des optionalen Wertes oder fehlerhaftes Ergebnis.
	 */
	public <T> Ergebnis<T> beiVorhandenemWert(final Class<T> element, final Meldung fehler)
	{
		if (istFehler())
		{
			return Ergebnis.mitFehler(getFehler());
		}
		if (!(getWert() instanceof Optional))
		{
			return Ergebnis.mitFehler(fehler);
		}
		@SuppressWarnings("unchecked")
		final Optional<T> optional = (Optional<T>) getWert();
		if (!optional.isPresent())
		{
			return Ergebnis.mitFehler(fehler);
		}
		return Ergebnis.mitWert(optional.get());
	}

	/**
	 * Führt die angegebene Funktion aus, falls das Ergebnis fehlerhaft ist.
	 *
	 * @param funktion Die auszuführende Funktion.
	 * @return Das bisherige Ergebnis.
	 */
	public Ergebnis<?> beiFehler(final Runnable funktion)
	{
		if (istFehler())
		{
			funktion.run();
		}
		return this;
	}

	/**
	 * Führt die angegebene Funktion aus, egal ob das Ergebnis fehlerhaft oder erfolgreich ist.
	 *
	 * @param funktion Die auszuführende Funktion.
	 * @return Das bisherige Ergebnis.
	 */
	public Ergebnis<W> inJedemFall(final Consumer<Ergebnis<W>> funktion)
	{
		funktion.accept(this);
		return this;
	}

	/**
	 * Führt die angegebene Prüfung durch, falls das Ergebnis erfolgreich ist.
	 *
	 * @param pruefung Die durchzuführende Prüfung.
	 * @param fehler Fehler, falls die Prüfung nicht erfolgreich ist.
	 * @return Ergebnis mit überprüftem Wert oder fehlerhaftes Ergebnis.
	 * @throws LeeresErgebnisHatKeinenWertException Falls das aktuelle Ergebnis keinen Wert hat.
	 */
	public Ergebnis<W> stelleSicher(final Predicate<W> pruefung, final Meldung fehler)
	{
		if (istFehler())
		{
			return this;
		}
		try
		{
			if (!pruefung.test(getWert()))
			{
				return Ergebnis.mitFehler(fehler);
			}
		}
		catch (final LeeresErgebnisHatKeinenWertException exception)
		{
			throw exception;
		}
		catch (final Exception exception)
		{
			return Ergebnis.mitFehler(fehler);
		}
		return this;
	}

	/**
	 * Extrahiert ein <code>Ergebnis&lt;T&gt;</code> aus einem
	 * <code>Ergebnis&lt;Ergebnis&lt;T&gt;&gt;</code>, falls das aktuelle Ergebnis erfolgreich ist.
	 *
	 * @param funktion Die Funktion, die ein <code>Ergebnis&lt;Ergebnis&lt;T&gt;&gt;</code> liefert.
	 * @return Das extrahierte <code>Ergebnis&lt;T&gt;</code>.
	 */
	public <T> Ergebnis<T> flatMap(final Function<W, Ergebnis<T>> funktion)
	{
		final Optional<Ergebnis<T>> ergebnis = ergebnisWennFehler();
		return ergebnis.orElseGet(() -> funktion.apply(getWert()));
	}

	/**
	 * Wandelt das aktuelle Ergebnis in ein Ergebnis des angegenen Typs um, falls das aktuelle
	 * Ergebnis erfolgreich ist.
	 *
	 * @param funktion Die Funktion, die den Wert des neuen Ergebnisses liefert.
	 * @return Das Ergebnis des neuen Typs oder ein fehlerhaftes Ergebnis.
	 */
	public <T> Ergebnis<T> map(final Function<W, T> funktion)
	{
		return flatMap(funktion.andThen(wert -> Ergebnis.mitWert(wert)));
	}
}
