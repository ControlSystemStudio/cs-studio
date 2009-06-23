package org.csstudio.swt.xygraph.undo;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.linearscale.Range;

/**The command for graph zooming and panning.
 * @author Xihui Chen
 *
 */
public class ZoomCommand implements IUndoableCommand {

	private List<Axis> xAxisList;
	private List<Axis> yAxisList;
	
	private List<Range> beforeXRangeList, beforeYRangeList,
		afterXRangeList, afterYRangeList;

	private String name;
	
	public ZoomCommand(String name, List<Axis> xAxisList, List<Axis> yAxisList) {
		this.name = name;
		this.xAxisList = xAxisList;
		this.yAxisList = yAxisList;
		beforeXRangeList = new ArrayList<Range>();
		beforeYRangeList = new ArrayList<Range>();
		afterXRangeList = new ArrayList<Range>();
		afterYRangeList = new ArrayList<Range>();
	}

	public void redo() {
		int i=0;
		for(Axis axis : xAxisList){
			axis.setRange(afterXRangeList.get(i));
			i++;
		}
		i=0;
		for(Axis axis : yAxisList){
			axis.setRange(afterYRangeList.get(i));
			i++;
		}
	}

	public void undo() {
		int i=0;
		for(Axis axis : xAxisList){
			axis.setRange(beforeXRangeList.get(i));
			i++;
		}
		i=0;
		for(Axis axis : yAxisList){
			axis.setRange(beforeYRangeList.get(i));
			i++;
		}
	}
	
	public void savePreviousStates(){
		for(Axis axis : xAxisList)
			beforeXRangeList.add(axis.getRange());
		for(Axis axis : yAxisList)
			beforeYRangeList.add(axis.getRange());
	}
	
	public void saveAfterStates(){
		for(Axis axis : xAxisList)
			afterXRangeList.add(axis.getRange());
		for(Axis axis : yAxisList)
			afterYRangeList.add(axis.getRange());
	}

	@Override
	public String toString() {
		return name;
	}
}
