package org.csstudio.sds.components.ui.internal.editparts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.csstudio.sds.components.model.RectangleElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableRectangleFigure;
import org.eclipse.draw2d.IFigure;
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
	 * Test method for
	 * {@link org.csstudio.sds.components.ui.internal.editparts.RectangleEditPart#createFigure()}.
	 */
	@Test
	public void testCreateFigure() {
		IFigure figure = _editPart.doCreateFigure();
		assertTrue(figure instanceof RefreshableRectangleFigure);
	}

}
