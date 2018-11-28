package it.dataIntegration.view;

import it.dataIntegration.controller.CalcolatoreFrequenzaController;

import javax.swing.*;
import java.awt.*;

/**
 * View creata per comodità. Massimo 1000 estrazioni tramite dandelion al giorno.
 */
public class CalcolatoreFrequenzaView1 extends JFrame{
    private JPanel calcolatoreFrequenzaPanel;
    private JButton estraiTotButton;
    private CalcolatoreFrequenzaController calcolatoreFrequenzaController;

    public CalcolatoreFrequenzaView1(CalcolatoreFrequenzaController calcolatoreFrequenzaController) {
        this.calcolatoreFrequenzaController = calcolatoreFrequenzaController;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setBounds((int) screenSize.getWidth()/2,(int)screenSize.getHeight()/2,
                (int) screenSize.getWidth()/3,(int)screenSize.getHeight()/3);
        this.add(calcolatoreFrequenzaPanel);

    }

    public JButton getEstraiTotButton() {
        return estraiTotButton;
    }
}

