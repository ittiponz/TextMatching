import java.util.Scanner;


public class Main {
	
	private static Scanner scanner;

	public static void main(String[] args) {
		String str = "";

		
		scanner = new Scanner(System.in);
		
		do {
			System.out.print("Product Search - Input your keyword (s):");
			str = scanner.nextLine();
			BmMatching bm = new BmMatching(str);
			bm.printResult();
			
			System.out.println();
			System.out.print("Do you want to continue (Y or anykey to exit):");
			str = scanner.nextLine();
		} while (str.toLowerCase().equals("y"));
		
		
	}

}
