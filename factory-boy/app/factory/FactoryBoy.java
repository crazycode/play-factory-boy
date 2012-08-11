package factory;

import play.db.jpa.GenericModel;
import play.test.Fixtures;


public class FactoryBoy {

	public static <T extends GenericModel> void init(Class<T>... clazzes) {
		Fixtures.delete(clazzes);
		/*
		for (Class<GenericModel> clazz : clazzes) {
			
			ModelFactory<?> modelFactory = findModelFactory(clazz);
			modelFactory.define();
		}
		*/
    }

	public static <T extends GenericModel> ModelFactory<T> findModelFactory(Class<T> clazz) {
		String clazzFullName = clazz.getName();
		String modelFactoryName = clazzFullName.replaceAll("^models\\.",	"factory.") + "Factory";
	    try {
	        return (ModelFactory<T>)Class.forName(modelFactoryName).newInstance();
        } catch (Exception e) {
	        e.printStackTrace();
        }
		throw new RuntimeException("Can't find class:" + modelFactoryName);
    }

	/**
	 * Create the <i>clazz</i> Object and SAVE it to Database.
	 * @param clazz
	 * @return
	 */
	public static <T extends GenericModel> T create(Class<T> clazz) {
		T t = build(clazz);
		t.save();
	    return t;
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


	public static <T extends GenericModel> T create(Class<T> clazz,
            BuildCallBack<T> buildCallBack) {
		T t = build(clazz, buildCallBack);
		t.save();
	    return t;
    }
	
	/**
	 * Build the <i>clazz</i> Object, but NOT save it.
	 * @param clazz
	 * @return
	 */
	public static <T extends GenericModel> T build(Class<T> clazz) {
		ModelFactory<T> modelFactory = findModelFactory(clazz);
		T t = modelFactory.define();
	    return t;
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

	public static <T extends GenericModel> T build(Class<T> clazz,
            BuildCallBack<T> buildCallBack) {
	    T t = build(clazz);
	    buildCallBack.build(t);
	    return t;
    }
}
