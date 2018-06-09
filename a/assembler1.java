import java.io.*;
import java.util.*;

public class assembler1{
	public static void main(String[] args) throws Exception{

		//read and create opcode table
		BufferedReader br = new BufferedReader(new FileReader("opCode.txt"));//read file
		String line;//for readline
		ArrayList<String> mnemonic = new ArrayList<String>();
		ArrayList<String> opcode = new ArrayList<String>();
		while((line = br.readLine()) != null) {//read until empty line
			String[] splited = line.split(" ");
			mnemonic.add(splited[0]);
			opcode.add(splited[1]);
		}
		br.close();

		//read prog
		br = new BufferedReader(new FileReader("testprog1.S"));
		ArrayList<String> token = new ArrayList<String>();
		ArrayList<Integer> type = new ArrayList<Integer>();
		//0 loc, 1 label, 2 direct addressing, 3 index addressing, 
		//4 other, 5 operand, 6 index
		ArrayList<String> mneList = new ArrayList<String>();
		ArrayList<String> mneLoc = new ArrayList<String>();
		ArrayList<String> symLabel = new ArrayList<String>();
		ArrayList<String> symLoc = new ArrayList<String>();
		ArrayList<symBol> symTable = new ArrayList<symBol>();
		int start=0,count=0,exit=0;
		String s, progname, startLoc;
		while((s = br.readLine()) != null || exit==0){
			// count++;
			// System.out.println("count"+count);
			// System.out.println("size"+token.size());
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
							//System.out.println(token.get(j));
							if(!getOpcode(mnemonic,opcode,cut[j]).equals("not found")){
								if(cut[j].contains(",")){

								}else {
									if(j == 0){
										if(cut.length==2){
											mneList.add(cut[j]);
											symBol obj = new symBol();
											obj.label = cut[j+1];
											obj.loc = "empty";
											symbolTable.add(obj);
											symLabel.add(cut[j+1]);
										}else{
											System.out.println("error");
										}
									}else if(j==1){
										if(cut.length==3) {
											
											mneList.add(cut[j]);
										}else{
											System.out.println("error");
										}
									}
								}
							}//else if(cut[j])
						}
						if(cut[j].equals("END")){
							exit=1;
						}
					}else{
						if(cut[j].equals("START")){//when scanned "START"
							start++;
							type.add(1);
							progname = cut[j-1];
							type.add(4);
							mneList.add(cut[j]);
							type.add(5);
							startLoc = cut[j+1];

						}
					}
					
					
				}
			}
			
			
		}
		System.out.println("size: -------"+token.size());
		System.out.println("token: -------");
		for(int j=0;j<token.size();j++){
			System.out.println(token.get(j));
		}
	}
	public static class symBol{
		public symBol(){
			String label;
			String loc;
		}
	}
	public static String getOpcode(ArrayList<String> mnemonic, ArrayList<String> opcode, String search) {
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