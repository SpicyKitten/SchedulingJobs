
public class Job {
	
	public int wait;
	
	public Job(int w)
	{
		this.wait = w;
	}

	public void waitForCompletion(boolean verbose) throws InterruptedException {
		//ignoring parameter for now
		Thread.sleep(wait);
		complete();
	}
	
	private void complete()
	{
//		System.out.println("Slept for " + wait/1000 + " seconds, job complete!");
	}

}
