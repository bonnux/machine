package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * [���ӻỰ,��ҪΪ��д�߳�]
 * 
 **/

public class SessionConnection {
	private Socket mClient;

	private ReadThread mReadThread;
	private WriteThread mWriteThread;
	private InputStream inputStream;
	private OutputStream outputStream;

	private List<byte[]> mListSend;
	private SessionListener mSessionListener;

	/**
	 * ��һ�ν��յ����ݵ�ʱ��
	 */
	private long mlLastRcvDataTime = System.currentTimeMillis();
	/**
	 * ��һ�η������ݵ�ʱ��
	 */
	private long mlLastSendDataTime = System.currentTimeMillis();

	public SessionConnection(Socket mClient, SessionListener mSessionListener) {
		this.mClient = mClient;
		this.mSessionListener = mSessionListener;
		try {
			inputStream = mClient.getInputStream();
			outputStream = mClient.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mListSend = new ArrayList<byte[]>();

	}

	public Socket getmClient() {
		return mClient;
	}

	public void setmClient(Socket mClient) {
		this.mClient = mClient;
	}

	public void registerSession() {

		mReadThread = new ReadThread();
		mReadThread.start();

		mWriteThread = new WriteThread();
		mWriteThread.start();
		System.out.println("registerSession");
		mSessionListener.addSessionConnection(this);
	}

	public void releaseConnection() {
		mReadThread.interrupt();
		mWriteThread.interrupt();
		try {
			inputStream.close();
			outputStream.close();
			mClient.close();
			mSessionListener.removeSessionConnection(this);
			System.out.println("�ͻ���:" + mClient + "�Ͽ�����:");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void sendDate(String obj) {

		byte[] buf = obj.getBytes();
		mListSend.add(buf);

	}

	/**
	 * ������
	 */
	public void sendXT() {
		long TimeOut = Math
				.abs(System.currentTimeMillis() - mlLastSendDataTime);
		if (TimeOut >= 6000) {
			mlLastSendDataTime = System.currentTimeMillis();
			sendDate("xtb");
		}
	}

	public void sendHostname() throws UnknownHostException {
		InetAddress addr = InetAddress.getLocalHost();
		String hostname = addr.getHostName();
		sendDate(hostname);
	}//

	/**
	 * ���ӳ�ʱ��û�н��յ�����
	 * 
	 * @return
	 */
	public boolean tryToReleaseConnect2TimeOut() {
		boolean bRet = false;

		boolean bTimeOut = Math.abs(System.currentTimeMillis()
				- mlLastRcvDataTime) > 6000 * 3 ? true : false;
		if (bTimeOut) {
			bRet = true;
		}
		return bRet;
	}

	private class ReadThread extends Thread {
		public void run() {
			String str = "";
			int n = 0;
			byte[] buffer;
			while (!isInterrupted()) {
				try {
					buffer = new byte[2048];
					n = inputStream.read(buffer);
					str = new String(buffer, 0, n);
					mlLastRcvDataTime = System.currentTimeMillis();
					if (str.equals("xtb")) {
						System.out.println("�����:" + mClient + " ������-->��" + str);
					}
					} catch (IOException e) {
						e.printStackTrace();
						releaseConnection();
						break;
				}
			}
		}
	}

	private class WriteThread extends Thread {
		public void run() {
			while (!isInterrupted()) {
				try {
					for (byte[] data : mListSend) {
						outputStream.write(data);
					}
					mListSend.clear();
					sendXT();
					//sendHostname();
				} catch (IOException e) {
					e.printStackTrace();
					releaseConnection();
					break;
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					break;
				}
			}
		}
	}

	public interface SessionListener {
		public void addSessionConnection(SessionConnection mSessionConnection);

		public void removeSessionConnection(SessionConnection mSessionConnection);
	}
}
