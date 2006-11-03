package org.csstudio.alarm.table.expertSearch;

import java.util.StringTokenizer;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.preferences.JmsLogPreferenceConstants;
import org.csstudio.alarm.table.preferences.LogArchiveViewerPreferenceConstants;
import org.csstudio.alarm.table.timeSelection.TimestampWidget;
import org.csstudio.data.Timestamp;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ExpertSearchDialog extends Dialog{

	private Timestamp start;
	private Timestamp end;
	private Shell shell;


	public ExpertSearchDialog(Shell shell, Timestamp start, Timestamp end)
	    {
	        super(shell);
	        this.shell=shell;
	        this.start = start;
	        this.end = end;
	    }

    @Override
    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);
        this.shell=shell;
        shell.setText("Expert Search");
        shell.setSize(400,235);
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite box = (Composite) super.createDialogArea(parent);
        GridLayout layout = (GridLayout) box.getLayout();
        layout.numColumns = 2;
        GridData gd;

        Group left = new Group(box, 0);
        left.setText("Start Time");
        gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1,1);
        left.setLayoutData(gd);
        left.setLayout(new FillLayout());
//        start_widget = new TimestampWidget(left, 0, start);
//        start_widget.addListener(this);

        Group right = new Group(box, 0);
        right.setText("End Time");
//        gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1,1);
        right.setLayoutData(gd);
        right.setLayout(new FillLayout());

//        end_widget = new TimestampWidget(right, 0, end);
//        end_widget.addListener(this);

//        info = new Label(box, SWT.NULL);
//        info.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
//        gd = new GridData();
//        gd.horizontalSpan = layout.numColumns;
//        gd.grabExcessHorizontalSpace = true;
//        gd.horizontalAlignment = SWT.FILL;
//        info.setLayoutData(gd);

        Group down = new Group(box, 0);
        down.setText("Search");
        gd = new GridData(SWT.FILL, SWT.FILL, true, false, 2,1);
        down.setLayoutData(gd);
        down.setLayout(new FillLayout(SWT.VERTICAL));

        addNewFilter(down);

        return box;
    }

	private void addNewFilter(final Group down) {
		final Composite c = new Composite(down, SWT.BORDER);
		c.setLayout(new GridLayout(2,false));
		Composite filter = new Composite(c,SWT.NONE);
		filter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,false,1,1));
		filter.setLayout(new GridLayout(2,false));
		Combo title = new Combo(filter,SWT.SINGLE|SWT.READ_ONLY);
		String[] test = JmsLogsPlugin.getDefault().getPluginPreferences().getString(LogArchiveViewerPreferenceConstants.P_STRINGArch).split(";");
		title.setItems(test);
		title.select(0);
		Text search = new Text(filter,SWT.SINGLE|SWT.BORDER);
		search.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,false,1,1));
		final Composite comButton = new Composite(c, SWT.NONE);
		comButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,false,1,1));
		comButton.setLayout(new GridLayout(2,true));
		Button and = new Button(comButton,SWT.UP);
		and.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,1));
		and.setText("AND");
		Button or = new Button(comButton,SWT.DOWN);
		or.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,1));
		or.setText("OR");
		down.getParent().pack();
		and.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

			public void widgetSelected(SelectionEvent e) {
				Text logic = new Text(c, SWT.READ_ONLY|SWT.SINGLE);
//				logic.setLayoutData(new GridData(SWT.CENTER,SWT.FILL,true,false,2,1));
				logic.setText("AND");
				addNewFilter(down);
				comButton.dispose();
				shell.pack();
				shell.setSize(400,shell.getSize().y);

			}

		});
		or.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

			public void widgetSelected(SelectionEvent e) {
				Text logic = new Text(c, SWT.READ_ONLY|SWT.SINGLE);
//				logic.setLayoutData(new GridData(SWT.CENTER,SWT.FILL,true,false,2,1));
				logic.setText("OR");
				addNewFilter(down);
				comButton.dispose();
				shell.pack();
				shell.setSize(400,shell.getSize().y);

			}

		});

	}

}
