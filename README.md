# Overview

## The disadvantages of Play Framework 1.x Fixture

Play Framework 1.x provides Fixture functionality of defining initial data for testing via YAML file

If you make heavy use of Fixture, it is more difficult to generate or modify test data. In addition, it has the following drawbacks:

* It is not quite flexible for YAML file to adapt to different test conditions, because only by using a number of Fixture can it be done so that it is difficult to maintain.
* The contents of Test and Fixture defined by YAML files are on different files, so you need to switch files back and forth to check test data when writing tests.
* It is hard to maintain a lot of Fixture after data changes occur, because Model refactoring are not supported. 
* Fixture will interfere with each other when used in multiple tests because of its global scope.
* It is hard to use Fixture, because there is no quick reference for Fixture in the code except finding it with idCache. 

## The objectives of Play FactoryBoy

Play FactoryBoy is designed to solve the problem above. The main idea comes from the popular FactoryGirl among Ruby on Rails ( a tool used to construct test data using Factory/Build pattern ), so named for Play FactoryBoy.  

The advantages are as follows:

* define test data and strong type and support refactoring implemented fully in Java
* redefine test data by calling Callback method
* generate test data in batches
* generate data objects but not saved to the database via build method 

The source code is in Github：<https://github.com/crazycode/play-factory-boy>.
Currently, you can only add Play FactoryBoy module to Project Dependencies. Later, I will add it to official module source.

# User's Guide

## Introduction of Play FactoryBoy
Add the following contents in conf/dependencies.yml of the project:

    require:
        - play
        - play -> factory-boy 0.1

## The basic usage of FactoryBoy
Define Factory class to generate test data for Model class and correspond to Model class via Naming Conventions, as follow:

* Package name: package name of Model class starts with models in Play but package name of Model Factory class starts with factory and others is same with Model class.
* Class name: add a “Factory” suffix for Model class name.

For example, there is a Model class called *models.Product*.The corresponding Factory class name is *factory.ProductFactory* by the rules above.

After defining Factory class, you get preparations for test data done easily by using FactoryBoy in the test code. 

FactoryBoy provides several ways to set up test data:

### create method
Set up test data directly using create method, as follows: 

    Product product = FactoryBoy.create(Product.class);

When executed, there is a new test data in Product table. You can use product variable directly for testing.

### create by name
There is @Factory method within Factory that corresponds to Model. As a result, test data can be set up by name corresponding to @Factory, as follows: 

    Product product = FactoryBoy.create(Product.class, "hhkb"); 

Refer to *Define Model Factory* in the following section.

### create by callback method such as BuildCallback or SequenceCallback
Defining all custom data via Factory class is not necessary, because you can override specific attributes via BuildCallback method in the test code, as follows: 

Establish hierarchical dependencies among Order objects as shown in the example below:

        final Category parent = FactoryBoy.create(Category.class);
        FactoryBoy.create(Category.class,
                        new BuildCallback<Category>() {
                            @Override
                            public void build(Category category) {
                                category.parent = parent;
                            }
                        });

or use SequenceCallback method to specify values for generated objects by sequence, as follows: 

        final Category parent = FactoryBoy.create(Category.class);
        FactoryBoy.create(Category.class,
                        new SequenceCallback<Category>() {
                            @Override
                            public void sequence(Category category, int seq) {
                                category.name = "Child #" + seq;
                                category.parent = parent;
                            }
                        });

specify a name by BuildCallback or SequenceCallback method, which is used to customize the data on the object specified by @Factory, as follows:

        Product product = FactoryBoy.create(Product.class, "hhkb",
                        new BuildCallback<Product>() {
                            @Override
                            public void build(Product target) {
                                target.name = "HHKB Pro Type-S";
                            }
                        });

### create in batch 
There are corresponding methods (batchCreate) for three create methods above, which is designed for generating multiple test data, as follows:

    # create 5 default Product data
    Product product = FactoryBoy.batchCreate(5, Product.class);

    # create 10 Product data by the name of  hhkb
    Product product = FactoryBoy.batchCreate(10, Product.class, "hhkb");

    # create fifteen custom data 
    FactoryBoy.batchCreate(15, Category.class,
                        new SequenceCallback<Category>() {
                            @Override
                            public void sequence(Category category, int seq) {
                                category.name = "Child #" + seq;
                                category.parent = parent;
                            }
                        });

### build method
There are corresponding methods (build) for create methods above, as follows:

    Product product = FactoryBoy.build(Product.class);

    Product product = FactoryBoy.build(Product.class, "hhkb");

Product product = FactoryBoy.batchBuild(5, Product.class);

The difference is that the object generated by build method is not saved to the database but by create method is. 

POJO object can be generated rapidly for aided test by build method.

### last method and lastOrCreate method
In some cases, it is required to use the instances of other classes while Factory class is built. For example:

    public class OrderItemFactory extends ModelFactory<OrderItem> {
        @Override
        public OrderItem define() {
            OrderItem orderItem = new OrderItem();
            orderItem.order = FactoryBoy.create(Order.class);
            orderItem.product = FactoryBoy.create(Product.class, "random");
            return orderItem;
        }
    }

In the example above, it is possible to generate unnecessary instances, hard to use these instances and inflexible to control how they are created on test when order and product attributes are generated by FactoryBoy.create() method.

FactoryBoy provides last method. In the example above, just replace create method with last method, which will get the instance generated by create method last time, as follows:

    public class OrderItemFactory extends ModelFactory<OrderItem> {
        @Override
        public OrderItem define() {
            OrderItem orderItem = new OrderItem();
            orderItem.order = FactoryBoy.last(Order.class);
            orderItem.product = FactoryBoy.last(Product.class, "random");
            return orderItem;
        }
    }

On test, call FactoryBoy in the following sequence:

    Order order = FactoryBoy.create(Order.class);
    Product product = FactoryBoy.create(Product.class);
    OrderItem orderItem = FactoryBoy.create(OrderItem.class);
    
Then, order and product attributes of orderItem class can use order and product objects created in the first and second row. These objects can be created by any aforementioned _create_ method. So, it is more flexible to adapt to different test scenarios.

Note: test will be interrupted and excexption is thrown if FactoryBoy.last() method is called but specified class does not call FactoryBoy.create() method.

If you hope there is a safer option, you can use FactoryBoy.lastOrCreate() method. This method will call FactoryBoy.last() method first, then if 
pre-created instance can not be found, it will create new instance by FactoryBoy.create() method. 

Compared with FactoryBoy.last() method, FactoryBoy.lastOrCreate() method provides a more safer and convenient option. I recommend you use FactoryBoy.lastOrCreate() method whenever possible if you need to use instances of other classes among ModelFactory.


## define Model Factory

What follows is the example of ModelFactory class corresponding to models.Product class:

    package factory;
    import static util.DateHelper.t;
    import java.math.BigDecimal;
    import models.Product;
    import factory.annotation.Factory;
    public class ProductFactory extends ModelFactory<Product> {
        @Override
        public Product define() {
            Product product = new Product();
            product.name = "Sample Product";
            product.price = BigDecimal.TEN;
            product.expiredAt = t("2012-08-21 12:31");
            return product;
        }
    }

A default define() method is required to be defined in this class, which returns a Product object by default.

Factory class also provides the support for the following methods. See below:

### define data by name via @Factory annotation

Several @Factory annotation methods can be declared in a Model Factory class, which is used to define different kinds of Model objects by name. By default, the object returned by define() method used in @Factory annotation serves as the initial object for further data process. 

The following two forms implemented in the code are supported in @Factory annotation:

    /**
     * used in the test code:
     *     Product product = FactoryBoy.create(Product.class, "hhkb");
     * to be called.
     */
    @Factory(name = "hhkb")
    public void defineHhkb(Product product) {
        product.name = "HHKB";
        product.price = new BigDecimal("2000.00");
    }

    /**
     * used in the test code:
     *     Product product = FactoryBoy.create(Product.class, "sequence");
     * to be called, to generate different product.name on every call
     */
    @Factory(name = "sequence")
    public void defineSequenceProduct(Product product, int seq) {
        product.name = "Product " + seq;
    }

## Custom data cleanup method of Model Factory
In-memory database is used generally in test database, so you can clear the entire database easily by FactoryBoy.deleteAll() method in test. FactoryBoy.deleteAll() method can clear the database by calling  Fixtures.deleteDatabase() method.  

But sometimes you hope to retain some initial data and just clear some test-related data. And then cleanup fails, perhaps due to some data constraints.

Play FactoryBoy provides two methods for the finer data cleanup, as follows:

### Define relative Model (Optional)
RelationModels method can be realized in Model Factory class. You need to delete other Model before deleting current Models, so that the failure of deleting by foreign key constraints can be avoided. 

Here is an example:

    public class OrderFactory extends ModelFactory<Order> {

        // ......

        @Override
        public Class<?>[] relationModels() {
            return new Class<?>[] { OrderItem.class };
        }

    }


## The usage among UnitTest and FunctionalTest

### Unit setUp method
Play FactoryBoy provides three methods in setUp method of UnitTest, which is used for data cleaning.

#### FactoryBoy.deleteAll() (Recommend)
Delete the entire test database completely. See below:

    @Before
    public void setUp() {
        FactoryBoy.deleteAll();
    }

If plug-ins in the application are required to load some configuration data, then don't use this method.

#### FactoryBoy.lazyDelete() (Recommend)
Defer the deletion of the data. Only if the first call is made to FactoryBoy.create method by Model, data cleanup will occur. See below:

    @Before
    public void setUp() {
        FactoryBoy.lazyDelete();
    }

#### FactoryBoy.delete(Model.class...)
Delete specified Model by sequence. See below:

    @Before
    public void setUp() throws Exception {
        FactoryBoy.delete(Product.class, Category.class);
    }

## The usage in Selenium test 
Play FactoryBoy provides a __#{factory}__ tag available for Selenium test where Factoryboy can be used. It can replace a #{fixture} tag provided by PlayFramework 1.x completely.

An example is as follows:

    #{factory delete:'all’}
    #{factory var:’pp', type:'Product'/}

    #{selenium 'Test Get Products'}
      open('/products')
      verifyTextPresent('1 Products')
  
      open('/products/${pp.id}')
      verifyValue('id=object_name’,’${pp.name}’)
    #{/selenium}

In the example above, #{factory} tag offers the following options:

* __delete:’all’__ : Delete all the test data.
* __type__ : Specify Model class name loaded on demand. Note that it is required to write out full class names except models. For example, the corresponding loading syntax of models.cms.Post class is type:’cms.Post’.  
* __id__ (optional): Specify the name used in test, corresponding to the object generated by FactoryBoy.
* __name__ (optional): Call the generation method specified by ModelFactory. Refer to [link](create by name) create by name.

# Appendix
There are complete example and test case in samples-and-tests/demo directory.

Here is a complete example of TestCase:

    package unit;
    import static asserts.ModelAssert.assertDifference;
    import java.math.BigDecimal;
    import java.util.List;
    import models.Product;
    import org.junit.Before;
    import org.junit.Test;
    import play.test.UnitTest;
    import asserts.Callback;
    import factory.FactoryBoy;
    import factory.callback.BuildCallback;
    import factory.callback.SequenceCallback;
    public class ProductTest extends UnitTest {

        Product product = null;

        @Before
        public void setUp() {
            FactoryBoy.lazyDelete();
        }

        @Test
        public void testCreateProduct() throws Exception {
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


