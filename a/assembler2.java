import java.io.*;
import java.util.*;

public class assembler2{
	public static void main(String[] args) throws Exception{
		double starttime = System.currentTimeMillis();
		//read and create opcode table
		BufferedReader br = new BufferedReader(new FileReader("opCode.txt"));//read file
		String line;//for readline
		ArrayList<Mne> opTable = new ArrayList<Mne>();
		while((line = br.readLine()) != null) {//read until empty line
			String[] splited = line.split(" ");
			Mne mne = new Mne();
			mne.name = splited[0];
			mne.opcode = splited[1];
			opTable.add(mne);
		}
		br.close();
		PrintWriter writer = new PrintWriter("object.txt", "UTF-8");
		//read prog
		br = new BufferedReader(new FileReader("testprog1.S"));
		//0 loc, 1 label, 2 direct addressing, 3 index addressing, 
		//4 other, 5 operand, 6 index
		String[] notmne = {"BYTE", "RESB", "WORD", "RESW"};
		ArrayList<outPut> output = new ArrayList<outPut>();
		ArrayList<symBol> symTable = new ArrayList<symBol>();
		int start=0,lineN=0,exit=0, error=0;
		String s, progname="", startLoc="", endLoc="", currentLoc="";

		while((s = br.readLine())!= null){
			lineN++;
			if(s != null && s.contains(".")){//delete all comments first
				String cut[] = s.split("\\.");//split by fullstop
				if(cut.length>=1)//if can be cut
					s = cut[0];
				else
					s = null;//line is only "."
			}
			
			if(s != null){
				if(s.trim().length()>0){//if lines have word(not included 
					// spaces before first character and last character)
					s = s.toUpperCase();
					String cut[] = s.trim().split("\\s+");//split by all space
					if(cut.length == 4){
						cut[2] = cut[2]+cut[3];
						cut = Arrays.copyOf(cut, cut.length-1);
					}
					if(cut.length<=3){
						if(start == 0){
							if(cut.length==3){
								if(cut[1].equals("START")){
									if(cut[2].matches("[0-9A-F]+")){
										progname = cut[0];
										startLoc = cut[2];
										outPut result = new outPut();
										result.loc = startLoc;
										result.label = progname;
										result.mnemonic = cut[1];
										result.operand = startLoc;
										result.lineN = lineN;
										result.obc.opc = "";
										result.obc.sbc = "";
										output.add(result);
										currentLoc = startLoc;
										start = 1;
									}else{
										error++;
										System.out.println("error:only allow 16 digits behind START at line "+lineN);
										break;
									}
								}
							}else if(cut.length==2){
								if(cut[1].equals("START")){
									error++;
									System.out.println("error:no starting address at line "+lineN);
									break;
								}else if(cut[0].equals("START")){
									error++;
									System.out.println("error:no progname at line "+lineN);
									break;
								}
							}else{
								if(cut[0].equals("START")){
									error++;
									System.out.println("error:no progname and starting address at line "+lineN);
									break;
								}
							}
						}else if(start == 1){
							outPut result = new outPut();
							result.loc = currentLoc;
							result.lineN = lineN;
							if(cut.length ==1){
								if(cut[0].equals("RSUB")){
									result.mnemonic = cut[0];
									result.obc.opc = "4C";
									result.obc.sbc = "0000";
									output.add(result);
									currentLoc = locAdd(currentLoc, 3);
								}else if(!getOpcode(opTable,cut[0]).equals("not found") || !notMne(notmne, cut[0]).equals("not found")){
									error++;
									System.out.println("error:no operand at line "+lineN);
								}else if(cut[0].equals("END")){
									exit=1;
									error++;
									System.out.println("error:no prog first line address at line "+lineN);
								}else{
									error++;
									System.out.println("error:format wrong at line "+lineN);
								}
							}else if(cut.length == 2) {
								if(cut[0].equals("RSUB")){
									result.obc.opc = "4C";
									result.obc.sbc = "0000";
									if(cut[1].trim().length()>0){
										error++;
										System.out.println("error:RSUB can not have operand at line "+lineN);
										continue;
									}
								}
								if(!getOpcode(opTable,cut[0]).equals("not found")){
									result.mnemonic = cut[0];
									result.obc.opc = getOpcode(opTable,cut[0]);
									if(cut[1].contains(",")){
										result.addressing = -1;
										String[] cutIndex = cut[1].split("\\,");
										cut[1] = cutIndex[0];
										if(!cutIndex[1].equals("X")){
											error++;
											System.out.println("error:index wrong at line "+lineN);
										}
										result.operand = cut[1];
										if(checkSymbol(symTable, cut[1]).label == null && checkSymbol(symTable,cut[1]).loc==null){
											symBol sb = new symBol();
											sb.label = cut[1];
											symTable.add(sb);
											
										}else {
											result.obc.sbc = locAdd(checkSymbol(symTable, cut[1]).loc,32768);
											 
										}
										output.add(result);
										currentLoc = locAdd(currentLoc, 3);
									}else{
										result.operand = cut[1];
										if(checkSymbol(symTable, cut[1]).label == null && checkSymbol(symTable,cut[1]).loc==null){
											symBol sb = new symBol();
											sb.label = cut[1];
											symTable.add(sb);
										}else{
											result.obc.sbc = checkSymbol(symTable, cut[1]).loc;
										}
										output.add(result);
										currentLoc = locAdd(currentLoc, 3);
									}
								}else if(cut[1].equals("RSUB")){
									result.mnemonic=cut[1];
									result.obc.opc = "4C";
									result.obc.sbc = "0000";
									result.label = cut[0];
									if(!getOpcode(opTable,cut[0]).equals("not found") || !notMne(notmne, cut[0]).equals("not found")){
										error++;
										System.out.println("error:symbol cannot same with mnemonic at line "+lineN);
										continue;
									}
									if(checkSymbol(symTable, cut[0]).label == null && checkSymbol(symTable,cut[0]).loc==null){
										symBol sb = new symBol();
										sb.label = cut[0];
										sb.loc = currentLoc;
										symTable.add(sb);
									}else if(checkSymbol(symTable, cut[0]).label != null && checkSymbol(symTable,cut[0]).loc==null){
										checkSymbol(symTable, cut[0]).loc = currentLoc;
										for(int i=0;i<output.size();i++){
												if(output.get(i).obc.sbc == null && output.get(i).operand.equals(cut[0])){
													if(output.get(i).addressing == -1){
														output.get(i).obc.sbc = locAdd(currentLoc, 32768);
													}else{
														output.get(i).obc.sbc = currentLoc;
													}
												}
											}
									}else{
										error++;
										System.out.println("error:cannot define symbol more than once at line "+lineN);
									}
									output.add(result);
									currentLoc = locAdd(currentLoc, 3);

								}else if(!notMne(notmne, cut[0]).equals("not found")){
									error++;
									System.out.println("error:cannot without label at line "+lineN);
								}else if(cut[0].equals("END")){
									if(checkSymbol(symTable, cut[1]).label == null ){
										error++;
										System.out.println("error:undefined symbol behind END at line "+lineN);
										exit=1;
									}else{
										result.mnemonic=cut[0];
										result.operand=cut[1];
										result.obc.opc="";
										result.obc.sbc="";
										output.add(result);
										endLoc = currentLoc;
										exit = 1;
									}
								}else if(!getOpcode(opTable,cut[1]).equals("not found")){
									error++;
									System.out.println("error:no operand at line "+lineN);
									// wrong 
								}else if(getOpcode(opTable,cut[0]).equals("not found") && notMne(notmne, cut[0]).equals("not found")){
									error++;
									System.out.println("error:mnemonic wrong at line "+lineN);

								}else {
									error++;
									System.out.println("error:format wrong at line "+lineN);
								}
							}else if(cut.length == 3){
								if(cut[1].equals("RSUB")){
									result.label = cut[0];
									result.obc.opc = "4C";
									result.obc.sbc = "0000";
									if(!getOpcode(opTable,cut[0]).equals("not found") || !notMne(notmne, cut[0]).equals("not found")){
										error++;
										System.out.println("error:symbol cannot same with mnemonic at line "+lineN);
										continue;
									}
									
									if(checkSymbol(symTable, cut[0]).label != null && checkSymbol(symTable,cut[0]).loc==null){
										checkSymbol(symTable,cut[0]).loc = currentLoc;
										for(int i=0;i<output.size();i++){
											if(output.get(i).obc.sbc == null && output.get(i).operand.equals(cut[0])){
												if(output.get(i).addressing == -1){
													output.get(i).obc.sbc = locAdd(currentLoc, 32768);
												}else{
													output.get(i).obc.sbc = currentLoc;
												}
											}
										}
									}else{
										error++;
										System.out.println("error: cannot define symbol more than once at line "+lineN);
									}
									
									if(cut[2].trim().length()>0){
										error++;
										System.out.println("error:RSUB can not have operand at line "+lineN);
										continue;
									}
								}
								if(!getOpcode(opTable,cut[0]).equals("not found")){
									result.mnemonic = cut[0];
									result.obc.opc = getOpcode(opTable,cut[0]);
									if(cut[1].contains(",")){
										result.addressing = -1;
										String[] cutIndex = cut[1].split("\\,");
										cut[1] = cutIndex[0];
										if(!cut[2].equals("X")){
											error++;
											System.out.println("error:index wrong at line "+lineN);
										}
										result.operand = cut[1];
										if(checkSymbol(symTable, cut[1]).label == null && checkSymbol(symTable,cut[1]).loc==null){
											symBol sb = new symBol();
											sb.label = cut[1];
											symTable.add(sb);
										}else{
											result.obc.sbc = locAdd(checkSymbol(symTable, cut[1]).loc,32768);
										}
										output.add(result);
										currentLoc = locAdd(currentLoc, 3);

									}else{
										error++;
										System.out.println("error:format wrong at line "+lineN);
									}
								}else if(!getOpcode(opTable,cut[1]).equals("not found")){
									if(!getOpcode(opTable,cut[0]).equals("not found") || !notMne(notmne, cut[0]).equals("not found")){
										error++;
										System.out.println("error:symbol cannot same with mnemonic at line "+lineN);
									}else{
										result.mnemonic = cut[1];
										result.obc.opc = getOpcode(opTable,cut[1]);
										result.label = cut[0];

										if(checkSymbol(symTable, cut[0]).label == null && checkSymbol(symTable,cut[0]).loc==null){
											symBol sb = new symBol();
											sb.label = cut[0];
											sb.loc = currentLoc;
											symTable.add(sb);
										}else if(checkSymbol(symTable, cut[0]).label != null && checkSymbol(symTable,cut[0]).loc==null){
											checkSymbol(symTable,cut[0]).loc = currentLoc;
											for(int i=0;i<output.size();i++){
												if(output.get(i).obc.sbc == null && output.get(i).operand.equals(cut[0])){
													if(output.get(i).addressing == -1){
														output.get(i).obc.sbc = locAdd(currentLoc, 32768);
													}else{
														output.get(i).obc.sbc = currentLoc;
													}
												}
											}
										}else{
											error++;
											System.out.println("error: cannot define symbol more than once at line "+lineN);
										}
										if(cut[2].contains(",")){
											String[] cutIndex = cut[2].split("\\,");
											cut[2] = cutIndex[0];
											result.addressing = -1;
										}
										if(checkSymbol(symTable, cut[2]).label == null && checkSymbol(symTable,cut[2]).loc==null){
											symBol sb = new symBol();
											sb.label = cut[2];
											symTable.add(sb);
										}else{
											if(result.addressing == -1){
												result.obc.sbc = locAdd(checkSymbol(symTable,cut[2]).loc, 32768);
											}else{
												result.obc.sbc = checkSymbol(symTable,cut[2]).loc;
											}
										}
										result.operand = cut[2];
										output.add(result);
										currentLoc = locAdd(currentLoc, 3);
									}
								}else if(!notMne(notmne, cut[1]).equals("not found")){
									if(!getOpcode(opTable,cut[0]).equals("not found") || !notMne(notmne, cut[0]).equals("not found")){
										error++;
										System.out.println("error:symbol cannot same with mnemonic at line "+lineN);
									}else{
										result.label = cut[0];
										result.mnemonic = cut[1];
										result.operand = cut[2];
										if(checkSymbol(symTable, cut[0]).label == null && checkSymbol(symTable,cut[0]).loc==null){
											symBol sb = new symBol();
											sb.label = cut[0];
											sb.loc = currentLoc;
											symTable.add(sb);
										}else if(checkSymbol(symTable, cut[0]).label != null && checkSymbol(symTable,cut[0]).loc==null){
											checkSymbol(symTable,cut[0]).loc = currentLoc;
											for(int i=0;i<output.size();i++){
												if(output.get(i).obc.sbc == null && output.get(i).operand.equals(cut[0])){
													if(output.get(i).addressing == -1){
														output.get(i).obc.sbc = locAdd(currentLoc, 32768);
													}else{
														output.get(i).obc.sbc = currentLoc;
													}
												}
											}
										}else{
											error++;
											System.out.println("error: cannot define symbol more than once at line "+lineN);
										
										}
										if(notMne(notmne,cut[1]).equals("BYTE")){
											String[] bs = cut[2].split("\\'");
											if(bs.length<2){
												error++;
												System.out.println("error:byte value cannot be empty at line "+lineN);
											}else{
												if(bs[0].equals("C")){
													result.obc.opc = "";
													result.obc.sbc = "";
													for(int i=0;i<bs[1].length();i++){
														result.obc.sbc = result.obc.sbc+Integer.toHexString((int) bs[1].charAt(i));
													}
													currentLoc = locAdd(currentLoc, bs[1].length());
													
												}else if(bs[0].equals("X")){
													if((bs[1].trim().length())%2 != 0){
														error++;
														System.out.println("error:byte X value length cannot be odd number at line "+lineN);
													}else{
														if(bs[1].matches("[0-9A-F]+")){
															result.obc.opc = "";
															result.obc.sbc = bs[1];
															currentLoc = locAdd(currentLoc, 1);		
														}else{
															error++;
															System.out.println("error:BYTE X value allow 16 digits at line "+lineN);
														}
														
													}
												}else {
													error++;
													System.out.println("error:value type of BYTE only allow C and X at line "+lineN);
												}
											}

										}else if(notMne(notmne,cut[1]).equals("RESB")){
											result.obc.opc = "";
											result.obc.sbc = "";
											if(!getOpcode(opTable,cut[0]).equals("not found") || !notMne(notmne, cut[0]).equals("not found")){
												error++;
												System.out.println("error:symbol cannot same with mnemonic at line "+lineN);
												continue;
											}
											if(!cut[2].matches("[0-9]+")){
												error++;
												System.out.println("error:RESB value only allow decimal at line "+lineN);
											}else{
												int loc10 = Integer.parseInt(cut[2]);
												currentLoc = locAdd(currentLoc, loc10);
											}
										}else if(notMne(notmne,cut[1]).equals("WORD")){
											if(!getOpcode(opTable,cut[0]).equals("not found") || !notMne(notmne, cut[0]).equals("not found")){
												error++;
												System.out.println("error:symbol cannot same with mnemonic at line "+lineN);
												continue;
											}
											if(!cut[2].matches("[0-9]+")){
												error++;
												System.out.println("error:WORD value only allow decimal at line "+lineN);
											}else{
												result.obc.opc = "00";
												int loc10 = Integer.valueOf(cut[2],10);
												String loc16 = Integer.toHexString(loc10);
												result.obc.sbc = (("0000"+loc16).substring(loc16.length())).toUpperCase();
												currentLoc = locAdd(currentLoc, 3);
											}

										}else if(notMne(notmne,cut[1]).equals("RESW")){
											if(!getOpcode(opTable,cut[0]).equals("not found") || !notMne(notmne, cut[0]).equals("not found")){
												error++;
												System.out.println("error:symbol cannot same with mnemonic at line "+lineN);
												continue;
											}
											if(!cut[2].matches("[0-9]+")){
												error++;
												System.out.println("error:RESW value only allow decimal at line "+lineN);
											}else{
												result.obc.opc = "";
												result.obc.sbc = "";
												int loc10 = Integer.parseInt(cut[2]);
												currentLoc = locAdd(currentLoc, loc10*3);
											}
										}

										output.add(result);
									}
								}else if(getOpcode(opTable,cut[1]).equals("not found") && notMne(notmne, cut[1]).equals("not found") && !cut[1].equals("END")){
									error++;
									System.out.println("error:mnemonic wrong at line "+lineN);

								}else if(cut[1].equals("END")){
									if(!getOpcode(opTable,cut[0]).equals("not found") || !notMne(notmne, cut[0]).equals("not found")){
										error++;
										System.out.println("error:symbol cannot same with mnemonic at line "+lineN);
										exit=1;
									}else{
										result.label = cut[0];
										result.mnemonic=cut[1];
										result.operand=cut[2];
										result.obc.opc="";
										result.obc.sbc="";
										endLoc = currentLoc;
										exit = 1;
										if(checkSymbol(symTable, cut[0]).label == null && checkSymbol(symTable,cut[0]).loc==null){
											symBol sb = new symBol();
											sb.label = cut[0];
											sb.loc = currentLoc;
											symTable.add(sb);
										}else if(checkSymbol(symTable, cut[0]).label != null && checkSymbol(symTable,cut[0]).loc==null){
											checkSymbol(symTable,cut[0]).loc = currentLoc;
											for(int i=0;i<output.size();i++){
												if(output.get(i).obc.sbc == null && output.get(i).operand.equals(cut[0])){
													if(output.get(i).addressing == -1){
														output.get(i).obc.sbc = locAdd(currentLoc, 32768);
													}else{
														output.get(i).obc.sbc = currentLoc;
													}
												}
											}
										}else{
											error++;
											System.out.println("error: cannot define symbol more than once at line "+lineN);
										
										}
										output.add(result);
										exit = 1;
									}
								}else {
									error++;
									System.out.println("error:format wrong at line "+lineN);
								}
							}
							
						}
					}else {//token more than 3
						error++;
						System.out.println("error:token more than 3 at line "+lineN);
					}
				}
				
			}

		}
	
		for(int i=0;i<symTable.size();i++){
			if(symTable.get(i).loc == null){
				for(int j=0;j<output.size();j++){
					if(symTable.get(i).label.equals(output.get(j).operand)){
						error++;
						System.out.println("error:undefined symbol '"+output.get(j).operand+"' at line"+output.get(j).lineN);
					}
				}
			}
		}
	
		if(exit == 0){
			System.out.println("error:no END in prog");
		}
		if(error == 0 && exit == 1){
			
			//print ouput
			int end10 = Integer.valueOf(endLoc, 16);
			int start10 = Integer.valueOf(startLoc, 16);
			int length10 = end10 - start10;
			String progLength = (Integer.toHexString(length10)).toUpperCase();
			
			for(int i = 0; i<output.size(); i++){
				System.out.print(output.get(i).loc+"	");
				if(output.get(i).label == null)
					System.out.print("	");
				else
					System.out.print(output.get(i).label+"	");
				
				System.out.print(output.get(i).mnemonic+"	");
				if(output.get(i).operand == null)
					System.out.print("	");
				else
					System.out.print(output.get(i).operand+"	");
				System.out.print(output.get(i).addressing+"	");

				if(output.get(i).obc.opc.trim().length()>0 && output.get(i).obc.sbc.trim().length()>0){
					System.out.print(output.get(i).obc.opc);
					System.out.print(output.get(i).obc.sbc+"		");
				}else if(output.get(i).obc.sbc.trim().length()>0){
					System.out.print(output.get(i).obc.sbc+"		");
				}else{
					System.out.print("		");

				}
				
				System.out.println(output.get(i).lineN+"	");
			}

			//print symbol table
			for(int i = 0; i<symTable.size(); i++){
				System.out.print(symTable.get(i).label+"	");
				System.out.println(symTable.get(i).loc);
			}

			//print object code
			System.out.println("H^"+progname+"	^"+("000000"+startLoc).substring(startLoc.length())+"^"+("000000"+progLength).substring(progLength.length()));
			writer.println("H^"+progname+"	^"+("000000"+startLoc).substring(startLoc.length())+"^"+("000000"+progLength).substring(progLength.length()));
			int count=0, loccount=0;
			for(int i=1;i<output.size()-1;i++){
				if(i ==output.size()-2){
					int currentlength = Integer.valueOf(output.get(i+1).loc, 16) - Integer.valueOf(output.get(i).loc, 16);
					if(output.get(i).obc.sbc.trim().length()!=0){
						loccount = loccount + currentlength;
						count++;
					
						if(output.get(i+1).obc.sbc.trim().length()==0){
							
							System.out.print("T^00"+output.get(i-count+1).loc);
							writer.print("T^00"+output.get(i-count+1).loc);
							String loccount16 = Integer.toHexString(loccount).toUpperCase();
							System.out.print("^"+(("00"+loccount16).substring(loccount16.length())));
							writer.print("^"+(("00"+loccount16).substring(loccount16.length())));
							for(int j=i-count+1;j<i+1;j++){
								System.out.print("^"+output.get(j).obc.opc+output.get(j).obc.sbc);
								writer.print("^"+output.get(j).obc.opc+output.get(j).obc.sbc);
							}
							System.out.println();
							writer.println();
							loccount = 0;
							count = 0;
						}
					}
				}else{
					int currentlength = Integer.valueOf(output.get(i+1).loc, 16) - Integer.valueOf(output.get(i).loc, 16);
					int nextlength = Integer.valueOf(output.get(i+2).loc, 16) - Integer.valueOf(output.get(i+1).loc, 16);
					if(output.get(i).obc.sbc.trim().length()!=0){
						loccount = loccount + currentlength;
						count++;
					
						if(loccount+nextlength>30 || (output.get(i+1).obc.sbc.trim().length()==0)){
							
							System.out.print("T^00"+output.get(i-count+1).loc);
							writer.print("T^00"+output.get(i-count+1).loc);
							String loccount16 = Integer.toHexString(loccount).toUpperCase();
							System.out.print("^"+(("00"+loccount16).substring(loccount16.length())));
							writer.print("^"+(("00"+loccount16).substring(loccount16.length())));
							for(int j=i-count+1;j<i+1;j++){
								System.out.print("^"+output.get(j).obc.opc+output.get(j).obc.sbc);
								writer.print("^"+output.get(j).obc.opc+output.get(j).obc.sbc);
							}
							System.out.println();
							writer.println();
							loccount = 0;
							count = 0;
						}
					}
				}
			}
			if(checkSymbol(symTable, output.get(output.size()-1).operand).label != null &&
			 checkSymbol(symTable, output.get(output.size()-1).operand).loc != null){
				System.out.println("E^00"+checkSymbol(symTable, output.get(output.size()-1).operand).loc);
				writer.println("E^00"+checkSymbol(symTable, output.get(output.size()-1).operand).loc);

			}
			//System.out.println("E^00"+output.get(0).loc);
			
		}
		writer.close();
		double endtime = System.currentTimeMillis();
		System.out.println(endtime - starttime);
		//System.out.println("size: -------"+token.size());
		
	}

	public static class outPut{
		String loc;
		String label;
		String mnemonic;
		String operand;
		int lineN;
		int addressing;
		obCode obc = new obCode();
		public outPut(){
			loc=null;
			label=null;
			mnemonic=null;
			operand=null;
			lineN=0;
			addressing=0;
		}
	}
	public static class obCode{
		String opc;
		String sbc;
		public obCode(){
			opc = null;
			sbc = null;
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
	public static class Mne{
		String name;
		String opcode;
		public Mne(){
			name = null;
			opcode = null;
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
	public static String getOpcode(ArrayList<Mne> opTable, String search) {
		String ans = "not found";
		for(int i=0;i<opTable.size();i++){
			if(search.equals(opTable.get(i).name)){
				ans = opTable.get(i).opcode;
			}
		}
		return ans;
	}
	public static symBol checkSymbol(ArrayList<symBol> symTable, String operand) {
		//int ans = 0;//0 not found, 1 found, 2 have loc
		symBol sb = new symBol();
		for(int i=0;i<symTable.size();i++){
			if(operand.equals(symTable.get(i).label)){
				sb = symTable.get(i);
			}
		}
		return sb;
	}
	public static String locAdd(String currentLoc, int n){
		int loc10 = Integer.valueOf(currentLoc,16);
		loc10 = loc10+n;
		String nextLoc = (Integer.toHexString(loc10)).toUpperCase();
		return nextLoc;
	}
}