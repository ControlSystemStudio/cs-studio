package org.csstudio.config.ioconfig.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructureDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IViewSite;

/**
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.10 $
 * @since 19.06.2007
 */
class ProfibusTreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {

    /** The Tree Root Node. */
//    private IViewSite _site;
    private List<FacilityDBO> _facilities;
    private String _wait;

    public ProfibusTreeContentProvider(final IViewSite site) {
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public void inputChanged(final Viewer v, final Object oldInput, final Object newInput) {
        if (newInput instanceof FacilityDBO) {
            _facilities.add((FacilityDBO) newInput);
        } else if (newInput instanceof List) {
            _facilities = (List<FacilityDBO>) newInput;
        } else if(newInput instanceof String) {
            _wait = (String) newInput;
        }

    }

    /** {@inheritDoc} */
    public void dispose() {
    }

    /** {@inheritDoc} */
    public Object[] getElements(final Object parent) {
        if (_facilities != null) {
            return _facilities.toArray();
        } else if(_wait!=null) {
            return new String[] {_wait};
        }
        return new Object[0];
    }

    /** {@inheritDoc} */
    public Object getParent(final Object child) {
        if (child instanceof AbstractNodeDBO) {
            return ((AbstractNodeDBO) child).getParent();
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
//        if (parent instanceof FacilityLight) {
//            final FacilityLight l = (FacilityLight) parent;
//            try {
//                //FIXME: Der ProgressDialog wired
//                final Job loadJob = new Job("DBLoader") {
//
//                    @Override
//                    protected IStatus run(IProgressMonitor monitor) {
//                        monitor.beginTask("DBLoaderMonitor", IProgressMonitor.UNKNOWN);
//                        monitor.setTaskName("Load " + l.getName()+"\t+\t"+new Date());
//                        // das wird beim erstenmal eine zeitlang dauern...
//                        l.getFacility();
//                        monitor.done();
//                        return Status.OK_STATUS;
//                    }
//
//                };
//                loadJob.setUser(true);
//                loadJob.schedule();
//
//                do {
//                     Thread.yield();
//                }while(loadJob.getState()!=Job.NONE);
//
//                Object[] array = l.getFacility().getChildren().toArray();
//                return array;
//            } catch (Exception e) {
//                ErrorDialog
//                        .openError(
//                                _site.getShell(),
//                                "Data Base Error 1",
//                                null,
//                                new OperationStatus(
//                                        OperationStatus.ERROR,
//                                        ActivatorUI.PLUGIN_ID,
//                                        3,
//                                        String
//                                                .format(
//                                                        "Device Data Base (DDB) Error\nCan't load the Facility '%1s' (ID: %2s)",
//                                                        l.getName(), l.getId()), e));
//                CentralLogger.getInstance().error(this, e);
//            }
//            return null;
//        } else
            if (parent instanceof ModuleDBO) {
            ModuleDBO module = (ModuleDBO) parent;
            Collection<ChannelStructureDBO> values = module.getChannelStructsAsMap().values();
            List<AbstractNodeDBO> list = new ArrayList<AbstractNodeDBO>(values.size());
            for (ChannelStructureDBO channelStructure : values) {
                if (channelStructure.isSimple()) {
                    list.addAll(channelStructure.getChildrenAsMap().values());
                } else {
                    list.add(channelStructure);
                }
            }
            return list.toArray(new AbstractNodeDBO[list.size()]);
        }else if(parent instanceof ChannelStructureDBO) {
            ChannelStructureDBO cs = (ChannelStructureDBO) parent;
            if(cs.isSimple()) {
                return cs.getChildrenAsMap().values().toArray(new AbstractNodeDBO[0]);
            }else {
                Collection<? extends AbstractNodeDBO> values = cs.getChildrenAsMap().values();
                if(cs.getChildrenAsMap().containsKey((short)-1)){
                    values.remove(values.iterator().next());
                }
                return values.toArray(new AbstractNodeDBO[0]);
            }

        }else if (parent instanceof AbstractNodeDBO) {
            return ((AbstractNodeDBO) parent).getChildrenAsMap().values().toArray(new AbstractNodeDBO[0]);
        }

        return new NamedDBClass[0];
    }

    /** {@inheritDoc} */
    public boolean hasChildren(final Object parent) {
        return getChildren(parent).length > 0;
    }

}
