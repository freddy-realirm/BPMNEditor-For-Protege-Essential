package org.yaoqiang.bpmn.editor.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.JTabbedPane;

import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.dialog.PanelContainer;

/**
 * XMLTabbedPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class XMLTabbedPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	private JTabbedPane tabbedPane;

	public XMLTabbedPanel(PanelContainer pc, XMLElement owner, List<XMLPanel> panels) {
		super(pc, owner);
		this.setLayout(new BorderLayout());
		tabbedPane = new JTabbedPane();
		for (int i = 0; i < panels.size(); i++) {
			XMLPanel p = panels.get(i);
			tabbedPane.addTab(p.getName(), p);
		}
		if (tabbedPane.getTabCount() > 0) {
			tabbedPane.setSelectedIndex(0);
		}
		add(tabbedPane);
	}

	public XMLPanel getTabbedPane(String name) {
		for (Component c : tabbedPane.getComponents()) {
			if (c instanceof XMLPanel && c.getName().equals(name)) {
				return (XMLPanel) c;
			}
		}
		return null;
	}

	public void addTabbedPane(XMLPanel panel) {
		if (getTabbedPane(panel.getName()) == null) {
			tabbedPane.addTab(panel.getName(), panel);
		}
	}
	
	public void removeTabbedPane(String name) {
		for (Component c : tabbedPane.getComponents()) {
			if (c instanceof XMLPanel && c.getName().equals(name)) {
				tabbedPane.remove(c);
			}
		}
	}
	
	public void saveObjects() {
		for (Component c : tabbedPane.getComponents()) {
			if (c instanceof XMLPanel) {
				((XMLPanel) c).saveObjects();
			}
		}
	}

}
