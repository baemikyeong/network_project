package baseball;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

public class SimpleChatClient {
	private JTextArea incoming;
	private JTextField messageBox;
	private JButton sendButton;
	private JButton hintButton;
	private BufferedReader reader;
	private PrintWriter writer;
	private boolean selfChecking;

	AsynchronousChannelGroup channelGroup;
	AsynchronousSocketChannel socketChannel;
	static String sAddr = "localhost";
	static int Port = -1;
	static int client_num;

	public SimpleChatClient(String address, int cPort, int client_num) {
		this.Port = cPort;
		this.sAddr = address;
		this.client_num = client_num;
		selfChecking = true;

		setUpGUI();
		establishConnection();
		System.out.println("Setup Finished");
	}

	public void establishConnection() {
		try {
			System.out.println("SimpleChatClient.establishConnection");
			channelGroup = AsynchronousChannelGroup.withFixedThreadPool(Runtime.getRuntime().availableProcessors(),
					Executors.defaultThreadFactory());
			socketChannel = AsynchronousSocketChannel.open(channelGroup);

			socketChannel.connect(new InetSocketAddress(sAddr, Port), null, new CompletionHandler<Void, Void>() {
				@Override
				public void completed(Void result, Void attachment) {

					try {
						System.out.println("[connection success: " + socketChannel.getRemoteAddress() + "]");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					receive();

				}

				@Override
				public void failed(Throwable e, Void attachment) {
					System.out.println("[fail to connect server]");
					if (socketChannel.isOpen()) {
						try {
							System.out.println("[not connected]");

							if (channelGroup != null && !channelGroup.isShutdown()) {
								channelGroup.shutdownNow();
							}
						} catch (IOException i) {
						}
					}
				}
			});
		} catch (IOException k) {
		}

	}

	public void receive() {
		ByteBuffer byteBuffer = ByteBuffer.allocate(100);
		socketChannel.read(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
			@Override
			public void completed(Integer result, ByteBuffer attachment) {
				try {
					attachment.flip();

					Charset charset = Charset.forName("utf-8");
					String data = charset.decode(attachment).toString();

					if (data.contains("ryidhdgsfbhmjktyjrhewg")) {
						data = "";
						if (selfChecking == true) {
							if (Server.checking == true) {
								send("trtjfjadsgmpiopuyuj");
								data = "You are Helper";
								Server.main(null);
							}
						}
					} else if (data.contains("oiwporfkdkkwldggl")) {
						data = "";
						if (selfChecking == false) {
							System.out.println("check");
							data = "You get helped";
							Client.startImageSend();
						}
					}

					System.out.println("From Server to Client :" + data);
					incoming.append(data);
					incoming.append("\n");
					ByteBuffer byteBuffer = ByteBuffer.allocate(100);
					socketChannel.read(byteBuffer, byteBuffer, this);// receive()

				} catch (Exception e) {
				}
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				System.out.println("[cannot read the message]");
				try {
					System.out.println("[not connected]");
					if (channelGroup != null && !channelGroup.isShutdown()) {
						channelGroup.shutdownNow();
					}
				} catch (IOException e) {
				}
			}

		});

	}

	public void send(String data) throws IOException {
		data = "[" + client_num + "]" + data;
		Charset charset = Charset.forName("utf-8");
		ByteBuffer byteBuffer = charset.encode(data);

		socketChannel.write(byteBuffer, null, new CompletionHandler<Integer, Void>() {
			@Override
			public void completed(Integer result, Void attachment) {
				System.out.println("[send successed]");
			}

			@Override
			public void failed(Throwable exc, Void attachment) {
				System.out.println("[send fail]");
				try {
					System.out.println("[disconnected]");
					if (channelGroup != null && !channelGroup.isShutdown()) {
						channelGroup.shutdownNow();
					}
				} catch (IOException e) {
				}
			}
		});

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
		sendButton = new JButton("Send");
		hintButton = new JButton("Hint");
		JPanel mainPanel = new JPanel();

		JPanel subPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		subPanel.setLayout(new BorderLayout());
		mainPanel.add("Center", scrollPane);
		subPanel.add("Center", messageBox);
		subPanel.add("East", sendButton);
		subPanel.add("South", hintButton);

		sendButton.addActionListener(new SendButtonActivationListener());
		hintButton.addActionListener(new HintButtonActivationListener());
		messageBox.addActionListener(new SendButtonActivationListener());
		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		frame.getContentPane().add(BorderLayout.SOUTH, subPanel);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		hintButton = new JButton("Hint");
		mainPanel.add(hintButton);
		hintButton.setVisible(true);
		hintButton.setBounds(125, 15, 50, 50);
		hintButton.addActionListener(new HintButtonActivationListener());

	}

	public class SendButtonActivationListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String text = messageBox.getText();
			if (text.length() > 0) {
				System.out.println("messageBox.getText()1 = " + text);

				try {
					send(text);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				messageBox.setText("");
			}
			messageBox.requestFocus();
		}
	}

	public class HintButtonActivationListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (selfChecking == true) {
				selfChecking = false;
				try {
					send("erreereeriouuytrhtmyee");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else {
				incoming.append("Hint can be used only one chance");
			}

		}
	}

}