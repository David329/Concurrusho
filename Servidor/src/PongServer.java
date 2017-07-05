
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
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;

public class PongServer extends JFrame implements KeyListener, Runnable, WindowListener {

    private static final long serialVersionUID = 1L;

    //Variables ventana
    private static final String TITLE = "ping-pong::servidor";
    private static final int WIDTH = 800;
    private static final int HEIGHT = 460;

    //Variables del juego
    boolean enEjecucion = false;
    boolean verificar = true;
    boolean initgame = false;

    //Variables de jugador y objetos
    Ball ball;
    private PlayerServer playerServer;
    private PlayerClient playerClient;

    private int ballVelocidad = 4;
    private int playerBarWidth = 30;
    private int playerBarHeight = 120;
    private int puntajeMaximo = 9;
    private int playerVelocidad = 10;
    private boolean reiniciar = false;
    private boolean reiniciarON = false;

    //Servidor
    private static Socket clientSocket = null;
    private static ServerSocket serverSocket = null;
    private int puerto;

    //Gráficos
    private Graphics gr;
    private Font sFont = new Font("TimesRoman", Font.BOLD, 90);
    private Font mFont = new Font("TimesRoman", Font.BOLD, 50);
    private Font nFont = new Font("TimesRoman", Font.BOLD, 32);
    private Font rFont = new Font("TimesRoman", Font.BOLD, 18);
    private String[] message;
    private Thread ballThread;

    public PongServer(String servername, String portAdd) {

        playerServer = new PlayerServer();
        playerClient = new PlayerClient("");
        playerServer.setName(servername);

        this.puerto = Integer.parseInt(portAdd);
        this.enEjecucion = true;
        this.setTitle(TITLE + "::puerto[" + portAdd + "]");
        this.setSize(WIDTH, HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);

        ball = new Ball(playerServer.getBallx(), playerServer.getBally(), ballVelocidad, ballVelocidad, 45, WIDTH, HEIGHT);

        addKeyListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(puerto);
            System.out.println("El servidor ha empezado a correr en el puerto " + puerto + ".\nEsperando otro jugador...");
            System.out.println("Esperando conexión...");
            playerServer.setImessage("Esperando otro jugador");
            clientSocket = serverSocket.accept();

            System.out.println("Conectado a un jugador...");

            if (clientSocket.isConnected()) {

                boolean noVerificado = true;
                ballThread = new Thread(ball);
                while (true) {
                    if (reiniciar == true) {
                        reiniciar = false;
                    }

                    if (playerServer.getScoreP() >= puntajeMaximo || playerServer.getScoreS() >= puntajeMaximo && reiniciar == false) {

                        if (playerServer.getScoreS() > playerServer.getScoreP()) {
                            playerServer.setOmessage("Ganador   Perdedor-Jugar de nuevo: Presione cualquier tecla || Salir: Esc|N");
                            playerServer.setImessage("Ganador   Perdedor-Jugar de nuevo: Presione cualquier tecla || Salir: Esc|N");
                            reiniciar = true;

                        } else {
                            playerServer.setImessage("Perdedor  Ganador-Juegar de nuevo: Presione cualquier tecla || Salir: Esc|N");
                            playerServer.setOmessage("Perdedor  Ganador-Juegar de nuevo: Presione cualquier tecla || Salir: Esc|N");
                            reiniciar = true;

                        }
                        ballThread.suspend();
                    }

                    if (playerClient.ok && noVerificado) {
                        playerServer.setImessage("");
                        ballThread.start();
                        noVerificado = false;
                    }

                    updateBall();

                    ObjectInputStream getObj = new ObjectInputStream(clientSocket.getInputStream());
                    playerClient = (PlayerClient) getObj.readObject();
                    getObj = null;

                    ObjectOutputStream sendObj = new ObjectOutputStream(clientSocket.getOutputStream());
                    sendObj.writeObject(playerServer);
                    sendObj = null;

                    if (reiniciarON) {

                        if (playerClient.restart) {
                            playerServer.setScoreP(0);
                            playerServer.setScoreS(0);
                            playerServer.setOmessage("");
                            playerServer.setImessage("");
                            reiniciar = false;
                            playerServer.setRestart(false);
                            playerServer.setBallx(380);
                            playerServer.setBally(230);
                            ball.setX(380);
                            ball.setY(230);
                            ballThread.resume();
                            reiniciarON = false;
                        }
                    }

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
        gr.setFont(sFont);
        gr.setColor(new Color(228, 38, 36));
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
        gr.setColor(ball.getColorBall());
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

    @Override
    public void paint(Graphics g) {
        g.drawImage(createImage(), 0, 0, this);
    }

    public void updateBall() {

        verificarColisiones();

        playerServer.setBallx(ball.getX());
        playerServer.setBally(ball.getY());

    }

    // Mover barra hacia arriba
    public void playerUP() {
        if (playerServer.getY() - playerVelocidad > playerBarHeight / 2 - 10) {

            playerServer.setY(playerServer.getY() - playerVelocidad);
        }
    }

    // Mover barra hacia abajo
    public void playerDOWN() {
        if (playerServer.getY() + playerVelocidad < HEIGHT - playerBarHeight - 30) {

            playerServer.setY(playerServer.getY() + playerVelocidad);
        }
    }

    public void verificarColisiones() {

        if (playerServer.getBallx() < playerClient.getX() && playerServer.getBallx() > playerServer.getX()) {
            verificar = true;
        }

        if (playerServer.getBallx() > playerClient.getX() && verificar) {

            playerServer.setScoreS(playerServer.getScoreS() + 1);

            verificar = false;
        } else if (playerServer.getBallx() <= playerServer.getX() && verificar) {

            playerServer.setScoreP(playerServer.getScoreP() + 1);

            verificar = false;

        }

        if (ball.getX() <= playerServer.getX() + playerBarWidth && ball.getY() + ball.getRadio() >= playerServer.getY() && ball.getY() <= playerServer.getY() + playerBarHeight) {
            ball.setX(playerServer.getX() + playerBarWidth);
            playerServer.setBallx(playerServer.getX() + playerBarWidth);
            ball.setXv(ball.getXv() * -1);
        }

        if (ball.getX() + ball.getRadio() >= playerClient.getX() && ball.getY() + ball.getRadio() >= playerClient.getY() && ball.getY() <= playerClient.getY() + playerBarHeight) {
            ball.setX(playerClient.getX() - ball.getRadio());
            playerServer.setBallx(playerClient.getX() - ball.getRadio());
            ball.setXv(ball.getXv() * -1);
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
        if (reiniciar == true) {
            reiniciarON = true;
            playerServer.setRestart(true);
        }

        if (keycode == KeyEvent.VK_N || keycode == KeyEvent.VK_ESCAPE && reiniciar == true) {
            try {
                this.setVisible(false);
                serverSocket.close();
                System.exit(EXIT_ON_CLOSE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void windowClosing(WindowEvent arg0) {
        Thread.currentThread().stop();
        this.setVisible(false);
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(1);
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
        System.exit(1);
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
