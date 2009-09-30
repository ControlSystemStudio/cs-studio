package org.csstudio.opibuilder.widgets.figures;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * An action button figure.
 * 
 * @author Xihui Chen
 */
public final class ActionButtonFigure extends Button implements
		IAdaptable {
	
	/**
	 * Default label font.
	 */
	public static final Font FONT = CustomMediaFactory.getInstance().getFont(
			CustomMediaFactory.FONT_ARIAL); //$NON-NLS-1$
	
	/**
	 * The Label for the Button.
	 */
	private Label label;
	
	/**
	 * An Array, which contains the PositionConstants for Center, Top, Bottom, Left, Right.
	 */
	private final int[] alignments = new int[] {PositionConstants.CENTER, PositionConstants.TOP, PositionConstants.BOTTOM, PositionConstants.LEFT, PositionConstants.RIGHT};
	
	
	private ExecutionMode executionMode;

	/**
	 * Constructor.
	 */
	public ActionButtonFigure(ExecutionMode executionMode) {
		label = new Label("");
		this.executionMode = executionMode;
		setContents(label);
		setFont(FONT);
	}
	
	/**
	 * Sets the text for the Button.
	 * @param s
	 * 			The text for the button
	 */
	public void setText(final String s) {
		label.setText(s);
	}
	
	public void setImage(final Image img){
		label.setIcon(img);
	}
	
	@Override
	public void setEnabled(boolean value) {		
		super.setEnabled(value);
		if(executionMode == ExecutionMode.EDIT_MODE)
			label.setEnabled(true);
	}
	
	/**
	 * Sets the alignment of the buttons text.
	 * The parameter is a {@link PositionConstants} (LEFT, RIGHT, TOP, CENTER, BOTTOM)
	 * @param alignment
	 * 			The alignment for the text 
	 */
	public void setTextAlignment(final int alignment) {
		if (alignment>=0 && alignment<alignments.length) {
			if (alignments[alignment]==PositionConstants.LEFT || alignments[alignment]==PositionConstants.RIGHT) {
				label.setTextPlacement(PositionConstants.NORTH);
			} else {
				label.setTextPlacement(PositionConstants.EAST);
			}
			label.setTextAlignment(alignments[alignment]);
		}
	}
	
	@Override
	public boolean isOpaque() {
		return true;
	}
	
	@Override
	public void setFont(Font f) {
		super.setFont(f);
		label.revalidate();
	}
	
	/**
	 * Set the style of the Button.
	 * @param style false = Push, true=Toggle.
	 */
	public void setStyle(final boolean style){
	    if(style){
	        setStyle(Button.STYLE_TOGGLE);
	    }else{
	        setStyle(Button.STYLE_BUTTON);
	    }
	        
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Class adapter) {
		return null;
	}
	
}
