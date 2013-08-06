package org.yaoqiang.bpmn.editor.dialog.date;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.BPMNEditor;

/**
 * DayPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class DayPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	protected JButton[] days;
	protected JButton[] weeks;
	protected JButton selectedDay;
	protected JPanel weekPanel;
	protected JPanel dayPanel;
	protected int day;
	protected Color oldDayBackgroundColor;
	protected Color selectedColor;
	protected Color sundayForeground;
	protected Color weekdayForeground;
	protected Color decorationBackgroundColor;
	protected String[] dayNames;
	protected Calendar calendar;
	protected Calendar today;
	protected Locale locale = BPMNEditor.locale;
	protected boolean initialized;
	protected boolean decorationBackgroundVisible = true;
	protected boolean decorationBordersVisible;
	private boolean alwaysFireDayProperty = true;

	public DayPanel() {
		setBackground(Color.blue);
		
		days = new JButton[49];

		selectedDay = null;
		calendar = Calendar.getInstance(locale);
		today = (Calendar) calendar.clone();

		setLayout(new BorderLayout());

		dayPanel = new JPanel();
		dayPanel.setLayout(new GridLayout(7, 7));

		sundayForeground = new Color(164, 0, 0);
		weekdayForeground = new Color(0, 90, 164);

		// decorationBackgroundColor = new Color(194, 211, 252);
		// decorationBackgroundColor = new Color(206, 219, 246);
		decorationBackgroundColor = new Color(210, 228, 238);

		for (int y = 0; y < 7; y++) {
			for (int x = 0; x < 7; x++) {
				int index = x + (7 * y);

				if (y == 0) {
					days[index] = new JButton() {
						private static final long serialVersionUID = 1L;

						public boolean isFocusable() {
							return false;
						}
					};

					days[index].setContentAreaFilled(decorationBackgroundVisible);
					days[index].setBorderPainted(decorationBordersVisible);
					days[index].setBackground(decorationBackgroundColor);
				} else {
					days[index] = new JButton("x");
					days[index].addActionListener(this);
				}

				days[index].setMargin(new Insets(0, 0, 0, 0));
				dayPanel.add(days[index]);
			}
		}

		weekPanel = new JPanel();
		weekPanel.setLayout(new GridLayout(7, 1));
		weeks = new JButton[7];

		for (int i = 0; i < 7; i++) {
			weeks[i] = new JButton() {
				private static final long serialVersionUID = 1L;

				public boolean isFocusable() {
					return false;
				}
			};
			weeks[i].setMargin(new Insets(0, 0, 0, 0));
			weeks[i].setFocusPainted(false);
			weeks[i].setBackground(decorationBackgroundColor);
			weeks[i].setForeground(new Color(100, 100, 100));
			weeks[i].setContentAreaFilled(decorationBackgroundVisible);
			weeks[i].setBorderPainted(decorationBordersVisible);

			if (i != 0) {
				weeks[i].setText("0" + (i + 1));
			}

			weekPanel.add(weeks[i]);
		}

		init();
		drawWeeks();

		setDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
		add(dayPanel, BorderLayout.CENTER);
		add(weekPanel, BorderLayout.WEST);

		initialized = true;
	}

	protected void init() {
		JButton testButton = new JButton();
		oldDayBackgroundColor = testButton.getBackground();
		selectedColor = new Color(160, 160, 160);

		Date date = calendar.getTime();
		calendar = Calendar.getInstance(locale);
		calendar.setTime(date);

		drawDayNames();
		drawDays();
	}

	private void drawDayNames() {
		int firstDayOfWeek = calendar.getFirstDayOfWeek();
		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
		dayNames = dateFormatSymbols.getShortWeekdays();

		int day = firstDayOfWeek;

		for (int i = 0; i < 7; i++) {
			days[i].setText(dayNames[day]);

			if (day == 1) {
				days[i].setForeground(sundayForeground);
			} else {
				days[i].setForeground(weekdayForeground);
			}

			if (day < 7) {
				day++;
			} else {
				day -= 6;
			}
		}
	}

	protected void drawWeeks() {
		Calendar tmpCalendar = (Calendar) calendar.clone();

		for (int i = 1; i < 7; i++) {
			tmpCalendar.set(Calendar.DAY_OF_MONTH, (i * 7) - 6);

			int week = tmpCalendar.get(Calendar.WEEK_OF_YEAR);
			String buttonText = Integer.toString(week);

			if (week < 10) {
				buttonText = "0" + buttonText;
			}

			weeks[i].setText(buttonText);

			if ((i == 5) || (i == 6)) {
				weeks[i].setVisible(days[i * 7].isVisible());
			}
		}
	}

	protected void drawDays() {
		Calendar tmpCalendar = (Calendar) calendar.clone();
		int firstDayOfWeek = tmpCalendar.getFirstDayOfWeek();
		tmpCalendar.set(Calendar.DAY_OF_MONTH, 1);

		int firstDay = tmpCalendar.get(Calendar.DAY_OF_WEEK) - firstDayOfWeek;

		if (firstDay < 0) {
			firstDay += 7;
		}

		int i;

		for (i = 0; i < firstDay; i++) {
			days[i + 7].setVisible(false);
			days[i + 7].setText("");
		}

		tmpCalendar.add(Calendar.MONTH, 1);

		Date firstDayInNextMonth = tmpCalendar.getTime();
		tmpCalendar.add(Calendar.MONTH, -1);

		Date day = tmpCalendar.getTime();
		int n = 0;
		Color foregroundColor = getForeground();

		while (day.before(firstDayInNextMonth)) {
			days[i + n + 7].setText(Integer.toString(n + 1));
			days[i + n + 7].setVisible(true);

			if ((tmpCalendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) && (tmpCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR))) {
				days[i + n + 7].setForeground(sundayForeground);
			} else {
				days[i + n + 7].setForeground(foregroundColor);
			}

			if ((n + 1) == this.day) {
				days[i + n + 7].setBackground(selectedColor);
				selectedDay = days[i + n + 7];
			} else {
				days[i + n + 7].setBackground(oldDayBackgroundColor);
			}

			n++;
			tmpCalendar.add(Calendar.DATE, 1);
			day = tmpCalendar.getTime();
		}

		for (int k = n + i + 7; k < 49; k++) {
			days[k].setVisible(false);
			days[k].setText("");
		}
	}

	public void setDay(int d) {
		if (d < 1) {
			d = 1;
		}

		Calendar tmpCalendar = (Calendar) calendar.clone();
		tmpCalendar.set(Calendar.DAY_OF_MONTH, 1);
		tmpCalendar.add(Calendar.MONTH, 1);
		tmpCalendar.add(Calendar.DATE, -1);

		int maxDaysInMonth = tmpCalendar.get(Calendar.DATE);

		if (d > maxDaysInMonth) {
			d = maxDaysInMonth;
		}

		int oldDay = day;
		day = d;

		if (selectedDay != null) {
			selectedDay.setBackground(oldDayBackgroundColor);
			selectedDay.repaint();
		}

		for (int i = 7; i < 49; i++) {
			if (days[i].getText().equals(Integer.toString(day))) {
				selectedDay = days[i];
				selectedDay.setBackground(selectedColor);

				break;
			}
		}

		if (alwaysFireDayProperty) {
			firePropertyChange("day", 0, day);
		} else {
			firePropertyChange("day", oldDay, day);
		}
	}

	public void setMonth(int month) {
		calendar.set(Calendar.MONTH, month);

		boolean storedMode = alwaysFireDayProperty;
		alwaysFireDayProperty = false;
		setDay(day);
		alwaysFireDayProperty = storedMode;

		drawDays();
		drawWeeks();
	}

	public void setYear(int year) {
		calendar.set(Calendar.YEAR, year);
		drawDays();
		drawWeeks();
	}

	public void actionPerformed(ActionEvent e) {
		JButton button = (JButton) e.getSource();
		String buttonText = button.getText();
		int day = new Integer(buttonText).intValue();
		setDay(day);
	}

	public final boolean isAlwaysFireDayProperty() {
		return alwaysFireDayProperty;
	}

	public final void setAlwaysFireDayProperty(boolean alwaysFireDayProperty) {
		this.alwaysFireDayProperty = alwaysFireDayProperty;
	}

}
