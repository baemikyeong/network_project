package baseball;


import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.util.List;
import java.util.Vector;
//import java.awt.List;
import java.io.*;
public class ClientTest2 {
	public static void main(String[] args) throws NotBoundException{
		Socket socket = null;
		
		try {
			
			System.out.println("서버와 연결 중 ...");
			socket = new Socket("localhost", 9000); 
			System.out.println("서버에서 확인 중 ");
			String server = "127.0.0.1";
			Information c = (Information)Naming.lookup("rmi://"+server+"/Information");
			c.Inform_gameRule();
			InputStream is = socket.getInputStream(); 
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			OutputStream os = socket.getOutputStream(); 
			PrintWriter pw = new PrintWriter(os);
			
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		//	System.out.println("rmi서버에 올리는 중  ");
		//	String a = socket.getLocalAddress().getHostAddress();
		//	String b = "Game";
		//	Game zz = new GameImpl(); //야구게임 클래스 객체 생성
			//Runtime.getRuntime().exec("rmiregistry 1099");
		//LocateRegistry.createRegistry(1099);
		//Naming.rebind("rmi://"+a+":1099/"+b, zz);
			
			String str = "",str1="";
			System.out.println("게임을 시작하겠습니다");
			while(true){
				
=======
			System.out.println("rmi서버에 올리는 중  ");
			String a = socket.getLocalAddress().getHostAddress();
		//	int socket_port = socket.getLocalPort();
			//System.out.println("a:"+socket_port);
			String b = "Game";
			Game zz = new GameImpl(); // 야구게임 클래스 객체 생성
			LocateRegistry.createRegistry(atr);
			System.out.println("Not bound");
			Naming.rebind("rmi://" + a + ":" + Integer.toString(atr) +"/" + b, zz);
			System.out.println("a:bind OK");

			String str = "", str1 = "";
			System.out.println("게임을 시작하겠습니다 ");
			while (true) {

>>>>>>> e43cf40b1d692ba7bbd0f582b86552e8b2ad27e3
				System.out.print("4자리 숫자를 입력해 주세요 >>");

				str = stdin.readLine();
				String h = "h";
				String H = "H";
				if (str.length() < 4) {
					if (str.equals(h) || str.equals(H)) {
						SimpleChatClient client = new SimpleChatClient("localhost", 8888);
						
						continue;
					}
					System.out.println("4자리로 다 입력해주세요 >>");
					str = stdin.readLine();
				}
<<<<<<< HEAD
			
=======

>>>>>>> e43cf40b1d692ba7bbd0f582b86552e8b2ad27e3
				pw.println(str);
				pw.flush();
				// pw.close();
				// socket.close();
				str1 = br.readLine();

				System.out.println("서버 >>" + str1);

				if (str1.subSequence(6, 8).charAt(0) != 'F') {
					break;
				}

			}
<<<<<<< HEAD
			
			System.out.println("승리하였습니다!!");
			
=======

			System.out.println("승리하였습니다!! ");

>>>>>>> e43cf40b1d692ba7bbd0f582b86552e8b2ad27e3
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}