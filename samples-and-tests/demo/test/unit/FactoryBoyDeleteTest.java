package unit;

import java.util.HashSet;

import models.Category;
import models.Product;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;
import factory.FactoryBoy;
import factory.callback.BuildCallback;

public class FactoryBoyDeleteTest extends UnitTest {

    @Before
    public void setUp() throws Exception {
        FactoryBoy.delete(Product.class, Category.class);

        final Category parent = FactoryBoy.create(Category.class);
        FactoryBoy.batchCreate(5, Category.class,
                        new BuildCallback<Category>() {
                            @Override
                            public void build(Category category) {
                                category.parent = parent;
                                category.displayOrder = FactoryBoy
                                                .sequence(Category.class);
                            }
                        });
        Category top = FactoryBoy.create(Category.class);
        parent.parent = top;
        parent.save();

        FactoryBoy.create(Product.class,
                        new BuildCallback<Product>() {
                            @Override
                            public void build(Product target) {
                                target.categories = new HashSet<Category>();
                                target.categories.add(parent);
                            }
                        });

    }

    @Test(expected = RuntimeException.class)
    public void testCustomDeleteFailed() throws Exception {
        FactoryBoy.delete(Category.class);
    }

    @Test
    public void testCustomDeleteProduct() throws Exception {
        assertEquals(7l, Category.count());
        assertEquals(1l, Product.count());
        FactoryBoy.delete(Product.class);
    }

    @Test
    public void testDeleteAll() throws Exception {
        FactoryBoy.deleteAll();
        assertEquals(0l, Product.count());
    }

}
