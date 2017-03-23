
package informes;

import java.util.ArrayList;

public class Taula {
	private int nfiles;
	
	public int getNfiles() {
		return nfiles;
	}

	protected void setNfiles(int nfiles) {
		this.nfiles = nfiles;
	}

	private int ncolumnes;

	public int getNcolumnes() {
		return ncolumnes;
	}

	protected void setNcolumnes(int ncolumnes) {
		this.ncolumnes = ncolumnes;
	}

	private ArrayList taula = null;

	public ArrayList getTaula() {
		return taula;
	}

	public void setTaula(ArrayList taula) {
		this.taula = taula;
	}
	
	public Taula(int nfiles, int ncolumnes) {
		setNfiles(nfiles);
		setNcolumnes(ncolumnes);
		ArrayList t = new ArrayList();
		for (int i=0 ; i<nfiles ; i++) {
			ArrayList fila = new ArrayList();
			for (int j=0; j<ncolumnes ; j++) {
				// fila.add(new String());
				fila.add(null);
			}
			t.add(fila);
		}
		setTaula(t);
	}
	
	public void afegeixFila() {
		int ncolumnes = getNcolumnes();
		ArrayList fila = new ArrayList();
		for (int j=0; j<ncolumnes ; j++) {
			// fila.add(new String());
			fila.add(null);
		}
		getTaula().add(fila);		
		setNfiles(getNfiles()+1);
	}

	public void afegeixFila(ArrayList llistaStrings) {
		getTaula().add(llistaStrings);		
		setNfiles(getNfiles()+1);
	}
	
	public void setPosicio(int fila, int columna, String str) { 
	    // numerem de 1 ... n i no de 0 ... n-1
		((ArrayList) getTaula().get(fila-1)).set(columna-1,str);
	}

	public String getPosicio(int fila, int columna) {
		return (String) ((ArrayList) getTaula().get(fila-1)).get(columna-1);
	}
	
	public void imprimeix() {
		System.out.println(this.getTaula());
	}


}
