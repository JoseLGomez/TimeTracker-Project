package timetracker;



/**
 * Clase correspondiente al titulo para los informes.
 * @author Jose Luis Gomez, Eric jaen, Jesus Serrano
 *
 */
public class Title extends Element {
  private String titol;
  /**
   * accept del title.
   */
  public void accept(Format visit) {
    assert ( visit != null ) : "Title: ÁEl vivit contiene valor negativo";
    visit.visitTitle(this);
  }

  /**
   * contructor.
   */
  public Title(String titulo) {
    assert ( titulo != null ) : "Title: titol nulo";
    assert ( !titulo.isEmpty() ) : "Title: titol vacio";
    this.setTitol(titulo);
    checkInvariante();
  }
  
  /**
   * Revision de las propiedades del la clases.
   */
  private void checkInvariante() {
    assert ( this.titol != null ) : "Title: titol nulo";
    assert ( !this.titol.isEmpty() ) : "Title: titol vacio";
  }


  public String getTitol() {
    return titol;
  }

/**
 * set Titol.
 */
  public void setTitol(String titol) {
    assert ( titol != null ) : "Title: titol nulo";
    assert ( !titol.isEmpty() ) : "Title: titol vacio";
    this.titol = titol;
    checkInvariante();
  }
}

