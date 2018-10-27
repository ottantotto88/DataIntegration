package it.dataIntegration.view;

import it.dataIntegration.controller.CalcolatoreFrequenzaController;

import javax.swing.*;
import java.awt.*;

public class CalcolatoreFrequenzaView extends JFrame {
    private CalcolatoreFrequenzaController calcolatoreFrequenzaController;
    private JScrollPane jScrollPane;
    private JPanel listPane;
    private JTextField jTextField;
    private JButton jButton;


    public CalcolatoreFrequenzaView(CalcolatoreFrequenzaController calcolatoreFrequenzaController) {
        this.calcolatoreFrequenzaController = calcolatoreFrequenzaController;

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle("Calcolatore di frequenza");


        this.listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));

        this.jScrollPane = new JScrollPane(listPane);
        this.jTextField = new JTextField();
        jTextField.setText("https://sport.ilmessaggero.it/motorsport/moto_gp_valentino_rossi_il_circuito_austria_mai_molto_positivo_per_noi-3903171.html");
        this.jButton = new JButton("estrai");


        getContentPane().add(jTextField,BorderLayout.PAGE_START);
        getContentPane().add(jButton,BorderLayout.LINE_END);
        getContentPane().add(jScrollPane, BorderLayout.CENTER);


        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(screenSize.width/2, screenSize.height/2, screenSize.width/3,screenSize.height/3);

    }

    public JButton getjButton() {
        return jButton;
    }

    public String getUrl(){
        return jTextField.getText();
    }

    public void addElementToScroll(String string){
        listPane.add(new Label(string));
    }





}
