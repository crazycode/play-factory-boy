package factory;

import play.db.jpa.GenericModel;

public interface SequenceCallBack<T extends GenericModel> {
	void sequence(T target, int seq);
}
