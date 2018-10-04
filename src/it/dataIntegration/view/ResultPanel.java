package it.dataIntegration.view;

import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JTextArea;
import javax.swing.border.MatteBorder;
import java.awt.Color;

@SuppressWarnings("serial")
public class ResultPanel extends JPanel {
	private JTextArea textArea;
	public ResultPanel() {
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		textArea = new JTextArea();
		textArea.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		add(textArea);
	}
	public JTextArea getTextArea() {
		return textArea;
	}
}
