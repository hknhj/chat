package productMangement;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONObject;

public class ProductServer {
	
	ServerSocket serverSocket;
	List<Product> list = new ArrayList<>();
	
	public void start() throws IOException {
		serverSocket=new ServerSocket(50001);
		
		Thread thread = new Thread(()->{
			try {
				while(true) {
					//서버소켓 accept하고 클라이언트와 연결됨
					Socket socket = serverSocket.accept();
					SocketClient socketClient = new SocketClient(this, socket);
				}	
			}catch (IOException e) {
			}
		});
		thread.start();
	}
	
	public void stop() {
		try {
			serverSocket.close();
			System.out.println("[서버] 종료됨");
		} catch(IOException e) {}
	}
	
	public static void main(String[] args) {
		try {
			ProductServer productServer = new ProductServer();
			productServer.start();
			
			System.out.println("------------------------------------");
			System.out.println("서버를 종료하려면 q 또는 Q를 입력하고 Enter");
			System.out.println("------------------------------------");
			
			Scanner scanner = new Scanner(System.in);
			while(true) {
				String key = scanner.nextLine();
				if(key.equals("q")) break;
			}
			
			scanner.close();
			productServer.stop();
		} catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}
}