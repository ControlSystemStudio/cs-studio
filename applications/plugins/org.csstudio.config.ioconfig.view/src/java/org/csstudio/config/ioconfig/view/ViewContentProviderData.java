package org.csstudio.config.ioconfig.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
	@SuppressWarnings("unchecked")
	public void inputChanged(@Nullable final Viewer v,
			@Nullable final Object oldInput, @Nullable final Object newInput) {
	    if(newInput == null && _facilities == null) {
	        _facilities = new ArrayList<FacilityDBO>();
	    } else  if (newInput instanceof FacilityDBO) {
			_facilities.add((FacilityDBO) newInput);
		} else if (newInput instanceof List) {
			_facilities = (List<FacilityDBO>) newInput;
		} else if (newInput instanceof String) {
			_wait = (String) newInput;
		}

	}

	/** {@inheritDoc} */
	@Override
	public void dispose() {
		// nothing to dispose
	}

	/** {@inheritDoc} */
	@Override
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
	public Object getParent(@Nullable final Object child) {
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
	@Override
	public Object[] getChildren(@Nullable final Object parent) {

		if (parent instanceof ModuleDBO) {
			ModuleDBO module = (ModuleDBO) parent;
			return handleModule(module);
		} else if (parent instanceof ChannelStructureDBO) {
			ChannelStructureDBO cs = (ChannelStructureDBO) parent;
			return handleChannelStructure(cs);
		} else if (parent instanceof AbstractNodeDBO) {
			return ((AbstractNodeDBO) parent).getChildrenAsMap().values()
					.toArray(new AbstractNodeDBO[0]);
		}

		return new NamedDBClass[0];
	}

	/**
	 * @param cs
	 * @return
	 */
	 @Nonnull
	private Object[] handleChannelStructure(@Nonnull ChannelStructureDBO cs) {
		if (cs.isSimple()) {
			return cs.getChildrenAsMap().values()
					.toArray(new AbstractNodeDBO[0]);
		} else {
			Collection<? extends AbstractNodeDBO> values = cs
					.getChildrenAsMap().values();
			if (cs.getChildrenAsMap().containsKey((short) -1)) {
				values.remove(values.iterator().next());
			}
			return values.toArray(new AbstractNodeDBO[0]);
		}
	}

	/**
	 * @param module
	 * @return
	 */
	 @Nonnull
	private Object[] handleModule(@Nonnull ModuleDBO module) {
		Collection<ChannelStructureDBO> values = module
				.getChannelStructsAsMap().values();
		List<AbstractNodeDBO> list = new ArrayList<AbstractNodeDBO>(
				values.size());
		for (ChannelStructureDBO channelStructure : values) {
			if (channelStructure.isSimple()) {
				list.addAll(channelStructure.getChildrenAsMap().values());
			} else {
				list.add(channelStructure);
			}
		}
		return list.toArray(new AbstractNodeDBO[list.size()]);
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren(@Nullable final Object parent) {
		return getChildren(parent).length > 0;
	}

}
