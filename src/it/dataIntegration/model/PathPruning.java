package it.dataIntegration.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/*
 * questa classe modella un path all'interno di un grafo
 */
public class PathPruning {
    private String subject;
    private String predicate;
    private String object;


    public PathPruning(String subject, String predicate, String object) {
        super();
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;

    }

    public String getSubject() {
        return subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public String getObject() {
        return object;
    }


    @Override
    public String toString() {
        return this.subject + " -> " + this.predicate + " -> " + this.object;
    }

    // questo metodo restituisce una stringa che riporta un percorso all'iterno di un grafo

    public static String writePath(ArrayList<PathPruning> paths) {
        PathPruning path = paths.get(0);
        int peso = paths.size();
        String fine = "null";
        String percorso = "(s) " + path.subject + " -> " + "(p) " + path.predicate + " -> " + "(o) " + path.object;
        if (path.predicate.equals("http://www.w3.org/2002/07/owl#sameAs")) {
                peso--;
        }
        for (int i = 1; i < paths.size(); i++) {
            if (paths.get(i).object.equals(paths.get(i - 1).object)) {
                percorso = percorso.concat(" (o) <- " + "(p) " + paths.get(i).predicate + " <- " + "(s) " + paths.get(i).subject + " \n");
                if(paths.get(i).predicate.equals("http://www.w3.org/2002/07/owl#sameAs")){
                    peso--;
                }
                fine = paths.get(i).subject;
            } else if (paths.get(i).subject.equals(paths.get(i - 1).object)) {
                percorso = percorso.concat(" (s) -> " + "(p) " + paths.get(i).predicate + " -> " + "(o) " + paths.get(i).object + " ");
                if(paths.get(i).predicate.equals("http://www.w3.org/2002/07/owl#sameAs")){
                    peso--;
                }
                fine = paths.get(i).subject;

            } else {
                percorso = percorso.concat(" (s) -> " + "(p) " + paths.get(i).predicate + " -> " + "(o) " + paths.get(i).object);
                if(paths.get(i).predicate.equals("http://www.w3.org/2002/07/owl#sameAs")){
                    peso--;
                }
                fine = paths.get(i).subject;
            }

        }

        String inizio = paths.get(0).subject;
        writeCSV(inizio, fine, peso, paths.size());

        return percorso + "\n La lunghezza del percorso è "+ paths.size() + ". Il peso del percorso è " + peso;
    }


    public static void writeCSV(String inizio, String fine, int peso, int lunghezza){
        File file = new File("src/output.csv");
        FileWriter fr = null;
        String text = "\n" + inizio + ";"+ fine + ";"+ peso + ";" + lunghezza;
        try {
            fr = new FileWriter(file, true);
            fr.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }




}


