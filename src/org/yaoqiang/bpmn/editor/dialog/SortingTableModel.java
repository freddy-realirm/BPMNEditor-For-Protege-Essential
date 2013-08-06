package org.yaoqiang.bpmn.editor.dialog;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * SortingTableModel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class SortingTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	public SortingTableModel(Vector<?> data, Vector<?> names) {
		super(data, names);
	}

	@SuppressWarnings("unchecked")
	public void sortByColumn(int col, boolean ascending) {
		Vector<Object> dv = getDataVector();
		Vector<Object> v = new Vector<Object>(dv);
		int vs = v.size();

		if (vs > 0) {
			Collections.sort(v, new ColumnSortingComparator(col, ascending));
			for (int i = 0; i < vs; i++) {
				dv.set(i, v.get(i));
			}
		}
	}

	public Class<?> getColumnClass(int c) {
		Object val = getValueAt(0, c);
		return val == null ? Object.class : val.getClass();
	}

	static class ColumnSortingComparator implements Comparator<Object> {

		protected int index;
		protected boolean ascending;

		public ColumnSortingComparator(int index, boolean ascending) {
			this.index = index;
			this.ascending = ascending;
		}

		@SuppressWarnings("unchecked")
		public int compare(Object first, Object second) {
			if (first instanceof Vector<?> && second instanceof Vector<?>) {
				if (((Vector<Object>) first).elementAt(index) instanceof Integer && ((Vector<Object>) second).elementAt(index) instanceof Integer) {
					int int1 = ((Integer) ((Vector<Object>) first).elementAt(index)).intValue();
					int int2 = ((Integer) ((Vector<Object>) second).elementAt(index)).intValue();
					if (ascending) {
						return int1 - int2;
					}
					return int2 - int1;
				} else {
					String str1 = ((Vector<Object>) first).elementAt(index).toString();
					String str2 = ((Vector<Object>) second).elementAt(index).toString();
					if (ascending) {
						return str1.compareTo(str2);
					}
					return str2.compareTo(str1);
				}
			}
			return 1;
		}

	}
}
