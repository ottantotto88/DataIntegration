package it.dataIntegration.view;

import it.dataIntegration.DataIntegration;
import it.dataIntegration.controller.CalcolatoreFrequenzaController;
import it.dataIntegration.controller.DataIntegrationController;
import it.dataIntegration.controller.DataPruningController;
import it.dataIntegration.utility.ProcessCpuLoad;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by giuli on 17/12/2018.
 */
public class OpeningForm extends JPanel{
    private JButton cercaConPruningButton;
    private JPanel panel1;
    private JButton cercaSenzaPruningButton;

    public OpeningForm(final DataIntegrationPanel dataIntegrationPanel, final DataPruningPanel dataPruningPanel, final JFrame frame){
        frame.add(panel1);
        frame.setVisible(true);

        cercaConPruningButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame pruningFrame =  new JFrame();
                pruningFrame.setTitle("Data Integration Tool");
                pruningFrame.setBounds(100, 100, 1100, 800);
                pruningFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                pruningFrame.getContentPane().add(dataPruningPanel);
                pruningFrame.setVisible(true);
                new DataPruningController(dataPruningPanel,pruningFrame);
            }
        });

        cercaSenzaPruningButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame nonPruningFrame =  new JFrame();

                nonPruningFrame.setTitle("Data Integration Tool");
                nonPruningFrame.setBounds(100, 100, 1100, 800);
                nonPruningFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                nonPruningFrame.getContentPane().add(dataIntegrationPanel);
                nonPruningFrame.setVisible(true);
                new DataIntegrationController(dataIntegrationPanel,nonPruningFrame);
            }
        });


    }


}
