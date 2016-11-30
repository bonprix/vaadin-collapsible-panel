package org.vaadin.addons.collapsiblepanel.client;

import com.vaadin.shared.communication.ServerRpc;

public interface CollapsiblePanelServerRpc extends ServerRpc {
	
	void onExpand();
	
	void onCollapse();

}
