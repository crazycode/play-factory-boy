package factory;

import play.db.jpa.GenericModel;

public abstract class ModelFactory<T extends GenericModel> {

    public abstract T define();

    public Class<?>[] relationModels() {
        return null;
    }

}
