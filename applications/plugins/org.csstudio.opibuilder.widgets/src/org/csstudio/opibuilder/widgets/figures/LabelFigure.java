package org.csstudio.opibuilder.widgets.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.swt.graphics.Font;

/**
 * A Figure with an embedded TextFlow within a FlowPage that contains
 * text.
 *
 * @author Xihui Chen
 *
 */
public class LabelFigure extends Figure{
	
	public enum H_ALIGN{
		LEFT("Left"),
		CENTER("Center"),
		RIGHT("Right");
		String descripion;
		H_ALIGN(String description){
			this.descripion = description;
		}
		public static String[] stringValues(){
			String[] result = new String[values().length];
			int i=0;
			for(H_ALIGN h : values()){
				result[i++] = h.toString();
			}
			return result;
		}
		
		@Override
		public String toString() {
			return descripion;
		}
	}
	
	public enum V_ALIGN{
		TOP("Top"),
		MIDDLE("Middle"),
		BOTTOM("Bottom");
		String descripion;
		V_ALIGN(String description){
			this.descripion = description;
		}
		public static String[] stringValues(){
			String[] result = new String[values().length];
			int i=0;
			for(V_ALIGN h : values()){
				result[i++] = h.toString();
			}
			return result;
		}
		
		@Override
		public String toString() {
			return descripion;
		}
	}

	
	/** The inner TextFlow **/
	private TextFlow textFlow;
	private FlowPage flowPage;
	
	private V_ALIGN v_alignment = V_ALIGN.TOP;
	private H_ALIGN h_alignment = H_ALIGN.LEFT;

	/** 
	 * Creates a new StickyNoteFigure with a MarginBorder that is the given size and a
	 * FlowPage containing a TextFlow with the style WORD_WRAP_SOFT.
	 * 
	 * @param borderSize the size of the MarginBorder
	 */
	public LabelFigure() {
		
		//setLayoutManager(new StackLayout());
		//add(scrollPane);
		flowPage = new FlowPage();
		textFlow = new TextFlow(){
			@Override
			public void setFont(Font f) {
				super.setFont(f);
				revalidateBidi(this);
				repaint();
			}
		};
		textFlow.setLayoutManager(new ParagraphTextLayout(textFlow,
				ParagraphTextLayout.WORD_WRAP_SOFT));
		flowPage.add(textFlow);
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
		revalidate();

	}

	

	@Override
	public void setOpaque(boolean opaque) {		
		textFlow.setOpaque(opaque);
		super.setOpaque(opaque);
	}
	
	
	@Override
	public void setFont(Font f) {
		flowPage.setFont(f);
		textFlow.setFont(f);
		super.setFont(f);
		revalidate();			
	}
	

	

	
	public void setV_alignment(V_ALIGN vAlignment) {
		v_alignment = vAlignment;
		revalidate();
	}
	
	public void setH_alignment(H_ALIGN hAlignment) {
		h_alignment = hAlignment;
		revalidate();
	}
	
	
	
	@Override
	protected void layout() {
		Rectangle clientArea = getClientArea();
		Dimension preferedSize = flowPage.getPreferredSize();
			int x=clientArea.x;
			if(clientArea.width > preferedSize.width){
				
				switch (h_alignment) {
				case CENTER:
					x = clientArea.x + (clientArea.width - preferedSize.width)/2;
					break;
				case RIGHT:
					x = clientArea.x + clientArea.width - preferedSize.width;
					break;
				case LEFT:
				default:
					x=clientArea.x;
					break;
				}
			}else{
				preferedSize = flowPage.getPreferredSize(clientArea.width, -1);
			}
			int y=clientArea.y;
			if(clientArea.height > preferedSize.height){
				switch (v_alignment) {
				case MIDDLE:
					y = clientArea.y + (clientArea.height - preferedSize.height)/2;
					break;
				case BOTTOM:
					y = clientArea.y + clientArea.height - preferedSize.height;
					break;
				case TOP:
				default:
					y=clientArea.y;
					break;
				}
			}
			
			flowPage.setBounds(new Rectangle(x, y, 
					clientArea.width - (x - clientArea.x), clientArea.height - (y - clientArea.y)));
	}
	
	
	public Dimension getAutoSizeDimension(){
		return flowPage.getPreferredSize().getCopy().expand(
				getInsets().getWidth(), getInsets().getHeight());
	}

}
