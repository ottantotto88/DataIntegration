package it.dataIntegration.controller;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import it.dataIntegration.model.DbpediaObject;
import it.dataIntegration.model.Path;
import it.dataIntegration.model.ExpandedNode;
import it.dataIntegration.utility.ProcessCpuLoad;
import it.dataIntegration.utility.RestServices;
import it.dataIntegration.utility.SparqlQuery;
import it.dataIntegration.view.DataIntegrationPanel;
import it.dataIntegration.view.FinishedElaborationPanel;
import it.dataIntegration.view.PleaseWaitPanel;
import it.dataIntegration.view.ResultPanel;

public class DataIntegrationController {
	private Model initalFirstModel;
	private Model modelFirstUri;
	private Model modelFirstUriModified;
	private Model modelSecondUri;
	private DataIntegrationPanel view;
	private ArrayList<RDFNode> matches = new ArrayList<RDFNode>();
	ArrayList<DbpediaObject> list = new ArrayList<DbpediaObject>();
	ArrayList<DbpediaObject> list2 = new ArrayList<DbpediaObject>();
	Instant start;
	private Instant end;
	private boolean monitorThread = true;
	private PrintWriter writer;
	private RDFNode firstObject = null;
	private RDFNode secondObject = null;
	private int maxIteration;
	private int currentIteration = 1;
	private int matchFound = 0;

	public DataIntegrationController(final DataIntegrationPanel view, Frame frame) {
		this.view = view;
		// creo il file di log
		try {
			File file = new File("log.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			writer = new PrintWriter(bw);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		view.getBtnCerca().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				 * Thread utilizzato in fase di test per comparare le prestazioni della ricerca
				 * in profondità e della ricerca in ampienza
				 */
				Thread threadCpuLoad = new Thread(new Runnable() {
					public void run() {
						while (monitorThread) {
							try {
								ProcessCpuLoad.getProcessCpuLoad();
								Thread.sleep(500);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

				});
				threadCpuLoad.start();
				view.getBtnCerca().setEnabled(false);
				view.getBtnPulisci().setEnabled(true);
				JDialog dialog = new JDialog(frame, "Please Wait...", true);
				PleaseWaitPanel panelPleaseWait = new PleaseWaitPanel();
				dialog.getContentPane().add(panelPleaseWait);
				dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				dialog.pack();
				dialog.setSize(350, 150);
				dialog.setMinimumSize(dialog.getSize());
				dialog.setMaximumSize(dialog.getSize());
				dialog.setLocationRelativeTo(frame);

				Thread thread = new Thread(new Runnable() {
					public void run() {
						start = Instant.now();
						String urlNotizia = view.getTxtUrl().getText();
						String urlNotizia2 = view.getTxtUrl2().getText();
						maxIteration = Integer.valueOf(view.getTxtIterazioni().getText());
						// Lista di Uri dbpedia estratti dalla prima notizia
						list = RestServices.getRequest(urlNotizia);
						// Lista di Uri dbpedia estratti dalla seconda notizia
						list2 = RestServices.getRequest(urlNotizia2);
						// elimino doppioni dalle liste
						list = DbpediaObject.cleanArrayList(list);
						list2 = DbpediaObject.cleanArrayList(list2);
						// scrivo sul file di log le Uri estratte
						writeLog(list, list2, true, null);
						// cerco tutte le triple che hanno come soggetto/oggetto i vari uri estratti
						// dalla prima notizia
						modelFirstUri = SparqlQuery.QuerySparql(list);
						initalFirstModel = modelFirstUri;
						modelFirstUriModified = modelFirstUri;
						// cerco tutte le triple che hanno come soggetto/oggetto i vari uri estratti
						// dalla seconda notizia
						modelSecondUri = SparqlQuery.QuerySparql(list2);

						// Stampo a schermo le keyword con i relativi uri DBPedia estratti dalle news
						writeResult(true, false, false);

						writer.println("Iterazione n° " + currentIteration + ":");
						writer.println();
						// faccio un check sui due modelli ottenuti al fine di constatare un'eventuale
						// intersezione
						boolean match = searchMatch();
						writeResult(false, match, false);
						if (view.getRdbtnProfondità().isSelected()) {
							ricercaInProfondita();
						} else {
							ricercaInAmpiezza();
						}
						dialog.dispose();
						writer.close();

						JDialog dialog2 = new JDialog(frame, "Elaborazioe Terminata", true);
						String message;
						if (currentIteration == maxIteration) {
							message = "La ricerca è terminata dopo " + currentIteration + " iterazioni, trovando "
									+ matchFound + " match";
						} else {
							message = "L'algoritmo non ha potuto raggiungere il numero di iterazioni richieste"
									+ "\n Si è fermato a " + currentIteration + " iterazioni trovando " + matchFound
									+ " match";
						}
						FinishedElaborationPanel panelfinishedElaboration = new FinishedElaborationPanel(message);
						dialog2.getContentPane().add(panelfinishedElaboration);
						dialog2.pack();
						dialog2.setSize(500, 180);
						dialog2.setMinimumSize(dialog2.getSize());
						dialog2.setMaximumSize(dialog2.getSize());
						dialog2.setLocationRelativeTo(frame);
						panelfinishedElaboration.getBtnOk().addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								dialog2.dispose();
							}
						});
						dialog2.setVisible(true);
					}
				});
				thread.start();
				dialog.setVisible(true);
			}
		});

		view.getBtnPulisci().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				view.getBtnPulisci().setEnabled(false);
				view.getPanelBox().removeAll();
				view.getPanelBox().revalidate();
				view.getBtnCerca().setEnabled(true);
				view.getTxtIterazioni().setText("100");
				currentIteration = 1;
				monitorThread = true;
				firstObject = null;
				secondObject = null;
				matches.clear();
				matchFound = 0;
				ProcessCpuLoad.clearArrayList();
			}
		});
	}

	/*
	 * Questso metodo scrive nel file di log tutti gli uri estratti dalle due
	 * notizie; il parametro mode serve per differenziare tra le da fare sul file di
	 * log
	 */
	public void writeLog(ArrayList<DbpediaObject> list, ArrayList<DbpediaObject> list2, boolean mode, String nodo) {
		if (mode) {
			writer.println("Link notizia 1: " + view.getTxtUrl().getText());
			writer.println();
			writer.println("Uri estratti dalla prima notizia:");
			writer.println();
			for (int i = 0; i < list.size(); i++) {
				writer.println(list.get(i).getNome() + " ---> " + list.get(i).getUriDbpedia());
			}
			writer.println();
			writer.println();
			writer.println("Link notizia 2: " + view.getTxtUrl2().getText());
			writer.println();
			writer.println("Uri estratti dalla seconda notizia:");
			writer.println();
			for (int i = 0; i < list2.size(); i++) {
				writer.println(list2.get(i).getNome() + " ---> " + list2.get(i).getUriDbpedia());
			}
			writer.println();
			writer.println();
		} else if (!mode && nodo == null) {
			writer.println("Dimensione del grafo 1 ---> " + modelFirstUriModified.size());
			writer.println("Dimensione del grafo 2 ---> " + modelSecondUri.size());
			writer.println();
			writer.println();
		} else {
			writer.println("Iterazione n° " + currentIteration + ":");
			writer.println();
			writer.println("Espansione dell' URI: " + nodo);
			writer.println();
		}

	}

	/*
	 * il metodo searchMatch() controlla se tra i subject e gli object presenti nel
	 * primo modello è presente un match con un qualche altro subject o object
	 * presente nel secondo modello
	 */
	private boolean searchMatch() {
		// stampo nel file di log la dimensione dei due grafi
		writeLog(null, null, false, null);
		// estraggo tutti subject degli statements del primo modello
		ArrayList<Resource> resources = (ArrayList<Resource>) modelFirstUri.listSubjects().toList();
		boolean match = false;
		for (int i = 0; i < resources.size(); i++) {
			/*
			 * verifico se uno di questi subject è presente in qualche tripla del secondo
			 * modello, sia come subject sia come object valuto solo letterali o uri di tipo
			 * resource
			 */
			if (resources.get(i).toString().matches("http://it.dbpedia.org/resource/(.*)")
					|| resources.get(i).isLiteral()) {
				SimpleSelector selectSub = new SimpleSelector(resources.get(i), (Property) null, (RDFNode) null);
				StmtIterator iter = modelSecondUri.listStatements(selectSub);
				SimpleSelector selectObj = new SimpleSelector((Resource) null, (Property) null,
						(RDFNode) resources.get(i));
				StmtIterator iter2 = modelSecondUri.listStatements(selectObj);
				if (iter.hasNext() || iter2.hasNext()) {
					match = true;
					// salvo tutti i match in un opportuno ArrayList
					matches.add(resources.get(i));
				}
			}
		}
		/*
		 * ripeto lo stesso procidemento del ciclo precednete, lavorando però questa
		 * volta con gli object estratti dagli statements del primo modello
		 */
		ArrayList<RDFNode> nodes = (ArrayList<RDFNode>) modelFirstUri.listObjects().toList();
		for (int j = 0; j < nodes.size(); j++) {
			if (nodes.get(j).toString().matches("http://it.dbpedia.org/resource/(.*)")) {
				SimpleSelector selectSbj = new SimpleSelector((Resource) nodes.get(j), (Property) null, (RDFNode) null);
				SimpleSelector selectObj = new SimpleSelector((Resource) null, (Property) null, (RDFNode) nodes.get(j));
				StmtIterator iter = modelSecondUri.listStatements(selectSbj);
				StmtIterator iter2 = modelSecondUri.listStatements(selectObj);
				if (iter.hasNext() || iter2.hasNext()) {
					match = true;
					// salvo tutti i match in un opportuno ArrayList
					matches.add(nodes.get(j));
				}
			} else if (nodes.get(j).isLiteral()) {
				SimpleSelector selectObj = new SimpleSelector((Resource) null, (Property) null, (RDFNode) nodes.get(j));
				StmtIterator iter = modelSecondUri.listStatements(selectObj);
				if (iter.hasNext()) {
					match = true;
					// salvo tutti i match in un opportuno ArrayList
					matches.add(nodes.get(j));
				}
			}
		}
		return match;
	}

	/*
	 * Nel caso in cui non sia presente alcun match tra i due modelli iniziali
	 * proseguo espandendo in profondintà gli object del primo modello.
	 */
	private void ricercaInProfondita() {
		boolean match = false;
		/*
		 * definisco un array list che userò per memorizzare tutti i nodi espansi, così
		 * da evitare di espandere due volte lo stesso nodo
		 */
		ArrayList<String> nodeAlreadyExpanded = new ArrayList<String>();

		// Prendo tutti gli object del primo modello, che poi espanderò
		ArrayList<RDFNode> resources = (ArrayList<RDFNode>) modelFirstUri.listObjects().toList();
		// estraggo tutti i subjects dal modello, che poi espanderò
		ArrayList<Resource> subjects = (ArrayList<Resource>) modelFirstUri.listSubjects().toList();
		// per ogni subjects estratto, faccio il cast in RDFNode e lo inserisco
		// all'iterno del ArrayList
		for (int i = 0; i < subjects.size(); i++) {
			RDFNode nodeX = (RDFNode) subjects.get(i);
			resources.add(nodeX);
		}

		/*
		 * itero sulla lista di Objects estratti per e di volta in volta definisco un
		 * nuovo modello contenente tutte le triple che hanno come subject o object
		 * l'object della lista
		 */
		for (int i = 0; i < resources.size(); i++) {
			if (currentIteration < maxIteration) {
				RDFNode node = resources.get(i);
				if (!nodeAlreadyExpanded.contains(node.toString())) {
					currentIteration++;
					nodeAlreadyExpanded.add(node.toString());
					// vado a scrive l'uri del nodo che sto espandendo nel log
					writeLog(null, null, false, node.toString());

					/*
					 * la variabile globale fistObject memorizza il nodo che sto espandendo sarà poi
					 * utile per ricostruire il path dell'eventuale match trovato
					 */
					firstObject = node;
					Model newModel;
					if (node.isLiteral()) {
						newModel = SparqlQuery.QuerySparql(node.toString(), true);
					} else {
						newModel = SparqlQuery.QuerySparql(node.toString(), false);
					}
					// vado ad aggiornare la dimensione del grafo
					modelFirstUriModified = modelFirstUriModified.union(newModel);
					/*
					 * a questo punto sovrascrivo il precendente modello con l'attuale per poi
					 * verificare se sono presenti dei match con i nuovi subject e object estratti
					 */
					modelFirstUri = newModel;
					match = searchMatch();
					writeResult(false, match, false);
					/*
					 * A questo punto espando una sola volta gli object del modello creato sopra
					 */
					// if (!match) {
					ArrayList<RDFNode> resources2 = (ArrayList<RDFNode>) newModel.listObjects().toList();
					ArrayList<Resource> subjects2 = (ArrayList<Resource>) newModel.listSubjects().toList();
					for (int ii = 0; ii < subjects2.size(); ii++) {
						RDFNode nodeX = (RDFNode) subjects2.get(ii);
						resources2.add(nodeX);
					}
					for (int j = 0; j < resources2.size(); j++) {
						if (currentIteration < maxIteration) {
							RDFNode node2 = resources2.get(j);
							if (!nodeAlreadyExpanded.contains(node2.toString())) {
								currentIteration++;
								nodeAlreadyExpanded.add(node2.toString());
								/*
								 * la variabile globale secondObject memorizza il sotto-nodo(rispetto al nodo
								 * firstObject) che sto espandendo sarà poi utile per ricostruire il path
								 * dell'eventuale match trovato
								 */
								secondObject = node2;
								// vado a scrive l'uri del nodo che sto espandendo nel log
								writeLog(null, null, false, node2.toString());
								Model newModel2;
								if (node2.isLiteral()) {
									// devo cercare tutte le triple cha hanno node come uri
									newModel2 = SparqlQuery.QuerySparql(node2.toString(), true);
								} else {
									// devo cercare tutte le triple cha hanno node come uri
									newModel2 = SparqlQuery.QuerySparql(node2.toString(), false);
								}
								// vado ad aggiornare la dimensione del grafo
								modelFirstUriModified = modelFirstUriModified.union(newModel2);
								// quindi cerco di nuovi dei match
								modelFirstUri = newModel2;
								match = searchMatch();
								writeResult(false, match, false);
								/*
								 * if (match) { // se trovo un match interrompo l'iterazione break; }
								 */
							}
						} else {
							break;
						}
					}
					secondObject = null;
					// }
					// se trovo un match interrompo l'iterazione

					// if (match) {
					// break;
					// }

					/*
					 * Giunti qui, se non si è trovato alcun match si prosegue espandendo gli Object
					 * estratti inizialmente (quelli del primo ciclo for)
					 */
				}
			} else {
				break;
			}
		}
		writeResult(false, false, true);
	}

	/* Inizio metodo nuovo */

	private void ricercaInAmpiezza() {
		boolean match = false;
		/*
		 * definisco un array list che userò per memorizzare tutti i nodi espansi,
		 * cosicchè da evitare di espandere due volte lo stesso nodo
		 */
		ArrayList<String> nodeAlreadyExpanded = new ArrayList<String>();

		// Prendo tutti gli object del primo modello, che poi espander�
		ArrayList<RDFNode> resources = (ArrayList<RDFNode>) modelFirstUri.listObjects().toList();
		// estraggo tutti i subjects dal modello, che poi espander�
		ArrayList<Resource> subjects = (ArrayList<Resource>) modelFirstUri.listSubjects().toList();

		ArrayList<ExpandedNode> expandedNodes = new ArrayList<ExpandedNode>();

		// per ogni subjects estratto, faccio il cast in RDFNode e lo inserisco
		// all'iterno del ArrayList
		for (int i = 0; i < subjects.size(); i++) {
			RDFNode nodeX = (RDFNode) subjects.get(i);
			resources.add(nodeX);
		}

		/*
		 * itero sulla lista di Objects estratti per e di volta in volta definisco un
		 * nuovo modello contenente tutte le triple che hanno come subject o object
		 * l'object della lista
		 */

		for (int i = 0; i < resources.size(); i++) {
			if (currentIteration < maxIteration) {
				RDFNode node = resources.get(i);
				if (!nodeAlreadyExpanded.contains(node.toString())) {
					currentIteration++;
					nodeAlreadyExpanded.add(node.toString());
					// vado a scrive l'uri del nodo che sto espandendo nel log
					writeLog(null, null, false, node.toString());

					/*
					 * la variabile globale fistObject memorizza il nodo che sto espandendo sarà poi
					 * utile per ricostruire il path dell'eventuale match trovato
					 */
					firstObject = node;
					Model newModel;
					if (node.isLiteral()) {
						newModel = SparqlQuery.QuerySparql(node.toString(), true);
					} else {
						newModel = SparqlQuery.QuerySparql(node.toString(), false);
					}
					// vado ad aggiornare la dimensione del grafo
					modelFirstUriModified = modelFirstUriModified.union(newModel);
					/*
					 * a questo punto sovrascrivo il precendente modello con l'attuale per poi
					 * verificare se sono presenti dei match con i nuovi subject e object estratti
					 */
					modelFirstUri = newModel;
					ExpandedNode object1 = new ExpandedNode(node, modelFirstUri);
					expandedNodes.add(object1);

					match = searchMatch();
					writeResult(false, match, false);
				}
			} else {
				break;
			}
		}

		/*
		 * se non ho superato il numero massimo di iterazioni proseguo espandendo una
		 * volta ogni nodo contenuto nei modelli salvati all'interno della lista
		 * expandedNodes. Quindi scendo in profondità nel mio grafo
		 */
		for (int i = 0; i < expandedNodes.size(); i++) {
			if (currentIteration < maxIteration) {
				firstObject = expandedNodes.get(i).getNode();
				Model modelListRDFNodeModel = expandedNodes.get(i).getModel();
				ArrayList<RDFNode> resources2 = (ArrayList<RDFNode>) modelListRDFNodeModel.listObjects().toList();
				ArrayList<Resource> subjects2 = (ArrayList<Resource>) modelListRDFNodeModel.listSubjects().toList();
				for (int ii = 0; ii < subjects2.size(); ii++) {
					RDFNode nodeX = (RDFNode) subjects2.get(ii);
					resources2.add(nodeX);
				}
				for (int j = 0; j < resources2.size(); j++) {
					RDFNode node2 = resources2.get(j);
					if (!nodeAlreadyExpanded.contains(node2.toString()) && currentIteration < maxIteration) {
						currentIteration++;
						nodeAlreadyExpanded.add(node2.toString());
						/*
						 * la variabile globale secondObject memorizza il sotto-nodo(rispetto al nodo
						 * firstObject) che sto espandendo sarà poi utile per ricostruire il path
						 * dell'eventuale match trovato
						 */
						secondObject = node2;
						// vado a scrive l'uri del nodo che sto espandendo nel log
						writeLog(null, null, false, node2.toString());
						Model newModel2;
						if (node2.isLiteral()) {
							// devo cercare tutte le triple cha hanno node come uri
							newModel2 = SparqlQuery.QuerySparql(node2.toString(), true);
						} else {
							// devo cercare tutte le triple cha hanno node come uri
							newModel2 = SparqlQuery.QuerySparql(node2.toString(), false);
						}
						// vado ad aggiornare la dimensione del grafo
						modelFirstUriModified = modelFirstUriModified.union(newModel2);
						// quindi cerco di nuovi dei match
						modelFirstUri = newModel2;
						match = searchMatch();
						writeResult(false, match, false);
					} else {
						if(currentIteration >= maxIteration) {
							break;
						}
					}
				}
				secondObject = null;
			} else {
				break;
			}

		}
		writeResult(false, false, false);
	}

	/*
	 * Questo metodo mostra a schermo eventuali match trovati durante le varie
	 * iterazioni dell'algoritmo di ricerca
	 */
	private void writeResult(boolean keyword, boolean match, boolean finish) {
		if (keyword) {
			ResultPanel resultPanel = new ResultPanel();
			JTextArea txtArea = resultPanel.getTextArea();
			txtArea.setFont(new Font("Lucida Grande", Font.BOLD, 14));

			txtArea.append("Keyword/UriDbPedia estratti dalla prima notizia:");
			txtArea.append(System.lineSeparator());
			txtArea.append(System.lineSeparator());
			int j = 0;
			for (int i = 0; i < list.size(); i++) {
				j = i + 1;
				txtArea.append(j + ") " + list.get(i).getNome() + "/" + list.get(i).getUriDbpedia()+ " , confidence: " + list.get(i).getConfidence()) ;
				txtArea.append(System.lineSeparator());
			}

			txtArea.append(System.lineSeparator());
			txtArea.append(System.lineSeparator());
			txtArea.append("Keyword/UriDbPedia estratti dalla seconda notizia:");
			txtArea.append(System.lineSeparator());
			txtArea.append(System.lineSeparator());
			j = 0;
			for (int i = 0; i < list2.size(); i++) {
				j = i + 1;
				txtArea.append(j + ") " + list2.get(i).getNome() + "/" + list2.get(i).getUriDbpedia()+ " , confidence:  " + list2.get(i).getConfidence());
				txtArea.append(System.lineSeparator());
			}
			view.getPanelBox().add(txtArea);
			view.getPanelBox().revalidate();
		}
		if (match) {
			ResultPanel resPanel = new ResultPanel();
			JTextArea txtArea = resPanel.getTextArea();
			txtArea.setFont(new Font("Lucida Grande", Font.BOLD, 14));
			txtArea.append("Risultato iterazione n°: " + currentIteration);
			txtArea.append(System.lineSeparator() + "Durante l'iterazione " + currentIteration
					+ " sono stati trovati match per i seguenti letterali e/o uri:" + System.lineSeparator());
			txtArea.append(System.lineSeparator());
			for (int i = 0; i < matches.size(); i++) {
				txtArea.append(matches.get(i).toString() + System.lineSeparator());
				matchFound++;
			}
			txtArea.append(System.lineSeparator());
			for (int i = 1; i <= matches.size(); i++) {
				String path1 = searchPath(i - 1, true);
				String path2 = searchPath(i - 1, false);
				txtArea.append(System.lineSeparator());
				txtArea.append(System.lineSeparator() + "Percorso su grafo 1 (" + i + "° match): " + path1);
				txtArea.append(System.lineSeparator() + "Percorso su grafo 2 (" + i + "° match): " + path2);
			}
			view.getPanelBox().add(txtArea);
			view.getPanelBox().revalidate();
		}
		if (finish) {
			ResultPanel resPanel = new ResultPanel();
			JTextArea txtArea = resPanel.getTextArea();
			end = Instant.now();
			monitorThread = false;
			Duration d = Duration.between(start, end);
			txtArea.setFont(new Font("Lucida Grande", Font.BOLD, 14));
			txtArea.append("Tempo trascorso durante la ricerca: " + d.toMinutes() + " minuti "
					+ d.minusMinutes(d.toMinutes()).getSeconds() + " secondi.");
			txtArea.append(System.lineSeparator());
			txtArea.append(System.lineSeparator() + "Utilizzo massimo della cpu per la JVM: "
					+ ProcessCpuLoad.getMaxProcessLoad() + "%");
			txtArea.append(System.lineSeparator() + "Utilizzo medio della cpu per la JVM: "
					+ ProcessCpuLoad.getAverageProcessLoad() + "%");
			view.getPanelBox().add(txtArea);
			view.getPanelBox().revalidate();
		}
		matches.clear();
	}

	/*
	 * Questo metodo permette restituisce il percorso seguito per trovare una match
	 * tra i due grafi se check == false trova il percorso relativo al match sul
	 * secondo grafo
	 */
	private String searchPath(int numeroMatch, boolean check) {
		// le seguenti variabili servono per selezionare gli statements nel primo
		// modello che portano al match trovato
		SimpleSelector selectSub;
		SimpleSelector selectSub1;
		StmtIterator iter;
		StmtIterator iter1;
		Statement stmt = null;
		Statement stmt1;
		ArrayList<Path> paths = new ArrayList<Path>();

		// for (int i = 0; i < matches.size(); i++) {
		if (check) {
			// trovato un match al primo livello di espansione
			if (firstObject != null) {
				if (!DbpediaObject.contains(list, firstObject.toString())) {
					boolean trovato = false;
					// cerco lo statement relativo all'object espanso
					selectSub = new SimpleSelector((Resource) null, (Property) null, (RDFNode) firstObject);
					iter = initalFirstModel.listStatements(selectSub);
					while (iter.hasNext()) {
						stmt = iter.next();
						if (DbpediaObject.contains(list, stmt.getSubject().toString())) {
							trovato = true;
							break;
						}
					}
					if (!trovato) {
						selectSub = new SimpleSelector((Resource) firstObject, (Property) null, (RDFNode) null);
						iter = initalFirstModel.listStatements(selectSub);
						while (iter.hasNext()) {
							stmt = iter.next();
							if (DbpediaObject.contains(list, stmt.getObject().toString())) {
								trovato = true;
								break;
							}
						}
					}
					if(stmt != null) {
						// Con lo statament trovato creo una variabile di tipo path che utilizzero per
						// stampare a video poi il percorso seguito
						Path path = new Path(stmt.getSubject().toString(), stmt.getPredicate().toString(),
								stmt.getObject().toString());
						paths.add(path);
					}
				}
				// se secondObject != null vuol dire che dopo aver espanso un nodo, ho espanso
				// anche un suo figlio
				if (secondObject != null) {
					// di conseguenza devo ricercare il predicato che lega i due nodi espansi
					if (!firstObject.isLiteral()) {
						selectSub = new SimpleSelector((Resource) firstObject, (Property) null, (RDFNode) secondObject);
						iter = modelFirstUriModified.listStatements(selectSub);
					} else {
						selectSub = null;
						iter = null;
					}
					if (!secondObject.isLiteral()) {
						selectSub1 = new SimpleSelector((Resource) secondObject, (Property) null,
								(RDFNode) firstObject);
						iter1 = modelFirstUriModified.listStatements(selectSub1);
					} else {
						selectSub1 = null;
						iter1 = null;
					}

					if (selectSub != null && iter.hasNext()) {
						stmt = iter.next();
						Path path1 = new Path(stmt.getSubject().toString(), stmt.getPredicate().toString(),
								stmt.getObject().toString());
						paths.add(path1);
					}
					if (selectSub1 != null && iter1.hasNext()) {
						stmt1 = iter1.next();
						Path path2 = new Path(stmt1.getSubject().toString(), stmt1.getPredicate().toString(),
								stmt1.getObject().toString());
						paths.add(path2);
					}
					// a questo punto devo trovare lo statement dal secondo nodo espanso al match
					// trovato
					selectSub = new SimpleSelector((Resource) secondObject, (Property) null,
							(RDFNode) matches.get(numeroMatch));
					iter = modelFirstUriModified.listStatements(selectSub);

					if (!matches.get(numeroMatch).isLiteral()) {
						selectSub1 = new SimpleSelector((Resource) matches.get(numeroMatch), (Property) null,
								(RDFNode) secondObject);
						iter1 = modelFirstUriModified.listStatements(selectSub1);
					} else {
						selectSub1 = null;
					}

					if (iter.hasNext()) {
						stmt = iter.next();
						Path path1 = new Path(stmt.getSubject().toString(), stmt.getPredicate().toString(),
								stmt.getObject().toString());
						paths.add(path1);
					}
					if (selectSub1 != null && iter1.hasNext()) {
						stmt1 = iter1.next();
						Path path2 = new Path(stmt1.getSubject().toString(), stmt1.getPredicate().toString(),
								stmt1.getObject().toString());
						paths.add(path2);
					}
				} else {
					/*
					 * Se sono qui vuol dire che non ho espanso alcun nodo figlio di un nodo già
					 * espanso quindi devo trovare il predicato che lega il match trovato con il
					 * nodo espanso
					 */

					selectSub = new SimpleSelector((Resource) firstObject, (Property) null,
							(RDFNode) matches.get(numeroMatch));
					iter = modelFirstUriModified.listStatements(selectSub);
					if (!matches.get(numeroMatch).isLiteral()) {
						selectSub1 = new SimpleSelector((Resource) matches.get(numeroMatch), (Property) null,
								(RDFNode) firstObject);
						iter1 = modelFirstUriModified.listStatements(selectSub1);
					} else {
						selectSub1 = null;
						iter1 = null;
					}
					if (iter.hasNext()) {
						stmt = iter.next();
						Path path1 = new Path(stmt.getSubject().toString(), stmt.getPredicate().toString(),
								stmt.getObject().toString());
						paths.add(path1);
					}
					if (selectSub1 != null && iter1.hasNext()) {
						stmt1 = iter1.next();
						Path path2 = new Path(stmt1.getSubject().toString(), stmt1.getPredicate().toString(),
								stmt1.getObject().toString());
						paths.add(path2);
					}
				}
			} else {
				/*
				 * Ho trovato un match senza alcuna espansione devo quindi trovare soggetto e
				 * predicato del match trovato (o oggetto e predicato)
				 */
				selectSub = new SimpleSelector((Resource) null, (Property) null, (RDFNode) matches.get(numeroMatch));
				iter = modelFirstUriModified.listStatements(selectSub);
				if (!matches.get(numeroMatch).isLiteral()) {
					selectSub1 = new SimpleSelector((Resource) matches.get(numeroMatch), (Property) null,
							(RDFNode) null);
					iter1 = modelFirstUriModified.listStatements(selectSub1);
				} else {
					selectSub1 = null;
					iter1 = null;
				}
				if (iter.hasNext()) {
					stmt = iter.next();
					Path path1 = new Path(stmt.getSubject().toString(), stmt.getPredicate().toString(),
							stmt.getObject().toString());
					paths.add(path1);
				}
				if (selectSub1 != null && iter1.hasNext()) {
					stmt1 = iter1.next();
					Path path2 = new Path(stmt1.getSubject().toString(), stmt1.getPredicate().toString(),
							stmt1.getObject().toString());
					paths.add(path2);
				}
			}
		} else {
			/*
			 * Se sono qui è perchè sto cercando lo statement relativo al match trovato sul
			 * secondo modello dato che il secondo modello non viene espanso devo solo
			 * trovare soggetto e predicato (o oggetto e predicato) del match trovato
			 */

			selectSub = new SimpleSelector((Resource) null, (Property) null, (RDFNode) matches.get(numeroMatch));
			iter = modelSecondUri.listStatements(selectSub);
			if (!matches.get(numeroMatch).isLiteral()) {
				selectSub1 = new SimpleSelector((Resource) matches.get(numeroMatch), (Property) null, (RDFNode) null);
				iter1 = modelSecondUri.listStatements(selectSub1);
			} else {
				selectSub1 = null;
				iter1 = null;
			}

			if (iter.hasNext()) {
				stmt = iter.next();
				Path path1 = new Path(stmt.getSubject().toString(), stmt.getPredicate().toString(),
						stmt.getObject().toString());
				paths.add(path1);
			}
			if (selectSub1 != null && iter1.hasNext()) {
				stmt1 = iter1.next();
				Path path2 = new Path(stmt1.getSubject().toString(), stmt1.getPredicate().toString(),
						stmt1.getObject().toString());
				paths.add(path2);
			}
		}
		// }

		return Path.writePath(paths);
	}
}
