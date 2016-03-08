/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.sds.ui.internal.editor;

import org.csstudio.sds.internal.preferences.PreferenceConstants;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

/**
 * A {@link ControlContribution} to set the spacing of the grid from the {@link DisplayEditor}.
 * @author Kai Meyer
 *
 */
public final class GridSpacingContributionItem extends ControlContribution
    implements SelectionListener, ModifyListener, IPropertyChangeListener {

    /**
     * The {@link Spinner}.
     */
    private Spinner _spacingSpinner;

    /**
     * Constructor.
     */
    public GridSpacingContributionItem() {
        super("GRID_SPACING");
        SdsUiPlugin.getCorePreferenceStore().addPropertyChangeListener(this);
    }

    /**
     * Creates the control for this {@link ContributionItem}.
     * @param parent The parent composite
     * @return The Control
     */
    @Override
    protected Control createControl(final Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2,false);
        layout.marginBottom = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        layout.marginTop = 0;
        comp.setLayout(layout);
        Label label = new Label(comp, SWT.NONE);
        label.setText("Grid Space:");
        label.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false));
        int spacing = SdsUiPlugin.getCorePreferenceStore().getInt(
                PreferenceConstants.PROP_GRID_SPACING);
        _spacingSpinner = new Spinner(comp, SWT.NONE);
        _spacingSpinner.setMinimum(2);
        _spacingSpinner.setMaximum(500);
        _spacingSpinner.setToolTipText("The width of the grid in the editor");
        _spacingSpinner.setSelection(spacing);
        _spacingSpinner.addSelectionListener(this);
        _spacingSpinner.addModifyListener(this);
        _spacingSpinner.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false));
        comp.setBounds(comp.getBounds().x, comp.getBounds().y, computeWidth(_spacingSpinner)+computeWidth(label), comp.getBounds().height);
        return comp;
    }

    /**
     * Computes the width required by control.
     * @param control The control to compute width
     * @return int The width required
     */
    @Override
    protected int computeWidth(final Control control) {
        return control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
    }

    /**
     * Sets the preference value.
     */
    private void setPreferenceValue() {
        String value = String.valueOf(_spacingSpinner.getSelection());
        SdsUiPlugin.getCorePreferenceStore().setValue(PreferenceConstants.PROP_GRID_SPACING, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        this.setPreferenceValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetSelected(final SelectionEvent e) {
        this.setPreferenceValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modifyText(final ModifyEvent e) {
        this.setPreferenceValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        if (event.getProperty().equals(PreferenceConstants.PROP_GRID_SPACING)) {
            Integer spacing;
            if (event.getNewValue() instanceof String) {
                spacing = new Integer((String) event.getNewValue());
            } else {
                spacing = (Integer) event.getNewValue();
            }
            if (spacing!=null && _spacingSpinner!=null && !_spacingSpinner.isDisposed()) {
                _spacingSpinner.setSelection(spacing);
            }
        }
    }
}
