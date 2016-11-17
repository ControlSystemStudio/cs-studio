package org.csstudio.alarm.beast.history.views;

import static org.csstudio.alarm.beast.history.views.AlarmHistoryQueryParameters.AlarmHistoryQueryBuilder.buildQuery;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
/**
 * @author shroffk
 *
 */
public class AlarmHistoryQueryParametersDialog extends Dialog {

    protected Shell dialogShell;

    private AlarmHistoryQueryParameters query;

    private final String title;

    private AlarmHistoryQueryParametersWidget pvs;

    /**
     * Create a string list selection dialog.
     *
     * @param parent
     * @param possibleValues -  a List of Values
     * @param selectedValues - a List of selected Values
     * @param title
     */
    public AlarmHistoryQueryParametersDialog(Shell parent, AlarmHistoryQueryParameters query, String title) {
        super(parent);
        setShellStyle(SWT.RESIZE | SWT.DIALOG_TRIM);
        this.query = query;
        this.title = title;
    }

    @Override
    public void create() {
        super.create();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        getShell().setText(title);
        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = (GridLayout) container.getLayout();
        gridLayout.marginTop = 2;
        gridLayout.marginLeft = 2;
        gridLayout.verticalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.horizontalSpacing = 0;
        gridLayout.marginHeight = 0;
        pvs = new AlarmHistoryQueryParametersWidget(container, SWT.NONE);
        pvs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        pvs.setBounds(10,100,150,50);

        initialize();

        return container;
    }

    private void initialize(){
        pvs.setPVs(query.getPvs());
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "Apply", true);
        createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false);
    }

    public AlarmHistoryQueryParameters getAlarmHistoryQueryParameters() {
        return buildQuery().forPVs(pvs.getPVs()).build();
    }

}
