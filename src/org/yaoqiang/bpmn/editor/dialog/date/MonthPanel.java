package org.yaoqiang.bpmn.editor.dialog.date;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.BPMNEditor;

/**
 * MonthPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class MonthPanel extends JPanel implements ItemListener {

	private static final long serialVersionUID = 1L;
	private Locale locale = BPMNEditor.locale;
	private int month;

	private DayPanel dayChooser;
	private JComboBox comboBox;

	private boolean initialized;
	private boolean localInitialize;

	public MonthPanel() {
		setLayout(new BorderLayout());
		Dimension dim = new Dimension(90, 26);
		comboBox = new JComboBox();
		this.setPreferredSize(dim);
		this.setMaximumSize(dim);
		this.setMinimumSize(dim);
		comboBox.addItemListener(this);

		initNames();

		add(comboBox);

		initialized = true;
		setMonth(Calendar.getInstance().get(Calendar.MONTH));
	}

	public void initNames() {
		localInitialize = true;

		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
		String[] monthNames = dateFormatSymbols.getMonths();

		if (comboBox.getItemCount() == 12) {
			comboBox.removeAllItems();
		}

		for (int i = 0; i < 12; i++) {
			comboBox.addItem(monthNames[i]);
		}

		localInitialize = false;
		comboBox.setSelectedIndex(month);
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			int index = comboBox.getSelectedIndex();

			if ((index >= 0) && (index != month)) {
				setMonth(index, false);
			}
		}
	}

	private void setMonth(int newMonth, boolean select) {
		if (!initialized || localInitialize) {
			return;
		}

		int oldMonth = month;
		month = newMonth;

		if (select) {
			comboBox.setSelectedIndex(month);
		}

		if (dayChooser != null) {
			dayChooser.setMonth(month);
		}

		firePropertyChange("month", oldMonth, month);
	}

	public void setMonth(int newMonth) {
		if (newMonth < 0 || newMonth > 11)
			return;
		setMonth(newMonth, true);
	}

	public void setDayChooser(DayPanel dayChooser) {
		this.dayChooser = dayChooser;
	}

}