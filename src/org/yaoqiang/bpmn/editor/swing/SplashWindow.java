package org.yaoqiang.bpmn.editor.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

import org.yaoqiang.util.Constants;
import org.yaoqiang.util.Resources;


/**
 * SplashWindow
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class SplashWindow extends JWindow {

	private static final long serialVersionUID = 1L;

	private static JLabel logo = new JLabel(new ImageIcon(SplashWindow.class.getResource("/org/yaoqiang/bpmn/editor/images/yaoqiang_splash.png")));

	public SplashWindow() {
		add(logo);
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				hideSplash();
			}
		});
		pack();

		Dimension screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds().getSize();
		Dimension windowSize = getPreferredSize();
		if (windowSize.width > screenSize.width - 100) {
			windowSize.width = screenSize.width - 100;
		}
		if (windowSize.height > screenSize.height - 200) {
			windowSize.height = screenSize.height - 200;
		}
		setSize(windowSize);
		setLocation(screenSize.width / 2 - (windowSize.width / 2), screenSize.height / 2 - (windowSize.height / 2));

		setVisible(true);
	}

	public void hideSplash() {
		if (isVisible()) {
			setVisible(false);
			dispose();
		}
	}

	public static JPanel getSplashPanel() {
		Hyperactive ha = new Hyperactive();

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());

		JEditorPane text = new JEditorPane();
		text.setAlignmentX(CENTER_ALIGNMENT);
		text.setAlignmentY(TOP_ALIGNMENT);
		text.addHyperlinkListener(ha);
		text.setContentType("text/html");
		text.setOpaque(false);
		String version = Resources.get("version");
		String website = Resources.get("visitwebsite");
		String locstring = Constants.SETTINGS.getProperty("Locale", "en");
		if (locstring.equals("zh_CN") && !Constants.OS.startsWith("Windows")) {
			version = "Version";
			website="Visit <a href='http://bpmn.sf.net'>http://bpmn.sf.net</a> for more information.<br><br>QQ Group:630437<br><br>For commercial support please contact: <a href='mailto:shi_yaoqiang@yahoo.com'>shi_yaoqiang@yahoo.com</a>.";
		}
		String t = "<html><p align=center><b>" + version + ": " + Constants.VERSION + "</b><br><br>" + website + "<br><br></html>";
		text.setText(t);
		text.setEditable(false);

		p.add(logo, BorderLayout.NORTH);
		p.add(text, BorderLayout.CENTER);

		return p;
	}
}
