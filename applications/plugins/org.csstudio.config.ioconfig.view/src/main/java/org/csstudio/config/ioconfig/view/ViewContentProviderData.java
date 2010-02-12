package org.csstudio.config.ioconfig.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.csstudio.config.ioconfig.model.Facility;
import org.csstudio.config.ioconfig.model.FacilityLight;
import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructure;
import org.csstudio.config.ioconfig.model.pbmodel.Module;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IViewSite;

/**
 * 
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 19.06.2007
 */
class ProfibusTreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {

    /** The Tree Root Node. */
    private IViewSite _site;
    private List<FacilityLight> _facilities;

    public ProfibusTreeContentProvider(IViewSite site) {
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public void inputChanged(final Viewer v, final Object oldInput, final Object newInput) {
        FacilityLight fL = null;
        if (newInput instanceof Facility) {
            Facility newFacility = (Facility) newInput;
            if (_facilities != null) {
                fL = new FacilityLight(newFacility);
                newFacility.setFacilityLigth(fL);
                _facilities.add(fL);
            }
        } else if (newInput instanceof FacilityLight) {
            fL = (FacilityLight) newInput;
            _facilities.add(fL);
        } else if (newInput instanceof List) {
            _facilities = (List<FacilityLight>) newInput;
            return;
        }

    }

    /** {@inheritDoc} */
    public void dispose() {
    }

    /** {@inheritDoc} */
    public Object[] getElements(final Object parent) {
        if (_facilities != null) {
            return _facilities.toArray();
        }
        return new Object[0];
    }

    /** {@inheritDoc} */
    public Object getParent(final Object child) {
        if (child instanceof Node) {
            return ((Node) child).getParent();
        }
        return null;
    }

    /**
     * The Object can Typ of NodeParent or Object.
     * 
     * @param parent
     *            The Parent Object.
     * @return an Array of Children Objects.
     */
    public Object[] getChildren(final Object parent) {
        if (parent instanceof FacilityLight) {
            final FacilityLight l = (FacilityLight) parent;
            try {
                //FIXME: Der ProgressDialog wired
                final Job loadJob = new Job("DBLoader") {

                    @Override
                    protected IStatus run(IProgressMonitor monitor) {
                        monitor.beginTask("DBLoaderMonitor", IProgressMonitor.UNKNOWN);
                        monitor.setTaskName("Load " + l.getName()+"\t+\t"+new Date());
                        // das wird beim erstenmal eine zeitlang dauern...
                        l.getFacility();
                        monitor.done();
                        return Status.OK_STATUS;
                    }

                };
                loadJob.setUser(true);
                loadJob.schedule();
                
                do {
                     Thread.yield();
                }while(loadJob.getState()!=Job.NONE);

                Object[] array = l.getFacility().getChildren().toArray();
                return array;
            } catch (Exception e) {
                ErrorDialog
                        .openError(
                                _site.getShell(),
                                "Data Base Error 1",
                                null,
                                new OperationStatus(
                                        OperationStatus.ERROR,
                                        ActivatorUI.PLUGIN_ID,
                                        3,
                                        String
                                                .format(
                                                        "Device Data Base (DDB) Error\nCan't load the Facility '%1s' (ID: %2s)",
                                                        l.getName(), l.getId()), e));
                CentralLogger.getInstance().error(this, e);
            }
            return null;
        } else if (parent instanceof Module) {
            Module module = (Module) parent;
            Collection<ChannelStructure> values = module.getChannelStructsAsMap().values();
            List<Node> list = new ArrayList<Node>(values.size());
            for (ChannelStructure channelStructure : values) {
                if (channelStructure.isSimple()) {
                    list.addAll(channelStructure.getChildrenAsMap().values());
                } else {
                    list.add(channelStructure);
                }
            }
            return list.toArray(new Node[list.size()]);
        }else if(parent instanceof ChannelStructure) {
            ChannelStructure cs = (ChannelStructure) parent;
            if(cs.isSimple()) {
                return cs.getChildrenAsMap().values().toArray(new Node[0]);
            }else {
                Collection<? extends Node> values = cs.getChildrenAsMap().values();
                if(cs.getChildrenAsMap().containsKey((short)-1)){
                    values.remove(values.iterator().next());
                }
                return values.toArray(new Node[0]);
            }
            
        }else if (parent instanceof Node) {
            return ((Node) parent).getChildrenAsMap().values().toArray(new Node[0]);
        }

        return new NamedDBClass[0];
    }

    /** {@inheritDoc} */
    public boolean hasChildren(final Object parent) {
        if (parent instanceof FacilityLight) {
            FacilityLight l = (FacilityLight) parent;
            return l.hasChildren();
        }

        return getChildren(parent).length > 0;
    }

}
