package it.dataIntegration.model;

import java.util.ArrayList;

/*
 * classe utilizzata per modella uri dbpedia estratti dalle notizie
 */
public class DbpediaObject {

	private String nome;
	private String uriDbpedia;
	private Double confidence;



	public DbpediaObject(String nome, String uriDbpedia, Double confidence) {
		super();
		this.nome = nome;
		this.uriDbpedia = uriDbpedia;
		this.confidence= confidence;
	}

	public String getNome() {
		return nome;
	}

	public String getUriDbpedia() {
		return uriDbpedia;
	}

	public Double getConfidence() {
		return confidence;
	}

	/**
	 * 
	 * Dbpedia Object sono uguali se hanno uri e nome uguali
	 * 
	 */

	@Override

	public boolean equals(Object obj) {

		return (this.uriDbpedia.equals(((DbpediaObject) obj).uriDbpedia)

				&& this.nome.equals(((DbpediaObject) obj).nome));
	}

	public static ArrayList<DbpediaObject> cleanArrayList(ArrayList<DbpediaObject> list) {
		ArrayList<DbpediaObject> cleanedArray = new ArrayList<DbpediaObject>();
		for (int i = 0; i < list.size(); i++) {
			if (!cleanedArray.contains(list.get(i))) {
				cleanedArray.add(list.get(i));
			}
		}
		return cleanedArray;
	}
	
	public static boolean contains(ArrayList<DbpediaObject> list, String Uri) {
		boolean result = false;
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).getUriDbpedia().toString().equals(Uri)) {
				result = true;
				break;
			}
		}
		return result;
	}

}
