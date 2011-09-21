/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.displaybrowser.ui;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.utility.displaybrowser.DisplaySearch;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @since 13.09.2011
 */
public class SearchView extends ViewPart implements Observer {

    public static final String ID = "org.csstudio.utility.displaybrowser.ui.SearchView";
    private StyledText _resultStyledText;
    private Text _timeText;
    private Date _startTime;
    private ProgressBar _pb;
    private Text _searchText;

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(final Composite parent) {
        parent.setLayout(new GridLayout(4, false));

        final Label source = new Label(parent, SWT.NONE);
        source.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        source.setText("Source");
        final Text sourceText = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        sourceText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        final Button sourceSelectionButton = new Button(parent, SWT.PUSH);
        sourceSelectionButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        sourceSelectionButton.setText("Select Source");

        final Label search = new Label(parent, SWT.NONE);
        search.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        search.setText("Search");
        _searchText = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        _searchText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        _searchText.setMessage("regex");
        final Button searchButton = new Button(parent, SWT.PUSH);
        searchButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        searchButton.setText("Search");

        final Label filesCount = new Label(parent, SWT.NONE);
        filesCount.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        filesCount.setText("Searched Files");
        final Text searchedFilesText = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.READ_ONLY
                | SWT.BORDER);
        searchedFilesText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        final Label time = new Label(parent, SWT.NONE);
        time.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        time.setText("Time to Search");
        _timeText = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.READ_ONLY | SWT.BORDER);
        _timeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        _pb = new ProgressBar(parent, SWT.HORIZONTAL);
        _pb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1));
        _pb.setVisible(false);

        _resultStyledText = new StyledText(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY);
        _resultStyledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));

        sourceSelectionButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void widgetSelected(final SelectionEvent e) {

                final ElementTreeSelectionDialog fileDialog = new ElementTreeSelectionDialog(parent
                        .getShell(), new WorkbenchLabelProvider(), new WorkbenchContentProvider());
                //                if (startingDirectory != null) {
                //                    fileDialog.setInitialSelection(startingDirectory);
                //                } else if (_filterPath != null) {
                //                    fileDialog.setInitialSelection(_filterPath.getFullPath());
                //                }
                fileDialog.addFilter(new ViewerFilter() {

                    @Override
                    public boolean select(@Nonnull final Viewer viewer,
                                          @Nonnull final Object parentElement,
                                          @Nullable final Object element) {
                        if (element instanceof IFile) {
                            final IFile file = (IFile) element;
                            return file.getFileExtension().equals("css-sds");
                        }
                        return true;
                    }
                });
                final IWorkspace workspace = ResourcesPlugin.getWorkspace();
                fileDialog.setInput(workspace.getRoot());
                final int status = fileDialog.open();
                searchedFilesText.setText(" --- ");
                _timeText.setText(" --- ");
                _resultStyledText.setText("");
                if (status == fileDialog.OK) {
                    final Object[] result = fileDialog.getResult();
                    sourceText.setText(Arrays.toString(result));
                    sourceText.setData(result);
                }
            }

        });

        searchButton.addSelectionListener(new SelectionListener() {
            boolean search = true;
            private DisplaySearch _displaySearch;

            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (search) {
                    searchButton.setText("Stop");
                    search();
                    search = false;
                } else {
                    searchButton.setText("Serach");
                    stop();
                    search = true;
                }
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                if (search) {
                    searchButton.setText("Stop");
                    search();
                    search = false;
                } else {
                    searchButton.setText("Serach");
                    stop();
                    search = true;
                }
            }

            private void stop() {
                if (_displaySearch != null) {
                    _displaySearch.stopSearch();
                }
                update(null, false);
            }

            private void search() {
                final Object data = sourceText.getData();
                if (data instanceof Object[]) {
                    _startTime = new Date();
                    final Object[] result = (Object[]) data;
                    final Set<File> files = fileTreeWorker(result);
                    _pb.setMinimum(0);
                    _pb.setMaximum(files.size());
                    _pb.setSelection(0);
                    _pb.setVisible(true);
                    searchedFilesText.setText("" + files.size());
                    _resultStyledText.setText("");
                    _displaySearch = new DisplaySearch(files, _searchText.getText());
                    _displaySearch.addObserver(SearchView.this);
                    _displaySearch.startSearch();
                }
            }

            /**
             * @param result
             */
            private Set<File> fileTreeWorker(final Object[] pathAndFiles) {
                final Set<File> result = new HashSet<File>();
                if (pathAndFiles != null) {
                    for (final Object object : pathAndFiles) {
                        if (object instanceof IFolder) {
                            final IFolder folder = (IFolder) object;
                            final File[] listFiles = folder.getLocation().toFile().listFiles();
                            result.addAll(fileTreeWorker(listFiles));
                        } else if (object instanceof IProject) {
                            final IContainer container = (IProject) object;
                            final File[] listFiles = container.getLocation().toFile().listFiles();
                            result.addAll(fileTreeWorker(listFiles));
                        } else if (object instanceof IPath) {
                            final IPath path = (IPath) object;
                            final File[] listFiles = path.toFile().listFiles();
                            result.addAll(fileTreeWorker(listFiles));
                        } else if (object instanceof IFile) {
                            final IFile file = (IFile) object;
                            if (file.getFileExtension().toLowerCase().equals("css-sds")) {
                                result.add(file.getLocation().toFile());
                            }
                        } else if (object instanceof File) {
                            final File file = (File) object;
                            if (file.isDirectory()) {
                                final File[] listFiles = file.listFiles();
                                result.addAll(fileTreeWorker(listFiles));
                            } else if (file.isFile() && file.getName().endsWith(".css-sds")) {
                                result.add(file);
                            }
                        }
                    }
                }
                return result;
            }
        });

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final Observable arg0, final Object arg1) {
        if (arg1 instanceof Integer) {
            _pb.setSelection((Integer) arg1);
            return;
        }
        if (arg1 instanceof Map) {
            final StringBuilder sb = new StringBuilder();
            final Map<IProcessVariableAddress, File> resulte = new HashMap<IProcessVariableAddress, File>((Map<IProcessVariableAddress, File>) arg1);
            final Set<Entry<IProcessVariableAddress, File>> entrySet = resulte.entrySet();
            for (final Entry<IProcessVariableAddress, File> entry : entrySet) {
                sb.append(String.format("%-24s @ %-40s", entry.getKey().getProperty().trim(),entry.getValue().getName())).append("\r");
            }
            _resultStyledText.setText(sb.toString());
        }
        if (arg1 instanceof Boolean) {
            final Boolean done = (Boolean) arg1;
            final Date endSearchDate = new Date();
            final String text = _resultStyledText.getText();
            if (text.length() == 0 && done) {
                _resultStyledText.setText("not found");
            } else if (!done) {
                String text2 = _resultStyledText.getText();
                text2 = text2 + "search canceld!";
                //                    _resultStyledText.append("\n\n\tsearch canceled!");
                _resultStyledText.setText(text2);

            }
            _timeText.setText("" + (endSearchDate.getTime() - _startTime.getTime()));
            _pb.setVisible(false);
        }
    }

    /**
     * @param name
     */
    public void setFilter(final String filter) {
        _searchText.setText(filter);
    }

}
