package test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import test.SessionConnection.SessionListener;

/**
 * [���ӹ���]
 * 
 **/

public class SessionManager extends Thread implements SessionListener {
	/*
	 * SOCKET�ͻ����б�
	 */
	private List<SessionConnection> mClientList;

	public SessionManager() {
		mClientList = new ArrayList<SessionConnection>();
	}

	@Override
	public synchronized void addSessionConnection(
			SessionConnection mSessionConnection) {
		mClientList.add(mSessionConnection);
		for (SessionConnection c : mClientList) {
			System.out.println("�Ѿ������socket��" + c.getmClient());
		}
	}

	@Override
	public synchronized void removeSessionConnection(
			SessionConnection mSessionConnection) {
		boolean isExit = false;
		for (SessionConnection c : mClientList) {
			if (c == mSessionConnection) {
				isExit = true;
				break;
			}
		}
		if (isExit) {
			mClientList.remove(mSessionConnection);
		}
	}

	public void run() {

		while (!isInterrupted()) {

			SessionConnection delClient = null;

			for (SessionConnection c : mClientList) {
				if (c == null || c.tryToReleaseConnect2TimeOut()) {
					c.releaseConnection();
					delClient = c;
					break;
				}
			}
			if (delClient != null) {
				mClientList.remove(delClient);
				delClient = null;
				continue;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				break;
			}
		}
	}

}
