package factory;

import play.db.jpa.GenericModel;

public interface SequenceCallBack<T extends GenericModel> {
	public T sequence(T target, int seq);
}
