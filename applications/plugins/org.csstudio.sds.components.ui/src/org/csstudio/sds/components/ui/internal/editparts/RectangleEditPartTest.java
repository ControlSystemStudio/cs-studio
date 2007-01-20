package org.csstudio.sds.components.ui.internal.editparts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.csstudio.sds.components.model.RectangleElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableRectangle;
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
public final class RectangleEditPartTest {

	/**
	 * The instance to test.
	 */
	private RectangleEditPart _editPart;

	/**
	 * Test set up.
	 */
	@Before
	public void setUp() {
		_editPart = new RectangleEditPart();
		_editPart.setModel(new RectangleElement());
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.ui.internal.editparts.RectangleEditPart#doRefreshFigure(java.lang.String, java.lang.Object)}.
	 */
	@Test
	public void testDoRefreshFigure() {
		RefreshableRectangle rectangleFigure = (RefreshableRectangle) _editPart.getFigure();
		
		RGB oldBackgroundColor = rectangleFigure.getBackgroundColor().getRGB();
		RGB newBackgroundColor = new RGB((oldBackgroundColor.red +100)%255, 0, 0);
		_editPart.doRefreshFigure(AbstractElementModel.PROP_BACKGROUND_COLOR, newBackgroundColor);
		assertEquals(newBackgroundColor, rectangleFigure.getBackgroundColor().getRGB());
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.ui.internal.editparts.RectangleEditPart#createFigure()}.
	 */
	@Test
	public void testCreateFigure() {
		IFigure figure = _editPart.createFigure();
		assertTrue(figure instanceof RefreshableRectangle);
	}

}
