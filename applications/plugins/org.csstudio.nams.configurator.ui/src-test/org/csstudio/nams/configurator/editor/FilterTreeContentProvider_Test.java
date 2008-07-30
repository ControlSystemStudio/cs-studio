package org.csstudio.nams.configurator.editor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
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

	@Override
	@Before
	public void setUp() throws Exception {
		this.stringVergleichAusserhalb = new FilterbedingungBean();
		this.stringVergleichAusserhalb.setName("Hallo");
		this.stringVergleichAusserhalb
				.setFilterSpecificBean(new StringFilterConditionBean(
						MessageKeyEnum.TEXT,
						StringRegelOperator.OPERATOR_TEXT_EQUAL, "Hallo Welt!"));

		this.stringVergleichImOderLinkeSeite = new FilterbedingungBean();
		this.stringVergleichImOderLinkeSeite.setName("Links");
		this.stringVergleichImOderLinkeSeite
				.setFilterSpecificBean(new StringFilterConditionBean(
						MessageKeyEnum.TEXT,
						StringRegelOperator.OPERATOR_TEXT_EQUAL,
						"Hallo Oder 1!"));

		this.stringVergleichImOderRechteSeite1 = new FilterbedingungBean();
		this.stringVergleichImOderRechteSeite1.setName("Rechts1");
		this.stringVergleichImOderRechteSeite1
				.setFilterSpecificBean(new StringFilterConditionBean(
						MessageKeyEnum.TEXT,
						StringRegelOperator.OPERATOR_TEXT_EQUAL,
						"Hallo Oder 2!"));

		this.stringVergleichImOderRechteSeite2 = new FilterbedingungBean();
		this.stringVergleichImOderRechteSeite2.setName("Rechts2");
		this.stringVergleichImOderRechteSeite2
				.setFilterSpecificBean(new StringFilterConditionBean(
						MessageKeyEnum.TEXT,
						StringRegelOperator.OPERATOR_TEXT_EQUAL,
						"Hallo Oder 2!"));

		this.and = new JunctorConditionForFilterTreeBean();
		this.and.setJunctorConditionType(JunctorConditionType.AND);

		this.and.addOperand(this.stringVergleichImOderRechteSeite1);
		this.and.addOperand(this.stringVergleichImOderRechteSeite2);

		this.notImOderRechteSeite = new NotConditionForFilterTreeBean();
		this.notImOderRechteSeite.setFilterbedingungBean(this.and);

		this.oder = new JunctorConditionForFilterTreeBean();
		this.oder.setJunctorConditionType(JunctorConditionType.OR);

		this.oder.addOperand(this.stringVergleichImOderLinkeSeite);
		this.oder.addOperand(this.notImOderRechteSeite);

		this.rootListAsEditorInputAND = new LinkedList<FilterbedingungBean>();
		this.rootListAsEditorInputAND.add(this.stringVergleichAusserhalb);
		this.rootListAsEditorInputAND.add(this.oder);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		this.rootListAsEditorInputAND = null;
		this.stringVergleichImOderLinkeSeite = null;
		this.stringVergleichImOderRechteSeite1 = null;
		this.stringVergleichAusserhalb = null;
		this.oder = null;
	}

	@Test
	public void testMethodsThatShouldDoNothing() {
		final FilterTreeContentProvider provider = new FilterTreeContentProvider();
		provider.inputChanged(null, null, null);
		provider.dispose();
	}

	@Test
	public void testNormalUse() {
		final FilterTreeContentProvider provider = new FilterTreeContentProvider();

		// Get Parent liefert zu begin für alles null
		Assert.assertNull(provider.getParent(new Object()));
		Assert.assertNull(provider.getParent(this.stringVergleichAusserhalb));
		Assert.assertNull(provider.getParent(this.oder));
		Assert.assertNull(provider
				.getParent(this.stringVergleichImOderLinkeSeite));
		Assert.assertNull(provider
				.getParent(this.stringVergleichImOderRechteSeite1));

		// Keine root-nodes vorhanden
		Assert.assertNotNull(provider.getContentsOfRootANDCondition());
		Assert.assertEquals(0, provider.getContentsOfRootANDCondition().size());

		// Der TreeInput geht hier erstmal rein...
		final Object[] thingsToBeShown = provider
				.getElements(this.rootListAsEditorInputAND);

		// 2 root nodes
		List<FilterbedingungBean> contentsOfRootANDCondition = provider
				.getContentsOfRootANDCondition();
		Assert.assertNotNull(provider.getContentsOfRootANDCondition());
		Assert.assertEquals(2, contentsOfRootANDCondition.size());
		Assert.assertSame(this.oder, contentsOfRootANDCondition.get(0));
		Assert.assertSame(this.stringVergleichAusserhalb,
				contentsOfRootANDCondition.get(1));

		// Testen das die folgenden Aufrufe nix zerstören
		this.testMethodsThatShouldDoNothing();

		// Only root node!
		Assert.assertEquals(1, thingsToBeShown.length);
		Assert
				.assertTrue(thingsToBeShown[0] instanceof JunctorConditionForFilterTreeBean);
		final JunctorConditionForFilterTreeBean treeRootBean = ((JunctorConditionForFilterTreeBean) thingsToBeShown[0]);
		Assert.assertEquals(JunctorConditionType.AND, treeRootBean
				.getJunctorConditionType());

		final Set<FilterbedingungBean> operandsOfRootNode = treeRootBean
				.getOperands();
		Assert.assertEquals(2, operandsOfRootNode.size());
		Assert.assertTrue(operandsOfRootNode
				.containsAll(this.rootListAsEditorInputAND));
		final Object orFromProvider = operandsOfRootNode.toArray()[0];
		Assert.assertTrue("OR",
				orFromProvider instanceof JunctorConditionForFilterTreeBean);
		final Object outherStringCompareFromProvider = operandsOfRootNode
				.toArray()[1];
		Assert.assertTrue("StringVergleich Ausserhalb",
				outherStringCompareFromProvider instanceof FilterbedingungBean);

		// hasChildren
		Assert.assertTrue(provider.hasChildren(treeRootBean));
		Assert.assertTrue(provider.hasChildren(orFromProvider));
		Assert.assertFalse(provider
				.hasChildren(outherStringCompareFromProvider));
		Assert.assertTrue(provider.hasChildren(this.notImOderRechteSeite));

		// children
		final Object[] childrenOfRootNode = provider.getChildren(treeRootBean);
		Assert.assertEquals(2, childrenOfRootNode.length);
		Assert.assertTrue(Arrays.asList(childrenOfRootNode).containsAll(
				this.rootListAsEditorInputAND));

		final Object[] childrenOfOr = provider.getChildren(orFromProvider);
		Assert.assertEquals(2, childrenOfOr.length);
		final List<Object> childrenOfOrAsList = Arrays.asList(childrenOfOr);
		Assert.assertTrue(childrenOfOrAsList
				.contains(this.stringVergleichImOderLinkeSeite));
		Assert.assertTrue(childrenOfOrAsList
				.contains(this.notImOderRechteSeite));

		final Object[] childrenOfNot = provider
				.getChildren(this.notImOderRechteSeite);
		Assert.assertNotNull(childrenOfNot);
		Assert.assertEquals(2, childrenOfNot.length);
		Assert.assertEquals(this.stringVergleichImOderRechteSeite1,
				childrenOfNot[0]);
		Assert.assertEquals(this.stringVergleichImOderRechteSeite2,
				childrenOfNot[1]);

		// Parent
		Assert.assertNull(provider.getParent(treeRootBean));
		Assert.assertEquals(treeRootBean, provider
				.getParent(this.stringVergleichAusserhalb));
		Assert.assertEquals(treeRootBean, provider.getParent(this.oder));
		Assert.assertEquals(this.oder, provider
				.getParent(this.stringVergleichImOderLinkeSeite));
		Assert.assertEquals(this.oder, provider
				.getParent(this.notImOderRechteSeite));

		// auch zum schluss 2 root nodes
		contentsOfRootANDCondition = provider.getContentsOfRootANDCondition();
		Assert.assertNotNull(provider.getContentsOfRootANDCondition());
		Assert.assertEquals(2, contentsOfRootANDCondition.size());
		Assert.assertSame(this.oder, contentsOfRootANDCondition.get(0));
		Assert.assertSame(this.stringVergleichAusserhalb,
				contentsOfRootANDCondition.get(1));

		// Robust?? Ungueltige Zugriffe
		final Object somethingInvalid = new Object();
		Assert.assertFalse(provider.hasChildren(somethingInvalid));
		final Object[] childrenOfInvalidNode = provider
				.getChildren(somethingInvalid);
		Assert.assertNotNull(childrenOfInvalidNode);
		Assert.assertEquals(0, childrenOfInvalidNode.length);
		Assert.assertNull(provider.getParent(somethingInvalid));

	}
}
