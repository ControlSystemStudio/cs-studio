package org.csstudio.dct.model.visitors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IVisitor;
import org.csstudio.dct.model.internal.Project;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.AliasResolutionException;
import org.csstudio.platform.util.StringUtil;

/**
 * Visitor implementation that determines logical and semantic errors in a DCT
 * project.
 * 
 * @author Sven Wende
 * 
 */
public class ProblemVisitor implements IVisitor {
	private Map<String, Set<UUID>> finalRecordNames;
	private Set<Error> errors;

	/**
	 * Constructor.
	 */
	public ProblemVisitor() {
		errors = new HashSet<Error>();
		finalRecordNames = new HashMap<String, Set<UUID>>();
	}

	/**
	 *{@inheritDoc}
	 */
	public void visit(Project project) {
		if (!StringUtil.hasLength(project.getDbdPath())) {
			errors.add(new Error(project.getId(), "No DBD file specified for project."));
		}
	}

	/**
	 *{@inheritDoc}
	 */
	public void visit(IFolder folder) {
		// FIXME: Fehlerermittlung ausprogrammieren
	}

	/**
	 *{@inheritDoc}
	 */
	public void visit(IPrototype prototype) {
		// FIXME: Fehlerermittlung ausprogrammieren
	}

	/**
	 *{@inheritDoc}
	 */
	public void visit(IInstance instance) {
		// FIXME: Fehlerermittlung ausprogrammieren
	}

	/**
	 *{@inheritDoc}
	 */
	public void visit(IRecord record) {
		// .. check name resolution
		Set<String> missing = determineMissingAliases(record.getNameFromHierarchy(), record);
		if (!missing.isEmpty()) {
			errors.add(new Error(record.getId(), "Parameters [" + StringUtil.toSeparatedString(missing, ",")
					+ "] in record name cannot be resolved."));
		} else {
			// .. check that final record names for concrete records are unique
			// all over the project
			if (!record.isAbstract()) {
				try {
					String finalName = AliasResolutionUtil.resolve(record.getNameFromHierarchy(), record);

					if (finalRecordNames.containsKey(finalName)) {
						finalRecordNames.get(finalName).add(record.getId());

						for (UUID id : finalRecordNames.get(finalName)) {
							errors.add(new Error(id, "Record name \"" + finalName + "\" is not unique."));
						}
					} else {
						finalRecordNames.put(finalName, new HashSet<UUID>());
						finalRecordNames.get(finalName).add(record.getId());
					}

				} catch (Exception e) {
					assert false : "Should not happen";
				}
			}
		}

		// .. check field resolution
		Map<String, Object> fields = record.getFields();

		for (String key : fields.keySet()) {
			try {
				AliasResolutionUtil.resolve((String) fields.get(key), record);
			} catch (AliasResolutionException e) {
				errors.add(new Error(record.getId(), "Resolution error:" + e.getMessage()));
			}
		}
	}

	/**
	 * Returns the errors that have been discovered.
	 * 
	 * @return all discovered errors
	 */
	public Set<Error> getErrors() {
		return errors;
	}

	/**
	 * Determines missing aliases.
	 * 
	 * @param value
	 *            the value that has to be resolved
	 * @param record
	 *            the base element
	 * @return all missing aliases
	 */
	private Set<String> determineMissingAliases(String value, IRecord record) {
		Set<String> result = new HashSet<String>();
		Map<String, String> aliases = AliasResolutionUtil.getFinalAliases(record.getContainer());

		if (StringUtil.hasLength(value)) {
			List<String> required = AliasResolutionUtil.getRequiredAliasNames(value);

			if (!required.isEmpty()) {
				for (String r : required) {
					if (!aliases.containsKey(r)) {
						result.add(r);
					}
				}
			}
		}

		return result;
	}

	/**
	 * Represents an error.
	 * 
	 * @author Sven Wende
	 * 
	 */
	public static class Error {
		private UUID id;
		private String message;

		/**
		 * Constructor.
		 * 
		 * @param id
		 *            the id of the model element, the error is related to
		 * @param message
		 *            the error message
		 */
		private Error(UUID id, String message) {
			super();
			this.id = id;
			this.message = message;
		}

		/**
		 * Returns the id of the model element, the error is related to.
		 * 
		 * @return id of the model element, the error is related to
		 */
		public UUID getId() {
			return id;
		}

		/**
		 * Returns the error message.
		 * 
		 * @return the error message
		 */
		public String getErrorMessage() {
			return message;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + ((message == null) ? 0 : message.hashCode());
			return result;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Error other = (Error) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (message == null) {
				if (other.message != null)
					return false;
			} else if (!message.equals(other.message))
				return false;
			return true;
		}

	}

}
