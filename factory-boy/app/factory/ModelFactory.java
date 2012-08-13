package factory;

import play.db.jpa.GenericModel;

public abstract class ModelFactory<T extends GenericModel> {

	private int sequence = 0;
	
	public synchronized int sequence() {
		return sequence++;
	}
	
	public abstract T define();
	
}
