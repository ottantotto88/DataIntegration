package it.dataIntegration.controller;

import it.dataIntegration.model.DbpediaObject;
import it.dataIntegration.utility.RestServices;
import it.dataIntegration.utility.SparqlQuery;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import it.dataIntegration.view.CalcolatoreFrequenzaView1;
import org.apache.jena.base.Sys;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Connection.Response;

import javax.print.Doc;


public class CalcolatoreFrequenzaController {

    private CalcolatoreFrequenzaView1 calcolatoreFrequenzaView1;
    private ArrayList<DbpediaObject> dbpediaObjects = new ArrayList<DbpediaObject>();
    private ArrayList<String> news = new ArrayList<>();
    private Document document;
    private Response response;
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
                    Elements links = document.select("a[href]");

                    news = filtraNotizie(links);
                    FileWriter fileWriter = new FileWriter("link_notizie");
                    PrintWriter printWriter = new PrintWriter(fileWriter);

                    for (int i = 0; i < news.size() ; i = i + 2){
                        Document document1 =  Jsoup.connect(news.get(i)).get();
                        System.out.println(document1.select("a[href]").get(44).attr("abs:href"));
                        printWriter.println(document1.select("a[href]").get(44).attr("abs:href"));
                    }
                    printWriter.close();

                } catch (IOException exception) {
                //blocco di catch autogenerato per la gestione eccezione di jsoup
                    exception.printStackTrace();
                }

            }
        });


    }

    //questo metodo prende in ingresso i risultati del parser html, che carica tutti i link che trova nella pagina.
    //Ai fini della ricerca sono ovviamente interessanti tutti e soli i link inerenti alle notizie (si vogliono ignorare
    // i link ad esempioa google play o altri servizi). Vengono quindi filtrati e restituiti come una lista di stringhe.
    public ArrayList<String> filtraNotizie(Elements links){
        ArrayList<String> notizie = new ArrayList<>();

        for (Element link : links) {
            String url = link.attr("abs:href");
            if(url.matches("https://news.google.com/article(.*)"))
                notizie.add(url);
        }
        return notizie;
    }
}
