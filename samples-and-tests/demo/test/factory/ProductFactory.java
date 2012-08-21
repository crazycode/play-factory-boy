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

    @Factory(name = "hhkb")
    public void defineHhkb(Product product) {
        product.name = "HHKB";
        product.price = new BigDecimal("2000.00");
    }

    @Factory(name = "hhkb2", base = "hhkb")
    public void defineHhkb2(Product product) {
        product.name = "HHKB Pro2";
    }

    @Factory(name = "random")
    public void defineRandomProduct(Product product) {
        product.name = "Product " + FactoryBoy.sequence(Product.class);
    }

    @Factory(name = "sequence")
    public void defineSequenceProduct(Product product, int seq) {
        product.name = "Product " + seq;
    }

}
