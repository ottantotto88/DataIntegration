package it.dataIntegration.view;

import it.dataIntegration.controller.CalcolatoreFrequenzaController;

import javax.swing.*;
import java.awt.*;

public class CalcolatoreFrequenzaView1 extends JFrame{
    private JButton estraiPropertiesButton;
    private JButton estraiNotizieDaGnewsButton;
    private JTextField argomentiTextField;
    private JTextField urlTextField1;
    private JPanel calcolatoreFrequenzaPanel;
    private CalcolatoreFrequenzaController calcolatoreFrequenzaController;


    public CalcolatoreFrequenzaView1(CalcolatoreFrequenzaController calcolatoreFrequenzaController) {
        this.calcolatoreFrequenzaController = calcolatoreFrequenzaController;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setBounds((int) screenSize.getWidth()/2,(int)screenSize.getHeight()/2,
                (int) screenSize.getWidth()/3,(int)screenSize.getHeight()/3);
        this.add(calcolatoreFrequenzaPanel);

    }


    public JButton getEstraiNotizieDaGnewsButton() {
        return estraiNotizieDaGnewsButton;
    }

    public JButton getEstraiPropertiesButton() {
        return estraiPropertiesButton;
    }


    public JTextField getUrlTextField1() {
        return urlTextField1;
    }

    public JTextField getArgomentiTextField() {
        return argomentiTextField;
    }
}

