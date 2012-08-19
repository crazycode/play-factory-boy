package unit;

import static factory.asserts.ModelAssert.assertDifference;

import java.math.BigDecimal;
import java.util.List;

import models.Product;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;
import factory.BuildCallback;
import factory.FactoryBoy;
import factory.SequenceCallback;
import factory.asserts.Callback;

public class ProductTest extends UnitTest {
	
	Product product = null;
	
	@Before
	public void setUp() {
		//FactoryBoy.delete(Product.class);
	    FactoryBoy.lazyDelete();
	}
 
	@Test
	public void testCreateProduct() throws Exception {
	    // when use assertDifference, DON'T use lazyDelete(), OR call a create() method at first.
	    FactoryBoy.delete(Product.class); 
		assertDifference(Product.class, 1, new Callback() {
			@Override
            public void run() {
				product = FactoryBoy.build(Product.class);
				product.save();	            
            }
		});
	}
	
	@Test
	public void testUpdateProduct() {
		product = FactoryBoy.create(Product.class);
		product.name = "New Name";
		product.save();
		Product p = Product.findById(product.id);
		assertEquals("New Name", p.name);
	}
	
	@Test
	public void testFindByName() {
		Product product = FactoryBoy.create(Product.class, new BuildCallback<Product>() {
			@Override
            public void build(Product target) {
				target.name = "HHKB";
            }
		});
		
		Product p = Product.find("byName", "HHKB").first();
		assertEquals(product.id, p.id);
	}
	
	@Test
	public void testDeleteProduct() throws Exception {
		product = FactoryBoy.create(Product.class);
		assertDifference(Product.class, -1, new Callback() {
			@Override
            public void run() {
				product.delete();	            
            }
		});
	}
	
	@Test
	public void testGetNamedProduct() throws Exception {
		product = FactoryBoy.create(Product.class, "hhkb");
		assertEquals("HHKB", product.name);
		assertEquals(new BigDecimal("2000.00"), product.price);
		
		Product product2 =FactoryBoy.create(Product.class, "hhkb2"); 
		assertEquals("HHKB Pro2", product2.name);
		assertEquals(new BigDecimal("2000.00"), product2.price);
	}
	
	@Test
	public void testBatchCreateProducts() throws Exception {
	    FactoryBoy.delete(Product.class);
		assertDifference(Product.class, 5, new Callback() {
			@Override
            public void run() {
				List<Product> products = FactoryBoy.batchCreate(5, Product.class, new SequenceCallback<Product>() {
					@Override
                    public void sequence(Product target, int seq) {
						target.name = "Test Product " + seq;
						target.price = BigDecimal.TEN.add(new BigDecimal(seq));
                    }
				});
				assertEquals(5, products.size());
			}
		});
	}
	
}
