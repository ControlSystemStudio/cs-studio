package de.desy.language.snl.diagram.model;

import java.util.LinkedList;
import java.util.List;

public class WhenParserUtil {

    public static List<WhenModel> getWhens(String whens) {
        List<WhenModel> list = new LinkedList<WhenModel>();
        if (whens.contains(",")) {
            String[] whenArray = whens.split(", ");
            for (String whenString : whenArray) {
                WhenModel model = WhenParserUtil.getWhen(whenString);
                if (model!=null) {
                    list.add(model);
                }
            }
        } else {
            WhenModel model = WhenParserUtil.getWhen(whens);
            if (model!=null) {
                list.add(model);
            }
        }

        return list;
    }

    private static WhenModel getWhen(String whenString) {
        if (whenString.contains(":")) {
            String[] whenParts = whenString.split(":");
            if (whenParts.length==2) {
                return new WhenModel(whenParts[0], whenParts[1]);
            }
        }
        return null;
    }

}
