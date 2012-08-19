package factory;

import models.Category;

public class CategoryFactory extends ModelFactory<Category> {

    @Override
    public Category define() {
        Category category = new Category();
        category.name = "Category." + FactoryBoy.sequence(Category.class);
        return category;
    }

}
