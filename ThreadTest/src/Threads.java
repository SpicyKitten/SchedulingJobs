import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class Threads {
	private static final class JobInfo 
	{
	    private Thread thread;		//if asynchronous, the job will be run on this thread
	    private Job job;			//the job to run
	    private boolean parallel;		//will the job be run in parallel?
	    public void setThread(Thread t) { this.thread = t; }
	    public Thread getThread() { return this.thread; }
	    public void setJob(Job j) { this.job = j; }
	    public Job getJob() { return this.job; }
	    public void setParallel(boolean p) { this.parallel = p; }
	    public boolean isParallel() { return this.parallel; }
	    public String toString() { return "Wait "+job.wait+", parallel="+parallel; }
	}
	private static List<JobInfo> generateJobs(int n)
	{
		List<JobInfo> jobs = new ArrayList<>();
		for(int i = 0; i < n; ++i)
		{
			JobInfo jobInfo = new JobInfo();
			jobInfo.setJob(new Job(1000));
			jobInfo.setParallel(i % 5 < 3);
			jobs.add(jobInfo);
		}
		return jobs;
	}
	
	public static void main(String[] args) throws Exception
	{
		int n = 10;
		List<JobInfo> jobs = generateJobs(n); 	//generate n job descriptions
		jobs.forEach(System.out::println);
		for(int i = 0; i < jobs.size(); ++i)
		{
		    if(!jobs.get(i).isParallel())
		    {
		        jobs.get(i).getJob().waitForCompletion(true);
		        System.out.println("Job "+i+" completed serially!");
		    }
		    else
		    {
		    	int i_ = i;
		    	jobs.get(i_).setThread(new Thread(ThrowingRunnable.of(() -> jobs.get(i_).getJob().waitForCompletion(true))));
		    	jobs.get(i_).getThread().start();
		    	int j = i + 1;
		        for(; j < jobs.size(); ++j)
		            if(!jobs.get(j).isParallel())
		                break;
		            else
		            {
		                int k = j;
		                jobs.get(j).setThread(new Thread(ThrowingRunnable.of(() -> jobs.get(k).getJob().waitForCompletion(true))));
		                jobs.get(j).getThread().start();
		            }
		        for(int k = i; k < Math.min(j, jobs.size()); ++k)
		        {
		            jobs.get(k).getThread().join();
		            System.out.println("Job "+k+" completed in parallel!");
		        }
		        i = j - 1;
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
	@FunctionalInterface
	public interface ThrowingConsumer<T> extends Consumer<T> 
	{	//pretty similar to code online at https://gist.github.com/myui/9722c1301434a3b69cf898ccd9090ff1
		//but honestly there isn't really any other way to do it
		//and still keep it both readable and usable
		public abstract void accept_(T t) throws Exception;
		@Override
		default void accept(T t)
		{
			try { accept_(t); }
			catch (Exception e)
				{ raise(e); }
		}
		static <T> Consumer<T> of(ThrowingConsumer<T> tc) 
		{
			return tc;
		}
	}
}
