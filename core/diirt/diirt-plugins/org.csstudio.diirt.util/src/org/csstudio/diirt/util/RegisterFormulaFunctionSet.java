package org.csstudio.diirt.util;

import java.util.logging.Logger;

import org.diirt.datasource.formula.FormulaFunctionSet;
import org.diirt.datasource.formula.FormulaRegistry;

public class RegisterFormulaFunctionSet {

    private static final Logger logger = Logger.getLogger(RegisterFormulaFunctionSet.class.getCanonicalName());

    public void registerFormulaFunctionSet(FormulaFunctionSet formulaFunctionSet) {
        logger.info("register FormulaFunctionSet:" + formulaFunctionSet.getName());
        FormulaRegistry.getDefault().registerFormulaFunctionSet(
                formulaFunctionSet);
    }

    public void deregisterFormulaFunctionSet(
            FormulaFunctionSet formulaFunctionSet) {
        logger.info("deregister FormulaFunctionSet:" + formulaFunctionSet.getName());
    }

}
