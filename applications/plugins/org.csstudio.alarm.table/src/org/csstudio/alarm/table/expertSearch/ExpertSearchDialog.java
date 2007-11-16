package org.csstudio.alarm.table.expertSearch;

import java.util.Calendar;
import java.util.HashMap;

import org.csstudio.alarm.dbaccess.ArchiveDBAccess;
import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.internal.localization.Messages;
import org.csstudio.alarm.table.preferences.LogArchiveViewerPreferenceConstants;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.util.time.AbsoluteTimeParser;
import org.csstudio.util.time.RelativeTime;
import org.csstudio.util.time.StartEndTimeParser;
import org.csstudio.util.time.swt.CalendarWidget;
import org.csstudio.util.time.swt.CalendarWidgetListener;
import org.csstudio.util.time.swt.RelativeTimeWidget;
import org.csstudio.util.time.swt.RelativeTimeWidgetListener;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ComboViewer;
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
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
/**
 * 
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 17.07.2007
 */
public class ExpertSearchDialog extends Dialog implements CalendarWidgetListener, RelativeTimeWidgetListener {
    /**
     * 
     * @author hrickens
     * @author $Author$
     * @version $Revision$
     * @since 28.08.2007
     */
    private class MsgType {
        /** The ID of the MSG Type. */
        private int _id;
        /** The name of the Msg Type.*/
        private String _type;
        /**
         * 
         * @param id set the Id of the MSg Type.
         * @param type set the name of the MSg Type.
         */
        public MsgType(final int id, final String type){
            _id = id;
            _type = type;
        }
        /** @return the ID of the MSG Type. */
        public int getId() {
            return _id;
        }
        /** @param id set the ID of the MSG Type. */
        public void setId(final int id) {
            this._id = id;
        }
        /** @return the Name of the MSG Type. */
        public String getType() {
            return _type;
        }
        
        /** @param type set the name of the MSG Type. */
        public void setType(final String type) {
            this._type = type;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString(){
            return _type;
        }
    }
    /** The Shell to Display the Dialog. */
    private Shell _shell;
    /** The start Time for the startWidget (left side). */
	private ITimestamp _start;
    /** The end Time for the endWidget (rigth side). */
	private ITimestamp _end;
    /** A Widget to selct the start time. */
    private CalendarWidget _fromAbsWidget;
    /** A Widget to selct the end time. */
    private CalendarWidget _toAbsWidget;
	
	private HashMap<String, String> _filterMap;
	private Group down;
	private String filterString;
	private Label info;
    /** The widht of the Dialog. */
	private final int _windowXSize = 650;
	/** margin Top. */
    private final int _mTop = 0;
    /** margin Bottom. */
    private final int _mBottom = 0;
    /** margin Top. */
    private final int _mHeignt = 0;
    /** margin Widht. */
    private final int _mWidht = 0;
    /** The default filterstring. */
    private String _filter;
    private RelativeTimeWidget _fromRelWidget;
    private RelativeTimeWidget _toRelWidget;
    private Text _fromText;
    private Text _toText;
    private String _startSpecification;
    private String _endSpecification;
    private StartEndTimeParser _startEnd;
    private MsgType[] _msgTypes;
    
    /**
     *   The Constructor.
     * @param shell The Shell to Display this Dialog.
     * @param start The default start time or null for now.
     * @param end The default start time or null for now.
     * @param filter The default filtersor null for none.
     */
	public ExpertSearchDialog(final Shell shell, final ITimestamp start, final ITimestamp end, final String filter){
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
        shell.setSize(_windowXSize,445);
    }
    /** {@inheritDoc} */
    @Override
    protected final Control createDialogArea(final Composite parent){
         String[][] answer = ArchiveDBAccess.getMsgTypes();
        _msgTypes = new MsgType[answer.length];
        for (int i = 0; i < answer.length; i++) {
            _msgTypes[i]=new MsgType(Integer.parseInt(answer[i][0]),answer[i][1]);
        }
        
//    	filter = new HashMap<String, String>();
    	filterString = "AND ("; //$NON-NLS-1$
        Composite box = (Composite) super.createDialogArea(parent);
        GridLayout layout = (GridLayout) box.getLayout();
        layout.numColumns = 4;
        GridData gd;

        Composite[] c2 = makeTimeSelect(box,Messages.ExpertSearchDialog_startTime,
                org.csstudio.util.time.swt.Messages.StartEnd_AbsStart,
                org.csstudio.util.time.swt.Messages.StartEnd_AbsStart_TT,
                org.csstudio.util.time.swt.Messages.StartEnd_RelStart,
                org.csstudio.util.time.swt.Messages.StartEnd_RelStart_TT);
        _fromAbsWidget=(CalendarWidget) c2[0];
        _fromRelWidget=(RelativeTimeWidget) c2[1];
        c2 = makeTimeSelect(box,Messages.ExpertSearchDialog_startTime,
                org.csstudio.util.time.swt.Messages.StartEnd_AbsEnd,
                org.csstudio.util.time.swt.Messages.StartEnd_AbsEnd_TT,
                org.csstudio.util.time.swt.Messages.StartEnd_RelEnd,
                org.csstudio.util.time.swt.Messages.StartEnd_RelEnd_TT);
        _toAbsWidget=(CalendarWidget) c2[0];
        _toRelWidget=(RelativeTimeWidget) c2[1];

        // New Row
        Label l = new Label(box, SWT.NULL);
        l.setText(org.csstudio.util.time.swt.Messages.StartEnd_StartTime);
        gd = new GridData();
        l.setLayoutData(gd);
        
        _fromText = new Text(box, SWT.LEFT);
        _fromText.setToolTipText(org.csstudio.util.time.swt.Messages.StartEnd_StartTime_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        _fromText.setLayoutData(gd);

        l = new Label(box, SWT.NULL);
        l.setText(org.csstudio.util.time.swt.Messages.StartEnd_EndTime);
        gd = new GridData();
        l.setLayoutData(gd);

        _toText = new Text(box, SWT.LEFT);
        _toText.setToolTipText(org.csstudio.util.time.swt.Messages.StartEnd_EndTime_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        _toText.setLayoutData(gd);
// __________________________________________________-
        info = new Label(box, SWT.NULL);
        info.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        info.setLayoutData(gd);

        down = new Group(box, 0);
        down.setText(Messages.ExpertSearchDialog_search);
        gd = new GridData(SWT.FILL, SWT.FILL, true, false, 4,1);
        down.setLayoutData(gd);
        down.setLayout(new FillLayout(SWT.VERTICAL));
        String type=""; //$NON-NLS-1$
        String value=""; //$NON-NLS-1$
        String logic=""; //$NON-NLS-1$
        final String key = "like lower('"; //$NON-NLS-1$
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
                        int p1 = _filter.indexOf("AND", k); //$NON-NLS-1$
                        int p2 = _filter.indexOf("OR", k); //$NON-NLS-1$
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
    /**
     * @param box the parent Composite
     * @param groupName The Text for the Group
     * @param absStart the Text for the tab from abs 
     * @param absStartTT the Tooltip text for the tab from abs 
     * @param relStart the Text for the tab from rel
     * @param relStartTT the Tooltip text for the tab from rel
     * @return CalendarWidget absWidget and RelativeTimeWidget relWidget 
     */
    private Composite [] makeTimeSelect(final Composite box, final String groupName, final String absStart, final String absStartTT, final String relStart, final String relStartTT) {
        TabFolder leftTab = new TabFolder(timeBox(box,groupName)
                , SWT.BORDER);
        TabItem tab = new TabItem(leftTab, 0);
        tab.setText(absStart);
        tab.setToolTipText(absStartTT);
        CalendarWidget absWidget = new CalendarWidget(leftTab,
        0,_start.toCalendar());
        absWidget.addListener(this);
        tab.setControl(absWidget);
        
        tab = new TabItem(leftTab, 0);
        tab.setText(relStart);
        tab.setToolTipText(relStart);
        RelativeTimeWidget relWidget = new RelativeTimeWidget(leftTab, 0);
        relWidget.addListener(this);
        tab.setControl(relWidget);
        return new Composite[] {absWidget,relWidget};
    }

    /** @param parent the Parent Composite. 
     * @param text The Grouptext 
     * @return the Group
     */
    private Group timeBox(final Composite parent, final String text) {
        Group left = new Group(parent, 0);
        left.setText(text);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false, 2,1);
        left.setLayoutData(gd);
        left.setLayout(new FillLayout());
        return left;
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
		glMain.marginBottom=_mBottom;
		glMain.marginTop=_mTop;
		glMain.marginHeight=_mHeignt;
		c.setLayout(glMain);
		Composite filter = new Composite(c,SWT.NONE);
		filter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,false,1,1));
		GridLayout glLeft = new GridLayout(2,false);
		glLeft.marginBottom=_mBottom;
		glLeft.marginTop=_mTop;
		glLeft.marginHeight=_mHeignt;
		filter.setLayout(glLeft);
		ComboViewer title = new ComboViewer(filter,SWT.SINGLE|SWT.READ_ONLY);
		String[] test = JmsLogsPlugin.getDefault().getPluginPreferences().getString(LogArchiveViewerPreferenceConstants.P_STRINGArch).split(";"); //$NON-NLS-1$
		title.add(_msgTypes);
		int index=0;
		if(type!=null){
		    index = title.getCombo().indexOf(type);
		    if(index<0){
	            index=0;
	        }
		}
        title.getCombo().select(index);
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
    		glRight.marginBottom=_mBottom;
    		glRight.marginTop=_mTop;
    		glRight.marginHeight=_mHeignt;
    		glRight.marginWidth=_mWidht;
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
                    _shell.setSize(_windowXSize,_shell.getSize().y);

    			}
    
    		});
    		or.addSelectionListener(new SelectionListener(){
    
    			public void widgetDefaultSelected(final SelectionEvent e) {}
    
    			public void widgetSelected(final SelectionEvent e) {
                    addRow(Messages.ExpertSearchDialog_Label_Or,c,comButton,true);
                    _shell.pack();
                    _shell.setSize(_windowXSize,_shell.getSize().y);

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
     * @param newRow if true add a new row.
     */
    private void addRow(final String text, final Composite parent,final Composite comButton, final boolean newRow) {
        down.setRedraw(false);
        comButton.dispose();
        final Composite c = new Composite(parent,SWT.NONE);
        c.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,1,1));
        GridLayout glRight = new GridLayout(2,true);
        glRight.marginBottom=_mBottom;
        glRight.marginTop=_mTop;
        glRight.marginHeight=_mHeignt;
        glRight.marginWidth=_mWidht;
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
                _shell.setSize(_windowXSize,_shell.getSize().y);
                System.out.println("Control[] childs size= "+parent.getChildren().length);
            }
            
        });
        if(newRow){
            addNewFilter(down,null,null,null);
        }
        down.setRedraw(true);
    }

    /** {@inheritDoc} */
    @Override
    protected final void okPressed(){
        
            _startSpecification = _fromText.getText();
            _endSpecification = _toText.getText();
            // If the specifications don't parse, don't allow 'OK'
            try{
                _startEnd = new StartEndTimeParser(_startSpecification, _endSpecification);
                if (_startEnd.getStart().compareTo(_startEnd.getEnd()) >= 0){
                    info.setText(org.csstudio.util.time.swt.Messages.StartEnd_StartExceedsEnd);
                    return;
                }
                _start = TimestampFactory.fromCalendar(_startEnd.getStart());
                _end = TimestampFactory.fromCalendar(_startEnd.getEnd());
                System.out.println("set time");

            } catch (Exception ex){System.out.println("throw ex");}

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
                            filterString    += " (lower(mpt.NAME) like lower('" //$NON-NLS-1$
                                            +((Combo)typeComboAndValueText[0]).getItem(((Combo)typeComboAndValueText[0]).getSelectionIndex())
                                            +"')" //$NON-NLS-1$
                                            +" AND lower(mc.VALUE) like lower('" //$NON-NLS-1$
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
    
    /** @return the filter as an Hashmap. */
    public final HashMap<String, String> getFilterMap(){
		return _filterMap;
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

    /** {@inheritDoc}*/
    public final void updatedCalendar(final CalendarWidget source, final Calendar stamp){

        if (source == _fromAbsWidget){
            _fromText.setText(AbsoluteTimeParser.format(stamp));
//            _start = TimestampFactory.fromCalendar(stamp);
        } else{
            _toText.setText(AbsoluteTimeParser.format(stamp));
//            _end = TimestampFactory.fromCalendar(stamp);
        }
        if (_start.isGreaterOrEqual(_end)){
            info.setText(Messages.ExpertSearchDialog_startEndMessage);
        }else{
            info.setText(""); //$NON-NLS-1$
        }
    }

    /** {@inheritDoc} */
    public final void updatedTime(final RelativeTimeWidget source, final RelativeTime time) {
        if (source == _fromRelWidget){
            _fromText.setText(time.toString());
        } else{
            _toText.setText(time.toString());
        }
    }

}
