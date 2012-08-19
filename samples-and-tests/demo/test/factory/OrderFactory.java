package factory;

import models.Order;
import models.OrderItem;

public class OrderFactory extends ModelFactory<Order> {

    @Override
    public Order define() {
        Order order = new Order();
        order.consignee = "Ridge Tang";
        order.address = "Shanghai City, China";
        return order;
    }

    @Override
    public Class<?>[] relationModels() {
        return new Class<?>[] { OrderItem.class };
    }

}
