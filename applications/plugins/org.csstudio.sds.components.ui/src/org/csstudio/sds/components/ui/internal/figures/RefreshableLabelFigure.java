package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

/**
 * A label figure.
 * 
 * @author Sven Wende, Alexander Will, Stefan Hofer
 * 
 */
public final class RefreshableLabelFigure extends Label implements
		IRefreshableFigure {
	/**
	 * Default label font.
	 */
	public static final Font FONT = CustomMediaFactory.getInstance().getFont(
			"Arial", 8, SWT.NONE); //$NON-NLS-1$

	/**
	 * A border adapter, which covers all border handlings.
	 */
	private IBorderEquippedWidget _borderAdapter;
	
	/**
	 * Constructor.
	 */
	public RefreshableLabelFigure() {
		setFont(FONT);
	}

	/**
	 * {@inheritDoc}
	 */
	public void randomNoiseRefresh() {
		setText("" + Math.random()); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText() {
		return super.getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setText(final String s) {
		super.setText(s);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(final Class adapter) {
		if (adapter == IBorderEquippedWidget.class) {
			if(_borderAdapter==null) {
				_borderAdapter = new BorderAdapter(this);
			}
			return _borderAdapter;
		}
		return null;
	}
}
