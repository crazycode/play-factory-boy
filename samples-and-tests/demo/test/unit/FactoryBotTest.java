package unit;

import model.UnUseModel;
import models.Order;
import models.Product;

import org.junit.Test;

import play.test.UnitTest;
import factory.FactoryBoy;
import factory.ProductFactory;

public class FactoryBotTest extends UnitTest {
	
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
	
}
