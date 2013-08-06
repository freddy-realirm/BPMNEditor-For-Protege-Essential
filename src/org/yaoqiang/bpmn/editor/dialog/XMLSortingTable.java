package org.yaoqiang.bpmn.editor.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 * XMLSortingTable
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class XMLSortingTable extends JTable {

	private static final long serialVersionUID = 1L;
	
	protected int sortedColIndex = -1;
	
	protected boolean ascending = true;

	protected XMLTablePanel owner;

	public XMLSortingTable(XMLTablePanel p, Vector<?> data, Vector<?> names) {
		super(new SortingTableModel(data, names));
		this.owner = p;
		
		setShowGrid(true);
		setGridColor(Color.GRAY);
		
		JTableHeader h = getTableHeader();
		h.setDefaultRenderer(new SHRenderer());
		MouseListener ml = new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				performSorting(event);
			}
		};
		h.addMouseListener(ml);

	}

	public int getSortedColumnIndex() {
		return sortedColIndex;
	}

	public boolean isSortedColumnAscending() {
		return ascending;
	}

	public void performSorting(MouseEvent event) {
		TableColumnModel colModel = getColumnModel();
		int index = colModel.getColumnIndexAtX(event.getX());
		int modelIndex = colModel.getColumn(index).getModelIndex();

		SortingTableModel model = (SortingTableModel) getModel();

		if (sortedColIndex == index && event != null) {
			ascending = !ascending;
		}
		sortedColIndex = index;

		model.sortByColumn(modelIndex, ascending);

		Object selEl = null;
		int sr = getSelectedRow();
		if (sr >= 0) {
			selEl = model.getValueAt(sr, 0);
		}
		if (selEl != null) {
			owner.setSelectedElement(selEl);
		}

		update(getGraphics());
		getTableHeader().update(getTableHeader().getGraphics());

	}

	static class SHRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;
		static Icon NONSORTED = null;
		static Icon ASCENDING = new ImageIcon(XMLSortingTable.class.getClassLoader().getResource("org/yaoqiang/bpmn/editor/images/arrowup.gif"));
		static Icon DESCENDING = new ImageIcon(XMLSortingTable.class.getClassLoader().getResource("org/yaoqiang/bpmn/editor/images/arrowdown.gif"));

		public SHRenderer() {
			setHorizontalTextPosition(LEFT);
			setHorizontalAlignment(CENTER);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
			boolean ascending = true;
			XMLSortingTable sortTable = (XMLSortingTable) table;
			int index = sortTable.getSortedColumnIndex();
			ascending = sortTable.isSortedColumnAscending();

			Icon icon = null;
			if (ascending) {
				icon = ASCENDING;
			} else {
				icon = DESCENDING;
			}

			if (col == index) {
				setIcon(icon);
			} else {
				setIcon(NONSORTED);
			}

			if (value == null) {
				setText("");
			} else {
				setText(value.toString());
			}

			setBorder(BorderFactory.createRaisedBevelBorder());
			return this;
		}
	}

}
