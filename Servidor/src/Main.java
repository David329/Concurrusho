
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author David
 */
public class Main extends JFrame implements KeyListener, Runnable {

    private static final long serialVersionUID = 1L;

    private static Image imagen;
    private Graphics gr;
    private static final String TITLE = "ConcurrenteTF - Ping-Pong";
    private static final int WIDTH = 800;
    private static final int HEIGHT = 460;
    private String nombreServidor = "nombreservidor";

    public Main() {

    }

    @Override
    public void run() {
        this.setVisible(true);
        this.setTitle(TITLE);
        this.setSize(WIDTH, HEIGHT);
        this.setResizable(false);
        this.addKeyListener(this);
    }

    public static void main(String[] args) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        imagen = tk.getImage("..\\Resources\\Servidor.png"); // - Set background texture of main menu - //
        Main newT = new Main();
        newT.run();
    }

    private Image crearImagen() {
        BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        gr = bufferedImage.createGraphics();
        gr.fillRect(0, 0, WIDTH, HEIGHT);
        gr.drawImage(imagen, 0, 0, this);
        return bufferedImage;
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(crearImagen(), 0, 20, this);
    }

    @Override
    public void keyPressed(KeyEvent arg0) {

        int keyCode = arg0.getKeyCode();
        String puerto = null;

        if (keyCode == KeyEvent.VK_S) {

            puerto = JOptionPane.showInputDialog(null, "Ejemplo: 6666", "Ingrese el puerto del servidor:", 1);

            if (puerto != null) {
                if (!validarPuerto(puerto)) {
                    JOptionPane.showMessageDialog(null, "¡El formato del puerto no es válido!", "Error!", JOptionPane.ERROR_MESSAGE);
                } else {

                    nombreServidor = JOptionPane.showInputDialog(null, "Nombre de jugador:", "Ingrese su nombre:", 1);
                    nombreServidor += "";

                    if (nombreServidor.length() > 10 || nombreServidor.length() < 3 || nombreServidor.startsWith("null")) {
                        JOptionPane.showMessageDialog(null, "¡El nombre ingresado no tiene un fomato válido!", "Error!", JOptionPane.ERROR_MESSAGE);

                    } else {

                        PongServer pongServer = new PongServer(nombreServidor, puerto);
                        Thread serverThread = new Thread(pongServer);
                        serverThread.start();
                        this.setVisible(false);
                    }
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {

    }

    @Override
    public void keyTyped(KeyEvent arg0) {

    }

    private boolean validarPuerto(String str) {
        Pattern pPattern = Pattern.compile("\\d{1,4}");
        return pPattern.matcher(str).matches();
    }
}
