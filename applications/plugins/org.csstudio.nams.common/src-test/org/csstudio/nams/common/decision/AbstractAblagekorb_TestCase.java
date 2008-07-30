package org.csstudio.nams.common.decision;

import junit.framework.Assert;

import org.csstudio.nams.common.testutils.AbstractObject_TestCase;
import org.junit.Test;

abstract public class AbstractAblagekorb_TestCase<T extends Ablagefaehig, KT>
		extends AbstractObject_TestCase<KT> {
	@Test(timeout = 2000)
	public void testAblegen() throws InterruptedException {
		final Ablagekorb<T> eingangskorb = this.gibNeuesExemplar();

		final T object1 = this.gibNeuesAblagefaehigesExemplar();
		final T object2 = this.gibNeuesAblagefaehigesExemplar();

		// Objects ablegen:
		eingangskorb.ablegen(object1);
		eingangskorb.ablegen(object2);

		// Objects in richtiger Reihenfolge abholbar?
		Assert.assertTrue("Das zu erst hineingelegte ist enthalten", this
				.pruefeObEnthalten(eingangskorb, object1));
		Assert.assertTrue("Das zu yweit hineingelegte ist enthalten", this
				.pruefeObEnthalten(eingangskorb, object2));
	}

	protected abstract T gibNeuesAblagefaehigesExemplar();

	protected abstract Ablagekorb<T> gibNeuesExemplar();

	protected abstract boolean pruefeObEnthalten(Ablagekorb<T> korb, T element);
}
