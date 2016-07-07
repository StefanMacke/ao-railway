package net.aokv.railway.ergebnis.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import net.aokv.railway.ergebnis.Ergebnis;
import net.aokv.railway.ergebnis.Meldung;

public class IstFehlerMitMeldung<T> extends TypeSafeDiagnosingMatcher<Ergebnis<T>>
{
	private final Meldung meldung;

	public IstFehlerMitMeldung(final Meldung meldung)
	{
		this.meldung = meldung;
	}

	@Override
	public void describeTo(final Description description)
	{
		description.appendText("Ein fehlerhaftes Ergebnis mit Meldung <")
				.appendValue(meldung)
				.appendText(">");
	}

	@Override
	protected boolean matchesSafely(final Ergebnis<T> ergebnis, final Description description)
	{
		description.appendText("was ")
				.appendValue(ergebnis.toString());
		return ergebnis.getFehler().equals(meldung);
	}
}