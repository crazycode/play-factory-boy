package factory.asserts;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import play.db.jpa.GenericModel;

public class ModelAssert {

	public static <T extends GenericModel> void assertDifference(Class<T> clazz, int expectDifference, Callback callback) throws Exception {
		Method countMethod = clazz.getMethod("count", new Class<?>[]{});
		long countBefore = (long)countMethod.invoke(clazz, new Object[]{});
		callback.run();
		long countAfter = (long)countMethod.invoke(clazz, new Object[]{});
		assertTrue(String.format("Expect the %s's count changes %d, but was %d(before:%d, after:%d)", clazz.getName(), expectDifference, 
		        (countAfter - countBefore), countBefore, countAfter),
				(expectDifference == (countAfter - countBefore)));
	}
}
