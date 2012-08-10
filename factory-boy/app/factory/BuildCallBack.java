package factory;

/**
 * 
 * @author crazycode@gmail.com
 * @param <T>
 */
public interface BuildCallBack<T> {
	T build(T target);
}
