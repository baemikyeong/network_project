package baseball;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

class GameImpl extends UnicastRemoteObject implements Game{
	
	
	private static final long serialVersionUID = 1L;
	public static int MAX_COUNT = 4;  
	public static int MAX_INPUT = 20; 
	int x=0, y=0;	
	private boolean val=false;
	private int baseNumber[] = new int[MAX_COUNT];	
	private int userNumber[] = new int[MAX_COUNT];	
	private int cnt=0;
	
	GameImpl() throws RemoteException{ super();}
	
	public void randomInt() throws RemoteException{ 
		do{baseNumber[0] = (int)(Math.random()*10);
		}while(baseNumber[0]==0);
		
		for(int i=1;i<baseNumber.length;i++){
			baseNumber[i] = (int)(Math.random()*10);
			for(int j=0;j<i;j++){
				while(baseNumber[i]==baseNumber[j] || baseNumber[i]==0){
					baseNumber[i] = (int)(Math.random()*10); j=0;
				}
			}
		}	
	}

	public void inputUserNumber1(String in1) throws RemoteException{ 
				
		String in = in1;
		for(int i=0;i<userNumber.length;i++){
			userNumber[i]=in.charAt(i)-48;
		}
		countSB(baseNumber,userNumber);
		
	}
	public void countSB(int a[],int b[]) throws RemoteException{ 
		
		x=0; y=0;
		for(int i=0;i<a.length;i++){
			for(int j=0;j<b.length;j++){
				if(b[i]==a[j])
					if(i==j) x+=1;
					else y+=1;
			}
		}
		cnt++;
		if(x==MAX_COUNT) {val=true;return;} 
	}
	public int getX()  throws RemoteException{	return x;}
	public int getY()  throws RemoteException{	return y;}
	
	public int getCount() throws RemoteException{	return cnt; } 
	public boolean getValue() throws RemoteException{	return val; } 
	
}