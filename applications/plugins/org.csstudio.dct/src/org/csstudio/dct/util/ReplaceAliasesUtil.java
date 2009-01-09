/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.dct.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.platform.util.StringUtil;

/**
 * A utility class, which helps to validate inputs for or apply alias
 * substitutions to channel names.
 * 
 * @author Sven Wende
 * 
 * @deprecated: TODO: Methoden nach {@link AliasResolutionUtil} verschieben
 * 
 */
final class ReplaceAliasesUtil {

	/**
	 * Private constructor.
	 */
	private ReplaceAliasesUtil() {
	}

	/**
	 * Regular expression which is used to find alias names in an arbitrary
	 * string.
	 */
	private static final Pattern FIND_ALIAS_NAME_PATTERN = Pattern.compile("\\$([^$]*)\\$");

	/**
	 * Regular expression which is used to validate arbitrary inputs. In general
	 * any text is valid. Optionally inputs can contain aliases, which are
	 * bordered by "$" signs. In this case, the input must contain an even
	 * number of "$" signs and at least one other sign between two "$" signs.
	 */
	private static final Pattern VALIDATE_TEXT_PATTERN = Pattern.compile("[^$]*([^$]*\\$[^$]+\\$[^$]*)*[^$]*");

	/**
	 * Generates a regular expression which is used to replace aliases in
	 * arbitrary texts.
	 * 
	 * @param aliasName
	 *            the alias name, which should be found (without the bordering
	 *            "$" signs
	 * 
	 * @return a regular expression which is used to replace aliases in
	 *         arbitrary texts
	 * 
	 */
	private static Pattern createSearchPattern(final String aliasName) {
		return Pattern.compile("(\\$" + aliasName + "\\$)");
	}

	public static List<String> getRequiredAliasNames(final String input) {
		List<String> result = new ArrayList<String>();

		// Get a Matcher based on the target string.
		Matcher matcher = FIND_ALIAS_NAME_PATTERN.matcher(input);

		// Find all the matches.
		while (matcher.find()) {
			String name = matcher.group(1);
			result.add(name);
		}

		return result;
	}

	/**
	 * Tests the validity of the provided input string. In general any non-empty
	 * text is valid. Optionally inputs can contain aliases, which are bordered
	 * by "$" signs. In this case, the input must contain an even number of "$"
	 * signs and at least one other sign between two "$" signs.
	 * 
	 * @param input
	 *            the text to be checked
	 * @return true, if the text is valid, false otherwise
	 */
	public static boolean testValidity(final String input) {
		boolean result = true;

		if (input == null) {
			result = false;
		} else {
			Matcher m = VALIDATE_TEXT_PATTERN.matcher(input);
			result = m.matches();
		}
		return result;
	}

	/**
	 * Creates a canonical representation of the provided input string in which
	 * all aliases are replaced by their values. All aliases need to be
	 * delivered in a Map.
	 * 
	 * @param input
	 *            the input text
	 * @param aliases
	 *            a map, which contains the aliases
	 * @throws ChannelReferenceValidationException
	 *             this exception is thrown, if the specified input or the
	 *             aliases cannot be processed
	 * 
	 * @return a canonical representation of the provided input string in which
	 *         all aliases are replaced by their values
	 */
	public static String createCanonicalName(final String input, final Map<String, String> aliases) throws AliasResolutionException {
		// check validity of the input
		if (!testValidity(input)) {
			throw new AliasResolutionException("The name >" + input + "< is invalid");
		}

		// check validity of all aliases
		for (String alias : aliases.keySet()) {
			String aliasValue = aliases.get(alias);
			if (!testValidity(aliasValue)) {
				throw new AliasResolutionException("The alias value >" + aliasValue + "< for the alias >" + alias + "< is invalid.");
			}
		}

		return doResolve(input, new ArrayList<String>(), false, aliases);
	}

	/**
	 * This method is called recursively, to apply alias substitutions to the
	 * provided input text.
	 * 
	 * @param input
	 *            the text input
	 * @param markerList
	 *            a marker list, which contains aliases that are already on the
	 *            stack
	 * @param isAlias
	 *            flag, which indicates whether the input is a alias (this is
	 *            necessary because aliases might contain other aliases which is
	 *            handled via the same recursive call)
	 * @param aliases
	 *            the existing aliases as provided by the user
	 * 
	 * @return a canonical name in which all aliases are replaced by their real
	 *         values
	 * 
	 * @throws ChannelReferenceValidationException
	 *             this exception is thrown, if an error occurs during the
	 *             replacement procedure (e.g. in case of circular references
	 *             between aliases (e.g. when $a$=$b$ and $b$=$a$)
	 */
	private static String doResolve(final String input, final List<String> markerList, final boolean isAlias, final Map<String, String> aliases)
			throws AliasResolutionException {

		String result = "";
		
		// .. special treatment if we are resolving an alias
		if (isAlias) {
			if (!aliases.containsKey(input)) {
				 throw new AliasResolutionException("$" + input + "$ is not resolvable.");
			} else {
				if (markerList.contains(input)) {
					throw new AliasResolutionException("$" + input + "$ causes a circular relation.");
				} else {
					result = aliases.get(input);
					markerList.add(input);
				}
			}
		} else {
			result = input;
		}

		// .. find all variables that needs to be replaced by real values
		Matcher matcher = FIND_ALIAS_NAME_PATTERN.matcher(result);

		while (matcher.find()) {
			String alias = matcher.group(1);

			// .. recursively resolve the alias itself 
			String resolvedAlias = doResolve(alias, markerList, true, aliases);
			assert StringUtil.hasLength(resolvedAlias);
			
			// .. replace all occurences of the alias in the initial string
			Matcher matcher2 = createSearchPattern(alias).matcher(result);
			result = matcher2.replaceAll(resolvedAlias);
		}

		return result;
	}
}
