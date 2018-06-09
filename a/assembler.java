import java.io.*;
import java.util.*;

public class assembler{
	public static void main(String[] args) throws Exception{

		//read and create opcode table
		BufferedReader br = new BufferedReader(new FileReader("opCode.txt"));//read file
		ArrayList<String> mnemonic = new ArrayList<String>();
		ArrayList<String> opcode = new ArrayList<String>();
		String line;//for readline
		while((line = br.readLine()) != null) {//read until empty line
			String[] splited = line.split(" ");
			mnemonic.add(splited[0]);
			opcode.add(splited[1]);
		}
		br.close();

		//read prog
		br = new BufferedReader(new FileReader("testprog1.S"));
		ArrayList<String> token = new ArrayList<String>();
		ArrayList<String> type = new ArrayList<String>();
		int start=0,count=0,exit=0;
		String s;
		while((s = br.readLine()) != null || exit==0){
			//count++;
			// System.out.println(count);
			//System.out.println(s);

			if(s.contains(".")){//delete all note first
				String cut[] = s.split("\\.");//split by fullstop
				if(cut.length>=1)//if can be cut
					s = cut[0];
			}
			
			if(s.trim().length()>0){//if lines have word(not included 
				// spaces before first character and last character)
				String cut[] = s.split("\\s+");//split by all space
				for(int j=0;j<cut.length;j++){
					if(start == 1){//when prog started
						if(!cut[j].equals("") && !cut[j].equals(".")){
							token.add(cut[j]);//cut and add into token list
						}
					}
					if(cut[j].equals("START")){//when scanned "START"
						start++;
						token.add((cut[j-1]));
						token.add((cut[j]));
						if(start>=2)//to
							System.out.println("START more than one");
					}
					if(cut[j].equals("END")){
						exit=1;
					}
					
				}
			}
			
			
		}
		for(int j=0;j<token.size();j++){
			System.out.println(token.get(j));
		}
	}
}