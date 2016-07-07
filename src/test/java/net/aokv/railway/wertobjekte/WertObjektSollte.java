package net.aokv.railway.wertobjekte;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

public class WertObjektSollte
{
	@SuppressWarnings("serial")
	private static class MeinStringObjekt extends WertObjekt<String>
	{
		public MeinStringObjekt(final String wert)
		{
			super(wert);
		}
	}

	@SuppressWarnings("serial")
	private static class MeinIntegerObjekt extends WertObjekt<Integer>
	{
		public MeinIntegerObjekt(final Integer wert)
		{
			super(wert);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> WertObjekt<T> erzeugeWertObjekt(final T wert)
	{
		if (wert instanceof String)
		{
			return (WertObjekt<T>) new MeinStringObjekt((String) wert);
		}
		return (WertObjekt<T>) new MeinIntegerObjekt((Integer) wert);
	}

	@Test
	public void seinenWertKennen()
	{
		assertThat(erzeugeWertObjekt("Der String").getWert(), is("Der String"));
		assertThat(erzeugeWertObjekt(123).getWert(), is(123));
	}

	@Test
	public void fürGleicheWerteGleichenHashcodeErzeugen()
	{
		assertThat(erzeugeWertObjekt("Ein String").hashCode(),
				is(erzeugeWertObjekt("Ein String").hashCode()));
		assertThat(erzeugeWertObjekt(123).hashCode(),
				is(erzeugeWertObjekt(123).hashCode()));
	}

	@Test
	public void fürUnterschiedlicheWerteUnterschiedlicheHashcodeErzeugen()
	{
		assertThat(erzeugeWertObjekt("Ein String").hashCode(),
				is(not(erzeugeWertObjekt("Ein anderer String").hashCode())));
		assertThat(erzeugeWertObjekt(123).hashCode(),
				is(not(erzeugeWertObjekt(124).hashCode())));
	}

	@Test
	public void gleicheWerteAlsGleichErkennen()
	{
		assertThat(erzeugeWertObjekt("Ein String"),
				is(erzeugeWertObjekt("Ein String")));
		assertThat(erzeugeWertObjekt(123),
				is(erzeugeWertObjekt(123)));
	}

	@Test
	public void verschiedeneWerteAlsVerschiedenErkennen()
	{
		assertThat(erzeugeWertObjekt("Ein String"),
				is(not(erzeugeWertObjekt("Ein anderer String"))));
		assertThat(erzeugeWertObjekt(123),
				is(not(erzeugeWertObjekt(124))));
	}

	@Test
	public void exceptionWerfenWennWertNullIst()
	{
		try
		{
			new MeinStringObjekt(null);
			fail("WertObjekt sollte sich nicht mit null erzeugen lassen.");
		}
		catch (final IllegalArgumentException e)
		{
			assertThat(e.getMessage(), is("Wert für MeinStringObjekt darf nicht null sein."));
		}
		try
		{
			new MeinIntegerObjekt(null);
			fail("WertObjekt sollte sich nicht mit null erzeugen lassen.");
		}
		catch (final IllegalArgumentException e)
		{
			assertThat(e.getMessage(), is("Wert für MeinIntegerObjekt darf nicht null sein."));
		}
	}
}
