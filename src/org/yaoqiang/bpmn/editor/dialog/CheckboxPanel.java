package org.yaoqiang.bpmn.editor.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import org.yaoqiang.dialog.Panel;
import org.yaoqiang.dialog.PanelContainer;
import org.yaoqiang.util.Constants;
import org.yaoqiang.util.Resources;


/**
 * CheckboxPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class CheckboxPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected String title;

	protected JCheckBox jcb;

	protected boolean defaultValue;

	public CheckboxPanel(final PanelContainer pc, Object owner, String title) {
		this(pc, owner, title, true, true);
	}

	public CheckboxPanel(final PanelContainer pc, Object owner, String title, boolean defaultValue) {
		this(pc, owner, title, true, defaultValue);
	}

	public CheckboxPanel(final PanelContainer pc, Object owner, boolean defaultValue) {
		this(pc, owner, null, true, defaultValue);
	}

	public CheckboxPanel(final PanelContainer pc, Object owner, String title, boolean isEnabled, boolean defaultValue) {
		super(pc, owner);
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.title = title;
		this.defaultValue = defaultValue;

		JLabel jl = new JLabel(" "+ Resources.get(title));

		jcb = new JCheckBox();
		jcb.setBorder(BorderFactory.createEmptyBorder());
		boolean value = Constants.SETTINGS.getProperty(title, defaultValue ? "1" : "0").equals("1");
		jcb.setSelected(value);
		jcb.setEnabled(isEnabled);
		jcb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				pc.panelChanged();
			}
		});

		this.add(jcb, BorderLayout.WEST);
		this.add(Box.createHorizontalGlue(), BorderLayout.EAST);
		this.add(jl, BorderLayout.CENTER);
	}

	public JCheckBox getCheckBox() {
		return jcb;
	}

	public boolean isSelected() {
		return jcb.isSelected();
	}

	public void setSelected(boolean b) {
		jcb.setSelected(b);
	}

	public void addActionListener(ActionListener l) {
		jcb.addActionListener(l);
	}

	public void saveObjects() {
		if (jcb.isSelected()) {
			Constants.SETTINGS.put(title, "1");
		} else {
			Constants.SETTINGS.put(title, "0");
		}
	}

	public void setEnabled(boolean b) {
		super.setEnabled(b);
		jcb.setEnabled(b);
	}

}