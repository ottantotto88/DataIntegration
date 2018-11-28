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

/**
 * L'arraylist di dbpedia object è usato per ospitare gli URI estratti tramite dandelion. In esso si trovano gli uri
 * necessari ad interrogare dbpedia inglese. Gli argomenti sono estratti da un file nella cartella root della
 * applicazione. Viene usata la libreria esterna Jsoup per il parsing della pagina html, vengono in particolare
 * estratti tutti i link per poi filtrare solo quelli relativi alle notizie. I link delle notizie non possono essere
 * consultati da jsoup in quanto operano un redirect in javascript alla pagina vera e propria. Per risolvere questo
 * problema viene effettuato un secondo parsing al fine di recuperare il vero link della notizia.
 */

public class CalcolatoreFrequenzaController {

    private CalcolatoreFrequenzaView1 calcolatoreFrequenzaView1;
    private CalcolatoreFrequenzaModel calcolatoreFrequenzaModel;
    private ArrayList<DbpediaObject> dbpediaObjects = new ArrayList<DbpediaObject>();
    private ArrayList<String> news = new ArrayList<>();
    private Document document;



    public CalcolatoreFrequenzaController() {

        this.calcolatoreFrequenzaView1 = new CalcolatoreFrequenzaView1(this);
        this.calcolatoreFrequenzaModel = new CalcolatoreFrequenzaModel(this);

        calcolatoreFrequenzaView1.setVisible(true);

        calcolatoreFrequenzaView1.getEstraiTotButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idArgomento;
                try {
                    FileReader fileReader = new FileReader("argomenti.txt");
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String argomento;

                    while ((argomento = bufferedReader.readLine()) != null) {
                        //viene restituito l id dell'argomento appena inserito come intero
                        idArgomento = calcolatoreFrequenzaModel.insertArgomento(argomento);
                        // il link a gnews in inglese è costruito riempiendo la parte mancante con l'argomento
                        document= Jsoup.connect("https://news.google.com/search?q="+ argomento +"&hl=en-US&gl=US&ceid=US%3Aen").get();
                        //sono estratti solo i link
                        Elements links = document.select("a[href]");
                        //filtraggio dei link relativi alle notizie, sono da ignorare link ad altri servizi
                        // come google play etc.
                        news = filtraNotizie(links);

                        //per qualche motivo i link sono ripetuti due volte nella pagina quindi delle coppie
                        // identiche se ne considera solo uno.
                        for (int i = 0; i < news.size() ; i = i + 2){
                            //risoluzione del redirect tramite secondo parsing
                            Document document1 =  Jsoup.connect(news.get(i)).get();
                            //nel parsing il link di interesse  è il 44esimo, soluzione hard coded non
                            // resiliente ai cambiamenti lato server
                            String link_vero = document1.select("a[href]").get(44).attr("abs:href");
                            dbpediaObjects = RestServices.getRequest(link_vero);
                            System.out.println(link_vero);
                            //qui sono inseriti tutti le properties trovato per ogni uri estratto dalla notizia
                            SparqlQuery.getPropertiesResultSet(dbpediaObjects, calcolatoreFrequenzaModel, idArgomento);
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
