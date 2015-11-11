package lectorentrada;

import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplateFactory;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;

/**
 *
 * @author Francisco
 */
public class LectorHuella {
    private final DPFPCapture capturer = DPFPGlobal.getCaptureFactory().createCapture();
    private final DPFPVerification verificator = DPFPGlobal.getVerificationFactory().createVerification();
    //Frame del programa
    JFrame frame = new JFrame();
    //Define si el programa se encuentra en el proceso de lectura de una huella
    boolean noLeeHuella = true;
    String[] huellasAVerificar;
    //Timer para levantar java automáticamente en caso de que pierda el focus y no se pueda recuperar.
    Timer timerEjecucionEnvio = new Timer();
    LectorHuella(){
        //Colocar la ventana del tamaño de la mitad vertical de la pantalla
        frame.setSize(new Dimension(4, 4));
        frame.setBackground(Color.WHITE);
        //Evitar que se pueda cerrar accidentalmente
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //Evitar que se pueda cambiar el tamaño
        frame.setResizable(false);
        //Colocar como no decorado para lograr máxima invisibilidad
        frame.setUndecorated(true);
        //Visualizar
        frame.setVisible(true);
        //Colocar titulo
        frame.setTitle("LectorHuella");
        
        //Colocar los eventos de cambio de focus
        WindowFocusListener listenerDeFocus = new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {}
            @Override
            public void windowLostFocus(WindowEvent e) {   
                if(noLeeHuella){
                    RecuperarFocusJava();
                }  
            }
        };

            huellasAVerificar = new String[10];
            huellasAVerificar[0] = "indiceDerecho";
            huellasAVerificar[1] = "indiceIzquierdo";
            huellasAVerificar[2] = "pulgarDerecho";
            huellasAVerificar[3] = "pulgarIzquierdo";
            huellasAVerificar[4] = "medioDerecho";
            huellasAVerificar[5] = "medioIzquierdo";
            huellasAVerificar[6] = "anularDerecho";
            huellasAVerificar[7] = "anularIzquierdo";
            huellasAVerificar[8] = "meniqueDerecho";
            huellasAVerificar[9] = "meniqueIzquierdo";
        //Añadir el listener de focus a frame
        frame.addWindowFocusListener(listenerDeFocus);
        timerEjecucionEnvio.schedule(colocarFocusEnJavaAutomaticamente, 0l, 1000);
    }
    
    //Coloca como iniciado el objeto de captura de la huella
    protected void start(){
        capturer.startCapture();	
    }
    
    //método inicial de la huella, contiene el listener y es quien llama al método de proceso
    protected void init(){
	capturer.addDataListener(new DPFPDataAdapter() {
	@Override public void dataAcquired(final DPFPDataEvent e) {
            //Decir que noLeeHuella es false
            noLeeHuella = false;
            ToastMessage mensajeProcesando = new ToastMessage("Procesando Información, Por Favor Espere.",3000);
            mensajeProcesando.setVisible(true);
            ArrayList resultadoBusqueda;
            String claveNomina;
            boolean SeHaEncontradoLaHuella = false;
            for(int i = 0; i < huellasAVerificar.length; i++){
                resultadoBusqueda = procesarHuella(e.getSample(), huellasAVerificar[i]);
                SeHaEncontradoLaHuella = (boolean) resultadoBusqueda.get(0);
                if(SeHaEncontradoLaHuella){
                    claveNomina = (String) resultadoBusqueda.get(1); 
                    //JOptionPane.showMessageDialog(frame, "Huella Encontrada " + claveNomina); 
                    try {
                        enviarDatoAReloj(claveNomina);
                    } catch (IOException ex) {
                        Logger.getLogger(LectorHuella.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (AWTException ex) {
                        Logger.getLogger(LectorHuella.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break; 
                }
            }
            if(!SeHaEncontradoLaHuella){
                ToastMessage mensajeHuellaNoEncontrada = new ToastMessage("Huella No Encontrada, Intente de Nuevo.",3000);
                mensajeHuellaNoEncontrada.setVisible(true);
            }
	}
	});    
    }
    
    //Método que recibe la huella para realizar la entrada y la procesa para realizar la entrada
    protected ArrayList procesarHuella(DPFPSample sample, String buscarHuella){
        //JOptionPane.showMessageDialog(frame, "Process");
        ClaseQuery busquedas = new ClaseQuery();
        DPFPFeatureSet features = extractFeatures(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);
        DPFPTemplateFactory templateFactory = DPFPGlobal.getTemplateFactory();
        
        ArrayList listaResultado = busquedas.obtenerTemplateSQL(buscarHuella);
        ArrayList<byte[]> listaHuellas = (ArrayList<byte[]>) listaResultado.get(0);
        ArrayList<String> listaClaveNomina = (ArrayList<String>) listaResultado.get(1);
        
        String claveEmpleado = "";
        boolean huellaVerificada = false;
        DPFPVerificationResult result;
        
        for (int i = 0; i < listaHuellas.size(); i++){
            if (features != null){                
                //Comparar las caracteristicas de la muestra con la base de datos hasta encontrar una equivalencia
                result = verificator.verify(features, templateFactory.createTemplate(listaHuellas.get(i)));
                huellaVerificada = result.isVerified();
                if(huellaVerificada){
                    claveEmpleado = listaClaveNomina.get(i);
                    break;
                }
            }
        }
       busquedas.limpiarListaHuellas();
       ArrayList resultadoHuella = new ArrayList();
       resultadoHuella.add(huellaVerificada);
       resultadoHuella.add(claveEmpleado);
       return resultadoHuella; 
    }
    
    //Extraer las carácteristicas de la huella que con la que se pretende realizar el ingreso
    protected DPFPFeatureSet extractFeatures(DPFPSample sample, DPFPDataPurpose purpose){
	DPFPFeatureExtraction extractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
	try {
	    return extractor.createFeatureSet(sample, purpose);
	} catch (DPFPImageQualityException e) {
	    return null;
	}
    }
        
    //Utilizar un robot para enviar la clave del empleado al reloj
    private void enviarDatoAReloj(String claveNomina) throws IOException, AWTException{
        simularClick(400, 300,100);
        FocusEnReloj();    
        //Esperar a que la ventana esté atrás 
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(LectorHuella.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Llamar a la clase para escribir
        RobotThread myRunnable = new RobotThread(claveNomina);
        new Thread(myRunnable).start();
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(LectorHuella.class.getName()).log(Level.SEVERE, null, ex);
        }
      
        try {
            Thread.sleep(500);
            noLeeHuella = true;
            RecuperarFocusJava();
        } catch (InterruptedException ex) {}
    }
    
    //Simular un click en la pantalla para asegurarse que se ha seleccionado la entrada de texto en el reloj
    public static void simularClick(int x, int y, int sleep) throws AWTException{
        //Robot para emular el movimiento del mouse
        Robot simuladorClick = new Robot();
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException ex) {
            Logger.getLogger(LectorHuella.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Mover el mouse a un punto de la pantalla
            simuladorClick.mouseMove(x, y); 
        //Dar tiempo al robot para mover el mouse al punto requerido
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {}
        //simular una presión del mouse y una liberación
        simuladorClick.mousePress(InputEvent.BUTTON1_MASK);
        simuladorClick.delay(100);
        simuladorClick.mouseRelease(InputEvent.BUTTON1_MASK);  
    }
    
    //Invocar un script vbs para colocar el focus en java
    public void RecuperarFocusJava(){
        try {
            Runtime.getRuntime().exec(new String[] {
            "wscript.exe",
            "C:\\Program Files\\LectorHuella\\CambiarFocusALector.vbs"
            });
        } catch (IOException ex) {
           JOptionPane.showMessageDialog(frame, "No se encuentra un archivo VBS; Reportar Incidente");
        }
    }
    
    //Invocar un script vbs para colocar el focus en el reloj
    public void FocusEnReloj(){
        try {
            Runtime.getRuntime().exec(new String[] {
            "wscript.exe",
            "C:\\Program Files\\LectorHuella\\CambiarFocusAReloj.vbs"
            });
        }catch( IOException e ) {
            JOptionPane.showMessageDialog(frame, "No se encuentra un archivo VBS; Reportar Incidente");
        }
    }
             
    //Proceso a ejecutar continuamente cada segundo para levantar el focus cada vez que se pierde
    TimerTask colocarFocusEnJavaAutomaticamente = new TimerTask () {
        @Override
        public void run () {
            //Cuando lee huella (o escribe en el reloj) no la levanta, ya que interrumpiría el proceso
            if(noLeeHuella){
                RecuperarFocusJava();
            }
        }
    };
}