package test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import java.net.UnknownHostException;

public class ClientMainThread extends Thread {
	SessionManager mSessionManager;
	public void run() {
		try {
			mSessionManager = new SessionManager();
			Socket socket = new Socket(StaticValue.SERVER_HOST,
					StaticValue.SERVER_PORT);
			System.out.println("---连接到服务器端---" + socket);
			SessionConnection mSessionConnection = new SessionConnection(
					socket, mSessionManager);
			mSessionConnection.registerSession();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ClientMainThread mMainThread = new ClientMainThread();
		mMainThread.start();
	}
}
