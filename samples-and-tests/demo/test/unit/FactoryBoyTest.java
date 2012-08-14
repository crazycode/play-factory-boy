package unit;

import model.UnUseModel;
import models.Order;
import models.Product;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;
import factory.BuildCallBack;
import factory.FactoryBoy;
import factory.ProductFactory;

public class FactoryBoyTest extends UnitTest {
	
	@Before
	public void setUp() {
		FactoryBoy.delete(Order.class, Product.class);
	}
	
	@Test
	public void testFactoryBoySequence() {
		int orderSequence = FactoryBoy.sequence(Order.class);
		int productSequence = FactoryBoy.sequence(Product.class);
		assertEquals(productSequence, orderSequence);
		
		assertEquals(orderSequence + 1, FactoryBoy.sequence(Order.class));
		
		int orderSequence1 = FactoryBoy.sequence(Order.class);
		int productSequence1 = FactoryBoy.sequence(Product.class);
		assertEquals(productSequence1 + 1, orderSequence1);
	}
	
	@Test(expected=RuntimeException.class)
	public void testFindUnExistsModelFactory() {
		FactoryBoy.findModelFactory(UnUseModel.class);
	}

	@Test
	public void testFindModelFactory() {
		assertEquals(ProductFactory.class, FactoryBoy.findModelFactory(Product.class).getClass());
	}

	@Test
	public void testLazyDelete() {
		FactoryBoy.create(Product.class);
		assertEquals(1l, Product.count());
		FactoryBoy.lazyDelete();
		
		// when lazy delete, the count of all model will not be changed.
		assertEquals(1l, Product.count());
		
		// but after to create any model, it will delete the old model object.
		FactoryBoy.create(Product.class);
		assertEquals(1l, Product.count());
		
		FactoryBoy.create(Product.class, new BuildCallBack<Product>() {
			@Override
            public void build(Product target) {
				target.name = "New Product";
            }
		});
		
		assertEquals(2l, Product.count());
	}
}
