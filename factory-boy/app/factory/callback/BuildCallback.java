package factory.callback;

import play.db.jpa.GenericModel;

/**
 * 
 * @author crazycode@gmail.com
 * @param <T>
 */
public interface BuildCallback<T extends GenericModel> {
	void build(T target);
}
