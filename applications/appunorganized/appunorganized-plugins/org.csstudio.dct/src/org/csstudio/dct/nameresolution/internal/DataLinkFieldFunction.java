package org.csstudio.dct.nameresolution.internal;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.DctActivator;
import org.csstudio.dct.PreferenceSettings;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.nameresolution.FieldFunctionContentProposal;
import org.csstudio.dct.nameresolution.IFieldFunction;
import org.csstudio.dct.nameresolution.RecordFinder;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.domain.common.strings.StringUtil;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.fieldassist.IContentProposal;

/**
 * Implementation for the datalink() function.
 *
 * @author Sven Wende
 *
 */
public final class DataLinkFieldFunction implements IFieldFunction {

    /**
     *{@inheritDoc}
     */
    @Override
    public String evaluate(String name, String[] parameters, IRecord record, String fieldName) throws Exception {
        IRecord r = RecordFinder.findRecordByPath(parameters[0], record.getContainer());

        String result = null;

        if (r != null) {
            StringBuffer sb = new StringBuffer();
            sb.append(AliasResolutionUtil.getEpicsNameFromHierarchy(r));

            String field = parameters[1];

            if (field != null && !field.trim().equals("")) {
                sb.append(".");
                sb.append(field);
            }

            sb.append(" ");
            sb.append(parameters[2]);
            sb.append(" ");
            sb.append(parameters[3]);
            result = sb.toString();
        } else {
            result = "No Record found";
        }

        return result;
    }

    public List<IContentProposal> getParameterProposal(int parameterIndex, String[] knowParameters, IRecord record) {
        List<IContentProposal> result = new ArrayList<IContentProposal>();

        switch (parameterIndex) {
        case 0:
            String para = "";
            if(knowParameters.length>0) {
                para = knowParameters[0]==null?"":knowParameters[0];
            }
            for (IRecord r : record.getContainer().getRecords()) {
                String name = AliasResolutionUtil.getNameFromHierarchy(r);
                if(name.startsWith(para)) {
                    result.add(new FieldFunctionContentProposal(name, name, "Reference to record [" + name + "]", name.length()));
                }
            }
            break;
        case 1:
            if (knowParameters.length > 0) {
                IRecord r = RecordFinder.findRecordByPath(knowParameters[0], record.getContainer());
                if (r != null) {
                    for (String f : r.getFinalFields().keySet()) {
                        result.add(new FieldFunctionContentProposal(f, f, f + " field", f.length()));
                    }
                }
            }
            break;
        case 2:
            result.addAll(createProposalsFromPreferences(PreferenceSettings.DATALINK_FUNCTION_PARAMETER_3_PROPOSAL));
            break;
        case 3:
            result.addAll(createProposalsFromPreferences(PreferenceSettings.DATALINK_FUNCTION_PARAMETER_4_PROPOSAL));
            break;
        default:
            break;
        }
        return result;
    }

    private List<IContentProposal> createProposalsFromPreferences(PreferenceSettings key) {
        List<IContentProposal> result = new ArrayList<IContentProposal>();

        String proposals = Platform.getPreferencesService().getString(DctActivator.PLUGIN_ID, key.name(), "", null);

        if (StringUtil.hasLength(proposals)) {
            for (String p : proposals.split(",")) {
                result.add(new FieldFunctionContentProposal(p, p, p, p.length()));
            }
        }

        return result;
    }
}
