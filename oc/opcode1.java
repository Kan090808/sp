import java.io.*;
import java.util.*;

public class opcode1	{
	public static void main(String[] args) throws Exception{
		BufferedReader reader = new BufferedReader(new FileReader("opCode.txt"));;
		Scanner scanner = new Scanner(System.in);
		ArrayList<String> mnemonic = new ArrayList<String>();
		ArrayList<String> opcode = new ArrayList<String>();
		//get n
		int exit=0;
		String line;
		while((line = reader.readLine()) != null) {
			String[] splited = line.split(" ");
			mnemonic.add(splited[0]);
			opcode.add(splited[1]);
		}
		reader.close();
	
		// System.out.println("mnemonic: ");
		// for(int j=0; j<mnemonic.size(); j++) {
		// 	System.out.print(mnemonic.get(j)+"    ");
		// 	System.out.println(opcode.get(j));
		// }
		//read data into array	
		while(exit != 1) {
			System.out.println();
			System.out.println("----------------");
			System.out.println("歡迎查詢，離開時輸入88");
			String search = scanner.next(); // Scans the next token of the input as an int.
			
			if(search.equals("88"))
				break;
			search = search.toUpperCase();
			if(searchOp(mnemonic, opcode, search) != "not found"){
				System.out.println("其opcode爲： " + searchOp(mnemonic, opcode, search));
			}
			else{
				System.out.println("差無資料");
			}
			
		}
	}
	public static String searchOp(ArrayList<String> mnemonic,ArrayList<String> opcode, String search){
		String ans = "not found";
		for(int i=0;i<mnemonic.size();i++){
			if(search.charAt(0) == mnemonic.get(i).charAt(0)){
				if(search.equals(mnemonic.get(i))){
					ans = opcode.get(i);
				}
			}
		}
		return ans;
	}
}