package net.aokv.railway.ergebnis.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import net.aokv.railway.ergebnis.Ergebnis;

public class IstErfolg<T> extends TypeSafeDiagnosingMatcher<Ergebnis<T>>
{
	@Override
	public void describeTo(final Description description)
	{
		description.appendText("Ein erfolgreiches Ergebnis");
	}

	@Override
	protected boolean matchesSafely(final Ergebnis<T> ergebnis, final Description description)
	{
		description.appendText("was ")
				.appendValue(ergebnis.toString());
		return ergebnis.istErfolg();
	}
}