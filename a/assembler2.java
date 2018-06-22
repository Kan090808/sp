import java.io.*;
import java.util.*;

public class assembler2{
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
		//0 loc, 1 label, 2 direct addressing, 3 index addressing, 
		//4 other, 5 operand, 6 index
		ArrayList<String> mneList = new ArrayList<String>();
		String[] notmne = {"BYTE", "RESB", "WORD", "RESW"};
		ArrayList<outPut> output = new ArrayList<outPut>();
		ArrayList<symBol> symTable = new ArrayList<symBol>();
		int start=0,lineN=0,exit=0;
		String s, progname, startLoc="", currentLoc="";

		while(exit==0){
			lineN++;
			// System.out.println("count"+count);
			// System.out.println("size"+token.size());
			//System.out.println("lineN: "+lineN);
			s = br.readLine();
			if(s.contains(".")){//delete all note first
				String cut[] = s.split("\\.");//split by fullstop
				if(cut.length>=1)//if can be cut
					s = cut[0];
				else
					s = null;
			}
			
			if(s != null){
				if(s.trim().length()>0){//if lines have word(not included 
					// spaces before first character and last character)


					String cut[] = s.trim().split("\\s+");//split by all space
					//System.out.println("cut: "+cut.length);

					if(cut.length<=3){
						if(start == 0){
							if(cut.length==3){
								if(cut[1].equals("START")){
									progname = cut[0];
									startLoc = cut[2];
									outPut result = new outPut();
									result.loc = startLoc;
									result.label = progname;
									result.mnemonic = cut[1];
									result.operand = startLoc;
									result.lineN = lineN;
									output.add(result);
									currentLoc = startLoc;
									start = 1;
								}
							}
						}else if(start == 1){
							if(cut.length ==1){
								if(cut[0].equals("RSUB")){
									outPut result = new outPut();
									result.loc = currentLoc;
									result.mnemonic = cut[0];
									result.lineN = lineN;
									output.add(result);
									currentLoc = locAdd(currentLoc, 3);
								}else if(!getOpcode(mnemonic,opcode,cut[0]).equals("not found") || !notMne(notmne, cut[0]).equals("not found")){
									System.out.println("no operand");
								}else{
									System.out.println("error1 at "+lineN);
								}
							}else if(cut.length == 2) {
								if(!getOpcode(mnemonic,opcode,cut[0]).equals("not found")){
									outPut result = new outPut();
									result.loc = currentLoc;
									result.mnemonic = cut[0];
									result.operand = cut[1];
									result.lineN = lineN;
									output.add(result);
									currentLoc = locAdd(currentLoc, 3);
								}else if(cut[0].equals("RSUB")){
									outPut result = new outPut();
									result.loc = currentLoc;
									result.mnemonic=cut[0];
									result.operand=cut[1];
									result.lineN = lineN;
									output.add(result);
									currentLoc = locAdd(currentLoc, 3);


								}else if(cut[1].equals("RSUB")){
									outPut result = new outPut();
									result.loc = currentLoc;
									result.label = cut[0];
									result.mnemonic=cut[1];
									result.lineN = lineN;
									output.add(result);
									currentLoc = locAdd(currentLoc, 3);

								}else if(cut[0].equals("END")){
									outPut result = new outPut();
									result.loc = currentLoc;
									result.mnemonic=cut[0];
									result.operand=cut[1];
									result.lineN = lineN;
									output.add(result);
									exit = 1;

								}else{
									System.out.println("error2 at "+lineN);
								}
							}else if(cut.length == 3){
								if(!getOpcode(mnemonic,opcode,cut[0]).equals("not found")){
									if(cut[1].contains(",")){
										outPut result = new outPut();
										result.loc = currentLoc;
										result.mnemonic = cut[0];
										result.operand = cut[1];
										result.addressing = -1;
										result.lineN = lineN;
										output.add(result);
										currentLoc = locAdd(currentLoc, 3);

									}else{
										System.out.println("error3 at "+lineN);
									}
								}else if(!getOpcode(mnemonic,opcode,cut[1]).equals("not found")){
									outPut result = new outPut();
									result.loc = currentLoc;
									result.label = cut[0];
									result.mnemonic = cut[1];
									result.operand = cut[2];
									result.lineN = lineN;
									output.add(result);
									currentLoc = locAdd(currentLoc, 3);

								}else if(!notMne(notmne, cut[1]).equals("not found")){
									outPut result = new outPut();
									result.loc = currentLoc;
									result.label = cut[0];
									result.mnemonic = cut[1];
									result.operand = cut[2];
									result.lineN = lineN;
									output.add(result);
									if(notMne(notmne,cut[1]).equals("BYTE")){
										String[] bs = cut[2].split("\\'");
										if(bs[0].equals("C")){
											currentLoc = locAdd(currentLoc, bs[1].length());
										}else if(bs[0].equals("X"))
											currentLoc = locAdd(currentLoc, 1);

									}else if(notMne(notmne,cut[1]).equals("RESB")){
										int loc10 = Integer.parseInt(cut[2]);
										currentLoc = locAdd(currentLoc, loc10);
									}else if(notMne(notmne,cut[1]).equals("WORD")){
										currentLoc = locAdd(currentLoc, 3);
									}else if(notMne(notmne,cut[1]).equals("RESW")){
										int loc10 = Integer.parseInt(cut[2]);
										currentLoc = locAdd(currentLoc, loc10*3);
									}
								}
							}
							
						}else{
							System.out.println("more than one START");
						}
					}else{
						System.out.println("error4 at "+lineN);
					}
				}
				
			}

		}

		for(int i = 0; i<output.size(); i++){
			System.out.print(output.get(i).loc+"	");
			System.out.print(output.get(i).label+"	");
			System.out.print(output.get(i).mnemonic+"	");
			System.out.print(output.get(i).operand+"	");
			System.out.print(output.get(i).addressing+"	");
			System.out.println(output.get(i).lineN);
		}
		//System.out.println("size: -------"+token.size());
		
	}

	public static class outPut{
		String loc;
		String label;
		String mnemonic;
		String operand;
		int addressing;
		int lineN;
		public outPut(){
			loc=null;
			label=null;
			mnemonic=null;
			operand=null;
			addressing=0;
			lineN=0;
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
	public static String notMne(String[] notmne, String search){
		String ans="not found";
		for(int i=0;i<notmne.length;i++){
			if(search.equals(notmne[i])){
				ans = notmne[i];
			}
		}
		return ans;
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
	public static String locAdd(String currentLoc, int n){
		int loc10 = Integer.valueOf(currentLoc,16);
		loc10 = loc10+n;
		String nextLoc = Integer.toHexString(loc10);
		return nextLoc;
	}
}