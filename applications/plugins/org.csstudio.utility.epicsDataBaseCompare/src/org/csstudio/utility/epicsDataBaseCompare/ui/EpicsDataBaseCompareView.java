/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id$
 */
package org.csstudio.utility.epicsDataBaseCompare.ui;

import java.io.IOException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.structuremergeviewer.DiffContainer;
import org.eclipse.compare.structuremergeviewer.DiffTreeViewer;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 27.10.2009
 */
public class EpicsDataBaseCompareView extends ViewPart {

    /**
     * TODO (hrickens) :
     *
     * @author hrickens
     * @since 06.09.2011
     */
    private static final class EpicsDBTreeContentProvider implements ITreeContentProvider {

        /**
         * Constructor.
         */
        public EpicsDBTreeContentProvider() {
            // Constructor.
        }


        @Override
        public void dispose() {
            // TODO Auto-generated method stub

        }

        @Override
        @CheckForNull
        public Object[] getChildren(@Nullable final Object element) {
            if (element instanceof EpicsDBFile) {
                final EpicsDBFile dbFile = (EpicsDBFile) element;
                return dbFile.getRecords().values().toArray();
            } else if (element instanceof EpicsRecord) {
                final EpicsRecord record = (EpicsRecord) element;
                return record.getFilds().toArray();
            }
            return null;
        }

        @Override
        @CheckForNull
        public Object[] getElements(@Nullable final Object inputElement) {
            if (inputElement instanceof EpicsDBFile) {
                final EpicsDBFile file = (EpicsDBFile) inputElement;
                return file.getRecords().values().toArray();

            }
            return null;
        }

        @Override
        @CheckForNull
        public Object getParent(@Nullable final Object element) {
            return null;
        }

        @Override
        public boolean hasChildren(@Nullable final Object element) {
            if (element instanceof EpicsDBFile) {
                final EpicsDBFile dbFile = (EpicsDBFile) element;
                return !dbFile.getRecords().isEmpty();
            } else if (element instanceof EpicsRecord) {
                final EpicsRecord record = (EpicsRecord) element;
                return !record.getFilds().isEmpty();
            }
            return false;
        }

        @Override
        public void inputChanged(@Nullable final Viewer viewer, @Nullable final Object oldInput, @Nullable final Object newInput) {
            // TODO Auto-generated method stub
        }
    }

    /**
     * @author hrickens
     * @since 09.09.2011
     */
    private static final class FileDialogSelectionListener implements SelectionListener {
        private final Text _textField;
        private final Composite _parent;

        public FileDialogSelectionListener(@Nonnull final Text textField, @Nonnull final Composite parent) {
            _textField = textField;
            _parent = parent;
        }

        @Override
        public void widgetDefaultSelected(@Nullable final SelectionEvent e) {
            open();
        }

        @Override
        public void widgetSelected(@Nullable final SelectionEvent e) {
            open();
        }

        private void open() {
            final FileDialog fd = new FileDialog(_parent.getShell());
            fd.setFilterExtensions(new String[] {"*.db*", "*.*" });
            final String file = fd.open();
            if (file != null) {
                _textField.setText(file);
            }

        }
    }
    /**
     * @author hrickens
     * @since 06.09.2011
     */
    private final class ILabelProviderImplementation extends LabelProvider implements
    IColorProvider {
        private final Color red = new Color(null, 255, 0, 0);
        private final Color orange = new Color(null, 200, 200, 0);
        private final Color white = new Color(null, 255,255,255);
        private final TreeViewer _otherSide;

        /**
         * Constructor.
         * @param otherSide
         */
        public ILabelProviderImplementation(@Nonnull final TreeViewer otherSide) {
            _otherSide = otherSide;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public Color getBackground(@Nullable final Object element) {

            if (element instanceof EpicsRecord) {
                return getRecordBackgroundColor(element);

            } else if (element instanceof Field) {
                return getFieldBackgroundColor(element);
            }
            return white;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @CheckForNull
        public Color getForeground(@Nullable final Object element) {
            return null;
        }

        @Override
        @Nonnull
        public String getText(@Nonnull final Object element) {
            if (element instanceof EpicsRecord) {
                final EpicsRecord record = (EpicsRecord) element;
                return record.getRecordName();
            }
            return element.toString();
        }

        @Nonnull
        private Color getFieldBackgroundColor(@Nonnull final Object element) {
            final Field field = (Field) element;
            final EpicsRecord parent = field.getParent();
            final Object input = _otherSide.getInput();
            if (input instanceof EpicsDBFile) {
                final EpicsDBFile dbFile = (EpicsDBFile) input;
                final EpicsRecord record = dbFile.getRecord(parent.getRecordName());
                if (record == null) {
                    return red;
                }
                final Field otherField = record.getField(field.getField());
                if (field.compareTo(otherField) != 0) {
                    return orange;

                }
            }
            return white;
        }

        @Nonnull
        private Color getRecordBackgroundColor(@Nonnull final Object element) {
            final EpicsRecord record = (EpicsRecord) element;
            final Object input = _otherSide.getInput();
            if (input instanceof EpicsDBFile) {
                final EpicsDBFile dbFile = (EpicsDBFile) input;
                final EpicsRecord record2 = dbFile.getRecord(record.getRecordName());
                if (record2 == null) {
                    return red;
                }
                if (record.compareTo(record2) != 0) {
                    return orange;
                }
            }
            return white;
        }
    }

    /**
     * TODO (hrickens) :
     *
     * @author hrickens
     * @since 09.09.2011
     */
    private final class StartCompareSelectionListener implements SelectionListener {
        private final EpicsDataBaseCompareView _epicsDataBaseCompareView;

        /**
         * Constructor.
         * @param epicsDataBaseCompareView
         */
        public StartCompareSelectionListener(@Nonnull final EpicsDataBaseCompareView epicsDataBaseCompareView) {
            _epicsDataBaseCompareView = epicsDataBaseCompareView;
        }

        @Override
        public void widgetDefaultSelected(@Nullable final SelectionEvent e) {
            _epicsDataBaseCompareView.compare();
        }

        @Override
        public void widgetSelected(@Nullable final SelectionEvent e) {
            _epicsDataBaseCompareView.compare();
        }
    }
    private TabFolder _tabFolder;
    private Text _fileLeft;
    private Text _fileRight;
    private TreeViewer _compareRight;
    private TreeViewer _compareLeft;
    private EpicsDBFile _epicsDBFileLeft;
    private EpicsDBFile _epicsDBFileRight;

    private Text _leftText;

    private Text _rightText;

    private DiffTreeViewer _diffTreeViewerLeft;

    private DiffTreeViewer _diffTreeViewerRight;

    /**
     * Constructor.
     */
    public EpicsDataBaseCompareView() {
        // Constructor.
    }

    @Override
    public final void createPartControl(@Nonnull final Composite parent) {
        _tabFolder = new TabFolder(parent, SWT.TOP);
        _tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final TabItem userInterfaceTabItem = new TabItem(_tabFolder, SWT.NONE);
        userInterfaceTabItem.setText("Control");
        final Composite userInterfaceComposite = new Composite(_tabFolder, SWT.NONE);
        userInterfaceComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        userInterfaceComposite.setLayout(new GridLayout(2, true));
        userInterfaceTabItem.setControl(userInterfaceComposite);
        makeUserInterface(userInterfaceComposite);

        final TabItem tree2TabItem = new TabItem(_tabFolder, SWT.NONE);
        tree2TabItem.setText("DiffTreeView");
        final Composite diffTreeComposite = new Composite(_tabFolder, SWT.NONE);
        diffTreeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        diffTreeComposite.setLayout(new GridLayout(2, true));
        tree2TabItem.setControl(diffTreeComposite);
        _diffTreeViewerLeft = new DiffTreeViewer(diffTreeComposite, new CompareConfiguration());
        _diffTreeViewerLeft.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        _diffTreeViewerRight = new DiffTreeViewer(diffTreeComposite, new CompareConfiguration());
        _diffTreeViewerRight.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final TabItem treeTabItem = new TabItem(_tabFolder, SWT.NONE);
        treeTabItem.setText("TreeCompareView");
        final Composite treeCompareComposite = new Composite(_tabFolder, SWT.NONE);
        treeCompareComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        treeCompareComposite.setLayout(new GridLayout(2, true));
        treeTabItem.setControl(treeCompareComposite);
        makeTreeCompareView(treeCompareComposite);

    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    /**
     * @param treeTabItem
     */
    private void makeTreeCompareView(@Nonnull final Composite parent) {
        _leftText = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.READ_ONLY | SWT.BORDER);
        _leftText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        _rightText = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.READ_ONLY | SWT.BORDER);
        _rightText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        _compareLeft = new TreeViewer(parent);
        _compareLeft.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        _compareLeft.setContentProvider(new EpicsDBTreeContentProvider());

        _compareRight = new TreeViewer(parent);
        _compareRight.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        _compareRight.setContentProvider(new EpicsDBTreeContentProvider());

        _compareLeft.setLabelProvider(new ILabelProviderImplementation(_compareRight));
        _compareRight.setLabelProvider(new ILabelProviderImplementation(_compareLeft));
    }

    private void makeUserInterface(@Nonnull final Composite parent) {
        final GridLayoutFactory numColumns = GridLayoutFactory.swtDefaults().numColumns(3);
        numColumns.applyTo(parent);

        // File 1
        final Label file1Label = new Label(parent, SWT.NONE);
        file1Label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        file1Label.setText("File 1:");
        _fileLeft = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        _fileLeft.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        final Button file1Open = new Button(parent, SWT.PUSH);
        file1Open.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        file1Open.setText("Open");
        file1Open.addSelectionListener(new FileDialogSelectionListener(_fileLeft, parent));

        // File 2
        final Label file2Label = new Label(parent, SWT.NONE);
        file2Label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        file2Label.setText("File 2:");
        _fileRight = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        _fileRight.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        final Button file2Open = new Button(parent, SWT.PUSH);
        file2Open.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        file2Open.setText("Open");
        file2Open.addSelectionListener(new FileDialogSelectionListener(_fileRight, parent));

        // Compare
        final Button compareButton = new Button(parent, SWT.PUSH);
        compareButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        compareButton.setText("Compare");
        GridDataFactory.swtDefaults().applyTo(compareButton);
        compareButton.addSelectionListener(new StartCompareSelectionListener(this));

    }

    protected void compare() {
        if (!have2Files()) {
            return;
        }
        final EpicsDBParser p1 = new EpicsDBParser();
        final EpicsDBParser p2 = new EpicsDBParser();
        try {
            _epicsDBFileLeft = p1.parseFile(_fileLeft.getText());
            _epicsDBFileRight = p2.parseFile(_fileRight.getText());
        } catch (final IOException e) {
            e.printStackTrace();
            return;
        }
        if (_epicsDBFileLeft == null || _epicsDBFileRight == null) {
            return;
        }

        _leftText.setText("Size: "+_epicsDBFileLeft.getRecords().size());
        _rightText.setText("Size: "+_epicsDBFileRight.getRecords().size());
        _compareLeft.setInput(_epicsDBFileLeft);
        _compareRight.setInput(_epicsDBFileRight);

        final DiffContainer diffContainerLeft = DiffContainerBuilder.build(_epicsDBFileLeft, _epicsDBFileRight, true);
        final DiffContainer diffContainerRight = DiffContainerBuilder.build(_epicsDBFileRight, _epicsDBFileLeft, false);

        final CompareConfiguration configuration = new CompareConfiguration();
        configuration.setLeftLabel(_epicsDBFileLeft.getFileName());
        configuration.setRightLabel(_epicsDBFileRight.getFileName());
        configuration.setProperty(CompareConfiguration.IGNORE_WHITESPACE, true);
        configuration.setProperty(CompareConfiguration.USE_OUTLINE_VIEW, true);
        final EpicsDBCompareEditorInput input = new EpicsDBCompareEditorInput(configuration, _epicsDBFileLeft, _epicsDBFileRight);
        CompareUI.openCompareEditor(input);
        _diffTreeViewerLeft.setInput(diffContainerLeft);
        _diffTreeViewerRight.setInput(diffContainerRight);
    }

    /**
     * @return
     */
    private boolean have2Files() {
        return !(_fileLeft == null || _fileLeft.getText() == null || _fileRight == null
                || _fileRight.getText() == null);
    }

}
