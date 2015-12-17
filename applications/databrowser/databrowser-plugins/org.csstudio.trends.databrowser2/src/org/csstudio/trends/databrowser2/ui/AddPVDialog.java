/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.csstudio.autocomplete.ui.AutoCompleteTypes;
import org.csstudio.autocomplete.ui.AutoCompleteWidget;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Dialog for creating a new PV or Formula Item: Get name, axis.
 *  For PV, also scan period.
 *
 *  @author Kay Kasemir
 *  @author Takashi Nakamoto changed AddPVDialog to update the error/warning message
 *                           dynamically as the user changes any value in this
 *                           dialog message, and to accept an existing name for
 *                           the name of newly created PV item.
 */
public class AddPVDialog extends TitleAreaDialog
{
    /** Existing names */
    final private Set<String> existing_names;

    /** Value axis names */
    final private List<String> axes;

    /** Add formula, not PV? */
    final private boolean formula;

    // GUI elements
    private Text[] txt_name;
    private Text[] txt_period;
    private Button[] btn_monitor;
    private Combo[] axis;

    /** Entered names */
    private String[] name;

    /** Entered period */
    private double[] period;

    /** Selected Axis index or -1 */
    private int[] axis_index;

    private AutoCompleteWidget[] autoCompleteWidget;

    /** Initialize
     *  @param shell Shell
     *  @param count Number of names (or formulas)
     *  @param model Model to get existing names and axes
     *  @param formula Add formula, not PV?
     */
    public AddPVDialog(final Shell shell, final int count, final Model model, final boolean formula)
    {
        super(shell);
        this.formula = formula;
        txt_name = new Text[count];
        txt_period = new Text[count];
        btn_monitor = new Button[count];
        axis = new Combo[count];
        name = new String[count];
        period = new double[count];
        axis_index = new int[count];
        autoCompleteWidget = new AutoCompleteWidget[count];

        existing_names = new HashSet<>();
        for (ModelItem item : model.getItems())
            existing_names.add(item.getName());

        axes = new ArrayList<>();
        for (AxisConfig axis : model.getAxes())
            axes.add(axis.getName());

        setShellStyle(getShellStyle() | SWT.RESIZE);
        setHelpAvailable(false);
    }

    /** @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell) */
    @Override
    protected void configureShell(final Shell shell)
    {
        super.configureShell(shell);
        shell.setText(formula ? Messages.AddFormula : Messages.AddPV);
    }

    /** Allow resize */
    @Override
    protected boolean isResizable()
    {
        return true;
    }

    /** @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite) */
    @Override
    protected Control createDialogArea(final Composite parent_widget)
    {
        final Composite parent_composite = (Composite) super.createDialogArea(parent_widget);

        // Title & Image
        setTitle(formula ? Messages.AddFormula : Messages.AddPV);
        setMessage(formula ? Messages.AddFormulaMsg : Messages.AddPVMsg);
        setTitleImage(Activator.getDefault().getImage("icons/config_image.png")); //$NON-NLS-1$

        // Scroll pane for the box
        final ScrolledComposite scroll = new ScrolledComposite(parent_composite, SWT.H_SCROLL | SWT.V_SCROLL);
        scroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        scroll.setExpandHorizontal(true);
        scroll.setExpandVertical(true);
        scroll.setLayout(new FillLayout());

        // Create box for widgets we're about to add
        final Composite box = new Composite(scroll, 0);
        box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final GridLayout layout = new GridLayout(3, false);
        box.setLayout(layout);

        final ModifyListener update_on_modify = (e) -> updateAndValidate();

        for (int i=0; i<name.length; ++i)
        {
            if (i > 0)
            {
                Label sep = new Label(box, SWT.SEPARATOR | SWT.HORIZONTAL);
                sep.setLayoutData(new GridData(SWT.FILL, 0, true, false, layout.numColumns, 1));
            }

            // PV Name              : _____________________
            Label l = new Label(box, 0);
            l.setText(Messages.Name);
            l.setLayoutData(new GridData());

            txt_name[i] = new Text(box, SWT.BORDER);
            txt_name[i].setToolTipText(formula ? Messages.AddFormula_NameTT : Messages.AddPV_NameTT);
            txt_name[i].setLayoutData(new GridData(SWT.FILL, 0, true, false, layout.numColumns-1, 1));
            autoCompleteWidget[i] = new AutoCompleteWidget(txt_name[i], AutoCompleteTypes.PV);

            if (! formula)
            {
                // Scan Period [seconds]: _____   [x] on change
                l = new Label(box, 0);
                l.setText(Messages.AddPV_Period);
                l.setLayoutData(new GridData());

                txt_period[i] = new Text(box, SWT.BORDER);
                txt_period[i].setToolTipText(Messages.AddPV_PeriodTT);
                txt_period[i].setLayoutData(new GridData(SWT.FILL, 0, true, false));

                btn_monitor[i] = new Button(box, SWT.CHECK);
                btn_monitor[i].setText(Messages.AddPV_OnChange);
                btn_monitor[i].setToolTipText(Messages.AddPV_OnChangeTT);
                btn_monitor[i].setLayoutData(new GridData());

                // Initialize to default period
                final double period = Preferences.getScanPeriod();
                if (period > 0.0)
                    txt_period[i].setText(Double.toString(period));
                else
                {   // 'monitor'
                    txt_period[i].setText("1.0"); //$NON-NLS-1$
                    txt_period[i].setEnabled(false);
                    btn_monitor[i].setSelection(true);
                }

                // Hook listener after initial value has been set
                txt_period[i].addModifyListener(update_on_modify);

                // In 'monitor' mode, the period entry is disabled
                final int index = i;
                btn_monitor[i].addSelectionListener(new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        txt_period[index].setEnabled(!btn_monitor[index].getSelection());
                        updateAndValidate();
                    }
                });
            }

            // Value Axis:            _____
            // If there are axes to select, add related GUI
            l = new Label(box, 0);
            l.setText(Messages.AddPV_Axis);
            l.setLayoutData(new GridData());

            axis[i] = new Combo(box, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.SINGLE);
            axis[i].setToolTipText(Messages.AddPV_AxisTT);
            axis[i].setLayoutData(new GridData(SWT.FILL, 0, true, false));
            // First entry is 'new axis', rest actual axis names
            axis[i].add(Messages.AddPV_NewOrEmptyAxis);
            for (String name : axes)
                axis[i].add(name);

            // Suggest multiple PVs are all placed on the same axis, the last one
            if (name.length > 1)
            {
                axis[i].select(axes.size());
                axis_index[i] = axes.size()-1;
            }
            else
            {
                axis[i].select(0);
                axis_index[i] = -1;
            }


            // Empty label to fill last column
            l = new Label(box, 0);
            l.setLayoutData(new GridData());

            // Set initial text
            if (name[i] != null)
                txt_name[i].setText(name[i]);
            // and _then_ connect modify listener so it's not called for initial text
            txt_name[i].addModifyListener(update_on_modify);
        }

        scroll.setContent(box);
        scroll.setMinSize(box.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        // Perform initial validation
        updateAndValidate();

        return parent_composite;
    }

    /** Save user values
     *  @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed()
    {
        if (!updateAndValidate())
            return;
        for (int i=0; i<name.length; ++i)
            autoCompleteWidget[i].getHistory().addEntry(txt_name[i].getText());
        super.okPressed();
    }

    /** Set initial name. Only effective when called before dialog is opened.
     *  @param i Index
     *  @param name Suggested name
     */
    public void setName(final int i, final String name)
    {
        this.name[i] = name;
    }

    /** @param i Index
     *  @return Entered PV name
     */
    public String getName(final int i)
    {
        return name[i];
    }

    /** @param i Index
     *  @return Entered scan period in seconds
     */
    public double getScanPeriod(final int i)
    {
        return period[i];
    }

    /** @param i Index
     *  @return Index of Value Axis or -1 for 'create new'
     */
    public int getAxisIndex(final int i)
    {
        return axis_index[i];
    }

    /** Update the internal variables according to the user input and
     *  validate them.
     *  This method also updates the message shown in this dialog.
     * @return True if all the values input by the user are valid. Otherwise, false.
     */
    private boolean updateAndValidate()
    {
        for (int i=0; i<name.length; ++i)
        {
            // Valid name?
            name[i] = txt_name[i].getText().trim();
            if (name[i].length() <= 0)
            {
                setMessage(Messages.EmptyNameError, IMessageProvider.ERROR);
                return false;
            }

            // Valid scan period?
            if (!formula && !btn_monitor[i].getSelection())
            {
                if (btn_monitor[i].getSelection())
                    period[i] = 0.0;
                else
                {
                    try
                    {
                        period[i] = Double.parseDouble(txt_period[i].getText().trim());
                        if (period[i] < 0)
                            throw new Exception();
                    }
                    catch (Throwable ex)
                    {
                        setMessage(Messages.InvalidScanPeriodError,
                                IMessageProvider.ERROR);
                        return false;
                    }
                }
            }

            // update axis_index internally
            if (axis[i] == null   ||   axis[i].getSelectionIndex() <= 0)
                axis_index[i] = -1;
            else // entry 0 is 'no axis'
                axis_index[i] = axis[i].getSelectionIndex() - 1;

            // Now that Model accepts multiple items with the same name,
            // there is no need to prohibit from adding a new item with the
            // existing name, but this dialog just warns that the model has
            // at least one item with the given name.
            if (existing_names.contains(name[i]))
            {
                setMessage(NLS.bind(Messages.DuplicateItemFmt, name),
                        IMessageProvider.WARNING);
                return true;
            }
        }
        // All OK
        setMessage(formula ? Messages.AddFormulaMsg : Messages.AddPVMsg);
        return true;
    }
}
