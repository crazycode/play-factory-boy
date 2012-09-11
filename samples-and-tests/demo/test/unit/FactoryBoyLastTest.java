package unit;

import java.util.List;

import junit.framework.AssertionFailedError;
import models.Product;

import org.junit.Before;
import org.junit.Test;

import play.db.jpa.GenericModel;
import play.test.UnitTest;
import factory.FactoryBoy;
import factory.callback.BuildCallback;
import factory.callback.SequenceCallback;

public class FactoryBoyLastTest extends UnitTest {

	@Before
	public void setUp() {
		FactoryBoy.lazyDelete();
	}

	@Test(expected=AssertionFailedError.class)
	public void testThrowAssertExceptionWhenNoCreate() throws Exception {
		FactoryBoy.last(Product.class);
	}
	
	@Test
	public void testCheckTheLastObjest() throws Exception {
		Product origin = FactoryBoy.create(Product.class);
		Product last = FactoryBoy.last(Product.class);
		assertEquals(origin, last);
	}
	
	@Test
	public void testCheckTheLastObject2() throws Exception {
		FactoryBoy.create(Product.class, new BuildCallback<Product>() {
			@Override
			public void build(Product target) {
				target.name = "Macbook Air";
			}
		});
		Product last = FactoryBoy.last(Product.class);
		assertEquals("Macbook Air", last.name);
		
		Product another = FactoryBoy.create(Product.class);
		Product last2 = FactoryBoy.last(Product.class);
		assertFalse(last == last2);
		assertEquals(another, last2);
	}
	
	@Test
	public void testTheLastObjectShouldNotOverideByFactoryBoyBuildMethod() throws Exception {
		Product origin = FactoryBoy.create(Product.class);
		Product last = FactoryBoy.last(Product.class);
		
		Product temp = FactoryBoy.build(Product.class);
		assertNotNull(temp);
		assertFalse(temp.isPersistent());
		Product last2 = FactoryBoy.last(Product.class);
		assertEquals(origin, last);
		assertEquals(last, last2);
	}
	
	@Test
	public void testCheckLastObjectWhenCallBatchCreate() throws Exception {
		List<Product> products = FactoryBoy.batchCreate(5, Product.class, new SequenceCallback<Product>() {
			@Override
			public void sequence(Product target, int seq) {
				target.name = "Sample Product #" + seq;				
			}
		});
		Product last = FactoryBoy.last(Product.class);
		assertEquals(5, products.size());
		assertEquals(last, products.get(4));
		
	}
}
