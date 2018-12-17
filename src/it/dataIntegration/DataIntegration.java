package it.dataIntegration;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;

import it.dataIntegration.controller.CalcolatoreFrequenzaController;
import it.dataIntegration.controller.DataIntegrationController;
import it.dataIntegration.view.DataIntegrationPanel;
import it.dataIntegration.view.DataPruningPanel;
import it.dataIntegration.view.OpeningForm;

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
		start();
	}


	private void start(){
		frame = new JFrame();
		frame.setTitle("Data Integration Tool");
		frame.setBounds(100,100,550,400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		DataIntegrationPanel panelLinkedData = new DataIntegrationPanel();
		DataPruningPanel panelPruningData = new DataPruningPanel();


		OpeningForm openingForm = new OpeningForm(panelLinkedData,panelPruningData,frame);
		frame.getContentPane().add(openingForm);
		new CalcolatoreFrequenzaController();

	}

}
