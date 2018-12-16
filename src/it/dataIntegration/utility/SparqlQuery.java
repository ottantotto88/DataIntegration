package it.dataIntegration.utility;

import it.dataIntegration.model.CalcolatoreFrequenzaModel;
import it.dataIntegration.model.DbpediaObject;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import java.io.FileWriter;
import java.io.PrintWriter;
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
			org.apache.jena.query.Query query = QueryFactory.create(queryString);

			// Definizione del grafo che conterrà le triple estratte
			// Esecuzione della Query
			try (QueryExecution qexec = QueryExecutionFactory.sparqlService(service, query);) {
				org.apache.jena.query.ResultSet resultSet = qexec.execSelect();
				for (; resultSet.hasNext(); ) {
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
		org.apache.jena.query.Query query = QueryFactory.create(queryString);

		// Definizione del grafo che conterrà le triple estratte
		// Esecuzione della Query
		try (QueryExecution qexec = QueryExecutionFactory.sparqlService(service, query);) {
			org.apache.jena.query.ResultSet resultSet = qexec.execSelect();
			for (; resultSet.hasNext(); ) {
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





	public static void getPropertiesFile(ArrayList<DbpediaObject> dbpediaObjects) {
		String service = "http://dbpedia.org/sparql";
		for (DbpediaObject dbpediaObject : dbpediaObjects) {
			String queryString = new String("SELECT ?p ?o " +
					"WHERE{ " +
					" ?s ?p ?o. FILTER ( ?s = <" +
					dbpediaObject.getUriDbpedia() +
 					">). } LIMIT 20"); //restituisce il rs che ha properties e values
			org.apache.jena.query.Query query = QueryFactory.create(queryString);

			try (QueryExecution qexec = QueryExecutionFactory.sparqlService(service, query);) {
				org.apache.jena.query.ResultSet resultSet = qexec.execSelect();


				FileWriter fileWriter = new FileWriter("estrazione_prop", true);
				PrintWriter printWriter = new PrintWriter(fileWriter);
				while (resultSet.hasNext()) {
					QuerySolution solution = resultSet.nextSolution();
					printWriter.println("p: " + solution.get("p").toString() + " o: " + solution.get("o").toString());
				}
				printWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}


	}

    // questo metodo prende in ingresso un vettore  di dbpedia object da cui estrarre l'uri per costruire la
	// query da eseguire sul  servizio sparql inglese. Sono restituiti sottoforma di resultset properties
	// e values ottenute da dbpedia inglese. Il riferimento al model è passato al fine di poter operare
	// gli inserimenti nel db.
	public static void getPropertiesResultSet(ArrayList<DbpediaObject> dbpediaObjects, CalcolatoreFrequenzaModel calcolatoreFrequenzaModel, int idArgomento) {
		String service = "https://dbpedia.org/sparql"; //possiamo fare prove delle query qui

		for (DbpediaObject dbpediaObject : dbpediaObjects) {
			String queryString = new String("SELECT ?p ?o " +
					"WHERE{ " +
					" ?s ?p ?o. FILTER ( ?s = <" +
					dbpediaObject.getUriDbpedia() +
					">). } LIMIT 20");
			System.out.println(queryString);
			org.apache.jena.query.Query query = QueryFactory.create(queryString);

			try (QueryExecution qexec = QueryExecutionFactory.sparqlService(service, query);) {
				org.apache.jena.query.ResultSet resultSet = qexec.execSelect();
				while (resultSet.hasNext()) {
					QuerySolution solution = resultSet.nextSolution();
					calcolatoreFrequenzaModel.insertProperty(idArgomento,solution.get("p").toString(), solution.get("o").toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}



    // questo metodo prende in ingresso un vettore  di dbpedia object da cui estrarre l'uri per costruire la
    // query da eseguire sul  servizio sparql inglese. Sono restituiti sottoforma di resultset properties
    // e values ottenute da dbpedia inglese. Il riferimento al model è passato al fine di poter operare
    // gli inserimenti nel db.
    public static void getPropertiesRS(ArrayList<DbpediaObject> list) {
        String service = "https://dbpedia.org/sparql"; //possiamo fare prove delle query qui

        for (int i = 0; i < list.size(); i++) {
            String queryString = new String("SELECT ?p" +
                    "WHERE{ " +
                    " ?s ?p ?o. FILTER ( ?s = <" +
                    list.get(i).getUriDbpedia() +
                    ">). } LIMIT 20");
            System.out.println(queryString);
            org.apache.jena.query.Query query = QueryFactory.create(queryString);

            try (QueryExecution qexec = QueryExecutionFactory.sparqlService(service, query);) {
                org.apache.jena.query.ResultSet resultSet = qexec.execSelect();
                FileWriter fileWriter = new FileWriter("estrazione_properties", true);
                PrintWriter printWriter = new PrintWriter(fileWriter);
                while (resultSet.hasNext()) {
                    QuerySolution solution = resultSet.nextSolution();
                    printWriter.println( " p: " + solution.get("p").toString());
                }
                printWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }




}