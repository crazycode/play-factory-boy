package factory;

import play.db.jpa.GenericModel;

/**
 * 
 * @author crazycode@gmail.com
 * @param <T>
 */
public interface BuildCallBack<T extends GenericModel> {
	T build(T target);
}
