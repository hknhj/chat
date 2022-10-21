package productMangement;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import org.json.JSONObject;
import org.json.JSONArray;

public class ProductClient {
	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	
	//서버와 연결
	public void connect() throws IOException{
		socket=new Socket("localhost", 50001);
		dis=new DataInputStream(socket.getInputStream());
		dos=new DataOutputStream(socket.getOutputStream());
		System.out.println("클라이언트가 연결되었습니다");
		
	}
	
	//서버와 연결끊기
	public void unconnet() throws IOException{
		socket.close();
	}
	
	//서버에서 데이터 받기
	public void receive() {

		Thread thread = new Thread(()->{
			try {
				while(true) {
					String json = dis.readUTF();
					System.out.println(json);
					JSONObject root = new JSONObject(json);
					if(root.getInt("datasize")==0) {
						printMenu();
					}else {
						JSONArray array = root.getJSONArray("data");
						printMenuarr(array);
					}
				}
			} catch(Exception e) {
			}
		});
		thread.start();
	}
	
	//서버로 데이터 보내기
	public void send(String json) throws IOException {
		dos.writeUTF(json);
		dos.flush();
		//System.out.println(json);
	}
	
	public void printMenuarr(JSONArray array) { //매개변수로 서버에 데이터에 값이 있는지 받기
		System.out.println("[상품 목록]");
		System.out.println("-----------------------------------------------");
		System.out.println("no    name                  price      stock");
		System.out.println("-----------------------------------------------");
		for(int i=0;i<array.length();i++) {
			System.out.println(array.get(i));
		}
		System.out.println("-----------------------------------------------");
	}
	
	public void printMenu() {
		System.out.println("[상품 목록]");
		System.out.println("-----------------------------------------------");
		System.out.println("no    name                  price      stock");
		System.out.println("-----------------------------------------------");
		System.out.println("-----------------------------------------------");
	}
	
	public String option1(Scanner scanner, JSONObject json) {
		System.out.println("[상품 생성]");
		System.out.print("상품 이름 : ");
		String name = scanner.nextLine();
		System.out.print("상품 가격 : ");
		int price = scanner.nextInt();
		scanner.nextLine();
		System.out.print("상품 재고 : ");
		int stock = scanner.nextInt();
		scanner.nextLine();
		
		json = new JSONObject();
		json.put("menu", 1);
		json.put("name", name);
		json.put("price", price);
		json.put("stock", stock);
		String send = json.toString();
		
		return send;
	}
	
	public String option2(Scanner scanner, JSONObject json) {
		System.out.println("[상품 수정]");
		System.out.print("상품 번호 : ");
		int no = scanner.nextInt();
		scanner.nextLine();
		System.out.print("상품 이름 : ");
		String name = scanner.nextLine();
		System.out.print("상품 가격 : ");
		int price = scanner.nextInt();
		scanner.nextLine();
		System.out.print("상품 재고 :");
		int stock = scanner.nextInt();
		scanner.nextLine();
		
		json.put("menu", 2);
		json.put("no", no);
		json.put("name", name);
		json.put("price", price);
		json.put("stock", stock);
		
		String send = json.toString();
		
		return send;
	}
	
	public String option3(Scanner scanner, JSONObject json) {
		System.out.println("[상품 삭제]");
		System.out.print("상품 번호 ; ");
		int no = scanner.nextInt();
		
		json.put("menu", 3);
		json.put("no", no);
		
		String send = json.toString();
		
		return send;
	}
	
	public static void main(String[] args) {
		try {
			ProductClient productClient = new ProductClient();
			productClient.connect();
			JSONObject json=new JSONObject();
			json.put("menu", 0);
			String start = json.toString();
			productClient.send(start);
			productClient.receive();
			Scanner scanner = new Scanner(System.in);
			while(true) {
				int key = scanner.nextInt();
				scanner.nextLine();
				switch(key) {
					case 1: 
						json=new JSONObject();
						String data1 = productClient.option1(scanner, json);
						productClient.send(data1);
						break;
				
					case 2: 
						json=new JSONObject();
						String data2 = productClient.option2(scanner, json);
						productClient.send(data2);
						break;
						
					case 3: 
						String data3 = productClient.option3(scanner, json);
						productClient.send(data3);
						break;
						
					case 4: 
						System.out.println("클라이언트 종료");
						scanner.close();
						productClient.unconnet();
						break;
					default :
						System.out.println("다시 입력하세요");
						continue;
				}
			}
		} catch (IOException e) {
			
		}
	}
}