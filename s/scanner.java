import java.io.*;
import java.util.*;

public class scanner{
	public static void main(String[] args) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader("testprog.S"));
		BufferedReader reader = new BufferedReader(new FileReader("opCode.txt"));
		String s;
		String line;
		ArrayList<String> token = new ArrayList<String>();
		ArrayList<Integer> type = new ArrayList<Integer>();
		//-1 empty 0 address, 1 label, 2 direct, 3 index,4 other, 5 operand, 6 index
		ArrayList<String> mnemonic = new ArrayList<String>();
		ArrayList<String> opcode = new ArrayList<String>();
		String[] notmne = {"RESB", "RESW", "BYTE", "WORD"};
		int i = 0;  
		while((s = br.readLine()) != null){
			if(s.contains("START")){//find program start point
				if(s.contains(".")){
					String cut[] = s.split("\\.");
					s = cut[0];
				}
				if(!s.equals("")){
					//System.out.println("line"+(i+1));
					i++;
					//System.out.println(s);
				}
				if(s.trim().length() > 0){
					String cut[] = s.split("\\s+");
					for(int j = 0; j < cut.length; j++)
						//if(!cut[j].equals(""))
							token.add(cut[j]);
				}
				break;
			}
		}
		while((s= br.readLine()) != null){
		
			if(s.contains(".")){
				String cut[] = s.split("\\.");
				s = cut[0];
			}

			if(s.trim().length() > 0){
				String cut[] = s.split("\\s+");
				for(int j = 0; j < cut.length; j++)
					//if(!cut[j].equals(""))
						token.add(cut[j]);
			}
		}
		while((line = reader.readLine()) != null) {
			String[] splited = line.split(" ");
			mnemonic.add(splited[0]);
			opcode.add(splited[1]);
		}
		reader.close();

		// show all mnemonic and opcode
		// for(int j = 0; j < mnemonic.size(); j++){
		// 	System.out.print(mnemonic.get(j)+" ");
		// 	System.out.println(opcode.get(j)+" ");
		// }

		// show all token
		// System.out.println("token: ");
		// for(int j = 0; j < token.size(); j++){
		// 	System.out.print(j +" ");
		// 	System.out.println(token.get(j));

		// }
		int start = 0;
		for(int j = 0; j < token.size(); j++){
			if(!(token.get(j)).equals("")){
				if(start == 0){
					if((token.get(j)).equals("START")){
						type.add(1);
						type.add(4);
						type.add(5);
						start = 1;
					}
				}else{
					for(int k = 0; k < mnemonic.size(); k++){
						if((token.get(j)).equals(mnemonic.get(k))){
							//System.out.println(token.get(j));
							if((token.get(j+1)).contains(",")){
								if(!(token.get(j - 1)).equals("")){
									type.add(1);
									//System.out.println("empty");
								}
								type.add(3);
								type.add(5);
								type.add(6);
							}else{
								if(!(token.get(j - 1)).equals("")){
									type.add(1);
									//System.out.println("empty");
								}
								if(!((token.get(j)).equals("RSUB"))){
									type.add(2);
									type.add(5);
								}else
									type.add(4);
							}
						}
					}
					
					for(int k=0; k<notmne.length; k++){
						if((token.get(j)).equals(notmne[k])){
							type.add(1);
							type.add(4);
							type.add(5);
						}
					}
					if((token.get(j)).equals("END")){
						type.add(4);
						type.add(5);			
					}
				}
			}else{
				type.add(-1);
			}
		}	
		System.out.println(token.size());
		System.out.println(type.size());
		
		//show all type 
		for(int j = 0; j<type.size(); j++){
			System.out.println(type.get(j));
		}

		//print like prog
		for(int j=1; j<token.size(); j++){
			if(type.get(j) == 2){
				if(type.get(j-1) != -1){
					System.out.println(token.get(j-1)+"	"+token.get(j)+"	"+token.get(j+1));
					System.out.println("-----------------------");
					System.out.println("using direct addressing");
					System.out.println("label: "+token.get(j-1)+", mnemonic: "+token.get(j)+", operand: "+token.get(j+1));
					System.out.println();
				}else{
					System.out.println("	"+token.get(j)+"	"+token.get(j+1));
					System.out.println("-----------------------");
					System.out.println("using direct addressing");
					System.out.println("mnemonic: "+token.get(j)+", operand: "+token.get(j+1));	
					System.out.println();	
				}
			}else if(type.get(j) == 3){
				if(type.get(j-1) != -1){
					System.out.println(token.get(j-1)+"	"+token.get(j)+"	"+token.get(j+1)+token.get(j+2));
					System.out.println("-----------------------");
					System.out.println("using index addressing");
					System.out.println("label: "+token.get(j-1)+", mnemonic: "+token.get(j)+", operand: "+token.get(j+1)+token.get(j+2));
					System.out.println();
				}else{
					System.out.println("	"+token.get(j)+"	"+token.get(j+1)+token.get(j+2));
					System.out.println("-----------------------");
					System.out.println("using index addressing");
					System.out.println("mnemonic: "+token.get(j)+", operand: "+token.get(j+1)+token.get(j+2));	
					System.out.println();
				}
			}else if(type.get(j) == 4){
				if((token.get(j)).equals("START")){
					System.out.println(token.get(j-1)+"	"+token.get(j)+"	"+token.get(j+1));
					System.out.println("-----------------------");
					System.out.println("prog name: "+token.get(j-1)+", start address: "+token.get(j+1));
					System.out.println();
				}else if((token.get(j)).equals("END")){
					System.out.println(token.get(j)+"	"+token.get(j+1));
					System.out.println("-----------------------");
					System.out.println("end of prog, start from: "+token.get(j+1));
					System.out.println();
				}else{
					for(int k=0; k<notmne.length; k++){
						if((token.get(j)).equals(notmne[k])){
							if(type.get(j-1) != -1){
								System.out.println(token.get(j-1)+"	"+token.get(j)+"	"+token.get(j+1)+token.get(j+2));
								System.out.println("-----------------------");
								System.out.println("label: "+token.get(j-1)+", mnemonic: "+token.get(j)+", operand: "+token.get(j+1)+token.get(j+2));
								System.out.println();
							}else{
								System.out.println("	"+token.get(j)+"	"+token.get(j+1)+token.get(j+2));
								System.out.println("-----------------------");
								System.out.println("mnemonic: "+token.get(j)+", operand: "+token.get(j+1)+token.get(j+2));	
								System.out.println();
							}
							break;
						}
					}
				}
			}
		}
	}
}