package net.aokv.railway.ergebnis.matcher;

import net.aokv.railway.ergebnis.Meldung;

public class ErgebnisMatcher
{
	public static <T> IstErfolg<T> istErfolg()
	{
		return new IstErfolg<T>();
	}

	public static <T> IstFehler<T> istFehler()
	{
		return new IstFehler<T>();
	}

	public static <T> IstFehlerMitMeldungstext<T> istFehlerMitMeldungstext(final String meldungstext)
	{
		return new IstFehlerMitMeldungstext<T>(meldungstext);
	}

	public static <T> IstFehlerMitMeldung<T> istFehlerMitMeldung(final Meldung meldung)
	{
		return new IstFehlerMitMeldung<T>(meldung);
	}

	public static <T> HatWert<T> hatWert(final T wert)
	{
		return new HatWert<T>(wert);
	}
}
