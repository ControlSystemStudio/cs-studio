package org.csstudio.sds.ui.behaviors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.csstudio.sds.eventhandling.AbstractBehavior;
import org.csstudio.sds.internal.eventhandling.BehaviorDescriptor;
import org.csstudio.sds.internal.eventhandling.BehaviorService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class BehaviourServiceTest {

	private BehaviorService _service;

	@Before
	public void setUp() throws Exception {
		_service = new BehaviorService();
	}

	@Test
	@Ignore("dead code after refectoring?")
	public void testGetBehaviour() {
		AbstractBehavior behaviour = _service.getBehavior("Behaviour1", "ellipse");
		assertNotNull(behaviour);
		assertEquals("ellipse", behaviour.getWidgetTypeId());
		assertEquals("Behaviour1", behaviour.getBehaviorId());

		behaviour = _service.getBehavior("Behaviour2", "rectangle");
		assertNotNull(behaviour);
		assertEquals("rectangle", behaviour.getWidgetTypeId());
		assertEquals("Behaviour2", behaviour.getBehaviorId());

		behaviour = _service.getBehavior("Behaviour3", "ellipse");
		assertNull(behaviour);
	}

	@Test
	@Ignore("dead code after refectoring?")
	public void testGetDefaultBehaviour() {
		List<BehaviorDescriptor> behaviours = _service.getBehaviors("ellipse");
		assertNotNull(behaviours);
		assertEquals(3, behaviours.size());
		for (final BehaviorDescriptor current : behaviours) {
			final String typeId = current.getWidgetTypeId();
			assertTrue(typeId.equals("ellipse") || typeId.equals("All"));
		}

		behaviours = _service.getBehaviors("rectangle");
		assertNotNull(behaviours);
		assertEquals(3, behaviours.size());
		for (final BehaviorDescriptor current : behaviours) {
			final String typeId = current.getWidgetTypeId();
			assertTrue(typeId.equals("rectangle") || typeId.equals("All"));
		}

		behaviours = _service.getBehaviors("bargraph");
		assertNotNull(behaviours);
		assertEquals(2, behaviours.size());
		for (final BehaviorDescriptor current : behaviours) {
			final String typeId = current.getWidgetTypeId();
			assertTrue(typeId.equals("bargraph") || typeId.equals("All"));
		}

		behaviours = _service.getBehaviors("image");
		assertNotNull(behaviours);
		assertEquals(1, behaviours.size());
		assertEquals("All", behaviours.get(0).getWidgetTypeId());
		//assertEquals(BehaviorService.DEFAULT_BEHAVIOR_ID, behaviours.get(0).getBehaviorId());
	}

//	@Test
//	public void testGetBehaviours() {
//		final AbstractBehavior defaultBehaviour = _service.getDefaultBehaviour();
//		assertNotNull(defaultBehaviour);
//		assertEquals("All", defaultBehaviour.getWidgetTypeId());
//		assertEquals(BehaviorService.DEFAULT_BEHAVIOR_ID, defaultBehaviour.getBehaviorId());
//	}
//
//	@Test(expected=IllegalArgumentException.class)
//	public void testLookupWithSameId() {
//		new BehaviorService() {
//			protected void doLookup() {
//				AbstractBehavior behaviour = new DefaultBehavior();
//				behaviour.setExtensionData("Behaviour1", "rectangle", "RecTest1");
//				addBehaviour(behaviour);
//
//				behaviour = new DefaultBehavior();
//				behaviour.setExtensionData("Behaviour1", "rectangle", "RecTest1");
//				addBehaviour(behaviour);
//			};
//		};
//	}
//
//	@Test(expected=AssertionError.class)
//	public void testLookUpWithoutDefault() {
//		new BehaviorService() {
//			protected void doLookup() {
//				AbstractBehavior behaviour = new DefaultBehavior();
//				behaviour.setExtensionData("Behaviour1", "rectangle", "RecTest1");
//				addBehaviour(behaviour);
//
//				behaviour = new DefaultBehavior();
//				behaviour.setExtensionData("Behaviour2", "rectangle", "RecTest2");
//				addBehaviour(behaviour);
//			};
//		};
//	}

}
