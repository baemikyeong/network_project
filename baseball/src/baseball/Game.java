package baseball;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Game extends Remote{

	public void randomInt() throws RemoteException;
	public void inputUserNumber1(String in1) throws RemoteException;
	public void countSB(int a[],int b[]) throws RemoteException;
	public int getX()  throws RemoteException;
	public int getY()  throws RemoteException;
	public int getCount() throws RemoteException;
	public boolean getValue() throws RemoteException;
	
	
}
