package timetracker;

import java.util.ArrayList;

public class Taula {
  private int nfiles;
  
  public int getNfiles() {
    return nfiles;
  }

  protected void setNfiles(int nfiles) {
    assert ( nfiles > 0 ) : "Taula: nfiles igual o menos a 0";
    this.nfiles = nfiles;
  }

  private int ncolumnes;

  public int getNcolumnes() {
    return ncolumnes;
  }

  protected void setNcolumnes(int ncolumnes) {
    assert ( ncolumnes > 0 ) : "Taula: ncolumans igual o menos a 0";
    this.ncolumnes = ncolumnes;
  }

  private ArrayList taula = null;

  public ArrayList getTaula() {
    return taula;
  }

  public void setTaula(ArrayList taula) {
    assert ( taula != null ) : "Taula:taula nulo";
    this.taula = taula;
  }
  
  /**
   * Crea una Tabla de NxM.
   */
  public Taula(int nfiles, int ncolumnes) {
    assert ( nfiles > 0 ) : "Taula: nfiles 0 o inferior";
    assert ( ncolumnes > 0 ) : "Taula: ncolumnes 0 o ingerior";
    setNfiles(nfiles);
    setNcolumnes(ncolumnes);
    ArrayList temp = new ArrayList();
    for (int i = 0 ; i < nfiles ; i++) {
      ArrayList fila = new ArrayList();
      for (int j = 0; j < ncolumnes ; j++) {
        // fila.add(new String());
        fila.add(null);
      }
      temp.add(fila);
    }
    setTaula(temp);
  }
  
  /**
   * añade una nueva liena a la tabla.
   */
  public void afegeixFila() {
    int ncolumnes = getNcolumnes();
    ArrayList fila = new ArrayList();
    for (int j = 0; j < ncolumnes ; j++) {
      // fila.add(new String());
      fila.add(null);
    }
    getTaula().add(fila);    
    setNfiles(getNfiles() + 1);
  }
/**
 * Agrega una fila a una lista pasada pro parametro.
 */
  public void afegeixFila(ArrayList llistaStrings) {
    assert ( llistaStrings != null ) : "Taula: llistaString vuida";
    getTaula().add(llistaStrings);    
    setNfiles(getNfiles() + 1);
  }
  
  /**
   * set Posicion.
   */
  public void setPosicio(int fila, int columna, String str) {
    assert ( fila > 0 ) : "Taula: fila nulo";
    assert ( columna > 0 ) : "Taula: columna nulo";
    assert ( str != null ) : "Taula: str nulo";
      // numerem de 1 ... n i no de 0 ... n-1
    ((ArrayList) getTaula().get(fila - 1)).set(columna - 1,str);
  }

  /**
   * get Posicion.
   */
  public String getPosicio(int fila, int columna) {
     assert ( fila > 0 ) : "Taula: fila nulo";
     assert ( columna > 0 ) : "Taula: columna nulo";
    return (String) ((ArrayList) getTaula().get(fila - 1)).get(columna - 1);
  }
  
  public void imprimeix() {
    System.out.println(this.getTaula());
  }


}
