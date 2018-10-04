package it.dataIntegration;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;

import it.dataIntegration.controller.DataIntegrationController;
import it.dataIntegration.view.DataIntegrationPanel;

public class DataIntegration {
	
	private JFrame frame;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// Set cross-platform Java L&F
				 	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					DataIntegration window = new DataIntegration();
					window.frame.setVisible(true);
				} catch (Exception e) {
				}
			}
		});
	}

	//Create the application.
	 
	public DataIntegration() {
		initialize();
	}
	 
	private void initialize() {
						
		frame = new JFrame();
		frame.setTitle("Data Integration Tool");
		frame.setBounds(100, 100, 1100, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		DataIntegrationPanel panelLinkedData = new DataIntegrationPanel();
		frame.getContentPane().add(panelLinkedData);
		new DataIntegrationController(panelLinkedData,frame);
		
	}
}
