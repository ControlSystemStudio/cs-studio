/*
 *
 */
package de.desy.language.editor.core.parser;

import org.eclipse.osgi.util.NLS;

/**
 * TODO (valett) :
 *
 * @author valett
 * @author $Author$
 * @version $Revision$
 * @since 21.06.2010
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "de.desy.language.editor.core.parser.messages"; //$NON-NLS-1$
    public static String Node_Contract_Ensure_end_ge_0_todoParam;
    public static String Node_Contract_Ensure_end_ge_start_todoParam;
    public static String Node_Contract_Ensure_result_ge_0_todoParam;
    public static String Node_Contract_Ensure_start_ge_0_todoParam;
    public static String Node_Contract_Ensure_this_containsErrors;
    public static String Node_Contract_Ensure_this_containsWarnings;
    public static String Node_Contract_Ensure_this_containsWarnings_trim_length_ge_0;
    public static String Node_Contract_Require_hasChildren;
    public static String Node_Contract_Require_this_hasOffsets;
    public static String Node_Contract_RequireNotNull_child_todoParam;
    public static String Node_Contract_require_errorMessage_trim_length_ge_0_todoParam;
    public static String Node_Contract_requireNotNull_errorMessage_todoParam;
    public static String Node_Contract_requireNotNull_warningMessage;
    public static String RootNode_ReturnMessage_Root;
    public static String RootNode_ReturnMessage_ThisIsTheRoot;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
