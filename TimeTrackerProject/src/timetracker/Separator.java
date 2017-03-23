package timetracker;



/**
 * Clase que crea un separador para la estructura del informe.
 * @author Jose Luis Gomez, Eric Jaen, Jesus Serrano
 *
 */
public class Separator extends Element {


  /**
   * accept del separador.
   */
  public void accept(Format visitor) {
    assert ( visitor != null ) : "Separator: visitor nulo";
    visitor.visitSeparator(this);
  }

  public Separator(){
  }

}