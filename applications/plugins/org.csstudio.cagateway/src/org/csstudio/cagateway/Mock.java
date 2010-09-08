package org.csstudio.cagateway;

/**
 * Simple DOOCS simulation
 */

public class Mock implements Runnable {
	
	private double speed;
	private String name;
	
	public Mock(){
		name = "default";
		speed = 0;
		new Thread(this, "name").start();	
	}
	
	public void setPV(double speed) {
		this.speed = speed;
	}
	
	public double getPV() {
		return speed;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public void run() {
		
		while(true){
			setPV(getPV()+Math.random()*10-5);
			try {
				Thread.sleep((int)(Math.random()*1000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}		
	
	
}
