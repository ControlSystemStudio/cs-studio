package org.csstudio.sds.components.ui.internal.editparts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.csstudio.sds.components.model.MeterElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableMeterFigure;
import org.csstudio.sds.model.AbstractElementModel;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.RGB;
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
	 * Test method for {@link org.csstudio.sds.components.ui.internal.editparts.MeterEditPart#doRefreshFigure(java.lang.String, java.lang.Object)}.
	 */
	@Test
	public void testDoRefreshFigure() {
		RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) _editPart.getFigure();
		final double lowerBorderValue = meterFigure.getInterval1LowerBorder();
		final double lowerBorderNewValue = lowerBorderValue+1;
		
		_editPart.doRefreshFigure(MeterElement.PROP_INTERVAL1_LOWER_BORDER, lowerBorderNewValue);
		assertEquals(lowerBorderNewValue, meterFigure.getInterval1LowerBorder());
		
		RGB oldBackgroundColor = meterFigure.getBackgroundColor().getRGB();
		RGB newBackgroundColor = new RGB((oldBackgroundColor.red +100)%255, 0, 0);
		_editPart.doRefreshFigure(AbstractElementModel.PROP_COLOR_BACKGROUND, newBackgroundColor);
		assertEquals(newBackgroundColor, meterFigure.getBackgroundColor().getRGB());
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.ui.internal.editparts.MeterEditPart#createFigure()}.
	 */
	@Test
	public void testCreateFigure() {
		IFigure figure = _editPart.createFigure();
		assertTrue(figure instanceof RefreshableMeterFigure);
	}

}
