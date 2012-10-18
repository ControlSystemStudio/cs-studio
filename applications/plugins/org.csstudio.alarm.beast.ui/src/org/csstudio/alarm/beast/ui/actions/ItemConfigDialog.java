/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.alarm.beast.client.AADataStructure;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.GDCDataStructure;
import org.csstudio.alarm.beast.ui.Activator;
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.apputil.formula.Formula;
import org.csstudio.ui.util.swt.stringtable.StringTableEditor;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Dialog that allows configuration of an alarm tree item.
 *  <p>
 *  The dialog initializes with the configuration of a given
 *  alarm tree item. It then allows to edit that configuration,
 *  but it does not actually update the item!
 *  Instead, it only provides the (maybe updated) configuration info.
 *  @author Kay Kasemir, Xihui Chen
 */
public class ItemConfigDialog extends TitleAreaDialog
{
    private AlarmTreeItem item;

    /** PV description or null */
    private Text description_text;

    /** PV alarm delay or null */
    private Text delay_text;

    /** PV enable button or null */
    private Button enable_button;

    /** PV latch enable or null */
    private Button latch_button;

    /** PV annunciation enable or null */
    private Button annunciate_button;

    private String description = ""; //$NON-NLS-1$
    private boolean enabled = true;
    private boolean latch = true;
    private boolean annunciate = false;
    private String filter = ""; //$NON-NLS-1$
    private List<String[]> guidance_table_list, displays_table_list, commands_table_list, auto_actions_table_list;
    private StringTableEditor guidance_editor, display_editor, command_editor, automated_action_editor;

    private int delay = 0;
    private int count = 0;
    private Text count_text, filter_text;


    /** Initialize
     *  @param shell Shell
     *  @param item Item who's configuration is initially displayed.
     *  Dialog will not change the PV, only read its current configuration.
     */
    public ItemConfigDialog(final Shell shell, final AlarmTreeItem item)
    {
        super(shell);
        this.item = item;
    }

    @Override
    protected boolean isResizable()
    {
        return true;
    }

    /** @return description */
    public String getDescription()
    {
        return description;
    }

    /** @return enabled */
    public boolean isEnabled()
    {
        return enabled;
    }

    /** @return Filter expression */
    public String getFilter()
    {
    	return filter;
    }

    /** @return latching */
    public boolean isLatch()
    {
        return latch;
    }

    /** @return annunciating */
    public boolean isAnnunciate()
    {
        return annunciate;
    }

    /** @return Alarm delay [seconds] */
    public int getDelay()
    {
        return delay;
    }

    /** @return Alarm count within delay to detect */
    public int getCount()
    {
        return count;
    }

    /** @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell) */
    @Override
    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);
        shell.setText(Messages.Config_Title);
    }
    
    /** @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite) */
    @Override
	protected Control createDialogArea(final Composite parent) {
		GridData gd = null;
		final Composite parent_composite = (Composite) super
				.createDialogArea(parent);

		final ScrolledComposite sc = new ScrolledComposite(parent, 
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		final Composite composite = new Composite(sc, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);

		// Set title image, arrange for it to be disposed
		final Image title_image = Activator.getImageDescriptor("icons/config_image.png").createImage(); //$NON-NLS-1$
		setTitleImage(title_image);
		parent.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				title_image.dispose();
			}
		});

		setTitle(NLS.bind(Messages.Config_ItemFmt, item.getPathName()));
		setMessage(Messages.Config_Message);

		if (item instanceof AlarmTreePV) {
			final AlarmTreePV pv = (AlarmTreePV) item;

			// Description: __________________
			Label l = new Label(composite, 0);
			l.setText(Messages.Config_Description);
			l.setLayoutData(new GridData());

			description_text = new Text(composite, SWT.BORDER);
			description_text.setText(pv.getDescription());
			description_text.setToolTipText(Messages.Config_DescriptionTT);
			gd = new GridData();
			gd.horizontalSpan = layout.numColumns - 1;
			gd.grabExcessHorizontalSpace = true;
			gd.horizontalAlignment = SWT.FILL;
			description_text.setLayoutData(gd);

			// Alarm Delay [sec]: __________________
			l = new Label(composite, 0);
			l.setText(Messages.Config_Delay);
			l.setLayoutData(new GridData());

			delay_text = new Text(composite, SWT.BORDER);
			delay_text.setText(Integer.toString(pv.getDelay()));
			delay_text.setToolTipText(Messages.Config_DelayTT);
			gd = new GridData();
			gd.horizontalSpan = layout.numColumns - 1;
			gd.grabExcessHorizontalSpace = true;
			gd.horizontalAlignment = SWT.FILL;
			delay_text.setLayoutData(gd);

			// Alarm Count [within delay]: __________________
			l = new Label(composite, 0);
			l.setText(Messages.Config_Count);
			l.setLayoutData(new GridData());

			count_text = new Text(composite, SWT.BORDER);
			count_text.setText(Integer.toString(pv.getCount()));
			count_text.setToolTipText(Messages.Config_CountTT);
			gd = new GridData();
			gd.horizontalSpan = layout.numColumns - 1;
			gd.grabExcessHorizontalSpace = true;
			gd.horizontalAlignment = SWT.FILL;
			count_text.setLayoutData(gd);

			// Behavior: Enabled[x] Latching [x] Annunciating [x]
			l = new Label(composite, 0);
			l.setText(Messages.Config_Behavior);
			l.setLayoutData(new GridData());

			enable_button = new Button(composite, SWT.CHECK);
			enable_button.setText(Messages.Config_Enabled);
			enable_button.setToolTipText(Messages.Config_EnabledTT);
			enable_button.setLayoutData(new GridData());
			enable_button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (enable_button.getSelection())
						setErrorMessage(null);
					else
						setErrorMessage(Messages.Config_DisableWarning);
				}
			});
			enable_button.setSelection(pv.isEnabled());

			latch_button = new Button(composite, SWT.CHECK);
			latch_button.setText(Messages.Config_Latch);
			latch_button.setToolTipText(Messages.Config_LatchingTT);
			latch_button.setSelection(pv.isLatching());
			latch_button.setLayoutData(new GridData());

			annunciate_button = new Button(composite, SWT.CHECK);
			annunciate_button.setText(Messages.Config_Annunciate);
			annunciate_button.setToolTipText(Messages.Config_AnnunciateTT);
			annunciate_button.setSelection(pv.isAnnunciating());
			annunciate_button.setLayoutData(new GridData());

			// Filter: _____________________
			l = new Label(composite, 0);
			l.setText(Messages.Config_Filter);
			l.setLayoutData(new GridData());

			filter_text = new Text(composite, SWT.BORDER);
			filter_text.setText(pv.getFilter());
			filter_text.setToolTipText(Messages.Config_FilterTT);
			gd = new GridData();
			gd.horizontalSpan = layout.numColumns - 1;
			gd.grabExcessHorizontalSpace = true;
			gd.horizontalAlignment = SWT.FILL;
			filter_text.setLayoutData(gd);
			final ModifyListener filter_overrides_enablement = new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					final String filter_spec = filter_text.getText().trim();
					final boolean have_filter = filter_spec.length() > 0;
					enable_button.setEnabled(!have_filter);
					if (have_filter) {
						// When entering a filter, the manual 'enable' is always
						// 'on'
						enable_button.setSelection(true);
						if (isFilterSpecOK(filter_spec))
							setErrorMessage(null);
						else
							setErrorMessage(Messages.ErrorInFilter);
					}
				}
			};
			filter_text.addModifyListener(filter_overrides_enablement);
			// Run once to initialize
			filter_overrides_enablement.modifyText(null);

			// -----------------------------
			l = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
			gd = new GridData();
			gd.horizontalSpan = layout.numColumns;
			gd.grabExcessHorizontalSpace = true;
			gd.horizontalAlignment = SWT.FILL;
			l.setLayoutData(gd);
		}

		// Guidance:
		// +------------------+
		// | List |
		// +------------------+
		// Turn GDC list into tableList, so it can be manipulated in table
		final boolean[] editable = { true, true };
		guidance_table_list = getStringList(item.getGuidance());
		Label l = new Label(composite, 0);
		l.setText(Messages.Config_Guidance);
		l.setLayoutData(new GridData(SWT.FILL, 0, true, false, layout.numColumns, 1));

		guidance_editor = new StringTableEditor(composite, 
				new String[] { Messages.Title, Messages.Detail }, editable,
				guidance_table_list, new EditGDCItemDialog(parent.getShell()), 
				new int[] { 120, 120 });
		guidance_editor.setToolTipText(Messages.Config_GuidanceTT);
		guidance_editor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, layout.numColumns, 1));

		// Displays:
		// +------------------+
		// | List |
		// +------------------+
		displays_table_list = getStringList(item.getDisplays());
		l = new Label(composite, 0);
		l.setText(Messages.Config_Displays);
		l.setLayoutData(new GridData(SWT.FILL, 0, true, false, layout.numColumns, 1));

		display_editor = new StringTableEditor(composite, 
				new String[] { Messages.Title, Messages.Command }, editable,
				displays_table_list, new EditGDCItemDialog(parent.getShell()),
				new int[] { 120, 120 });
		display_editor.setToolTipText(Messages.Config_DisplaysTT);
		display_editor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, layout.numColumns, 1));

		// Commands:
		// +------------------+
		// | List |
		// +------------------+
		commands_table_list = getStringList(item.getCommands());
		l = new Label(composite, 0);
		l.setText(Messages.Config_Commands);
		l.setLayoutData(new GridData(SWT.FILL, 0, true, false, layout.numColumns, 1));

		command_editor = new StringTableEditor(composite, 
				new String[] { Messages.Title, Messages.Command }, editable,
				commands_table_list, new EditGDCItemDialog(parent.getShell()),
				new int[] { 120, 120 });
		command_editor.setToolTipText(Messages.Config_CommandsTT);
		command_editor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, layout.numColumns, 1));

		// Automated Actions:
		// +------------------+
		// | List |
		// +------------------+
		auto_actions_table_list = new ArrayList<String[]>();
		for (AADataStructure aa : item.getAutomatedActions()) {
			final String row[] = { aa.getTitle(), aa.getDetails(),
					String.valueOf(aa.getDelay()) };
			auto_actions_table_list.add(row);
		}
		l = new Label(composite, 0);
		l.setText(Messages.Config_AutomatedActions);
		l.setLayoutData(new GridData(SWT.FILL, 0, true, false, layout.numColumns, 1));
		automated_action_editor = new StringTableEditor(composite,
				new String[] { Messages.Title, Messages.Detail, Messages.Delay }, null, 
				auto_actions_table_list, new EditAAItemDialog(parent.getShell()), 
				new int[] { 120, 110, 10 });
		automated_action_editor.setToolTipText(Messages.Config_AutomatedActionsTT);
		automated_action_editor.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, true, layout.numColumns, 1));

		// Item info line
		l = new Label(composite, 0);
		l.setText(NLS.bind(Messages.Config_ItemInfoFmt,
				Integer.toString(item.getID()), item.getConfigTime()));
		l.setLayoutData(new GridData(0, 0, true, false, layout.numColumns, 1));

		sc.setContent(composite);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		sc.setShowFocusedControl(true);

		return parent_composite;
	}

    /** Convert array from GDC to plain strings for table editor
     *  @param gdc_list List of GDCDataStructure
     *  @return List of String[]
     */
    private List<String[]> getStringList(final GDCDataStructure gdc_list[])
    {
        final List<String[]> result = new ArrayList<String[]>();
        for (GDCDataStructure gdc : gdc_list)
        {
            final String row[] = { gdc.getTitle(), gdc.getDetails() };
            result.add(row);
        }
        return result;
    }

    /** Create GDC from elements
     *  @param title
     *  @param details
     *  @return GDCDataStructure or <code>null</code>
     */
    @SuppressWarnings("nls")
    private GDCDataStructure checkGDCData(final String title, final String details)
    {
       	//if neither title nor details are configured, ignore this entry
    	if ((title == null || title.equals("")) && (details == null || details.equals("")))
    		return null;
    	//if title is not configured, make it same as details
    	if (title == null || title.equals(""))
    		return new GDCDataStructure(details, details);
    	//if details is not configured, make it same as title
    	if (details == null || details.equals(""))
    	    return new GDCDataStructure(title, title);
    	return new GDCDataStructure(title, details);
    }
    
    /** Convert table editor's String table into GDC list */
    private GDCDataStructure[] getGDBArray(final List<String[]> list)
    {
        final List<GDCDataStructure> gdc_list = new ArrayList<GDCDataStructure>();
        for(String[] row : list)
        {
            final GDCDataStructure gdc = checkGDCData(row[0], row[1]);
            if (gdc != null)
                gdc_list.add(gdc);
        }
        return gdc_list.toArray(new GDCDataStructure[gdc_list.size()]);
    }

    /** @return Guidance messages */
    public GDCDataStructure[] getGuidance()
    {
        return getGDBArray(guidance_table_list);
    }

    /** @return Commands */
    public GDCDataStructure[] getCommands()
    {
        return getGDBArray(commands_table_list);
    }
    
    /** @return Related displays */
    public GDCDataStructure[] getDisplays()
    {
        return getGDBArray(displays_table_list);
    }
    
    /** Create AA from elements
     *  @param title
     *  @param details
     *  @param delay
     *  @return AADataStructure or <code>null</code>
     */
	@SuppressWarnings("nls")
	private AADataStructure checkAAData(final String title, final String details, final String delay) {
		int delayInt = 0; // default delay
		if (delay != null && !delay.isEmpty()) delayInt = Integer.valueOf(delay);
		// if neither title nor details are configured, ignore this entry
		if ((title == null || title.equals("")) && (details == null || details.equals("")))
    		return null;
		// if title is not configured, make it same as details
		if (title == null || title.equals(""))
			return new AADataStructure(details, details, delayInt);
		// if details is not configured, make it same as title
		if (details == null || details.equals(""))
			return new AADataStructure(title, title, delayInt);
		return new AADataStructure(title, details, delayInt);
	}
    
	/** @return Automated Actions */
	public AADataStructure[] getAutomatedActions() {
		final List<AADataStructure> aa_list = new ArrayList<AADataStructure>();
		for (String[] row : auto_actions_table_list) {
			final AADataStructure gdc = checkAAData(row[0], row[1], row[2]);
			if (gdc != null) aa_list.add(gdc);
		}
		return aa_list.toArray(new AADataStructure[aa_list.size()]);
	}

    /** Perform basic syntax check of filter formula
     *  @param filter_spec Filter specification
     *  @return <code>true</code> when OK
     */
    protected boolean isFilterSpecOK(final String filter_spec)
    {
        try
        {
        	new Formula(filter_spec, true);
        	return true;
        }
        catch (Exception ex)
        {
    	    return false;
        }
    }

	/** Save user values
     *  @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed()
    {
        // Save values that are currently being edited
        guidance_editor.forceFocus();
        display_editor.forceFocus();
        command_editor.forceFocus();

        description = description_text == null
            ? "" : description_text.getText().trim(); //$NON-NLS-1$
        enabled = enable_button == null ? true : enable_button.getSelection();
        annunciate = annunciate_button == null
            ? false : annunciate_button.getSelection();
        latch = latch_button == null
            ? false : latch_button.getSelection();
        filter = filter_text == null
            ? "" : filter_text.getText().trim(); //$NON-NLS-1$
        if (filter.length() > 0   &&   ! isFilterSpecOK(filter))
        {
        	setErrorMessage(Messages.ErrorInFilter);
        	return;
        }
        if (filter.length() > 0)
            enabled = true;
        try
        {
            delay  = delay_text == null
                ? 0 : Integer.parseInt(delay_text.getText());
        }
        catch (Throwable ex)
        {
            setErrorMessage(Messages.Config_DelayError);
            return;
        }
        try
        {
            count  = count_text == null
                ? 0 : Integer.parseInt(count_text.getText());
        }
        catch (Throwable ex)
        {
            setErrorMessage(Messages.Config_CountError);
            return;
        }
        super.okPressed();
    }
}
