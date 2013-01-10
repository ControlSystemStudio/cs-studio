package org.csstudio.ui.util.widgets;


import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.ResourceManager;

/**
 * An error bar to display an exception message and the details in a pop-up.
 * 
 * @author carcassi
 *
 */
public class ErrorBar extends Composite {

	private Label errorImage;
	private CLabel errorLabel;
	private Exception exception;
	private int marginTop = 0;
	private int marginBottom = 0;
	private int marginLeft = 0;
	private int marginRight = 0;
	
	/**
	 * Create a new error bar.
	 * 
	 * @param parent widget parent
	 * @param style style of the widget
	 */
	public ErrorBar(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginLeft = 1;
		setLayout(gridLayout);
		
		Cursor handCursor = new Cursor(getDisplay(), SWT.CURSOR_HAND);
		MouseListener listener = new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == 1) {
					ExceptionDetailsErrorDialog.openError(getShell(), "Query failed...", exception);
				}
			}
		};
		
		errorImage = new Label(this, SWT.NONE);
		GridData gd_errorImage = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		errorImage.setLayoutData(gd_errorImage);
		errorImage.setImage(ResourceManager.getPluginImage("org.csstudio.ui.util", "icons/error-16.png"));
		errorImage.setCursor(handCursor);
		errorImage.addMouseListener(listener);
		
		errorLabel = new CLabel(this, SWT.NONE);
		GridData gd_errorLabel = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_errorLabel.widthHint = 221;
		errorLabel.setLayoutData(gd_errorLabel);
		errorLabel.setCursor(handCursor);
		errorLabel.addMouseListener(listener);
		setException(null);
	}
	
	public void setException(Exception ex) {
		if (!isDisposed()) {
			this.exception = ex;
			if (ex == null) {
				errorLabel.setToolTipText("");
				errorLabel.setText("");
				GridData gd = (GridData) errorLabel.getLayoutData();
				gd.exclude = true;
				errorLabel.setLayoutData(gd);
				gd = (GridData) errorImage.getLayoutData();
				gd.exclude = true;
				errorImage.setLayoutData(gd);
				((GridLayout) getLayout()).marginBottom = 0;
				((GridLayout) getLayout()).marginTop = 0;
				((GridLayout) getLayout()).marginLeft = 0;
				((GridLayout) getLayout()).marginRight = 0;
			} else {
				ex.printStackTrace();
				errorLabel.setToolTipText(ex.getMessage());
				errorLabel.setText(ex.getMessage());
				GridData gd = (GridData) errorLabel.getLayoutData();
				gd.exclude = false;
				errorLabel.setLayoutData(gd);
				gd = (GridData) errorImage.getLayoutData();
				gd.exclude = false;
				errorImage.setLayoutData(gd);
				((GridLayout) getLayout()).marginBottom = marginBottom;
				((GridLayout) getLayout()).marginTop = marginTop;
				// We always want at least one pixel on the left
				((GridLayout) getLayout()).marginLeft = marginLeft + 1;
				((GridLayout) getLayout()).marginRight = marginRight;
			}
			getParent().layout();
		}
	}

	/**
	 * The margin on the top of the error bar, if displayed.
	 * 
	 * @return the top margin
	 */
	public int getMarginTop() {
		return marginTop;
	}

	/**
	 * Changes the margin on the top of the error bar, if displayed.
	 * 
	 * @param marginTop the new margin
	 */
	public void setMarginTop(int marginTop) {
		this.marginTop = marginTop;
		getParent().layout();
	}

	/**
	 * The margin on the bottom of the error bar, if displayed.
	 * 
	 * @return the top margin
	 */
	public int getMarginBottom() {
		return marginBottom;
	}

	/**
	 * Changes the margin on the bottom of the error bar, if displayed.
	 * 
	 * @param marginBottom the new margin
	 */
	public void setMarginBottom(int marginBottom) {
		this.marginBottom = marginBottom;
		getParent().layout();
	}

	/**
	 * The margin on the left of the error bar, if displayed.
	 * 
	 * @return the left margin
	 */
	public int getMarginLeft() {
		return marginLeft;
	}

	/**
	 * Changes the margin on the left of the error bar, if displayed.
	 * 
	 * @param marginLeft the new margin
	 */
	public void setMarginLeft(int marginLeft) {
		this.marginLeft = marginLeft;
		getParent().layout();
	}

	/**
	 * The margin on the right of the error bar, if displayed.
	 * 
	 * @return the right margin
	 */
	public int getMarginRight() {
		return marginRight;
	}

	/**
	 * Changes the margin on the right of the error bar, if displayed.
	 * 
	 * @param marginRight the new margin
	 */
	public void setMarginRight(int marginRight) {
		this.marginRight = marginRight;
		getParent().layout();
	}
	

}
