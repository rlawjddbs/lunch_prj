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
					System.out.println("서버 가동");
					client = server.accept();
					System.out.println("접속자 있음");
					
					dis = new DataInputStream(client.getInputStream());
					
					cnt = dis.readInt(); // 클라이언트가 보내오는 파일명의 개수
					fileNames = new String[ cnt ];
					for(int i = 0; i < cnt; i++) {
						fileNames[i] = dis.readUTF();
						System.out.println( i+"번째 파일 "+fileNames[i]);
					} // end for
					
					// 서버에 존재하는 파일명을 배열로 복사
					serverFileNames = new String[LunchAdminMain.lunchImageList.size()];
					LunchAdminMain.lunchImageList.toArray( serverFileNames );
					
//					System.out.println("서버 "+ Arrays.toString(serverFileNames));
//					System.out.println( "클라이언트 "+Arrays.toString(fileNames) );

					// 클라이언트가 보내온 파일명과 서버의 파일명을 비교하여 
					// 클라이언트가 없는 파일명을 출력
					for( String tName : LunchAdminMain.lunchImageList) {
						tempFileList.add(tName); // 서버에 존재하는 파일명을 리스트에 담고
						tempFileList.add("s_"+tName);
					} // end for
					for( String rmName : fileNames ) {
						tempFileList.remove(rmName); // 그 리스트의 목록중 클라이언트에 존재하는 파일명과 일치하는 것을 지우면
						tempFileList.remove("s_"+rmName);
						// 서버에만 존재하고 클라이언트는 가지고 있지 않은 파일목록이 나온다.
					} // end for
					
					System.out.println("============ 서버측에서 파일을 "+tempFileList.size()+"개 보냄");
					dos = new DataOutputStream(client.getOutputStream());
					dos.writeInt( tempFileList.size() ); // 전송할 파일의 개수를 서버로 보낸다.
					
					for(String fName : tempFileList ) {
						
						fileSend(fName, dos);
						
						try {
							Thread.sleep(1000); // 지연시간을 설정한다. (1초)
						} catch (InterruptedException e) {
							e.printStackTrace();
						} // end catch
						
					} // end for
					
				} // end while
				
				} finally {
					if( server != null ) { server.close();} // end if
				} // end finally
			} catch (IOException ie) {
				JOptionPane.showMessageDialog(null, "파일을 보내는 데 실패 했습니다.");
				ie.printStackTrace();
			} // end catch
	} // run
	
	/**
	 * 들어오는 파일명을 가지고 클라이언트측으로 이미지를 전송하는 일
	 * 인자로 받는 Socket은 여기서 끊는것이 아닌 호출하는 쪽에서 끊어줘야 한다.
	 * @param name
	 * @throws IOException
	 */
	private void fileSend(String fName, DataOutputStream dos) throws IOException{
		
		FileInputStream fis = null;
		
		
		try {
			int fileData = 0; // 파일의 크기 
			
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
				dos.write(readData, 0, fileLen); // 0 에서 부터 fileLen 까지
//				fileData--;
			} // end while
			dos.flush();
			
		} finally {
			if( fis != null ) { fis.close(); } // end if
		} // end finally
		
		
	} // fileSend
	
	
	
	
} // class
