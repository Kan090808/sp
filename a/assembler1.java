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
		ArrayList<String> loc = new ArrayList<String>();
		ArrayList<String> operand = new ArrayList<String>();
		ArrayList<symBol> symTable = new ArrayList<symBol>();
		int start=0,count=0,exit=0;
		String s, progname, startLoc="", currentLoc="";
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
				for(int j=0;j<cut.length;j++){//loop by token
					if(start == 0){
						if(cut[j].equals("START")){//when scanned "START"
							start++;
							type.add(1);
							progname = cut[j-1];
							type.add(4);
							mneList.add(cut[j]);
							type.add(5);
							startLoc = cut[j+1];
							// System.out.println(startLoc);
							currentLoc = startLoc;
						}
					}
					if(start == 1){//when prog started

						if(!cut[j].equals("") && !cut[j].equals(".")){//delete all comments and empty line
							//System.out.println(token.get(j));
							if(!getOpcode(mnemonic,opcode,cut[j]).equals("not found")){
								if(loc.size() == 0){//first line using starting address
									loc.add(currentLoc);
								}
								else{
									currentLoc = locAdd(currentLoc);//curren loc
									loc.add(currentLoc);
								}

								if(j==0 && cut.length == 2){
									mneList.add(cut[j]);
									if(cut[j+1].contains(",")){//index addressing

									}else{
										if(checkSymbol(symTable,cut[j+1]) == 0){//undefined
											symBol obj = new symBol();
											obj.label=cut[j+1];
											symTable.add(obj);
										}
									}
									operand.add(cut[j+1]);
									//System.out.println(cut.length);
								}else if(j==1 && cut.length == 3){
									symBol obj = new symBol();
									obj.label=cut[j-1];
									obj.loc = currentLoc;
									symTable.add(obj);
									mneList.add(cut[j]);
									if(cut[j+1].contains(",")){//index addressing

									}else{
										if(checkSymbol(symTable, cut[j+1]) == 0){
											obj = new symBol();
											obj.label=cut[j+1];
											symTable.add(obj);
										}
										operand.add(cut[j+1]);
									}
								}else if(cut.length>3){
									System.out.println("error");
								}
									
								
							}//else if()
						}
						if(cut[j].equals("END")){
							exit=1;
						}
					}
				}
			}
			
			
		}
		//System.out.println("size: -------"+token.size());
		System.out.println("symTable: -------");
		for(int j=0;j<symTable.size();j++){
			System.out.print(symTable.get(j).label+"  ");
			System.out.println(symTable.get(j).loc);
		}
	}
	public static class symBol{
		String label;
		String loc;
		public symBol(){
			label=null;
			loc=null;
		}
	}
	public static String getOpcode(ArrayList<String> mnemonic, ArrayList<String> opcode, String search) {
		String ans = "not found";
		for(int i=0;i<mnemonic.size();i++){
			if(search.equals(mnemonic.get(i))){
				ans = opcode.get(i);
			}
		}
		return ans;
	}
	public static int checkSymbol(ArrayList<symBol> symTable, String operand) {
		int ans = 0;//0 not found, 1 found, 2 have loc
		for(int i=0;i<symTable.size();i++){
			if(operand.equals(symTable.get(i).label)){
				if(symTable.get(i).loc == null){
					ans=1;
				}else{
					ans=2;
				}
			}else{
				ans=0;//undefined
			}
		}
		return ans;
	}
	public static String locAdd(String currentLoc){
		int loc10 = Integer.valueOf(currentLoc,16);
		loc10 = loc10+3;
		String nextLoc = Integer.toHexString(loc10);
		return nextLoc;
	}
}