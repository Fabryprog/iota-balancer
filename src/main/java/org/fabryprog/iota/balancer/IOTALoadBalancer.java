/**
 * 
 */
package org.fabryprog.iota.balancer;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.camel.Header;
import org.apache.camel.PropertyInject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fabrizio Spataro <fabryprog@gmail.com>
 *
 */
public class IOTALoadBalancer implements Serializable {
	private static final long serialVersionUID = -741884543226247187L;

	private static final Logger LOG = LoggerFactory.getLogger(IOTALoadBalancer.class);
	
	private static final Long MAX_TS = 120000L; // 2 minuti
	
	@PropertyInject("trust.nodes")
	protected String trustNodes;
	
	final List<NodeDTO> nodes = new LinkedList<NodeDTO>();
	final Map<String, String> availableNodes = new HashMap<String, String>();
	
	public void init() {
		//available node
		for(String n: trustNodes.split(",")) {
			String[] pair = n.split(":");
			
			availableNodes.put(pair[0], pair[1]);
		}
	}
	
	public String getUrl(@Header("_id") String id) {
		return availableNodes.get(id);
	}
	
	public void addNode(@Header("_id") String id, @Header("_value") String load) {
		LOG.info("ADD Node {} {}", id, load);
		if(availableNodes.containsKey(id)) {
			for (NodeDTO n : nodes) {
				if(id.equals(n.getId())) {
					nodes.remove(n);
					
					break;
				}
			}
			nodes.add(new NodeDTO(id, availableNodes.get(id), Double.valueOf(load)));
		}
	}

	public void removeNode(@Header("_id") String id) {
		if(availableNodes.containsKey(id)) {
			for (NodeDTO n : nodes) {
				if(id.equals(n.getId())) {
					nodes.remove(n);
					
					break;
				}
			}
		}
	}

	public synchronized String route() {
		NodeDTO node = null;
		
		LOG.info("Nodes size {}", nodes.size());

		List<NodeDTO> expiredNodes = new LinkedList<NodeDTO>();
		
		Collections.sort(nodes, new Comparator<NodeDTO>() {
			@Override
			public int compare(NodeDTO o1, NodeDTO o2) {
				int c = o1.getLoad().compareTo(o2.getLoad());
				return c == 0 ? -1 : c;
			}
		});

		for (NodeDTO n : nodes) {
			LOG.info("Loop node {} url {}",n.getId(), n.getUrl());
			
			if (n.getTs() > Calendar.getInstance().getTimeInMillis() - MAX_TS) {
				LOG.info("Routing to node  {} url {}",n.getId(), n.getUrl());
				node = n;
			} else {
				expiredNodes.add(n);
			}
		}

		Integer idx = new Random().nextInt(nodes.size());
		Integer loop = 0;
		for (NodeDTO n : nodes) {
			if (idx == loop) {
				LOG.info("[RANDOM] Routing to node  {} url {}",n.getId(), n.getUrl());

				node = n;
			}
		}
		
		nodes.removeAll(expiredNodes);
		
		return node != null ? node.getUrl() : null;
	}

	//used by junit test
	protected List<NodeDTO> getNodes() {
		return nodes;
	}

	//used by junit test
	protected Map<String, String> getAvailableNodes() {
		return availableNodes;
	}
	
	class NodeDTO {

		private String id;
		private String url;
		private Double load;
		private Long ts;

		public NodeDTO(String id, String url, Double load) {
			this.id = id;
			this.url = url;
			this.load = load;
			this.ts = Calendar.getInstance().getTimeInMillis();
		}

		public String getId() {
			return id;
		}

		public String getUrl() {
			return url;
		}

		public Double getLoad() {
			return load;
		}

		public Long getTs() {
			return ts;
		}

	}
}
