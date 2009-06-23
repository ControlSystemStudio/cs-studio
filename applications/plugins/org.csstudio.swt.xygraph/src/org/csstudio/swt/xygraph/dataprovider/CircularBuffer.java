package org.csstudio.swt.xygraph.dataprovider;

import java.util.AbstractCollection;
import java.util.Iterator;

/**A particular circular buffer. New arrived data will be appended to the tail of the buffer. 
 * When buffer is full, the oldest data will be deleted when new data arrived. 
 * 
 * @author Xihui Chen 
 */
public class CircularBuffer<T> extends AbstractCollection<T> {
	private int bufferSize =0;
	private T[] buffer;
	private int head;
	private int tail;
	private int count;
	
	public CircularBuffer(int bufferSize) {
		this.setBufferSize(bufferSize, true);
	}
	
	/** Add an element.
	 * @param element
	 */
	public synchronized boolean add(T element){		
		if(tail == head && count == bufferSize) { //buffer is full
			buffer[tail] = element;	
			head = (head + 1) % bufferSize;
			tail = (tail + 1) % bufferSize;
			return true;
		}
		else{//buffer is not full
			buffer[tail] = element;	
			tail = (tail + 1) % bufferSize;					
			count++;
			return true;
		}
	}
	
	/**Get element
	 * @param index the index of the element in the buffer.
	 * @return the element. null if the data at the index doesn't exist.
	 */
	public T getElement(int index){
		if(index < count)
			return buffer[(head + index) % bufferSize];		
		else
			return null;
	}
	
	/**Get head element
	 * @return the head element. null if the buffer is empty.
	 */
	public T getHead(){
		if(count > 0)
			return buffer[head];		
		else
			return null;
	}
	
	/**Get tail element
	 * @return the tail element. null if the buffer is empty.
	 */
	public T getTail(){
		if(count > 0)
			return buffer[(head+count-1)%bufferSize];		
		else
			return null;
	}
	
	
	
	/**
	 * clear the buffer;
	 */
	public void clear(){
		head = 0;
		tail = 0;
		count = 0;
	}

	/**Set the buffer size.
	 * @param bufferSize the bufferSize to set
	 * @param clear clear the buffer if true. Otherwise keep the exist data;
	 * Extra data on the end would be omitted if the new bufferSize is less 
	 * than the exist data count. 
	 */
	@SuppressWarnings("unchecked")
	public void setBufferSize(int bufferSize, boolean clear) {
		assert bufferSize > 0;		
		if(this.bufferSize != bufferSize){
			this.bufferSize = bufferSize;
			if(clear){//clear 
				buffer = (T[]) new Object[bufferSize];
				clear();
			}else{// keep the exist data
				T[] tempBuffer = (T[]) toArray();
				buffer = (T[]) new Object[bufferSize];
				for(int i=0; i<Math.min(bufferSize, count); i++){
					buffer[i] = tempBuffer[i];
				}
				count = Math.min(bufferSize, count);
				head =0;
				tail = count%bufferSize;
			}			
		}		
	}

	/**
	 * @return the bufferSize
	 */
	public int getBufferSize() {
		return bufferSize;
	}	


	public Iterator<T> iterator() {
		return new Iterator<T>(){
			private int index=0;

			public boolean hasNext() {
				return index < count;
			}
			public T next() {
				return buffer[(head+index++)%bufferSize];
			}
			public void remove() {}			
		};
	}

	@Override
	public int size() {
		return count;
	}
	
	
	
}
