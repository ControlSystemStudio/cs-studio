package org.csstudio.opibuilder.widgets.figures;

import org.csstudio.opibuilder.visualparts.BorderFactory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ScrollPane;
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
public class LabelFigure extends Figure{

	private Label label;
	private ScrollPane scrollPane;
	private boolean opaque;

	/** 
	 * Creates a new StickyNoteFigure with a MarginBorder that is the given size and a
	 * FlowPage containing a TextFlow with the style WORD_WRAP_SOFT.
	 * 
	 * @param borderSize the size of the MarginBorder
	 */
	public LabelFigure() {
		scrollPane = new ScrollPane(){
			@Override
			public boolean isOpaque() {
				return opaque;
			}
		};
		scrollPane.setOpaque(false);
		
		setLayoutManager(new StackLayout());
		add(scrollPane);
		label = new Label();
		label.setOpaque(false);
		scrollPane.setContents(label);		
	}

	/**
	 * Returns the text inside the TextFlow.
	 * 
	 * @return the text flow inside the text.
	 */
	public String getText() {
		return label.getText();
	}

	/**
	 * Sets the text of the TextFlow to the given value.
	 * 
	 * @param newText the new text value.
	 */
	public void setText(String newText) {		
		label.setText(newText);
		label.setSize(label.getPreferredSize());
		label.revalidate();
	}

	public Label getLabel() {
		return label;
	}
	

	@Override
	public void setOpaque(boolean opaque) {		
		this.opaque = opaque;
		label.setOpaque(opaque);
		super.setOpaque(opaque);
	}
	
	
	@Override
	public void setFont(Font f) {
		label.setFont(f);
		super.setFont(f);
		revalidate();			
	}
	
	public void setScrollbarVisible(boolean visible){
		scrollPane.setScrollBarVisibility(visible ? ScrollPane.AUTOMATIC : ScrollPane.NEVER);
	}
	

}
