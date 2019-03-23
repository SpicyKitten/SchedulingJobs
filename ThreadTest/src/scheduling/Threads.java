package scheduling;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;


public class Threads {
	private static final class Task 
	{
	    private Thread thread;		//if asynchronous, the job will be run on this thread
	    private Job job;			//the job to run
	    private boolean parallel;		//will the job be run in parallel?
	    private String description;
	    public Task(Job job, boolean isParallel, String description)
	    {
	    	this.job = job;
	    	this.parallel = isParallel;
	    	this.description = description;
	    }
	    public Task(Job job, boolean isParallel)
	    {
	    	this(job, isParallel, "");
	    }
	    /**
	     * @return The Task dispatched on another thread for completion
	     */
	    public Task dispatch()
	    {
	    	thread = new Thread(ThrowingRunnable.of(() -> job.waitForCompletion(true)));
	    	thread.start();
	    	return this;
	    }
	    /**
	     * @return the Task completed, unless interrupted
	     */
	    public Task await() throws InterruptedException
	    {
	    	return await(0);
	    }
	    /**
	     * @param millis the time to wait in milliseconds
	     * @return the Task completed, unless interrupted
	     */
	    public Task await(long millis) throws InterruptedException
	    {
	    	thread.join(millis);
	    	return this;
	    }
	    public boolean isParallel() { return this.parallel; }
	    public String toString() { return "Wait "+job.wait+", parallel="+parallel; }
	    public String id() { return description; }
	}
	private static Queue<Task> generateJobs(int n)
	{
		Queue<Task> jobs = new LinkedList<>();
		for(int i = 0; i < n; ++i)
		{
			Task jobInfo = new Task(new Job(1000), i % 5 < 3, "Task "+i);
			jobs.add(jobInfo);
		}
		return jobs;
	}
	
	public static void main(String[] args) throws Exception
	{
		int n = 10;
		Queue<Task> jobs = generateJobs(n); 	//generate n job descriptions
		jobs.forEach(System.out::println);
		Queue<Task> parallel = new ArrayDeque<>();
		while(!jobs.isEmpty())
		{
			Task next = jobs.poll();
			if(next.isParallel())
			{
				next.dispatch();
				parallel.offer(next);
			}
			else
			{
				while(!parallel.isEmpty())
				{
					Task stillRunning = parallel.poll();
					stillRunning.await();
					System.out.println(String.format("%s completed in parallel!", stillRunning.id()));
				}
				next.dispatch().await();
				System.out.println(String.format("%s completed serially!", next.id()));
			}
		}
		System.out.println("All jobs completed");
	}
	
	@FunctionalInterface 
	public interface ThrowingRunnable extends Runnable
	{
		public abstract void run_() throws Exception;
		@Override
		default void run()
		{
			try { run_(); }
			catch (Exception e)
				{ raise(e); }
		}
		static Runnable of(ThrowingRunnable tc) 
		{
			return tc;
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <E extends Exception> void raise(Exception e) throws E {
		throw (E) e;// sneakyThrow if you google it, restricted to exceptions only
	}
}
