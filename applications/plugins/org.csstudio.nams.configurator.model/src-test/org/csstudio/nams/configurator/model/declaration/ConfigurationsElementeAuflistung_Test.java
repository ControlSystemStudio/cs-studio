package org.csstudio.nams.configurator.model.declaration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.csstudio.nams.common.testutils.AbstractObject_TestCase;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.junit.Test;

public class ConfigurationsElementeAuflistung_Test extends AbstractObject_TestCase<ConfigurationsElementeAuflistung<?>> {

	@Test
	public void testListeFuellenUndFiltern() {
		ConfigurationsElementeAuflistung<AlarmbearbeiterDTO> liste = new ConfigurationsElementeAuflistung<AlarmbearbeiterDTO>();
		
		Set<AlarmbearbeiterDTO> testDaten = createTestDaten();
		
		assertEquals(4, testDaten.size());
		
		liste.setElements(testDaten);
		
		Set<AlarmbearbeiterDTO> visibleElements = liste.getVisibleElementsInAscendingOrder();
		
		assertNotNull(visibleElements);
		assertEquals(4, visibleElements.size());
		assertEquals(testDaten, visibleElements);
		
		liste.setNameFilter("otto");
		
		visibleElements = liste.getVisibleElementsInAscendingOrder();
		assertNotNull(visibleElements);
		assertEquals(2, visibleElements.size());

		AlarmbearbeiterDTO[] visibleElementsAsSortedArray = visibleElements.toArray(new AlarmbearbeiterDTO[2]);
		assertEquals("Hans Otto", visibleElementsAsSortedArray[0].getUserName());
		assertEquals("Otto Meyer", visibleElementsAsSortedArray[1].getUserName());
		
		liste.setCategoryFilter(1);
		
		visibleElements = liste.getVisibleElementsInAscendingOrder();
		assertNotNull(visibleElements);
		assertEquals(1, visibleElements.size());
		
		visibleElementsAsSortedArray = visibleElements.toArray(new AlarmbearbeiterDTO[2]);
		assertEquals("Hans Otto", visibleElementsAsSortedArray[0].getUserName());
		
		liste.releaseNameFilter();
		
		visibleElements = liste.getVisibleElementsInAscendingOrder();
		assertNotNull(visibleElements);
		assertEquals(2, visibleElements.size());

		visibleElementsAsSortedArray = visibleElements.toArray(new AlarmbearbeiterDTO[2]);
		assertEquals("Hans Otto", visibleElementsAsSortedArray[0].getUserName());
		assertEquals("Olli Dittrich", visibleElementsAsSortedArray[1].getUserName());
		
		liste.releaseCategoryFilter();
		
		visibleElements = liste.getVisibleElementsInAscendingOrder();
		assertNotNull(visibleElements);
		assertEquals(4, visibleElements.size());
		assertEquals(testDaten, visibleElements);
	}
	
	private Set<AlarmbearbeiterDTO> createTestDaten() {
		Set<AlarmbearbeiterDTO> result = new HashSet<AlarmbearbeiterDTO>();
		
		result.add(new AlarmbearbeiterDTO(1,1,"Hans Otto", "", "", "", "", "", true));
		result.add(new AlarmbearbeiterDTO(2,2,"Hans Werner", "", "", "", "", "", true));
		result.add(new AlarmbearbeiterDTO(3,2,"Otto Meyer", "", "", "", "", "", true));
		result.add(new AlarmbearbeiterDTO(4,1,"Olli Dittrich", "", "", "", "", "", true));
		
		return result;
	}

	@Override
	protected ConfigurationsElementeAuflistung<?> getNewInstanceOfClassUnderTest() {
		ConfigurationsElementeAuflistung<AlarmbearbeiterDTO> liste = new ConfigurationsElementeAuflistung<AlarmbearbeiterDTO>();
		Set<AlarmbearbeiterDTO> testDaten = createTestDaten();
		liste.setElements(testDaten);
		return liste;
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected ConfigurationsElementeAuflistung<?>[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		ConfigurationsElementeAuflistung<?> [] confAuf = new ConfigurationsElementeAuflistung[3];
		
		ConfigurationsElementeAuflistung<AlarmbearbeiterDTO> liste = new ConfigurationsElementeAuflistung<AlarmbearbeiterDTO>();
		Set<AlarmbearbeiterDTO> testDaten = createTestDaten();
		liste.setElements(testDaten);
		
		confAuf[0] = liste;
		
		liste = new ConfigurationsElementeAuflistung<AlarmbearbeiterDTO>();
		testDaten = Collections.emptySet();
		liste.setElements(testDaten);
		
		confAuf[1] = liste;
		
		liste = new ConfigurationsElementeAuflistung<AlarmbearbeiterDTO>();
		testDaten = new HashSet<AlarmbearbeiterDTO>();
		testDaten.add(new AlarmbearbeiterDTO(1,1,"Hans Otto", "", "", "", "", "", true));
		testDaten.add(new AlarmbearbeiterDTO(2,2,"Hans Werner", "", "", "", "", "", true));
		liste.setElements(testDaten);
		
		confAuf[2] = liste;
		
		return confAuf;
	}
}
