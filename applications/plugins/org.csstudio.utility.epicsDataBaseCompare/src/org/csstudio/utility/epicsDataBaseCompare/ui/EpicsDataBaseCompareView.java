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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.utility.epicsDataBaseCompare.Activator;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.team.core.synchronize.SyncInfo;
import org.eclipse.team.core.variants.ThreeWayResourceComparator;
import org.eclipse.team.core.variants.ThreeWaySynchronizer;
import org.eclipse.team.internal.ui.actions.CompareRevisionAction;
import org.eclipse.team.internal.ui.synchronize.SaveablesCompareEditorInput;
import org.eclipse.team.ui.synchronize.SyncInfoCompareInput;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.ViewPart;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 27.10.2009
 */
public class EpicsDataBaseCompareView extends ViewPart {

    private static final ImageDescriptor ALL_LINES_IMAGE = PlatformUI.getWorkbench().getSharedImages()
                    .getImageDescriptor(ISharedImages.IMG_OBJ_FILE);
    private static ImageDescriptor DEFF_LINES_IMAGE;

    private static final String LS = System.getProperty("line.separator");

    private static final Color ROW_PART_COLOR = CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_YELLOW);
    private static final Color FULL_ROW_COLOR = CustomMediaFactory.getInstance().getColor(255, 255, 145);

    private boolean _print;
    private StyledText _file1Compare;
    private StyledText _file2Compare;

    private TabItem _compareTabItem;
    private TabFolder _tabFolder;

    private Text _fileLeft;
    private Text _fileRight;
    
    private int _diffCount;
    private StatusLineContributionItem _statusLineItem;

    private final class FileDialogSelectionListener implements SelectionListener {
        private final Text _textField;
        private final Composite _parent;

        private FileDialogSelectionListener(Text textField, Composite parent) {
            _textField = textField;
            _parent = parent;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            open();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            open();
        }

        private void open() {
            FileDialog fd = new FileDialog(_parent.getShell());
            fd.setFilterExtensions(new String[] { "*.db*", "*.*" });
            String file = fd.open();
            if (file != null) {
                _textField.setText(file);
            }

        }
    }

    /**
     * 
     */
    public EpicsDataBaseCompareView() {
        DEFF_LINES_IMAGE = Activator.getImageDescriptor("/icons/deprecated.gif");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(final Composite parent) {
//        getViewSite().
        fillLocalToolBar();
        _tabFolder = new TabFolder(parent, SWT.TOP);
        _tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        TabItem userInterfaceTabItem = new TabItem(_tabFolder, SWT.NONE);
        userInterfaceTabItem.setText("Control");
        Composite userInterfaceComposite = new Composite(_tabFolder, SWT.NONE);
        userInterfaceComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        userInterfaceComposite.setLayout(new GridLayout(2, true));
        userInterfaceTabItem.setControl(userInterfaceComposite);
        makeUserInterface(userInterfaceComposite);

        _compareTabItem = new TabItem(_tabFolder, SWT.NONE);
        _compareTabItem.setText("Compare");
        Composite compareComposite = new Composite(_tabFolder, SWT.NONE);
        compareComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        compareComposite.setLayout(new GridLayout(2, true));
        _compareTabItem.setControl(compareComposite);
        makeCompareInterface(compareComposite);

    }

    private void fillLocalToolBar() {
        IActionBars bars = getViewSite().getActionBars();
        IAction showEquelAction = new Action("Create") {

            public void run() {
                _print = !_print;
                if(_print) {
                    setImageDescriptor(ALL_LINES_IMAGE);
                }else {
                    setImageDescriptor(DEFF_LINES_IMAGE);
                }
                compare();
            }
        };
        showEquelAction.setToolTipText("Action Create tooltip");
        showEquelAction.setChecked(true);
        showEquelAction.setImageDescriptor(DEFF_LINES_IMAGE);
        bars.getToolBarManager().add(showEquelAction);

        _statusLineItem = new StatusLineContributionItem("EDBCV_ID");
        _statusLineItem.setText("Test");
        bars.getStatusLineManager().add(_statusLineItem);

    }

    private void makeCompareInterface(Composite parent) {
        _file1Compare = new StyledText(parent, SWT.MULTI | SWT.LEAD | SWT.BORDER | SWT.V_SCROLL);
        _file1Compare.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        _file1Compare.setText("123");

        _file2Compare = new StyledText(parent, SWT.MULTI | SWT.LEAD | SWT.BORDER | SWT.V_SCROLL);
        _file2Compare.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        _file2Compare.setText("ABC");

        final ScrollBar vBar1 = _file1Compare.getVerticalBar();
        final ScrollBar vBar2 = _file2Compare.getVerticalBar();
        // final ScrollBar hBar1 = _file1Compare.getHorizontalBar ();
        // final ScrollBar hBar2 = _file2Compare.getHorizontalBar ();
        SelectionListener listener1 = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                _file2Compare.getVerticalBar().setSelection(
                        _file1Compare.getVerticalBar().getSelection());
                _file2Compare.redraw();
                _file2Compare.update();
            }
        };
        SelectionListener listener2 = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                _file1Compare.getVerticalBar().setSelection(
                        _file2Compare.getVerticalBar().getSelection());
                _file1Compare.layout(false);
                _file1Compare.update();
            }
        };
        vBar1.addSelectionListener(listener1);
        // hBar1.addSelectionListener (listener1);
        vBar2.addSelectionListener(listener2);
        // hBar2.addSelectionListener (listener2);

    }

    private void makeUserInterface(Composite parent) {
        GridLayoutFactory numColumns = GridLayoutFactory.swtDefaults().numColumns(3);
        numColumns.applyTo(parent);

        // File 1
        Label file1Label = new Label(parent, SWT.NONE);
        file1Label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        file1Label.setText("File 1:");
        _fileLeft = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        _fileLeft.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        Button file1Open = new Button(parent, SWT.PUSH);
        file1Open.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        file1Open.setText("Open");
        file1Open.addSelectionListener(new FileDialogSelectionListener(_fileLeft, parent));

        // File 2
        Label file2Label = new Label(parent, SWT.NONE);
        file2Label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        file2Label.setText("File 2:");
        _fileRight = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        _fileRight.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        Button file2Open = new Button(parent, SWT.PUSH);
        file2Open.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        file2Open.setText("Open");
        file2Open.addSelectionListener(new FileDialogSelectionListener(_fileRight, parent));

        // Compare
        Button compareButton = new Button(parent, SWT.PUSH);
        compareButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        compareButton.setText("Compare");
        GridDataFactory.swtDefaults().applyTo(compareButton);
        compareButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                compare();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                compare();
            }
        });

    }

    private ITypedElement getElementFor(IResource resource) {
        return SyncInfoCompareInput.createFileElement((IFile) resource);
        // return SaveablesCompareEditorInput.createFileElement((IFile) resource);
    }

    private void openInCompare(IFile file1, URI uri2) {

        //        
        // CompareConfiguration configuration = new CompareConfiguration();
        // ISynchronizePageConfiguration pageConfiguration;
        // ISynchronizeParticipant participant = new ModelSynchronizeParticipant();
        // ParticipantPageCompareEditorInput ppcei = new
        // ParticipantPageCompareEditorInput(configuration, pageConfiguration, participant);

        // ResourcesPlugin.getWorkspace().getRoot().getFile(nul).;
        // ISynchronizer iSynchronizer = ResourcesPlugin.getWorkspace().getSynchronizer();
        QualifiedName name = new QualifiedName(null, uri2.getRawPath());
        ThreeWaySynchronizer synchronizer = new ThreeWaySynchronizer(name);
        ThreeWayResourceComparator threeWayResourceComparator = new ThreeWayResourceComparator(
                synchronizer);
        SyncInfo sync = new SyncInfo(file1, null, null, threeWayResourceComparator);
        SyncInfoCompareInput sici = new SyncInfoCompareInput("Test", sync);
        // CompareUI.openCompareEditor(sici);
        CompareUI.openCompareDialog(sici);

    }

    private void openInCompare(ITypedElement ancestor, ITypedElement left, ITypedElement right) {
        IWorkbenchPage workBenchPage = Workbench.getInstance().getActiveWorkbenchWindow()
                .getActivePage();
        CompareEditorInput input = new SaveablesCompareEditorInput(ancestor, left, right,
                workBenchPage);
        IEditorPart editor = CompareRevisionAction.findReusableCompareEditor(workBenchPage);
        if (editor != null) {
            IEditorInput otherInput = editor.getEditorInput();
            if (otherInput.equals(input)) {
                // simply provide focus to editor
                workBenchPage.activate(editor);
            } else {
                // if editor is currently not open on that input either re-use
                // existing
                CompareUI.reuseCompareEditor(input, (IReusableEditor) editor);
                workBenchPage.activate(editor);
            }
        } else {
            CompareUI.openCompareEditor(input);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    private void compare() {
        if(_fileLeft==null||_fileLeft.getText()==null||_fileRight==null||_fileRight.getText()==null) {
            return;
        }
        int iLeft = 0;
        int iRight = 0;
        int start = 0;
        _diffCount = 0;
        EpicsDBParser p1 = new EpicsDBParser();
        EpicsDBParser p2 = new EpicsDBParser();
        final EpicsDBFile epicsDBFileLeft = p1.parseFile(_fileLeft.getText());
        final EpicsDBFile epicsDBFileRight = p2.parseFile(_fileRight.getText());
        if(epicsDBFileLeft==null||epicsDBFileRight==null ) {
            return;
        }
        Color gray = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
        List<StyleRange> styleRanges1 = new ArrayList<StyleRange>();
        List<StyleRange> styleRanges2 = new ArrayList<StyleRange>();
        StringBuffer left = new StringBuffer(epicsDBFileLeft.getFile());
        styleRanges1.add(new StyleRange(start, left.length(), gray, null));
        left.append(LS);
        StringBuffer right = new StringBuffer(epicsDBFileRight.getFile());
        styleRanges2.add(new StyleRange(start, right.length(), gray, null));
        right.append(LS);

        while (iLeft < epicsDBFileLeft.size() && iRight < epicsDBFileRight.size()) {
            int compareEpicsRecords = compareEpicsRecords(epicsDBFileLeft.get(iLeft),
                    epicsDBFileRight.get(iRight));
            if (compareEpicsRecords == 0) {
                // left side
                start = left.length();
                boolean equal = epicsDBFileLeft.get(iLeft).toString().equals(
                        epicsDBFileRight.get(iRight).toString());
                if (_print || !equal) {
                    left.append(epicsDBFileLeft.get(iLeft));
                    if (!equal) {
                        _diffCount++;
                        generatestyleRanges(start, styleRanges1, epicsDBFileLeft.get(iLeft)
                                .toString(), epicsDBFileRight.get(iRight).toString());
                    }
                    left.append(LS);
                }
                // right side
                start = right.length();
                equal = epicsDBFileLeft.get(iLeft).toString().equals(
                        epicsDBFileRight.get(iRight).toString());
                if (_print || !equal) {
                    right.append(epicsDBFileRight.get(iRight));
                    if (!equal) {
                        generatestyleRanges(start, styleRanges2, epicsDBFileRight.get(iRight)
                                .toString(), epicsDBFileLeft.get(iLeft).toString());
                    }
                    right.append(LS);
                }
                iLeft++;
                iRight++;
            } else if (compareEpicsRecords < 0) {
                _diffCount++;
                start = left.length();
                left.append(epicsDBFileLeft.get(iLeft));
                styleRanges1.add(getDarkYellow(start, left.length() - start));
                left.append(LS);
                // right side
                right.append(LS);
                iLeft++;
            } else if (compareEpicsRecords > 0) {
                _diffCount++;
                // left side
                left.append(LS);
                // right side
                start = right.length();
                right.append(epicsDBFileRight.get(iRight));
                styleRanges2.add(getDarkYellow(start, right.length() - start));
                right.append(LS);
                iRight++;
            }
        }
        left.append(LS);
        right.append(LS);
        _file1Compare.setText(left.toString());
        _file2Compare.setText(right.toString());
        // _file1Compare.setText("");
        // _file2Compare.setText("");
        if (styleRanges1 != null && styleRanges1.size() > 0) {
            StyleRange[] array = styleRanges1.toArray(new StyleRange[0]);
            _file1Compare.setStyleRanges(array);
        }
        if (styleRanges2 != null && styleRanges2.size() > 0) {
            StyleRange[] array = styleRanges2.toArray(new StyleRange[0]);
            _file2Compare.setStyleRanges(array);
        }
        _statusLineItem.setText("Found "+_diffCount+" differnces");
        _tabFolder.setSelection(_compareTabItem);
        // try {
        // final File tempFile1 = File.createTempFile("compare", ".txt");
        // File tempFile2 = File.createTempFile("compare", ".txt");
        //
        // FileWriter fw = new FileWriter(tempFile1);
        // BufferedWriter bf = new BufferedWriter(fw);
        // bf.append(epicsDBFile1.toString());
        // bf.flush();
        //
        // fw = new FileWriter(tempFile2);
        // bf = new BufferedWriter(fw);
        // bf.append(epicsDBFile1.toString());
        // bf.flush();
        //
        // Path path1 = new Path(tempFile1.getAbsolutePath());
        // Path path2 = new Path(tempFile2.getAbsolutePath());
        //
        // IFile file1 = ResourcesPlugin.getWorkspace().getRoot().getFile(path1);
        // IFile file2 = ResourcesPlugin.getWorkspace().getRoot().getFile(path2);

        // openInCompare(file1, tempFile2.toURI());
        // openInCompare(null, left, right);
        //                    
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

    }

    private void generatestyleRanges(int start, List<StyleRange> styleRanges, String string1,
            String string2) {
        int startPos = 0;
        int endPos = 0;
        for (int i = 0; i < string1.length() && i < string2.length(); i++) {
            if (string1.charAt(i) == string2.charAt(i)) {
                if (startPos > endPos) {
                    styleRanges.add(getYellow(start + startPos, i - startPos));
                    startPos = i;
                    endPos = i;
                }
            } else {
                if (startPos <= endPos) {
                    startPos = i;
                }
            }

        }
        if (startPos > endPos) {
            styleRanges.add(getYellow(start + startPos, string1.length() - startPos));
        }
    }

    private StyleRange getYellow(int startPos, int length) {
        return new StyleRange(startPos, length, null, ROW_PART_COLOR);
    }

    private StyleRange getDarkYellow(int startPos, int length) {
        return new StyleRange(startPos, length, null, FULL_ROW_COLOR);
    }

    private int compareEpicsRecords(EpicsRecord epicsRecord1, EpicsRecord epicsRecord2) {
        return epicsRecord1.compareTo(epicsRecord2);
    }

}
