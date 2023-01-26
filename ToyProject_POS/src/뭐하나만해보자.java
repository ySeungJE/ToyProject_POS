import java.io.IOException;
import java.util.Scanner;

public class 뭐하나만해보자 {
	public static void main(String[] args) throws IOException {
		
		String str = "981006";
		
		String str2;
		Scanner sc = new Scanner(System.in);
		System.out.print("문자열 입력 : ");
		str2 = sc.nextLine();
		
		System.out.println(str.equals(str2));
		
		
	}
}
