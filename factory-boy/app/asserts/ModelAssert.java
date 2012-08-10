package asserts;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import play.db.jpa.GenericModel;

public class ModelAssert {

	public static <T extends GenericModel> void assertModelCount(Class<T> clazz, int expectCountChaange, CallBack callback) throws Exception {
		Method countMethod = clazz.getMethod("count", new Class<?>[]{});
		long countBefore = (long)countMethod.invoke(clazz, new Object[]{});
		callback.run();
		long countAfter = (long)countMethod.invoke(clazz, new Object[]{});
		assertTrue(String.format("Expect count changes %d, but was %d", expectCountChaange, (countAfter - countBefore)),
				(expectCountChaange == (countAfter - countBefore)));
	}
}
