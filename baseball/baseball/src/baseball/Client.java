package baseball;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class Client {// Sender
	final static int w = Toolkit.getDefaultToolkit().getScreenSize().width;
	static final int h = Toolkit.getDefaultToolkit().getScreenSize().height;
	JFrame frame;
	JTextField text;
	JButton button;

	public static void startImageSend() throws UnknownHostException {
		client_work();
		
	}
	public Client() {
		frame = new JFrame("Client"); 
		frame.setBounds(0, 0, 300, 100);
		frame.setLayout(null); 
	
		text = new JTextField();
		text.setVisible(true);
		text.setBounds(25, 15, 100, 50);
		
		button = new JButton("Accepted");
		button.setVisible(true);
		button.setBounds(125, 15, 50, 50);
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		frame.add(text);
		frame.add(button);
		frame.setVisible(true);

	}


	public static void client_work() throws UnknownHostException {
		Socket socket = null;
		
		System.out.println("Client Get Ready");
		
		try {
			socket = new Socket("127.0.0.1", 12345);
			System.out.println("Connection Completed on Client");
			// BufferedImage image = new BufferedImage(1280, 720,
			// BufferedImage.TYPE_3BYTE_BGR);
			BufferedImage image; 
			Robot r = new Robot();
			BufferedOutputStream bout = new BufferedOutputStream(socket.getOutputStream());
				// image.getGraphics().drawImage(r.createScreenCapture(new
				// Rectangle(0,0,w,h)).getScaledInstance(1280, 720,
				// Image.SCALE_SMOOTH), 0, 0, null);
				image = r.createScreenCapture(new Rectangle(0, 0, w, h));
				ImageIO.write(image, "bmp", bout);
												
				bout.flush();
			//	socket.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fail to Connection on Client");
		}
	}

}
