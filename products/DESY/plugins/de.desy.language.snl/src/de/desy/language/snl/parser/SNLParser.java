/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR
 * PURPOSE AND  NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, 
 * REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL
 * PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER 
 * EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE 
 * SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND 
 * OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU 
 * MAY FIND A COPY AT {@link http://www.desy.de/legal/license.htm}
 */
package de.desy.language.snl.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import de.desy.language.editor.core.parser.AbstractLanguageParser;
import de.desy.language.editor.core.parser.Node;
import de.desy.language.snl.parser.nodes.AllVariablesNode;
import de.desy.language.snl.parser.nodes.AssignStatementNode;
import de.desy.language.snl.parser.nodes.EntryNode;
import de.desy.language.snl.parser.nodes.EventFlagNode;
import de.desy.language.snl.parser.nodes.ExitNode;
import de.desy.language.snl.parser.nodes.MonitorStatementNode;
import de.desy.language.snl.parser.nodes.OptionStatementNode;
import de.desy.language.snl.parser.nodes.PlaceholderNode;
import de.desy.language.snl.parser.nodes.ProgramNode;
import de.desy.language.snl.parser.nodes.StateNode;
import de.desy.language.snl.parser.nodes.StateSetNode;
import de.desy.language.snl.parser.nodes.SyncStatementNode;
import de.desy.language.snl.parser.nodes.VariableNode;
import de.desy.language.snl.parser.nodes.WhenNode;
import de.desy.language.snl.parser.parser.AssignStatementParser;
import de.desy.language.snl.parser.parser.EntryParser;
import de.desy.language.snl.parser.parser.EventFlagParser;
import de.desy.language.snl.parser.parser.ExitParser;
import de.desy.language.snl.parser.parser.MonitorStatementParser;
import de.desy.language.snl.parser.parser.MultiLineCommentParser;
import de.desy.language.snl.parser.parser.MultiLineEmbeddedCParser;
import de.desy.language.snl.parser.parser.OptionStatementParser;
import de.desy.language.snl.parser.parser.ProgramParser;
import de.desy.language.snl.parser.parser.SingleLineCommentParser;
import de.desy.language.snl.parser.parser.SingleLineEmbeddedCParser;
import de.desy.language.snl.parser.parser.StateParser;
import de.desy.language.snl.parser.parser.StateSetParser;
import de.desy.language.snl.parser.parser.SyncStatemantParser;
import de.desy.language.snl.parser.parser.VariableParser;
import de.desy.language.snl.parser.parser.WhenParser;

/**
 * Outline parser of the SNL language.
 * 
 * @author <a href="mailto:kmeyer@c1-wps.de">Kai Meyer</a>
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.2
 */
public class SNLParser extends AbstractLanguageParser {

	private static String replaceWithWhitespace(final String input,
			final String toReplace) {
		final int length = toReplace.length();
		final char[] replacement = new char[length];
		Arrays.fill(replacement, ' ');
		return input.replace(toReplace, new String(replacement));
	}

	private String _input;

	public SNLParser() {
	}

	@Override
	protected Node doParse(final CharSequence input, IResource sourceResource,
			final IProgressMonitor progressMonitor) {
		if (input.length() == 0) {
			final PlaceholderNode placeholder = new PlaceholderNode(
					"No content to outline avail.");
			return placeholder;
		}
		progressMonitor.beginTask("Parsing outline...",
				IProgressMonitor.UNKNOWN);
		progressMonitor.worked(1);
		this._input = this.removeAllComments(input);
		progressMonitor.worked(2);
		this._input = this.removeAllEmbeddedC(this._input);
		progressMonitor.worked(3);

		Node root = null;

		final ProgramParser programParser = new ProgramParser();
		programParser.findNext(this._input);
		if (programParser.hasFoundElement()) {
			progressMonitor.worked(4);
			final ProgramNode programNode = programParser.getLastFoundAsNode();
			root = programNode;
			final OptionStatementParser optionParser = new OptionStatementParser();
			optionParser.findNext(this._input);
			if (optionParser.hasFoundElement()) {
				final OptionStatementNode optionStatementNode = optionParser
						.getLastFoundAsNode();
				programNode.addChild(optionStatementNode);
				progressMonitor.worked(5);
			}

			// configure the program node:
			this.findAndAddAllVariables(programNode, this._input);
			progressMonitor.worked(6);
			this.findAndAddAllEventFlags(programNode, this._input);
			progressMonitor.worked(7);
			this.findAndAddAllStateSets(programNode, this._input);
			progressMonitor.worked(8);
		} else {
			final PlaceholderNode placeholder = new PlaceholderNode(
					"Missing: program statement");
			if (sourceResource != null) {
				try {
					IMarker errorMarker = sourceResource
							.createMarker(IMarker.PROBLEM);
					errorMarker.setAttribute(IMarker.SEVERITY,
							IMarker.SEVERITY_ERROR);
					errorMarker
							.setAttribute(
									IMarker.MESSAGE,
									"Missing introducing program statement, place a statement like \"program MyProgramName\"");
				} catch (CoreException e) {
					// No need to handle!
					// TODO Decide for a better exception handling, maybee
					// extend
					// interface to transport exception!
					e.printStackTrace();
				}
			}
			root = placeholder;
		}
		progressMonitor.done();
		return root;
	}

	private void findAndAddAllEntrys(final Node node, final String input) {
		final EntryParser entryParser = new EntryParser();
		entryParser.findNext(input);
		while (entryParser.hasFoundElement()) {
			final EntryNode entryNode = entryParser.getLastFoundAsNode();
			final int startOffset = entryNode.getStatementStartOffset()
					+ node.getStatementStartOffset();
			final int endOffset = entryNode.getStatementEndOffset()
					+ node.getStatementStartOffset() + 1;
			entryNode.setOffsets(startOffset, endOffset);
			node.addChild(entryNode);
			final int lastFound = entryParser.getEndOffsetLastFound();
			entryParser.findNext(input, lastFound);
		}
	}

	private void findAndAddAllEventFlags(final Node node, final String input) {
		final Map<String, EventFlagNode> eventFlags = new HashMap<String, EventFlagNode>();

		final EventFlagParser eventFlagParser = new EventFlagParser();

		eventFlagParser.findNext(input);

		while (eventFlagParser.hasFoundElement()) {
			final EventFlagNode varNode = eventFlagParser.getLastFoundAsNode();
			node.addChild(varNode);

			eventFlags.put(varNode.getSourceIdentifier(), varNode);

			final int lastEndPosition = eventFlagParser.getEndOffsetLastFound();

			// search next one
			eventFlagParser.findNext(input, lastEndPosition);
		}

		final SyncStatemantParser syncParser = new SyncStatemantParser();
		syncParser.findNext(input);

		while (syncParser.hasFoundElement()) {
			final SyncStatementNode syncNode = syncParser.getLastFoundAsNode();
			final EventFlagNode eventNode = eventFlags.get(syncNode
					.getSourceIdentifier());
			if (eventNode == null) {
				syncNode.addWarning("No event flag definition found for '"
						+ syncNode.getSourceIdentifier() + "'");
				node.addChild(syncNode);
			} else {
				eventNode.setSynchronized(syncNode);
			}

			final int lastEndPosition = syncParser.getEndOffsetLastFound();

			// search next one
			syncParser.findNext(input, lastEndPosition);
		}
	}

	private void findAndAddAllExits(final Node node, final String input) {
		final ExitParser exitParser = new ExitParser();
		exitParser.findNext(input);
		while (exitParser.hasFoundElement()) {
			final ExitNode exitNode = exitParser.getLastFoundAsNode();
			final int startOffset = exitNode.getStatementStartOffset()
					+ node.getStatementStartOffset();
			final int endOffset = exitNode.getStatementEndOffset()
					+ node.getStatementStartOffset() + 1;
			exitNode.setOffsets(startOffset, endOffset);
			node.addChild(exitNode);
			final int lastFound = exitParser.getEndOffsetLastFound();
			exitParser.findNext(input, lastFound);
		}
	}

	private void findAndAddAllStates(final Node node, final CharSequence input) {
		final StateParser stateParser = new StateParser();
		stateParser.findNext(input);
		while (stateParser.hasFoundElement()) {
			final StateNode stateNode = stateParser.getLastFoundAsNode();
			final int startOffset = stateNode.getStatementStartOffset()
					+ node.getStatementStartOffset();
			final int endOffset = stateNode.getStatementEndOffset()
					+ node.getStatementStartOffset() + 1;
			stateNode.setStartOffsets(startOffset, endOffset);
			node.addChild(stateNode);
			final String lastFoundStatement = stateParser
					.getLastFoundStatement();
			this.findAndAddAllEntrys(stateNode, lastFoundStatement);
			this.findAndAddAllWhens(stateNode, lastFoundStatement);
			this.findAndAddAllExits(stateNode, lastFoundStatement);
			final int lastFound = stateParser.getEndOffsetLastFound();
			stateParser.findNext(input, lastFound);
		}
	}

	private CharSequence findAndAddAllStateSets(final Node node,
			final String input) {
		final String result = input;
		final StateSetParser stateSetParser = new StateSetParser();
		stateSetParser.findNext(result);
		while (stateSetParser.hasFoundElement()) {
			final StateSetNode stateSetNode = stateSetParser
					.getLastFoundAsNode();
			node.addChild(stateSetNode);
			final String lastFoundStatement = stateSetParser
					.getLastFoundStatement();

			this.findAndAddAllStates(stateSetNode, lastFoundStatement);
			final int lastFound = stateSetParser.getEndOffsetLastFound();
			stateSetParser.findNext(result, lastFound);
		}

		return result;
	}

	private void findAndAddAllVariables(final Node node, final String input) {
		final Map<String, VariableNode> variableMap = new HashMap<String, VariableNode>();
		final Map<String, AssignStatementNode> assignMap = new HashMap<String, AssignStatementNode>();

		AllVariablesNode variableParentNode = new AllVariablesNode();
		node.addChild(variableParentNode);

		final VariableParser variableParser = new VariableParser();

		variableParser.findNext(input);

		while (variableParser.hasFoundElement()) {
			final VariableNode varNode = variableParser.getLastFoundAsNode();
			variableParentNode.addChild(varNode);

			variableMap.put(varNode.getSourceIdentifier(), varNode);

			final int lastEndPosition = variableParser.getEndOffsetLastFound();

			// search next one
			variableParser.findNext(input, lastEndPosition);
		}

		final AssignStatementParser assignParser = new AssignStatementParser();
		assignParser.findNext(input);

		while (assignParser.hasFoundElement()) {
			final AssignStatementNode assignNode = assignParser
					.getLastFoundAsNode();
			assignMap.put(assignNode.getSourceIdentifier(), assignNode);
			final VariableNode varNode = variableMap.get(assignNode
					.getSourceIdentifier());
			if (varNode == null) {
				assignNode.addWarning("No variable definition found for '"
						+ assignNode.getSourceIdentifier() + "'");
				variableParentNode.addChild(assignNode);
			} else {
				varNode.setAssignedChannel(assignNode);
			}

			final int lastEndPosition = assignParser.getEndOffsetLastFound();

			// search next one
			assignParser.findNext(input, lastEndPosition);
		}

		final MonitorStatementParser monitorParser = new MonitorStatementParser();
		monitorParser.findNext(input);

		while (monitorParser.hasFoundElement()) {
			final MonitorStatementNode monitorNode = monitorParser
					.getLastFoundAsNode();
			final VariableNode varNode = variableMap.get(monitorNode
					.getSourceIdentifier());
			final AssignStatementNode assignNode = assignMap.get(monitorNode
					.getSourceIdentifier());
			if (varNode == null) {
				monitorNode.addWarning("No variable definition found for '"
						+ monitorNode.getSourceIdentifier() + "'");
				variableParentNode.addChild(monitorNode);
			} else {
				varNode.setMonitored(monitorNode);
			}
			if (assignNode == null) {
				monitorNode.addWarning("No assign statement found for '"
						+ monitorNode.getSourceIdentifier() + "'");
			}

			final int lastEndPosition = monitorParser.getEndOffsetLastFound();

			// search next one
			monitorParser.findNext(input, lastEndPosition);
		}
	}

	private void findAndAddAllWhens(final Node node, final String input) {
		final WhenParser whenParser = new WhenParser();
		whenParser.findNext(input);
		while (whenParser.hasFoundElement()) {
			final WhenNode whenNode = whenParser.getLastFoundAsNode();
			final int startOffset = whenNode.getStatementStartOffset()
					+ node.getStatementStartOffset();
			final int endOffset = whenNode.getStatementEndOffset()
					+ node.getStatementStartOffset() + 1;
			whenNode.setOffsets(startOffset, endOffset);
			node.addChild(whenNode);
			final int lastFound = whenParser.getEndOffsetLastFound();
			whenParser.findNext(input, lastFound);
		}
	}

	public String getClearedInput() {
		return this._input;
	}

	private String removeAllComments(final CharSequence input) {
		String result = input.toString();

		final MultiLineCommentParser multiCommentParser = new MultiLineCommentParser();
		multiCommentParser.findNext(result);
		while (multiCommentParser.hasFoundElement()) {
			final String comment = multiCommentParser.getLastFoundStatement();
			if (!result.contains(comment)) {
				break;
			}
			result = SNLParser.replaceWithWhitespace(result, comment);
			multiCommentParser.findNext(result);
		}

		final SingleLineCommentParser singleLineCommentParser = new SingleLineCommentParser();
		singleLineCommentParser.findNext(result);
		while (singleLineCommentParser.hasFoundElement()) {
			final String singleCommentLine = singleLineCommentParser
					.getLastFoundStatement();
			if (!result.contains(singleCommentLine)) {
				break;
			}
			result = SNLParser.replaceWithWhitespace(result, singleCommentLine);

			singleLineCommentParser.findNext(result);
		}

		return result;
	}

	private String removeAllEmbeddedC(final String input) {
		String result = input;

		final MultiLineEmbeddedCParser multiLineEmbeddedCParser = new MultiLineEmbeddedCParser();
		multiLineEmbeddedCParser.findNext(result);
		while (multiLineEmbeddedCParser.hasFoundElement()) {
			final String embeddedCline = multiLineEmbeddedCParser
					.getLastFoundStatement();
			if (!result.contains(embeddedCline)) {
				break;
			}
			result = SNLParser.replaceWithWhitespace(result, embeddedCline);

			multiLineEmbeddedCParser.findNext(result);
		}

		final SingleLineEmbeddedCParser singleLineEmbeddedCParser = new SingleLineEmbeddedCParser();
		singleLineEmbeddedCParser.findNext(result);
		while (singleLineEmbeddedCParser.hasFoundElement()) {
			final String embeddedCline = singleLineEmbeddedCParser
					.getLastFoundStatement();
			if (!result.contains(embeddedCline)) {
				break;
			}
			result = SNLParser.replaceWithWhitespace(result, embeddedCline);

			singleLineEmbeddedCParser.findNext(result);
		}

		return result;
	}

}
