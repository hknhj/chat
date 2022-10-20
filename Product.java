package product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Product {
	private int no;
	private String name;
	private int price;
	private int stock;
	
	@Override
	public String toString() {
		return no+"    "+name+"              "+price+"     "+stock;
	}
}