package org.csstudio.saverestore.masar;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.ui.util.swt.stringtable.StringTableEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 *
 * <code>ServicesFieldEditor</code> is the preferences editor for the MASAR services list.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ServicesFieldEditor extends FieldEditor {
    private List<String> services = new ArrayList<>();
    private Label label;
    private StringTableEditor editor;

    /**
     * Construct new editor.
     *
     * @param parent the parent of this editor
     */
    public ServicesFieldEditor(Composite parent) {
        super("MASAR Services Names", "MASAR Services", parent);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
     */
    @Override
    public int getNumberOfControls() {
        return 1;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
     */
    @Override
    protected void adjustForNumColumns(int numColumns) {
        if (editor != null && label != null) {
            GridData gd = (GridData) label.getLayoutData();
            gd.horizontalSpan = numColumns;
            gd = (GridData) editor.getLayoutData();
            gd.horizontalSpan = numColumns;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite, int)
     */
    @Override
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        label = new Label(parent, 0);
        label.setText("MASAR Services:");
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        label.setLayoutData(gd);

        editor = new StringTableEditor(parent, services);
        editor.setToolTipText("The list of all available MASAR services");
        gd = new GridData();
        gd.horizontalSpan = numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        editor.setLayoutData(gd);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.preference.FieldEditor#doLoad()
     */
    @Override
    protected void doLoad() {
        updateColumns(Activator.getInstance().getServices());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
     */
    @Override
    protected void doLoadDefault() {
        String defaultServices = getPreferenceStore().getDefaultString(Activator.PREF_SERVICES);
        if (defaultServices == null) {
            updateColumns(new String[0]);
        } else {
            updateColumns(defaultServices.split("\\,"));
        }
    }

    private void updateColumns(final String[] services) {
        this.services.clear();
        for (String s : services) {
            this.services.add(s);
        }
        if (editor != null) {
            editor.refresh();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.preference.FieldEditor#doStore()
     */
    @Override
    protected void doStore() {
        String[] s = services.toArray(new String[services.size()]);
        Activator.getInstance().setServices(s);
    }
}
