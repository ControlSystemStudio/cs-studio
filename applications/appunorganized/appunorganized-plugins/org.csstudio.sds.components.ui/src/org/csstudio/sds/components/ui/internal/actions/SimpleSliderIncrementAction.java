package org.csstudio.sds.components.ui.internal.actions;

import org.csstudio.sds.components.model.SimpleSliderModel;
import org.csstudio.sds.components.ui.internal.editparts.SimpleSliderEditPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 *
 * @author Kai Meyer
 * @author $Author: jhatje $
 * @version $Revision: 1.5 $
 * @since 15.04.2010
 */
public class SimpleSliderIncrementAction extends Action implements IObjectActionDelegate {

    private SimpleSliderModel _widgetModel;

    /**
     *
     * (@inheritDoc)
     */
    public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
    }

    /**
     *
     * (@inheritDoc)
     */
    public final void run(final IAction action) {
        this.run();
    }

    @Override
    public void run() {
        if (_widgetModel != null) {
            String title = "Change increment";
            String message = "Enter the new increment";
            String initialValue = String.valueOf(_widgetModel.getIncrement());
            InputDialog incDialog = new IncInputDialog(null,
                                                       title,
                                                       message,
                                                       initialValue,
                                                       new IInputValidator() {
                                                           //
                                                           public String isValid(final String newText) {
                                                               try {
                                                                   double d = Double
                                                                           .parseDouble(newText);
                                                                   if (d <= 0) {
                                                                       return "Value has to be greater than 0";
                                                                   }else if (d > 100) {
                                                                       return "Value has to be smaller or equal than 100";
                                                                   }

                                                               } catch (NumberFormatException x) {
                                                                   return "Only numbers are allowed";
                                                               }

                                                               return null;
                                                           }
                                                       });
            int returnCode = incDialog.open();

            if (returnCode == Window.OK) {
                Double increment = Double.valueOf(incDialog.getValue());
                _widgetModel.setPropertyValue(SimpleSliderModel.PROP_INCREMENT, increment);
            }
        }
    }

    /**
     *
     * (@inheritDoc)
     */
    public final void selectionChanged(final IAction action, final ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            Object element = ((IStructuredSelection) selection).getFirstElement();
            if (element instanceof SimpleSliderEditPart) {
                _widgetModel = (SimpleSliderModel) ((SimpleSliderEditPart) element)
                        .getWidgetModel();
            }
        }
    }

    /**
     *
     * A Input Dialog that have four buttons to set most wanted values.
     *
     * @author hrickens
     * @author $Author: jhatje $
     * @version $Revision: 1.5 $
     * @since 15.04.2010
     */
    private final class IncInputDialog extends InputDialog {
        private IncInputDialog(final Shell parentShell,
                               final String dialogTitle,
                               final String dialogMessage,
                               final String initialValue,
                               final IInputValidator validator) {
            super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
        }

        @Override
        protected Control createButtonBar(final Composite parent) {
            Control createButtonBar = super.createButtonBar(parent);
            getShell().pack();
            return createButtonBar;
        }
        @Override
        protected Control createDialogArea(final Composite parent) {
            final Composite createDialogArea = (Composite) super.createDialogArea(parent);
            final Composite chooserComposite = new Composite(createDialogArea, SWT.NONE);
            chooserComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
            chooserComposite.setLayout(new GridLayout(4, true));

            Button button = new Button(chooserComposite, SWT.PUSH);
            button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
            button.setText("0.01");
            button.addSelectionListener(getListener(getText(), "0.01"));

            button = new Button(chooserComposite, SWT.PUSH);
            button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
            button.setText("0.1");
            button.addSelectionListener(getListener(getText(), "0.1"));

            button = new Button(chooserComposite, SWT.PUSH);
            button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
            button.setText("1.0");
            button.addSelectionListener(getListener(getText(), "1.0"));

            button = new Button(chooserComposite, SWT.PUSH);
            button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
            button.setText("10.0");
            button.addSelectionListener(getListener(getText(), "10.0"));
            chooserComposite.pack();
            Point cL = Display.getCurrent().getCursorLocation();
            getShell().setLocation(cL.x,cL.y-100);
            return createDialogArea;
        }

        private SelectionListener getListener(final Text text, final String inc) {
            return new IncSelectionListener(text, inc);
        }
    }

    /**
     *  This listener set the given value, on a selection, to the Text.
     *
     * @author hrickens
     * @author $Author: jhatje $
     * @version $Revision: 1.5 $
     * @since 15.04.2010
     */
    private class IncSelectionListener implements SelectionListener {

        private final Text _text;
        private final String _value;

        public IncSelectionListener(final Text text, final String value) {
            _text = text;
            _value = value;
        }

        public void widgetSelected(final SelectionEvent e) {
            setInc();
        }

        private void setInc() {
            _text.setText(_value);
        }

        public void widgetDefaultSelected(final SelectionEvent e) {
            setInc();
        }

    }

}
