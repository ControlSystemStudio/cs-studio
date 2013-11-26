/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.iter.css.product.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.core.commands.CommandManager;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.commands.contexts.ContextManager;
import org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler;
import org.eclipse.jface.bindings.Binding;
import org.eclipse.jface.bindings.BindingManager;
import org.eclipse.jface.bindings.Scheme;
import org.eclipse.jface.bindings.keys.KeyBinding;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;

public class WorkbenchUtil {

	private static final String[] HIDE_MESSAGE_STARTS_WITH = new String[] {
			"Keybinding conflicts occurred.  They may interfere with normal accelerator operation.",
			"Invalid preference page path: XML Syntax",
			"Job found still running after platform shutdown." };

	public static final String[] IGNORE_PERSPECTIVES = new String[] {
	// "org.eclipse.debug.ui.DebugPerspective", // Used by Pydev
	"org.eclipse.wst.xml.ui.perspective" };

	private static class HideUnWantedLogFilter implements Filter {

		private Filter previousFilter;

		public HideUnWantedLogFilter(Filter previousFilter) {
			this.previousFilter = previousFilter;
		}

		@Override
		public boolean isLoggable(LogRecord record) {
			if (record.getMessage() != null) {
				for (String hideMsgStartsWith : HIDE_MESSAGE_STARTS_WITH) {
					if (record.getMessage().startsWith(hideMsgStartsWith)) {
						return false;
					}
				}
			}
			if (previousFilter == null) {
				return true;
			}
			return previousFilter.isLoggable(record);
		}
	};

	public static void removeUnWantedLog() {
		// Hide unwanted message from log
		Logger rootLogger = Logger.getLogger("");
		rootLogger.setFilter(new HideUnWantedLogFilter(rootLogger.getFilter()));
		for (Handler handler : rootLogger.getHandlers()) {
			handler.setFilter(new HideUnWantedLogFilter(handler.getFilter()));
		}
	}

	/**
	 * Removes the unwanted perspectives from your RCP application
	 */
	public static void removeUnWantedPerspectives() {

		IPerspectiveRegistry perspectiveRegistry = PlatformUI.getWorkbench()
				.getPerspectiveRegistry();
		IPerspectiveDescriptor[] perspectiveDescriptors = perspectiveRegistry
				.getPerspectives();
		List<String> ignoredPerspectives = Arrays.asList(IGNORE_PERSPECTIVES);
		List<IPerspectiveDescriptor> removePerspectiveDesc = new ArrayList<IPerspectiveDescriptor>();

		// Add the perspective descriptors with the matching perspective ids to
		// the list
		for (IPerspectiveDescriptor perspectiveDescriptor : perspectiveDescriptors) {
			if (ignoredPerspectives.contains(perspectiveDescriptor.getId())) {
				removePerspectiveDesc.add(perspectiveDescriptor);
			}
		}

		// If the list is non-empty then remove all such perspectives from the
		// IExtensionChangeHandler
		if (perspectiveRegistry instanceof IExtensionChangeHandler
				&& !removePerspectiveDesc.isEmpty()) {
			IExtensionChangeHandler extChgHandler = (IExtensionChangeHandler) perspectiveRegistry;
			extChgHandler
					.removeExtension(null, removePerspectiveDesc.toArray());
		}
	}

	/**
	 * Unbind F11 KeyBinding of org.eclipse.debug.ui to avoid conflict with
	 * org.csstudio.opibuilder plugin
	 */
	public static void unbindDebugLast() {
		IBindingService bindingService = (IBindingService) PlatformUI
				.getWorkbench().getAdapter(IBindingService.class);
		BindingManager localChangeManager = new BindingManager(
				new ContextManager(), new CommandManager());

		final Scheme[] definedSchemes = bindingService.getDefinedSchemes();
		try {
			for (int i = 0; i < definedSchemes.length; i++) {
				final Scheme scheme = definedSchemes[i];
				final Scheme copy = localChangeManager
						.getScheme(scheme.getId());
				copy.define(scheme.getName(), scheme.getDescription(),
						scheme.getParentId());
			}
			localChangeManager
					.setActiveScheme(bindingService.getActiveScheme());
		} catch (final NotDefinedException e) {
			e.printStackTrace();
		}
		localChangeManager.setLocale(bindingService.getLocale());
		localChangeManager.setPlatform(bindingService.getPlatform());
		localChangeManager.setBindings(bindingService.getBindings());

		KeyBinding opiFullScreenBinding = null;
		int nbBinding = 0;

		Binding[] bArray = bindingService.getBindings();
		if (bArray != null) {
			for (Binding binding : bArray) {
				if (binding instanceof KeyBinding) {
					KeyBinding kBind = (KeyBinding) binding;
					if (kBind.getParameterizedCommand() != null
							&& kBind.getParameterizedCommand().getCommand() != null) {
						if ("org.eclipse.debug.ui.commands.DebugLast"
								.equals(kBind.getParameterizedCommand()
										.getCommand().getId())) {
							KeySequence triggerSequence = kBind
									.getKeySequence();
							String contextId = kBind.getContextId();
							String schemeId = kBind.getSchemeId();
							KeyBinding deleteBinding = new KeyBinding(
									triggerSequence, null, schemeId, contextId,
									null, null, null, Binding.USER);
							localChangeManager.addBinding(deleteBinding);
							try {
								bindingService.savePreferences(
										localChangeManager.getActiveScheme(),
										localChangeManager.getBindings());
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else if ("org.csstudio.opibuilder.actions.fullscreen"
								.equals(kBind.getParameterizedCommand()
										.getCommand().getId())) {
							if (opiFullScreenBinding == null)
								opiFullScreenBinding = kBind;
							nbBinding++;
						}
					}
				}
			}
		}

		// Rebind OPI runner full screen command if it exists only one time
		if (nbBinding == 1) {
			KeySequence triggerSequence = opiFullScreenBinding.getKeySequence();
			String contextId = opiFullScreenBinding.getContextId();
			String schemeId = opiFullScreenBinding.getSchemeId();

			KeyBinding updateBinding = new KeyBinding(triggerSequence,
					opiFullScreenBinding.getParameterizedCommand(), schemeId,
					contextId, null, null, null, Binding.USER);
			localChangeManager.addBinding(updateBinding);
			try {
				bindingService.savePreferences(
						localChangeManager.getActiveScheme(),
						localChangeManager.getBindings());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
