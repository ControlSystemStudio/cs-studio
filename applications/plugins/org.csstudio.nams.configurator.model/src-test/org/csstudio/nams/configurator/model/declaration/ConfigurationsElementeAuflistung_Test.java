package org.csstudio.nams.configurator.model.declaration;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.junit.Test;

public class ConfigurationsElementeAuflistung_Test extends TestCase {

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
}
