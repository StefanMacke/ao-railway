package net.aokv.railway.ergebnis;

public class FehlerhaftesErgebnisHatKeinenWertException extends RuntimeException
{
	private static final long serialVersionUID = -8674147111084040578L;

	private final Object fehler;

	public FehlerhaftesErgebnisHatKeinenWertException(final Object fehler)
	{
		super();
		this.fehler = fehler;
	}

	public Object getFehler()
	{
		return fehler;
	}

	@Override
	public String toString()
	{
		return "FehlerhaftesErgebnisHatKeinenWertException [fehler=" + getFehler() + "]";
	}
}
