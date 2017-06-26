package baseball;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import baseball.SimpleChatServer.Client;

public class InfomationImpl extends UnicastRemoteObject implements Information{
	
	protected InfomationImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
	private static final long serialVersionUID = 1L;
	public static int client;
	public String Inform_gameRule() throws RemoteException{
		String rule = "Select number from 0 to 9 and Hide them"
				+ "\n User should find out the hidden numbers and the sequence, too"
				+ ".\n If one digit fits to the position, it is expressed as 1 strike, 1S"
				+ "\nThe position is incorrect, but if you find a hidden number, it's 1 ball, 1B"
				+ "\n you win by matching four hidden numbers to the position. 4S";
		return rule;
	};
	
	public void set_client(int a) throws RemoteException{
		client = a;
		System.out.println(a);
	};
	
	public int get_client() throws RemoteException{
		return client;
	};
}
