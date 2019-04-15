package kr.co.sist.admin.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import kr.co.sist.lunch.admin.run.LunchAdminMain;

public class FileServer extends Thread {
	
	@Override
	public void run() {
		
		ServerSocket server = null;
		
		
		try {
			try {
				server = new ServerSocket(25000);
				Socket client = null;
				DataInputStream dis = null;
				int cnt = 0;
				String[] fileNames = null;
				String[] serverFileNames = null;
				
				List<String> tempFileList = new ArrayList<String>();
				
				DataOutputStream dos = null;
				
				while(true) {
					System.out.println("���� ����");
					client = server.accept();
					System.out.println("������ ����");
					
					dis = new DataInputStream(client.getInputStream());
					
					cnt = dis.readInt(); // Ŭ���̾�Ʈ�� �������� ���ϸ��� ����
					fileNames = new String[ cnt ];
					for(int i = 0; i < cnt; i++) {
						fileNames[i] = dis.readUTF();
						System.out.println( i+"��° ���� "+fileNames[i]);
					} // end for
					
					// ������ �����ϴ� ���ϸ��� �迭�� ����
					serverFileNames = new String[LunchAdminMain.lunchImageList.size()];
					LunchAdminMain.lunchImageList.toArray( serverFileNames );
					
//					System.out.println("���� "+ Arrays.toString(serverFileNames));
//					System.out.println( "Ŭ���̾�Ʈ "+Arrays.toString(fileNames) );

					// Ŭ���̾�Ʈ�� ������ ���ϸ�� ������ ���ϸ��� ���Ͽ� 
					// Ŭ���̾�Ʈ�� ���� ���ϸ��� ���
					for( String tName : LunchAdminMain.lunchImageList) {
						tempFileList.add(tName); // ������ �����ϴ� ���ϸ��� ����Ʈ�� ���
						tempFileList.add("s_"+tName);
					} // end for
					for( String rmName : fileNames ) {
						tempFileList.remove(rmName); // �� ����Ʈ�� ����� Ŭ���̾�Ʈ�� �����ϴ� ���ϸ�� ��ġ�ϴ� ���� �����
						tempFileList.remove("s_"+rmName);
						// �������� �����ϰ� Ŭ���̾�Ʈ�� ������ ���� ���� ���ϸ���� ���´�.
					} // end for
					
					System.out.println("============ ���������� ������ "+tempFileList.size()+"�� ����");
					dos = new DataOutputStream(client.getOutputStream());
					dos.writeInt( tempFileList.size() ); // ������ ������ ������ ������ ������.
					
					for(String fName : tempFileList ) {
						
						fileSend(fName, dos);
						
						try {
							Thread.sleep(1000); // �����ð��� �����Ѵ�. (1��)
						} catch (InterruptedException e) {
							e.printStackTrace();
						} // end catch
						
					} // end for
					
				} // end while
				
				} finally {
					if( server != null ) { server.close();} // end if
				} // end finally
			} catch (IOException ie) {
				JOptionPane.showMessageDialog(null, "������ ������ �� ���� �߽��ϴ�.");
				ie.printStackTrace();
			} // end catch
	} // run
	
	/**
	 * ������ ���ϸ��� ������ Ŭ���̾�Ʈ������ �̹����� �����ϴ� ��
	 * ���ڷ� �޴� Socket�� ���⼭ ���°��� �ƴ� ȣ���ϴ� �ʿ��� ������� �Ѵ�.
	 * @param name
	 * @throws IOException
	 */
	private void fileSend(String fName, DataOutputStream dos) throws IOException{
		
		FileInputStream fis = null;
		
		
		try {
			int fileData = 0; // ������ ũ�� 
			
			fis = new FileInputStream("C:/dev/workspace/lunch_prj/src/kr/co/sist/lunch/admin/img/"+fName);
			byte[] readData = new byte[512];
			
			int fileLen = 0;
			
			while( (fileLen = fis.read(readData)) != -1) {
				fileData++;
			} // end while 
			
			fis.close();
			
			
			dos.writeInt(fileData);
			dos.flush();
			
			dos.writeUTF( fName ); // writeUTF
			
			fis = new FileInputStream("C:/dev/workspace/lunch_prj/src/kr/co/sist/lunch/admin/img/"+fName);
			while( (fileLen = fis.read(readData)) != -1) {
				dos.write(readData, 0, fileLen); // 0 ���� ���� fileLen ����
//				fileData--;
			} // end while
			dos.flush();
			
		} finally {
			if( fis != null ) { fis.close(); } // end if
		} // end finally
		
		
	} // fileSend
	
	
	
	
} // class
