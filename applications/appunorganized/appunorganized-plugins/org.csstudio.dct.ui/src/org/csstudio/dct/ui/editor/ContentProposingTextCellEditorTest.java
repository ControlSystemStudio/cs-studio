package org.csstudio.dct.ui.editor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class ContentProposingTextCellEditorTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
    }

    public void testProposalPopupClosed() {
        Pattern pattern = Pattern.compile(">([^(]+)\\((([^,()]*[,]?)*)");
        Matcher matcher = pattern.matcher(">datalink(a,b,");

        if (matcher.matches()) {
            String name = matcher.group(1);
            String parameters = matcher.group(2);

            int param = 0;
            for (int i=0;i<parameters.length();i++) {
                if(",".equals(parameters.substring(i,i+1))) {
                    param++;
                }
            }

            System.out.println(param);
        }else {
            System.out.println("nomatch");
        }
    }

}
