package it.dataIntegration.model;

import java.util.ArrayList;

/*
 * questa classe modella un path all'interno di un grafo
 */
public class Path {
	private String subject;
	private String predicate;
	private String object;


	public Path(String subject, String predicate, String object) {
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
		return this.subject +" -> "+ this.predicate + " -> " + this.object;
	}
	
	// questo metodo restituisce una stringa che riporta un percorso all'iterno di un grafo
	
	public static String writePath(ArrayList<Path> paths) {
		Path path = paths.get(0);
		String percorso ="(s) " + path.subject + " -> " +"(p) " + path.predicate + " -> " + "(o) " + path.object +  " -> " + "(end) ";
		for(int i=0; i<paths.size(); i++) {
			if(paths.size()==1){
				percorso = percorso.concat(" (o) <- " + "(p) " + paths.get(i).predicate + " <- " + "(s) " + paths.get(i).subject + " " );
			}else if(paths.get(i).object.equals(paths.get(i-1).object)) {
				percorso = percorso.concat(" (o) <- " + "(p) " + paths.get(i).predicate + " <- " + "(s) " + paths.get(i).subject + " " );
			} else if(paths.get(i).subject.equals(paths.get(i-1).object)) {
				percorso = percorso.concat(" (s) -> " + "(p) " + paths.get(i).predicate + " -> " + "(o) " + paths.get(i).object + " " );
			} else {
				percorso = percorso.concat(" (s) -> " + "(p) " + paths.get(i).predicate + " -> " + "(o) " + paths.get(i).object);
			}



		}
		return percorso;
	}

}
