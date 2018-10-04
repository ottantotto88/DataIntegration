package it.dataIntegration.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;

public class FinishedElaborationPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private JPanel panelButton;
		private JButton btnOk	;

		public FinishedElaborationPanel(String message) {
			setLayout(new BorderLayout(0, 0));
			
			JPanel panel = new JPanel();
			
			panel.setAlignmentX(CENTER_ALIGNMENT);
			panel.setAlignmentY(CENTER_ALIGNMENT);
			add(panel, BorderLayout.CENTER);
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			
			JPanel panelAlto = new JPanel();
			panel.add(panelAlto);
			panelAlto.setLayout(new BoxLayout(panelAlto, BoxLayout.Y_AXIS));
			
			Component verticalStrut = Box.createVerticalStrut(30);
			panelAlto.add(verticalStrut);
			
			JPanel panelLbl = new JPanel();
			panelAlto.add(panelLbl);
			
			String[] lines = message.split("\n");
			for (String line : lines) {
			    JLabel label = new JLabel(line);
			    label.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
			    panelLbl.add(label);
			}
					
			panelButton = new JPanel();
			panel.add(panelButton);
			
			btnOk = new JButton("Ok");
			panelButton.add(btnOk);
		}

		public JButton getBtnOk() {
			return btnOk;
		}

		
}
