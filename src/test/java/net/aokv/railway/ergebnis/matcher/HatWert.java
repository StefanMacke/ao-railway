package net.aokv.railway.ergebnis.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import net.aokv.railway.ergebnis.Ergebnis;

public class HatWert<T> extends TypeSafeDiagnosingMatcher<Ergebnis<T>>
{
	private final T wert;

	public HatWert(final T wert)
	{
		this.wert = wert;
	}

	@Override
	public void describeTo(final Description description)
	{
		description.appendText("Ein erfolgreiches Ergebnis mit Wert <")
				.appendValue(wert)
				.appendText(">");
	}

	@Override
	protected boolean matchesSafely(final Ergebnis<T> ergebnis, final Description description)
	{
		description.appendText("was ")
				.appendValue(ergebnis.toString());
		return ergebnis.getWert().equals(wert);
	}
}