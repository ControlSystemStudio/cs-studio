package de.desy.language.snl.parser;

public class Interval {
	
	private final int _start;
	private final int _end;

	public Interval(int start, int end) {
		assert start >= 0 : "start >= 0";
		assert start < end : "start < end";
		assert end > 0 : "end > 0";
		_start = start;
		_end = end;
	}

	public int getStart() {
		return _start;
	}

	public int getEnd() {
		return _end;
	}
	
	public boolean contains(int position) {
		return (_start <= position && position <= _end);
	}

}
