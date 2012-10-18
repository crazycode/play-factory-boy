*{
 *  Call FactoryBoy at Selenium Test.
 *  id (optional) identity the variable of factoryboy created object.
 *  type (required) Model type to create.
 *  name (optional) select the factory method.
}*
%{
    if (_delete == 'all') {
        factory.FactoryBoy.deleteAll()
    } else if(_delete) {
        factory.FactoryBoy.lazyDelete()
    }

    if (_type) {
        if (_name) {
            __obj = factory.FactoryBoy.createByName(_type, _name)
        } else {
            __obj = factory.FactoryBoy.createByName(_type)
        }

	    if (_var) {
	        var_name = _var
	    } else {
	        var_name = factory.FactoryBoy.getSimpleVariableName(_type)
	    }
	    
	    _caller.put(var_name, __obj)
	 }
}%

