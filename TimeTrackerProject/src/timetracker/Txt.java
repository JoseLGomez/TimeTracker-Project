package timetracker;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Clase que implementa un visitor concreto, en este caso el formato.
 * de salida de informe en texto plano
 * @author Jose Luis Gomez, Eric jaen, Jesus Serrano
 *
 */
public class Txt extends Format {
  /**
  * Logger para mostrar mensajes.
  */
  private static Logger logger = LoggerFactory.getLogger(Txt.class);
  /**
   * Archivo txt generado.
   */
private String log = "";
  /**
  *Visita el subtitulo para imprimirlo.
  */
  public void visitSubtitle(Subtitle sub) {
    assert ( sub != null ) : "Txt: sub nulo";
    System.out.println(sub.getSubtitle());
    log += "\n" + sub.getSubtitle() + "\n";
  }
  

  
  /**
   * Vista el titulo para imprimir txt.
 */
  public void visitTitle(Title title) {
    assert ( title != null ) : "Txt: title nulo";
    System.out.println(title.getTitol());
    log += "\n" + title.getTitol() + "\n";
  }

  /**
   * Vista el texto para imprimir txt.
 */
  public void visitText(Text text) {
    assert ( text != null ) : "Txt: text nulo";
    System.out.println(text.getText());
    log += "\r\n" + text.getText() + "\r\n";
  }

  /**
   * Vista el separador para imprimir txt.
 */
  public void visitSeparator(Separator sep) {
    System.out.println(" ----------------------------------------------------------------------- ");
    log +="\r\n-----------------------------------------------------------------------\r\n";
  }

  /**
   * Visita la tabla y printa fichero plano.
   */

  public void visitTable(Table tab) {
    assert ( tab != null ) : "Txt: tab nulo";
    String taula = "";
    for (int i = 1; i <= tab.getNfiles();i++) {
      for (int j = 1; j <= tab.getNcolumnes(); j++) {
        taula += tab.getPosicio(i, j);
        taula += "\t \t";  
      }
      taula += System.getProperty("line.separator");
    }
    System.out.println(taula);
    log += "\n" + taula + "\n";
  }

  /**
   * constructor.
   */
  public Txt(){  
  }


  /**
  * metodo para guardar el fichero.
  */
  public void visitGuardar(Save guardar){
	  assert ( guardar != null ) : "Format: Nombre del Archivo nulo";
	    String archivo = guardar.getName();
	    //File fileName = new File(archivo + ".txt");
	    BufferedWriter writer = null;
	    try {
	      writer = new BufferedWriter(new FileWriter(archivo + ".txt"));
	      writer.write(this.log);
	    } catch (IOException e) {
	      e.printStackTrace();
	    } finally {
	      try {
	        if (writer != null) {
	          writer.close();
	          logger.info(" fichero " + archivo + ".txt creado");
	        }
	       
	      } catch (IOException e) {
	        e.printStackTrace();
	      }
	    }
  }
}
