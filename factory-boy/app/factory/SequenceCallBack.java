package factory;

import play.db.jpa.GenericModel;

public abstract class SequenceCallBack<T extends GenericModel> implements
        BuildCallBack<T> {

    public abstract void sequence(T target, int seq);

    @Override
    public final void build(T target) {
        this.sequence(target, FactoryBoy.sequence(target.getClass()));
    }

}
