# 概述

## Play Framework 1.x Fixture机制的缺点

Play Framework 1.x提供了Fixture功能，即通过yaml文件定义初始化测试数据。

在测试中如果大量使用Fixture，除了生成、修改测试数据比较困难外，还有以下缺点：

* YAML文件在适应不同测试条件是不够灵活，只能通过定义大量Fixuture来适用不同条件，难维护
* YAML文件定义的Fixture与Test内容不在同一文件，编写测试时需要来回切换文件检查测试数据
* 不支持Model重构，在数据Model变更后，维护大量Fixture的字段相当烦人
* Fixture是全局的，在多个测试共用时容易互相影响
* 没有直接提供在代码中快速引用Fixture的机制，只能用idCache查找，很难用

## Play FactoryBoy设计目标

Play FactoryBoy的设计目标就是用于解决上述问题，其主要设计思想来自在Ruby on Rails中很流行的Factory Girl（一种使用Factory/Build模式来构造测试数据的工具），所以命名为Play Factory Boy。

Play FactoryBoy有以下优点：

* 完全使用Java代码定义测试数据，强类型，支持重构
* 通过Callback方法重定义测试数据
* 支持批量生成测试数据
* 支持通过build方法生成数据对象而不保存到数据库

源代码在Github上：<https://github.com/crazycode/play-factory-boy>， 目前使用的兄弟只能自己把这个模块加入项目依赖中。稍后我会推动把Play FactoryBoy加入官方模块源。

# 使用指南

## 引入Play FactoryBoy
在项目的conf/dependencies.yml中加入以下内容：

    require:
        - play
        - play -> factory-boy 0.1

## FactoryBoy基本用法
为了给一个Model类生成测试数据，需要为Model定义一个Factory类，这个类通过命名约定与Model类对应：

* 包名：Play中Model的包名以models开头，Model Factory类包名把Model的包名中models换成factory，其它一样
* 类名：为Model类名加上Factory后缀

例如一个Model类：*models.Product*，按以上规则，对应的Factory类为*factory.ProductFactory*

在完成Factory类的定义后，就可以在测试代码中通过FactoryBoy提供的方法简单完成测试数据的准备了。

FactoryBoy提供了多种方式进行测试数据的建立：

### create
最简单直接的建立测试数据，是使用create方法：

    Product product = FactoryBoy.create(Product.class);

执行完成后，测试数据中Product表会产生一条新的测试数据。测试代码可以直接使用product变量进行测试。

### 按名称进行create
在Model对应的Factory中有提供@Factory方法后，可通过@Factory对应的名称建立测试数据：

    Product product = FactoryBoy.create(Product.class, "hhkb");

具体参考后面*定义Model Factory*的相关内容

### 使用回调方法BuildCallback或SequenceCallback进行create
并不需要所有的自定义数据都通过Factory类中定义，可以通过在测试代码中使用BuildCallback方法对特定属性进行覆盖：

如以下建立Category对象的层次依赖关系：

        final Category parent = FactoryBoy.create(Category.class);
        FactoryBoy.create(Category.class,
                        new BuildCallback<Category>() {
                            @Override
                            public void build(Category category) {
                                category.parent = parent;
                            }
                        });

或使用SequenceCallback方法，为生成的对象按序列指定值：

        final Category parent = FactoryBoy.create(Category.class);
        FactoryBoy.create(Category.class,
                        new SequenceCallback<Category>() {
                            @Override
                            public void sequence(Category category, int seq) {
                                category.name = "Child #" + seq;
                                category.parent = parent;
                            }
                        });

在使用BuildCallback或SequenceCallback时也可以指定一个命名，用于在@Factory指定的对象上进行自定义数据：

        Product product = FactoryBoy.create(Product.class, "hhkb",
                        new BuildCallback<Product>() {
                            @Override
                            public void build(Product target) {
                                target.name = "HHKB Pro Type-S";
                            }
                        });

### 批量create
以上三种create方法都有对应的批量创建方法(batchCreate)，用于生成多个测试数据：

    # 创建5条默认Product数据
    Product product = FactoryBoy.batchCreate(5, Product.class);

    # 创建10条hhkb命名的Product数据
    Product product = FactoryBoy.batchCreate(10, Product.class, "hhkb");

    # 创建15条自定义数据
    FactoryBoy.batchCreate(15, Category.class,
                        new SequenceCallback<Category>() {
                            @Override
                            public void sequence(Category category, int seq) {
                                category.name = "Child #" + seq;
                                category.parent = parent;
                            }
                        });

### build方法
以上create方法均有对应的build方法版本，如：

    Product product = FactoryBoy.build(Product.class);

    Product product = FactoryBoy.build(Product.class, "hhkb");

    Product product = FactoryBoy.batchBuild(5, Product.class);

build方法与create方法的区别是产生的对象没有保存到数据库。

通过build方法，可以快速生成纯POJO对象，用于辅助测试。

### last方法/lastOrCreate方法
有些情况下，构建Factory类时需要使用其它类的实例，如：

    public class OrderItemFactory extends ModelFactory<OrderItem> {
        @Override
        public OrderItem define() {
            OrderItem orderItem = new OrderItem();
            orderItem.order = FactoryBoy.create(Order.class);
            orderItem.product = FactoryBoy.create(Product.class, "random");
            return orderItem;
        }
    }

上例中使用FactoryBoy.create()方法生成order和product属性，可能会产生不需要的实例，而且不容易在测试中引用这些产生的实例，在测试时亦无法灵活控制其创建方式。

FactoryBoy提供last方法，只要把上例中的create方法换成last方法，last方法会得到最后一次调用create方法所产生的实例对象：

    public class OrderItemFactory extends ModelFactory<OrderItem> {
        @Override
        public OrderItem define() {
            OrderItem orderItem = new OrderItem();
            orderItem.order = FactoryBoy.last(Order.class);
            orderItem.product = FactoryBoy.last(Product.class, "random");
            return orderItem;
        }
    }

在测试时，按以下顺序调用FactoryBoy：

    Order order = FactoryBoy.create(Order.class);
    Product product = FactoryBoy.create(Product.class);
    OrderItem orderItem = FactoryBoy.create(OrderItem.class);
    
这时，orderItem的order和product属性会使用第1、2行所创建的order和product对象，这些对象可以使用前述任何create方法创建，更加灵活以适应不同测试场景。

注意：如果在调用FactoryBoy.last()方法时，所指定的类没有被调用过FactoryBoy.create()，测试会中断并抛出异常。

如果希望提供一个更安全的选择，可以使用FactoryBoy.lastOrCreate()方法，此方法将先调用FactoryBoy.last()方法，如果没有找到预先创建的实例，则会调用FactoryBoy.create()方法建立新的实例。

相对FactoryBoy.last()方法，FactoryBoy.lastOrCreate()提供了一个更安全方便的选择，我推荐在ModelFactory中，如果需要使用其它类实例，尽量使用FactoryBoy.lastOrCreate()方法。


## 定义Model Factory

以下是一个models.Product类对应的ModelFactory类的例子：

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

这个类需要定义一个默认的define()方法，这一方法返回默认情况下的一个Product对象。

Factory类还支持以下方法：

### @Factory标注支持按名字定义数据

一个Model Factory类可以有声明多个@Factory标注的方法，用于通过名称定义不同类别的Model对象。默认情况下，@Factory标注的方法使用define()返回的对象作为初始对象进行进一步的数据加工。

@Factory标注的方法支持以下2种形式的代码实现：

    /**
     * 在测试代码中可使用:
     *     Product product = FactoryBoy.create(Product.class, "hhkb");
     * 进行调用.
     */
    @Factory(name = "hhkb")
    public void defineHhkb(Product product) {
        product.name = "HHKB";
        product.price = new BigDecimal("2000.00");
    }

    /**
     * 在测试代码中可使用:
     *     Product product = FactoryBoy.create(Product.class, "sequence");
     * 进行调用，每次调用生成的product.name都不一样.
     */
    @Factory(name = "sequence")
    public void defineSequenceProduct(Product product, int seq) {
        product.name = "Product " + seq;
    }

## Model Factory自定义数据清除方法
测试数据库一般使用内存数据库，测试时我们可以简单通过FactoryBoy.deleteAll()把整个数据库清空。FactoryBoy.deleteAll()方法通过调用Fixtures.deleteDatabase()方法实现清空数据库。

但有时希望保留一些初始数据，只清空测试相关的数据，而这时可能因为一些数据约束而清除失败。

Play FactoryBoy提供了2种方法进行更精细的数据清除：

### 定义相关Model (Optional)
在Model Factory类中可以实现relationModels方法，指定需要在删除当前Model前，先删除的其它Model，这样可以避免因外键约束而导致删除失败。

以下是一个例子：

    public class OrderFactory extends ModelFactory<Order> {

        // ......

        @Override
        public Class<?>[] relationModels() {
            return new Class<?>[] { OrderItem.class };
        }

    }


## UnitTest和FunctionalTest中的使用

### Unit setUp方法
Play FactoryBoy提供了三种方法放在UnitTest的setUp方法中，以进行数据清理。

#### FactoryBoy.deleteAll() (推荐)
完整的删除整个测试数据库：

    @Before
    public void setUp() {
        FactoryBoy.deleteAll();
    }

如果应用程序有插件需要加载一些配置数据，则不要使用此方法。

#### FactoryBoy.lazyDelete() (推荐)
延迟删除数据，只有在Model第一次调用FactoryBoy.create方法时才进行数据清除：

    @Before
    public void setUp() {
        FactoryBoy.lazyDelete();
    }

#### FactoryBoy.delete(Model.class...)
按顺序删除指定的Model：

    @Before
    public void setUp() throws Exception {
        FactoryBoy.delete(Product.class, Category.class);
    }
    
## Selenium测试中的使用
Play FactoryBoy提供了一个#{factory}标签，用于在Selenium测试中使用FactoryBoy，可完全代替PlayFramework 1.x提供的#{fixture}标签。

以下是一个例子：

    #{factory delete:'all’/}
    #{factory var:’pp', type:'Product'/}

    #{selenium 'Test Get Products'}
      open('/products')
      verifyTextPresent('1 Products')
  
      open('/products/${pp.id}')
      verifyValue('id=object_name’,’${pp.name}’)
    #{/selenium}

上例中，#{factory}标签了以下选项：

* delete:’all’  用于删除所有测试数据。
* type:  指定需要加载的Model类名，注意需要写全除models外的完整类名称，如类models.cms.Post对应的加载写法应为type:’cms.Post’
* id: 可选，指定FactoryBoy生成对象在测试时所使用的名字
* name: 可选，调用指定ModelFactory对应名称的生成方法，参考 [link](按名称进行create)按名称进行create

# 附录
在samples-and-tests/demo目录有完整的使用例子和测试用例。

以下是一个完整的TestCase例子：

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
