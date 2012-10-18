package functional;

import models.Product;

import org.junit.Before;
import org.junit.Test;

import play.test.FunctionalTest;
import factory.FactoryBoy;
import factory.callback.SequenceCallback;

public class ProductsTest extends FunctionalTest {

    @Before
    public void setUp() {
        FactoryBoy.lazyDelete();
    }

    @Test
    public void testList() {
        FactoryBoy.batchCreate(5, Product.class,
                new SequenceCallback<Product>() {
                    public void sequence(Product target, int seq) {

                    }
                });

    }
    
    @Test
    public void testCreateByName() {
    	Product p = FactoryBoy.createByName("Product");
    	assertNotNull(p);
    	assertTrue(p.isPersistent());
    }
    
    @Test
    public void testCreateByName2() {
    	Product p = FactoryBoy.createByName("Product", "hhkb");
    	assertNotNull(p);
    	assertTrue(p.isPersistent());
    }
}
