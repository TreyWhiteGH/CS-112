package friends;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
public class FriendsApp {
	static Scanner stdin = new Scanner(System.in);
	
	public static void main(String[] args) 
	throws IOException {
		System.out.print("Enter friends file name => ");
		String wordsFile = stdin.nextLine();
		Scanner sc = new Scanner(new File(wordsFile));
		Graph friends=new Graph(sc);
		String option=""; 
		System.out.println(friends.map.get("Not there"));
		while (!option.equals("q"))
		{
		System.out.print("Enter 's'hortestChain, 'c'liques, or c'o'nnectors or 'q'uit =>");
		option = stdin.nextLine();
		switch (option)
		{
		case "s":
			shortestChain(friends);
			break;
		case "c":
			cliques(friends);
			break;
		case "o":
			connectors(friends);
			break;
		case "q":
			break;
		default:
		throw new IOException("Incorrect input");
			
		}
		
		}
		
	}
	private static void shortestChain(Graph friends)
	{
		System.out.print("Enter first friends name => ");
		String p1 = stdin.nextLine();
		System.out.print("Enter second friends name => ");
		String p2 = stdin.nextLine();
		ArrayList<String> res=Friends.shortestChain(friends, p1, p2);
		System.out.print("Chain is: ");
		for (int i=0;i<res.size();i++)
		{
			System.out.println(" - " +res.get(i));
		}
		
	}
	private static void cliques(Graph friends)
	{
		System.out.print("Enter school's name => ");
		String school = stdin.nextLine();
		ArrayList<ArrayList<String>> ret= Friends.cliques(friends, school);
		if (ret==null)
		{
			System.out.println("DNE");
			
		}
		else
		{
		for (int i=0;i<ret.size();i++)
		{
			System.out.print("Clique : ");
			for (int j=0;j<ret.get(i).size();j++)
			{
				System.out.print(ret.get(i).get(j) +",");
			}
			System.out.println();
		}
		}
	}
	private static void connectors(Graph friends)
	{
		ArrayList<String> ret= Friends.connectors(friends);
		if (ret==null)
		{
			System.out.println("DNE");
			
		}
		else
		{System.out.print("Connectors : ");
		for (int i=0;i<ret.size();i++)
		{
			
			System.out.print(ret.get(i)+ ", ");
		}
		}
		System.out.println();
	}
}
	
