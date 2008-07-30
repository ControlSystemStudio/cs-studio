package org.csstudio.nams.configurator.model.declaration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.csstudio.nams.common.testutils.AbstractObject_TestCase;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.junit.Test;

public class ConfigurationsElementeAuflistung_Test extends
		AbstractObject_TestCase<ConfigurationsElementeAuflistung<?>> {

	@Test
	public void testListeFuellenUndFiltern() {
		final ConfigurationsElementeAuflistung<AlarmbearbeiterDTO> liste = new ConfigurationsElementeAuflistung<AlarmbearbeiterDTO>();

		final Set<AlarmbearbeiterDTO> testDaten = this.createTestDaten();

		Assert.assertEquals(4, testDaten.size());

		liste.setElements(testDaten);

		Set<AlarmbearbeiterDTO> visibleElements = liste
				.getVisibleElementsInAscendingOrder();

		Assert.assertNotNull(visibleElements);
		Assert.assertEquals(4, visibleElements.size());
		Assert.assertEquals(testDaten, visibleElements);

		liste.setNameFilter("otto");

		visibleElements = liste.getVisibleElementsInAscendingOrder();
		Assert.assertNotNull(visibleElements);
		Assert.assertEquals(2, visibleElements.size());

		AlarmbearbeiterDTO[] visibleElementsAsSortedArray = visibleElements
				.toArray(new AlarmbearbeiterDTO[2]);
		Assert.assertEquals("Hans Otto", visibleElementsAsSortedArray[0]
				.getUserName());
		Assert.assertEquals("Otto Meyer", visibleElementsAsSortedArray[1]
				.getUserName());

		liste.setCategoryFilter(1);

		visibleElements = liste.getVisibleElementsInAscendingOrder();
		Assert.assertNotNull(visibleElements);
		Assert.assertEquals(1, visibleElements.size());

		visibleElementsAsSortedArray = visibleElements
				.toArray(new AlarmbearbeiterDTO[2]);
		Assert.assertEquals("Hans Otto", visibleElementsAsSortedArray[0]
				.getUserName());

		liste.releaseNameFilter();

		visibleElements = liste.getVisibleElementsInAscendingOrder();
		Assert.assertNotNull(visibleElements);
		Assert.assertEquals(2, visibleElements.size());

		visibleElementsAsSortedArray = visibleElements
				.toArray(new AlarmbearbeiterDTO[2]);
		Assert.assertEquals("Hans Otto", visibleElementsAsSortedArray[0]
				.getUserName());
		Assert.assertEquals("Olli Dittrich", visibleElementsAsSortedArray[1]
				.getUserName());

		liste.releaseCategoryFilter();

		visibleElements = liste.getVisibleElementsInAscendingOrder();
		Assert.assertNotNull(visibleElements);
		Assert.assertEquals(4, visibleElements.size());
		Assert.assertEquals(testDaten, visibleElements);
	}

	@Override
	protected ConfigurationsElementeAuflistung<?> getNewInstanceOfClassUnderTest() {
		final ConfigurationsElementeAuflistung<AlarmbearbeiterDTO> liste = new ConfigurationsElementeAuflistung<AlarmbearbeiterDTO>();
		final Set<AlarmbearbeiterDTO> testDaten = this.createTestDaten();
		liste.setElements(testDaten);
		return liste;
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected ConfigurationsElementeAuflistung<?>[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		final ConfigurationsElementeAuflistung<?>[] confAuf = new ConfigurationsElementeAuflistung[3];

		ConfigurationsElementeAuflistung<AlarmbearbeiterDTO> liste = new ConfigurationsElementeAuflistung<AlarmbearbeiterDTO>();
		Set<AlarmbearbeiterDTO> testDaten = this.createTestDaten();
		liste.setElements(testDaten);

		confAuf[0] = liste;

		liste = new ConfigurationsElementeAuflistung<AlarmbearbeiterDTO>();
		testDaten = Collections.emptySet();
		liste.setElements(testDaten);

		confAuf[1] = liste;

		liste = new ConfigurationsElementeAuflistung<AlarmbearbeiterDTO>();
		testDaten = new HashSet<AlarmbearbeiterDTO>();
		testDaten.add(new AlarmbearbeiterDTO(1, 1, "Hans Otto", "", "", "", "",
				"", true));
		testDaten.add(new AlarmbearbeiterDTO(2, 2, "Hans Werner", "", "", "",
				"", "", true));
		liste.setElements(testDaten);

		confAuf[2] = liste;

		return confAuf;
	}

	private Set<AlarmbearbeiterDTO> createTestDaten() {
		final Set<AlarmbearbeiterDTO> result = new HashSet<AlarmbearbeiterDTO>();

		result.add(new AlarmbearbeiterDTO(1, 1, "Hans Otto", "", "", "", "",
				"", true));
		result.add(new AlarmbearbeiterDTO(2, 2, "Hans Werner", "", "", "", "",
				"", true));
		result.add(new AlarmbearbeiterDTO(3, 2, "Otto Meyer", "", "", "", "",
				"", true));
		result.add(new AlarmbearbeiterDTO(4, 1, "Olli Dittrich", "", "", "",
				"", "", true));

		return result;
	}
}
