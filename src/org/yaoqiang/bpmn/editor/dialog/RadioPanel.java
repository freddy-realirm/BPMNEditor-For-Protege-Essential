package org.yaoqiang.bpmn.editor.dialog;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.yaoqiang.dialog.Panel;
import org.yaoqiang.dialog.PanelContainer;
import org.yaoqiang.util.Resources;


/**
 * RadioPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class RadioPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected List<?> choices;

	protected ButtonGroup rGroup;

	public RadioPanel(PanelContainer pc, Object owner, String title, List<?> choices) {
		this(pc, owner, title, choices, "", false, true);
	}

	public RadioPanel(PanelContainer pc, Object owner, String title, List<?> choices, String defaultValue) {
		this(pc, owner, title, choices, defaultValue, false, true);
	}

	public RadioPanel(PanelContainer pc, Object owner, String title, List<?> choices, String defaultValue, boolean isVertical, boolean isEnabled) {
		super(pc, owner);

		if (title != null && title.length() != 0) {
			this.setBorder(BorderFactory.createTitledBorder(Resources.get(title)));
		} else {
			this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		}
		this.choices = choices;

		JPanel bgPanel = new JPanel();

		if (isVertical) {
			bgPanel.setLayout(new GridLayout(0, 1));
		} else {
			bgPanel.setLayout(new GridLayout(1, 0));
		}

		rGroup = new ButtonGroup();
		for (int i = 0; i < choices.size(); i++) {
			JRadioButton jr = new JRadioButton(Resources.get(choices.get(i).toString()));
			if (i == 0 && defaultValue.length() == 0 || choices.get(i).equals(defaultValue)) {
				jr.setSelected(true);
			}
			jr.setEnabled(isEnabled);
			jr.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					getPanelContainer().panelChanged();
				}
			});

			rGroup.add(jr);
			bgPanel.add(jr);
		}

		add(bgPanel);
		add(Box.createHorizontalGlue());
	}

	public Object getSelectedItem() {
		int i = getSelectedIndex();
		if (i == -1) {
			return null;
		}
		return choices.get(i);
	}

	public int getSelectedIndex() {
		Enumeration<AbstractButton> e = rGroup.getElements();
		int i = 0;
		while (e.hasMoreElements()) {
			JRadioButton jr = (JRadioButton) e.nextElement();
			if (jr.isSelected()) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public ButtonGroup getButtonGroup() {
		return rGroup;
	}

	public void saveObjects() {

	}

}
