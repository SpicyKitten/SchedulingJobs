package throwing;

@FunctionalInterface 
public interface ThrowingRunnable extends Runnable
{
	public abstract void run_() throws Exception;
	@Override
	default void run()
	{
		try { run_(); }
		catch (Exception e)
			{ ThrowingUtil.raise(e); }
	}
	static Runnable of(ThrowingRunnable tr) 
	{
		return tr;
	}
}
