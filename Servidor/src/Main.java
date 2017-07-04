
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
public class Main extends JFrame implements KeyListener, Runnable{
	
	private static final long serialVersionUID = 1L;
	  
	  ///////////////////
  	 // - Variables - //
	///////////////////
	
	private static Image  image;
	private Graphics g;
	private static final String TITLE  = "ConcurrenteTF - Ping-Pong";	
	private static final int    WIDTH  = 800;		  // - Width  size for window - //
	private static final int    HEIGHT = 460;		  // - Height size for window - //
	private String servername = "servername" , clientname = "clientname";
 
	  /////////////////////
	 // - Constructor - //
	/////////////////////
	
	public Main(){

	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.setVisible(true);
		this.setTitle(TITLE);
		this.setSize(WIDTH,HEIGHT);
		this.setResizable(false);
		this.addKeyListener(this);
	} 
	
	public static void main(String[] args){
		Toolkit tk = Toolkit.getDefaultToolkit();
		image = tk.getImage("..\\Resources\\Servidor.png"); // - Set background texture of main menu - //
		Main newT = new Main();
		newT.run();

	}
	

	  ///////////////
	 // - Paint - //
	///////////////
	
	private Image createImage(){
		
	    BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	    g = bufferedImage.createGraphics();
	    g.fillRect(0, 0, WIDTH, HEIGHT);
	    g.drawImage(image,0, 0, this);
	    return bufferedImage;
	    
	}
	@Override
	public void paint(Graphics g){
		g.drawImage(createImage(), 0, 20, this);
	}
	

	  /////////////////////
	 // - KeyListener - //
	/////////////////////
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
		int    keyCode = arg0.getKeyCode();
		String portAdd = null;
		String ipAdd   = null;
		
		// - Create a Server - //
		if(keyCode==KeyEvent.VK_S){
			
			// - Input Dialog for get a port address - //
			portAdd = JOptionPane.showInputDialog(null, "Ejemplo: 6666", "Ingrese el puerto del servidor:", 1);
			
			// - Alert Message - //
			if(portAdd!=null){
				if(!isPort(portAdd)){
					JOptionPane.showMessageDialog(null, "¡El formato del puerto no es válido!", "Error!", JOptionPane.ERROR_MESSAGE);
				}
			
			else{
				
				// - Input Dialog for get a nick name for server player - //
				servername = JOptionPane.showInputDialog(null, "Nombre de jugador:", "Ingrese su nombre:", 1);
				servername+="";
				
				// - Alert Message - //
				if(servername.length()>10 || servername.length()<3 || servername.startsWith("null")){
					JOptionPane.showMessageDialog(null, "¡El nombre ingresado no tiene un fomato válido!", "Error!", JOptionPane.ERROR_MESSAGE);
					
				} 
				
				// - Create a server - //
				else{
					
					PongServer myServer = new PongServer(servername,portAdd);
					Thread myServerT = new Thread(myServer);
					myServerT.start();
					this.setVisible(false);
				}
				}
			}
		}
}//<--end_of_the_switch-->//

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	  //////////////////////
	 // - Check Inputs - //
	//////////////////////
	
	// - Check PORT number type- //
	private boolean isPort(String str) {  
		  Pattern pPattern = Pattern.compile("\\d{1,4}");  
		  return pPattern.matcher(str).matches();  
		}  
	 // - Check IP address type- //
	private boolean isIPAddress(String str) {  
		  Pattern ipPattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");  
		  return ipPattern.matcher(str).matches();  
		}

 
}