package baseball;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

//import baseball.SimpleChatServer.SendButtonActivationListener;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//import baseball.EchoThread.SimpleChatServer;

//import my.net.SimpleChatServer;

//import baseball.SimpleChatServer.Client;

import java.io.*;

public class ServerTest2 {

	/**************
	 * 네트워크 프로그래밍 응용 야구게임 - 서버 ********************************
	 *
	 * 서버에서는 쓰레드를 활용하여 다중 접속자(클라이언트)를 처리한다. 접속자들이 보내는 값을 처리하여 결과를 보내준다. 클라이언트들이
	 * 보내온 값에 대한 처리값을 서버에서 관찰할 수 있도록 처리
	 * 
	 * @throws Exception
	 */

	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = null;
		Socket socket = null;

		Information main_info = new InfomationImpl();
		LocateRegistry.createRegistry(1099);
		String serveradd = "127.0.0.1";
		Naming.rebind("rmi://" + serveradd + ":1099/Information", main_info);

		try {
			// 1. 서버소켓 생성
			SimpleChatServer server = new SimpleChatServer(8888);
			ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
			serverSocket = new ServerSocket(9000);

			while (true) {
				// 2. 클라이언트의 접속요청을 대기한다.
				System.out.println("서버 접속 대기 중");
				socket = serverSocket.accept();
				System.out.println("클라이언트 접속함");

				// 별도 쓰레드 만들어 자료 송수신하게 한다. - 여러 접속자를 처리하기 위한 쓰레드

				// String a = socket.getLocalAddress().getHostAddress();
				// String b = "Game";
				// Game zz = new GameImpl(); //야구게임 클래스 객체 생성
				// LocateRegistry.createRegistry(socket.getPort());
				// Naming.rebind("rmi://"+a+":"+Integer.toString(socket.getPort())+"/"+b,
				// zz);
				EchoThread echoThread = new EchoThread(socket);
				executor.execute(echoThread);
				// echoThread.start();

			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				// 4. 소켓 닫기 --> 연결 끊기
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

// 쓰레드 클래스
class EchoThread extends Thread {

	Socket socket;

	InputStream is = null;
	BufferedReader br = null;

	OutputStream os = null;
	PrintWriter pw = null;

	EchoThread() {
	}

	EchoThread(Socket socket) throws NotBoundException, RemoteException {
		this.socket = socket;
		try {
			// 3. 소켓으로 부터 송수신을 위한 i/o stream 을 얻기
			is = socket.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			os = socket.getOutputStream();
			pw = new PrintWriter(os);
			// System.out.println("스레드 1차 생성");
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void run() {
		// System.out.println("돌아가기 시작함 ");
		try {
			String str = "";
			int cnt = 0;
			boolean val = false;
			char val1;
			String cnt1 = null;
			int x, y;
			// System.out.println("rmi call");
			String a = socket.getInetAddress().getHostAddress();
			String b = "Game";
			// Game c = (Game)Naming.lookup("rmi://"+a+"/"+b);
			Game c = new GameImpl(); // 야구게임 클래스 객체 생성
			c.randomInt();

			while (!val) {

				/*********************************/
				str = br.readLine(); // 라인단위로 받아서 스트링에 저장

				c.inputUserNumber1(str); // 입력 받은 숫자를 배열에 담음.

				x = c.getX();
				y = c.getY();

				cnt = c.getCount(); // 카운트 처리
				if (cnt < 10)
					cnt1 = "0" + String.valueOf(cnt);
				else
					cnt1 = String.valueOf(cnt);

				val = c.getValue(); // 종료처리
				if (val)
					val1 = 'T';
				else
					val1 = 'F';

				System.out.println(
						Thread.currentThread() + " : " + str + " :" + x + "S " + y + "B" + " " + val1 + " " + cnt1);
				pw.println(x + "S " + y + "B" + " " + val1 + " " + cnt1); // 출력
				pw.flush();

			}

		} catch (IOException e) {
			System.out.println("데이타 송수신 에러");
			e.printStackTrace();
		} finally {
			close();
		}

	}

	public void close() {
		try {
			// 4. 소켓 닫기 --> 연결 끊기
			is.close();
			br.close();
			os.close();
			pw.close();
			socket.close();
		} catch (IOException e) {
			System.out.println("socket closed");
			e.printStackTrace();
		}
	}
}

class SimpleChatServer {

	private JTextArea incoming;
	private JTextField messageBox;
	private JButton broadcastButton;
	AsynchronousChannelGroup channelGroup; // 비동기 채널 그룹 필드 선언
	AsynchronousServerSocketChannel serverSocketChannel; // 비동기 서버소켓 채널 필드 선언
	List<Client> connections = new Vector<Client>();
	Client c;

	public SimpleChatServer(int port) throws Exception {
		channelGroup = AsynchronousChannelGroup.withFixedThreadPool(Runtime.getRuntime().availableProcessors(),
				Executors.defaultThreadFactory());// 스레드 풀 생성 -> 이것을 이용하는

		// 9000번 포트에서 클라이언트의 연결을 수락하는 비동기 서버소켓채널을 생성
		serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);

		if (!serverSocketChannel.isOpen()) {
			throw new Exception("fail asynchronousServerSocketChannel open");
		}
		serverSocketChannel.bind(new InetSocketAddress(port));
		startAcceptingNewClient();
		setUpGUI();

	}

	public void setUpGUI() {
		JFrame frame = new JFrame();
		incoming = new JTextArea(15, 30);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(incoming);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		messageBox = new JTextField(20);
		broadcastButton = new JButton("Broadcast");
		JPanel mainPanel = new JPanel();
		JPanel subPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		subPanel.setLayout(new BorderLayout());
		mainPanel.add("Center", scrollPane);
		subPanel.add("Center", messageBox);
		subPanel.add("East", broadcastButton);
		broadcastButton.addActionListener(new SendButtonActivationListener());
		messageBox.addActionListener(new SendButtonActivationListener());
		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		frame.getContentPane().add(BorderLayout.SOUTH, subPanel);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	}

	public class SendButtonActivationListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String text = messageBox.getText();
			if (text.length() > 0) {
				System.out.println("messageBox.getText()=" + text);

				incoming.append("server : " + text);
				incoming.append("\n");
				broadcastMessage(text);
				messageBox.setText("");
			}
			messageBox.requestFocus();
		}
	}

	public void startAcceptingNewClient() throws IOException {
		serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
			@Override
			public void completed(AsynchronousSocketChannel socketChannel, Void attachment) {
				// TODO Auto-generated method stub
				try {
					String message = "[서버 연결 성공:" + serverSocketChannel.getLocalAddress() + "]";
					System.out.println(message);
					incoming.append(message);
					incoming.append("\n");
				} catch (IOException e) {
				}
				// 클라이언트 생성 ->객체 저장
				Client client = new Client(socketChannel);
				connections.add(client);
				System.out.println("[연결된 갯수:" + connections.size() + "]");

				incoming.append("[연결된 갯수:" + connections.size() + "]");
				incoming.append("\n");

				serverSocketChannel.accept(null, this);// 계속해서 리슨
			}

			@Override
			public void failed(Throwable exc, Void attachment) {
				// TODO Auto-generated method stub
				if (serverSocketChannel.isOpen()) {
					try {
						connections.clear();// connection에 연결된 모든 client 제거
						if (channelGroup != null && !channelGroup.isShutdown())
							channelGroup.shutdownNow();
					} catch (Exception e) {
					}

				}
			}

		});
	}

	public class Client {
		AsynchronousSocketChannel socketChannel;// 비동기 소켓 채널 필드 선언

		Client(AsynchronousSocketChannel socketChannel) {
			this.socketChannel = socketChannel;
			receive();
		}

		void receive() {
			ByteBuffer buffer = ByteBuffer.allocate(100);

			socketChannel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
				@Override
				public void completed(Integer result, ByteBuffer attachment) {
					try {
						String message = "[ 요청 처리 : " + socketChannel.getRemoteAddress() + ": "
								+ Thread.currentThread().getName() + "]";

						System.out.println(message);

						attachment.flip();

						Charset charset = Charset.forName("utf-8");
						String data = charset.decode(attachment).toString();// 문자열변화

						if (data.equals("please,start the receiver")) {
							for (Client client : connections) {
								client.send("please,restart the receiver");
							}
						} else if (data.equals("I am ready")) {
							for (Client client : connections) {
								client.send("Set, client Server");
							}
							incoming.append(data);
							incoming.append("\n");
						}
						// 모든 클라이언트에 보내기
						else {

							// 모든 클라이언트에 보내기
							for (Client client : connections) {
								client.send(data);
							}
						}
						ByteBuffer buffer = ByteBuffer.allocate(100);
						// 다시 메세지 읽기
						socketChannel.read(buffer, buffer, this);

					} catch (Exception e) {
						System.out.println("Exception for receive");

					}
				}

				@Override
				public void failed(Throwable exc, ByteBuffer attachment) {
					// TODO Auto-generated method stub
					try {
						String message = "[클라이언트 통신 안됨: " + socketChannel.getRemoteAddress() + ": "
								+ Thread.currentThread().getName() + "]";
<<<<<<< HEAD
						System.out.println(message);
						incoming.append(message);
=======
					 System.out.println(message);
					 incoming.append(message);
>>>>>>> e43cf40b1d692ba7bbd0f582b86552e8b2ad27e3
						incoming.append("\n");
						connections.remove(Client.this);
						socketChannel.close();

					} catch (IOException e) {
					}
				}
			});
		}

		public void send(String data) {
			Charset charset = Charset.forName("utf-8");
			ByteBuffer byteBuffer = charset.encode(data);
			socketChannel.write(byteBuffer, null, new CompletionHandler<Integer, Void>() {

				@Override
				public void completed(Integer result, Void attachment) {
					// TODO Auto-generated method stub

				}

				@Override
				public void failed(Throwable exc, Void attachment) {
					// TODO Auto-generated method stub
					try {
						String message = "[클라이언트 통신 안됨: " + socketChannel.getRemoteAddress() + ": "
								+ Thread.currentThread().getName() + "]";
						System.out.println(message);
						incoming.append(message);
						incoming.append("\n");
						connections.remove(Client.this);
						socketChannel.close();
					} catch (IOException e) {
					}
				}

			});

		}
	}
<<<<<<< HEAD

	public void broadcastMessage(String message) {
		for (Client client : connections) {
			client.send("server :" + message);
		}

	}

}
=======

	public void broadcastMessage(String message) {

		System.out.println("SimpleChatServer.broadcastMessage");

		for (Client client : connections) {
			client.send("server :" + message);
		}

	}

	/*
	 * public boolean ImageCommunicationCheck(){ return false;
	 * 
	 * }
	 */
}
>>>>>>> e43cf40b1d692ba7bbd0f582b86552e8b2ad27e3
