package org.csstudio.dct.nameresolution.internal;

import java.util.Collections;
import java.util.List;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.nameresolution.IFieldFunction;
import org.eclipse.jface.fieldassist.IContentProposal;

import com.bestcode.mathparser.IMathParser;
import com.bestcode.mathparser.MathParserFactory;

/**
 * Implementation for the forwardlink() function.
 *
 * @author Sven Wende
 *
 */
public final class EvalFieldFunction implements IFieldFunction {

    /**
     *{@inheritDoc}
     */
    @Override
    public String evaluate(String name, String[] parameters, IRecord record, String fieldName) throws Exception {
        String result = "";

        try {
            IMathParser parser = MathParserFactory.create();
            parser.setExpression(parameters[0]);
            result = "" + parser.getValue();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error in mathematical expression.");
        }
        return result;
    }

    @Override
    public List<IContentProposal> getParameterProposal(int parameterIndex, String[] knownParameters, IRecord record) {
        return Collections.EMPTY_LIST;
    }

}
