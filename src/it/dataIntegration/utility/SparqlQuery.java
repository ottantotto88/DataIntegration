package it.dataIntegration.utility;

import it.dataIntegration.model.DbpediaObject;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import java.util.ArrayList;

public class SparqlQuery {

	/*
	 * Il metodo seguente esegue una query Sparql su dbpedia(italiano) per ricercare
	 * tutte triple che hanno come soggetto un certo Uri ( estratto da una certa
	 * news) e tutte quelle che hanno lo stesso Uri come oggetto
	 */
	public static Model QuerySparql(ArrayList<DbpediaObject> list) {
		Model model = ModelFactory.createDefaultModel();
		for (int i = 0; i < list.size(); i++) {
			// Definizione della query Sparql
			String service = "http://it.dbpedia.org/sparql";
			String queryString = "Select ?s ?p ?o where" + "{" + "{" + "?s ?p ?o. " + "FILTER (?s = <"
					+ list.get(i).getUriDbpedia() + ">)." + "}" + "UNION" + "{" + "?s ?p ?o. " + "FILTER (?o = <"
					+ list.get(i).getUriDbpedia() + ">)." + "}" + "} LIMIT 20";
			Query query = QueryFactory.create(queryString);

			// Definizione del grafo che conterrà le triple estratte
			// Esecuzione della Query
			try (QueryExecution qexec = QueryExecutionFactory.sparqlService(service, query);) {
				ResultSet resultSet = qexec.execSelect();
				for (; resultSet.hasNext();) {
					QuerySolution solution = resultSet.nextSolution();
					// definisco namespace e localname di ciascuna propietà
					String namespace = solution.getResource("p").getNameSpace();
					String localName = solution.getResource("p").getLocalName();
					// faccio un check sull'oggetto, al fine di verificare che sia una risorsa
					// oppure un Literal
					if (solution.get("o") instanceof Resource) {
						// controllo se il subjecte estratto è già presente nel modello
						if (model.containsResource(solution.getResource("s"))) {
							model.getResource(solution.getResource("s").toString()).addProperty(
									model.createProperty(namespace, localName),
									// se è una risorsa il valore della propietà viena definito creando una nuova
									// risorsa
									model.createResource(solution.getResource("o").toString()));
						} else {
							// se la risorsa non è presente nel modello la creo
							model.createResource(solution.getResource("s").toString()).addProperty(
									model.createProperty(namespace, localName),
									model.createResource(solution.getResource("o").toString()));
						}
					} else {
						// se l'object estratto è un Literal il valore della propietà viene definito per
						// mezzo di una Stringa
						if (model.containsResource(solution.getResource("s"))) {
							int length = solution.get("o").toString().length();
							model.getResource(solution.getResource("s").toString()).addProperty(
									model.createProperty(namespace, localName),
									solution.get("o").toString().substring(0, length - 3));
						} else {
							int length = solution.get("o").toString().length();
							model.createResource(solution.getResource("s").toString()).addProperty(
									model.createProperty(namespace, localName),
									solution.get("o").toString().substring(0, length - 3));
						}
					}
				}
				System.out.println(model);
				qexec.close();
			}
		}
		return model;

	}

	/*
	 * il metodo è analogo al precedente con la differenza che gestisce il caso in
	 * cui il Subject fornito per la ricerca sia un literal
	 */
	public static Model QuerySparql(String subject, boolean literal) {
		Model model = ModelFactory.createDefaultModel();
		// Definizione della query Sparq
		subject = subject.replace("\\\\", "\\");
		String service = "http://it.dbpedia.org/sparql";
		String queryString;

		if (literal) {
			queryString = "Select ?s ?p ?o where" + "{" + "{" + "?s ?p ?o. " + "FILTER (?s = \"" + subject + "\"@it)."
					+ "}" + "UNION" + "{" + "?s ?p ?o. " + "FILTER (?o = \"" + subject + "\"@it)." + "}" + "} LIMIT 20";
		} else {
			queryString = "Select ?s ?p ?o where" + "{" + "{" + "?s ?p ?o. " + "FILTER (?s = <" + subject + ">)." + "}"
					+ "UNION" + "{" + "?s ?p ?o. " + "FILTER (?o = <" + subject + ">)." + "}" + "} LIMIT 20";
		}
		Query query = QueryFactory.create(queryString);

		// Definizione del grafo che conterrà le triple estratte
		// Esecuzione della Query
		try (QueryExecution qexec = QueryExecutionFactory.sparqlService(service, query);) {
			ResultSet resultSet = qexec.execSelect();
			for (; resultSet.hasNext();) {
				QuerySolution solution = resultSet.nextSolution();
				// definisco namespace e localname di ciascuna propietà
				String namespace = solution.getResource("p").getNameSpace();
				String localName = solution.getResource("p").getLocalName();
				// faccio un check sull'oggetto, al fine di verificare che sia una risorsa
				// oppure un Literal
				if (solution.get("o") instanceof Resource) {
					// controllo se il subjecte estratto è già presente nel modello
					if (model.containsResource(solution.getResource("s"))) {
						model.getResource(solution.getResource("s").toString()).addProperty(
								model.createProperty(namespace, localName),
								// se è una risorsa il valore della propietà viena definito creando una nuova
								// risorsa
								model.createResource(solution.getResource("o").toString()));
					} else {
						// se la risorsa non è presente nel modello la creo
						model.createResource(solution.getResource("s").toString()).addProperty(
								model.createProperty(namespace, localName),
								model.createResource(solution.getResource("o").toString()));
					}
				} else {
					// se l'object estratto è un Literal il valore della propietà viene definito per
					// mezzo di una Stringa
					if (model.containsResource(solution.getResource("s"))) {
						int length = solution.get("o").toString().length();
						model.getResource(solution.getResource("s").toString()).addProperty(
								model.createProperty(namespace, localName),
								solution.get("o").toString().substring(0, length - 3));
					} else {
						int length = solution.get("o").toString().length();
						model.createResource(solution.getResource("s").toString()).addProperty(
								model.createProperty(namespace, localName),
								solution.get("o").toString().substring(0, length - 3));
					}
				}
			}

			qexec.close();
		}
		return model;
	}
}
