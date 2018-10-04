package it.dataIntegration.view;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Component;
import javax.swing.Box;

public class PleaseWaitPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public PleaseWaitPanel() {
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
		
		JLabel lblEInCorso = new JLabel("E' in corso la ricerca di match, attendere prego");
		panelLbl.add(lblEInCorso);
		lblEInCorso.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		
		JPanel panelProgressBar = new JPanel();
		panel.add(panelProgressBar);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		panelProgressBar.add(progressBar);
	}

}
