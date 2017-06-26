package baseball;

import java.nio.channels.AsynchronousSocketChannel;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import baseball.ServerTest2;
import baseball.SimpleChatServer.Client;
public interface Information extends Remote{

	public String Inform_gameRule() throws RemoteException;
	public void set_client(int a) throws RemoteException;
	public int get_client() throws RemoteException;
}
