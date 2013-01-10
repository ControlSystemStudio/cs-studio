package org.csstudio.logbook.ologviewer;

import org.eclipse.jface.viewers.CellLabelProvider;

public class SimpleOlogTableColumnDescriptor implements
		OlogTableColumnDescriptor {
	private String text;
	private String tooltip;
	private int weight;
	private CellLabelProvider cellLabelProvider;

	public static class Builder {

		private String text;
		private String tooltip;
		private int weight;
		private CellLabelProvider cellLabelProvider;

		private Builder(String text) {
			this.text = text;
		}

		private Builder() {

		}

		public static Builder createColumn() {
			return new Builder();
		}

		public static Builder createColumn(String header) {
			return new Builder(header);
		}

		public Builder withHeader(String header) {
			if (header == null || header.isEmpty())
				throw new IllegalArgumentException("column header cannot be null or empty");
			this.text = header;
			return this;
		}

		public Builder withToooltip(String tooltip) {
			this.tooltip = tooltip;
			return this;
		}

		public Builder withWeight(int weight) {
			this.weight = weight;
			return this;
		}

		public Builder withCellLabelProvider(CellLabelProvider cellLabelProvider) {
			if (cellLabelProvider == null)
				throw new NullPointerException("column cellLabelProvider can't be null");
			this.cellLabelProvider = cellLabelProvider;
			return this;
		}

		public OlogTableColumnDescriptor build() {
			if(text == null || text.isEmpty())
				throw new IllegalArgumentException("cannot build column descriptor with header null");
			if(tooltip == null || tooltip.isEmpty())
				tooltip = text;
			if(cellLabelProvider == null)
				throw new IllegalArgumentException("cannot build cloumn with null CellLabelProvider");
			return new SimpleOlogTableColumnDescriptor(text, tooltip, weight,
					cellLabelProvider);
		}
	}

	private SimpleOlogTableColumnDescriptor(String text, String tooltip,
			int weight, CellLabelProvider cellLabelProvider) {
		super();
		this.text = text;
		this.tooltip = tooltip;
		this.weight = weight;
		this.cellLabelProvider = cellLabelProvider;
	}

	@Override
	public String getText() {
		return this.text;
	}

	@Override
	public String getTooltip() {
		return this.tooltip;
	}

	@Override
	public int getWeight() {
		return this.weight;
	}

	@Override
	public CellLabelProvider getCellLabelProvider() {
		return this.cellLabelProvider;
	}

	@Override
	public String toString() {
		return "SimpleOlogTableColumnDescriptor [text=" + text + ", tooltip="
				+ tooltip + ", weight=" + weight + ", cellLabelProvider="
				+ cellLabelProvider + "]";
	}

}
