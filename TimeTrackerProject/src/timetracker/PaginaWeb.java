
package timetracker;
//Aquest arxiu mostra el codi font de la llibreria paginaweb.jar, i per tant
//no es fa servir directament. L'afegim per mostrar com esta feta la classe. 
//Per això està exclos del path de compilació (opció menu emergent ->
//Build path -> Exclude).
//

import html.Attribute;
import html.Tag;

import java.util.Collection;
import java.util.Iterator;

public class PaginaWeb {

  /**
   * @uml.property name="paginaWeb"
   */
  private Tag paginaWeb = new Tag("html");

  /**
   * @uml.property name="head"
   */
  private Tag head = new Tag("head");

  /**
   * @uml.property name="body"
   */
  private Tag body = new Tag("body");

  /**
   * Crea todo el documento html que es necesario. apra despues rellenarlo
   */
  public PaginaWeb() {
    Tag title = new Tag("title");
    title.add("Informe TimeTracker");
    head.add(title);
    paginaWeb.add(head);
    paginaWeb.add(body);
  }

  /**
   * Agrega la seccionde head a al fichero HTML.
   */
  public void afegeixHeader(String str, int mida, boolean centrar) {
    assert ( str != null ) : "PaginaWeb: str nulo";
    assert ( !str.isEmpty() ) : "PaginaWeb: str nulo";
    assert ( mida > 0 ) : "PaginaWeb: str nulo";
    // fa text h1, h2 ... h6
    if (mida >= 1 && mida <= 6) {
      Tag tag2 = new Tag("h" + (new Integer(mida)).toString());
      tag2.add(str);
      if (centrar) {
        tag2.addAttribute(new Attribute("style", "text-align: center;"));
      }
      body.add(tag2);
    }
  }

  /**
   * Agregar text al curpo html.
   */
  public void afegeixTextNormal(String str) {
    assert ( str != null ) : "PaginaWeb: str nulo";
    assert ( !str.isEmpty() ) : "PaginaWeb: str nulo";
    body.add(str);
  }

  public void afegeixSaltDeLinia() {
    body.add(new Tag("br", true));
  }

  /**
   * Apartir de los datos de las tablas genera el una tabla en HTML.
   */
  public void afegeixTaula(Collection taula, 
      boolean primeraFilaCapsalera,
      boolean primeraColumnaCapsalera) {
    assert ( taula != null ) : "PaginaWeb: taula nulo";
    // taula es una llista (files) de llistes (columnes), implementat com un
    // arraylist d'arraylists, encara que aqui per mes generalitat hi posem
    // el tipus generic collection

    /*
     * Exemple : taula amb capsalera a la primera fila
     * 
     * <table style= "text-align: left; width: 842px;" border="1" cellpadding="2" cellspacing="2"> 
     *     <tbody> 
     *       <tr>
     *         <th style="background-color: rgb(102, 255, 255);">No.</th>
     *         <th style="background-color: rgb(102, 255, 255);">Projecte</th>
     *         <th style="background-color: rgb(102, 255, 255);">Data d'inici</th>
     *         <th style="background-color: rgb(102, 255, 255);">Data final</th>
     *         <th style="background-color: rgb(102, 255, 255);">Temps total</th>
     *       </tr> 
     *       <tr> 
     *         <td style="background-color: rgb(204, 255, 255);">1</td>
     *         <td style="background-color: rgb(204, 255, 255);">P&agrave;gina web personal</td> 
     *         <td style="background-color: rgb(204, 255, 255);">15/11/2006, 19:00h</td> 
     *         <td style="background-color: rgb(204, 255, 255);">25/11/2006, 20:00h</td> 
     *         <td style="background-color: rgb(204, 255, 255);">25h 45m 0s</td> 
     *       </tr> 
     *     </tbody> 
     * </table>
     */
    Tag tag1 = new Tag("table");
    tag1.addAttribute(new Attribute("style", "text-align: left; width: 842px;"));
    tag1.addAttribute(new Attribute("border", "1"));
    tag1.addAttribute(new Attribute("cellpadding", "2"));
    tag1.addAttribute(new Attribute("cellspacing", "2"));

    Tag tbody = new Tag("tbody");
    // les cel.les de capsalera tenen fons en blau fosc
    Attribute estilTh = new Attribute("style", "background-color: rgb(102, 255, 255);");
    // les cel.les de dades, fons en blau clar
    Attribute estilTd = new Attribute("style", "background-color: rgb(204, 255, 255);");

    Iterator itFiles = taula.iterator();
    Iterator itColumnes = null;
    boolean primeraFila = true;
    while (itFiles.hasNext()) {
      Tag tr = new Tag("tr"); // cada fila de la taula      
      itColumnes = ((Collection) itFiles.next()).iterator();
      boolean primeraColumna = true;
      while (itColumnes.hasNext()) {
        if ( (primeraFila && primeraFilaCapsalera) 
            || (primeraColumna && primeraColumnaCapsalera) ) { // th en comptes de td
          Tag th = new Tag("th"); 
          th.addAttribute(estilTh);
          th.add(itColumnes.next().toString());
          tr.add(th);
        } else {
          Tag td = new Tag("td"); 
          td.addAttribute(estilTd); 
          td.add(itColumnes.next().toString());
          tr.add(td);
        }
        primeraColumna = false;
      }
      primeraFila = false;
      tbody.add(tr);
    }
    tag1.add(tbody);
    body.add(tag1);
  }

  /**
   * Agrega un separador en fromato HTML.
   */
  public void afegeixLiniaSeparacio() {
    Tag hr = new Tag("hr");
    hr.addAttribute(new Attribute("style", "width: 100%; height: 2px;"));
    // <hr style="width: 100%; height: 2px;">
    body.add(hr);
  }

  public void escriuPagina() {
    System.out.println(paginaWeb);
  }
  
  public String getPaginaWeb() {
    return this.paginaWeb.toString();
  }

}
