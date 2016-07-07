package net.aokv.railway.ergebnis;

import static net.aokv.railway.ergebnis.matcher.ErgebnisMatcher.hatWert;
import static net.aokv.railway.ergebnis.matcher.ErgebnisMatcher.istErfolg;
import static net.aokv.railway.ergebnis.matcher.ErgebnisMatcher.istFehler;
import static net.aokv.railway.ergebnis.matcher.ErgebnisMatcher.istFehlerMitMeldung;
import static net.aokv.railway.ergebnis.matcher.ErgebnisMatcher.istFehlerMitMeldungstext;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Optional;
import java.util.function.Function;

import org.junit.Test;

public class ErgebnisSollte
{
	private static final Meldung FEHLER = Meldung.mitFehler("Fehler");

	@Test
	public void beiFehlerFehlerhaftSein()
	{
		assertThat(Ergebnis.mitFehler("der Fehler"), istFehler());
	}

	@Test
	public void beiFehlerNichtErfolgreichSein()
	{
		assertThat(Ergebnis.mitFehler("der Fehler").istErfolg(), is(false));
	}

	@Test
	public void beiFehlerDenFehlerLiefern()
	{
		assertThat(Ergebnis.mitFehler("der Fehler"), istFehlerMitMeldungstext("der Fehler"));
	}

	@Test(expected = FehlerhaftesErgebnisHatKeinenWertException.class)
	public void beiFehlerKeinenWertLiefern()
	{
		Ergebnis.mitFehler("der Fehler").getWert();
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullAlsFehlerNichtAkzeptieren()
	{
		final String f = null;
		Ergebnis.mitFehler(f);
	}

	@Test
	public void beiErfolgOhneWertNichtFehlerhaftSein()
	{
		assertThat(Ergebnis.ohneWert().istFehler(), is(false));
	}

	@Test
	public void beiErfolgOhneWertErfolgreichSein()
	{
		assertThat(Ergebnis.ohneWert(), istErfolg());
	}

	@Test
	public void beiErfolgMitWertNichtFehlerhaftSein()
	{
		assertThat(Ergebnis.mitWert("der Wert").istFehler(), is(false));
	}

	@Test
	public void beiErfolgMitWertErfolgreichSein()
	{
		assertThat(Ergebnis.mitWert("der Wert"), istErfolg());
	}

	@Test
	public void beiErfolgMitWertDenWertLiefern()
	{
		assertThat(Ergebnis.mitWert("der Wert"), hatWert("der Wert"));
	}

	@Test(expected = ErfolgreichesErgebnisHatKeinenFehlerException.class)
	public void beiErfolgMitWertKeinenFehlerLiefern()
	{
		Ergebnis.mitWert("der Wert").getFehler();
	}

	@Test(expected = ErfolgreichesErgebnisHatKeinenFehlerException.class)
	public void beiErfolgOhneWertKeinenFehlerLiefern()
	{
		Ergebnis.ohneWert().getFehler();
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullAlsWertNichtAkzeptieren()
	{
		Ergebnis.mitWert(null);
	}

	@Test
	public void erfolgreicheErgebnisseKombinieren()
	{
		assertThat(Ergebnis.kombiniere(Ergebnis.ohneWert(), Ergebnis.ohneWert()), istErfolg());
		assertThat(Ergebnis.kombiniere(Ergebnis.ohneWert(), Ergebnis.mitWert("wert")), istErfolg());
		assertThat(Ergebnis.kombiniere(Ergebnis.mitWert("wert"), Ergebnis.ohneWert()), istErfolg());
		assertThat(Ergebnis.kombiniere(Ergebnis.mitWert("wert"), Ergebnis.mitWert("wert2")), istErfolg());
	}

	@Test
	public void fehlerhafteErgebnisseKombinieren()
	{
		assertThat(Ergebnis.kombiniere(Ergebnis.ohneWert(), Ergebnis.mitFehler("fehler")), istFehler());
		assertThat(Ergebnis.kombiniere(Ergebnis.mitWert("wert"), Ergebnis.mitFehler("fehler")), istFehler());
		assertThat(Ergebnis.kombiniere(Ergebnis.mitFehler("fehler"), Ergebnis.ohneWert()), istFehler());
		assertThat(Ergebnis.kombiniere(Ergebnis.mitFehler("fehler"), Ergebnis.mitWert("wert")), istFehler());
		assertThat(Ergebnis.kombiniere(Ergebnis.mitFehler("f"), Ergebnis.mitFehler("f2")), istFehler());
	}

	@Test
	public void leereErgebnisseKombinieren()
	{
		assertThat(Ergebnis.kombiniere(), istErfolg());
	}

	@Test
	public void beiErfolgErfolgsfunktionAusfuehren()
	{
		assertThat(
				Ergebnis.ohneWert().beiErfolg(() ->
				{
					return Ergebnis.mitWert("Erfolg");
				}), hatWert("Erfolg"));
		assertThat(
				Ergebnis.mitWert("erster Wert").beiErfolg(() ->
				{
					return Ergebnis.mitWert("Erfolg");
				}), hatWert("Erfolg"));
		final Function<String, String> funktion = wert -> wert.toUpperCase();
		assertThat(
				Ergebnis.mitWert("eingang")
						.map(funktion),
				hatWert("EINGANG"));
	}

	@Test
	public void beiFehlerErfolgsfunktionNichtAusfuehren()
	{
		assertThat(Ergebnis.mitFehler("Fehler").beiErfolg(() ->
		{
			return Ergebnis.mitWert("Erfolg");
		}), istFehlerMitMeldungstext(FEHLER.getText()));

		final Ergebnis<String> ergebnis = Ergebnis.mitFehler("Fehler");
		final Function<String, String> funktion = wert -> wert.toUpperCase();
		assertThat(
				ergebnis.map(funktion),
				istFehlerMitMeldungstext(FEHLER.getText()));
	}

	private static class Container
	{
		private String string;

		public Container(final String string)
		{
			this.string = string;
		}

		public String getString()
		{
			return string;
		}

		public void setString(final String string)
		{
			this.string = string;
		}
	}

	@Test
	public void beiErfolgKonsumentenAufrufen()
	{
		final Container c = new Container("");
		assertThat(Ergebnis.mitWert("Wert")
				.beiErfolg((final String w) -> c.setString(w)), hatWert("Wert"));
		assertThat(c.getString(), is("Wert"));
	}

	@Test
	public void beiFehlerKonsumentenNichtAufrufen()
	{
		final Container c = new Container("");
		assertThat(Ergebnis.<String> mitFehler(FEHLER)
				.beiErfolg((final String w) -> c.setString(w)), istFehlerMitMeldung(FEHLER));
		assertThat(c.getString(), is(""));
	}

	@Test
	public void beiFehlerFehlerfunktionAusfuehren()
	{
		final Container c = new Container("");
		assertThat(Ergebnis.mitFehler("Der Fehler").beiFehler(() -> c.setString("Fehler")), istFehler());
		assertThat(c.getString(), is("Fehler"));
	}

	@Test
	public void beiErfolgFehlerfunktionNichtAusfuehren()
	{
		final Container c = new Container("");
		assertThat(Ergebnis.ohneWert().beiFehler(() -> c.setString("Fehler")), istErfolg());
		assertThat(c.getString(), is(""));
	}

	@Test
	public void beiErfolgErzeugtesErgebnisZurueckliefern()
	{
		assertThat(
				Ergebnis.mitWert("Wert")
						.flatMap(w -> Ergebnis.aus(w.length(), FEHLER)),
				hatWert(4));
		assertThat(
				Ergebnis.<String> mitFehler(FEHLER)
						.flatMap(w -> Ergebnis.aus(w.length(), FEHLER)),
				istFehlerMitMeldung(FEHLER));
	}

	@Test
	public void beiErfolgBeidefunktionDurchreichen()
	{
		final Container c = new Container("");
		assertThat(Ergebnis.<String> mitWert("Wert")
				.inJedemFall(e -> c.setString(e.getWert().toString())), istErfolg());
		assertThat(c.getString(), is("Wert"));
	}

	@Test
	public void beiFehlerBeidefunktionDurchreichen()
	{
		final Container c = new Container("");
		assertThat(Ergebnis.<String> mitFehler(FEHLER)
				.inJedemFall(e -> c.setString(e.getFehler().getText())), istFehler());
		assertThat(c.getString(), is("Fehler"));
	}

	@Test
	public void sichAusVorhandenemOptionalErzeugenLassen()
	{
		final Ergebnis<String> ergebnis = Ergebnis.aus(Optional.of("Wert"), FEHLER);
		assertThat(ergebnis, istErfolg());
		assertThat(ergebnis, hatWert("Wert"));
	}

	@Test
	public void sichAusNichtVorhandenemOptionalErzeugenLassen()
	{
		final Ergebnis<String> ergebnis = Ergebnis.aus(Optional.empty(), FEHLER);
		assertThat(ergebnis, istFehler());
		assertThat(ergebnis, istFehlerMitMeldung(FEHLER));
	}

	@Test
	public void sichAusVorhandenemWertErzeugenLassen()
	{
		final Ergebnis<String> ergebnis = Ergebnis.aus("Wert", FEHLER);
		assertThat(ergebnis, istErfolg());
		assertThat(ergebnis, hatWert("Wert"));
	}

	@Test
	public void sichAusNichtVorhandenemWertErzeugenLassen()
	{
		final Ergebnis<String> ergebnis = Ergebnis.aus(null, FEHLER);
		assertThat(ergebnis, istFehler());
		assertThat(ergebnis, istFehlerMitMeldung(FEHLER));
	}

	@Test
	public void sichAusNullOptionalErzeugenLassen()
	{
		final Optional<String> o = null;
		final Ergebnis<String> ergebnis = Ergebnis.aus(o, FEHLER);
		assertThat(ergebnis, istFehler());
		assertThat(ergebnis, istFehlerMitMeldung(FEHLER));
	}

	@Test
	public void pruefungAufWertDurchfuehren()
	{
		Ergebnis<String> ergebnis = Ergebnis.<String> mitWert("Wert")
				.stelleSicher(wert -> wert.length() > 1, FEHLER);
		assertThat(ergebnis, istErfolg());
		assertThat(ergebnis, hatWert("Wert"));

		ergebnis = Ergebnis.<String> mitWert("Wert")
				.stelleSicher(wert -> wert.length() > 5, FEHLER);
		assertThat(ergebnis, istFehler());
		assertThat(ergebnis, istFehlerMitMeldung(FEHLER));

		ergebnis = Ergebnis.<String> mitFehler(FEHLER)
				.stelleSicher(wert -> wert.length() > 1, Meldung.mitFehler("Fehler2"));
		assertThat(ergebnis, istFehler());
		assertThat(ergebnis, istFehlerMitMeldung(FEHLER));
	}

	@Test
	public void pruefungAufWertMitExceptionDurchfuehren()
	{
		final Ergebnis<String> ergebnis = Ergebnis.<String> mitWert("Wert")
				.stelleSicher(wert -> Long.parseLong(wert) > 1, FEHLER);
		assertThat(ergebnis, istFehler());
		assertThat(ergebnis, istFehlerMitMeldung(FEHLER));
	}

	@Test
	public void mappingDurchfuehren()
	{
		assertThat(Ergebnis.mitWert("Wert").map(wert -> wert.length()), hatWert(4));
		assertThat(Ergebnis.mitWert("Wert").map(wert -> wert + "2"), hatWert("Wert2"));
		assertThat(Ergebnis.mitFehler("Fehler").map(wert -> wert + "2"),
				istFehlerMitMeldungstext(FEHLER.getText()));
	}

	@Test
	public void flatMappingAufErgebnisDurchfuehren()
	{
		assertThat(Ergebnis.mitWert("Wert")
				.flatMap(wert -> Ergebnis.mitWert(wert.length())), hatWert(4));
		assertThat(Ergebnis.<String> mitFehler(FEHLER)
				.flatMap(wert -> Ergebnis.mitWert(wert.length())), istFehlerMitMeldung(FEHLER));
	}

	@Test(expected = LeeresErgebnisHatKeinenWertException.class)
	public void mappingOhneWertNichtDurchfuehren()
	{
		Ergebnis.ohneWert().map(wert -> wert.getClass());
	}

	@Test(expected = LeeresErgebnisHatKeinenWertException.class)
	public void mappingAufErgebnisOhneWertNichtDurchfuehren()
	{
		Ergebnis.ohneWert().flatMap(wert -> Ergebnis.ohneWert());
	}

	@Test(expected = LeeresErgebnisHatKeinenWertException.class)
	public void beiErfolgOhneWertKeinenWertLiefern()
	{
		Ergebnis.ohneWert().getWert();
	}

	@Test(expected = LeeresErgebnisHatKeinenWertException.class)
	public void beiErfolgOhneWertErfolgsfunktionNichtAusfuehren()
	{
		final Function<Void, String> funktion = wert -> "test";
		Ergebnis.ohneWert()
				.map(funktion);
	}

	@Test(expected = LeeresErgebnisHatKeinenWertException.class)
	public void pruefungAufNichtVorhandenenWertNichtDurchfuehren()
	{
		Ergebnis.ohneWert().stelleSicher(wert -> false, FEHLER);
	}

	@Test
	public void optionalAuspackenKoennen()
	{
		assertThat(
				Ergebnis.mitWert(Optional.of("Wert"))
						.beiVorhandenemWert(String.class, FEHLER),
				hatWert("Wert"));
		assertThat(
				Ergebnis.mitWert(Optional.empty())
						.beiVorhandenemWert(String.class, FEHLER),
				istFehlerMitMeldung(FEHLER));
		assertThat(
				Ergebnis.mitWert("KeinOptional")
						.beiVorhandenemWert(String.class, FEHLER),
				istFehlerMitMeldung(FEHLER));
		assertThat(
				Ergebnis.mitFehler("Fehler")
						.beiVorhandenemWert(String.class, FEHLER),
				istFehlerMitMeldungstext(FEHLER.getText()));
	}
}
