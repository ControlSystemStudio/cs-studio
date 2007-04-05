package org.csstudio.alarm.table.expertSearch;

import java.util.HashMap;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.internal.localization.Messages;
import org.csstudio.alarm.table.preferences.LogArchiveViewerPreferenceConstants;
import org.csstudio.alarm.table.timeSelection.TimestampWidget;
import org.csstudio.alarm.table.timeSelection.TimestampWidgetListener;
import org.csstudio.platform.util.ITimestamp;
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

public class ExpertSearchDialog extends Dialog implements TimestampWidgetListener{

	private ITimestamp start;
	private ITimestamp end;
	private Shell shell;
	private HashMap<String, String> filterMap;
	private Group down;
	private String filterString;
	private TimestampWidget start_widget;
	private TimestampWidget end_widget;
	private Label info;
	private int windowXSize = 450;


	public ExpertSearchDialog(Shell shell, ITimestamp start, ITimestamp end)
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
        shell.setText(Messages.ExpertSearchDialog_expertButton);
        shell.setSize(windowXSize,445);
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
//    	filter = new HashMap<String, String>();
    	filterString = "AND ("; //$NON-NLS-1$
        Composite box = (Composite) super.createDialogArea(parent);
        GridLayout layout = (GridLayout) box.getLayout();
        layout.numColumns = 2;
        GridData gd;

        Group left = new Group(box, 0);
        left.setText(Messages.ExpertSearchDialog_startTime);
        gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1,1);
        left.setLayoutData(gd);
        left.setLayout(new FillLayout());
        start_widget = new TimestampWidget(left, 0, start);
        start_widget.addListener(this);

        Group right = new Group(box, 0);
        right.setText(Messages.ExpertSearchDialog_endTime);
//        gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1,1);
        right.setLayoutData(gd);
        right.setLayout(new FillLayout());

        end_widget = new TimestampWidget(right, 0, end);
        end_widget.addListener(this);

        info = new Label(box, SWT.NULL);
        info.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        info.setLayoutData(gd);

        down = new Group(box, 0);
        down.setText(Messages.ExpertSearchDialog_search);
        gd = new GridData(SWT.FILL, SWT.FILL, true, false, 2,1);
        down.setLayoutData(gd);
        down.setLayout(new FillLayout(SWT.VERTICAL));

        addNewFilter(down);
        constrainShellSize();

        return box;
    }

    @Override
    protected void okPressed(){
    		Control[] children = down.getChildren();
    		for(int i=0;i<children.length;i++){
    			if (children[i] instanceof Composite){
    				Control[] c2 = ((Composite)children[i]).getChildren();
    				if(c2.length==2){
    					if (c2[0] instanceof Composite){
    						Control[] c3 = ((Composite)c2[0]).getChildren();
    						if(c3[0] instanceof Combo && c3[1] instanceof Text){
    							filterString 	+= " (lower(mpt.name) like lower('" //$NON-NLS-1$
    											+((Combo)c3[0]).getItem(((Combo)c3[0]).getSelectionIndex())
    											+"')" //$NON-NLS-1$
    											+" AND lower(mc.value) like lower('" //$NON-NLS-1$
    											+((Text)c3[1]).getText()+
    											"'))"; //$NON-NLS-1$
    						}
			    			if (c2[1] instanceof Label){
			    				filterString += " "+((Label)c2[1]).getText()+" "; //$NON-NLS-1$ //$NON-NLS-2$

			    			}
			    			else if (c2[1] instanceof Composite){
			    				filterString += ")"; //$NON-NLS-1$
			    			}
			    			else System.out.println("\t\tERROR Ungültige Strucktur"); //$NON-NLS-1$
						}
    				}
    			}
    		}
    		super.okPressed();
    }


	private void addNewFilter(final Group down) {
		int bTop = 0;
		int bBottom = 0;
		int bHeignt = 0;
		int bWidht = 0;
		final Composite c = new Composite(down, SWT.NONE);
		GridLayout glMain = new GridLayout(2,false);
		glMain.marginBottom=bBottom;
		glMain.marginTop=bTop;
		glMain.marginHeight=bHeignt;
		c.setLayout(glMain);
		Composite filter = new Composite(c,SWT.NONE);
		filter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,false,1,1));
		GridLayout glLeft = new GridLayout(2,false);
		glLeft.marginBottom=bBottom;
		glLeft.marginTop=bTop;
		glLeft.marginHeight=bHeignt;
		filter.setLayout(glLeft);
		Combo title = new Combo(filter,SWT.SINGLE|SWT.READ_ONLY);
		String[] test = JmsLogsPlugin.getDefault().getPluginPreferences().getString(LogArchiveViewerPreferenceConstants.P_STRINGArch).split(";"); //$NON-NLS-1$
		title.setItems(test);
		title.select(0);
		Text search = new Text(filter,SWT.SINGLE|SWT.BORDER);
		search.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,false,1,1));
		final Composite comButton = new Composite(c, SWT.NONE);
		comButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,false,1,1));
		GridLayout glRight = new GridLayout(2,true);
		glRight.marginBottom=bBottom;
		glRight.marginTop=bTop;
		glRight.marginHeight=bHeignt;
		glRight.marginWidth=bWidht;
		comButton.setLayout(glRight);
		Button and = new Button(comButton,SWT.UP);
		and.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,1));
		and.setText(Messages.ExpertSearchDialog_Button_And);
		Button or = new Button(comButton,SWT.DOWN);
		or.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,1));
		or.setText(Messages.ExpertSearchDialog_Button_Or);
		down.getParent().pack();
		and.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

			public void widgetSelected(SelectionEvent e) {
				Label logic = new Label(c, SWT.CENTER);
//				logic.setLayoutData(new GridData(SWT.CENTER,SWT.FILL,true,false,2,1));
				logic.setText(Messages.ExpertSearchDialog_Label_And);
				addNewFilter(down);
				comButton.dispose();
				shell.pack();
				shell.setSize(windowXSize,shell.getSize().y);

			}

		});
		or.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

			public void widgetSelected(SelectionEvent e) {
				Label logic = new Label(c, SWT.CENTER);
//				logic.setLayoutData(new GridData(SWT.CENTER,SWT.FILL,true,false,2,1));
				logic.setText(Messages.ExpertSearchDialog_Label_Or);
				addNewFilter(down);
				comButton.dispose();
				shell.pack();
				shell.setSize(windowXSize,shell.getSize().y);

			}

		});

	}

	public HashMap<String, String> getFilterMap(){
		return filterMap;
	}
	public String getFilterString(){
		return filterString;
	}
    /** @return the start time */
    public ITimestamp getStart()
    {
        return start;
    }

    /** @return the end time */
    public ITimestamp getEnd()
    {
        return end;
    }

	  // TimestampWidgetListener
    public void updatedTimestamp(TimestampWidget source, ITimestamp stamp)
    {
        if (source == start_widget)
            start = stamp;
        else
            end = stamp;

        System.out.println(Messages.ExpertSearchDialog_start + start.format(ITimestamp.FMT_DATE_HH_MM_SS));
        System.out.println(Messages.ExpertSearchDialog_end + end.format(ITimestamp.FMT_DATE_HH_MM_SS));

        if (start.isGreaterOrEqual(end))
            info.setText(Messages.ExpertSearchDialog_startEndMessage);
        else
            info.setText(""); //$NON-NLS-1$
    }
}
