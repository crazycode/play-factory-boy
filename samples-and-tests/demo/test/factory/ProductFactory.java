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

    @Factory(name="hhkb")
    public Product defineHhkb(Product product) {
        product.name = "HHKB";
        product.price = new BigDecimal("2000.00");
        return product;
    }

    @Factory(name="hhkb2", base="hhkb")
    public Product defineHhkb2(Product product) {
        product.name = "HHKB Pro2";
        return product;
    }
}
