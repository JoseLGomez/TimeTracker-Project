package timetracker;

import java.util.ArrayList;

/**
 * Clase facilitada para la practica que implementa las funcionalidades de 
 * una tabla, para hacer uso de ella en los informes.
 *
 */
public class Table extends Element {
  private int nfiles;
  
  public int getNfiles() {
    return nfiles;
  }

  protected void setNfiles(int nfiles) {
    assert ( nfiles >= 0 ) : "Table: nfiles valor negativo";
    this.nfiles = nfiles;
  }

  private int ncolumnes;

  public int getNcolumnes() {
    return ncolumnes;
  }

  protected void setNcolumnes(int ncolumnes) {
    assert ( ncolumnes >= 0 ) : "Table: Numero columnes valor negativo";
    this.ncolumnes = ncolumnes;
  }

  private ArrayList taula = null;

  public ArrayList getTaula() {
    return taula;
  }

  public void setTaula(ArrayList taula) {
    assert ( taula != null ) : "Table nulo";
    this.taula = taula;
  }
  
  /**
   * crea una tabla de NxM.
   */
  public Table(int nfiles, int ncolumnes) {
    assert ( nfiles > 0 ) : "Table: nfiles 0 o inferior";
    assert ( ncolumnes > 0 ) : "Table: ncolumnes 0 o ingerior";
    setNfiles(nfiles);
    setNcolumnes(ncolumnes);
    ArrayList temp = new ArrayList();
    for (int i = 0 ; i < nfiles ; i++ ) {
      ArrayList fila = new ArrayList();
      for (int j = 0; j < ncolumnes ; j++ ) {
        // fila.add(new String());
        fila.add(null);
      }
      temp.add(fila);
    }
    setTaula(temp);
  }
  
  /**
   * Agrega una nueva Fila a la tabla.
   */
  public void afegeixFila() {
    int ncolumnes = getNcolumnes();
    ArrayList fila = new ArrayList();
    for (int j = 0; j < ncolumnes ; j++ ) {
      // fila.add(new String());
      fila.add(null);
    }
    getTaula().add(fila);    
    setNfiles(getNfiles() + 1);
  }
  
/**
 *agregar fila a una lista.
 */
  public void afegeixFila(ArrayList llistaStrings) {
    assert ( llistaStrings != null ) : "Table: llistaString vuida";
    getTaula().add(llistaStrings);    
    setNfiles(getNfiles() + 1);
  }
  /**
   * Sacamos las posciones de la tabla.
   * numerem de 1 ... n i no de 0 ... n-1.
   */
  public void setPosicio(int fila, int columna, String str) { 
    assert ( fila > 0 ) : "Table: fila nulo";
    assert ( columna > 0 ) : "Table: columna nulo";
    assert ( str != null ) : "Table: str nulo";      
    ((ArrayList) getTaula().get(fila - 1)).set(columna - 1,str);
  }

  /**
   * get posicion.
   */
  public String getPosicio(int fila, int columna) {
    assert ( fila > 0 ) : "Table: fila nulo";
    assert ( columna > 0 ) : "Table: columna nulo";
    return (String) ((ArrayList) getTaula().get(fila - 1)).get(columna - 1);
  }
  
  public void imprimeix() {
    System.out.println(this.getTaula());
  }

  public void accept(Format visitor) {
    assert ( visitor != null ) : "Table: visitor nulo";
    visitor.visitTable(this);
  }
}
