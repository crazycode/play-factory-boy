package factory;

import models.Product;
import play.db.jpa.GenericModel;


public class FactoryBoy {

	public static void init(Class<?>... clazzes) {
	    	    
    }

	/**
	 * Create the <i>clazz</i> Object and SAVE it to Database.
	 * @param clazz
	 * @return
	 */
	public static <T extends GenericModel> T create(Class<T> clazz) {
	    return null;
    }
	
	/**
	 * Create the named <i>clazz</i> Object and SAVE it to Database.
	 * @param clazz
	 * @param name
	 * @return
	 */
	public static <T extends GenericModel> T create(Class<T> clazz, String name) {
	    return null;
    }
	
	/**
	 * Build the <i>clazz</i> Object, but NOT save it.
	 * @param clazz
	 * @return
	 */
	public static <T extends GenericModel> T build(Class<T> clazz) {
	    return null;
    }

	/**
	 * Build the named <i>clazz</i> Object, but NOT save it.
	 * @param clazz
	 * @param name
	 * @return
	 */
	public static <T extends GenericModel> T build(Class<T> clazz, String name) {
	    return null;
    }

	public static Product create(Class<Product> clazz,
            BuildCallBack buildCallBack) {
	    return null;
    }
}
