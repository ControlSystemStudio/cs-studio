package org.csstudio.pvmanager.formula.channelfinder;

import org.diirt.datasource.formula.FormulaFunctionSet;
import org.diirt.datasource.formula.FormulaFunctionSetDescription;

/**
 *
 * @author Kunal Shroff
 *
 */
public class ChannelFinderFunctionSet extends FormulaFunctionSet {

    public ChannelFinderFunctionSet() {
        super(new FormulaFunctionSetDescription("cf", "Functions to query ChannelFinder service")
        .addFormulaFunction(new CFQueryFunction()));
    }

}
