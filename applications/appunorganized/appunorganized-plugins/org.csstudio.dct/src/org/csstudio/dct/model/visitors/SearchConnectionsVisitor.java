package org.csstudio.dct.model.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IVisitor;
import org.csstudio.dct.model.internal.Project;
import org.csstudio.dct.nameresolution.RecordFinder;
import org.csstudio.dct.nameresolution.internal.FieldFunctionService;
import org.csstudio.dct.nameresolution.internal.ForwardLinkFieldFunction;

/**
 * Visitor implementation that can be used to find instances using their
 * prototype´s id as search criteria.
 *
 * @author swende
 *
 */
public final class SearchConnectionsVisitor implements IVisitor {
    private Set<ConnectionDescriptor> connections;

    private static final Pattern FIND_FORWARDLINK_PATTERN = Pattern.compile("^>forwardlink\\((.*)\\)$");
    private static final Pattern FIND_DATALINK_PATTERN = Pattern.compile("^>datalink\\((.*)\\)$");

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(Project project) {
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(IFolder folder) {
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(IPrototype prototype) {
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(IRecord record) {
        for (String source : record.getFinalFields().values()) {
            if (source != null) {
                // find forward links
                Matcher matcher = FIND_FORWARDLINK_PATTERN.matcher(source);

                if (matcher.find()) {
                    String[] params = matcher.group(1).split(",");
                    for (int i = 0; i < params.length; i++) {
                        params[i] = params[i].trim();
                    }

                    IRecord target = RecordFinder.findRecordByPath(params[0], record.getContainer());

                    if (target != null) {
                        connections.add(new ConnectionDescriptor(record, target, "forwardlink"));
                    }
                }

                // find data links
                matcher = FIND_DATALINK_PATTERN.matcher(source);

                if (matcher.find()) {
                    String[] params = matcher.group(1).split(",");
                    for (int i = 0; i < params.length; i++) {
                        params[i] = params[i].trim();
                    }

                    IRecord target = RecordFinder.findRecordByPath(params[0], record.getContainer());

                    if (target != null) {
                        connections.add(new ConnectionDescriptor(record, target, "datalink"));
                    }
                }
            }
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(IInstance instance) {
    }

    /**
     * Deep search for record connections (forward and/or datalinks) in a
     * project.
     *
     * @param project
     *            the project
     * @return all connections
     */
    public Set<ConnectionDescriptor> search(IProject project) {
        assert project != null;

        connections = new HashSet<ConnectionDescriptor>();

        project.accept(this);

        return connections;
    }

    public static class ConnectionDescriptor {
        private IRecord source;
        private IRecord target;
        private String details;

        private ConnectionDescriptor(IRecord source, IRecord target, String details) {
            this.details = details;
            this.source = source;
            this.target = target;
        }

        public IRecord getSource() {
            return source;
        }

        public IRecord getTarget() {
            return target;
        }

        public String getDetails() {
            return details;
        }

    }
}
