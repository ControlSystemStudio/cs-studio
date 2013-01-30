package org.csstudio.opibuilder.test;

public class Test {

	public Test() {
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		final Test test = new Test();
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				test.notifyWait();				
			}
		});
		thread.start();
		test.startWait();
		System.out.println("done!");
		
	}
	
	public void startWait() throws InterruptedException{
		synchronized (this) {
			System.out.println("Get the lock in wait");
			wait();
			Thread.sleep(5000);
			System.out.println("Release the lock in wait");
		}
	}
	
	public void notifyWait(){
		synchronized (this) {
			System.out.println("Get the lock in notify");
			notifyAll();
		}
	}
	
}
