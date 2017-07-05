
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JFrame;

public class PongClient extends JFrame implements KeyListener, Runnable, WindowListener {

    private static final long serialVersionUID = 1L;

    //Variables ventana
    private static final String TITLE = "ping-pong::cliente";
    private static final int WIDTH = 800;
    private static final int HEIGHT = 460;
    boolean enEjecucion = false;

    //Variables jugador
    private PlayerServer playerServer;
    private PlayerClient playerClient;
    private int playerBarWidth = 30;
    private int playerBarHeight = 120;
    private int playerVelocidad = 10;

    //Servidor
    private static Socket clientSocket;
    private int puerto;
    private String direccionIP;
    private boolean reset = false;
    private int countS = 0;

    //Gráficos
    private Graphics gr;
    private Font sFont = new Font("TimesRoman", Font.BOLD, 90);
    private Font mFont = new Font("TimesRoman", Font.BOLD, 50);
    private Font nFont = new Font("TimesRoman", Font.BOLD, 32);
    private Font rFont = new Font("TimesRoman", Font.BOLD, 18);
    private String[] message;

    public PongClient(String clientname, String puerto, String direccionIP) {

        playerServer = new PlayerServer();
        playerClient = new PlayerClient(clientname);
        playerServer.setName("");

        this.direccionIP = direccionIP;
        this.puerto = Integer.parseInt(puerto);
        this.enEjecucion = true;

        this.setTitle(TITLE);
        this.setSize(WIDTH, HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        addKeyListener(this);
    }

    @Override
    public void run() {

        try {

            System.out.println("Buscando servidor...\nConectándose a " + direccionIP + ":" + puerto);
            clientSocket = new Socket(direccionIP, puerto);
            System.out.println("Conectado al servidor...");

            if (clientSocket.isConnected()) {

                while (true) {

                    ObjectOutputStream sendObj = new ObjectOutputStream(clientSocket.getOutputStream());
                    sendObj.writeObject(playerClient);
                    sendObj = null;

                    ObjectInputStream getObj = new ObjectInputStream(clientSocket.getInputStream());
                    playerServer = (PlayerServer) getObj.readObject();
                    getObj = null;

                    if (reset) {

                        if (countS > 5) {
                            playerClient.restart = false;
                            reset = false;
                            countS = 0;
                        }
                    }
                    countS++;
                    repaint();
                }
            } else {
                System.out.println("Desconectado...");
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private Image createImage() {

        BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        gr = bufferedImage.createGraphics();

        //Tabla
        gr.setColor(new Color(15, 9, 9));
        gr.fillRect(0, 0, WIDTH, HEIGHT);

        //Lineas
        gr.setColor(Color.white);
        gr.fillRect(WIDTH / 2 - 5, 0, 5, HEIGHT);
        gr.fillRect(WIDTH / 2 + 5, 0, 5, HEIGHT);

        //Puntaje
        gr.setColor(new Color(228, 38, 36));
        gr.setFont(sFont);
        gr.drawString("" + playerServer.getScoreS(), WIDTH / 2 - 60, 120);
        gr.drawString("" + playerServer.getScoreP(), WIDTH / 2 + 15, 120);

        //Nombre jugadores
        gr.setFont(nFont);
        gr.setColor(Color.white);
        gr.drawString(playerServer.getName(), WIDTH / 10, HEIGHT - 20);
        gr.drawString(playerClient.getName(), 600, HEIGHT - 20);

        //Barra jugadores
        gr.setColor(new Color(73, 235, 224));
        gr.fillRect(playerServer.getX(), playerServer.getY(), playerBarWidth, playerBarHeight);
        gr.setColor(new Color(57, 181, 74));
        gr.fillRect(playerClient.getX(), playerClient.getY(), playerBarWidth, playerBarHeight);

        //Ball
        gr.setColor(new Color(255, 255, 255));
        gr.fillOval(playerServer.getBallx(), playerServer.getBally(), 45, 45);
        gr.setColor(new Color(228, 38, 36));
        gr.fillOval(playerServer.getBallx() + 5, playerServer.getBally() + 5, 45 - 10, 45 - 10);

        //Mensajes
        message = playerServer.getImessage().split("-");
        gr.setFont(mFont);
        gr.setColor(Color.white);
        if (message.length != 0) {
            gr.drawString(message[0], WIDTH / 4 - 31, HEIGHT / 2 + 38);
            if (message.length > 1) {
                if (message[1].length() > 6) {
                    gr.setFont(rFont);
                    gr.setColor(new Color(228, 38, 36));
                    gr.drawString(message[1], WIDTH / 4 - 31, HEIGHT / 2 + 100);
                }
            }
        }
        return bufferedImage;
    }

    public void paint(Graphics g) {
        g.drawImage(createImage(), 0, 0, this);
        playerClient.ok = true;
    }

    // Mover barra hacia arriba
    public void playerUP() {
        if (playerClient.getY() - playerVelocidad > playerBarHeight / 2 - 10) {

            playerClient.setY(playerClient.getY() - playerVelocidad);
        }
    }

    // Mover barra hacia abajo
    public void playerDOWN() {
        if (playerClient.getY() + playerVelocidad < HEIGHT - playerBarHeight - 30) {

            playerClient.setY(playerClient.getY() + playerVelocidad);
        }
    }

    @Override
    public void keyPressed(KeyEvent arg0) {

        int keycode = arg0.getKeyCode();
        if (keycode == KeyEvent.VK_UP) {
            playerUP();
            repaint();
        }
        if (keycode == KeyEvent.VK_DOWN) {
            playerDOWN();
            repaint();
        }
        if (playerServer.isRestart()) {
            playerClient.restart = true;
            reset = true;
        }
        if (keycode == KeyEvent.VK_ESCAPE || keycode == KeyEvent.VK_N && playerServer.isRestart()) {
            try {
                this.setVisible(false);
                clientSocket.close();
                System.exit(EXIT_ON_CLOSE);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {

    }

    @Override
    public void keyTyped(KeyEvent arg0) {

    }

    @Override
    public void windowActivated(WindowEvent arg0) {

    }

    @Override
    public void windowClosed(WindowEvent arg0) {

    }

    @SuppressWarnings("deprecation")
    @Override
    public void windowClosing(WindowEvent arg0) {

        Thread.currentThread().stop();
        this.setVisible(false);
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void windowDeactivated(WindowEvent arg0) {

    }

    @Override
    public void windowDeiconified(WindowEvent arg0) {

    }

    @Override
    public void windowIconified(WindowEvent arg0) {

    }

    @Override
    public void windowOpened(WindowEvent arg0) {

    }
}
