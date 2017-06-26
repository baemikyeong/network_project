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

	public static Information main_info;

	public static void main(String[] args) throws Exception {
		//

		ServerSocket serverSocket = null;
		Socket socket = null;

		main_info = new InfomationImpl();
		LocateRegistry.createRegistry(1099);
		String serveradd = "127.0.0.1";
		Naming.rebind("rmi://" + serveradd + ":1099/Information", main_info);

		try {
			SimpleChatServer server = new SimpleChatServer(8888);
			ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
			serverSocket = new ServerSocket(9000);
			int count = 0;
			while (true) {
				System.out.println("Waiting for New Client...");
				socket = serverSocket.accept();
				System.out.println("Client accepted");
				count++;
				main_info.set_client(count);

				EchoThread echoThread = new EchoThread(socket);
				executor.execute(echoThread);
			}

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
			is = socket.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			os = socket.getOutputStream();
			pw = new PrintWriter(os);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			String str = "";
			int cnt = 0;
			boolean val = false;
			char val1;
			String cnt1 = null;
			int x, y;
			String a = socket.getInetAddress().getHostAddress();
			String b = "Game";
			Game c = new GameImpl();
			c.randomInt();

			while (!val) {

				/*********************************/
				str = br.readLine();

				c.inputUserNumber1(str);

				x = c.getX();
				y = c.getY();

				cnt = c.getCount();
				if (cnt < 10)
					cnt1 = "0" + String.valueOf(cnt);
				else
					cnt1 = String.valueOf(cnt);

				val = c.getValue();
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
			System.out.println("Error for Data Communication");
			e.printStackTrace();
		} finally {
			close();
		}

	}

	public void close() {
		try {
			is.close();
			br.close();
			os.close();
			pw.close();
			socket.close();
		} catch (IOException e) {
			System.out.println("socket closed ");
			e.printStackTrace();
		}
	}
}

class SimpleChatServer {

	private JTextArea incoming;
	private JTextField messageBox;
	private JButton broadcastButton;
	AsynchronousChannelGroup channelGroup; 
	AsynchronousServerSocketChannel serverSocketChannel; 
	List<Client> connections = new Vector<Client>();
	Client c;

	public SimpleChatServer(int port) throws Exception {
		channelGroup = AsynchronousChannelGroup.withFixedThreadPool(Runtime.getRuntime().availableProcessors(),
				Executors.defaultThreadFactory());

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
				System.out.println("messageBox.getText()1 = " + text);

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
					String message = "[Connection Successed:" + serverSocketChannel.getLocalAddress() + "]";
					System.out.println(message);
					incoming.append(message);
					incoming.append("\n");
				} catch (IOException e) {
				}
				Client client = new Client(socketChannel);
				connections.add(client);
				
		
				System.out.println("[Connection Count:" + connections.size() + "]");

				incoming.append("[Connection Count:" + connections.size() + "]");
				incoming.append("\n");

				serverSocketChannel.accept(null, this);
			}

			@Override
			public void failed(Throwable exc, Void attachment) {
				// TODO Auto-generated method stub
				if (serverSocketChannel.isOpen()) {
					try {
						connections.clear();
						if (channelGroup != null && !channelGroup.isShutdown())
							channelGroup.shutdownNow();
					} catch (Exception e) {
					}

				}
			}

		});
	}

	public class Client {
		AsynchronousSocketChannel socketChannel;

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
						String message = "[read access: " + socketChannel.getRemoteAddress() + ": "
								+ Thread.currentThread().getName() + "]";

						System.out.println(message);

						attachment.flip();

						Charset charset = Charset.forName("utf-8");
						String data = charset.decode(attachment).toString();

						if (data.contains("erreereeriouuytrhtmyee")) {
							for (Client client : connections) {
								client.send("ryidhdgsfbhmjktyjrhewg");
							}
						} else if (data.contains("trtjfjadsgmpiopuyuj")) {
							for (Client client : connections) {
								client.send("oiwporfkdkkwldggl");
							}
							
						}
						else {

							for (Client client : connections) {
								client.send(data);
							}
							incoming.append(data);
							incoming.append("\n");
						}
						ByteBuffer buffer = ByteBuffer.allocate(100);
						socketChannel.read(buffer, buffer, this);

					} catch (Exception e) {
						System.out.println("Error for Exception");

					}
				}

				@Override
				public void failed(Throwable exc, ByteBuffer attachment) {
					// TODO Auto-generated method stub
					try {
						String message = "[cannot read data from client: " + socketChannel.getRemoteAddress() + ": "
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
						String message = "[cannot send data from client: " + socketChannel.getRemoteAddress() + ": "
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

	public void broadcastMessage(String message) {
		for (Client client : connections) {
			client.send("server :" + message);
		}

	}
}