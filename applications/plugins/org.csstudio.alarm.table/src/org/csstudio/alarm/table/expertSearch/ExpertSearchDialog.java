package org.csstudio.alarm.table.expertSearch;

import java.util.Calendar;
import java.util.HashMap;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.internal.localization.Messages;
import org.csstudio.alarm.table.preferences.LogArchiveViewerPreferenceConstants;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.util.time.swt.CalendarWidget;
import org.csstudio.util.time.swt.CalendarWidgetListener;
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
/**
 * 
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 17.07.2007
 */
public class ExpertSearchDialog extends Dialog implements CalendarWidgetListener {

    /** The Shell to Display the Dialog. */
    private Shell _shell;
    /** The start Time for the startWidget (left side). */
	private ITimestamp _start;
    /** The end Time for the endWidget (rigth side). */
	private ITimestamp _end;
    /** A Widget to selct the start time. */
    private CalendarWidget _startWidget;
    /** A Widget to selct the end time. */
    private CalendarWidget _endWidget;
	
	private HashMap<String, String> filterMap;
	private Group down;
	private String filterString;
	private Label info;
	private int windowXSize = 450;
    private int bTop = 0;
    private int bBottom = 0;
    private int bHeignt = 0;
    private int bWidht = 0;
    /** The default filterstring. */
    private String _filter;
    
    /**
     *   The Constructor.
     * @param shell The Shell to Display this Dialog.
     * @param start The default start time or null for now.
     * @param end The default start time or null for now.
     * @param filter The default filtersor null for none.
     */
	public ExpertSearchDialog(final Shell shell, final ITimestamp start, final ITimestamp end, final String filter)
	    {
	        super(shell);
	        this._shell=shell;
	        this._start = start;
	        this._end = end;
            _filter = filter;
	    }

    /** {@inheritDoc} */
    @Override
    protected final void configureShell(final Shell shell){
        super.configureShell(shell);
        this._shell=shell;
        shell.setText(Messages.ExpertSearchDialog_expertButton);
        shell.setSize(windowXSize,445);
    }
    /** {@inheritDoc} */
    @Override
    protected final Control createDialogArea(final Composite parent){
//    	filter = new HashMap<String, String>();
    	filterString = "AND ("; //$NON-NLS-1$
        Composite box = (Composite) super.createDialogArea(parent);
        GridLayout layout = (GridLayout) box.getLayout();
        layout.numColumns = 2;
        GridData gd;

        _startWidget = new CalendarWidget(timeBox(box,Messages.ExpertSearchDialog_startTime),
                                          0,_start.toCalendar());
        _startWidget.addListener(this);
        _endWidget = new CalendarWidget(timeBox(box,Messages.ExpertSearchDialog_endTime)
                                        , 0,_end.toCalendar());
        _endWidget.addListener(this);

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
        String type="";
        String value="";
        String logic="";
        final String key = "like lower('";
        down.setRedraw(false);
        if(_filter!= null){
            int k=0;
            int pointer=0;
            final int offset = key.length();
            while(pointer>=0){
                System.out.println(_filter);
                
                pointer = _filter.indexOf(key,k);
                k=pointer+offset;
                if(pointer>=0){
                    System.out.println("found first befor: " +k);
                    type = _filter.substring(k,_filter.indexOf('\'', k));
                    System.out.println("Type= "+type);
                    pointer = _filter.indexOf(key,k);
                    k=pointer+offset;
                    System.out.println("found first befor: " +k);
                    if(pointer>=0){
                        value = _filter.substring(k,_filter.indexOf('\'', k));
                        System.out.println("Type= "+value);
                        int p1 = _filter.indexOf("AND", k);
                        int p2 = _filter.indexOf("OR", k);
                        if(p1>=0&&(p1<p2||p2<0)){
                            logic = _filter.substring(p1,p1+3);
                            pointer=p1;
                            k=pointer+3;
                        }else if(p2>=0&&(p2<p1||p1<0)){
                            logic = _filter.substring(p2,p2+2);
                            pointer=p2;
                            k=pointer+2;
                        }else{
                            logic=null;
                            pointer=p1;
                        }
                            
                    }
                }
                addNewFilter(down, type, value, logic);
            }
        }else {
            addNewFilter(down, null, null, null);
        }
        System.out.println("Control[] childs size= "+parent.getChildren().length);

        constrainShellSize();
//        _shell.pack();
        down.setRedraw(true);
//        _shell.setSize(windowXSize,_shell.getSize().y);

        return box;
    }
    /** @param parent the Parent Composite. 
     * @param text The Grouptext 
     * @return the Group
     */
    private Group timeBox(final Composite parent, final String text) {
        Group left = new Group(parent, 0);
        left.setText(text);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1,1);
        left.setLayoutData(gd);
        left.setLayout(new FillLayout());
        return left;
    }

    /** {@inheritDoc} */
    @Override
    protected final void okPressed(){
    		Control[] rowsComposite = down.getChildren();
            // Step all rows.
    		for(int i=0;i<rowsComposite.length;i++){
                // All rows a Composite. 
    			if (rowsComposite[i] instanceof Composite){
    				Control[] typeAndValueComposite = ((Composite)rowsComposite[i]).getChildren();
                    // The Composite consists of two parts (Composite)
    				if(typeAndValueComposite.length==2 && typeAndValueComposite[0] instanceof Composite && typeAndValueComposite[1] instanceof Composite){
						Control[] typeComboAndValueText = ((Composite)typeAndValueComposite[0]).getChildren();
                        // First part a Composite with a Combo for the Typ and a Text for the value.
						if(typeComboAndValueText[0] instanceof Combo && typeComboAndValueText[1] instanceof Text){
							filterString 	+= " (lower(mpt.name) like lower('" //$NON-NLS-1$
											+((Combo)typeComboAndValueText[0]).getItem(((Combo)typeComboAndValueText[0]).getSelectionIndex())
											+"')" //$NON-NLS-1$
											+" AND lower(mc.value) like lower('" //$NON-NLS-1$
											+((Text)typeComboAndValueText[1]).getText()+
											"'))"; //$NON-NLS-1$
						}
                        Control[] logic = ((Composite)typeAndValueComposite[1]).getChildren();
                        // Second part a Composite with two Button or a Lable and a Button.
		    			if (logic[0] instanceof Label){
		    				filterString += " "+((Label)logic[0]).getText()+" "; //$NON-NLS-1$ //$NON-NLS-2$

		    			} else if (logic[0] instanceof Button){
		    				filterString += ")"; //$NON-NLS-1$
		    			} else{
                            JmsLogsPlugin.logInfo("\t\tERROR Ungültige Strucktur"); //$NON-NLS-1$
                        }
    				}
    			}
    		}
    		super.okPressed();
    }

    /**
     * Add a new filter-row to the Group.
     * 
     * @param parent The parent Group to add the row. 
     * @param type The selectet Type.
     * @param value The default vaule to the Typ.
     * @param logic The logic is set AND, OR or "" for not set 
     */
	private void addNewFilter(final Group parent, final String type, final String value, final String logic) {
		final Composite c = new Composite(parent, SWT.NONE);
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
        int index =0;
        if(type!=null && title.indexOf(type)>=0){
            index = title.indexOf(type);
        }
        title.select(index);
		Text search = new Text(filter,SWT.SINGLE|SWT.BORDER);
		search.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,false,1,1));
        if(value!=null){
            search.setText(value);
        }
		final Composite comButton = new Composite(c, SWT.NONE);
		comButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,false,1,1));
        // if logic empty no follow roles --> need Button
        if(logic==null||logic.trim().length()==0){
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
    		parent.getParent().pack();
    		and.addSelectionListener(new SelectionListener(){
    
    			public void widgetDefaultSelected(final SelectionEvent e) {}
    
    			public void widgetSelected(final SelectionEvent e) {
                    addRow(Messages.ExpertSearchDialog_Label_And,c,comButton,true);
                    _shell.pack();
                    _shell.setSize(windowXSize,_shell.getSize().y);

    			}
    
    		});
    		or.addSelectionListener(new SelectionListener(){
    
    			public void widgetDefaultSelected(final SelectionEvent e) {}
    
    			public void widgetSelected(final SelectionEvent e) {
                    addRow(Messages.ExpertSearchDialog_Label_Or,c,comButton,true);
                    _shell.pack();
                    _shell.setSize(windowXSize,_shell.getSize().y);

    			}
    
    		});
        }else{ // follow more rule whit logic roles-->  don't need Button. destroy ComButton
            addRow(logic,c,comButton,false);
        }
            
	}
	
    /**
     *  
     * @param text The value text for the filter.
     * @param parent The parent Composit.
     * @param comButton The Composit to contain the Buttons.
     * @param newRow 
     */
    protected final void addRow(final String text, final Composite parent,final Composite comButton, final boolean newRow) {
        down.setRedraw(false);
        comButton.dispose();
        final Composite c = new Composite(parent,SWT.NONE);
        c.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,1,1));
        GridLayout glRight = new GridLayout(2,true);
        glRight.marginBottom=bBottom;
        glRight.marginTop=bTop;
        glRight.marginHeight=bHeignt;
        glRight.marginWidth=bWidht;
        c.setLayout(glRight);
        Label logic = new Label(c, SWT.CENTER);
        logic.setText(text);
        logic.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,1));
        Button delButton = new Button(c,SWT.PUSH);
        delButton.setText(Messages.ExpertSearchDialog_Button_Del);
        delButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,1));
        delButton.addSelectionListener(new SelectionListener(){
            
            public void widgetDefaultSelected(final SelectionEvent e) {}

            public void widgetSelected(final SelectionEvent e) {
                down.setRedraw(false);
                parent.dispose();
                _shell.pack();
                down.setRedraw(true);
                _shell.setSize(windowXSize,_shell.getSize().y);
                System.out.println("Control[] childs size= "+parent.getChildren().length);
            }
            
        });
        if(newRow){
            addNewFilter(down,null,null,null);
        }
        down.setRedraw(true);
    }

    /** @return the filter as an Hashmap. */
    public final HashMap<String, String> getFilterMap(){
		return filterMap;
	}
    
    /** @return the filarter as String. */
	public final String getFilterString(){
		return filterString;
	}
    /** @return the start time */
    public final ITimestamp getStart(){
        return _start;
    }

    /** @return the end time */
    public final ITimestamp getEnd(){
        return _end;
    }

    /** {@Inherited}. */
    public final void updatedCalendar(final CalendarWidget source, final Calendar stamp)
    {
        if (source == _startWidget){
            _start = TimestampFactory.fromCalendar(stamp);
        } else{
            _end = TimestampFactory.fromCalendar(stamp);
        }
        if (_start.isGreaterOrEqual(_end)){
            info.setText(Messages.ExpertSearchDialog_startEndMessage);
        }else{
            info.setText(""); //$NON-NLS-1$
        }
    }

}
