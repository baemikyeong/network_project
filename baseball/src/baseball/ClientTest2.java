package baseball;

import java.net.*;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.List;
import java.util.Vector;

import javax.swing.JTextField;



//import java.awt.List;
import java.io.*;

public class ClientTest2 {
	static String eServer = "";
	static int ePort = 0000;
	String inputAddress;
	String inputPort;
	JTextField address = new JTextField("IP address");
	JTextField port = new JTextField("Port");

	public static void main(String[] args) {
		Socket socket = null;

		try {

			System.out.println("서버와 연결 중 ...");
			socket = new Socket("localhost", 9000);
			System.out.println("서버에서 확인 중 ");

			InputStream is = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			OutputStream os = socket.getOutputStream();
			PrintWriter pw = new PrintWriter(os);

			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("rmi서버에 올리는 중  ");
			String a = socket.getLocalAddress().getHostAddress();
			String b = "Game";
			Game zz = new GameImpl(); // 야구게임 클래스 객체 생성
			LocateRegistry.createRegistry(1099);
			Naming.rebind("rmi://" + a + ":1099/" + b, zz);

			String str = "", str1 = "";
			System.out.println("게임을 시작하겠습니다 ");
			while (true) {

				System.out.print("4자리 숫자를 입력해 주세요 >>");

				str = stdin.readLine();
				String h = "h";
				String H = "H";
				if (str.length() < 4) {
					if (str.equals(h) || str.equals(H)) {
						SimpleChatClient client = new SimpleChatClient("localhost", 8888);
						continue;
					}
					System.out.println("4자리로 입력해주세요 >>");
					str = stdin.readLine();
				}

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

			System.out.println("승리하였습니다!! ");

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