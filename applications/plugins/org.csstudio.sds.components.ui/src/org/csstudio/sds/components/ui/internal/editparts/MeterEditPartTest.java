package org.csstudio.sds.components.ui.internal.editparts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.csstudio.sds.components.model.MeterElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableMeterFigure;
import org.eclipse.draw2d.IFigure;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Stefan Hofer
 * 
 * @version $Revision$
 * 
 */
public final class MeterEditPartTest {

	/**
	 * The instance to test.
	 */
	private MeterEditPart _editPart;

	/**
	 * Test set up.
	 */
	@Before
	public void setUp() {
		_editPart = new MeterEditPart();
		_editPart.setModel(new MeterElement());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.components.ui.internal.editparts.MeterEditPart#createFigure()}.
	 */
	@Test
	public void testCreateFigure() {
		IFigure figure = _editPart.doCreateFigure();
		assertTrue(figure instanceof RefreshableMeterFigure);
	}

}
