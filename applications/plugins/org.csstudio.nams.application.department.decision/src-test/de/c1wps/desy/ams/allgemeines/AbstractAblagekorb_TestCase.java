package de.c1wps.desy.ams.allgemeines;

import org.csstudio.nams.common.decision.Ablagefaehig;
import org.csstudio.nams.common.decision.Ablagekorb;
import org.junit.Test;

import de.c1wps.desy.ams.AbstractObject_TestCase;

abstract public class AbstractAblagekorb_TestCase<T extends Ablagefaehig, KT> extends AbstractObject_TestCase<KT> {
	protected abstract Ablagekorb<T> gibNeuesExemplar();
	protected abstract T gibNeuesAblagefaehigesExemplar();
	protected abstract boolean pruefeObEnthalten(Ablagekorb<T> korb, T element);
	
	
	@Test(timeout=2000)
	public void testAblegen() throws InterruptedException {
		Ablagekorb<T> eingangskorb = gibNeuesExemplar();
		
		T object1 = gibNeuesAblagefaehigesExemplar();
		T object2 = gibNeuesAblagefaehigesExemplar();

		// Objects ablegen:
		eingangskorb.ablegen(object1);
		eingangskorb.ablegen(object2);
		
		// Objects in richtiger Reihenfolge abholbar?
		assertTrue("Das zu erst hineingelegte ist enthalten", pruefeObEnthalten(eingangskorb, object1));
		assertTrue("Das zu yweit hineingelegte ist enthalten", pruefeObEnthalten(eingangskorb, object2));
	}
}
