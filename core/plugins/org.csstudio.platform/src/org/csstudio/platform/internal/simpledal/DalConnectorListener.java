package org.csstudio.platform.internal.simpledal;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.simpledal.ISimpleDalListener;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;

@SuppressWarnings("unchecked")
class DalConnectorListener implements DynamicValueListener {
	private List<WeakReference<ISimpleDalListener>> _simpleDalListenerRefs;
	
	public DalConnectorListener(){
		_simpleDalListenerRefs = new ArrayList<WeakReference<ISimpleDalListener>>();
	}
	
	public void conditionChange(DynamicValueEvent event) {

	}

	public void errorResponse(DynamicValueEvent event) {

	}

	public void timelagStarts(DynamicValueEvent event) {

	}

	public void timelagStops(DynamicValueEvent event) {

	}

	public void timeoutStarts(DynamicValueEvent event) {

	}

	public void timeoutStops(DynamicValueEvent event) {

	}

	public void valueChanged(final DynamicValueEvent event) {
		new Run(){
			@Override
			void doRun(ISimpleDalListener simpleDalListener) {
				simpleDalListener.valueChanged(event.getValue());
			}
		};
	}

	public void valueUpdated(final DynamicValueEvent event) {
		new Run(){
			@Override
			void doRun(ISimpleDalListener simpleDalListener) {
				simpleDalListener.valueChanged(event.getValue());
			}
		};
	}
	
	abstract class Run {
		public Run() {
			for(WeakReference<ISimpleDalListener> wr : _simpleDalListenerRefs) {
				ISimpleDalListener listener = wr.get();
				
				if(listener!=null) {
					doRun(listener);
				} else {
					// TODO: CALLBACK
					_simpleDalListenerRefs.remove(wr);
				}
			}
		}
		
		abstract void doRun(ISimpleDalListener simpleDalListener);
	}

	public void addSimpleDalListener(ISimpleDalListener<Double> listener) {
		_simpleDalListenerRefs.add(new WeakReference<ISimpleDalListener>(listener));
	}

}
