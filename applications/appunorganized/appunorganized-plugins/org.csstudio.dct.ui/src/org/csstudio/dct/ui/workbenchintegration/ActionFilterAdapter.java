package org.csstudio.dct.ui.workbenchintegration;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IVisitor;
import org.csstudio.dct.model.internal.Project;
import org.csstudio.domain.common.strings.StringUtil;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IActionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a {@link IActionFilter} adapter for {@link IElement}
 * objects.
 *
 * @author Sven Wende
 *
 */
public final class ActionFilterAdapter implements IActionFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ActionFilterAdapter.class);

    private static final String ATTR_ERROR = "error";

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean testAttribute(Object target, String name, String value) {
        if (ATTR_ERROR.equals(name) && target instanceof IElement) {
            FindErrorMarkerVisitor visitor = new FindErrorMarkerVisitor();
            ((IElement) target).accept(visitor);

            String errors = visitor.isErrorFound() ? "true" : "false";

            return errors.equals(value);
        }

        return false;
    }

    /**
     * Visitor that finds error markers.
     *
     * @author Sven Wende
     *
     */
    static final class FindErrorMarkerVisitor implements IVisitor {
        private boolean errorFound;
        private final Set<UUID> nodesWithErrors;

        public FindErrorMarkerVisitor() {
            errorFound = false;
            nodesWithErrors = new HashSet<UUID>();
            try {
                IMarker[] markers = ResourcesPlugin.getWorkspace().getRoot().findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);

                for (IMarker marker : markers) {
                    String location = (String) marker.getAttribute(IMarker.LOCATION);
                    if (StringUtil.hasLength(location)) {
                        nodesWithErrors.add(UUID.fromString(location));
                    }
                }
            } catch (CoreException e) {
                //
                LOG.warn("Warn", e);
            }

        }

        @Override
        public void visit(Project project) {
            doVisit(project);
        }

        @Override
        public void visit(IFolder folder) {
            doVisit(folder);
        }

        @Override
        public void visit(IPrototype prototype) {
            doVisit(prototype);
        }

        @Override
        public void visit(IInstance instance) {
            doVisit(instance);
        }

        @Override
        public void visit(IRecord record) {
            doVisit(record);
        }

        public boolean isErrorFound() {
            return errorFound;
        }

        private void doVisit(IElement element) {
            if (nodesWithErrors.contains(element.getId())) {
                errorFound = true;
            }
        }

    }
}
