package throwing;

import java.util.function.Consumer;

/**
 * As long as you know what you're constructing,
 * this class will let you create a throwing lambda
 * without having to type out the full class name
 * @author ratha
 *
 */
public class Throwing 
{
	public static Runnable of(ThrowingRunnable tr) 
	{
		return ThrowingRunnable.of(tr);
	}
	public static <T> Consumer<T> of(ThrowingConsumer<T> tc) 
	{
		return ThrowingConsumer.of(tc);
	}
}
