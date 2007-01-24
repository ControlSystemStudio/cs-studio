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
	 * {@link org.csstudio.sds.components.ui.internal.editparts.MeterEditPart#doRefreshFigure(java.lang.String, java.lang.Object)}.
	 */
	@Test
	public void testDoRefreshFigure() {
		RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) _editPart
				.getFigure();
		final double lowerBorderValue = meterFigure.getInterval1LowerBorder();
		final double lowerBorderNewValue = lowerBorderValue + 1;

		_editPart.doRefreshFigure(MeterElement.PROP_INTERVAL1_LOWER_BORDER,
				lowerBorderNewValue, _editPart.getFigure());
		assertEquals(lowerBorderNewValue, meterFigure.getInterval1LowerBorder());

		

		double oldValue = meterFigure.getValue();
		double newValue = (oldValue + 10) % 100;

		_editPart.doRefreshFigure(MeterElement.PROP_VALUE, newValue, _editPart.getFigure());
		assertEquals(newValue, meterFigure.getValue());
		
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
