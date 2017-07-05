
import java.awt.Color;

public class Ball extends Thread {

    private int x;
    private int y;
    private double xv;  // velocidad en eje X 
    private double yv;  // velocidad en eje Y
    private int radio;
    private int HEIGHT;
    private int WIDTH;
    private Color color;

    @Override
    public void run() {
        while (true) {
            mover();
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Ball(int x, int y, double xv, double yv, int radio, int WIDTH, int HEIGHT) {
        super();
        this.x = x;
        this.y = y;
        this.xv = xv;
        this.yv = yv;
        this.radio = radio;
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.color = new Color(255, 242, 44);
    }

    public void mover() {
        if (x + xv > (WIDTH - radio) - 7) {
            x = (WIDTH - radio) - 7; // posición
            xv = xv * -1;   // velocidad

        }

        if (x + xv < 9) {
            x = 9;
            xv = xv * -1;
        }

        if (y + yv < radio / 2 + 7) {
            y = 29;
            yv = yv * -1;
        }

        if (y + yv > (HEIGHT - radio) - 6) {
            y = (HEIGHT - radio) - 6;
            yv = yv * -1;

        }
        x += xv;
        y += yv;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getXv() {
        return xv;
    }

    public void setXv(double xv) {
        this.xv = xv;
    }

    public double getYv() {
        return yv;
    }

    public void setYv(double yv) {
        this.yv = yv;
    }

    public int getRadio() {
        return radio;
    }

    public void setRadio(int radio) {
        this.radio = radio;
    }

    public Color getColorBall() {
        return color;
    }

    public void setColorBall(Color colorBall) {
        this.color = colorBall;
    }
}
