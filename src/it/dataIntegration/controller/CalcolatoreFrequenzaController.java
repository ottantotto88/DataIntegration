package it.dataIntegration.controller;

import it.dataIntegration.model.CalcolatoreFrequenzaModel;
import it.dataIntegration.model.DbpediaObject;
import it.dataIntegration.utility.RestServices;
import it.dataIntegration.utility.SparqlQuery;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.dataIntegration.view.CalcolatoreFrequenzaView1;
import org.apache.jena.base.Sys;
import org.apache.jena.query.QuerySolution;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Connection.Response;



import javax.print.Doc;


public class CalcolatoreFrequenzaController {

    private CalcolatoreFrequenzaView1 calcolatoreFrequenzaView1;
    private CalcolatoreFrequenzaModel calcolatoreFrequenzaModel;
    private ArrayList<DbpediaObject> dbpediaObjects = new ArrayList<DbpediaObject>();
    private ArrayList<String> news = new ArrayList<>();
    private Document document;
    private Response response;
    private String argomento;


    public CalcolatoreFrequenzaController() {

        this.calcolatoreFrequenzaView1 = new CalcolatoreFrequenzaView1(this);
        this.calcolatoreFrequenzaModel = new CalcolatoreFrequenzaModel(this);

        calcolatoreFrequenzaView1.setVisible(true);


        // per estrarre le properties viene prima creato un vettore di dbpedia objects, che contengono nomi e uri per
        // dbpedia, successivamente viene effettuata una chiamata statica del metodo getproperties che crea il file con
        // properties e values.
        calcolatoreFrequenzaView1.getEstraiPropertiesButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String urlProva = calcolatoreFrequenzaView1.getUrlTextField1().getText();
                dbpediaObjects = RestServices.getRequest(urlProva);
                SparqlQuery.getPropertiesFile(dbpediaObjects);
            }
        });

        calcolatoreFrequenzaView1.getEstraiNotizieDaGnewsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                org.apache.jena.query.ResultSet resultSet;
                String argomento = calcolatoreFrequenzaView1.getArgomentiTextField().getText();
                try {
                    document= Jsoup.connect("https://news.google.com/search?q="+ argomento +"&hl=en-US&gl=US&ceid=US%3Aen").get();
                    Elements links = document.select("a[href]");

                    news = filtraNotizie(links);
                    FileWriter fileWriter = new FileWriter("link_notizie");
                    PrintWriter printWriter = new PrintWriter(fileWriter);
                    System.out.println(news.size());

                    for (int i = 0; i < news.size() ; i = i + 2){
                        Document document1 =  Jsoup.connect(news.get(i)).get();
                        String link_vero = document1.select("a[href]").get(44).attr("abs:href");
                        dbpediaObjects = RestServices.getRequest(link_vero);
                        SparqlQuery.getPropertiesResultSet(dbpediaObjects, calcolatoreFrequenzaModel, 0);

                    }
                    System.out.println(news.size()/2);
                    printWriter.close();

                } catch (IOException exception){
                //blocco di catch autogenerato per la gestione eccezione di jsoup
                    exception.printStackTrace();
                }

            }
        });

        calcolatoreFrequenzaView1.getEstraiTotButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idArgomento;
                try {
                    FileReader fileReader = new FileReader("argomenti.txt");
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String argomento;

                    while ((argomento = bufferedReader.readLine()) != null) {
                        idArgomento = calcolatoreFrequenzaModel.insertArgomento(argomento);
                        document= Jsoup.connect("https://news.google.com/search?q="+ argomento +"&hl=en-US&gl=US&ceid=US%3Aen").get();
                        Elements links = document.select("a[href]");
                        news = filtraNotizie(links);


                        for (int i = 0; i < news.size() ; i = i + 2){
                            Document document1 =  Jsoup.connect(news.get(i)).get();
                            String link_vero = document1.select("a[href]").get(44).attr("abs:href");
                            dbpediaObjects = RestServices.getRequest(link_vero);
                            System.out.println(link_vero);
                            //SparqlQuery.getPropertiesResultSet(dbpediaObjects, calcolatoreFrequenzaModel, idArgomento);
                        }
                    }
                } catch (IOException | SQLException e1) {
                    e1.printStackTrace();
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
