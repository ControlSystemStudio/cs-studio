/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class SymbolUtils {

    /**
     * Allowed images extensions
     */
    public static final String[] IMAGE_EXTENSIONS = new String[] { "gif", "png", "svg", "GIF", "PNG", "SVG" };

    /**
     * Regular expression to find base state position in image name
     */
    private static final String STATE_MARKER = "@@state@@";

    /**
     * Constructor cannot be call because of static invocation.
     */
    private SymbolUtils() {
    }

    /**
     * Check if the file path exists into the current workspace.
     *
     * @param path
     * @return the check result
     */
    private static boolean isFileExists(IPath path) {
        return ResourceUtil.isExsitingFile(path, false);
    }

    public static boolean isExtensionAllowed(IPath imagePath) {
        List<String> extList = Arrays.asList(IMAGE_EXTENSIONS);
        return extList.contains(imagePath.getFileExtension());
    }

    public static String getBaseImagePath(IPath imagePath, Map<Integer, String> statesMap) {
        if (imagePath == null || imagePath.isEmpty())
            return null;
        for (Entry<Integer, String> state : statesMap.entrySet()) {
            String path = extractBasePath(imagePath, String.valueOf(state.getKey()));
            if (path == null)
                path = extractBasePath(imagePath, String.valueOf(state.getValue()));
            if (path != null)
                return path;
        }
        return null;
    }

    private static String extractBasePath(IPath imagePath, String value) {
        Pattern pattern = Pattern.compile("^(.+)\\s+(?i)(" + value + ")(\\.\\w+)$");
        Matcher matcher = pattern.matcher(imagePath.toString());
        // extract "<absolute base path> <state>.<extension>"
        String ext = null, basePath = null;
        if (matcher.matches()) {
            basePath = matcher.group(1);
            ext = matcher.group(3);
            // replace <state> by the state marker
            String path = basePath + " " + STATE_MARKER + ext;
            return path;
        }
        return null;
    }

    // Bug 3479: update widget to use state index instead of string value
    public static IPath searchStateImage(int stateIndex, String basePath) {
        if (basePath == null || basePath.isEmpty())
            return null;
        String path = basePath.replace(STATE_MARKER, String.valueOf(stateIndex));
        IPath stateImagePath = new Path(path);
        if (isFileExists(stateImagePath)) {
            return stateImagePath;
        }
        return null;
    }

    public static IPath searchStateImage(String state, String basePath) {
        if (basePath == null || basePath.isEmpty())
            return null;
        String path = basePath.replace(STATE_MARKER, state);
        IPath stateImagePath = new Path(path);
        if (isFileExists(stateImagePath)) {
            return stateImagePath;
        }
        return null;
    }

}
