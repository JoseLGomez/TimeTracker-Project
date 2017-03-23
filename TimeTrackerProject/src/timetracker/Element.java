package timetracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase abstracta que da pie a los elementos que compondran la estructura de datos
 * de los informes. Es la clase elemento del patron Visitor que define los metodos accept
 * que toma un visitante como argumento, en este caso el formato del informe
 * @author Jose Master
 *
 */
public abstract class Element {
  /**
  * Logger para mostrar mensajes.
  */
  private static Logger logger = LoggerFactory.getLogger(Element.class);

  /**
   * Definicion metodo accept del visitante.
   */
  public abstract void accept(Format visit);

}
