package org.csstudio.sds.ui.runmode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.internal.connection.ConnectionService;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.internal.editparts.WidgetEditPartFactory;
import org.csstudio.sds.ui.internal.runmode.AbstractRunModeBox;
import org.csstudio.sds.ui.internal.runmode.DisplayViewPart;
import org.csstudio.sds.ui.internal.runmode.IRunModeDisposeListener;
import org.csstudio.sds.ui.internal.runmode.RunModeBoxInput;
import org.csstudio.sds.ui.internal.runmode.RunModeType;
import org.csstudio.sds.ui.internal.runmode.ShellRunModeBox;
import org.csstudio.sds.ui.internal.runmode.ViewRunModeBox;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.tools.SelectionTool;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;

/**
 * 
 * Service, which manages the run mode versions of graphical viewers.
 * 
 * @author Sven Wende
 */
public final class RunModeService {

	private static final String SEPARATOR = "°°°";

	/**
	 * The singleton instance.
	 */
	private static RunModeService _instance;

	/**
	 * A Map of the already displayed IFiles and their RunModeBoxes.
	 */
	private HashMap<RunModeBoxInput, AbstractRunModeBox> _activeBoxes;

	/**
	 * Constructor.
	 */
	private RunModeService() {
		_activeBoxes = new HashMap<RunModeBoxInput, AbstractRunModeBox>();
	}

	/**
	 * Returns the singleton instance.
	 * 
	 * @return the singleton instance
	 */
	public static RunModeService getInstance() {
		if (_instance == null) {
			_instance = new RunModeService();
		}

		return _instance;
	}

	/**
	 * Opens a Display in a new Shell and adds the new Aliases.
	 * 
	 * @param path
	 *            The IPath of the Display
	 * @param aliases
	 *            A Map of new Aliases for the Display (can be null)
	 */
	public void openDisplayShellInRunMode(final IPath path,
			final Map<String, String> aliases) {
		assert path != null;

		final RunModeBoxInput runModeBoxInput = new RunModeBoxInput(path,
				aliases, RunModeType.SHELL);

		if (_activeBoxes.containsKey(runModeBoxInput)) {
			AbstractRunModeBox box = _activeBoxes.get(runModeBoxInput);
			box.bringToTop();
		} else {
			InputStream is = getInputStream(path);

			if (is != null) {

				final AbstractRunModeBox runModeBox = new ShellRunModeBox(is,
						path.toString(), ConnectionService.getInstance());

				// memorize box
				_activeBoxes.put(runModeBoxInput, runModeBox);

				// when the box is disposed, forget the box
				runModeBox.addDisposeListener(new IRunModeDisposeListener() {
					public void dispose() {
						_activeBoxes.remove(runModeBoxInput);
					}
				});

				// open the box
				runModeBox.openRunMode(aliases, null);
				
			} else {
				CentralLogger.getInstance().info(
						null,
						"Cannot open run mode: " + path.toOSString()
								+ " does not exist.");
			}
		}
	}

	/**
	 * Opens a Display in a new View.
	 * 
	 * @param location
	 *            The IPath to the Display
	 */
	public void openDisplayViewInRunMode(final IPath location) {
		this.openDisplayViewInRunMode(location, null);
	}

	/**
	 * Opens a Display in a new View and adds the new Aliases.
	 * 
	 * @param path
	 *            The IPath to the Display
	 * @param aliases
	 *            The new Aliases for the Display (can be null)
	 */
	public void openDisplayViewInRunMode(final IPath path,
			final Map<String, String> aliases) {
		assert path != null;
		openBoxForWorkbenchView(path, aliases, null);
	}

	/**
	 * Opens a Display in a new View with the informations of the given {@link IMemento}.
	 * @param displayViewPart the {@link DisplayViewPart}
	 * @param memento the {@link IMemento} for the view
	 * @required memento!=null
	 */
	public void openDisplayViewInRunMode(final DisplayViewPart displayViewPart,
			final IMemento memento) {
		assert memento!=null : "Precondition violated: memento!=null";
		String storedPath = memento.getString("FILE");

		if (storedPath != null) {
			Map<String, String> aliases = new HashMap<String, String>();
			String tmp = memento.getString("ALIASES");

			if (tmp != null) {
				String[] tmpA = tmp.split(SEPARATOR);

				if ((tmpA.length % 2 == 0)) {
					for (int i = 0; i <= tmpA.length - 1; i += 2) {
						String key = tmpA[i];
						String value = tmpA[i + 1];
						assert key != null;
						assert value != null;
						aliases.put(key, value);
					}
				}
			}
			openBoxForWorkbenchView(new Path(storedPath), aliases,
					displayViewPart);

		}
	}

	private void openBoxForWorkbenchView(final IPath path,
			final Map<String, String> aliases, final DisplayViewPart view) {
		assert path != null;

		final RunModeBoxInput runModeBoxInput = new RunModeBoxInput(path,
				aliases, RunModeType.VIEW);

		if (_activeBoxes.containsKey(runModeBoxInput)) {
			AbstractRunModeBox box = _activeBoxes.get(runModeBoxInput);
			box.bringToTop();
		} else {
			InputStream is = getInputStream(path);

			if (is != null) {
				final ViewRunModeBox runModeBox = new ViewRunModeBox(is, path
						.toString(), ConnectionService.getInstance(), view);

				// memorize box
				_activeBoxes.put(runModeBoxInput, runModeBox);

				// when the box is disposed, forget the box
				runModeBox.addDisposeListener(new IRunModeDisposeListener() {
					public void dispose() {
						_activeBoxes.remove(runModeBoxInput);
					}
				});

				// create a runnable that is executed, when the view is fully launched
				Runnable runnable = new Runnable() {
					public void run() {
						// IMPORTANT: set the memento infos on the view
						Map<String, String> mementoInfos = new HashMap<String, String>();

						// the file path
						mementoInfos.put("FILE", path.toOSString());

						if (aliases != null) {
							StringBuffer sb = new StringBuffer();
							for (String key : aliases.keySet()) {
								sb.append(key + SEPARATOR + aliases.get(key));
							}
							mementoInfos.put("ALIASES", sb.toString());
						}
						
						// TODO: Funktioniert so nicht, da die View asynchron geladen wird
						runModeBox.getView().setMementoInfos(mementoInfos);						
					}
				};
				
				
				// open the box
				runModeBox.openRunMode(aliases, runnable);
			} else {
				CentralLogger.getInstance().info(
						null,
						"Cannot open run mode: " + path.toOSString()
								+ " does not exist.");
			}
		}

	}

	/**
	 * Creates a graphical viewer that can be used to display SDS models.
	 * 
	 * @param parent
	 *            the parent composite
	 * 
	 * TODO: Methode abschaffen.
	 * 
	 * @deprecated
	 * @return a graphical viewer that can be used to display SDS models
	 */
	public static GraphicalViewer createGraphicalViewer(final Composite parent) {
		
		
		final ScrollingGraphicalViewer viewer = new ScrollingGraphicalViewer();
		viewer.createControl(parent);

		viewer.setEditPartFactory(new WidgetEditPartFactory(ExecutionMode.RUN_MODE));

		final ScalableFreeformRootEditPart root = new ScalableFreeformRootEditPart();
		viewer.setRootEditPart(root);

		EditDomain editDomain = new EditDomain();
		
		final SelectionTool tool = new SelectionTool();
		tool.setUnloadWhenFinished(false);
		editDomain.setDefaultTool(tool);
		editDomain.addViewer(viewer);
		
		return viewer;
	}

	/**
	 * Opens a Display in a new Shell.
	 * 
	 * @param filePath
	 *            The IPath of the Display
	 */
	public void openDisplayShellInRunMode(final IPath filePath) {
		openDisplayShellInRunMode(filePath, new HashMap<String, String>());
	}

	/**
	 * Return the {@link InputStream} from the given path.
	 * 
	 * @param path
	 *            The {@link IPath} to the file
	 * @return The corresponding {@link InputStream}
	 */
	private InputStream getInputStream(final IPath path) {
		InputStream result = null;

		// try workspace

		IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(path,
				false);
		if (r instanceof IFile) {
			try {
				result = ((IFile) r).getContents();
			} catch (CoreException e) {
				result = null;
			}
		}

		if (result == null) {
			// try from local file system
			try {
				result = new FileInputStream(path.toFile());
			} catch (FileNotFoundException e) {
				result = null;
			}

		}

		return result;
	}
}
