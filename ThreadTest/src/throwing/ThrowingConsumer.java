package throwing;

import java.util.function.Consumer;

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
			{ ThrowingUtil.raise(e); }
	}
	static <T> Consumer<T> of(ThrowingConsumer<T> tc) 
	{
		return tc;
	}
}
