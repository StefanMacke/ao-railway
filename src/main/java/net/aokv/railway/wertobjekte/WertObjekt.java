package net.aokv.railway.wertobjekte;

import java.io.Serializable;

import com.google.common.base.Objects;

public abstract class WertObjekt<T extends Object> implements Serializable
{
	private static final long serialVersionUID = 1524173187994925966L;

	private final T wert;

	/**
	 * Erzeugt ein neues WertObjekt aus dem übergebenen Wert.
	 *
	 * @param wert Der Wert des WertObjekts.
	 * @throws IllegalArgumentException Falls Wert null ist.
	 */
	protected WertObjekt(final T wert)
	{
		if (wert == null)
		{
			throw new IllegalArgumentException(
					String.format("Wert für %s darf nicht null sein.", getClass().getSimpleName()));
		}
		this.wert = wert;
	}

	/**
	 * Gibt den Wert des WertObjekts zurück.
	 *
	 * @return Der Wert des WertObjekts.
	 */
	public final T getWert()
	{
		return wert;
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(getWert());
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final WertObjekt<T> other = (WertObjekt<T>) obj;
		return Objects.equal(getWert(), other.getWert());
	}

	@Override
	public String toString()
	{
		return String.format("%s (%s)", getClass().getSimpleName(), String.valueOf(wert));
	}
}
