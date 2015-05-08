package org.csstudio.dct.ui.workbenchintegration;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IFolder;

/**
 * UI adapter for folders.
 *
 * @author Sven Wende
 */
public class FolderWorkbenchAdapter extends BaseWorkbenchAdapter<IFolder> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Object[] doGetChildren(IFolder folder) {
        List<Object> result = new ArrayList<Object>();
        result.addAll(folder.getMembers());
        return result.toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final String doGetLabel(IFolder folder) {
        return folder.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetIcon(IFolder folder) {
        return "icons/folder.png";
    }
}
