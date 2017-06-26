package baseball;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Server {//Receiver
   final int w = 1280, h = 720;
   final int x = Toolkit.getDefaultToolkit().getScreenSize().width / 2 - w / 2,
         y = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - h / 2;
   JFrame frame;
   static boolean checking = true; 
   
   public static void main(String[] args) {
      checking = false;
      new Server();    
      }

   /*public void startImageReceive(){
      new Server(); 
   }*/
   public Server() {
      frame = new JFrame("server");
      frame.setBounds(x, y, w, h);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);
    
      ServerSocket socket_s = null;
      Socket socket = null;
      try {
         socket_s = new ServerSocket(12345);
         socket = socket_s.accept();
         System.out.println("connection with client, success! - server" + socket);
                 BufferedInputStream bin = new BufferedInputStream(socket.getInputStream());
   
            frame.getGraphics().drawImage(ImageIO.read(ImageIO.createImageInputStream(bin)), 0, 0, w, h, frame);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

}