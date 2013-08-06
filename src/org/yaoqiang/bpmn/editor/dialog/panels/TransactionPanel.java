package org.yaoqiang.bpmn.editor.dialog.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;

import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.model.elements.activities.Transaction;

/**
 * TransactionPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class TransactionPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;
	
	protected XMLComboPanel methodPanel;

	protected XMLTextPanel uriPanel;

	public TransactionPanel(BPMNPanelContainer pc, Transaction owner) {
		super(pc, owner);
		this.setLayout(new BorderLayout());

		final Component emptyPanel = Box.createVerticalStrut(38);
		List<String> choices = new ArrayList<String>();
		choices.add("Compensate");
		choices.add("Image");
		choices.add("Store");
		choices.add("URI");
		methodPanel = new XMLComboPanel(pc, owner.getMethodAttribute(), "tranMethod", choices, false, false, true);
		methodPanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object sel = methodPanel.getSelectedItem();
				if (sel.equals("URI")) {
					emptyPanel.setVisible(false);
					uriPanel.setVisible(true);
				} else {
					uriPanel.setVisible(false);
					emptyPanel.setVisible(true);
				}
			}
		});

		uriPanel = new XMLTextPanel(pc, owner.getMethodAttribute(), "uri", "");
		if (methodPanel.getSelectedItem().equals("URI")) {
			emptyPanel.setVisible(false);
			uriPanel.setVisible(true);
			uriPanel.setText(owner.getMethod());
		} else {
			uriPanel.setVisible(false);
			emptyPanel.setVisible(true);
		}
		
		this.add(methodPanel, BorderLayout.NORTH);
		this.add(uriPanel, BorderLayout.CENTER);
		this.add(emptyPanel, BorderLayout.SOUTH);
	}
	
	public void saveObjects() {
		String method = "##Compensate";
		Object sel = methodPanel.getSelectedItem();
		if (sel.equals("URI")) {
			method = uriPanel.getText();
		} else if (sel.equals("Image")) {
			method = "##Image";
		} else if (sel.equals("Store")) {
			method = "##Store";
		}
		((Transaction)owner).setMethod(method);
	}
	
	
}
