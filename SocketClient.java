package productMangement;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.net.InetAddress;

import org.json.JSONObject;
import org.json.JSONArray;

public class SocketClient {
	ProductServer productServer;
	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	List<Product> list;
	int numOfData;
	
	public SocketClient(ProductServer productServer, Socket socket) {
		try {
			this.productServer=productServer;
			this.socket=socket;
			this.dis=new DataInputStream(socket.getInputStream());
			this.dos=new DataOutputStream(socket.getOutputStream());
			this.numOfData=productServer.list.size();
			this.list=productServer.list;
			receive();
			
		} catch (IOException e) {
		}
	}
	
	public void receive() {
		Thread thread = new Thread(()->{
			try {
				while(true) {
					String receiveJson = dis.readUTF();
					
					JSONObject jsonObject = new JSONObject(receiveJson);
					int menu = jsonObject.getInt("menu");
					
					switch(menu) {
//						case 0:
//							JSON
						case 1:
							//create
							//데이터전송 받음
							//데이터를 받아서 서버에 저장했음
							int a = create(jsonObject);
							//이제 양식에따라 데이터를 보내면 됨
							sendData(a);
							break;
							
							
						case 2:
							//update
							int b = update(jsonObject);
							//저장하고 클라이언트에게 데이터 전송
							sendData(b);
							break;
							
						case 3:
							//remove
							//json파일로 온 제품의 넘버로 해당 제품 삭제
							int c = remove(jsonObject);
							//저장하고 클라이언트에게 데이터 전송
							sendData(c);
							break;
					}
				}
			} catch(IOException e) {
			}
		});
	}
	
	//데이터 저장하는 함수
	public int create(JSONObject data) {
		//JSON파일에서 no가 0일 경우 맨 뒤에 넣는다.
		//JSON파일에서 1이오면 0번에 저장, 2가 오면 1번에 저장
		//2개있는데 데이터가 하나 들어오면 2번에저장되어야하는데 함수가 index-1이니까 1더하셈
		//list에 추가할때만 -1하고 
		int no = list.size();
		String name = data.getString("name");
		int price = data.getInt("price");
		int stock = data.getInt("stock");
		
		Product product = new Product(no,name,price,stock);
		list.add(no-1,product);
		
		int status = 1;
		return status;
	}
	
	public int update(JSONObject data) {
		int no = data.getInt("no");
		String name = data.getString("name");
		int price = data.getInt("price");
		int stock = data.getInt("stock");
		
		Product product = new Product(no,name,price,stock);
		list.set(no-1, product);
		
		int status = 1;
		return status;
	}
	
	public int remove(JSONObject data) {
		int no = data.getInt("no");
		
		list.remove(no-1);
		//remove하면 숫자도 바꿔줘야됨
		for(Product product : list) {
			//리스트에서 해당 product의 위치를 찾고 그것을 product의 no로 바꿔준다
			product.setNo(list.indexOf(product)+1);
		}
		
		int status = 1;
		return status;
	}
	
	public void sendData(int status) throws IOException{
		JSONObject root = new JSONObject();
		if(status ==1) {
			root.put("result", "success");
		} else {
			root.put("result", "fail");
		}
		
		JSONArray list = new JSONArray();
		for(Product product : this.list) {
			list.put(product);
		}
		root.put("data", list);
		
		String json = root.toString();
		send(json);
		
	}
	
	public void send(String data) throws IOException {
		dos.writeUTF(data);
		dos.flush();
	}
	
	
}
