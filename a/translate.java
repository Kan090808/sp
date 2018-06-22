import java.io.*;
import java.util.*;

public class translate{
	public static void main(String[] args){
		Scanner scanner = new Scanner (System.in);
		int exit = 0;
		while(exit == 0){
			String loc16 = scanner.next();
			String loc10 = Integer.valueOf(loc16,16).toString();
			System.out.println(loc10);
		}
	}
}