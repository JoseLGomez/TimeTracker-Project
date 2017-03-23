package timetracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



/**
 * Clase que implementa un visitor concreto, en este caso el formato
 * de salida de informe en HTML.
 * @author Jose Luis Gomez, Eric jaen, Jesus Serrano
 *
 */
public class Html extends Format {
  /**
  * Logger para mostrar mensajes.
  */
  private static Logger logger = LoggerFactory.getLogger(Html.class);

  /**
   * @uml.property  name="paginaWeb"
   * @uml.associationEnd  multiplicity="(1 1)" aggregation="shared" 
   *                      inverse="html:timetracker.PaginaWeb"
   */
  private PaginaWeb paginaWeb = new timetracker.PaginaWeb();

  /**
   * Getter of the property <tt>paginaWeb</tt>
   * @return  Returns the paginaWeb.
   * @uml.property  name="paginaWeb"
   */
  public PaginaWeb getPaginaWeb() {
    return paginaWeb;
  }

  /**
   * Setter of the property <tt>paginaWeb</tt>
   * @param paginaWeb  The paginaWeb to set.
   * @uml.property  name="paginaWeb"
   */
  public void setPaginaWeb(PaginaWeb paginaWeb) {
    assert ( paginaWeb != null ) : "Html: PaginaWeb nulo";
    this.paginaWeb = paginaWeb;
  }

  

  public void visitSubtitle(Subtitle sub) {
    assert ( sub != null ) : "Html: Subtitulo nulo";
    this.paginaWeb.afegeixHeader(sub.getSubtitle(), 2, false);
  }

  

  public void visitTitle(Title title) {
    assert ( title != null ) : "Html: Titulo nulo";
    paginaWeb.afegeixHeader(title.getTitol(), 1, true);
  }

  
/**
 * Metodo que implementa un texto.
 */
  public void visitText(Text text) {
    assert ( text != null ) : "Html: Text nulo";
    this.paginaWeb.afegeixTextNormal(text.getText());
    this.paginaWeb.afegeixSaltDeLinia();
  }

  
  public void visitSeparator(Separator sep) {
    assert ( sep != null ) : "Html: separador nulo";
    this.paginaWeb.afegeixLiniaSeparacio();   
  }

  /**
   * Visita la tabla dada una tabla en HTML.
   */
  public void visitTable(Table table) {
    assert ( table != null ) : "Html: table nulo";
    if (table.getNcolumnes() == 2) {
      this.paginaWeb.afegeixTaula(table.getTaula(), true, true);
    } else {
      this.paginaWeb.afegeixTaula(table.getTaula(), true, false);
    }
  }

  public Html() {
    this.paginaWeb = new PaginaWeb();
  }
  
  /**
   * Huarda el fichero en raiz del proyecto dado un Save.
   */
  public void visitGuardar(Save guardar) {
    assert ( guardar != null ) : "Format: Nombre del Archivo nulo";
    String archivo = guardar.getName();
    //File fileName = new File(archivo + ".html");
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(archivo + ".html"));
      writer.write(this.paginaWeb.getPaginaWeb());
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (writer != null) {
          writer.close();
          logger.info(" fichero " + archivo + ".html creado");
        }
       
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
  }

}
