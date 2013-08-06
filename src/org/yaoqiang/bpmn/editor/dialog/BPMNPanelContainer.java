package org.yaoqiang.bpmn.editor.dialog;

import org.yaoqiang.dialog.PanelContainer;

/**
 * BPMNPanelContainer
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class BPMNPanelContainer extends PanelContainer {

	private static final long serialVersionUID = 1L;
	
	public BPMNPanelContainer(BPMNElementDialog parentDialog) {
		super(parentDialog);
	}

	public void init() {
		panelFactory = new BPMNPanelFactory(this);
	}

	public void setActiveObject(Object el, String type) {
		setModified(false);
		setViewPanel(getPanelFactory().getPanel(el, type));
	}

	public BPMNElementDialog getParentDialog() {
		return (BPMNElementDialog) parentDialog;
	}

	public BPMNPanelFactory getPanelFactory() {
		return (BPMNPanelFactory) panelFactory;
	}

}
