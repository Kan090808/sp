import java.io.*;
import java.util.*;

public class opcode1	 {
	public static void main(String[] args) {
		BufferedReader reader;
		Scanner scanner = new Scanner(System.in);
		ArrayList<String> mnemonic = new ArrayList<String>();
		ArrayList<String> opcode = new ArrayList<String>();
		//get n
		try {
			reader = new BufferedReader(new FileReader("opCode.txt"));
			while(exit == 0) {
				String line = reader.readLine();
				if(line == null)
					break;
				String[] splited = line.split(" ");
				mnemonic.add(splited[0]);
				opcode.add(splited[1]);
			}
			reader.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("mnemonic: ");
		for(int j=0; j<i; j++) {
			System.out.print(mnemonic.get(j)+"    ");
			System.out.println(opcode.get(j));
		}
		//read data into array	
	}
}