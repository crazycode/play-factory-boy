package factory;

import java.util.List;
import play.db.jpa.GenericModel;

public abstract class ModelFactory<T extends GenericModel> {

	public abstract T define();
	
	public Class<? extends GenericModel>[] relationModels() {
	    return null;
	}
	
	/**
	 * If T.deleteAll() failed, FactoryBoy will call this deleteAll().
	 * @param t
	 */
	public void deleteAll() {
	    List<T> all = T.findAll();
	    for (T t : all) {
            delete(t);
        }
	}
	
	/**
	 * If T.deleteAll() failed, FactoryBoy will call delete(t) for each.
	 * @param t
	 */
	protected void delete(T t) {
	    
	}
	
}
