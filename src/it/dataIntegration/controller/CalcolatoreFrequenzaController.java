package it.dataIntegration.controller;

import it.dataIntegration.model.DbpediaObject;
import it.dataIntegration.utility.RestServices;
import it.dataIntegration.utility.SparqlQuery;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import it.dataIntegration.view.CalcolatoreFrequenzaView1;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class CalcolatoreFrequenzaController {

    private CalcolatoreFrequenzaView1 calcolatoreFrequenzaView1;
    private ArrayList<DbpediaObject> dbpediaObjects = new ArrayList<DbpediaObject>();
    private Document document;
    private String argomento;


    public CalcolatoreFrequenzaController() {

        this.calcolatoreFrequenzaView1 = new CalcolatoreFrequenzaView1(this);
        calcolatoreFrequenzaView1.setVisible(true);

        calcolatoreFrequenzaView1.getEstraiPropertiesButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String urlProva = calcolatoreFrequenzaView1.getUrlTextField1().getText();
                dbpediaObjects = RestServices.getRequest(urlProva);
                SparqlQuery.getProperties(dbpediaObjects);
            }
        });

        calcolatoreFrequenzaView1.getEstraiNotizieDaGnewsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String argomento = calcolatoreFrequenzaView1.getArgomentiTextField().getText();
                try {
                document= Jsoup.connect("https://news.google.com/search?q="+ argomento +"&hl=it&gl=IT&ceid=IT%3Ait").get();
                } catch (IOException exception) {
                //blocco di catch autogenerato per la gestione eccezione di jsoup
                exception.printStackTrace();
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
        });


    }
}
