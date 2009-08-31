/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.config.ioconfig.config.view.helper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.csstudio.config.ioconfig.config.view.NodeConfig;
import org.csstudio.config.ioconfig.model.Facility;
import org.csstudio.config.ioconfig.model.Ioc;
import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.NodeImage;
import org.csstudio.config.ioconfig.model.Repository;
import org.csstudio.config.ioconfig.model.Keywords.GSDFileTyp;
import org.csstudio.config.ioconfig.model.pbmodel.Channel;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFile;
import org.csstudio.config.ioconfig.model.pbmodel.Master;
import org.csstudio.config.ioconfig.model.pbmodel.Module;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnet;
import org.csstudio.config.ioconfig.model.pbmodel.Slave;
import org.csstudio.config.ioconfig.view.Activator;
import org.csstudio.config.ioconfig.view.ProfiBusTreeView;
import org.csstudio.platform.security.SecurityFacade;
import org.csstudio.platform.security.User;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 20.06.2007
 */
public final class ConfigHelper {

    /**
     * @author hrickens
     * @author $Author$
     * @version $Revision$
     * @since 22.07.2009
     */
    private static final class SpinnerKeyListener implements KeyListener {
        private SpinnerModifyListener _modifyListener;

        public SpinnerKeyListener(SpinnerModifyListener modifyListener) {
            _modifyListener = modifyListener;
        }

        public void keyPressed(KeyEvent e) {
            Spinner spinner = (Spinner) e.widget;
            if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                _modifyListener.doIt();
                spinner.setSelection(spinner.getSelection());
//                _modifyListener.modifyText(new ModifyEvent(new Event()));
            } else if (e.keyCode == SWT.ESC) {
                spinner.setSelection(_modifyListener.getLastvalue());
                _modifyListener.doIt();
            } else {
                _modifyListener.doItNot();
            }

        }

        public void keyReleased(KeyEvent e) {
        }

    }

    /**
     * @author hrickens
     * @author $Author$
     * @version $Revision$
     * @since 22.07.2007
     */
    private static final class SpinnerModifyListener implements ModifyListener {
        private final ProfiBusTreeView _profiBusTreeView;
        private final Node _node;
        private final Spinner _indexSpinner;
        private boolean _doIt = true;
        private int _lastValue;

        private SpinnerModifyListener(ProfiBusTreeView profiBusTreeView, Node node,
                Spinner indexSpinner) {
            _profiBusTreeView = profiBusTreeView;
            _node = node;
            _indexSpinner = indexSpinner;
            _lastValue = indexSpinner.getSelection();
        }

        public int getLastvalue() {
            return _lastValue;
        }

        public void modifyText(final ModifyEvent e) {
            if (_doIt) {
                // TODO: Hier gibt es noch ein GDI Object leak.
                short index = (short) _indexSpinner.getSelection();
                _node.moveSortIndex(index);
                if (_node.getParent() != null) {
                    _profiBusTreeView.refresh(_node.getParent());
                } else {
                    _profiBusTreeView.refresh();
                }
                _lastValue = index;
            }
        }

        public void doIt() {
            _doIt = true;
        }

        public void doItNot() {
            _doIt = false;
        }
    }

    /**
     * 
     */
    private static GSDFile _gsdFile;

    /**
     * The standard Date format.
     */
    private static SimpleDateFormat _simpleDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss.S");

    private static List<GSDFile> _gsdFiles;

    /**
     * The Private Constructor.
     */
    private ConfigHelper() {
    }

    /**
     * @param head
     *            Headline for the Tab.
     * @param tabFolder
     *            The Tab Folder to add the Tab Item.
     * @param size
     *            the number of column
     * @return Tab Item Composite.
     */
    public static Composite getNewTabItem(final String head, final TabFolder tabFolder,
            final int size) {
        return getNewTabItem(head, tabFolder, size, null);
    }

    public static Composite getNewTabItem(final String head, final TabFolder tabFolder,
            final int size, Composite viewer) {
        final TabItem item = new TabItem(tabFolder, SWT.NONE);
        item.setText(head);
        Composite comp = new Composite(tabFolder, SWT.NONE);
        comp.setLayout(new GridLayout(size, true));
        item.setControl(comp);

        comp.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        if (viewer instanceof DocumentationManageView) {
            final DocumentationManageView docView = (DocumentationManageView) viewer;

            tabFolder.addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent e) {
                    docTabSelectionAction(e);
                }

                public void widgetSelected(SelectionEvent e) {
                    docTabSelectionAction(e);
                }

                private void docTabSelectionAction(SelectionEvent e) {
                    if (e.item.equals(item)) {
                        docView.onActivate();
                    }
                }

            });

        }
        return comp;
    }

    /**
     * 
     * @param tabFolder
     *            The Tab Folder to add the Tab Item.
     * @param head
     *            Headline for the Tab.
     * @param node
     *            that have a GSD File.
     * @param fileTyp
     *            The GSD File Type (Master, Slave),
     * @return Tab Item Composite.
     */
    public static Composite makeGSDFileChooser(final TabFolder tabFolder, final String head,
            final NodeConfig node, final Enum<GSDFileTyp> fileTyp) {
        int columnNum = 7;
        final Composite comp = ConfigHelper.getNewTabItem(head, tabFolder, columnNum);

        Group gSelected, gAvailable;
        final Text tSelected;
        final Button fileSelect;
        Button fileAdd, fileRemove;

        gSelected = new Group(comp, SWT.NONE);
        gSelected.setText("Selected GSD File:");
        gSelected.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, columnNum, 1));
        gSelected.setLayout(new GridLayout(1, false));

        tSelected = new Text(gSelected, SWT.SINGLE | SWT.BORDER);
        tSelected.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        if (node != null && node.getGSDFile() != null) {
            _gsdFile = node.getGSDFile();
            node.fill(_gsdFile);
            tSelected.setText(_gsdFile.getName());
        }

        gAvailable = new Group(comp, SWT.NONE);
        gAvailable.setText("Available GSD File:");
        gAvailable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, columnNum, 1));
        gAvailable.setLayout(new GridLayout(1, false));

        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        Composite tableComposite = new Composite(gAvailable, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tableComposite);
        tableComposite.setLayout(tableColumnLayout);

        final boolean master = fileTyp == GSDFileTyp.Master;
        final TableViewer tableViewer = new TableViewer(tableComposite, SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.MULTI | SWT.FULL_SELECTION);
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setSorter(new ViewerSorter() {
            @Override
            public int compare(final Viewer viewer, final Object e1, final Object e2) {
                if (e1 instanceof GSDFile && e2 instanceof GSDFile) {
                    GSDFile file1 = (GSDFile) e1;
                    GSDFile file2 = (GSDFile) e2;

                    // sort wrong files to back.
                    if (!(file1.isMasterNonHN() || file1.isSlaveNonHN())
                            && (file2.isMasterNonHN() || file2.isSlaveNonHN())) {
                        return -1;
                    } else if ((file1.isMasterNonHN() || file1.isSlaveNonHN())
                            && !(file2.isMasterNonHN() || file2.isSlaveNonHN())) {
                        return 1;
                    }

                    // if master -> master file to top
                    if (master) {
                        if (file1.isMasterNonHN() && !file2.isMasterNonHN()) {
                            return -1;
                        } else if (!file1.isMasterNonHN() && file2.isMasterNonHN()) {
                            return 1;
                        }
                    } else {
                        // if slave -> slave file to top
                        if (file1.isSlaveNonHN() && !file2.isSlaveNonHN()) {
                            return -1;
                        } else if (!file1.isSlaveNonHN() && file2.isSlaveNonHN()) {
                            return 1;
                        }
                    }
                    return file1.getName().compareToIgnoreCase(file2.getName());
                }
                return super.compare(viewer, e1, e2);
            }
        });
        tableViewer.setLabelProvider(new GSDLabelProvider(master));
        tableViewer.getTable().setHeaderVisible(false);
        tableViewer.getTable().setLinesVisible(false);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tableViewer.getTable());

        _gsdFiles = Repository.load(GSDFile.class);
        tableViewer.setInput(_gsdFiles.toArray(new GSDFile[_gsdFiles.size()]));

        new Label(comp, SWT.NONE);
        fileSelect = new Button(comp, SWT.PUSH);
        fileSelect.setText("Select");
        fileSelect.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        fileSelect.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
                doFileAdd();
            }

            public void widgetSelected(final SelectionEvent e) {
                doFileAdd();
            }

            private void doFileAdd() {
                if (node.fill(_gsdFile)) {
                    tSelected.setText(_gsdFile.getName());
                    node.setSavebuttonEnabled("GSDFile", true);
                }
            }

        });
        new Label(comp, SWT.NONE);
        new Label(comp, SWT.NONE);
        fileAdd = new Button(comp, SWT.PUSH);
        fileAdd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        fileAdd.setText("Add File");
        fileAdd.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
                doFileAdd();
            }

            public void widgetSelected(final SelectionEvent e) {
                doFileAdd();
            }

            private void doFileAdd() {
                FileDialog fd = new FileDialog(comp.getShell(), SWT.MULTI);
                fd.setFilterExtensions(new String[] { "*.gsd" });
                fd.setFilterPath("Z:\\Boeckmann\\GSD_Dateien\\");
                if (fd.open() != null) {
                    File path = new File(fd.getFilterPath());
                    for (String fileName : fd.getFileNames()) {
                        if (fileNotContain(fileName)) {
                            String text = file2String(new File(path, fileName));
                            File file = new File(path, fileName);
                            GSDFile gsdFile = new GSDFile(file.getName(), text.toString());
                            _gsdFiles.add(gsdFile);
                            tableViewer.setInput(_gsdFiles);
                            Repository.save(gsdFile);
                        } else {
                            MessageDialog.openInformation(tabFolder.getShell(), "Double GSD File",
                                    "File is already in the DB");
                        }
                    }
                }
            }

            private boolean fileNotContain(String fileName) {
                boolean add = true;
                for (GSDFile file : _gsdFiles) {
                    add = !file.getName().equals(fileName);
                    if (!add) {
                        break;
                    }
                }
                return add;
            }

        });
        fileRemove = new Button(comp, SWT.PUSH);
        fileRemove.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        fileRemove.setText("Remove File");
        fileRemove.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
            }

            public void widgetSelected(final SelectionEvent e) {
                StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
                GSDFile removeFile = (GSDFile) selection.getFirstElement();

                if (MessageDialog.openQuestion(node.getShell(), "Lösche Datei aus der Datenbank",
                        "Sind sie sicher das sie die Datei " + removeFile.getName()
                                + " löschen möchten")) {
                    Repository.removeGSDFiles(removeFile);
                    _gsdFiles.remove(removeFile);
                    tableViewer.setInput(_gsdFiles);
                }

            }
        });

        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                StructuredSelection selection = (StructuredSelection) event.getSelection();
                if (selection == null || selection.isEmpty()) {
                    fileSelect.setEnabled(false);
                    return;
                }
                GSDFile file = (GSDFile) selection.getFirstElement();
                fileSelect.setEnabled(master == file.isMasterNonHN());
            }

        });

        new Label(comp, SWT.NONE);

        return comp;

    }

    /**
     * Put a Text file into a String.
     * 
     * @param file
     *            the Text file.
     * @return the Text of the File.
     */
    public static String file2String(final File file) {
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String tmp;
            while ((tmp = br.readLine()) != null) {
                text = text.append(tmp + "\r\n");
            }
        } catch (FileNotFoundException e1) {
            // TODO Fehler händling!
            e1.printStackTrace();
        } catch (IOException e2) {
            // TODO Fehler händling!
            e2.printStackTrace();
        }
        return text.toString();
    }

    /**
     * @return the Default CSS SimpleDateFormat.
     */
    public static SimpleDateFormat getSimpleDateFormat() {
        return _simpleDateFormat;
    }

    /**
     * @return The CSS User-Name.
     */
    public static String getUserName() {
        User user = SecurityFacade.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUsername();
        }
        return "unknown";
    }

    /**
     * 
     * @param parent
     *            The Parent composite.
     * @param node
     *            The Node that index the Spinner modify.
     * @param modifyListener
     *            The ModifyListener to set the Save dirty bit.
     * @param label
     *            Label text for Spinner
     * @param profiBusTreeView
     *            IO Config TreeViewer.
     * @return the Sort Index Spinner.
     */
    public static Spinner getIndexSpinner(final Composite parent, final Node node,
            final ModifyListener modifyListener, final String label,
            final ProfiBusTreeView profiBusTreeView) {
        int min = 0;
        int max = 99;

        // Label
        Label slotIndexLabel = new Label(parent, SWT.NONE);
        slotIndexLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, false, false, 1, 1));
        slotIndexLabel.setText(label);
        // Spinner
        final Spinner indexSpinner = new Spinner(parent, SWT.WRAP);
        indexSpinner.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, false, false, 1, 1));
        if (node.getParent() instanceof Slave) {
            Slave slave = (Slave) node.getParent();
            max = slave.getMaxSize() - 1;
        } else if (node.getParent() instanceof Master) {
            max = ProfibusSubnet.MAX_STATION_ADDRESS;
        }
        indexSpinner.setMinimum(min);
        indexSpinner.setMaximum(max);
        indexSpinner.setSelection(node.getSortIndex());
        indexSpinner.setData((short) node.getSortIndex());
        indexSpinner.addModifyListener(modifyListener);
        SpinnerModifyListener spinnerModifyListener = new SpinnerModifyListener(profiBusTreeView,
                node, indexSpinner);
        SpinnerKeyListener keyListener = new SpinnerKeyListener(spinnerModifyListener);
        indexSpinner.addKeyListener(keyListener);
        indexSpinner.addModifyListener(spinnerModifyListener);
        return indexSpinner;
    }

    public static Image getImageFromNode(Node node) {
        return getImageFromNode(node, -1, -1);
    }

    public static Image getImageFromNode(Node node, int width, int height) {
        if (node != null) {
            NodeImage icon = node.getIcon();
            if (icon != null) {
                ByteArrayInputStream bais = new ByteArrayInputStream(icon.getImageBytes());
                Image image = new Image(null, bais);
                return image;
            }
            // Get Default Image
            if (node instanceof Facility) {
                return getImageMaxSize("icons/css.gif", width, height);
            } else if (node instanceof Facility) {
                return getImageMaxSize("icons/3055555W.bmp", width, height);
            } else if (node instanceof Ioc) {
                return getImageMaxSize("icons/Buskopan.bmp", width, height);
            } else if (node instanceof ProfibusSubnet) {
                return getImageMaxSize("icons/Profibus2020.bmp", width, height);
            } else if (node instanceof Master) {
                return getImageMaxSize("icons/ProfibusMaster2020.bmp", width, height);
            } else if (node instanceof Slave) {
                return getImageMaxSize("icons/sie80a6n.bmp", width, height);
            } else if (node instanceof Module) {
                return getImageMaxSize("icons/3055555W.bmp", width, height);
            } else if (node instanceof Channel) {
                Channel channel = (Channel) node;
                return getChannelImage(channel.isInput(), channel.isDigital(), width, height);
            }
        }
        return null;
    }

    public static Image getImageMaxSize(String imagePath, int width, int height) {
        ImageData imageData = CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
                Activator.PLUGIN_ID, imagePath).getImageData();
        if (width > 0 && height > 0) {
            int width2 = imageData.width;
            int height2 = imageData.height;

            if (width2 > width && height2 > height) {
                width2 = width;
                height2 = height;
            }

            return new Image(null, imageData.scaledTo(width2, height2));
        }
        return new Image(null, imageData);
    }

    private static Image getChannelImage(boolean isInput, boolean isDigital, int width, int height) {
        // DI
        if (isInput && !isDigital) {
            return getImageMaxSize("icons/Input_red16.png", width, height);
            // DO
        } else if (isInput && isDigital) {
            return getImageMaxSize("icons/Input_green16.png", width, height);
            // AI
        } else if (!isInput && !isDigital) {
            return getImageMaxSize("icons/Output_red16.png", width, height);
            // AO
        } else if (!isInput && isDigital) {
            return getImageMaxSize("icons/Output_green16.png", width, height);
        }

        return null;
    }
}
