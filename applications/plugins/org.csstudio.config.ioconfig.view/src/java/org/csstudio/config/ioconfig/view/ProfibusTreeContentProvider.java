package org.csstudio.config.ioconfig.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructureDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @since 19.06.2007
 */
class ProfibusTreeContentProvider implements ITreeContentProvider {

    /** The Tree Root Node. */
    private List<FacilityDBO> _facilities;
    private String _wait;

    public ProfibusTreeContentProvider() {
        // Default Constructor.
    }

    /** {@inheritDoc} */
    @Override
    public void dispose() {
        // nothing to dispose
    }

    /**
     * The Object can Typ of NodeParent or Object.
     *
     * @param parent
     *            The Parent Object.
     * @return an Array of Children Objects.
     */
    @Override
    @Nonnull
    public Object[] getChildren(@Nullable final Object parent) {
        if (parent instanceof ModuleDBO) {
            final ModuleDBO module = (ModuleDBO) parent;
            return handleModule(module);
        } else if (parent instanceof ChannelStructureDBO) {
            final ChannelStructureDBO cs = (ChannelStructureDBO) parent;
            return handleChannelStructure(cs);
        } else if (parent instanceof AbstractNodeDBO) {
            return ((AbstractNodeDBO<?, ?>) parent).getChildrenAsMap().values()
                    .toArray(new AbstractNodeDBO[0]);
        }
        return new NamedDBClass[0];
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull
    public Object[] getElements(@Nullable final Object parent) {
        if (_facilities != null) {
            return _facilities.toArray();
        } else if (_wait != null) {
            return new String[] {_wait };
        }
        return new Object[0];
    }

    /** {@inheritDoc} */
    @Override
    @CheckForNull
    public Object getParent(@Nullable final Object child) {
        if (child instanceof AbstractNodeDBO) {
            return ((AbstractNodeDBO<?, ?>) child).getParent();
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasChildren(@Nullable final Object parent) {
        return getChildren(parent).length > 0;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public void inputChanged(@Nullable final Viewer v,
                             @Nullable final Object oldInput,
                             @Nullable final Object newInput) {
        if (newInput == null && _facilities == null) {
            _facilities = new ArrayList<FacilityDBO>();
        } else if (newInput instanceof FacilityDBO) {
            _facilities.add((FacilityDBO) newInput);
        } else if (newInput instanceof List) {
            _facilities = (List<FacilityDBO>) newInput;
        } else if (newInput instanceof String) {
            _facilities = null;
            _wait = (String) newInput;
        }

    }

    @Nonnull
    private Object[] handleChannelStructure(@Nonnull final ChannelStructureDBO cs) {
        if (cs.isSimple()) {
            return cs.getChildrenAsMap().values().toArray(new AbstractNodeDBO[0]);
        }
        final Collection<? extends AbstractNodeDBO<?, ?>> values = cs.getChildrenAsMap().values();
        if (cs.getChildrenAsMap().containsKey((short) -1)) {
            values.remove(values.iterator().next());
        }
        return values.toArray(new AbstractNodeDBO[0]);
    }

    @Nonnull
    private Object[] handleModule(@Nonnull final ModuleDBO module) {
        final Collection<ChannelStructureDBO> values = module.getChildrenAsMap().values();
        final List<AbstractNodeDBO<?, ?>> list = new ArrayList<AbstractNodeDBO<?, ?>>(values.size());
        for (final ChannelStructureDBO channelStructure : values) {
            if (channelStructure.isSimple()) {
                list.addAll(channelStructure.getChildrenAsMap().values());
            } else {
                list.add(channelStructure);
            }
        }
        return list.toArray(new AbstractNodeDBO[list.size()]);
    }

}
