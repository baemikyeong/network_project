package baseball;

import javax.swing.*;

import baseball.SimpleChatServer.SendButtonActivationListener;

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

	AsynchronousChannelGroup channelGroup; // 비동기 채널 그룹 필드 선언
	AsynchronousSocketChannel socketChannel;
	static String sAddr = "localhost";
	static int Port = -1;

	public SimpleChatClient(String address, int cPort) {
		this.Port = cPort;
		this.sAddr = address;
		setUpGUI();
		establishConnection();
		System.out.println("Setup Finished");
	}

	public void establishConnection() {
		try {
			System.out.println("SimpleChatClient.establishConnection");
			channelGroup = AsynchronousChannelGroup.withFixedThreadPool(Runtime.getRuntime().availableProcessors(),
					Executors.defaultThreadFactory()); // CPU 코어 수만큼 스레드를 관리하는
														// 스레드풀 생성하고 이것을 이용하는
														// 비동기 채널 그룹 생성.
			socketChannel = AsynchronousSocketChannel.open(channelGroup);// 비동기소켓채널생성

			socketChannel.connect(new InetSocketAddress(sAddr, Port), null, new CompletionHandler<Void, Void>() {
				@Override
				public void completed(Void result, Void attachment) {
					
					try {
							System.out.println("[연결 완료: " + socketChannel.getRemoteAddress() + "]");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					receive(); // 서버에서 보낸 데이터 받기!!!!!!!!
					
				}
				
				@Override
				public void failed(Throwable e, Void attachment) {
					System.out.println("[서버 통신 안됨]");
					if (socketChannel.isOpen()) {
						try {
							System.out.println("[연결 끊음]");
						
							if (channelGroup != null && !channelGroup.isShutdown()) {
								channelGroup.shutdownNow(); // 비동기 채널 그룹에 포함된 모든
															// 비동기 채널 닫음.
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
					System.out.println("[From Server to Client : ] " + data);
					incoming.append(data);
					incoming.append("\n");
					ByteBuffer byteBuffer = ByteBuffer.allocate(100);
					socketChannel.read(byteBuffer, byteBuffer, this);//receive() 재호출한구얌
				
				} catch (Exception e) {
				}
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				System.out.println("[메세지 읽을 수 없또]");
				try {
					System.out.println("[연결 끊음]");
					if (channelGroup != null && !channelGroup.isShutdown()) {
						channelGroup.shutdownNow(); // 비동기 채널 그룹에 포함된 모든 비동기 채널
													// 닫음.
					}
				} catch (IOException e) {
				}
			}
		});

	}

	public void send(String data) throws IOException {
		data = "[" + socketChannel.getLocalAddress().toString() + "]" + data;
		Charset charset = Charset.forName("utf-8");
		ByteBuffer byteBuffer = charset.encode(data);
		
		socketChannel.write(byteBuffer, null, new CompletionHandler<Integer, Void>() {
			@Override
			public void completed(Integer result, Void attachment) {
				System.out.println("[보내기 완료]");
			}

			@Override
			public void failed(Throwable exc, Void attachment) {
				System.out.println("[보내기 실패]");
				try {
					System.out.println("[연결 끊음]");
					if (channelGroup != null && !channelGroup.isShutdown()) {
						channelGroup.shutdownNow(); // 비동기 채널 그룹에 포함된 모든 비동기 채널
													// 닫음.
					}
				} catch (IOException e) {
				}
			}
		});

	}

	public void setUpGUI() {
		System.out.println("SimpleChatClient.setUpGUI");
		JFrame frame = new JFrame();
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
<<<<<<< HEAD
		JPanel subPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		subPanel.setLayout(new BorderLayout());
		mainPanel.add("Center", scrollPane);
		subPanel.add("Center", messageBox);
		subPanel.add("East", sendButton);
		subPanel.add("South", hintButton);
=======
		mainPanel.add(scrollPane);
		mainPanel.add(messageBox);
		
		mainPanel.add(sendButton);
>>>>>>> 0306ab12a2190005a6652c92d858a8f33747550c
		sendButton.addActionListener(new SendButtonActivationListener());
		hintButton.addActionListener(new helpButtonActivationListener());
		messageBox.addActionListener(new SendButtonActivationListener());
		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		frame.getContentPane().add(BorderLayout.SOUTH, subPanel);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		hintButton = new JButton("Hint");
		mainPanel.add(hintButton);
		hintButton.setVisible(true);
		hintButton.setBounds(125,15,50,50);
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
	
<<<<<<< HEAD
public class helpButtonActivationListener implements ActionListener {
		
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
		}
	}
=======
public class HintButtonActivationListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//////
		}
	}

>>>>>>> 0306ab12a2190005a6652c92d858a8f33747550c

}