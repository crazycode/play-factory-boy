package unit;

import models.Order;
import models.OrderItem;
import models.Product;
import models.UnUseModel;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import play.test.UnitTest;
import factory.FactoryBoy;
import factory.ProductFactory;
import factory.callback.BuildCallback;

public class FactoryBoyTest extends UnitTest {

    @Before
    public void setUp() {
        FactoryBoy.lazyDelete();
    }

    @Test
    public void testFactoryBoySequence() {
        int orderSequence = FactoryBoy.sequence(Order.class);
        int productSequence = FactoryBoy.sequence(Product.class);
        int delta = orderSequence - productSequence;
        assertEquals(productSequence + delta, orderSequence);

        assertEquals(orderSequence + 1, FactoryBoy.sequence(Order.class));

        int orderSequence1 = FactoryBoy.sequence(Order.class);
        int productSequence1 = FactoryBoy.sequence(Product.class);
        assertEquals(productSequence1 + delta + 1, orderSequence1);
    }

    @Test(expected = RuntimeException.class)
    public void testFindUnExistsModel() {
        FactoryBoy.findModelFactory(UnUseModel.class);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateNoDefNameModel() {
        FactoryBoy.create(Product.class, "undefined");
    }

    @Test
    public void testFindModelFactory() {
        assertEquals(ProductFactory.class,
                        FactoryBoy.findModelFactory(Product.class).getClass());
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

        FactoryBoy.create(Product.class, new BuildCallback<Product>() {
            @Override
            public void build(Product target) {
                target.name = "New Product";
            }
        });

        assertEquals(2l, Product.count());
    }

    @Test
    public void testRelationModel() {
        Order order = FactoryBoy.create(Order.class);
        assertTrue(order.isPersistent());
        FactoryBoy.create(OrderItem.class);
        assertTrue(order.isPersistent());
        assertEquals(2l, Order.count());
        assertEquals(1l, OrderItem.count());
        FactoryBoy.lazyDelete();
        FactoryBoy.create(Order.class);
        assertEquals(0l, OrderItem.count());
        assertEquals(1l, Order.count());

    }
}
