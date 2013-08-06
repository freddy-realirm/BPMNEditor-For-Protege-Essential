package org.yaoqiang.bpmn.editor.dialog.date;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.dialog.SpinnerPanel;
import org.yaoqiang.util.Resources;


/**
 * CalendarPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class CalendarPanel extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	private Calendar calendar;

	protected DayPanel dayChooser;

	protected MonthPanel monthChooser;

	protected SpinnerPanel yearChooser;

	protected SpinnerPanel timeChooser;

	public CalendarPanel() {
		this(null, null);
	}

	public CalendarPanel(Date date, Locale locale) {
		dayChooser = null;
		monthChooser = null;
		yearChooser = null;
		timeChooser = null;

		calendar = Calendar.getInstance();

		setLayout(new BorderLayout());

		JPanel monthYearPanel = new JPanel();
		monthYearPanel.setLayout(new BoxLayout(monthYearPanel, BoxLayout.X_AXIS));

		monthChooser = new MonthPanel();
		yearChooser = new SpinnerPanel("year", 2011, 60);
		timeChooser = new SpinnerPanel("time", (int) new Date().getTime(), 85);
		JButton nowButton = new JButton(Resources.get("now"));
		nowButton.setMaximumSize(new Dimension(65, 27));
		nowButton.setPreferredSize(new Dimension(65, 27));
		nowButton.setToolTipText(Resources.get("now"));
		nowButton.addActionListener(new AbstractAction("now") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
				calendar.setTime(new Date());
				boolean storedMode = dayChooser.isAlwaysFireDayProperty();
				dayChooser.setAlwaysFireDayProperty(false);
				setCalendar(calendar);
				dayChooser.setAlwaysFireDayProperty(storedMode);
			}
		});

		JButton emptyButton = new JButton(Resources.get("empty"));
		emptyButton.setMaximumSize(new Dimension(65, 27));
		emptyButton.setPreferredSize(new Dimension(65, 27));
		emptyButton.setToolTipText(Resources.get("empty"));
		emptyButton.addActionListener(new AbstractAction("empty") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
				calendar.setTime(new Date(0));
				setCalendar(calendar);
			}
		});
		monthYearPanel.add(monthChooser);
		monthYearPanel.add(yearChooser);
		monthYearPanel.add(nowButton);
		monthYearPanel.add(emptyButton);
		monthYearPanel.add(timeChooser);
		monthYearPanel.add(Box.createHorizontalGlue());
		dayChooser = new DayPanel();
		dayChooser.addPropertyChangeListener(this);
		monthChooser.setDayChooser(dayChooser);
		monthChooser.addPropertyChangeListener(this);
		yearChooser.addPropertyChangeListener(this);
		timeChooser.addPropertyChangeListener(this);
		add(monthYearPanel, BorderLayout.NORTH);
		add(dayChooser, BorderLayout.CENTER);

		if (date != null) {
			calendar.setTime(date);
		}

		setCalendar(calendar);
	}

	public DayPanel getDayChooser() {
		return dayChooser;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (calendar != null) {
			Calendar c = (Calendar) calendar.clone();
			int value = 0;
			if (evt.getNewValue() instanceof Integer) {
				value = ((Integer) evt.getNewValue()).intValue();
			} else if (evt.getNewValue() instanceof Date) {
				if (evt.getPropertyName().equals("year")) {
					Calendar year = (Calendar) calendar.clone();
					year.setTime((Date) evt.getNewValue());
					value = year.get(Calendar.YEAR);
				}
			}
			if (evt.getPropertyName().equals("day")) {
				c.set(Calendar.DAY_OF_MONTH, value);
				setCalendar(c, false);
			} else if (evt.getPropertyName().equals("month")) {
				c.set(Calendar.MONTH, value);
				setCalendar(c, false);
			} else if (evt.getPropertyName().equals("year")) {
				getDayChooser().setYear(value);
				c.set(Calendar.YEAR, value);
				setCalendar(c, false);
			} else if (evt.getPropertyName().equals("time")) {
				int y = c.get(Calendar.YEAR);
				int m = c.get(Calendar.MONTH);
				int d = c.get(Calendar.DAY_OF_MONTH);
				c.setTime((Date) evt.getNewValue());
				c.set(Calendar.YEAR, y);
				c.set(Calendar.MONTH, m);
				c.set(Calendar.DAY_OF_MONTH, d);
				setCalendar(c, false);
			} else if (evt.getPropertyName().equals("date")) {
				c.setTime((Date) evt.getNewValue());
				setCalendar(c, true);
			}
		}
	}

	public void setCalendar(Calendar c) {
		setCalendar(c, true);
	}

	private void setCalendar(Calendar c, boolean update) {
		Calendar oldCalendar = calendar;
		calendar = c;

		if (update) {
			yearChooser.setValue(c.get(Calendar.YEAR));
			monthChooser.setMonth(c.get(Calendar.MONTH));
			dayChooser.setDay(c.get(Calendar.DATE));
		}

		firePropertyChange("calendar", oldCalendar, calendar);
	}

	public Date getDate() {
		return new Date(calendar.getTimeInMillis());
	}

	public void setDate(Date date) {
		Date oldDate = calendar.getTime();
		calendar.setTime(date);

		yearChooser.setValue(calendar.get(Calendar.YEAR));
		dayChooser.setYear(calendar.get(Calendar.YEAR));

		monthChooser.setMonth(calendar.get(Calendar.MONTH));
		dayChooser.setDay(calendar.get(Calendar.DATE));

		firePropertyChange("date", oldDate, date);
	}
}
