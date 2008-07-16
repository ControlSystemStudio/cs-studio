package org.csstudio.nams.configurator.editor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.beans.filters.JunctorConditionForFilterTreeBean;
import org.csstudio.nams.configurator.beans.filters.NotConditionForFilterTreeBean;
import org.csstudio.nams.configurator.beans.filters.StringFilterConditionBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FilterTreeContentProvider_Test extends TestCase {

	private List<FilterbedingungBean> rootListAsEditorInputAND;
	private FilterbedingungBean stringVergleichImOderRechteSeite1;
	private FilterbedingungBean stringVergleichImOderLinkeSeite;
	private FilterbedingungBean stringVergleichAusserhalb;
	private JunctorConditionForFilterTreeBean oder;
	private NotConditionForFilterTreeBean notImOderRechteSeite;
	private FilterbedingungBean stringVergleichImOderRechteSeite2;
	private JunctorConditionForFilterTreeBean and;

	@Before
	public void setUp() throws Exception {
		stringVergleichAusserhalb = new FilterbedingungBean();
		stringVergleichAusserhalb.setName("Hallo");
		stringVergleichAusserhalb.setFilterSpecificBean(new StringFilterConditionBean(MessageKeyEnum.TEXT, StringRegelOperator.OPERATOR_TEXT_EQUAL, "Hallo Welt!"));
		
		stringVergleichImOderLinkeSeite = new FilterbedingungBean();
		stringVergleichImOderLinkeSeite.setName("Links");
		stringVergleichImOderLinkeSeite.setFilterSpecificBean(new StringFilterConditionBean(MessageKeyEnum.TEXT, StringRegelOperator.OPERATOR_TEXT_EQUAL, "Hallo Oder 1!"));
		
		stringVergleichImOderRechteSeite1 = new FilterbedingungBean();
		stringVergleichImOderRechteSeite1.setName("Rechts1");
		stringVergleichImOderRechteSeite1.setFilterSpecificBean(new StringFilterConditionBean(MessageKeyEnum.TEXT, StringRegelOperator.OPERATOR_TEXT_EQUAL, "Hallo Oder 2!"));
		
		stringVergleichImOderRechteSeite2 = new FilterbedingungBean();
		stringVergleichImOderRechteSeite2.setName("Rechts2");
		stringVergleichImOderRechteSeite2.setFilterSpecificBean(new StringFilterConditionBean(MessageKeyEnum.TEXT, StringRegelOperator.OPERATOR_TEXT_EQUAL, "Hallo Oder 2!"));
		
		and = new JunctorConditionForFilterTreeBean();
		and.setJunctorConditionType(JunctorConditionType.AND);
		
		and.addOperand(stringVergleichImOderRechteSeite1);
		and.addOperand(stringVergleichImOderRechteSeite2);
		
		notImOderRechteSeite = new NotConditionForFilterTreeBean();
		notImOderRechteSeite.setFilterbedingungBean(and);
		
		oder = new JunctorConditionForFilterTreeBean();
		oder.setJunctorConditionType(JunctorConditionType.OR);
		
		oder.addOperand(stringVergleichImOderLinkeSeite);
		oder.addOperand(notImOderRechteSeite);
		
		rootListAsEditorInputAND = new LinkedList<FilterbedingungBean>();
		rootListAsEditorInputAND.add(stringVergleichAusserhalb);
		rootListAsEditorInputAND.add(oder);
	}

	@After
	public void tearDown() throws Exception {
		rootListAsEditorInputAND = null;
		stringVergleichImOderLinkeSeite = null;
		stringVergleichImOderRechteSeite1 = null;
		stringVergleichAusserhalb = null;
		oder = null;
	}

	@Test
	public void testNormalUse() {
		FilterTreeContentProvider provider = new FilterTreeContentProvider();
		
		// Get Parent liefert zu begin für alles null
		assertNull(provider.getParent(new Object()));
		assertNull(provider.getParent(stringVergleichAusserhalb));
		assertNull(provider.getParent(oder));
		assertNull(provider.getParent(stringVergleichImOderLinkeSeite));
		assertNull(provider.getParent(stringVergleichImOderRechteSeite1));
		
		// Der TreeInput geht hier erstmal rein...
		Object[] thingsToBeShown = provider.getElements(rootListAsEditorInputAND);
		
		// Testen das die folgenden Aufrufe nix zerstören
		testMethodsThatShouldDoNothing();
		
		// Only root node!
		assertEquals(1, thingsToBeShown.length);
		assertTrue( thingsToBeShown[0] instanceof JunctorConditionForFilterTreeBean );
		JunctorConditionForFilterTreeBean treeRootBean = ((JunctorConditionForFilterTreeBean)thingsToBeShown[0]);
		assertEquals(JunctorConditionType.AND, treeRootBean.getJunctorConditionType());

		Set<FilterbedingungBean> operandsOfRootNode = treeRootBean.getOperands();
		assertEquals(2, operandsOfRootNode.size());
		assertTrue(operandsOfRootNode.containsAll(rootListAsEditorInputAND));
		Object orFromProvider = operandsOfRootNode.toArray()[0];
		assertTrue("OR", orFromProvider instanceof JunctorConditionForFilterTreeBean );
		Object outherStringCompareFromProvider = operandsOfRootNode.toArray()[1];
		assertTrue("StringVergleich Ausserhalb", outherStringCompareFromProvider instanceof FilterbedingungBean );
		
		// hasChildren
		assertTrue(provider.hasChildren(treeRootBean));
		assertTrue(provider.hasChildren(orFromProvider));
		assertFalse(provider.hasChildren(outherStringCompareFromProvider));
		assertTrue(provider.hasChildren(notImOderRechteSeite));
		
		// children
		Object[] childrenOfRootNode = provider.getChildren(treeRootBean);
		assertEquals(2, childrenOfRootNode.length);
		assertTrue(Arrays.asList(childrenOfRootNode).containsAll(rootListAsEditorInputAND));
		
		Object[] childrenOfOr = provider.getChildren(orFromProvider);
		assertEquals(2, childrenOfOr.length);
		List<Object> childrenOfOrAsList = Arrays.asList(childrenOfOr);
		assertTrue(childrenOfOrAsList.contains(stringVergleichImOderLinkeSeite));
		assertTrue(childrenOfOrAsList.contains(notImOderRechteSeite));
		
		Object[] childrenOfNot = provider.getChildren(notImOderRechteSeite);
		assertNotNull(childrenOfNot);
		assertEquals(2, childrenOfNot.length);
		assertEquals(stringVergleichImOderRechteSeite1, childrenOfNot[0]);
		assertEquals(stringVergleichImOderRechteSeite2, childrenOfNot[1]);
		
		// Parent
		assertNull(provider.getParent(treeRootBean));
		assertEquals(treeRootBean, provider.getParent(stringVergleichAusserhalb));
		assertEquals(treeRootBean, provider.getParent(oder));
		assertEquals(oder, provider.getParent(stringVergleichImOderLinkeSeite));
		assertEquals(oder, provider.getParent(notImOderRechteSeite));
		
		// Robust?? Ungueltige Zugriffe
		Object somethingInvalid = new Object();
		assertFalse(provider.hasChildren(somethingInvalid));
		Object[] childrenOfInvalidNode = provider.getChildren(somethingInvalid);
		assertNotNull(childrenOfInvalidNode);
		assertEquals(0, childrenOfInvalidNode.length);
		assertNull(provider.getParent(somethingInvalid));
		
		
	}

	@Test
	public void testMethodsThatShouldDoNothing() {
		FilterTreeContentProvider provider = new FilterTreeContentProvider();
		provider.inputChanged(null, null, null);
		provider.dispose();
	}
}
