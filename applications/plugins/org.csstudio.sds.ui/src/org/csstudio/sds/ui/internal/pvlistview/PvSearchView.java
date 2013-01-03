package org.csstudio.sds.ui.internal.pvlistview;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.sds.internal.persistence.DisplayModelLoadAdapter;
import org.csstudio.sds.ui.DisplayInfoService;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.internal.editor.DisplayEditor;
import org.csstudio.sds.ui.internal.pvlistview.preferences.PvSearchFolderPreferenceItem;
import org.csstudio.sds.ui.internal.pvlistview.preferences.PvSearchFolderPreferenceService;
import org.csstudio.ui.util.dnd.ControlSystemDropTarget;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PvSearchView extends ViewPart {

	public static final String VIEW_ID = "org.csstudio.sds.ui.internal.pvlistview.PvSearchView";
	
	private static final Logger LOG = LoggerFactory.getLogger(PvSearchView.class);

	private Button searchButton;
	private Text searchTextfield;
	private ProgressBar progressBar;
	private TreeViewer treeViewer;

	private final PvSearchFolderPreferenceService pvSearchFolderPreferenceService;

	private boolean isSearching = false;

	private SortedSet<IProcessVariableAddress> rootElements;
	private Map<IProcessVariableAddress, Set<File>> treeElements;
	private Pattern searchRegex;

	private Job searchJob;

	private Button searchFoldersCheckbox;
	private Text searchFoldersTextBox;

	public PvSearchView() {
		pvSearchFolderPreferenceService = SdsUiPlugin.getDefault()
				.getPvSearchFolderPreferenceService();

		rootElements = new TreeSet<IProcessVariableAddress>(
				new Comparator<IProcessVariableAddress>() {

					@Override
					public int compare(IProcessVariableAddress pv1,
							IProcessVariableAddress pv2) {
						return pv1.getRawName().compareTo(pv2.getRawName());
					}
				});
		treeElements = new HashMap<IProcessVariableAddress, Set<File>>();
	}

	@Override
	public void createPartControl(Composite parent) {
		final Composite main = parent;
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 1;
		gridLayout.horizontalSpacing = 2;
		main.setLayout(gridLayout);

		searchButton = new Button(main, SWT.NONE);
		GridData buttonData = new GridData(SWT.DEFAULT, SWT.CENTER, false,
				false);
		searchButton.setLayoutData(buttonData);
		searchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onToggleSearch();
			}

		});
		searchButton.setText("Search");

		searchTextfield = new Text(main, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).applyTo(searchTextfield);
		searchTextfield.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR || e.keyCode == SWT.LF) {
					if (!searchTextfield.getText().isEmpty()) {
						onToggleSearch();
					}
				}
			}
		});

		new ControlSystemDropTarget(searchTextfield, ProcessVariable.class,
				String.class) {

			@Override
			public void handleDrop(Object item) {
				if (item instanceof ProcessVariable) {
					final ProcessVariable pvs = (ProcessVariable) item;
					searchTextfield.setText((String) pvs.getName());
				}
			}
		};

		searchFoldersCheckbox = new Button(main, SWT.CHECK);
		searchFoldersCheckbox.setText("Standard folders");
		GridDataFactory.fillDefaults().grab(false, false).span(2, 1)
				.applyTo(searchFoldersCheckbox);

		searchFoldersTextBox = new Text(main, SWT.BORDER);
		final GridData searchFoldersTextBoxLayoutData = GridDataFactory.fillDefaults()
				.grab(true, false).span(2, 1).create();
		searchFoldersTextBox.setLayoutData(searchFoldersTextBoxLayoutData);
		searchFoldersTextBoxLayoutData.heightHint = 0;
		searchFoldersTextBox.setVisible(false);

		searchFoldersCheckbox.setSelection(true);
		searchFoldersCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isEnabled = searchFoldersCheckbox.getSelection();
				if (isEnabled) {
					searchFoldersTextBoxLayoutData.heightHint = 0;
					searchFoldersTextBox.setVisible(false);
				} else {
					searchFoldersTextBoxLayoutData.heightHint = -1;
					searchFoldersTextBox.setVisible(true);
					StringBuilder searchFoldersInPreferencesString = new StringBuilder();
					List<PvSearchFolderPreferenceItem> pvSearchItems = pvSearchFolderPreferenceService.loadPvSearchItems();
					for (int index = 0; index < pvSearchItems.size() ; index++) {
						PvSearchFolderPreferenceItem pvSearchFolderPreferenceItem = pvSearchItems.get(index);
						if(pvSearchFolderPreferenceItem.isChecked()) {
							searchFoldersInPreferencesString.append(pvSearchFolderPreferenceItem.getFolderPath());
								searchFoldersInPreferencesString.append("; ");
						}
					}
					searchFoldersTextBox.setText(searchFoldersInPreferencesString.toString().trim());
				}
				main.layout();
			}
		});
		progressBar = new ProgressBar(main, SWT.FILL | SWT.BORDER);
		GridData progressBarLayoutData = GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create();
		progressBarLayoutData.heightHint = 0;
		progressBar.setLayoutData(progressBarLayoutData);
		
		treeViewer = new TreeViewer(main, SWT.VIRTUAL | SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1)
				.applyTo(treeViewer.getControl());
		treeViewer.setContentProvider(new ITreeContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

			@Override
			public void dispose() {
			}

			@Override
			public boolean hasChildren(Object element) {
				boolean result = false;
				if (element instanceof IProcessVariableAddress) {
					synchronized (rootElements) {
						result = treeElements.get(element).size() > 1;
					}
				}
				return result;
			}

			@Override
			public Object getParent(Object element) {
				Object result = null;
				if (element instanceof File) {
					for (IProcessVariableAddress pvAddress : rootElements) {
						if (treeElements.get(pvAddress).contains(element)) {
							result = pvAddress;
							break;
						}
					}
				}
				return result;
			}

			@Override
			public IProcessVariableAddress[] getElements(Object arg) {
				IProcessVariableAddress[] result = null;
				synchronized (rootElements) {
					result = rootElements
							.toArray(new IProcessVariableAddress[0]);
				}
				return result;
			}

			@Override
			public File[] getChildren(Object parentElement) {
				File[] result = null;
				if (parentElement instanceof IProcessVariableAddress) {
					synchronized (rootElements) {
						result = treeElements.get(parentElement).toArray(
								new File[0]);
					}
				}
				return result;
			}
		});

		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				try {
					ITreeSelection selection = (ITreeSelection) event
							.getSelection();
					Object firstElement = selection.getFirstElement();
					if (firstElement instanceof File) {
						TreePath treePath = selection.getPathsFor(firstElement)[0];
						IProcessVariableAddress pvAddress = (IProcessVariableAddress) treePath
								.getFirstSegment();

						openDisplayInEditor((File) firstElement, pvAddress);
					} else if (firstElement instanceof IProcessVariableAddress
							&& rootElements.contains(firstElement)) {
						if (treeElements.get(firstElement).size() > 1) {
							treeViewer.expandToLevel(firstElement,
									TreeViewer.ALL_LEVELS);
						} else {
							Set<File> set = treeElements.get(firstElement);
							openDisplayInEditor(set.iterator().next(),
									(IProcessVariableAddress) firstElement);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			private void openDisplayInEditor(File selectedFile,
					final IProcessVariableAddress pvAddress)
					throws PartInitException {
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot root = workspace.getRoot();
				IFile file = root.getFileForLocation(new Path(selectedFile
						.getAbsolutePath()));
				IEditorInput editorInput = new FileEditorInput(file);
				IWorkbenchWindow window = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
				final IWorkbenchPage page = window.getActivePage();
				final DisplayEditor editor = (DisplayEditor) page.openEditor(
						editorInput,
						"org.csstudio.sds.ui.internal.editor.DisplayEditor");
				editor.addModelLoadedListener(new DisplayModelLoadAdapter() {
					@Override
					public void onDisplayModelLoaded() {
						PvListView pvListView;
						try {
							pvListView = (PvListView) PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getActivePage()
									.showView(PvListView.VIEW_ID);
							pvListView.selectPv(pvAddress, editor);
							// make sure that editor gets focus from click events
							page.activate(editor);
						} catch (PartInitException e) {
							LOG.error(e.getLocalizedMessage());
						}
					}
				});

			}
		});

		treeViewer.setLabelProvider(new LabelProvider() {

			private String getWorkspaceRelativePath(File file) {

				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot root = workspace.getRoot();
				IPath rootPath = root.getLocation();
				Path path = new Path(file.getAbsolutePath());
				return path.makeRelativeTo(rootPath).toString();
			}

			@Override
			public String getText(Object element) {
				if (element instanceof File) {
					return getWorkspaceRelativePath((File) element);
				}
				return super.getText(element);
			}
		});

		treeViewer.setInput(rootElements);
	}

	public void searchFor(String searchString) {
		if (isSearching) {
			cancelSearch();
		}
		searchTextfield.setText(searchString);
		doSearch(searchString);
	}

	protected void cancelSearch() {
		searchButton.setText("Search");

		if (searchJob != null && isSearching) {
			searchJob.cancel();
			handleSearchFinished();
		}
	}

	protected void doSearch(String searchString) {
		final List<File> allFiles;
		try {
			// Configure cancel button
			searchButton.setText("Cancel");

			// Clear previous results
			rootElements.clear();
			treeElements.clear();
			treeViewer.setInput(rootElements);

			allFiles = getAllFiles();
			progressBar.setMaximum(allFiles.size());
			((GridData) progressBar.getLayoutData()).heightHint = -1;
			progressBar.getParent().layout();

			searchRegex = buildSearchRegex(searchString);
			isSearching = true;

			// Start search
			final DisplayInfoService displayInfoService = new DisplayInfoService();
			searchJob = new Job("SDS Display Search") {

				private final static int REFRESH_UI_DELAY_MILLISECONDS = 2000;
				private final static int UPDATE_PROGRESS_THRESHOLD = 100;

				@Override
				protected IStatus run(final IProgressMonitor monitor) {
					monitor.beginTask("Process Variable Search",
							allFiles.size());

					int searchMatchCount = 0;
					RefreshUiRunnable refreshRunnable = new RefreshUiRunnable();

					for (int fileIndex = 0; fileIndex < allFiles.size(); fileIndex++) {

						if (monitor.isCanceled()) {
							handleSearchFinished();
							return Status.CANCEL_STATUS;
						}

						File file = allFiles.get(fileIndex);

						Set<IProcessVariableAddress> processVariableAddresses = displayInfoService
								.getProcessVariableAddresses(file);

						for (final IProcessVariableAddress pvAddress : processVariableAddresses) {
							if (isSearchMatch(pvAddress)) {
								searchMatchCount++;
								addToSearchResults(file, pvAddress);
							}
						}

						boolean hasNewResultsAndNotRefreshedInLast2Seconds = searchMatchCount > 0
								&& (System.currentTimeMillis() - refreshRunnable
										.getLastRefreshTime()) > REFRESH_UI_DELAY_MILLISECONDS;
						if (!refreshRunnable.isRunning()
								&& hasNewResultsAndNotRefreshedInLast2Seconds) {
							searchMatchCount = 0;
							refreshRunnable.setRunning(true);
							Display.getDefault().asyncExec(refreshRunnable);
						}

						if ((fileIndex + 1) % UPDATE_PROGRESS_THRESHOLD == 0) {
							updateProgress(UPDATE_PROGRESS_THRESHOLD, monitor);
						}

					}

					if (!refreshRunnable.isRunning()) {
						Display.getDefault().syncExec(refreshRunnable);
					}
					monitor.done();
					handleSearchFinished();

					return Status.OK_STATUS;
				}
			};
			searchJob.schedule();
		} catch (CoreException coreException) {
		}
	}

	private void onToggleSearch() {
		if (isSearching) {
			cancelSearch();
		} else if (!searchTextfield.getText().isEmpty()) {
			doSearch(searchTextfield.getText());
		}
	}

	private Pattern buildSearchRegex(String searchString) {
		StringBuilder regexBuilder = new StringBuilder();

		StringTokenizer tokenizer = new StringTokenizer(searchString, "*", true);

		boolean isLastTokenWildcard = false;
		while (tokenizer.hasMoreTokens()) {
			String nextToken = tokenizer.nextToken();
			if (nextToken.equals("*")) {
				if (!isLastTokenWildcard) {
					regexBuilder.append(".*");
					isLastTokenWildcard = true;
				}
			} else {
				regexBuilder.append("(" + Pattern.quote(nextToken) + ")");
				isLastTokenWildcard = false;
			}
		}

		return Pattern.compile(regexBuilder.toString(),
				Pattern.CASE_INSENSITIVE);
	}

	private boolean isSearchMatch(IProcessVariableAddress pvAddress) {
		return searchRegex.matcher(pvAddress.getProperty()).matches();
	}

	private void addToSearchResults(File file, IProcessVariableAddress pvAddress) {
		synchronized (rootElements) {
			if (!rootElements.contains(pvAddress)) {
				rootElements.add(pvAddress);
				HashSet<File> fileSet = new HashSet<File>();
				fileSet.add(file);
				treeElements.put(pvAddress, fileSet);
			} else {
				treeElements.get(pvAddress).add(file);
			}
		}
	}

	private void updateProgress(final int progressDelta,
			IProgressMonitor progressMonitor) {
		progressMonitor.worked(progressDelta);

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (isSearching) {
					int newProgressValue = Math.min(progressBar.getSelection()
							+ progressDelta, progressBar.getMaximum());
					progressBar.setSelection(newProgressValue);
				}
			}
		});
	}

	private void handleSearchFinished() {
		searchJob = null;
		isSearching = false;
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				((GridData) progressBar.getLayoutData()).heightHint = 0;
				progressBar.getParent().layout();
				progressBar.setSelection(0);
				searchButton.setText("Search");
			}
		});
	}

	private List<File> getAllFiles() throws CoreException {
		final Set<File> resultSet = new HashSet<File>();

		List<String> folderList = new ArrayList<String>();
		if (searchFoldersCheckbox.getSelection()) {
			List<PvSearchFolderPreferenceItem> loadPvSearchItems = pvSearchFolderPreferenceService.loadPvSearchItems();
			for (PvSearchFolderPreferenceItem pvSearchFolderPreferenceItem : loadPvSearchItems) {
				if (pvSearchFolderPreferenceItem.isChecked()) {
					folderList.add(pvSearchFolderPreferenceItem.getFolderPath());
				}
			}
		} else {
			String[] folderPathStrings = searchFoldersTextBox.getText().split(";");
			for (String folderPath : folderPathStrings) {
				folderList.add(folderPath.trim());
			}
		}

		for (String folderString : folderList) {
			Path folderPath = new Path(folderString);
			if (ResourcesPlugin.getWorkspace().getRoot().exists(folderPath)) {
				IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(folderPath);
				
				resource.accept(new IResourceVisitor() {
					@Override
					public boolean visit(IResource resource) {
						if (resource.getType() == IResource.FILE) {
							IFile file = (IFile) resource;
							if ("css-sds".equalsIgnoreCase(file
									.getFileExtension())) {
								File fileOnDisk = file.getRawLocation()
										.makeAbsolute().toFile();
								if (fileOnDisk.exists()) {
									resultSet.add(fileOnDisk);
								}
							}
							return false;
						} else if (resource.getType() == IResource.FOLDER) {
							return true;
						}
						return true;
					}
				});
			}
		}

		return new ArrayList<File>(resultSet);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	private class RefreshUiRunnable implements Runnable {
		private boolean isRunning = false;

		private long lastRefreshTime = 0;

		@Override
		public void run() {
			synchronized (rootElements) {
				treeViewer.setInput(rootElements);
			}
			isRunning = false;
			lastRefreshTime = System.currentTimeMillis();
		}

		public long getLastRefreshTime() {
			return lastRefreshTime;
		}

		public boolean isRunning() {
			return isRunning;
		}

		public void setRunning(boolean isRunning) {
			this.isRunning = isRunning;
		}
	}

}
