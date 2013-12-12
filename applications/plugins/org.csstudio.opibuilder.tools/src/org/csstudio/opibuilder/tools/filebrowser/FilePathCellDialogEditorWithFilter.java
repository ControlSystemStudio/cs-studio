package org.csstudio.opibuilder.tools.filebrowser;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.tools.Activator;
import org.csstudio.opibuilder.visualparts.AbstractDialogCellEditor;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * A custom cell dialog editor to browse file path applying filters.
 * 
 * @author SOPRA Group
 */
public final class FilePathCellDialogEditorWithFilter extends
		AbstractDialogCellEditor {

	/**
	 * The default value for the file extensions.
	 */
	private static final String[] IMAGE_EXTENSIONS = new String[] { "gif",
			"GIF", "png", "PNG", "svg", "SVG" };

	/**
	 * The regular expression for TTT pattern like FFF-FFF-FFF:TTT1234-AAAA TODO
	 * store it in INI file
	 */
	private static String TTT_REGEX;

	/**
	 * The current IPath.
	 */
	private IPath path;

	/**
	 * The filter path for the dialog.
	 */
	@SuppressWarnings("unused")
	private String filterPath = System.getProperty("user.home");

	/**
	 * The accepted file extensions.
	 */
	private String[] filters;
	/**
	 * The original file extensions.
	 */
	private String[] orgFileExtensions;

	/**
	 * TODO only use temporarily.
	 */
	private boolean onlyWorkSpace = true;

	private AbstractWidgetModel widgetModel;

	public FilePathCellDialogEditorWithFilter(final Composite parent,
			final AbstractWidgetModel widgetModel, final String[] fileExtensions) {
		super(parent, "Open File");
		this.orgFileExtensions = fileExtensions;
		this.widgetModel = widgetModel;
		convertFileExtensions();
		IPreferencesService service = Platform.getPreferencesService();
		TTT_REGEX = service.getString("org.csstudio.opibuilder.widgets.symbol",
				"filter_regex", "", null);
	}

	/**
	 * Convert the file extensions. Add '*.' to each extension if it does not
	 * start with it.
	 */
	private void convertFileExtensions() {
		if (onlyWorkSpace) {
			this.filters = orgFileExtensions;
		} else {
			if (orgFileExtensions.length > 0) {
				filters = new String[orgFileExtensions.length];
				for (int i = 0; i < filters.length; i++) {
					if (orgFileExtensions[i].startsWith("*.")) {
						filters[i] = orgFileExtensions[i];
					} else {
						filters[i] = "*." + orgFileExtensions[i];
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object doGetValue() {
		return path;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doSetValue(final Object value) {
		if (value == null || !(value instanceof IPath)) {
			path = new Path("");
		} else {
			path = (IPath) value;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void openDialog(final Shell parentShell, final String dialogTitle) {
		if (onlyWorkSpace) {
			String pvName = (String) widgetModel
					.getPropertyValue(AbstractPVWidgetModel.PROP_PVNAME);
			if (!pvName.isEmpty()) {
				ArrayList<String> listToFind = new ArrayList<String>();
				Pattern pattern = Pattern.compile(TTT_REGEX);
				Matcher matcher = pattern.matcher(pvName);
				if (matcher.find()) {
					for (int i = 0; i <= matcher.groupCount(); i++) {
						listToFind.add(matcher.group(i));
					}
				}
				if (!listToFind.isEmpty()) {
					filters = (String[]) listToFind
							.toArray(new String[listToFind.size()]);
				} else {
					Activator.getLogger().log(
							Level.WARNING,
							"Pattern " + TTT_REGEX + " can’t be found in PV name " + pvName);
				}
			} else {
				filters = IMAGE_EXTENSIONS;
			}
			FilePathDialogWithFilter rsd = new FilePathDialogWithFilter(
					parentShell, widgetModel.getRootDisplayModel()
							.getOpiFilePath().removeLastSegments(1),
					"Select a resource", filters);
			rsd.setSelectedResource(path);
			if (rsd.open() == Window.OK) {
				if (rsd.getSelectedResource() != null) {
					path = rsd.getSelectedResource();
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean shouldFireChanges() {
		return path != null;
	}

}
