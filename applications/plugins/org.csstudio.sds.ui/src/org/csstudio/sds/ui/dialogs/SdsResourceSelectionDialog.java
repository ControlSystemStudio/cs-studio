package org.csstudio.sds.ui.dialogs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.csstudio.sds.ui.thumbnail.SdsThumbnailPanel;
import org.csstudio.sds.ui.thumbnail.SdsThumbnailPanel.LibraryPanelLayout;
import org.csstudio.sds.ui.thumbnail.SdsThumbnailPanel.LibraryPanelListener;
import org.csstudio.ui.util.composites.ResourceSelectionGroup;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class SdsResourceSelectionDialog extends Dialog implements Listener {

	private ResourceSelectionGroup resourceTree;
	private SdsThumbnailPanel libraryPanel;
	private SdsThumbnailPanel listPanel;

	private static IPath selectedFolderPath;
	private IPath selectedPath;

	public SdsResourceSelectionDialog(Shell parent) {
		super(parent);
		this.setShellStyle(SWT.MODELESS | SWT.CLOSE | SWT.MAX | SWT.TITLE
				| SWT.BORDER | SWT.RESIZE | SWT.CENTER);

	}

	@Override
	public void handleEvent(Event event) {
		String filePathPrefix = ResourcesPlugin.getWorkspace().getRoot()
				.getRawLocation().toOSString();
		
		IPath fullPath = ((ResourceSelectionGroup) event.widget).getFullPath();

		List<File> folderList;
		if(fullPath != null) {
			 folderList = Arrays.asList(new File(filePathPrefix
					+ fullPath.toString()));
		}
		else {
			folderList = Collections.emptyList();
		}
		libraryPanel.setFolders(folderList);
		listPanel.setFolders(folderList);
		selectedFolderPath = fullPath;
	}

	public IPath getSelectedPath() {
		return selectedPath;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		Rectangle parentBounds = newShell.getParent().getBounds();

		int shellWidth = 600;
		int shellHeight = 400;
		int xPos = (parentBounds.width - shellWidth) / 2 + parentBounds.x;
		int yPos = (parentBounds.height - shellHeight) / 2 + parentBounds.y;
		
		newShell.setBounds(xPos, yPos, shellWidth, shellHeight);
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Control result = super.createContents(parent);
		if(selectedFolderPath != null) {
			this.resourceTree.setSelectedResource(selectedFolderPath);
		}
		return result;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new FillLayout());
		SashForm sashForm = new SashForm(composite, SWT.HORIZONTAL);
		sashForm.setLayout(new FillLayout());
		this.resourceTree = new ResourceSelectionGroup(sashForm, this, null,
				false);
		
		TabFolder tabFolder = new TabFolder(sashForm, SWT.BORDER);
		TabItem libraryTabItem = new TabItem(tabFolder, SWT.BORDER);
		libraryTabItem.setText("Gallery");

		this.libraryPanel = new SdsThumbnailPanel(tabFolder, SWT.None,
				LibraryPanelLayout.UngroupedGrid);
		libraryTabItem.setControl(this.libraryPanel);

		TabItem listTabItem = new TabItem(tabFolder, SWT.BORDER);
		listTabItem.setText("List");

		listPanel = new SdsThumbnailPanel(tabFolder, SWT.None,
				LibraryPanelLayout.UngroupedList);
		listTabItem.setControl(listPanel);

		sashForm.setWeights(new int[] { 1, 2 });

		LibraryPanelListener libraryListener = new LibraryPanelListener() {
			@Override
			public void onSelectionChanged(SdsThumbnailPanel sourcePanel) {
				File selectedFile = sourcePanel.getSelectedFile();
				if (selectedFile != null) {
					selectFile(new Path(selectedFile.getAbsolutePath())
							.makeRelativeTo(
									ResourcesPlugin.getWorkspace().getRoot()
											.getLocation()).makeAbsolute());
				} else {
					selectFile(null);
				}

				// Also select element in other library view
				SdsThumbnailPanel otherPanel = sourcePanel == libraryPanel ? listPanel
						: libraryPanel;
				if (sourcePanel.getSelectionGroupIndex() != otherPanel
						.getSelectionGroupIndex()
						|| sourcePanel.getSelectionIndex() != otherPanel
								.getSelectionIndex()) {
					otherPanel.select(sourcePanel.getSelectionGroupIndex(),
							sourcePanel.getSelectionIndex());
				}
			}
		};
		this.libraryPanel.addLibraryPanelListener(libraryListener);
		this.listPanel.addLibraryPanelListener(libraryListener);

		MouseAdapter doubleClickListener = new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				okPressed();
			}
		};
		this.libraryPanel.addMouseListener(doubleClickListener);
		this.listPanel.addMouseListener(doubleClickListener);

		return composite;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		this.getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	@Override
	protected void okPressed() {
		super.okPressed();

	}

	private void selectFile(IPath selectedPath) {
		this.selectedPath = selectedPath;

		this.getButton(IDialogConstants.OK_ID).setEnabled(selectedPath != null);

	}
}
