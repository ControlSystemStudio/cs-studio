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
package org.csstudio.utility.adlconverter.ui;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Pattern;

import org.csstudio.ui.util.dialogs.ResourceSelectionDialog;
import org.csstudio.utility.adlconverter.Activator;
import org.csstudio.utility.adlconverter.fpcreator.Creator;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.ui.preferences.ADLConverterPreferenceConstants;
import org.csstudio.utility.adlconverter.utility.DebugHelper;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 16.08.2007
 */
public class ADLConverterMainView extends ViewPart {

    private static final Logger LOG = LoggerFactory.getLogger(ADLConverterMainView.class);
    
	/**
	 * Default Constructor.
	 */
	public ADLConverterMainView() {
	    // Default Constructor.
	}

	/**
	 * The target path selected by the user.
	 */
	private IPath _targetPath;

	/**
	 * A marker for the path Part.
	 */
	private int _pathPos = 0;

	/**
	 * The parent Shell.
	 */
	private Shell _shell;

	/**
	 * A CheckButton to switch on/off use relative Path.
	 */
	private Button _isRelativePath;

	/**
	 * A Text field with the relative Path.
	 */
	private Text _relativePathText;

	/**
	 * The Viewer with the list of the files to Convert.
	 */
	private ListViewer _avaibleFiles;

	private final LinkedList<File> _avaibleFilesList = new LinkedList<File>();

	/**
	 * The ADL Converter Preferences. Contain the different default path.
	 */
	private IPreferenceStore _preferences;

	/**
	 * A Label to show a path example.
	 */
	private Label _examplePathLabel;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void createPartControl(final Composite parent) {
		_shell = parent.getShell();
		_preferences = Activator.getDefault().getPreferenceStore();
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(parent, Activator.PLUGIN_ID + ".adl_converter"); //$NON-NLS-1$
		parent.setLayout(new GridLayout(1, true));

		// Source and Destination Groups
		Group sourceGroup = new Group(parent, SWT.NONE);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		layoutData.minimumHeight = 100;
		sourceGroup.setLayoutData(layoutData);
		sourceGroup.setLayout(new GridLayout(4, true));
		sourceGroup.setText(Messages.ADLConverterMainView_SourceGroup);

		Group destinationGroup = new Group(parent, SWT.NONE);
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		layoutData.minimumHeight = 40;
		destinationGroup.setLayoutData(layoutData);
		destinationGroup.setLayout(new GridLayout(2, false));
		destinationGroup
				.setText(Messages.ADLConverterMainView_DestinationGroup);

		IResource initial = ResourcesPlugin.getWorkspace().getRoot();
		generateSourceBlock(sourceGroup, initial);
		generateDestinationBlock(destinationGroup);
	}

	/**
	 * @param sourceGroup
	 *            the Parent composite.
	 * @param initial
	 *            The Workspace resource.
	 */
	private void generateSourceBlock(final Group sourceGroup,
			final IResource initial) {

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		gridData.minimumWidth = 40;
		gridData.minimumHeight = 40;

		_avaibleFiles = new ListViewer(sourceGroup);
		_avaibleFiles.getList().setLayoutData(gridData);
		_avaibleFiles.setContentProvider(new ArrayContentProvider());
		_avaibleFiles.setLabelProvider(new LabelProvider());

		makeMenu();
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1);
		gridData.minimumWidth = 40;
		gridData.minimumHeight = 40;
		Composite buttonComposite = new Composite(sourceGroup, SWT.NONE);
		buttonComposite.setLayoutData(gridData);
		// buttonComposite.setLayoutData(GridDataFactory.fillDefaults().span(4,
		// 1).create());
		buttonComposite.setLayout(new RowLayout());

		Button openSourceButton = new Button(buttonComposite, SWT.PUSH);
		openSourceButton.setLayoutData(new RowData(80, 25));
		openSourceButton
				.setText(Messages.ADLConverterMainView_ADLSourceFileDialogButton);

		Button subFolderButton = new Button(buttonComposite, SWT.PUSH);
		subFolderButton.setLayoutData(new RowData(80, 25));
		subFolderButton
				.setText(Messages.ADLConverterMainView_ADLSourceFolderDialogButton);

		Button clearSourceButton = new Button(buttonComposite, SWT.PUSH);
		clearSourceButton
				.setText(Messages.ADLConverterMainView_ClearButtonText);
		RowData rowData = new RowData(80, 25);
		clearSourceButton.setLayoutData(rowData);

		final Button convertButton = new Button(buttonComposite, SWT.PUSH);
		convertButton.setText(Messages.ADLConverterMainView_ConvcertButtonText);
		rowData = new RowData(80, 25);
		convertButton.setLayoutData(rowData);

		final Button generateFpButton = new Button(buttonComposite, SWT.PUSH);
		generateFpButton.setText("Faceplates");
		rowData = new RowData(80, 25);
		generateFpButton.setLayoutData(rowData);
		
		// Listener
		openSourceButton.addSelectionListener(new SelectionAdapter() {

			@Override
            public void widgetSelected(final SelectionEvent e) {
				FileDialog dialog = new FileDialog(_shell, SWT.MULTI);
				String[] names = new String[] {
						Messages.ADLConverterMainView_BothFileSourceDialogFileDes,
						Messages.ADLConverterMainView_ADLFileSourceDialogFileDes,
						Messages.ADLConverterMainView_MDPFileSourceDialogFileDes,
						Messages.ADLConverterMainView_STCFileSourceDialogFileDes,
						Messages.ADLConverterMainView_AllFileSourceDialogFileDes };
				dialog.setFilterNames(names);
				String[] ext = new String[] {
						"*.adl;*.mfp;*.stc", "*.adl", "*.mfp", "*.stc", "*.*" }; // Windows wild cards //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5 
				dialog.setFilterExtensions(ext);
				String path = _preferences
						.getString(ADLConverterPreferenceConstants.P_STRING_Path_Source);
				dialog.setFilterPath(path);
				dialog.open();
				path = dialog.getFilterPath();
				String[] files = dialog.getFileNames();
				for (String name : files) {
					_avaibleFilesList.add(new File(path, name));
				}
				
				setAvaibleFilesInput();
				_avaibleFiles.getList().selectAll();
				refreshexamplePathLabel();
				checkRelativPath();
			}

		});

		subFolderButton.addSelectionListener(new SelectionListener() {

			@Override
            public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
            public void widgetSelected(SelectionEvent e) {
				final DirectoryDialog dialog = new DirectoryDialog(_shell,
						SWT.MULTI);
				String path = _preferences
						.getString(ADLConverterPreferenceConstants.P_STRING_Path_Source);
				// path = initial.getProjectRelativePath().toOSString();
				System.out.println("Path_: " + path); //$NON-NLS-1$
				dialog.setFilterPath(path);
				String open = dialog.open();
				if (open == null) {
					return;
				}
				Job job = new Job("ADL File Reader") {

					@Override
					protected IStatus run(IProgressMonitor monitor) {
						monitor.beginTask("ADL FileReader Worker", -1);
						try {
							final String filterPath = dialog.getFilterPath();
							File file = new File(filterPath);
							fillFiles(file);
							PlatformUI.getWorkbench().getDisplay()
									.syncExec(new Runnable() {

										@Override
                                        public void run() {
											_avaibleFiles
													.setInput(_avaibleFilesList);
											_avaibleFiles.getList().selectAll();
											refreshexamplePathLabel();
											checkRelativPath();
											_preferences
													.setValue(
															ADLConverterPreferenceConstants.P_STRING_Path_Source,
															filterPath);
										}
									});
							monitor.done();
						} catch (Exception e1) {
							LOG.error("Error: ", e1);
							monitor.setCanceled(true);
							return Status.CANCEL_STATUS;
						}
						return Status.OK_STATUS;
					}
				};
				job.setUser(true);
				job.schedule();
			}

			protected void fillFiles(File file) {
				String[] list = file.list(new FilenameFilter() {

					@Override
                    public boolean accept(File dir, String name) {

						if (name.endsWith(".adl") || name.endsWith(".mfp") || name.endsWith(".stc")) { //$NON-NLS-1$
							return true;
						}
						File file2 = new File(dir, name);
						return file2.isDirectory();
					}

				});
				if (list != null) {
					for (String name : list) {
						File element = new File(file, name);
						if (element.isFile()) {
							_avaibleFilesList.add(element);
						} else {
							fillFiles(element);
						}
					}
				}
			}

		});

		clearSourceButton.addSelectionListener(new SelectionAdapter() {

			@Override
            public void widgetSelected(final SelectionEvent e) {
				_avaibleFilesList.clear();
				setAvaibleFilesInput();
				setPathPos(0);
			}

		});

		convertButton.addSelectionListener(new SelectionAdapter() {

			@Override
            @SuppressWarnings("unchecked")//$NON-NLS-1$
			public void widgetSelected(final SelectionEvent e) {
				StructuredSelection sel = (StructuredSelection) _avaibleFiles
						.getSelection();
				final ArrayList<Object> list = new ArrayList<Object>(sel
						.toList());
				Job job = new Job("Convered ADL Files") {

					@Override
					protected IStatus run(final IProgressMonitor monitor) {
						final int startSize = list.size();
						int adlCount = 0;
						int mpfCout = 0;
						int stcCount = 0;
						DebugHelper.clear();
						monitor.beginTask("Start Convert Files", startSize);
						ADLDisplayImporter.reset();
						while (list.size() > 0) {
							final ADLDisplayImporter di = new ADLDisplayImporter();
							final File file = (File) list.remove(0);
							// IPath targetProject;
							String format = String.format(
									"Convert File: %1$40s\t(%2$4d/%3$4d)",
									file.getName(), +(startSize - list.size()),
									startSize);
							monitor.setTaskName(format);
							// monitor.setTaskName("Convert File: "+file.getName()+"\t("+(startSize-list.size())+"/"+startSize+")");
							final SelectContainer selection = new SelectContainer();
							PlatformUI.getWorkbench().getDisplay()
									.syncExec(new Runnable() {

										@Override
                                        public void run() {
											selection.setBool(_isRelativePath
													.getSelection());
										}
									});
							if (selection.getBool()) {
								selection.setiPath(getRelativPath(file)
										.removeLastSegments(1));
							} else {
								selection.setiPath(initial
										.getProjectRelativePath().append(
												_targetPath));
							}
							try {
								if (file.getName().endsWith(".adl")) {//$NON-NLS-1$
									adlCount++;
									PlatformUI.getWorkbench().getDisplay()
											.syncExec(new Runnable() {
												@Override
                                                public void run() {
													try {
														if (!di.importDisplay(
																file.getAbsolutePath(),
																selection
																		.getiPath(),
																file.getName()
																		.replace(
																				".adl", ".css-sds"))) { //$NON-NLS-1$ //$NON-NLS-2$
														}
													} catch (CoreException e1) {
														// TODO Auto-generated
														// catch block
													    LOG.error("Error: ", e1);
													}
												}
											});
									if (di.getStatus() == 5) {
										// Job is canceled.
										break;
									}
								} else if (file.getName().endsWith(".mfp")) {//$NON-NLS-1$
									mpfCout++;
									PlatformUI.getWorkbench().getDisplay()
											.syncExec(new Runnable() {
												@Override
                                                public void run() {
													try {
														if (!di.importFaceplate(
																file.getAbsolutePath(),
																selection
																		.getiPath(),
																file.getName()
																		.concat(".css-sds"))) { //$NON-NLS-1$ //$NON-NLS-2$
														}
													} catch (CoreException ce) {
														// TODO Auto-generated
														// catch block
													    LOG.error("Error: ", ce);
													}
												}
											});
									if (di.getStatus() == 5) {
										// Job is canceled.
										break;
									}
								} else if (file.getName().endsWith(".stc")) {//$NON-NLS-1$
									stcCount++;
									PlatformUI.getWorkbench().getDisplay()
											.syncExec(new Runnable() {
												@Override
                                                public void run() {

													// parse Strip Tool Files
													try {
														if (!di.importStripTool(
																file.getAbsolutePath(),
																selection
																		.getiPath(),
																file.getName()
																		.replace(
																				".stc", ".css-plt"))) { //$NON-NLS-1$ //$NON-NLS-2$
														}
													} catch (CoreException ce) {
														// TODO Auto-generated
														// catch block
													    LOG.error("Error: ", ce);
													} catch (IOException ioE) {
														// TODO Auto-generated
														// catch block
													    LOG.error("Error: ", ioE);
													}
												}
											});
									if (di.getStatus() == 5) {
										// Job is canceled.
										break;
									}
								}
							} catch (Exception e1) {
								LOG.error("Error: ", e1);
							}
							// file = null;

							PlatformUI.getWorkbench().getDisplay()
									.syncExec(new Runnable() {

										@Override
                                        public void run() {
											_avaibleFiles.setSelection(
													new StructuredSelection(
															list), true);
											_avaibleFiles.getList().getParent()
													.layout();
										}
									});
							monitor.worked(1);

						}
						DebugHelper.showAll();
						DebugHelper.showPath();
						DebugHelper.showWithoutPath();
						DebugHelper.showAllCaller();
						DebugHelper.showPathCaller();
						DebugHelper.closeLogFile();
						final int adlCount2 = adlCount;
						final int mpfCount2 = mpfCout;
						final int stcCount2 = stcCount;
						PlatformUI.getWorkbench().getDisplay()
								.syncExec(new Runnable() {
									@Override
                                    public void run() {

										MessageDialog.openInformation(
												convertButton.getShell(),
												"Finish",
												String.format(
														"Es waren %1$s Datein Ausgewählt.\r\n"
																+ "Es wurden \r\n"
																+ "\tADL Files:\t%2$4s\r\n"
																+ "\tMPF Files:\t%3$4s\r\n"
																+ "\tSTC Files:\t%4$4s\r\n"
																+ "Convertiert",
														startSize, adlCount2,
														mpfCount2, stcCount2));
									}
								});

						return Status.OK_STATUS;
					}

				};
				job.setUser(true);
				job.schedule();
			}
		});

		generateFpButton.addSelectionListener(new SelectionAdapter() {
		    
		    @Override
		    public void widgetSelected(final SelectionEvent e) {
		        Creator.createDisplays();
		    }
		    
		});
		
	}

	private void makeMenu() {
		Menu menu = new Menu(_avaibleFiles.getControl());
		makeRemoveSelection(menu);
		makeRomoveBak(menu);
		makeRomoveADL(menu);
		makeRomoveMFP(menu);
		makeRomoveSTC(menu);

		_avaibleFiles.getList().setMenu(menu);
	}

	private void makeRomoveSTC(Menu menu) {
		MenuItem showItem = new MenuItem(menu, SWT.PUSH);
		showItem.addSelectionListener(new SelectionListener() {

			@Override
            public void widgetSelected(SelectionEvent e) {
				removeSTC();
			}

			@Override
            public void widgetDefaultSelected(SelectionEvent e) {
				removeSTC();
			}

			private void removeSTC() {
				Iterator<File> iterator = _avaibleFilesList.iterator();
				while (iterator.hasNext()) {
					File file = (File) iterator.next();
					if (file != null) {
						String lowerCase = file.getName().toLowerCase();
						if (lowerCase.endsWith(".stc")) { //$NON-NLS-1$ //$NON-NLS-2$
							iterator.remove();
						}
					}
				}
				setAvaibleFilesInput();
			}
		});
		showItem.setText(Messages.ADLConverterMainView_RemoveSTCFiles);
		showItem.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_ELCL_REMOVEALL));

	}

	private void makeRomoveMFP(Menu menu) {
		MenuItem showItem = new MenuItem(menu, SWT.PUSH);
		showItem.addSelectionListener(new SelectionListener() {

			@Override
            public void widgetSelected(SelectionEvent e) {
				removeMFP();
			}

			@Override
            public void widgetDefaultSelected(SelectionEvent e) {
				removeMFP();
			}

			private void removeMFP() {
				Iterator<File> iterator = _avaibleFilesList.iterator();
				while (iterator.hasNext()) {
					File file = (File) iterator.next();
					if (file != null) {
						String lowerCase = file.getName().toLowerCase();
						if (lowerCase.endsWith(".mfp")) { //$NON-NLS-1$ //$NON-NLS-2$
							iterator.remove();
						}
					}
				}
				setAvaibleFilesInput();
			}
		});
		showItem.setText(Messages.ADLConverterMainView_RemoveMFPFiles);
		showItem.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_ELCL_REMOVEALL));
	}

	private void makeRomoveADL(Menu menu) {
		MenuItem showItem = new MenuItem(menu, SWT.PUSH);
		showItem.addSelectionListener(new SelectionListener() {

			@Override
            public void widgetSelected(SelectionEvent e) {
				removeADL();
			}

			@Override
            public void widgetDefaultSelected(SelectionEvent e) {
				removeADL();
			}

			private void removeADL() {
				Iterator<File> iterator = _avaibleFilesList.iterator();
				while (iterator.hasNext()) {
					File file = (File) iterator.next();
					if (file != null) {
						String lowerCase = file.getName().toLowerCase();
						if (lowerCase.endsWith(".adl")) { //$NON-NLS-1$ //$NON-NLS-2$
							iterator.remove();
						}
					}
				}
				setAvaibleFilesInput();
			}
		});
		showItem.setText(Messages.ADLConverterMainView_RemoveADLFiles);
		showItem.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_ELCL_REMOVEALL));
	}

	private void makeRomoveBak(Menu menu) {
		MenuItem showItem = new MenuItem(menu, SWT.PUSH);
		showItem.addSelectionListener(new SelectionListener() {

			@Override
            public void widgetSelected(SelectionEvent e) {
				removeBak();
			}

			@Override
            public void widgetDefaultSelected(SelectionEvent e) {
				removeBak();
			}

			private void removeBak() {
				Iterator<File> iterator = _avaibleFilesList.iterator();
				while (iterator.hasNext()) {
					File file = (File) iterator.next();
					if (file != null) {
						String lowerCase = file.getName().toLowerCase();
						if (lowerCase.contains("_bak") || lowerCase.contains("-bak") || lowerCase.contains(".bak") //$NON-NLS-1$ //$NON-NLS-2$
								|| lowerCase.contains("_neu") || lowerCase.contains("-neu") || lowerCase.contains(".neu") //$NON-NLS-1$ //$NON-NLS-2$
								|| lowerCase.contains("_new") || lowerCase.contains("-new") || lowerCase.contains(".new") //$NON-NLS-1$ //$NON-NLS-2$
								|| lowerCase.contains("_alt") || lowerCase.contains("-alt") || lowerCase.contains(".alt") //$NON-NLS-1$ //$NON-NLS-2$
								|| lowerCase.contains("_old") || lowerCase.contains("-old") || lowerCase.contains(".old")) { //$NON-NLS-1$ //$NON-NLS-2$
							iterator.remove();
						}
					}
				}
				setAvaibleFilesInput();
			}

		});
		showItem.setText(Messages.ADLConverterMainView_RemoveBakFiles);
		showItem.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_ELCL_REMOVEALL));
	}

	private void makeRemoveSelection(Menu menu) {

		MenuItem showItem = new MenuItem(menu, SWT.PUSH);
		showItem.addSelectionListener(new SelectionListener() {

			@Override
            public void widgetSelected(SelectionEvent e) {
				StructuredSelection selection = (StructuredSelection) _avaibleFiles
						.getSelection();
				_avaibleFiles.remove(selection.toArray());
			}

			@Override
            public void widgetDefaultSelected(SelectionEvent e) {
				StructuredSelection selection = (StructuredSelection) _avaibleFiles
						.getSelection();
				_avaibleFiles.remove(selection.toArray());
			}

		});
		showItem.setText(Messages.ADLConverterMainView_RemoveSelectedFiels);
		showItem.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_ELCL_REMOVE));

	}

	/**
	 * @param destinationGroup
	 *            the Parent composite.
	 * @param initial
	 */
	private void generateDestinationBlock(final Group destinationGroup) {
		// Destination ui elements.

		// first row
		Button openTargetButton = new Button(destinationGroup, SWT.PUSH);
		openTargetButton
				.setText(Messages.ADLConverterMainView_TargetOpenButton);
		openTargetButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false,
				false, 1, 1));

		final Text pathText = new Text(destinationGroup, SWT.BORDER);
		pathText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,
				1));
		_targetPath = new Path(
				_preferences
						.getString(ADLConverterPreferenceConstants.P_STRING_Path_Target));
		pathText.setText(_targetPath.toString());

		// second row
		Composite relativPathComp = new Composite(destinationGroup, SWT.NONE);
		relativPathComp.setLayoutData(GridDataFactory.fillDefaults().span(2, 1)
				.create());
		relativPathComp.setLayout(new GridLayout(2, false));

		Label label = new Label(relativPathComp, SWT.WRAP | SWT.READ_ONLY);
		label.setText(Messages.ADLConverterMainView_PathPrefix);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 2));

		// third row
		_isRelativePath = new Button(relativPathComp, SWT.CHECK);
		_isRelativePath.setText(""); //$NON-NLS-1$

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gridData.minimumWidth = 20;

		_relativePathText = new Text(relativPathComp, SWT.SINGLE | SWT.BORDER
				| SWT.READ_ONLY | SWT.SEARCH);
		_relativePathText.setLayoutData(gridData);
		_relativePathText
				.setToolTipText(Messages.ADLConverterMainView_PathPrefixToolTip);
		// _relativePathText.setText(_preferences
		// .getString(ADLConverterPreferenceConstants.P_STRING_Path_Relativ_Target));

		_examplePathLabel = new Label(relativPathComp, SWT.NONE);
		_examplePathLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 2, 1));

		// Listener

		openTargetButton.addSelectionListener(new SelectionAdapter() {

			@Override
            public void widgetSelected(final SelectionEvent e) {
				ResourceSelectionDialog dialog = new ResourceSelectionDialog(
						_shell,
						Messages.ADLConverterMainView_TargetFolderSelectionMessage,
						null);
				IPath path = new Path(pathText.getText());
				dialog.setSelectedResource(path);
				if (dialog.open() == Window.OK) {
					path = dialog.getSelectedResource();
					if (path != null) {
						_targetPath = path;
						pathText.setText(path.toString());
						refreshexamplePathLabel();
					}
				}
			}
		});

		_isRelativePath.addSelectionListener(new SelectionListener() {

			@Override
            public void widgetDefaultSelected(final SelectionEvent e) {
				setBackground();
			}

			@Override
            public void widgetSelected(final SelectionEvent e) {
				setBackground();
			}

			private void setBackground() {
				if (_isRelativePath.getSelection()) {
					_relativePathText.setBackground(Display.getDefault()
							.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
				} else {
					_relativePathText.setBackground(Display.getDefault()
							.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
				}
			}

		});

		_relativePathText.addKeyListener(new KeyAdapter() {
			@Override
            public void keyReleased(final KeyEvent e) {
				String[] pathPart;
				if (_isRelativePath.getSelection()) {
					if (e.keyCode == SWT.ARROW_LEFT) {
						if (getPathPos() > 0) {
							setPathPos(getPathPos() - 1);
							File file = _avaibleFilesList.getFirst();
							pathPart = file.getAbsolutePath().split(
									Pattern.quote(File.separator));
							if (pathPart.length > getPathPos()) {
								if (getPathPos() == 0) {
									_relativePathText.setText(_relativePathText
											.getText().replace(
													pathPart[getPathPos()], "")); //$NON-NLS-1$
								} else {
									_relativePathText
											.setText(_relativePathText
													.getText()
													.replace(
															File.separator
																	+ pathPart[getPathPos()],
															"")); //$NON-NLS-1$
								}
								refreshexamplePathLabel();
							}
						}
					} else if (e.keyCode == SWT.ARROW_RIGHT) {
						File file = _avaibleFilesList.getFirst();
						if (file != null) {
							pathPart = file.getAbsolutePath().split(
									Pattern.quote(File.separator));
							if (getPathPos() < pathPart.length - 1) {
								if (getPathPos() == 0) {
									_relativePathText
											.append(pathPart[getPathPos()]);
								} else {
									_relativePathText.append(File.separator
											+ pathPart[getPathPos()]);
								}
								refreshexamplePathLabel();
								setPathPos(getPathPos() + 1);
							}
						}
					}
				}
			}

		});
	}

	/**
	 * Extract the relative path part from example file path.
	 * 
	 * @param file
	 *            the example file.
	 * @return the relative path.
	 */
	private IPath getRelativPath(final File file) {
		final SelectContainer container = new SelectContainer();
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
            public void run() {
				String apsolutPath = Pattern.quote(_relativePathText.getText());
				container.setiPath(_targetPath.append(file.getAbsolutePath()
						.replaceFirst(apsolutPath, ""))); //$NON-NLS-1$
			}
		});
		return container.getiPath();
	}

	/**
	 * Refresh the path example label.
	 */
	private void refreshexamplePathLabel() {
		IPath relPath = getRelativPath(_avaibleFilesList.getFirst());
		_examplePathLabel.setText(relPath.toOSString());
	}

	private void checkRelativPath() {
		File path = _avaibleFilesList.getFirst();
		if (!path.getAbsolutePath().contains(_relativePathText.getText())) {
			_relativePathText.setText(""); //$NON-NLS-1$
		}
	}

	protected void setAvaibleFilesInput() {
        _avaibleFiles.setInput(_avaibleFilesList);                
    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
	    // nothing to do.
	}

    public void setPathPos(int pathPos) {
        _pathPos = pathPos;
    }

    public int getPathPos() {
        return _pathPos;
    }

}
