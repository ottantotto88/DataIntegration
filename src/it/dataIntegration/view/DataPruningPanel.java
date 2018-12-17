package it.dataIntegration.view;

import javax.swing.*;

import java.awt.BorderLayout;

import java.awt.FlowLayout;

@SuppressWarnings("serial")
public class DataPruningPanel extends JPanel {
	private JPanel panelBox;
	private JButton btnCercaConPruning;
	private JTextField txtUrl;
	private JTextField txtUrl2;
	private JScrollPane scrollPane;
	private JButton btnPulisci;
	private JPanel panelNorth4;
	private JRadioButton rdbtnProfondità;
	private JRadioButton rdbtnAmpiezza;
	private JPanel panelNorth3;
	private JLabel lblIterazioni;
	private JTextField txtIterazioni;
	private JLabel lblDescription;


    public DataPruningPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panelNorth = new JPanel();
		add(panelNorth, BorderLayout.NORTH);
		panelNorth.setLayout(new BoxLayout(panelNorth, BoxLayout.Y_AXIS));
		
		JPanel panelNorth1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelNorth1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panelNorth.add(panelNorth1);

		JLabel lblUrl = new JLabel("Url 1");
		panelNorth1.add(lblUrl);

		txtUrl = new JTextField();
		txtUrl.setText("https://www.mi-lorenteggio.com/2018/12/14/economia-circolare-cattaneo-si-stima-un-milione-di-posti-di-lavoro-in-piu-in-europa/73549/");
		txtUrl.setColumns(70);
		panelNorth1.add(txtUrl);

		
		
		JPanel panelNorth2 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panelNorth2.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panelNorth.add(panelNorth2);
		
		JLabel lblUrl2 = new JLabel("Url 2");
		panelNorth2.add(lblUrl2);
		
		txtUrl2 = new JTextField();
		txtUrl2.setText("http://www.qds.it/30153-economia-circolare-un-passo-in-avanti-per-ridurre-impatto-ambientale.htm");
		panelNorth2.add(txtUrl2);
		txtUrl2.setColumns(70);
		
		panelNorth3 = new JPanel();
		FlowLayout fl_panelNorth3 = (FlowLayout) panelNorth3.getLayout();
		fl_panelNorth3.setAlignment(FlowLayout.LEFT);
		panelNorth.add(panelNorth3);
		
		lblIterazioni = new JLabel("Iterazioni:");
		panelNorth3.add(lblIterazioni);
		
		txtIterazioni = new JTextField();
		txtIterazioni.setText("100");
		panelNorth3.add(txtIterazioni);
		txtIterazioni.setColumns(10);
		
		lblDescription = new JLabel("(Inserire il numero di iterazioni dopo il quale arrestare la ricerca)");
		panelNorth3.add(lblDescription);
		
		panelNorth4 = new JPanel();
		FlowLayout fl_panelNorth4 = (FlowLayout) panelNorth4.getLayout();
		fl_panelNorth4.setAlignment(FlowLayout.LEFT);
		panelNorth.add(panelNorth4);
		
		rdbtnProfondità = new JRadioButton("Ricerca in profodità");
		rdbtnProfondità.setSelected(true);
		panelNorth4.add(rdbtnProfondità);
		
		rdbtnAmpiezza = new JRadioButton("Ricerca in ampiezza");
		panelNorth4.add(rdbtnAmpiezza);
		
		ButtonGroup group = new ButtonGroup();
	    group.add(rdbtnProfondità);
	    group.add(rdbtnAmpiezza);
		
		scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panelBox= new JPanel();
		scrollPane.setViewportView(panelBox);
		panelBox.setLayout(new BoxLayout(panelBox, BoxLayout.Y_AXIS));
				
		JPanel panelSouth = new JPanel();
		add(panelSouth, BorderLayout.SOUTH);
		panelSouth.setLayout(new BoxLayout(panelSouth, BoxLayout.X_AXIS));

		btnCercaConPruning = new JButton("Cerca con Pruning ");
		panelSouth.add(btnCercaConPruning);
		
		btnPulisci = new JButton("Pulisci");
		btnPulisci.setEnabled(false);
		panelSouth.add(btnPulisci);
	}
	
	public JTextField getTxtIterazioni() {
		return txtIterazioni;
	}

	public JRadioButton getRdbtnProfondità() {
		return rdbtnProfondità;
	}

	public JRadioButton getRdbtnAmpiezza() {
		return rdbtnAmpiezza;
	}

	public JButton getBtnPulisci() {
		return btnPulisci;
	}

    public JButton getBtnCercaConPruning() { return btnCercaConPruning; }



    public JPanel getPanelBox() {
		return panelBox;
	}
	public JTextField getTxtUrl() {
		return txtUrl;
	}
	public JTextField getTxtUrl2() {
		return txtUrl2;
	}
	public JScrollPane getScrollPane() {
		return scrollPane;
	}
}
