package baseball;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Information extends Remote{

	public void Inform_gameRule() throws RemoteException;
}
