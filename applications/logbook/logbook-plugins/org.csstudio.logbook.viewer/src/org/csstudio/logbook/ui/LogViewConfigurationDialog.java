/**
 *
 */
package org.csstudio.logbook.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.csstudio.ui.util.widgets.ErrorBar;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * @author shroffk
 *
 */
public class LogViewConfigurationDialog extends Dialog {

    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(listener);
    }

    /**
     * @return the expandable
     */
    public boolean isExpandable() {
        return expandable;
    }

    /**
     *
     * @return
     */
    public int getRowSize(){
    return rowSize;
    }

    /**
     * @return the order
     */
    public int getOrder() {
        return order;
    }

    // Model
    private boolean expandable;
    private int order;
    private int rowSize;

    // GUI
    private Text text;
    private Label lblRowSize;
    private ErrorBar errorBar;
    private Combo combo;
    private Button btnConfirm;
    private Label label;

    public LogViewConfigurationDialog(Shell parentShell, boolean expandable, int order, int rowSize) {
    super(parentShell);
    this.expandable = expandable;
    this.order = order;
    this.rowSize = rowSize;
    setBlockOnOpen(false);
    setShellStyle(SWT.RESIZE | SWT.DIALOG_TRIM);
    }

    @Override
    public Control createDialogArea(Composite parent) {
    getShell().setText("Configuration Options");
    Composite container = (Composite) super.createDialogArea(parent);
    container.setLayout(new GridLayout(2, false));

    errorBar = new ErrorBar(container, SWT.NONE);
    errorBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    errorBar.setMarginBottom(5);

    btnConfirm = new Button(container, SWT.CHECK);
    btnConfirm.setText("Expanded");
    btnConfirm.setSelection(expandable);
    btnConfirm.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));

    btnConfirm.addSelectionListener(new SelectionAdapter()
    {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
            expandable = btnConfirm.getSelection();
            text.setEnabled(!expandable);
            lblRowSize.setEnabled(!expandable);
        }
    });

    lblRowSize = new Label(container, SWT.NONE);
    lblRowSize.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
    lblRowSize.setText("Row Size:");
    lblRowSize.setEnabled(!expandable);

    text = new Text(container, SWT.BORDER);
    text.addKeyListener(new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
        try {
            errorBar.setException(null);
            rowSize = Integer.valueOf(text.getText());
        } catch (NumberFormatException e1) {
            errorBar.setException(new Exception("RowSize must be an integer number, \"" +text.getText()+ "\" is not an integer", e1) );
        }
        }
    });
    text.setText(String.valueOf(rowSize));
    text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    text.setEnabled(!expandable);

    // if the order is set to < 0 do not show the configuration option for log Entry order
    // this option should be used by viewer which do not support history

    if (order > 0) {
        label = new Label(container, SWT.NONE);
        label.setText("Edited logEntry order:");

        combo = new Combo(container, SWT.READ_ONLY);
        combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
            1, 1));
        combo.setItems(new String[] { "Oldest", "Newest" });
        combo.select(order == SWT.UP ? 0 : 1);
        combo.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            switch (combo.getText()) {
            case "Oldest":
            order = SWT.UP;
            break;
            case "Newest":
            order = SWT.DOWN;
            break;
            default:
            order = SWT.DOWN;
            break;
            }
        }
        });
    }

    return container;

    }

}
