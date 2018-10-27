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
    private ArrayList<DbpediaObject> dbpediaObjects = new ArrayList<DbpediaObject>();
    private Document document;
    private String argomento;


    public CalcolatoreFrequenzaController() {
//        calcolatoreFrequenzaView = new CalcolatoreFrequenzaView(this);
//        calcolatoreFrequenzaView.setVisible(true);
//
//        calcolatoreFrequenzaView.getjButton().addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                creaListaTermini(calcolatoreFrequenzaView.getUrl());
//
//            }
//        });

        argomento = new String("animali");

        try {
                document= Jsoup.connect("https://news.google.com/search?q="+ argomento +"&hl=it&gl=IT&ceid=IT%3Ait").get();
        } catch (IOException e) {
            //blocco di catch autogenerato per la gestione eccezione di jsoup
                e.printStackTrace();
        }

        Elements links = document.select("a[href]");

        System.out.println("\nExtracted keywords:");
        for (Element link : links) {
            String url = link.attr("abs:href");
            dbpediaObjects = RestServices.getRequest(url);
            for (DbpediaObject dbpediaObject : dbpediaObjects){
                System.out.println(dbpediaObject.getUriDbpedia());

            }

        }







    }

    public void creaListaTermini(String urlNotizia) {
        dbpediaObjects = RestServices.getRequest(urlNotizia);
        for (int i = 0; i < dbpediaObjects.size(); i ++){
            String keyword = dbpediaObjects.get(i).getNome();
            calcolatoreFrequenzaView.addElementToScroll(keyword);
            System.out.println(keyword);
        }
    }


}
