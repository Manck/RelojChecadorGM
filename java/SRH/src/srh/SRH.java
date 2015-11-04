package srh;

import com.digitalpersona.onetouch.DPFPCaptureFeedback;
import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.DPFPTemplateFactory;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPImageQualityAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPImageQualityEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import net.miginfocom.swing.MigLayout;

public class SRH extends JFrame{
// <editor-fold defaultstate="collapsed" desc=" Globales ">
    /*Constantes*/

    public static final int REGISTRO_HUELLAS = 1111;
    public static final int IMPRESION_CREDENCIALES = 2222;
    public static final int CLAVENOMINA = 0;
    public static final int PATERNO = 1;
    public static final int MATERNO = 2;
    public static final int NOMBRE = 3;
    public static final int CHECATARJETA = 4;
    public static final int FECHAALTA = 5;
    public static final int DEPARTAMENTO = 6;
    public static final int PUESTO = 7;
    public static String TEMPLATE_PROPERTY = "template";
    /**/

    //Digital Persona Variables
    private final DPFPCapture capturer = DPFPGlobal.getCaptureFactory().createCapture();
    private JLabel picture = new JLabel();
    protected JLabel promptDeDigitalPersona = new JLabel();
    private final DPFPEnrollment enroller = DPFPGlobal.getEnrollmentFactory().createEnrollment();
    private DPFPTemplate template;
    DPFPTemplateFactory templateFactory = DPFPGlobal.getTemplateFactory();
    //Dimensiones de la pantalla
    Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    //Barra superior.
    JPanel BarraSuperior = new JPanel();
    //Barra de Menu con botones en la izquierda
    JPanel MenuIzquierda = new JPanel();
    //Elemento Central
    JPanel ElementoCentral = new JPanel();
    //Panel interior dentro de BarraSuperior para las herramientas de cada activiadad.
    JPanel BarraHerramientasSuperior = new JPanel();
    //Panel interior dentro de BarraSuperior para la barra de búsqueda, el isotipo y el nombre de la empresa
    JPanel BarraBusqueda = new JPanel();
    //Panel para el isotipo
    JPanel panelIsotipo = new JPanel();
    //Panel para el resto de barra busqueda; isotipo y busqueda
    JPanel BarraSuperiorInterna = new JPanel();
    //Panel para la información de la captura de la huella
    JPanel CapturaHuella = new JPanel();
    //Panel para titulo
    JPanel superiorCentral = new JPanel();
    //Panel para dibujar la huella
    JPanel ImagenHuella = new JPanel();
    //Componentes para la barra de busqueda
    JLabel JLB_IsotipoVidanta;
    JLabel JLB_Texto_Empresa;
    JLabel JLB_ClaveNomina = new JLabel();
    JTextField JTF_BarraBusqueda = new JTextField();
    JButton JBT_BuscarEmpleado = new JButton();

    //Componentes de la barra de menu izquierda 
    JButton JBT_RegistroHuellas = new JButton();
    JButton JBT_ImpresionCredenciales = new JButton();
    
    //Objeto de Conexión
    Connection Objeto_ConexionSQL;
    Connection Conexion_Local;
    
    //Variable de estado del sistema
    int estadoDelSistema = 0;

    //Valor de la clave Actual
    JLabel JLB_TituloClave = new JLabel();
    
    //ArrayList DatosEmpleadoHuella
    ArrayList<String> datosEmpleado = new ArrayList<>();
    
    //Etiquetas para mostrar información 
    JLabel JLB_NombreEmpleado = new JLabel();
    JLabel JLB_PuestoEmpleado = new JLabel();
    JLabel JLB_DepartamentoEmpleado = new JLabel();
    JLabel JLB_FechaIngreso = new JLabel();
    JLabel JLB_UtilizaChecador = new JLabel();
    
    //Labels para las imágenes de la captura de múltiples huellas
    JLabel JLB_DerPulgar = new JLabel();
    JLabel JLB_DerIndice = new JLabel();
    JLabel JLB_DerMedio = new JLabel();
    JLabel JLB_DerAnular = new JLabel();
    JLabel JLB_DerMenique = new JLabel();
    JLabel JLB_IzqMenique = new JLabel();
    JLabel JLB_IzqAnular = new JLabel();
    JLabel JLB_IzqMedio = new JLabel();
    JLabel JLB_IzqIndice = new JLabel();
    JLabel JLB_IzqPulgar = new JLabel();
  
    //String para colocar el dedo seleccionado para almacenar 
    String huellaSeleccionada;
    
    //Variable booleana para definir si las huellas individuales se encuentran habilitadas o deshabilitadas.
    boolean lecturaHuellaLista = false;
// </editor-fold>
    
    public SRH(){                                                   
                //Variables a ser usadas como parámetros.
                int labelBuscarEmpleadoGap = 20;
                int barraBusquedaGapLeft = 5;
                int gapTop = 15;
                int barraBusquedaW = 170;
                int barraBusquedaH = 30;
                //----------------------
                int BTNW = 20;
                int BTNH = 30;
                int BTGapLeft1 = 50;
                int BTGapLeft = 20;
                String nombreEmpresa = "The Grand Mayan";
                String tituloPrograma = "Sistema de Apoyo para Registro de Empleados. GrandMayan";
                
                //Propiedades del frame
                //this.setExtendedState(MAXIMIZED_BOTH);
                this.setResizable(false);
                
                this.add(new JToolBar(), BorderLayout.NORTH);
                this.setMinimumSize(new Dimension(1024, screenSize.height - 30));
                this.setPreferredSize(new Dimension(1024,screenSize.height - 30));
                this.setMaximumSize(new Dimension(1024,screenSize.height - 30));
                this.setDefaultCloseOperation(EXIT_ON_CLOSE);
                this.setTitle(tituloPrograma);
                
                //Inicializar el arraylist de los datos del empleado
                for(int i = 0; i < 8; i++){
                    datosEmpleado.add("");
                }

                //Cargar gráficos
                cargarEstructura();
                cargarElementosBarraBusqueda(labelBuscarEmpleadoGap, barraBusquedaGapLeft, gapTop, barraBusquedaW, barraBusquedaH, nombreEmpresa);
                cargarElementosMenuIzquierda();
                cargarElementosPanelHuella();
              
                //Propiedades de la barra de búsqueda
                JTF_BarraBusqueda.setEnabled(false);
                JBT_BuscarEmpleado.setEnabled(false);
                
                //Popiedades de etiqutas de huella
                promptDeDigitalPersona.setVisible(false);
                //Añadir Key Listener a JTF_BarraBusqueda para que solo ingrese números
                JTF_BarraBusqueda.addKeyListener(new KeyListener() {

                    @Override
                    public void keyTyped(KeyEvent e) {
                       if(!Character.isDigit(e.getKeyChar())){
                           e.consume();
                       }
                    }
                    @Override
                    public void keyPressed(KeyEvent e) {            
                    }
                    @Override
                    public void keyReleased(KeyEvent e) {          
                    }
                });
                
                //Añadir Mouse Listeners
                JLB_DerPulgar.addMouseListener(new MouseListener() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(lecturaHuellaLista){
                               huellaSeleccionada = "pulgarDerecho";
                               int IniciarRegistro = JOptionPane.showConfirmDialog(SRH.this, "Se Iniciará el Registro. "
                               + "En Caso de ya Existir, se Reemplazará. ¿Desea Continuar?", "Iniciar Registro", JOptionPane.YES_NO_OPTION);
                               //Si realmente quiere salir, entonces se limpia el objeto de registro, se actualiza la etiqueta y se buscan los nuevos datos
                               if (IniciarRegistro == JOptionPane.YES_OPTION){
                                    start();
                                    updateStatus();
                                    promptDeDigitalPersona.setVisible(true);
                                    picture.setIcon(null);
                                }
                        } else{
                            e.consume();
                        }
                    }
                    @Override
                    public void mousePressed(MouseEvent e) {}
                    @Override
                    public void mouseReleased(MouseEvent e) {}
                    @Override
                    public void mouseEntered(MouseEvent e) {}
                    @Override
                    public void mouseExited(MouseEvent e) {}
                });
                JLB_DerIndice.addMouseListener(new MouseListener() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(lecturaHuellaLista){
                            huellaSeleccionada = "indiceDerecho";
                            int IniciarRegistro = JOptionPane.showConfirmDialog(SRH.this, "Se Iniciará el Registro. "
                            + "En Caso de ya Existir, se Reemplazará. ¿Desea Continuar?", "Iniciar Registro", JOptionPane.YES_NO_OPTION);
                            //Si realmente quiere salir, entonces se limpia el objeto de registro, se actualiza la etiqueta y se buscan los nuevos datos
                            if (IniciarRegistro == JOptionPane.YES_OPTION){
                                start();
                                updateStatus();
                                promptDeDigitalPersona.setVisible(true);
                                picture.setIcon(null);
                            }
                        }else{
                            e.consume();
                        }
                    }
                    @Override
                    public void mousePressed(MouseEvent e) {}
                    @Override
                    public void mouseReleased(MouseEvent e) {}
                    @Override
                    public void mouseEntered(MouseEvent e) {}
                    @Override
                    public void mouseExited(MouseEvent e) {}
                });
                JLB_DerMedio.addMouseListener(new MouseListener() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(lecturaHuellaLista){
                        huellaSeleccionada = "medioDerecho";
                        int IniciarRegistro = JOptionPane.showConfirmDialog(SRH.this, "Se Iniciará el Registro. "
                        + "En Caso de ya Existir, se Reemplazará. ¿Desea Continuar?", "Iniciar Registro", JOptionPane.YES_NO_OPTION);
                        //Si realmente quiere salir, entonces se limpia el objeto de registro, se actualiza la etiqueta y se buscan los nuevos datos
                        if (IniciarRegistro == JOptionPane.YES_OPTION){
                            start();
                            updateStatus();
                            promptDeDigitalPersona.setVisible(true);
                            picture.setIcon(null);
                        }
                        }else{
                            e.consume();
                        }
                    }
                    @Override
                    public void mousePressed(MouseEvent e) {}
                    @Override
                    public void mouseReleased(MouseEvent e) {}
                    @Override
                    public void mouseEntered(MouseEvent e) {}
                    @Override
                    public void mouseExited(MouseEvent e) {}
                });
                JLB_DerAnular.addMouseListener(new MouseListener() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(lecturaHuellaLista){
                        huellaSeleccionada = "anularDerecho";
                        int IniciarRegistro = JOptionPane.showConfirmDialog(SRH.this, "Se Iniciará el Registro. "
                        + "En Caso de ya Existir, se Reemplazará. ¿Desea Continuar?", "Iniciar Registro", JOptionPane.YES_NO_OPTION);
                        //Si realmente quiere salir, entonces se limpia el objeto de registro, se actualiza la etiqueta y se buscan los nuevos datos
                        if (IniciarRegistro == JOptionPane.YES_OPTION){
                            start();
                            updateStatus();
                            promptDeDigitalPersona.setVisible(true);
                            picture.setIcon(null);
                        }
                        }else{
                            e.consume();
                        }
                    }
                    @Override
                    public void mousePressed(MouseEvent e) {}
                    @Override
                    public void mouseReleased(MouseEvent e) {}
                    @Override
                    public void mouseEntered(MouseEvent e) {}
                    @Override
                    public void mouseExited(MouseEvent e) {}
                });
                JLB_DerMenique.addMouseListener(new MouseListener() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(lecturaHuellaLista){
                        huellaSeleccionada = "meniqueDerecho";
                        int IniciarRegistro = JOptionPane.showConfirmDialog(SRH.this, "Se Iniciará el Registro. "
                        + "En Caso de ya Existir, se Reemplazará. ¿Desea Continuar?", "Iniciar Registro", JOptionPane.YES_NO_OPTION);
                        //Si realmente quiere salir, entonces se limpia el objeto de registro, se actualiza la etiqueta y se buscan los nuevos datos
                        if (IniciarRegistro == JOptionPane.YES_OPTION){
                            start();
                            updateStatus();
                            promptDeDigitalPersona.setVisible(true);
                            picture.setIcon(null);
                        }
                        }else{
                            e.consume();
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {}
                    @Override
                    public void mouseReleased(MouseEvent e) {}
                    @Override
                    public void mouseEntered(MouseEvent e) {}
                    @Override
                    public void mouseExited(MouseEvent e) {}
                });
                JLB_IzqMenique.addMouseListener(new MouseListener() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(lecturaHuellaLista){
                        huellaSeleccionada = "meniqueIzquierdo";
                        int IniciarRegistro = JOptionPane.showConfirmDialog(SRH.this, "Se Iniciará el Registro. "
                        + "En Caso de ya Existir, se Reemplazará. ¿Desea Continuar?", "Iniciar Registro", JOptionPane.YES_NO_OPTION);
                        //Si realmente quiere salir, entonces se limpia el objeto de registro, se actualiza la etiqueta y se buscan los nuevos datos
                        if (IniciarRegistro == JOptionPane.YES_OPTION){
                            start();
                            updateStatus();
                            promptDeDigitalPersona.setVisible(true);
                            picture.setIcon(null);
                        }
                        }else{
                            e.consume();
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {}
                    @Override
                    public void mouseReleased(MouseEvent e) {}
                    @Override
                    public void mouseEntered(MouseEvent e) {}
                    @Override
                    public void mouseExited(MouseEvent e) {}
                });
                JLB_IzqAnular.addMouseListener(new MouseListener() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(lecturaHuellaLista){
                        huellaSeleccionada = "anularIzquierdo";
                        int IniciarRegistro = JOptionPane.showConfirmDialog(SRH.this, "Se Iniciará el Registro. "
                        + "En Caso de ya Existir, se Reemplazará. ¿Desea Continuar?", "Iniciar Registro", JOptionPane.YES_NO_OPTION);
                        //Si realmente quiere salir, entonces se limpia el objeto de registro, se actualiza la etiqueta y se buscan los nuevos datos
                        if (IniciarRegistro == JOptionPane.YES_OPTION){
                            start();
                            updateStatus();
                            promptDeDigitalPersona.setVisible(true);
                            picture.setIcon(null);
                        }
                        }else{
                            e.consume();
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {}
                    @Override
                    public void mouseReleased(MouseEvent e) {}
                    @Override
                    public void mouseEntered(MouseEvent e) {}
                    @Override
                    public void mouseExited(MouseEvent e) {}
                });
                JLB_IzqMedio.addMouseListener(new MouseListener() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(lecturaHuellaLista){
                        huellaSeleccionada = "medioIzquierdo";
                        int IniciarRegistro = JOptionPane.showConfirmDialog(SRH.this, "Se Iniciará el Registro. "
                        + "En Caso de ya Existir, se Reemplazará. ¿Desea Continuar?", "Iniciar Registro", JOptionPane.YES_NO_OPTION);
                        //Si realmente quiere salir, entonces se limpia el objeto de registro, se actualiza la etiqueta y se buscan los nuevos datos
                        if (IniciarRegistro == JOptionPane.YES_OPTION){
                            start();
                            updateStatus();
                            promptDeDigitalPersona.setVisible(true);
                            picture.setIcon(null);
                        }
                        }else{
                            e.consume();
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {}
                    @Override
                    public void mouseReleased(MouseEvent e) {}
                    @Override
                    public void mouseEntered(MouseEvent e) {}
                    @Override
                    public void mouseExited(MouseEvent e) {}
                });
                JLB_IzqIndice.addMouseListener(new MouseListener() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(lecturaHuellaLista){
                        huellaSeleccionada = "indiceIzquierdo";
                        int IniciarRegistro = JOptionPane.showConfirmDialog(SRH.this, "Se Iniciará el Registro. "
                        + "En Caso de ya Existir, se Reemplazará. ¿Desea Continuar?", "Iniciar Registro", JOptionPane.YES_NO_OPTION);
                        //Si realmente quiere salir, entonces se limpia el objeto de registro, se actualiza la etiqueta y se buscan los nuevos datos
                        if (IniciarRegistro == JOptionPane.YES_OPTION){
                            start();
                            updateStatus();
                            promptDeDigitalPersona.setVisible(true);
                            picture.setIcon(null);
                        }
                        }else{
                            e.consume();
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {}

                    @Override
                    public void mouseReleased(MouseEvent e) {}

                    @Override
                    public void mouseEntered(MouseEvent e) {}

                    @Override
                    public void mouseExited(MouseEvent e) {}
                });
                JLB_IzqPulgar.addMouseListener(new MouseListener() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(lecturaHuellaLista){
                        huellaSeleccionada = "pulgarIzquierdo";
                        int IniciarRegistro = JOptionPane.showConfirmDialog(SRH.this, "Se Iniciará el Registro. "
                        + "En Caso de ya Existir, se Reemplazará. ¿Desea Continuar?", "Iniciar Registro", JOptionPane.YES_NO_OPTION);
                        //Si realmente quiere salir, entonces se limpia el objeto de registro, se actualiza la etiqueta y se buscan los nuevos datos
                        if (IniciarRegistro == JOptionPane.YES_OPTION){
                            start();
                            updateStatus();
                            promptDeDigitalPersona.setVisible(true);
                            picture.setIcon(null);
                        }
                        }else{
                            e.consume();
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {}

                    @Override
                    public void mouseReleased(MouseEvent e) {}

                    @Override
                    public void mouseEntered(MouseEvent e) {}

                    @Override
                    public void mouseExited(MouseEvent e) {}
                });
                 
                //Añadir Action Listeners
                JBT_RegistroHuellas.addActionListener((ActionEvent e) -> {/*<editor-fold>*/ 
                    //Colocar el estado del sistema como modo registro de huellas
                    estadoDelSistema = REGISTRO_HUELLAS;
                    //Mostrar u ocultar el panel de registro de huellas según sea el caso (toggle it)
                    if(CapturaHuella.isVisible()){
                        CapturaHuella.setVisible(false);
                        JTF_BarraBusqueda.setEnabled(false);
                        JBT_BuscarEmpleado.setEnabled(false);
                    }else{
                        CapturaHuella.setVisible(true);
                        JTF_BarraBusqueda.setEnabled(true);
                        JBT_BuscarEmpleado.setEnabled(true);
                   }    
                /*</editor-fold>*/});
                
                JBT_BuscarEmpleado.addActionListener((ActionEvent e) -> {/*<editor-fold>*/
                    
                    try {
                        
                        switch(estadoDelSistema){
                            case REGISTRO_HUELLAS:
                                buscadorClaveHuella();
                                break;
                            case IMPRESION_CREDENCIALES:
                                //claveEmpleado = buscarEmpleado();
                                break;
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(SRH.class.getName()).log(Level.SEVERE, null, ex);
                    }
                /*</editor-fold>*/});
                
                //Finalizar el Frame
                this.pack();
                this.setLocationRelativeTo(null);
                this.setVisible(true);
    }
    
    private void cargarEstructura(){
                //Barra de menus----------------------------------
                JMenuBar menuBar = new JMenuBar();
                //Menu Archivo
                JMenu menuArchivo = new JMenu("Archivo");
                menuBar.add(menuArchivo);
                //Menu Edición
                JMenu menuEdicion = new JMenu("Edición");
                menuBar.add(menuEdicion);
                //Menú Herramientas
                JMenu menuHerramientas = new JMenu("Herramientas");
                menuBar.add(menuHerramientas);
                //Menú Ayuda
                JMenu menuAyuda = new JMenu("Ayuda");
                menuBar.add(menuAyuda);
                //------------------------------------------------

                //Propiedades de BarraSuperior
                BarraSuperior.setOpaque(true);
                //BarraSuperior.setBackground(Color.blue); MOD
                BarraSuperior.setPreferredSize(new Dimension(900, 70));//hint at size
                BarraSuperior.setMaximumSize(new Dimension(screenSize.width, 70));//hint at size
                BarraSuperior.setLayout(new BorderLayout());
                //-------------------------------------------------------------------------------
                
                //Propiedades de barra de herramientas superior
               // BarraHerramientasSuperior.setBackground(Color.LIGHT_GRAY); MOD
                BarraHerramientasSuperior.setPreferredSize(new Dimension(400, 70));
                BarraHerramientasSuperior.setMaximumSize(new Dimension(400, 70));
                BarraHerramientasSuperior.setMinimumSize(new Dimension(400, 70));
                BarraHerramientasSuperior.setLayout(new MigLayout());
                //---------------------------------------------------------------------
               
                //Propiedades de la barra superior interna
                //BarraSuperiorInterna.setBackground(Color.red);
                BarraSuperiorInterna.setPreferredSize(new Dimension(screenSize.width - 400, 70));
                BarraSuperiorInterna.setMinimumSize(new Dimension(500, 70));
                BarraSuperiorInterna.setMaximumSize(new Dimension(screenSize.width - 400, 70));
                BarraSuperiorInterna.setLayout(new BorderLayout());
                
                //Propiedades de la barra de busqueda
               // BarraBusqueda.setBackground(Color.LIGHT_GRAY); MOD
                BarraBusqueda.setPreferredSize(new Dimension(screenSize.width - 470, 70));
                BarraBusqueda.setMinimumSize(new Dimension(screenSize.width - 470, 70));
                BarraBusqueda.setMaximumSize(new Dimension(screenSize.width - 470, 70));
                BarraBusqueda.setLayout(new MigLayout()); //"fillx", "[]20[][]"
                //Propiedades de elemento central
               // ElementoCentral.setBackground(Color.WHITE); MOD
                ElementoCentral.setOpaque(true);
                ElementoCentral.setLayout(new BorderLayout());
                ElementoCentral.setMaximumSize(new Dimension(screenSize.width - 70, this.getHeight()));
                ElementoCentral.setMinimumSize(new Dimension(screenSize.width - 470, this.getHeight()));
                
                //Propiedades del panel para mostrar huella
                //ImagenHuella.setBackground(Color.white); MOD
                ImagenHuella.setLayout(new BorderLayout());
                
                JPanel south = new JPanel();
                JPanel east = new JPanel();
                east.setMinimumSize(new Dimension(40, ElementoCentral.getHeight()));
                east.setPreferredSize(new Dimension(40, ElementoCentral.getHeight()));
                //east.setBackground(Color.white); MOD
                south.setMinimumSize(new Dimension(100, 400));
                south.setMaximumSize(new Dimension(100, 400));
                //south.setBackground(Color.WHITE); MOD
                //ImagenHuella.add(south, BorderLayout.SOUTH);
                ImagenHuella.add(east, BorderLayout.EAST);
                
                picture.setSize(new Dimension(ImagenHuella.getWidth(), ImagenHuella.getHeight()));
                ImagenHuella.add(picture, BorderLayout.CENTER);
                 
                //Propiedades de CapturaHuella
                CapturaHuella.setPreferredSize(new Dimension(ElementoCentral.getWidth(), ElementoCentral.getHeight()));
                CapturaHuella.setVisible(false);

                //Propiedades de panel superior central
                superiorCentral.setPreferredSize(new Dimension(ElementoCentral.getWidth(), 40));
                superiorCentral.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
                superiorCentral.setLayout(new GridBagLayout());
                superiorCentral.add(JLB_TituloClave);
                
                //Añadir elementos al elemento central
                ElementoCentral.add(CapturaHuella, BorderLayout.CENTER);
                ElementoCentral.add(superiorCentral, BorderLayout.NORTH);
                
                //Opciones del menú de la izquierda
                MenuIzquierda.setPreferredSize(new Dimension(70, this.getHeight()));
                MenuIzquierda.setMinimumSize(new Dimension(70, this.getHeight()));
                MenuIzquierda.setMaximumSize(new Dimension(70, this.getHeight()));
                //MenuIzquierda.setBackground(Color.DARK_GRAY); MOD
                MenuIzquierda.setLayout(new GridLayout(10,0, 0, 15));
                
                //Añadir elementos a la barra superior interna
                 
                addIcono();
                BarraSuperiorInterna.add(BarraBusqueda, BorderLayout.EAST);
                BarraSuperiorInterna.add(panelIsotipo, BorderLayout.WEST);
                
                //Añadir la barra de herraminetas superior y la barra de búsqueda a la barra superior.
                BarraSuperior.add(BarraHerramientasSuperior, BorderLayout.EAST);
                BarraSuperior.add(BarraSuperiorInterna, BorderLayout.WEST);                            
                
                //Cajas y páneles para acomodo
                
                //JPanel para el menú
                JPanel menuBox = new JPanel();
                menuBox.setPreferredSize(new Dimension(screenSize.width, 30));
                menuBox.setMaximumSize(new Dimension(screenSize.width, 30));
                menuBox.setMinimumSize(new Dimension(screenSize.width, 30));
                menuBox.setLayout(new FlowLayout(FlowLayout.LEFT));
                menuBox.add(menuBar);
                //------------------------------------------------------
                
                //Caja para la barra superior
                Box top = Box.createHorizontalBox();
                top.add(BarraSuperior);

                Box bottom = Box.createHorizontalBox(); 
                bottom.add(MenuIzquierda);
                bottom.add(ElementoCentral);
                //---------------------------------------------------------
                
                //Caja vertical y añadir elementos
                Box vert = Box.createVerticalBox();
                vert.add(menuBox);
                vert.add(top);
                vert.add(bottom);

                //Finalizar el frame añadiendo cajas
                this.add(vert);    
    }

    private void cargarElementosBarraBusqueda(int labelBuscarEmpleadoGap, int barraBusquedaGapLeft, int gapTop, 
            int barraBusquedaW, int barraBusquedaH, String nombreEmpresa){
        
        try {
           Image img = ImageIO.read(getClass().getResource("/Iconos/lupa16.png"));
           JBT_BuscarEmpleado.setIcon(new ImageIcon(img));
        } catch (IOException ex) {
        }
        
        //Colocar Texto en JLB_Texto_Empresa
        JLB_Texto_Empresa = new JLabel(nombreEmpresa);
        JLB_Texto_Empresa.setFont(new Font("Book Antiqua", Font.PLAIN, 20));
        JLB_ClaveNomina.setText("Clave Empleado: ");
        JLB_Texto_Empresa.setForeground(Color.BLACK);
               
        //Añadir elementos a la barra de búsqueda
        BarraBusqueda.add(JLB_Texto_Empresa, "gapleft 0, gaptop " + gapTop);
        BarraBusqueda.add(JLB_ClaveNomina, "gapleft" + labelBuscarEmpleadoGap + ", gaptop " + gapTop);
        BarraBusqueda.add(JTF_BarraBusqueda, "gapleft" + barraBusquedaGapLeft + ", w " + 
                          barraBusquedaW + ", h " + barraBusquedaH + ", gaptop " + gapTop);
        BarraBusqueda.add(JBT_BuscarEmpleado, "w 20, h 30, gaptop " + gapTop);
    }
    
    private void addIcono(){
        //Procesar el isotipo para convertirlo en un icono de cierto tamaño
        ImageIcon iconoVidanta = new ImageIcon(getClass().getResource("/Iconos/mayan.png"));
        Image imagenVidanta = iconoVidanta.getImage();
        BufferedImage bi = new BufferedImage(imagenVidanta.getWidth(null), imagenVidanta.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        g.drawImage(imagenVidanta, -3, -5, 64, 64, null);
        //Objeto Final
        ImageIcon iconoVidantaFinal = new ImageIcon(bi);
        //Colocar Icono en el JLabel 
        JLB_IsotipoVidanta = new JLabel(iconoVidantaFinal);
        
        panelIsotipo.setPreferredSize(new Dimension(70,70));
        panelIsotipo.setLayout(new MigLayout());
        //panelIsotipo.setBackground(Color.WHITE); MOD
        panelIsotipo.add(JLB_IsotipoVidanta, "gapleft 0");  
    }
    
    //Colocar una huella con el icono de huella ya registrada, es decir en azul.
    private void colocarHuellaComoRegistrada(JLabel HuellaRegistrada){
        ImageIcon huellaIcon = new ImageIcon(getClass().getResource("/Iconos/huellaMano.png"));
        Image huellaImg = huellaIcon.getImage();
        BufferedImage buffHuellaIzq = new BufferedImage(huellaImg.getWidth(null), huellaImg.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics graphMano = buffHuellaIzq.createGraphics();
        graphMano.drawImage(huellaImg, 0, 0, 70, 70, null);
        ImageIcon huellaIconFinal = new ImageIcon(buffHuellaIzq);
        HuellaRegistrada.setIcon(huellaIconFinal);
    }
    
    private void colocarIconosDefaultHuella(){
        ImageIcon huellaIcon = new ImageIcon(getClass().getResource("/Iconos/huellaManoOff.png"));
        Image huellaImg = huellaIcon.getImage();
        BufferedImage buffHuellaIzq = new BufferedImage(huellaImg.getWidth(null), huellaImg.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics graphMano = buffHuellaIzq.createGraphics();
        graphMano.drawImage(huellaImg, 0, 0, 70, 70, null);
        ImageIcon huellaIconFinal = new ImageIcon(buffHuellaIzq);
        
        JLB_IzqMenique.setIcon(huellaIconFinal);
        JLB_IzqAnular.setIcon(huellaIconFinal);
        JLB_IzqMedio.setIcon(huellaIconFinal);
        JLB_IzqIndice.setIcon(huellaIconFinal);
        JLB_IzqPulgar.setIcon(huellaIconFinal);
        
        JLB_DerMenique.setIcon(huellaIconFinal);
        JLB_DerAnular.setIcon(huellaIconFinal);
        JLB_DerMedio.setIcon(huellaIconFinal);
        JLB_DerIndice.setIcon(huellaIconFinal);
        JLB_DerPulgar.setIcon(huellaIconFinal);
    }
    
    private void PropiedadesHuellas(){
                       
        JLB_IzqMenique.setVerticalTextPosition(JLabel.BOTTOM);
        JLB_IzqMenique.setHorizontalTextPosition(JLabel.CENTER);
        JLB_IzqMenique.setText("Meñique");

        JLB_IzqAnular.setVerticalTextPosition(JLabel.BOTTOM);
        JLB_IzqAnular.setHorizontalTextPosition(JLabel.CENTER);
        JLB_IzqAnular.setText("Anular");

        JLB_IzqMedio.setVerticalTextPosition(JLabel.BOTTOM);
        JLB_IzqMedio.setHorizontalTextPosition(JLabel.CENTER);
        JLB_IzqMedio.setText("Medio");

        JLB_IzqIndice.setVerticalTextPosition(JLabel.BOTTOM);
        JLB_IzqIndice.setHorizontalTextPosition(JLabel.CENTER);
        JLB_IzqIndice.setText("Indice");

        JLB_IzqPulgar.setVerticalTextPosition(JLabel.BOTTOM);
        JLB_IzqPulgar.setHorizontalTextPosition(JLabel.CENTER);
        JLB_IzqPulgar.setText("Pulgar");

        JLB_DerPulgar.setVerticalTextPosition(JLabel.BOTTOM);
        JLB_DerPulgar.setHorizontalTextPosition(JLabel.CENTER);
        JLB_DerPulgar.setText("Pulgar");

        JLB_DerIndice.setVerticalTextPosition(JLabel.BOTTOM);
        JLB_DerIndice.setHorizontalTextPosition(JLabel.CENTER);
        JLB_DerIndice.setText("Indice");

        JLB_DerMedio.setVerticalTextPosition(JLabel.BOTTOM);
        JLB_DerMedio.setHorizontalTextPosition(JLabel.CENTER);
        JLB_DerMedio.setText("Medio");

        JLB_DerAnular.setVerticalTextPosition(JLabel.BOTTOM);
        JLB_DerAnular.setHorizontalTextPosition(JLabel.CENTER);
        JLB_DerAnular.setText("Anular");

        JLB_DerMenique.setVerticalTextPosition(JLabel.BOTTOM);
        JLB_DerMenique.setHorizontalTextPosition(JLabel.CENTER);
        JLB_DerMenique.setText("Meñique");
    }
    
    private void cargarElementosMenuIzquierda(){
       
        try {
          Image img = ImageIO.read(getClass().getResource("/Iconos/menuHuella.png"));
          JBT_RegistroHuellas.setIcon(new ImageIcon(img));
        } catch (IOException ex) {
        }
        try {
          Image img = ImageIO.read(getClass().getResource("/Iconos/identificacionID.png"));
         JBT_ImpresionCredenciales.setIcon(new ImageIcon(img));
       } catch (IOException ex) {
       }
       MenuIzquierda.add(JBT_RegistroHuellas);
       MenuIzquierda.add(JBT_ImpresionCredenciales);    

    }
    
    public static void main(String[] args){
            
        try {
            // select Look and Feel
            UIManager.setLookAndFeel("com.jtattoo.plaf.fast.FastLookAndFeel");
            // start application
            SRH sr = new SRH();
            sr.init();
            sr.start();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private ArrayList<String> buscarEmpleado() throws Exception{
        //Obtener la clave de empleado del campo
        String ClaveEmpleado =  JTF_BarraBusqueda.getText();
        //ArrayList DatosEmpleadoHuella
        ArrayList<String> arrayListParaRetorno = new ArrayList<>();
        /******************Buscar la clave del empleado en la base de datos********************/
        String comandoBusquedaDatosSQL = "SELECT n.Clavenomina, LTRIM(RTRIM(n.Paterno)), LTRIM(RTRIM(n.Materno)), LTRIM(RTRIM(n.Nombre)),"
                                       + " n.ChecaTarjeta, n.FechaAlta, c.Nombre, p.Nombre FROM " +
                                       "RHNomEmpleados n, RHNomPuestos p, RHNomDepartamentos c WHERE "
                                       + "n.ClaveNomina = ? AND n.Puesto = p.Puesto AND " +
                                       "n.Departamento = c.Departamento";
  
        //Crear Conexión con la base de datos
        Objeto_ConexionSQL = Conexion_Con_Servidor_SQL.createConnection();
        try {
            //Objetos para buscar los datos del empleado
            PreparedStatement busquedaClave = Objeto_ConexionSQL.prepareStatement(comandoBusquedaDatosSQL);
            busquedaClave.setString(1, ClaveEmpleado);
            ResultSet resultadoBusquedaClave =  busquedaClave.executeQuery();

             if(resultadoBusquedaClave.next()){
                arrayListParaRetorno.add(resultadoBusquedaClave.getString(1));
                arrayListParaRetorno.add(resultadoBusquedaClave.getString(2));
                arrayListParaRetorno.add(resultadoBusquedaClave.getString(3));
                arrayListParaRetorno.add(resultadoBusquedaClave.getString(4));
                arrayListParaRetorno.add(resultadoBusquedaClave.getString(5));
                arrayListParaRetorno.add(resultadoBusquedaClave.getString(6));
                arrayListParaRetorno.add(resultadoBusquedaClave.getString(7));
                arrayListParaRetorno.add(resultadoBusquedaClave.getString(8));   
                //Habilitar la lectura de huellas              
                promptDeDigitalPersona.setVisible(false);
                lecturaHuellaLista = true;
                //Colocar el icono default de las huellas
                colocarIconosDefaultHuella();
            }else{
                 JOptionPane.showMessageDialog(this, "El Empleado que Búsca No Se Encuentra Registrado.");
             }

        } catch (SQLException ex) {
            Logger.getLogger(SRH.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Limpiar el campo de búsqueda
        JTF_BarraBusqueda.setText("");    
        return arrayListParaRetorno;
    }
    
    private void colocarIconoHuella() throws Exception{
        System.out.println("colocarIconoHuella");
	//Comando de búsqueda para la huella 
        String comandoBusquedaHuellaSQL = "SELECT indiceDerecho, medioDerecho, anularDerecho, meniqueDerecho, pulgarDerecho, "
                                        + "indiceIzquierdo, medioIzquierdo, anularIzquierdo, meniqueIzquierdo, pulgarIzquierdo"
                                        + " FROM Huella WHERE ClaveNomina = ?";
        Objeto_ConexionSQL = Conexion_Con_Servidor_SQL.createConnection();
        try {
        //Objetos para buscar la huella del empleado 
        PreparedStatement busquedaHuella = Objeto_ConexionSQL.prepareStatement(comandoBusquedaHuellaSQL);
        busquedaHuella.setString(1, datosEmpleado.get(CLAVENOMINA));
        ResultSet resultadoBusquedaHuella = busquedaHuella.executeQuery();

            if(resultadoBusquedaHuella.next()){
                ImageIcon iconoHuella = new ImageIcon(getClass().getResource("/Iconos/huellaDefault.png"));
                Image imagenHuella = iconoHuella.getImage();
                drawPicture(imagenHuella);
                promptDeDigitalPersona.setVisible(false); 
                if(resultadoBusquedaHuella.getBinaryStream(1) != null){
		        colocarHuellaComoRegistrada(JLB_DerIndice);
		        JLB_DerIndice.repaint();
		    }
		
		    if(resultadoBusquedaHuella.getBinaryStream(2) != null){
		        colocarHuellaComoRegistrada(JLB_DerMedio);
		        JLB_DerIndice.repaint();
		    }
		
		    if(resultadoBusquedaHuella.getBinaryStream(3) != null){
		        colocarHuellaComoRegistrada(JLB_DerAnular);
		        JLB_DerIndice.repaint();
		    }
		
		    if(resultadoBusquedaHuella.getBinaryStream(4) != null){
		        colocarHuellaComoRegistrada(JLB_DerMenique);
		        JLB_DerIndice.repaint();
		    }
		
		    if(resultadoBusquedaHuella.getBinaryStream(5) != null){
		        colocarHuellaComoRegistrada(JLB_DerPulgar);
		        JLB_DerIndice.repaint();
		    }
		
		    if(resultadoBusquedaHuella.getBinaryStream(6) != null){
		        colocarHuellaComoRegistrada(JLB_IzqIndice);
		        JLB_IzqIndice.repaint();
		    }
		
		    if(resultadoBusquedaHuella.getBinaryStream(7) != null){
		        colocarHuellaComoRegistrada(JLB_IzqMedio);
		        JLB_IzqMedio.repaint();
		    }
		
		    if(resultadoBusquedaHuella.getBinaryStream(8) != null){
		        colocarHuellaComoRegistrada(JLB_IzqAnular);
		        JLB_IzqAnular.repaint();
		    }
		
		    if(resultadoBusquedaHuella.getBinaryStream(9) != null){
		        colocarHuellaComoRegistrada(JLB_IzqMenique);
		        JLB_IzqMenique.repaint();
		    }
		
		    if(resultadoBusquedaHuella.getBinaryStream(10) != null){
		        colocarHuellaComoRegistrada(JLB_IzqPulgar);
		        JLB_IzqPulgar.repaint();
                    }
            }    

        } catch (SQLException ex) {
            Logger.getLogger(SRH.class.getName()).log(Level.SEVERE, null, ex);
        }
}  
    
    private void cargarElementosPanelHuella(){
        
        //Paneles divisorios para la capturaHuella
        JPanel HuellaIzquierda = new JPanel();
        JPanel HuellaDerecha = new JPanel();
        //Panel para mostrar la huella capturada
      
        //Panel para colocar en coordenadas
        JPanel HuellaTitulo = new JPanel();
        JPanel HuellaWest = new JPanel();
        JPanel HuellaEast = new JPanel();
        
        HuellaWest.setPreferredSize(new Dimension(50, ElementoCentral.getHeight()-140));
        HuellaEast.setPreferredSize(new Dimension(50, ElementoCentral.getHeight()-140));
        HuellaTitulo.setPreferredSize(new Dimension(HuellaDerecha.getWidth(), 30));
        
        renombrarEtiquetas();

        JPanel PanelParaEtiquetas = new JPanel();
        PanelParaEtiquetas.setLayout(new MigLayout());
        PanelParaEtiquetas.setPreferredSize(new Dimension(HuellaIzquierda.getWidth(), HuellaIzquierda.getHeight() / 2));
        
        JPanel PanelSeleccionDedo = new JPanel();
        PanelSeleccionDedo.setPreferredSize(new Dimension(HuellaIzquierda.getWidth(), HuellaIzquierda.getHeight() / 2));
        
        //Propiedades de HuellaIzquierda
        HuellaIzquierda.setPreferredSize(new Dimension(ElementoCentral.getWidth() / 2, ElementoCentral.getHeight()-140 ));
        HuellaIzquierda.setMinimumSize(new Dimension(ElementoCentral.getWidth() / 2, ElementoCentral.getHeight()-140 ));
        HuellaIzquierda.setMaximumSize(new Dimension(ElementoCentral.getWidth() / 2, ElementoCentral.getHeight()-140 ));
        HuellaIzquierda.setBorder(new EtchedBorder(EtchedBorder.LOWERED)); 
        
        HuellaIzquierda.setLayout(new BoxLayout(HuellaIzquierda, BoxLayout.Y_AXIS));
 
        //Cargar elementos a HuellaIzquierda
        PanelParaEtiquetas.add(JLB_NombreEmpleado, "gaptop 30, gapleft 30, wrap");
        PanelParaEtiquetas.add(JLB_DepartamentoEmpleado, "gaptop 30, gapleft 30, wrap");
        PanelParaEtiquetas.add(JLB_PuestoEmpleado, "gaptop 30, gapleft 30, wrap");
        PanelParaEtiquetas.add(JLB_FechaIngreso, "gaptop 30, gapleft 30, wrap" );
        PanelParaEtiquetas.add(JLB_UtilizaChecador, "gaptop 30, gapleft 30, wrap");
        
        //Colocar las imágenes (Labels) para la captura de múltiples label.
        PanelSeleccionDedo.setLayout(new MigLayout());
        //Propiedades de los label para seleccionar huella
        PropiedadesHuellas();
        //Cargar los iconos default de los label para las huellas
        colocarIconosDefaultHuella();
        
        PanelSeleccionDedo.add(new JLabel("Mano Izquierda"), "gapleft 30, wrap");
        PanelSeleccionDedo.add(JLB_IzqIndice, "gapleft 20, gaptop 20");
        PanelSeleccionDedo.add(JLB_IzqMedio, "gapleft 10, gaptop 20");
        PanelSeleccionDedo.add(JLB_IzqAnular, "gapleft 10 , gaptop 20");
        PanelSeleccionDedo.add(JLB_IzqMenique, "gapleft 10,  gaptop 20");
        PanelSeleccionDedo.add(JLB_IzqPulgar, "gapleft 10, gaptop 20, wrap");
        
        PanelSeleccionDedo.add(new JLabel("Mano Derecha"), "gapleft 30, gaptop 20, wrap");
        PanelSeleccionDedo.add(JLB_DerIndice, "gapleft 20, gaptop 20");
        PanelSeleccionDedo.add(JLB_DerMedio, "gapleft 10, gaptop 20");
        PanelSeleccionDedo.add(JLB_DerAnular, "gapleft 10, gaptop 20");
        PanelSeleccionDedo.add(JLB_DerMenique, "gapleft 10, gaptop 20");
        PanelSeleccionDedo.add(JLB_DerPulgar, "gapleft 10,gaptop 20");
        
        HuellaIzquierda.add(PanelParaEtiquetas);
        HuellaIzquierda.add(PanelSeleccionDedo);
        //BUSCAR LA PROPORCIÓN CORRECTA PARA AÑADIR EL FILLER, EN BASE A LA ALTURA DE HUELLAIZQUIERDA, 
        Dimension minSize = new Dimension(HuellaIzquierda.getWidth(), 100);
        Dimension prefSize = new Dimension(HuellaIzquierda.getWidth(), 100);
        Dimension maxSize = new Dimension(HuellaIzquierda.getWidth(), 100);
        HuellaIzquierda.add(new Box.Filler(minSize, prefSize, maxSize));
        
        //Propiedades de huella derecha
        HuellaDerecha.setPreferredSize(new Dimension(ElementoCentral.getWidth() / 2, ElementoCentral.getHeight()-140 ));
        HuellaDerecha.setMaximumSize(new Dimension(ElementoCentral.getWidth() / 2, ElementoCentral.getHeight()-140 ));
        HuellaDerecha.setMinimumSize(new Dimension(ElementoCentral.getWidth() / 2, ElementoCentral.getHeight()-140 ));
        HuellaDerecha.setBorder(new EtchedBorder(EtchedBorder.LOWERED)); 
        
        //Añadir componentes a CapturaHuella
        CapturaHuella.setLayout(new GridLayout(0,2)); 
        CapturaHuella.add(HuellaIzquierda);
        CapturaHuella.add(HuellaDerecha); 
        
        //Cargar elementos a Huella Titulo
        HuellaTitulo.setLayout(new MigLayout());
        HuellaTitulo.add(promptDeDigitalPersona, "dock west, gapleft 60");

        //Añadir componentes a HuellaDerecha
        HuellaDerecha.setLayout(new BorderLayout());
        HuellaDerecha.add(HuellaTitulo, BorderLayout.NORTH);
        HuellaDerecha.add(HuellaEast, BorderLayout.EAST);
        HuellaDerecha.add(HuellaWest, BorderLayout.WEST);
        //HuellaDerecha.add(HuellaSouth, BorderLayout.SOUTH);
        HuellaDerecha.add(ImagenHuella, BorderLayout.CENTER);
    }
    
    private void renombrarEtiquetas(){
                //Datos en HuellaIzquierda
        JLB_NombreEmpleado.setText("Nombre:  " + datosEmpleado.get(NOMBRE) + " "+ datosEmpleado.get(PATERNO) + " " + datosEmpleado.get(MATERNO));
        JLB_PuestoEmpleado.setText("Puesto:  " + datosEmpleado.get(PUESTO));
        JLB_DepartamentoEmpleado.setText("Departamento:  " + datosEmpleado.get(DEPARTAMENTO));
        JLB_FechaIngreso.setText("Fecha de Ingreso:  "+ datosEmpleado.get(FECHAALTA));
        JLB_UtilizaChecador.setText("Checa Entrada:  " + datosEmpleado.get(CHECATARJETA));
        JLB_TituloClave.setText("Número de Empleado: "+datosEmpleado.get(0));
    }
  
    protected void init(){
		capturer.addDataListener(new DPFPDataAdapter() {
			@Override public void dataAcquired(final DPFPDataEvent e) {
				SwingUtilities.invokeLater(new Runnable() {	public void run() {
                                    try {
                                        //JOptionPane.showMessageDialog(getParent(),"The fingerprint sample was captured.");
                                        process(e.getSample());
                                    } catch (SQLException ex) {
                                        Logger.getLogger(SRH.class.getName()).log(Level.SEVERE, null, ex);
                                    }
				}});
			}
		});
		capturer.addReaderStatusListener(new DPFPReaderStatusAdapter() {
			@Override public void readerConnected(final DPFPReaderStatusEvent e) {
				SwingUtilities.invokeLater(new Runnable() {	public void run() {
				}});
			}
			@Override public void readerDisconnected(final DPFPReaderStatusEvent e) {
				SwingUtilities.invokeLater(new Runnable() {	public void run() {
				}});
			}
		});
		capturer.addSensorListener(new DPFPSensorAdapter() {
			@Override public void fingerTouched(final DPFPSensorEvent e) {
				SwingUtilities.invokeLater(new Runnable() {	public void run() {
				}});
			}
			@Override public void fingerGone(final DPFPSensorEvent e) {
				SwingUtilities.invokeLater(new Runnable() {	public void run() {
				}});
			}
		});
		capturer.addImageQualityListener(new DPFPImageQualityAdapter() {
			@Override public void onImageQuality(final DPFPImageQualityEvent e) {
				SwingUtilities.invokeLater(new Runnable() {	public void run() {
					if (e.getFeedback().equals(DPFPCaptureFeedback.CAPTURE_FEEDBACK_GOOD))
						JOptionPane.showMessageDialog(getParent(),"The quality of the fingerprint sample is good.");
					else
						JOptionPane.showMessageDialog(getParent(),"The quality of the fingerprint sample is poor.");
				}});
			}
		});
                updateStatus();
	}
    
    protected void start(){
		capturer.startCapture();
	}

    protected void stop(){
		capturer.stopCapture();
	}
    
    protected void process(DPFPSample sample) throws SQLException{
	    // Draw fingerprint sample image.
	    drawPicture(convertSampleToBitmap(sample));
            
            DPFPFeatureSet features = extractFeatures(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

		// Check quality of the sample and add to enroller if it's good
		if (features != null) try
		{
			JOptionPane.showMessageDialog(this,"Correcto. Capturar Siguiente Muestra.");
			enroller.addFeatures(features);		// Add feature set to template.
		}
		catch (DPFPImageQualityException ex) { }
		finally {
			updateStatus();

			// Check if template has been created.
			switch(enroller.getTemplateStatus())
			{
				case TEMPLATE_STATUS_READY:	// report success and stop capturing
					stop();
                                        /*Aqui almacenar la huella en las base de datos*/
                                         AlmacenarHuellaEnDB(); 
                                         //AlmacenarHuellaEnDBLocal();
                                         //Ocultar el promt de digital persona
                                         promptDeDigitalPersona.setVisible(false);
                        
                                         try {
                                             colocarIconoHuella();
                                         } catch (Exception ex) {
                                             Logger.getLogger(SRH.class.getName()).log(Level.SEVERE, null, ex);
                                         }
                        
                                         enroller.clear();
					 updateStatus();
                                         stop();
                                         
                                         JOptionPane.showMessageDialog(this,"Registro Correcto.");
					break;

				case TEMPLATE_STATUS_FAILED:	// report failure and restart capturing
					enroller.clear();
					stop();
					updateStatus();
					((SRH)getOwner()).setTemplate(null);
					JOptionPane.showMessageDialog(this, "Ha Ocurrido un Error, Repetir Proceso.", "Fingerprint Enrollment", JOptionPane.ERROR_MESSAGE);
					start();
					break;
			}
		}
    }
    
    private void AlmacenarHuellaEnDB() throws SQLException{              
        //Insertar el template generado por el objeto enroller en la base de datos utilizando ByteArrayInputStream y el método serialize de DigitalPersona           
        
        String buscarExistenciaClave = "SELECT ClaveNomina FROM Huella WHERE ClaveNomina = ?";
        PreparedStatement buscarExistenciaDeRegistro = Objeto_ConexionSQL.prepareStatement(buscarExistenciaClave);
        buscarExistenciaDeRegistro.setString(1, datosEmpleado.get(CLAVENOMINA));
        ResultSet resultadoBusquedaClave = buscarExistenciaDeRegistro.executeQuery();
        
        if(resultadoBusquedaClave.next()){
            String actualizarHuella = "UPDATE Huella SET " + huellaSeleccionada + " = ? WHERE ClaveNomina = ?";
            PreparedStatement datosActualizarHuella = Objeto_ConexionSQL.prepareStatement(actualizarHuella);
            datosActualizarHuella.setBinaryStream(1, new ByteArrayInputStream(enroller.getTemplate().serialize()), enroller.getTemplate().serialize().length);
            datosActualizarHuella.setString(2, datosEmpleado.get(CLAVENOMINA));
            datosActualizarHuella.executeUpdate();
        }else{
            String cadenaSQL = "INSERT INTO Huella (ClaveNomina, " + huellaSeleccionada + ") VALUES(?,?)";
            PreparedStatement insertarHuella = Objeto_ConexionSQL.prepareStatement(cadenaSQL);
            insertarHuella.setString(1, datosEmpleado.get(CLAVENOMINA));
            insertarHuella.setBinaryStream(2, new ByteArrayInputStream(enroller.getTemplate().serialize()), enroller.getTemplate().serialize().length);
            insertarHuella.executeUpdate();
        }
    }
    
    private void AlmacenarHuellaEnDBLocal(){              
        try {
            //Insertar el template generado por el objeto enroller en la base de datos utilizando ByteArrayInputStream y el método serialize de DigitalPersona
            try {
                //Insertar el template generado por el objeto enroller en la base de datos utilizando ByteArrayInputStream y el método serialize de DigitalPersona
                Conexion_Local = ConexionLocal.createConnection();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error con conexion BD Local");
            }
            String buscarExistenciaClave = "SELECT ClaveNomina FROM Huella WHERE ClaveNomina = ?";
            PreparedStatement buscarExistenciaDeRegistro = Conexion_Local.prepareStatement(buscarExistenciaClave);
            buscarExistenciaDeRegistro.setString(1, datosEmpleado.get(CLAVENOMINA));
            ResultSet resultadoBusquedaClave = buscarExistenciaDeRegistro.executeQuery();
            
            if(resultadoBusquedaClave.next()){
                String actualizarHuella = "UPDATE Huella SET " + huellaSeleccionada + " = ? WHERE ClaveNomina = ?";
                PreparedStatement datosActualizarHuella = Conexion_Local.prepareStatement(actualizarHuella);
                datosActualizarHuella.setBinaryStream(1, new ByteArrayInputStream(enroller.getTemplate().serialize()), enroller.getTemplate().serialize().length);
                datosActualizarHuella.setString(2, datosEmpleado.get(CLAVENOMINA));
                datosActualizarHuella.executeUpdate();
            }else{
                String cadenaSQL = "INSERT INTO Huella (ClaveNomina, " + huellaSeleccionada + ") VALUES(?,?)";
                PreparedStatement insertarHuella = Conexion_Local.prepareStatement(cadenaSQL);
                insertarHuella.setString(1, datosEmpleado.get(CLAVENOMINA));
                insertarHuella.setBinaryStream(2, new ByteArrayInputStream(enroller.getTemplate().serialize()), enroller.getTemplate().serialize().length);
                insertarHuella.executeUpdate();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error con los PS");
        }
    }
        
    public void drawPicture(Image image) {
		picture.setIcon(new ImageIcon(
		image.getScaledInstance(picture.getWidth(), picture.getHeight(), Image.SCALE_SMOOTH))); 
                picture.setAlignmentX(0);
                picture.setAlignmentY(0);
    }
    
    protected Image convertSampleToBitmap(DPFPSample sample) {
	    return DPFPGlobal.getSampleConversionFactory().createImage(sample);
    }

    protected DPFPFeatureSet extractFeatures(DPFPSample sample, DPFPDataPurpose purpose){
		DPFPFeatureExtraction extractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
		try {
			return extractor.createFeatureSet(sample, purpose);
		} catch (DPFPImageQualityException e) {
			return null;
		}
    }
    
    private void updateStatus(){
		//Muestra la cantidad de lecturas restantes 
		promptDeDigitalPersona.setText(String.format("Lecturas Restantes: %1$s", enroller.getFeaturesNeeded()));
	}
    
    //Método que llama el action listener cuando se encuentra dentro de huellas y se selecciona buscar.
    private void buscadorClaveHuella() throws Exception{
        //Si ya se está ejecutando un registro, entonces manda una advertencia para confirmar la salida
        if(enroller.getFeaturesNeeded() < 4){
            int reply = JOptionPane.showConfirmDialog(this, "Un registro está en proceso. ¿Desea Cancelarlo?", "Confirmar Salida", JOptionPane.YES_NO_OPTION);
            //Si realmente quiere salir, entonces se limpia el objeto de registro, se actualiza la etiqueta y se buscan los nuevos datos
            if (reply == JOptionPane.YES_OPTION){
                datosEmpleado = buscarEmpleado(); //COLOCAR ESTO MISMO EN EL ELSE, ES DECIR SI ES DESDE EL INICIO.    
                colocarIconoHuella();
                enroller.clear();
                stop();
                updateStatus();
                renombrarEtiquetas();
            }
            //Si no se está realizando un registro, procede con normalidad
        }else{
            datosEmpleado = buscarEmpleado();
            colocarIconoHuella();
            //promptDeDigitalPersona.setVisible(true);
            capturer.stopCapture();
            stop();
            enroller.clear();
            updateStatus();
            renombrarEtiquetas();
        }
    }
    
    public void setTemplate(DPFPTemplate template) {
		DPFPTemplate old = this.template;
		this.template = template;
		firePropertyChange(TEMPLATE_PROPERTY, old, template);
    }

}//Fin 