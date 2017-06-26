package baseball;


import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.util.List;
import java.util.Vector;

import baseball.SimpleChatServer.Client;

import java.io.*;
public class ClientTest2 {
	public static void main(String[] args) throws NotBoundException{
		Socket socket = null;
		
		try {
			
			System.out.println("Connecting to Server...");
			socket = new Socket("localhost", 9000); 
			String server = "127.0.0.1";
			Information c = (Information)Naming.lookup("rmi://"+server+"/Information");

			InputStream is = socket.getInputStream(); 
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			OutputStream os = socket.getOutputStream(); 
			PrintWriter pw = new PrintWriter(os);
			
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		
			String str = "",str1="";
			System.out.println("\n\n=====================================================================\n");
			System.out.println(c.Inform_gameRule());
			System.out.println("\n=====================================================================\n\n");
			System.out.println("Start Game");
			while(true){
				
				System.out.print("Input four numbers>>");
				
				str = stdin.readLine();
				String h = "h";
				String H = "H";
				if(str.length()<4){
					if(str.equals(h) || str.equals(H)){
						SimpleChatClient client = new SimpleChatClient("localhost", 8888, c.get_client());
						continue;
					}
					if(str.equals("c") || str.equals("C")){
						System.out.println(c.get_client()+"people.");
					    continue;
					}
					System.out.println("It's not afordable. Input just four numbers >>");
					str = stdin.readLine();
				}
			
				pw.println(str);
				pw.flush();
				//pw.close();
				//socket.close();
				str1 = br.readLine();
				
				System.out.println("Server >>"+str1);
				
				if(str1.subSequence(6, 8).charAt(0)!='F'){break;}
				
			}
			
			System.out.println("You are Winner!");
			
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally{
			try {
				socket.close();
			}catch(IOException e) {e.printStackTrace();}
		}
	}
}