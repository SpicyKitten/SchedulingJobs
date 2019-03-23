package scheduling;

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
		//do whatever, on completion
	}

}
