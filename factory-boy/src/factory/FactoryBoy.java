package factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;

import junit.framework.AssertionFailedError;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.Play;
import play.classloading.ApplicationClasses;
import play.classloading.ApplicationClassloader;
import play.db.Model;
import play.db.jpa.GenericModel;
import play.test.Fixtures;
import util.DatabaseUtil;
import factory.annotation.Factory;
import factory.callback.BuildCallback;

public class FactoryBoy {

    protected static Map<Class<?>, ModelFactory<?>> modelFactoryCacheMap = new HashMap<Class<?>, ModelFactory<?>>();

    protected static Map<Class<?>, Integer> modelSequenceMap = new HashMap<Class<?>, Integer>();

    protected static ThreadLocal<Set<Class<?>>> _threadLocalModelDeletedSet = new ThreadLocal<Set<Class<?>>>();

    protected static ThreadLocal<Map<Class<?>, Object>> _lastObjectMap = new ThreadLocal<Map<Class<?>, Object>>();
    
    protected static synchronized Set<Class<?>> modelDeletedSet() {
        Set<Class<?>> modelDeletedSet = _threadLocalModelDeletedSet.get();
        if (modelDeletedSet == null) {
            modelDeletedSet = new HashSet<Class<?>>();
            _threadLocalModelDeletedSet.set(modelDeletedSet);
        }
        return modelDeletedSet;
    }
    
    protected static synchronized Map<Class<?>, Object> lastObjectMap() {
    	Map<Class<?>, Object> lastObjectMap = _lastObjectMap.get();
    	if (lastObjectMap == null) {
    		lastObjectMap = new HashMap<Class<?>, Object>();
    		_lastObjectMap.set(lastObjectMap);
    	}
    	return lastObjectMap;
    }

    protected static void reset() {
    	_lastObjectMap.set(null);
        _threadLocalModelDeletedSet.set(null);
    }

    /**
     * Only delete the Model when first call create(...) method.
     */
    public static void lazyDelete() {
        reset();
    }

    /**
     * Deletes the specified Models.
     *
     * @param clazzes
     */
    public static void delete(Class<? extends GenericModel>... clazzes) {
        reset();
        Fixtures.delete(clazzes);
        for (Class<? extends GenericModel> clz : clazzes) {
            modelDeletedSet().add(clz);
        }
    }

    /**
     * Delete all will call the Fixtures.deleteDatabase()
     */
    public static void deleteAll() {
        reset();
        Fixtures.deleteAllModels();
        for (ApplicationClasses.ApplicationClass c : Play.classes.getAssignableClasses(Model.class)) {
           if( c.javaClass.isAnnotationPresent(Entity.class) ) {
               modelDeletedSet().add((Class<? extends Model>)c.javaClass);
            }
        }
    }

    protected static synchronized void checkOrDeleteModel(
                    Class<? extends GenericModel> clazz,
                    ModelFactory<? extends GenericModel> modelFactory) {
        if (!modelDeletedSet().contains(clazz)) {
            Class<?>[] relationModels = modelFactory.relationModels();
            if (relationModels != null) {
                for (Class<?> r : relationModels) {
                    if (GenericModel.class.isAssignableFrom(r)) {
                        Class<? extends GenericModel> gm = (Class<? extends GenericModel>) r;
                        if (!modelDeletedSet().contains(gm)) {
                            deleteModel(gm);
                            modelDeletedSet().add(gm);
                        }
                    }
                }
            }
            deleteModel(clazz);
            modelDeletedSet().add(clazz);
        }
    }

    private static <T extends GenericModel> void deleteModel(Class<T> clazz) {
        DatabaseUtil.disableForeignKeyConstraints();
        try {
            Model.Manager.factoryFor(clazz).deleteAll();
        } catch(Exception e) {
            Logger.error(e, "While deleting " + clazz + " instances");
        }
        DatabaseUtil.enableForeignKeyConstraints();
    }

    public static synchronized <T extends GenericModel> ModelFactory<T> findModelFactory(
                    Class<T> clazz) {
        // If the Model has not delete after lazyDelete, delete it all.
        ModelFactory<T> modelFactory = (ModelFactory<T>) modelFactoryCacheMap
                        .get(clazz);
        if (modelFactory != null) {
            return modelFactory;
        }
        String clazzFullName = clazz.getName();
        String modelFactoryName = clazzFullName.replaceAll("^models\\.",
                        "factory.") + "Factory";
        try {
            modelFactory = (ModelFactory<T>) Play.classloader.loadApplicationClass(modelFactoryName).newInstance();
            modelFactoryCacheMap.put(clazz, modelFactory);
            return modelFactory;
        } catch (Exception e) {
            throw new RuntimeException("Can't find class:" + modelFactoryName, e);
        }
    }

    /**
     * Create the <i>clazz</i> Object and SAVE it to Database.
     *
     * @param clazz
     * @return
     */
    public static <T extends GenericModel> T create(Class<T> clazz) {
        T t = build(clazz);
        saveModelObject(t);
        return t;
    }

    /**
     * Create the named <i>clazz</i> Object and SAVE it to Database.
     *
     * @param clazz
     * @param name
     * @return
     */
    public static <T extends GenericModel> T create(Class<T> clazz, String name) {
        T t = build(clazz, name);
        saveModelObject(t);
        return t;
    }

    public static <T extends GenericModel> T create(Class<T> clazz,
                    String name, BuildCallback<T> buildCallback) {
        T t = build(clazz, name, buildCallback);
        saveModelObject(t);
        return t;
    }

    public static <T extends GenericModel> T create(Class<T> clazz,
                    BuildCallback<T> buildCallback) {
        T t = build(clazz, buildCallback);
        saveModelObject(t);
        return t;
    }

    /**
     * Build the <i>clazz</i> Object, but NOT save it.
     *
     * @param clazz
     * @return
     */
    public static <T extends GenericModel> T build(Class<T> clazz) {
        ModelFactory<T> modelFactory = findModelFactory(clazz);
        checkOrDeleteModel(clazz, modelFactory);
        T t = modelFactory.define();
        return t;
    }

    /**
     * Build the named <i>clazz</i> Object, but NOT save it.
     *
     * @param clazz
     * @param name
     * @return
     */
    public static <T extends GenericModel> T build(Class<T> clazz, String name) {

        ModelFactory<T> modelFactory = findModelFactory(clazz);
        checkOrDeleteModel(clazz, modelFactory);

        T t = modelFactory.define();

        Method method = getModelDefineMethod(clazz, name, modelFactory);
        try {
            // process factory's base define method.
            Factory factory = method.getAnnotation(Factory.class);
            if (factory != null && StringUtils.isNotEmpty(factory.base())) {
                try {
                    Method baseMethod = getModelDefineMethod(clazz,
                                    factory.base(), modelFactory);
                    t = invokeModelFactoryMethod(modelFactory, t, baseMethod);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            t = invokeModelFactoryMethod(modelFactory, t, method);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return t;
    }

    private static <T extends GenericModel> T invokeModelFactoryMethod(
                    ModelFactory<T> modelFactory, T t, Method baseMethod)
                    throws IllegalAccessException, InvocationTargetException {
        int parameterNumber = baseMethod.getParameterTypes().length;
        Object returnObject = null;
        switch (parameterNumber) {
        case 0:
            // void method
            returnObject = baseMethod.invoke(modelFactory, new Object[] {});
            break;
        case 1:
            returnObject = baseMethod.invoke(modelFactory, t);
            break;
        case 2:
            returnObject = baseMethod.invoke(modelFactory, t,
                            sequence(t.getClass()));
        }
        if (returnObject != null) {
            return (T) returnObject;
        }
        return t;
    }

    private static <T extends GenericModel> Method getModelDefineMethod(
                    Class<T> clazz, String name, ModelFactory<T> modelFactory)
    {

        Method[] allMethods = modelFactory.getClass().getMethods();

        for (Method method : allMethods) {
            Factory factory = method.getAnnotation(Factory.class);
            if (factory != null) {
                String factoryName = factory.name();
                if (name.equals(factoryName)) {
                    return method;
                }
            }
        }
        throw new RuntimeException(
                        "Can't find any method with @Factory(name=" + name
                                        + " method at class "
                                        + modelFactory.getClass().getName()
                                        + ", Please define it.");
    }

    /**
     * Build the named <i>clazz</i> Object, but NOT save it.
     *
     * @param clazz
     * @param name
     * @return
     */
    public static <T extends GenericModel> T build(Class<T> clazz, String name,
                    BuildCallback<T> buildCallback) {
        T t = build(clazz, name);
        buildCallback.build(t);
        return t;
    }

    public static <T extends GenericModel> T build(Class<T> clazz,
                    BuildCallback<T> buildCallback) {
        T t = build(clazz);
        buildCallback.build(t);
        return t;
    }

    /*
     * // TODO public static <T extends GenericModel> List<T> batchCreate(int
     * size, Class<T> clazz) { return null; }
     */

    public static <T extends GenericModel> List<T> batchCreate(int size,
                    Class<T> clazz, BuildCallback<T> sequenceCallback) {
        List<T> list = batchBuild(size, clazz, sequenceCallback);
        for (T t : list) {
            saveModelObject(t);
        }
        return list;
    }

    public static <T extends GenericModel> List<T> batchBuild(int size,
                    Class<T> clazz, BuildCallback<T> buildCallback) {
        List<T> list = new ArrayList<T>();
        for (int i = 0; i < size; i++) {
            T t = build(clazz);
            buildCallback.build(t);
            list.add(t);
        }
        return list;
    }

    public static <T extends GenericModel> List<T> batchCreate(int size,
                    Class<T> clazz, String name, BuildCallback<T> buildCallback) {
        List<T> list = batchBuild(size, clazz, name, buildCallback);
        for (T t : list) {
            saveModelObject(t);
        }
        return list;
    }

	private static <T extends GenericModel> void saveModelObject(T t) {
		t.save();
		lastObjectMap().put(t.getClass(), t);
	}

    public static <T extends GenericModel> List<T> batchBuild(int size,
                    Class<T> clazz, String name, BuildCallback<T> buildCallback) {
        List<T> list = new ArrayList<T>();
        for (int i = 0; i < size; i++) {
            T t = build(clazz, name);
            buildCallback.build(t);
            list.add(t);
        }
        return list;
    }

    public static synchronized int sequence(Class<?> clazz) {
        Integer seq = modelSequenceMap.get(clazz);
        if (seq == null) {
            seq = 0;
        }
        modelSequenceMap.put(clazz, ++seq);
        return seq;
    }

    public static <T extends GenericModel> T last(Class<T> clazz) {
    	T lastObject = (T) lastObjectMap().get(clazz);
    	if (lastObject == null) {
    		throw new AssertionFailedError("Can't get the last " + clazz.getName() 
    				+ " Object, Please call FactoryBoy.create(" 
    				+ clazz.getName() + ".class) at first.");
    	}
        return lastObject;
    }

    public static <T extends GenericModel> T lastOrCreate(Class<T> clazz) {
    	T lastObject = (T) lastObjectMap().get(clazz);
    	if (lastObject == null) {
    		return create(clazz);
    	}
        return lastObject;
    }

    public static <T extends GenericModel> T lastOrCreate(Class<T> clazz, String name) {
    	T lastObject = (T) lastObjectMap().get(clazz);
    	if (lastObject == null) {
    		return create(clazz, name);
    	}
        return lastObject;
    }
    
    
    /**
     * Create Object by Type Name, used by Selenium Test.
     * @param typeName
     * @return
     */
    public static <T extends GenericModel> T createByName(String typeName) {
    	Class<T> clazz = (Class<T>)play.Play.classloader.getClassIgnoreCase("models." + typeName);
    	return create(clazz);
    }

    public static <T extends GenericModel> T createByName(String typeName, String name) {
    	Class<T> clazz = (Class<T>)play.Play.classloader.getClassIgnoreCase("models." + typeName);
    	return create(clazz, name);
    }
    
    public static String getSimpleVariableName(String typeName) {
    	Class<?> clazz = play.Play.classloader.getClassIgnoreCase("models." + typeName);
    	return clazz.getSimpleName().toLowerCase();
    }
}
