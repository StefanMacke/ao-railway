package net.aokv.railway.ergebnis.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import net.aokv.railway.ergebnis.Ergebnis;

public class IstFehlerMitMeldungstext<T> extends TypeSafeDiagnosingMatcher<Ergebnis<T>>
{
	private final String meldungstext;

	public IstFehlerMitMeldungstext(final String meldungstext)
	{
		this.meldungstext = meldungstext;
	}

	@Override
	public void describeTo(final Description description)
	{
		description.appendText("Ein fehlerhaftes Ergebnis mit Fehlertext <")
				.appendValue(meldungstext)
				.appendText(">");
	}

	@Override
	protected boolean matchesSafely(final Ergebnis<T> ergebnis, final Description description)
	{
		description.appendText("was ")
				.appendValue(ergebnis.toString());
		return ergebnis.getFehler().getText().equals(meldungstext);
	}
}