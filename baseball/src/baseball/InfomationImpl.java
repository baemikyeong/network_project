package baseball;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class InfomationImpl extends UnicastRemoteObject implements Information{
	
	protected InfomationImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
	private static final long serialVersionUID = 1L;
	public void Inform_gameRule() throws RemoteException{
		System.out.println("0~9 까지의 숫자 중에서 4개를 뽑아 숨겨둡니다.");
		System.out.println("사용자는 이 숨겨진 숫자 4개를 찾아야 하는데, 숫자  뿐 아니라 순서도 맞추어야 합니다.");
		System.out.println("하나의 숫자가 위치까지 맞으면 1스트라이크, 1S라고 표현합니다.");
		System.out.println("위치는 틀렸지만 숨겨진 숫자를 찾으면 1볼, 1B라고 표현합니다.");
		System.out.println("4S, 즉 4개의 숨겨진 숫자를 위치까지 맞추면 승리하는 게임입니다.");

	};
}
