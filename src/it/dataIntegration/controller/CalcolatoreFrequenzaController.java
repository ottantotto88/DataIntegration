package it.dataIntegration.controller;

import it.dataIntegration.model.DbpediaObject;
import it.dataIntegration.utility.RestServices;
import it.dataIntegration.view.CalcolatoreFrequenzaView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class CalcolatoreFrequenzaController {

    private CalcolatoreFrequenzaView calcolatoreFrequenzaView;
    private ArrayList<DbpediaObject> list = new ArrayList<DbpediaObject>();
    private Document document;
    private String argomento;


    public CalcolatoreFrequenzaController() {
        calcolatoreFrequenzaView = new CalcolatoreFrequenzaView(this);
        calcolatoreFrequenzaView.setVisible(true);

        calcolatoreFrequenzaView.getjButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                creaListaTermini(calcolatoreFrequenzaView.getUrl());

            }
        });

        argomento = new String("animali");

        try {
                document= Jsoup.connect("https://news.google.com/search?q="+ argomento +"&hl=it&gl=IT&ceid=IT%3Ait").get();
        } catch (IOException e) {
            //blocco di catch autogenerato per la gestione eccezione di jsoup
                e.printStackTrace();
        }

        Elements links = document.select("a[href]");

        System.out.println("\nLinks:");
        for (Element link : links) {

            System.out.println(link.attr("abs:href"));
        }







    }

    public void creaListaTermini(String urlNotizia) {
        list = RestServices.getRequest(urlNotizia);
        for (int i = 0; i < list.size(); i ++){
            String keyword = list.get(i).getNome();
            calcolatoreFrequenzaView.addElementToScroll(keyword);
            System.out.println(keyword);
        }
    }


}
