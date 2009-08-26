package org.csstudio.config.ioconfig.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.csstudio.config.ioconfig.model.Facility;
import org.csstudio.config.ioconfig.model.FacilityLight;
import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructure;
import org.csstudio.config.ioconfig.model.pbmodel.Module;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.commands.operations.OperationStatus;
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
//        _site = site;
//        _facilities = Repository.load(FacilityLight.class);
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
        //
        // Object[] o = getChildren(parent);
        // return o;
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
            FacilityLight l = (FacilityLight) parent;
            try {
                return l.getFacility().getChildren().toArray();
            } catch (Exception e) {
                ErrorDialog.openError(_site.getShell(), "Data Base Error 1", null, new OperationStatus(OperationStatus.ERROR, Activator.PLUGIN_ID, 3, String.format("Device Data Base (DDB) Error\nCan't load the Facility '%1s' (ID: %2s)",l.getName(), l.getId()),e));
                CentralLogger.getInstance().error(this, e);
            }
            return null;
        }
        if (parent instanceof Module) {
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
        } else if (parent instanceof Node) {
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
