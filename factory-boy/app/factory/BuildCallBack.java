package factory;

import play.db.jpa.GenericModel;

/**
 * 
 * @author crazycode@gmail.com
 * @param <T>
 */
public interface BuildCallBack<T extends GenericModel> {
	void build(T target);
}
