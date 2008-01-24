/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.sds.ui.internal.commands;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.model.WidgetModelFactoryService;
import org.csstudio.sds.ui.internal.editor.AliasInitializationDialog;
import org.csstudio.sds.ui.internal.editor.DropPvRequest;
import org.csstudio.sds.ui.internal.editor.WidgetCreationFactory;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A command, which creates a widget, caused by a Drag and Drop.
 * 
 * @author Sven Wende, Kai Meyer
 */
public final class CreateWidgetFromDroppedPvCommand extends Command {
	/**
	 * The container, which should contain the new Widget.
	 */
	private ContainerModel _containerModel;
	/**
	 * The Request.
	 */
	private DropPvRequest _dropPvRequest;

	/**
	 * The internal {@link CompoundCommand}.
	 */
	private Command _compoundCommand;

	/**
	 * Constructor.
	 * 
	 * @param request
	 *            The Request
	 * @param containerModel
	 *            The DisplayModel for the new Widget
	 */
	public CreateWidgetFromDroppedPvCommand(final DropPvRequest request,
			final ContainerModel containerModel) {
		assert request != null;
		assert containerModel != null;
		this.setLabel("Drop Widget");
		_containerModel = containerModel;
		_dropPvRequest = request;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		_compoundCommand.undo();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		String typeId = determineWidgetType();

		if (typeId != null) {
			WidgetCreationFactory factory = new WidgetCreationFactory(typeId);
			// create a widget
			AbstractWidgetModel widgetModel = (AbstractWidgetModel) factory
					.getNewObject(_dropPvRequest.getProcessVariableAddress());
			// initialize widget
			widgetModel.setLocation(_dropPvRequest.getLocation().x,
					_dropPvRequest.getLocation().y);
			widgetModel.setLayer(_containerModel.getLayerSupport()
					.getActiveLayer().getId());

			// create compound command, which can be undone
			_compoundCommand = new AddWidgetCommand(_containerModel,
					widgetModel);

			// execute the command
			_compoundCommand.execute();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void redo() {
		_compoundCommand.execute();
	}

	private Map<String, String> determineAliases(String droppedString) {
		Map<String, String> result = null;

		// pop up the alias dialog
		Map<String, String> initialAliases = new HashMap<String, String>();
		initialAliases.put("channel", droppedString);

		// 
		AliasInitializationDialog dialog = new AliasInitializationDialog(
				Display.getCurrent().getActiveShell(), initialAliases);
		if (dialog.open() == Window.OK) {
			result = dialog.getAliasDescriptors();
		}

		return result;
	}

	private String determineWidgetType() {
		String result = null;
		final WidgetDialog dialog = new WidgetDialog(new Shell(),
				_dropPvRequest.getProcessVariableAddress().toString());

		if (dialog.open() == Window.OK) {
			result = dialog.getSelectedWidgetType();
		}
		return result;
	}

	/**
	 * A Dialog, which allows to choose the Widget, which should be created.
	 * 
	 * @author Kai Meyer
	 */
	private final class WidgetDialog extends TitleAreaDialog {
		/**
		 * The selected type of the widget.
		 */
		private String _selectedWidget = null;

		/**
		 * The List of Buttons.
		 */
		private final List<Button> _buttonList = new LinkedList<Button>();

		/**
		 * The name of the PV.
		 */
		private String _pvName = null;

		/**
		 * Constructor.
		 * 
		 * @param parentShell
		 *            The parent Shell for this Dialog
		 * @param pvName
		 *            The name of the PV
		 */
		public WidgetDialog(final Shell parentShell, final String pvName) {
			super(parentShell);
			_pvName = pvName;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void configureShell(final Shell shell) {
			super.configureShell(shell);
			shell.setText("Selected Widget");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Control createDialogArea(final Composite parent) {
			final Composite composite = (Composite) super
					.createDialogArea(parent);
			this.setTitle("Select the Widget");
			if (_pvName != null && _pvName.trim().length() > 0) {
				this.setMessage("for the PV '" + _pvName + "'");
			}
			Label label = new Label(composite, SWT.NONE);
			label.setText("Available Widgets:");
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
					false, 2, 1));
			ScrolledComposite scrolledComposite = new ScrolledComposite(
					composite, SWT.BORDER | SWT.V_SCROLL);
			scrolledComposite.setExpandHorizontal(true);
			scrolledComposite.setExpandVertical(true);
			scrolledComposite.setBackground(Display.getCurrent()
					.getSystemColor(SWT.COLOR_RED));
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
			gridData.heightHint = 400;
			scrolledComposite.setLayoutData(gridData);
			Composite comp = new Composite(scrolledComposite, SWT.NONE);
			composite.setBackground(Display.getCurrent().getSystemColor(
					SWT.COLOR_BLUE));
			comp.setLayout(new GridLayout(1, false));
			this.createRadioButtons(comp);
			scrolledComposite.setContent(comp);
			scrolledComposite.setMinSize(comp.computeSize(SWT.DEFAULT,
					SWT.DEFAULT));
			return composite;
		}

		/**
		 * Creates a Radiobutton for every registered Widget.
		 * 
		 * @param parent
		 *            The parent composite for the Buttons
		 */
		private void createRadioButtons(final Composite parent) {
			Set<String> widgetTypes = WidgetModelFactoryService.getInstance()
					.getWidgetTypes();
			for (String widgetId : widgetTypes) {
				Button button = new Button(parent, SWT.RADIO);
				button.setText(WidgetModelFactoryService.getInstance().getName(
						widgetId));
				button.setData(widgetId);
				String contributingPluginId = WidgetModelFactoryService
						.getInstance().getContributingPluginId(widgetId);
				String iconPath = WidgetModelFactoryService.getInstance()
						.getIcon(widgetId);
				button.setImage(CustomMediaFactory.getInstance()
						.getImageFromPlugin(contributingPluginId, iconPath));
				_buttonList.add(button);
			}
			if (_buttonList.isEmpty()) {
				this.getButton(IDialogConstants.OK_ID).setEnabled(false);
			} else {
				_buttonList.get(0).setEnabled(true);
			}
		}

		/**
		 * Gets the type of the selected widget.
		 * 
		 * @return String The type of the selected widget
		 */
		public String getSelectedWidgetType() {
			return _selectedWidget;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void okPressed() {
			for (Button button : _buttonList) {
				if (button.getSelection()) {
					_selectedWidget = (String) button.getData();
					break;
				}
			}
			super.okPressed();
		}

	}
}
