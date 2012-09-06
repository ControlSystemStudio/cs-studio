package org.csstudio.utility.toolbox.func;

public final class Some<T> extends Option<T> {

	private final T value;
	
	public Some(T value) {
		if (value == null) {
			throw new IllegalStateException("Some must not contain null values");
		}
		this.value = value;
	}
	
	public Some(Option<T> value) {
		if (value == null) {
			throw new IllegalStateException("Some must not contain null values");
		}
		if (!value.hasValue()) {
			throw new IllegalStateException("No value given");
		}
		this.value = value.get();
	}
	
	@Override
	public boolean hasValue() {
		return true;
	}

	@Override
	public T get() {
		return value;
	}
	
	public String toString() {
		return value.toString();
	}
	
	public boolean equals(Object other) {
		if (other == null || other.getClass() != Some.class) {
			return false;
		}
		Some<?> that = (Some<?>)other;
		Object thatValue = that.get();		
		return value.equals(thatValue);
	}
	
	public int hashCode() {
		return 37 * value.hashCode();
	}
	
	public static Some<Func0Void> some(Func0Void func0Void) {
		return new Some<Func0Void>(func0Void);
	}  

	public static <A> Some<Func1Void<A>> some(Func1Void<A> func1Void) {
		return new Some<Func1Void<A>>(func1Void);
	}  

}
