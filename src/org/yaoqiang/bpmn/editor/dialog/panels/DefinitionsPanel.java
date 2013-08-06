package org.yaoqiang.bpmn.editor.dialog.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.model.BPMNModelConstants;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.core.infrastructure.Definitions;

/**
 * DefinitionsPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class DefinitionsPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLPanel namePanel;
	protected XMLComboPanel typeLanguagePanel;
	protected XMLComboPanel expressionLanguagePanel;

	public DefinitionsPanel(BPMNPanelContainer pc, final Definitions owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		namePanel = new XMLTextPanel(pc, owner.get("name"));

		JPanel idNamePanel = new JPanel();
		idNamePanel.setLayout(new BoxLayout(idNamePanel, BoxLayout.X_AXIS));
		final XMLTextPanel idPanel = new XMLTextPanel(pc, owner.get("id"), false);
		idNamePanel.add(idPanel);
		JButton genId = new JButton("Gen-Id");
		genId.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				owner.getNamespaces().remove(owner.getTargetNamespace());
				owner.setId("_" + System.currentTimeMillis());
				owner.setTargetNamespace(BPMNModelConstants.BPMN_TARGET_MODEL_NS + owner.getId());
				owner.getNamespaces().put(owner.getTargetNamespace(), "tns");
				idPanel.setText(owner.getId());
			}
		});
		idNamePanel.add(genId);
		idNamePanel.add(namePanel);
		List<String> choices = new ArrayList<String>();
		choices.add("http://www.w3.org/2001/XMLSchema");
		choices.add("http://www.java.com/javaTypes");
		choices.add("http://java.sun.com/");
		choices.add("http://jcp.org/en/jsr/detail?id=270");
		typeLanguagePanel = new XMLComboPanel(pc, owner.get("typeLanguage"), null, choices, false, true, true);
		typeLanguagePanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object sel = typeLanguagePanel.getSelectedItem();
				if (sel.equals("http://www.w3.org/2001/XMLSchema")) {
					expressionLanguagePanel.setSelectedItem("http://www.w3.org/1999/XPath");
				} else if (sel.equals("http://www.java.com/javaTypes")) {
					expressionLanguagePanel.setSelectedItem("http://www.mvel.org/2.0");
				} else if (sel.equals("http://java.sun.com/")) {
					expressionLanguagePanel.setSelectedItem("http://java.sun.com/products/jsp/");
				} else if (sel.equals("http://jcp.org/en/jsr/detail?id=270")) {
					expressionLanguagePanel.setSelectedItem("http://www.jcp.org/en/jsr/detail?id=245");
				}
			}
		});
		choices = new ArrayList<String>();
		choices.add("http://www.w3.org/1999/XPath");
		choices.add("http://www.mvel.org/2.0");
		choices.add("http://groovy.codehaus.org/");
		choices.add("http://java.sun.com/products/jsp/");
		choices.add("http://www.jcp.org/en/jsr/detail?id=245");
		expressionLanguagePanel = new XMLComboPanel(pc, owner.get("expressionLanguage"), null, choices, false, true, true);

		this.add(idNamePanel);
		this.add(typeLanguagePanel);
		this.add(expressionLanguagePanel);
		this.add(Box.createVerticalGlue());
	}

	public void saveObjects() {
		namePanel.saveObjects();
		expressionLanguagePanel.saveObjects();
		String oldType = ((Definitions) owner).getTypeLanguage();
		String newType = typeLanguagePanel.getSelectedItem().toString();
		typeLanguagePanel.saveObjects();
		if (!oldType.equals(newType)) {
			BPMNModelUtils.refreshTypes((Definitions) owner);
		}
	}

}
