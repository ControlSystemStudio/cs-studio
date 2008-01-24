package org.csstudio.sds.internal;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.model.logic.RuleService;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

public class SdsResourceChangeListener implements IResourceChangeListener {

	/**
	 * The used resource delta visitor.
	 */
	private IResourceDeltaVisitor _visitor;

	/**
	 * Constructor.
	 */
	public SdsResourceChangeListener() {
		_visitor = new DeltaVisitor();
	}

	/**
	 * {@inheritDoc}
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		try {
			event.getDelta().accept(_visitor);
		} catch (CoreException e) {
			CentralLogger.getInstance().error(this, e);
		}
	}

	private class DeltaVisitor implements IResourceDeltaVisitor {

		public boolean visit(IResourceDelta delta) throws CoreException {
			boolean result = true;

			IResource resource = delta.getResource();

			if (resource instanceof IFile) {
				result = false;

				if (delta.getKind() == IResourceDelta.CHANGED) {
					RuleService.getInstance().updateScriptedRule(
							resource.getName(), (IFile) resource);
					System.out.println(resource.getName() + " ==> UPDATE");
				} else if (delta.getKind() == IResourceDelta.REMOVED) {
					RuleService.getInstance().updateScriptedRule(
							resource.getName(), null);
					System.out.println(resource.getName() + " ==> REMOVE");
				}
			} else if (resource instanceof IProject) {
				result = RuleService.SCRIPT_PROJECT_NAME.equals(resource
						.getName());
			}

			return result;
		}
	}

}
