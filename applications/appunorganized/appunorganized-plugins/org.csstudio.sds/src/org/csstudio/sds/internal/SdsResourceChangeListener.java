/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 package org.csstudio.sds.internal;

import org.csstudio.sds.internal.rules.RuleService;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class SdsResourceChangeListener implements IResourceChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(SdsResourceChangeListener.class);

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
    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        try {
            event.getDelta().accept(_visitor);
        } catch (CoreException e) {
            LOG.error(e.toString());
        }
    }

    private class DeltaVisitor implements IResourceDeltaVisitor {
        @Override
        public boolean visit(IResourceDelta delta) throws CoreException {
            boolean result = true;

            IResource resource = delta.getResource();

            // FIXME: Sven Wende: Soll das wirklich auf jedem File passieren? Es muss zusätzlich nach Dateitypen gefiltert werden!
            if (resource instanceof IFile) {
                result = false;

                if (delta.getKind() == IResourceDelta.CHANGED) {
                    RuleService.getInstance().updateScriptedRule(
                            resource.getName(), (IFile) resource);
                } else if (delta.getKind() == IResourceDelta.REMOVED) {
                    RuleService.getInstance().updateScriptedRule(
                            resource.getName(), null);
                }
            } else if (resource instanceof IProject) {
                result = RuleService.SCRIPT_PROJECT_NAME.equals(resource
                        .getName());
            }

            return result;
        }
    }

}
