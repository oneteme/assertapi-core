package org.usf.assertapi.core;

class ResponseComparatorProxyTest extends ResponseComparatorTest {
	
	public ResponseComparatorProxyTest() {
		super.comparator = new ResponseComparatorProxy(new ResponseComparator(), (api, res)-> {});
	}

}
