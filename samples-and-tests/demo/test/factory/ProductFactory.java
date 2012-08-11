package factory;

import static util.DateHelper.t;

import java.math.BigDecimal;

import models.Product;

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
