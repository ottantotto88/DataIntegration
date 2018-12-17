package it.dataIntegration.model;

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
        String percorso = "(s) " + path.subject + " -> " + "(p) " + path.predicate + " -> " + "(o) " + path.object;
        for (int i = 1; i < paths.size(); i++) {
            if (paths.size() == 1) {
                percorso = "(s) " + path.subject + " -> " + "(p) " + path.predicate + " -> " + "(o) " + path.object + " -> " + " \n " +
                        "La lunghezza del percorso è 1";
            } else if (paths.get(i).object.equals(paths.get(i - 1).object)) {
                percorso = percorso.concat(" (o) <- " + "(p) " + paths.get(i).predicate + " <- " + "(s) " + paths.get(i).subject + " \n");
            } else if (paths.get(i).subject.equals(paths.get(i - 1).object)) {
                percorso = percorso.concat(" (s) -> " + "(p) " + paths.get(i).predicate + " -> " + "(o) " + paths.get(i).object + " ");
            } else {
                percorso = percorso.concat(" (s) -> " + "(p) " + paths.get(i).predicate + " -> " + "(o) " + paths.get(i).object);
            }

        }


        if (path.getPredicate().equals("http://www.w3.org/2002/07/owl#sameAs")){
            int peso =0;
            return percorso.concat(". La lunghezza del percorso è " + paths.size() + ". Il peso del percorso è " + peso);
        }

        else {
            for (int i = 1; i < paths.size(); i++)
                if (!paths.get(i).predicate.equals("http://www.w3.org/2002/07/owl#sameAs") && (!paths.get(i - 1).predicate.equals("http://www.w3.org/2002/07/owl#sameAs") ))
                    return percorso.concat(". La lunghezza del percorso è " + paths.size() + ". Il peso del percorso è " + paths.size());
        }

        int peso = paths.size();
        return percorso.concat(". La lunghezza del percorso è " + paths.size() + ". Il peso del percorso è" + peso);
    }
}
