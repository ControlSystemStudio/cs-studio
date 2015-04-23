package org.csstudio.alarm.beast.ui.alarmtable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * <code>ColumnConfigurer</code> is a dialog that allows to configure the visibility
 * of table columns as well as their order of appearance in the table.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ColumnConfigurer extends TitleAreaDialog {

    private static final String LEFT = "Back24.gif";
    private static final String RIGHT = "Forward24.gif";
    private static final String UP = "Up24.gif";
    private static final String DOWN = "Down24.gif";
    private static final ImageRegistry IMAGES = new ImageRegistry(Display.getDefault());
    static {
        String ICONS = "icons/";
        IMAGES.put(LEFT, Activator.imageDescriptorFromPlugin(Activator.ID, ICONS + LEFT));
        IMAGES.put(RIGHT, Activator.imageDescriptorFromPlugin(Activator.ID, ICONS + RIGHT));
        IMAGES.put(UP, Activator.imageDescriptorFromPlugin(Activator.ID, ICONS + UP));
        IMAGES.put(DOWN, Activator.imageDescriptorFromPlugin(Activator.ID, ICONS + DOWN));
    }

    private static class ContentProvider implements IStructuredContentProvider {
        @Override
        public Object[] getElements(Object inputElement) {
            List<?> list = (List<?>) inputElement;
            return list.toArray(new ColumnWrapper[list.size()]);
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        @Override
        public void dispose() {
        }
    }

    private CellLabelProvider labelProvider = new CellLabelProvider() {

        @Override
        public void update(ViewerCell cell) {
            ColumnWrapper cw = (ColumnWrapper) cell.getElement();
            cell.setText(cw.getColumnInfo().getTitle());
        }

    };

    private TableViewer shownList;
    private TableViewer hiddenList;
    private Button leftButton;
    private Button rightButton;
    private Button upButton;
    private Button downButton;

    private final ColumnWrapper[] columns;

    /**
     * Constructs a new configurer.
     * 
     * @param parentShell the parent window
     * @param columns the columns that will be manipulated
     */
    public ColumnConfigurer(Shell parentShell, ColumnWrapper[] columns) {
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        this.columns = columns;
    }

    /**
     * @return the columns as they were set by this configurator
     */
    public ColumnWrapper[] getColumns() {
        return columns;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected void okPressed() {
        List<ColumnWrapper> hidden = (List<ColumnWrapper>) hiddenList.getInput();
        List<ColumnWrapper> shown = (List<ColumnWrapper>) shownList.getInput();
        int i = 0;
        for (ColumnWrapper cw : shown) {
            columns[i++] = cw;
        }
        for (ColumnWrapper cw : hidden) {
            columns[i++] = cw;
        }
        super.okPressed();
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.ColumnConfigTitle);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);

        setTitle(Messages.ColumnConfigTitle);
        setMessage(Messages.ColumnConfigDescription);
        Composite base = new Composite(composite, SWT.NONE);
        base.setLayout(new GridLayout(2, true));
        base.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite left = new Composite(base, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        left.setLayout(layout);
        left.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label leftTitle = new Label(left, SWT.LEFT);
        leftTitle.setText("Hidden Columns");
        GridData data = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false, 2, 1);
        leftTitle.setLayoutData(data);

        hiddenList = new TableViewer(left);
        hiddenList.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        hiddenList.setLabelProvider(labelProvider);
        hiddenList.setContentProvider(new ContentProvider());
        hiddenList.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                moveLeftRight(true);
            }
        });

        Composite leftRight = new Composite(left, SWT.NONE);
        layout = new GridLayout(1, true);
        layout.marginLeft = 5;
        leftRight.setLayout(layout);
        rightButton = new Button(leftRight, SWT.PUSH);
        rightButton.setToolTipText("Show");
        rightButton.setImage(IMAGES.get(RIGHT));
        rightButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                moveLeftRight(true);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        rightButton.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
        leftButton = new Button(leftRight, SWT.PUSH);
        leftButton.setToolTipText("Hide");
        leftButton.setImage(IMAGES.get(LEFT));
        leftButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                moveLeftRight(false);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        leftButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        leftRight.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        Composite right = new Composite(base, SWT.NONE);
        right.setLayout(new GridLayout(2, false));
        right.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label rightTitle = new Label(right, SWT.LEFT);
        rightTitle.setText("Shown Columns");
        data = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false, 2, 1);
        rightTitle.setLayoutData(data);

        shownList = new TableViewer(right);
        shownList.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        shownList.setLabelProvider(labelProvider);
        shownList.setContentProvider(new ContentProvider());
        shownList.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                moveLeftRight(false);
            }
        });

        Composite upDown = new Composite(right, SWT.NONE);
        upDown.setLayout(new GridLayout(1, true));
        upButton = new Button(upDown, SWT.PUSH);
        upButton.setToolTipText("Up");
        upButton.setImage(IMAGES.get(UP));
        upButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                moveUpDown(true);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        upButton.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
        downButton = new Button(upDown, SWT.PUSH);
        downButton.setToolTipText("Down");
        downButton.setImage(IMAGES.get(DOWN));
        downButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                moveUpDown(false);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        downButton.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false, 1, 1));
        upDown.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        List<ColumnWrapper> hidden = new LinkedList<>();
        List<ColumnWrapper> shown = new LinkedList<>();
        for (ColumnWrapper c : columns) {
            if (c.isVisible()) {
                hidden.add(c);
            } else {
                shown.add(c);
            }
        }
        hiddenList.setInput(hidden);
        shownList.setInput(shown);

        updateTables();

        return composite;

    }

    private void moveLeftRight(boolean right) {
        IStructuredSelection selection;
        if (right) {
            selection = (IStructuredSelection) hiddenList.getSelection();
        } else {
            selection = (IStructuredSelection) shownList.getSelection();
        }
        Iterator<?> it = selection.iterator();
        while (it.hasNext()) {
            ((ColumnWrapper) it.next()).setVisible(right);
        }
        updateTables();
    }

    private void moveUpDown(boolean up) {
        IStructuredSelection selection = (IStructuredSelection) shownList.getSelection();

        @SuppressWarnings("unchecked")
        List<ColumnWrapper> shown = (List<ColumnWrapper>) shownList.getInput();
        Iterator<?> it = selection.iterator();
        while (it.hasNext()) {
            ColumnWrapper wrapper = ((ColumnWrapper) it.next());
            int idx = -1;
            Iterator<ColumnWrapper> shownIt = shown.iterator();
            while (shownIt.hasNext()) {
                idx++;
                if (shownIt.next() == wrapper) {
                    break;
                }
            }

            if (up) {
                if (idx > 0) {
                    ColumnWrapper w = shown.get(idx - 1);
                    shown.set(idx - 1, wrapper);
                    shown.set(idx, w);
                }
            } else {
                if (idx < shown.size() - 1) {
                    ColumnWrapper w = shown.get(idx + 1);
                    shown.set(idx + 1, wrapper);
                    shown.set(idx, w);
                }
            }
        }
        hiddenList.refresh();
        shownList.refresh();
    }

    @SuppressWarnings("unchecked")
    private void updateTables() {
        List<ColumnWrapper> hidden = (List<ColumnWrapper>) hiddenList.getInput();
        List<ColumnWrapper> shown = (List<ColumnWrapper>) shownList.getInput();

        ListIterator<ColumnWrapper> hi = hidden.listIterator();
        while (hi.hasNext()) {
            ColumnWrapper w = hi.next();
            if (w.isVisible()) {
                hi.remove();
                shown.add(w);
            }
        }

        ListIterator<ColumnWrapper> si = shown.listIterator();
        while (si.hasNext()) {
            ColumnWrapper w = si.next();
            if (!w.isVisible()) {
                si.remove();
                hidden.add(w);
            }
        }

        hiddenList.refresh();
        shownList.refresh();
    }

}
