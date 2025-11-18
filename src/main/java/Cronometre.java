/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Date;

/** Implementa un cronómetre senzill.
 *
 * @author Joan Pons Tugores
 */
public class Cronometre {

    /** Instant inicial del periode que volem cronometrar   */
    Date inici;
    /** Instant final  del periode que volem cronometrar      */
    Date fi;

    /**Inicialitza els dos instants a null
     * 
     */
    public Cronometre() {
        inici = null;
        fi = null;
    }

    /**
     * Registre l'instant inicial i inicialitza el final
     */
    public void inicia() {
        inici = new Date();
        fi = null;
    }

    /**
     * Registra l'instant final
     */
    public void atura() {
        fi = new Date();
    }

    /**Calcula la durada de l'interval en milisegons
     * 
     * @return la durada de l'interval en milisegons
     */
    public long mostra() {
        long resultat = -1;
        if ((inici != null) && (fi != null)) {
            resultat = fi.getTime() - inici.getTime();
        }
        return resultat;
    }
}
