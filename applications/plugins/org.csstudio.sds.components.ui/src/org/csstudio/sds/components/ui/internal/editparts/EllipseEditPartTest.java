package org.csstudio.sds.components.ui.internal.editparts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.csstudio.sds.components.model.EllipseElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableEllipse;
import org.eclipse.draw2d.IFigure;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Stefan Hofer
 * 
 * @version $Revision$
 *
 */
public final class EllipseEditPartTest {

	/**
	 * The instance to test.
	 */
	private EllipseEditPart _editPart;

	/**
	 * Test set up.
	 */
	@Before
	public void setUp() {
		_editPart = new EllipseEditPart();
		_editPart.setModel(new EllipseElement());
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.ui.internal.editparts.EllipseEditPart#doRefreshFigure(java.lang.String, java.lang.Object)}.
	 */
	@Test
	public void testDoRefreshFigure() {
		RefreshableEllipse ellipseFigure = (RefreshableEllipse) _editPart.getFigure();
		
		final double oldFillValue = ellipseFigure.getFill();
		final double newFillValue = oldFillValue+1;
		
		_editPart.doRefreshFigure(EllipseElement.PROP_FILL_PERCENTAGE, newFillValue);
		assertEquals(newFillValue, ellipseFigure.getFill());
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.ui.internal.editparts.EllipseEditPart#createFigure()}.
	 */
	@Test
	public void testCreateFigure() {
		IFigure figure = _editPart.createFigure();
		assertTrue(figure instanceof RefreshableEllipse);
	}

}
