package timetracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase abstracta que implementa cada tipo de visitante, que dada una lista
 * de elementos que componen la estructura de un informe, estos llamaran a los
 *  metodos de los visitantes concretos para proceder de la manera adecuada.
 * @author Jose Luis Gomez, Eric jaen, Jesus Serrano
 *
 */
public abstract class Format {
  /**
  * Logger para mostrar mensajes.
  */
  private static Logger logger = LoggerFactory.getLogger(Format.class);

  /**
   * Visita el Subtitulo.
 */
  public void visitSubtitle(Subtitle sub) {
    assert ( sub != null ) : "Format: Subtitulo nulo";
  }

  /**
   * Visita el Titulo.
   */
  public void visitTitle(Title title) {
    assert ( title != null ) : "Format: Titulo nulo";
  }


  /**
   * Visita el Texto.
   */
  public void visitText(Text text) {
    assert ( text != null ) : "Format: Text nulo";
  }


  /**
   * Visita el SubtiSeparator.
   */
  public void visitSeparator(Separator sep) {
    assert ( sep != null ) : "Format: Separator nulo";
  }


  /**
   * Visita el Tabla.
   */
  public void visitTable(Table table) {
    assert ( table != null ) : "Format: Table nulo";
  }
  /**
   * Visita para guardar el archivo generado.
   */
  public void visitGuardar(Save save) {
    assert ( save != null ) : "Format: Nombre del Archivo nulo";
  }
  

}
