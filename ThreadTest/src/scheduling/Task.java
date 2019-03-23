package scheduling;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import throwing.Throwing;

public class Task<V>
{
    private Thread thread;			//thread on which the job is run
    private FutureTask<V> job;				//the job to run
    private boolean parallel;		//will the job be run in parallel?
    private String description;		//what does the job do?
    public Task(FutureTask<V> job, boolean isParallel, String description)
    {
    	this.job = job;
    	this.parallel = isParallel;
    	this.description = description;
    }
    public Task(FutureTask<V> job, boolean isParallel)
    {
    	this(job, isParallel, "");
    }
    /**
     * @return The Task dispatched on another thread for completion
     */
    public Task<V> dispatch()
    {
    	thread = new Thread(Throwing.of(() -> job.run()));
    	thread.start();
    	return this;
    }
    /**
     * @return the Task completed, unless interrupted
     */
    public Task<V> await() throws InterruptedException
    {
    	return await(0);
    }
    /**
     * @param millis the time to wait in milliseconds
     * @return the Task completed, unless interrupted
     */
    public Task<V> await(long millis) throws InterruptedException
    {
    	thread.join(millis);
    	return this;
    }
    /**
     * @return The value of this task, if possible
     */
    public V fetchValue() throws InterruptedException, ExecutionException, TimeoutException
    {
    	if(!this.job.isDone())
    		return null;
    	return this.job.get(0, TimeUnit.NANOSECONDS);
    }
    /**
     * @return The value of this task after the task is complete
     */
    public V awaitValue() throws InterruptedException, ExecutionException 
    {
    	return this.job.get();
    }
    public boolean isParallel() { return this.parallel; }
    public String toString() { return description; }
}
