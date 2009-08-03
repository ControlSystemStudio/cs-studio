package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.opibuilder.visualparts.BorderFactory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.swt.graphics.Font;

/**
 * A Figure with a bent corner and an embedded TextFlow within a FlowPage that contains
 * text.
 */
public class LabelFigure extends RectangleFigure
{

/** The inner TextFlow **/
private TextFlow textFlow;
private FlowPage flowPage;
private boolean isFill;


/** 
 * Creates a new StickyNoteFigure with a MarginBorder that is the given size and a
 * FlowPage containing a TextFlow with the style WORD_WRAP_SOFT.
 * 
 * @param borderSize the size of the MarginBorder
 */
public LabelFigure(boolean withEditorBorder) {
	if(withEditorBorder)
		setBorder(BorderFactory.createBorder(BorderStyle.BUTTON_PRESSED, 1, null, ""));
	
	setOutline(false);
	flowPage = new FlowPage();
	isFill = true;
	textFlow = new TextFlow();
	textFlow.setLayoutManager(new ParagraphTextLayout(textFlow,
				ParagraphTextLayout.WORD_WRAP_HARD));
	flowPage.add(textFlow);
	setLayoutManager(new StackLayout());
	add(flowPage);
}

	/**
	 * Returns the text inside the TextFlow.
	 * 
	 * @return the text flow inside the text.
	 */
	public String getText() {
		return textFlow.getText();
	}

	/**
	 * Sets the text of the TextFlow to the given value.
	 * 
	 * @param newText the new text value.
	 */
	public void setText(String newText) {
		
		textFlow.setText(newText);
	}

	public Dimension getAutoSizeDimension(){
		return textFlow.getPreferredSize().getCopy().expand(
				getInsets().getWidth(), getInsets().getHeight());
	}
	
	@Override
	public void setFont(Font f) {
		textFlow.setFont(f);
		flowPage.setFont(f);
		super.setFont(f);
		revalidate();			
	}
	@Override
	public void setFill(boolean b) {
		isFill = b;
		super.setFill(b);
			
	}
	public boolean isFill(){
		return isFill;
	}

}
