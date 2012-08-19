package factory;

import java.math.BigDecimal;

import models.Order;
import models.OrderItem;
import models.Product;

public class OrderItemFactory extends ModelFactory<OrderItem> {

    @Override
    public OrderItem define() {
        OrderItem orderItem = new OrderItem();
        orderItem.order = FactoryBoy.create(Order.class);
        orderItem.price = BigDecimal.TEN;
        orderItem.product = FactoryBoy.create(Product.class, "random");
        orderItem.qty = 1;
        return orderItem;
    }

}
