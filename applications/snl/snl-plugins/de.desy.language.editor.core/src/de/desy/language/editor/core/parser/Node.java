package de.desy.language.editor.core.parser;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Collection;

import de.desy.language.libraries.utils.contract.Contract;

/**
 * A node in the outline tree for exactly one document.
 *
 * @author C1 WPS / KM, MZ
 */
public abstract class Node {

    private int _statementEndOffset;
    private int _statementStartOffset;
    private List<Node> _children = new LinkedList<Node>();
    private final Set<String> _errorMessages = new HashSet<String>();
    private final Set<String> _warningMessages = new HashSet<String>();

    public Node() {
        this._statementStartOffset = -1;
        this._statementEndOffset = -1;
    }

    /**
     * Gives the children of this node.
     *
     * @return A not-null array of nodes which are the children of this node (this array may be empty!).
     */
    public final Node[] getChildrenNodesAsArray()
    {
        Node[] result = this._children.toArray(new Node[this._children.size()]);

        Contract.ensureResultNotNull(result);
        return result;
    }

    /**
     * Gives the children of this node.
     *
     * @require hasChildren()
     * @return A not-null Set of nodes which are the children of this node.
     */
    public final Collection<Node> getChildrenNodes()
    {
        Contract.require(hasChildren(), Messages.Node_Contract_Require_hasChildren);

        Collection<Node> result = Collections.unmodifiableCollection(this._children);

        Contract.ensureResultNotNull(result);
        return result;
    }

    /**
     * Determines if this node has children.
     */
    public final boolean hasChildren() {
        return this._children.size() > 0;
    }

    /**
     * Gives the statements ending index in the source. Pay attention, this
     * index may not be accurate if changes happens since the last call of save.
     *
     * To enable this feature, you have to set the offsets by internally calling
     * {@link #setStatementOffsets(int, int)}.
     *
     * @return An index >= 0.
     */
    public final int getStatementEndOffset() {
        Contract.require(this.hasOffsets(), Messages.Node_Contract_Require_this_hasOffsets);

        final int result = this._statementEndOffset;

        Contract.ensure(result >= 0, Messages.Node_Contract_Ensure_result_ge_0_todoParam);
        return result;
    }

    /**
     * Gives the statements beginning index in the source. Pay attention, this
     * index may not be accurate if changes happens since the last call of save.
     *
     * To enable this feature, you have to set the offsets by internally calling
     * {@link #setStatementOffsets(int, int)}.
     *
     * @return An index >= 0.
     */
    public final int getStatementStartOffset() {
        Contract.require(this.hasOffsets(), Messages.Node_Contract_Require_this_hasOffsets);

        final int result = this._statementStartOffset;

        Contract.ensure(result >= 0, Messages.Node_Contract_Ensure_result_ge_0_todoParam);
        return result;
    }

    /**
     * Indicates if source offsets of the statement are avail.
     *
     * To enable this feature, you have to set the offsets by internally calling
     * {@link #setStatementOffsets(int, int)}.
     */
    public final boolean hasOffsets() {
        return (this._statementStartOffset >= 0)
                && (this._statementEndOffset >= 0);
    }

    public abstract String getNodeTypeName();

    /**
     * Gives the name to be shown for this note.
     *
     * @return A not null non empty string.
     */
    public abstract String humanReadableRepresentation();

    @Override
    final public String toString() {
        return this.humanReadableRepresentation();
    }

    /**
     * Used to internally set the offsets of a statement in the source.
     *
     * @param start
     *            The start offset (>= 0)
     * @param end
     *            The end offset (>= 0, end > start)
     */
    protected final void setStatementOffsets(final int start, final int end) {
        Contract.require(start >= 0, Messages.Node_Contract_Ensure_start_ge_0_todoParam);
        Contract.require(end >= 0, Messages.Node_Contract_Ensure_end_ge_0_todoParam);
        Contract.require(end > start, Messages.Node_Contract_Ensure_end_ge_start_todoParam);

        this._statementStartOffset = start;
        this._statementEndOffset = end;
    }

    /**
     * Adds a child node to this node. Currently this is only done be the parser
     * which uses the statement contents to determine the child-nodes. This is
     * done to avoid a hierarchical structure of the parser-process.
     */
    public final void addChild(final Node child) {
        Contract.requireNotNull(Messages.Node_Contract_RequireNotNull_child_todoParam, child);
        this._children.add(child);
    }

    //
    // public final void removeChild(final Node child) {
    // this._children.remove(child);
    // }

    /**
     * Checks if this node contains any error messages.
     *
     * @return {@code true} if and only if error messages are added to this
     *         node, {@code false} otherwise.
     */
    public final boolean containsErrors() {
        return !this._errorMessages.isEmpty();
    }

    /**
     * Checks if this node contains any warning messages.
     *
     * @return {@code true} if and only if warning messages are added to this
     *         node, {@code false} otherwise.
     */
    public final boolean containsWarnings() {
        return !this._warningMessages.isEmpty();
    }

    /**
     * Adds a new error message to this node.
     *
     * @param errorMessage
     *            The not-null, non-empty error message to be added.
     */
    public synchronized final void addError(final String errorMessage) {
        Contract.requireNotNull(Messages.Node_Contract_requireNotNull_errorMessage_todoParam, errorMessage);
        Contract.require(errorMessage.trim().length() > 0,
                Messages.Node_Contract_require_errorMessage_trim_length_ge_0_todoParam);

        this._errorMessages.add(errorMessage);

        Contract.ensure(this.containsErrors(), Messages.Node_Contract_Ensure_this_containsErrors);
    }

    /**
     * Adds a new warning message to this node.
     *
     * @param errorMessage
     *            The not-null, non-empty warning message to be added.
     */
    public synchronized final void addWarning(final String warningMessage) {
        Contract.requireNotNull(Messages.Node_Contract_requireNotNull_warningMessage, warningMessage);
        Contract.require(warningMessage.trim().length() > 0,
                Messages.Node_Contract_Ensure_this_containsWarnings_trim_length_ge_0);

        this._warningMessages.add(warningMessage);

        Contract.ensure(this.containsWarnings(), Messages.Node_Contract_Ensure_this_containsWarnings);
    }

    /**
     * Gives all nodes beginning with this one and including all children-node
     * which contains warnings. The current may not be present in resulting
     * list, if the current node does not contains warnings.
     *
     * @return A not-null but possible empty set containing all significant
     *         nodes; the result is an unmodifiable view on a set.
     */
    public final Set<Node> getAllWarningNodes() {
        Set<Node> result = new HashSet<Node>();
        if (this.containsWarnings()) {
            result.add(this);
        }
        for (final Node child : this.getChildrenNodesAsArray()) {
            result.addAll(child.getAllWarningNodes());
        }

        result = Collections.unmodifiableSet(result);

        Contract.ensureResultNotNull(result);
        return result;
    }

    /**
     * Gives all nodes beginning with this one and including all children-node
     * which contains errors. The current may not be present in resulting list,
     * if the current node does not contains errors.
     *
     * @return A not-null but possible empty set containing all significant
     *         nodes; the result is an unmodifiable view on a set.
     */
    public final Set<Node> getAllErrorNodes() {
        Set<Node> result = new HashSet<Node>();
        if (this.containsErrors()) {
            result.add(this);
        }
        for (final Node child : this.getChildrenNodesAsArray()) {
            result.addAll(child.getAllErrorNodes());
        }

        result = Collections.unmodifiableSet(result);

        Contract.ensureResultNotNull(result);
        return result;
    }

    /**
     * Returns a unmodifiable view on a Set of error messages.
     *
     * @require this.containsErrors()
     * @return A not-null, possible empty Set of errors.
     */
    public final Set<String> getErrorMessages() {
        Contract.require(this.containsErrors(), Messages.Node_Contract_Ensure_this_containsErrors);

        Set<String> result = Collections.unmodifiableSet(this._errorMessages);

        Contract.ensureResultNotNull(result);
        return result;
    }

    /**
     * Returns a unmodifiable view on a Set of warning messages.
     *
     * @require this.containsWarnings()
     * @return A not-null, possible empty Set of warnings.
     */
    public final Set<String> getWarningMessages() {
        Contract.require(this.containsWarnings(), Messages.Node_Contract_Ensure_this_containsWarnings);

        Set<String> result = Collections.unmodifiableSet(this._warningMessages);

        Contract.ensureResultNotNull(result);
        return result;
    }
}
