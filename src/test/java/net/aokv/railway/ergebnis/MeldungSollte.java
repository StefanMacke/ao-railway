package net.aokv.railway.ergebnis;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class MeldungSollte
{
	private Meldung sut;

	@Test
	public void sichAlsFehlerAusStringErzeugenLassen()
	{
		sut = Meldung.mitFehler("Dies ist der Fehler");
		assertThat(sut.getCode(), is(not(0)));
		assertThat(sut.getEbene(), is(Meldungsebene.FEHLER));
		assertThat(sut.getIndex(), is(0));
		assertThat(sut.getQuelle(), is("sichAlsFehlerAusStringErzeugenLassen"));
		assertThat(sut.getText(), is("Dies ist der Fehler"));
	}

	@Test
	public void sichMitEinerGleichenMeldungVergleichenLassen()
	{
		assertThat(Meldung.mitFehler("Dies ist der Fehler"), is(Meldung.mitFehler("Dies ist der Fehler")));
		assertThat(
				Meldung.neu()
						.mitCode(1)
						.mitDetails("Details")
						.mitEbene(Meldungsebene.INFO)
						.mitIndex(2)
						.mitQuelle("Quelle")
						.mitText("Text")
						.baue(),
				is(Meldung.neu()
						.mitCode(1)
						.mitDetails("Details")
						.mitEbene(Meldungsebene.INFO)
						.mitIndex(2)
						.mitQuelle("Quelle")
						.mitText("Text")
						.baue()));
	}

	@Test
	public void sichMitMeldungBuilderErzeugenLassen()
	{
		sut = Meldung.neu()
				.mitCode(1)
				.mitEbene(Meldungsebene.FEHLER)
				.mitIndex(2)
				.mitQuelle("Die Quelle")
				.mitText("Der Text")
				.baue();
		assertThat(sut.getCode(), is(1));
		assertThat(sut.getEbene(), is(Meldungsebene.FEHLER));
		assertThat(sut.getIndex(), is(2));
		assertThat(sut.getQuelle(), is("Die Quelle"));
		assertThat(sut.getText(), is("Der Text"));
	}

	@Test
	public void standardwerteVerwendenWennKeineWerteGegebenSind()
	{
		sut = Meldung.neu().baue();
		assertThat(sut.getCode(), is(1));
		assertThat(sut.getEbene(), is(Meldungsebene.FEHLER));
		assertThat(sut.getIndex(), is(0));
		assertThat(sut.getQuelle(), is("standardwerteVerwendenWennKeineWerteGegebenSind"));
		assertThat(sut.getText(), is("Kein Text"));
		assertThat(sut.getDetails(), is("Keine weiteren Informationen"));
	}

	@Test
	public void sichAlsStringAusgebenKoennen()
	{
		final Exception exception = new Exception("Innere Exception");
		sut = Meldung.neu()
				.mitCode(1)
				.mitEbene(Meldungsebene.FEHLER)
				.mitIndex(0)
				.mitQuelle("Test")
				.mitText("Der Fehler")
				.mitDetails(exception).baue();
		assertThat(sut.toString(), containsString("FEHLER (1, Test, 0): \"Der Fehler\""));
		assertThat(sut.toString(), containsString("Innere Exception"));
		assertThat(sut.toString(), containsString("sichAlsStringAusgebenKoennen"));
		System.out.println(sut);
	}

	@Test
	public void sichMitEinemCodeIdentifizierenLassen()
	{
		sut = Meldung.neu().mitCode(1234).baue();
		assertThat(sut.hatCode(1234), is(true));
		assertThat(sut.hatCode(1235), is(false));
	}

}
