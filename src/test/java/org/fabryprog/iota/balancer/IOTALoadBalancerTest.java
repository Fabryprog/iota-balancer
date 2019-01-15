/**
 * 
 */
package org.fabryprog.iota.balancer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Fabrizio Spataro <fabryprog@gmail.com>
 *
 */
public class IOTALoadBalancerTest {
	
	
	@Test
	public void readPropertiesTest() {
		IOTALoadBalancer instance = new IOTALoadBalancer();
		
		instance.trustNodes = "fabry-01:fabry-01.iota.it,fabry-02:fabry-02.iota.it,fabry-03:fabry-03.iota.it";
		instance.init();
		
		assertEquals(instance.getAvailableNodes().size(), 3);
		
		assertTrue(instance.getAvailableNodes().get("fabry-01") != null);
		assertEquals(instance.getAvailableNodes().get("fabry-01"), "fabry-01.iota.it");
		
		assertTrue(instance.getAvailableNodes().get("fabry-02") != null);
		assertEquals(instance.getAvailableNodes().get("fabry-02"), "fabry-02.iota.it");
		
		assertTrue(instance.getAvailableNodes().get("fabry-03") != null);
		assertEquals(instance.getAvailableNodes().get("fabry-03"), "fabry-03.iota.it");

		assertTrue(instance.getAvailableNodes().get("fabry-xx") == null);
	}
}
