
package timetracker;

import java.io.Serializable;

/** 
 * Retorna un identificador que no se repite, dando igual si es actividad, tarea o projecto.
 * @author Jose Master
 */

public class IdGenerator implements Serializable {

/**
 * @uml.property  name="id"
 */
  //ID que se aportara a los objetos que lo requieran
  private static int id = 0;
/** 
 * 
 * @uml.property name="instance"
 */
//Generamos aqui mismo la instancia para evitar que se instancien.
//mas de uno y asi no tener un constructor.
  private static IdGenerator instance = new IdGenerator();

  /** 
   * Devuelve un nuevo ID unico, cada vez que le pides uno.
   */
  public int newId() {
    int idPre = id;  
    id++;
    int idPost = id;
    assert (idPre == idPost - 1 ) : "Problema con identificadores";
    return id;
  }

  /** 
   * Substituimos el constructor por esta funcion, que nos da a todos la misma instancia.
   * sin multiplicarla (Singlenton).
   */
  public static IdGenerator getInstance() {
    return instance;
  }


}
