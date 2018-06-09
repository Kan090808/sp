import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class opcode {
	public static void main(String[] args) {
		BufferedReader reader;
		int i = 0;
		int n = 0;
		int exit = 0;
		Scanner scanner = new Scanner(System.in); 
		//get n
		try {
			reader = new BufferedReader(new FileReader("opCode.txt"));
			String line = reader.readLine();
			while(line != null) {
				n++;
				line = reader.readLine();
			}
			reader.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
		String[] data = new String[n];
		String[] mnemonic = new String[n];
		String[] opcode = new String[n];
		//read data into array
		try {
			reader = new BufferedReader(new FileReader("opCode.txt"));
			String line = reader.readLine();
			while(line != null) {
				String[] splited = line.split(" ");
				mnemonic[i] = splited[0];
				opcode[i] = splited[1];
				i++;
				line = reader.readLine();
			}
			reader.close();
		}catch (IOException e) {
			e.printStackTrace();
		}

		while(exit != 1) {
			System.out.println();
			System.out.println("----------------");
			System.out.println("歡迎查詢，離開時輸入88");
			String search = scanner.next(); // Scans the next token of the input as an int.
			
			
			if(search.equals("88"))
				break;
			search = search.toUpperCase();
			if(sequential(mnemonic, opcode, search) != "not found"){
				
				System.out.println("其opcode爲： " + sequential(mnemonic, opcode, search));
			}
			else{
				System.out.println("差無資料");
			}
			
		}
	}

	public static String sequential(String[] m, String[] o, String search) {
		String ans = "not found";
		for (int i = 0; i < m.length; i++){
        	if (m[i].equals(search)){
        		ans = o[i];

	        	break;
	        }

		}

		return ans;

	}
}