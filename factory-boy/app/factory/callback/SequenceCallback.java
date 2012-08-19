package factory.callback;

import factory.FactoryBoy;
import play.db.jpa.GenericModel;

public abstract class SequenceCallback<T extends GenericModel> implements
        BuildCallback<T> {

    public abstract void sequence(T target, int seq);

    @Override
    public final void build(T target) {
        this.sequence(target, FactoryBoy.sequence(target.getClass()));
    }

}
