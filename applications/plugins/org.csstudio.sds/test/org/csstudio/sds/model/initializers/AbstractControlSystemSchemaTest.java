package org.csstudio.sds.model.initializers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.eclipse.swt.graphics.RGB;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Stefan Hofer
 * @version $Revision$
 * 
 */
public final class AbstractControlSystemSchemaTest {

	/**
	 * A dummy initialization schema for unit tests.
	 * 
	 * @author Stefan Hofer
	 * @version $Revision$
	 * 
	 */
	static final class DummySchema extends AbstractControlSystemSchema {

		/**
		 * Property name.
		 */
		static final String STRING = "string"; //$NON-NLS-1$

		/**
		 * Property name.
		 */
		static final String BOOLEAN = "boolean"; //$NON-NLS-1$

		/**
		 * Property name.
		 */
		static final String INT = "int"; //$NON-NLS-1$

		/**
		 * Property name.
		 */
		static final String DOUBLE = "double"; //$NON-NLS-1$

		/**
		 * Property name.
		 */
		static final String OBJECT = "object"; //$NON-NLS-1$

		/**
		 * Property name.
		 */
		static final String COLOR = "color"; //$NON-NLS-1$

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void initializeProperties() {
			addGlobalProperty(STRING, "test"); //$NON-NLS-1$
			addGlobalProperty(BOOLEAN, true);
			addGlobalProperty(INT, 17);
			addGlobalProperty(DOUBLE, 17.1);
			addGlobalProperty(OBJECT, new HashSet<String>());
			addGlobalProperty(COLOR, new RGB(10, 20, 30));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void initializeWidget(final AbstractWidgetModel widgetModel) {
			
		}

		@Override
		protected void initializeAliases(final AbstractWidgetModel widgetModel) {
			// TODO Auto-generated method stub
			
		}
	}

	/**
	 * The instance under test.
	 */
	private AbstractControlSystemSchema _schema;

	/**
	 * Test set up.
	 * 
	 */
	@Before
	public void setUp() {
		_schema = new DummySchema();
	}

	/**
	 * Tests with invalid property names.
	 */
	@Test
	public void testInvalidProperty() {
		assertNotNull(_schema.getObjectProperty(null));
		assertNotNull(_schema.getObjectProperty("doesNotExist")); //$NON-NLS-1$
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.model.initializers.AbstractControlSystemSchema#getBooleanProperty(java.lang.String)}.
	 */
	@Test
	public void testGetBooleanProperty() {
		assertTrue(_schema.getBooleanProperty(DummySchema.BOOLEAN));
		assertFalse(_schema.getBooleanProperty(DummySchema.STRING));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.model.initializers.AbstractControlSystemSchema#getColorProperty(java.lang.String)}.
	 */
	@Test
	public void testGetColorProperty() {
		assertNotNull(_schema.getColorProperty(DummySchema.COLOR));
		assertNotNull(_schema.getColorProperty(DummySchema.STRING));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.model.initializers.AbstractControlSystemSchema#getDoubleProperty(java.lang.String)}.
	 */
	@Test
	public void testGetDoubleProperty() {
		assertNotNull(_schema.getDoubleProperty(DummySchema.DOUBLE));
		assertNotNull(_schema.getDoubleProperty(DummySchema.STRING));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.model.initializers.AbstractControlSystemSchema#getIntegerProperty(java.lang.String)}.
	 */
	@Test
	public void testGetIntegerProperty() {
		assertNotNull(_schema.getIntegerProperty(DummySchema.DOUBLE));
		assertNotNull(_schema.getIntegerProperty(DummySchema.STRING));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.model.initializers.AbstractControlSystemSchema#getObjectProperty(java.lang.String)}.
	 */
	@Test
	public void testGetObjectProperty() {
		assertNotNull(_schema.getObjectProperty(DummySchema.OBJECT));
		assertNotNull(_schema.getObjectProperty(DummySchema.STRING));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.model.initializers.AbstractControlSystemSchema#getPropertyNames()}.
	 */
	@Test
	public void testGetPropertyNames() {
		assertEquals(6, _schema.getPropertyNames().size());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.model.initializers.AbstractControlSystemSchema#getStringProperty(java.lang.String)}.
	 */
	@Test
	public void testGetStringProperty() {
		assertNotNull(_schema.getStringProperty(DummySchema.STRING));
		assertNotNull(_schema.getStringProperty(DummySchema.DOUBLE));
	}

}
