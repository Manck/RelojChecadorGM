package lectorentrada;

/**
 *
 * @author Francisco
 */
public class MainReloj {

    public static void main(String[] args) {
        LectorHuella ventanaLector = new LectorHuella();
        ventanaLector.start();
        ventanaLector.init();
        ventanaLector.FocusEnReloj();
        ventanaLector.RecuperarFocusJava();
    }
    
}
