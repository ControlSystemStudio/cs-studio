/**
 *
 */
package org.csstudio.utility.pvmanager.ui.toolbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.diirt.datasource.formula.FormulaFunction;
import org.diirt.datasource.formula.FormulaFunctionSet;

/**
 * @author carcassi
 *
 */
public class FunctionTreeContentProvider implements ITreeContentProvider {

    private List<FormulaFunctionSet> functionSets;

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    @Override
    public void dispose() {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
     * .viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.functionSets = (List<FormulaFunctionSet>) newInput;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return functionSets.toArray();
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof FormulaFunctionSet) {
            List<FormulaFunction> functions = new ArrayList<FormulaFunction>(
                    ((FormulaFunctionSet) parentElement).getFunctions());
            Collections.sort(functions, new Comparator<FormulaFunction>() {

                @Override
                public int compare(FormulaFunction o1, FormulaFunction o2) {
                    int result = o1.getName().compareTo(o2.getName());
                    if (result != 0) {
                        return result;
                    }
                    return Integer.compare(o1.getArgumentTypes().size(), o2.getArgumentTypes().size());
                }
            });
            return functions.toArray();
        }
        return null;
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof FormulaFunctionSet) {
            return !((FormulaFunctionSet) element).getFunctions().isEmpty();
        }
        return false;
    }
}
