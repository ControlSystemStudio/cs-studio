package org.csstudio.sds.components.ui.internal.editparts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.csstudio.sds.components.model.LabelElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableLabelFigure;
import org.eclipse.draw2d.IFigure;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Stefan Hofer
 * 
 * @version $Revision$
 *
 */
public final class LabelEditPartTest {

	/**
	 * The instance to test.
	 */
	private LabelEditPart _editPart;

	/**
	 * Test set up.
	 */
	@Before
	public void setUp() {
		_editPart = new LabelEditPart();
		_editPart.setModel(new LabelElement());
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.ui.internal.editparts.LabelEditPart#doRefreshFigure(java.lang.String, java.lang.Object)}.
	 */
	@Test
	public void testDoRefreshFigure() {
		RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) _editPart.getFigure();
		
		final String oldText = labelFigure.getText();
		final String newText = oldText+oldText;
		
		_editPart.doRefreshFigure(LabelElement.PROP_LABEL, newText);
		assertEquals(newText, labelFigure.getText());
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.ui.internal.editparts.LabelEditPart#createFigure()}.
	 */
	@Test
	public void testCreateFigure() {
		IFigure figure = _editPart.createFigure();
		assertTrue(figure instanceof RefreshableLabelFigure);
	}

}
