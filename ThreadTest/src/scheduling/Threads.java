package scheduling;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.FutureTask;

import throwing.Throwing;


public class Threads {
	
	private static Queue<Task<?>> generateJobs(int n)
	{
		Queue<Task<?>> jobs = new LinkedList<>();
		for(int i = 0; i < n; ++i)
		{
			Task<?> jobInfo = new Task<>(
					new FutureTask<>(Throwing.of(() -> Thread.sleep(1000)), null)
					, i % 5 < 3, "Task "+i);
			jobs.add(jobInfo);
		}
		return jobs;
	}
	
	public static void main(String[] args) throws Exception
	{
		int n = 12;
		Queue<Task<?>> jobs = generateJobs(n); 	//generate n job descriptions
		Queue<Task<?>> parallel = new ArrayDeque<>();
		while(!jobs.isEmpty())
		{
			Task<?> next = jobs.peek();
			if(next.isParallel())
			{
				do
				{
					next.dispatch();
					parallel.offer(jobs.poll());
					next = jobs.peek();
				} while(next != null && next.isParallel());
				parallel.forEach(Throwing.of(t -> {
					t.await();
					System.out.println(String.format("%s completed in parallel!", t));
				}));
				parallel.clear();
			}
			else
			{
				(next = jobs.poll()).dispatch().await();
				System.out.println(String.format("%s completed serially!", next));
			}
		}
		System.out.println("All jobs completed");
	}
	
	
}
