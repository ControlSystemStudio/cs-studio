package org.csstudio.util.threads;

/** Thread test stuff, for Profile tests.
 *  @author ky9
 */
public class ThreadTest extends Thread
{
	final private static Object lock = new Object();
	
	ThreadTest(final String name)
	{
		super(name);
	}
	
	@Override
	public void run()
	{
		for (int i=1; i<10; ++i)
		{
			synchronized (lock)
			{
				try
				{
					System.out.println(getName());
					Thread.sleep(50);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("nls")
	public static void main(String[] args) throws InterruptedException
	{
		Thread a = new ThreadTest("A");
		Thread b = new ThreadTest("B");
		a.start();
		b.start();
		a.join();
		b.join();
	}
}
