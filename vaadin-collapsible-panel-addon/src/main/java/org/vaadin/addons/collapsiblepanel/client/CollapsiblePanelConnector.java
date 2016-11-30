package org.vaadin.addons.collapsiblepanel.client;

import org.vaadin.addons.collapsiblepanel.CollapsiblePanel;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.InlineHTML;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.VConsole;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.VPanel;
import com.vaadin.shared.ui.Connect;

/**
 * @author cthiel
 *
 */
@Connect(CollapsiblePanel.class)
public class CollapsiblePanelConnector extends AbstractExtensionConnector implements AttachEvent.Handler {

	private static final long serialVersionUID = 1L;
	private static final String openIconHTML = "<span class=\"v-icon\" style=\"font-family: " + "FontAwesome"
			+ ";\">&#x" + Integer.toHexString(0XF078) + ";</span>";
	private static final String closedIconHTML = "<span class=\"v-icon\" style=\"font-family: " + "FontAwesome"
			+ ";\">&#x" + Integer.toHexString(0XF054) + ";</span>";

	private VPanel panel;

	private boolean collapsed = false;

	private final InlineHTML bullet = new InlineHTML();

	CollapsiblePanelServerRpc rpc = RpcProxy.create(CollapsiblePanelServerRpc.class, this);

	@Override
	protected void extend(final ServerConnector target) {
		this.panel = (VPanel) ((ComponentConnector) target).getWidget();

		this.panel.addAttachHandler(this);
	}

	@Override
	public CollapsiblePanelState getState() {
		return (CollapsiblePanelState) super.getState();
	}

	@Override
	public void onAttachOrDetach(final AttachEvent event) {
		VConsole.error("onAttack: " + event.isAttached());
		if (event.isAttached()) {
			this.panel.captionNode	.getStyle()
									.setCursor(Cursor.POINTER);
			this.bullet.setHTML(openIconHTML);

			// this is the small arrow picture
			this.panel.captionNode.insertFirst(this.bullet.getElement());

			DOM.sinkEvents(this.panel.captionNode, Event.ONCLICK);

			DOM.setEventListener(this.panel.captionNode, new EventListener() {
				@Override
				public void onBrowserEvent(final Event event) {
					if (event.getTypeInt() == Event.ONCLICK) {
						// ok, now it gets dirty :D
						// to make the content invisible remove it from the
						// parent, to make it visible again, add it at the same
						// position
						// in the current implementation of VPanel it is
						// directly before the bottom decoration
						if (CollapsiblePanelConnector.this.collapsed) {
							expand(true);
						} else {
							collapse(true);
						}
					}
				}
			});
		}
	}

	@Override
	public void onStateChanged(final StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);

		if (getState().collapsed) {
			collapse(false);
		} else {
			expand(false);
		}
	}

	private void expand(final boolean fireRpc) {
		if (this.collapsed) {
			this.panel	.getElement()
						.insertBefore(this.panel.contentNode, this.panel.bottomDecoration);
			this.bullet.setHTML(openIconHTML);
			this.collapsed = false;

			if (fireRpc) {
				this.rpc.onExpand();
			}
		}
	}

	private void collapse(final boolean fireRpc) {
		if (!this.collapsed) {
			this.panel.contentNode.removeFromParent();
			this.bullet.setHTML(closedIconHTML);
			this.collapsed = true;

			if (fireRpc) {
				this.rpc.onCollapse();
			}
		}
	}

}
