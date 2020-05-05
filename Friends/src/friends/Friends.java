package friends;

import java.util.ArrayList;

import structures.Queue;
import structures.Stack;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) 
	{   
    if (g.map.get(p1)==null || g.map.get(p2)==null) 
    {
        return null;
    }	
    ArrayList<String> shortest = new ArrayList<>();
    Queue<Integer> hold = new Queue<>();
    int[] dist = new int[g.members.length];
    int[] pred = new int[g.members.length];
    boolean[] visited = new boolean[g.members.length]; 
    for (int i = 0; i < visited.length; i++) 
    {
        visited[i] = false;
        dist[i] = 0;
        pred[i] = -1;
    }
    int startIndex = g.map.get(p1);
    visited[startIndex] = true;
    dist[startIndex] = 0;

    hold.enqueue(startIndex);

    while (!hold.isEmpty()) 
    {
        int v = hold.dequeue(); 
        Friend ptr = g.members[v].first;
        while (ptr != null) 
        {
            int fnum = ptr.fnum;
            if (!visited[fnum])
            {
                dist[fnum] = dist[v]+1;
                pred[fnum] = v;
                visited[fnum] = true;
                hold.enqueue(fnum);
            }
            ptr=ptr.next;
        }
    }
    Stack<String> path = new Stack<>();
    int spot = g.map.get(p2);
    if (!visited[spot]) 
    {
        return null;
    }
    while(spot != -1) 
    {
        path.push(g.members[spot].name);
        spot = pred[spot];
    }
    while (!path.isEmpty()) 
    {
        shortest.add(path.pop());
    }

    return shortest;
}
		
	
	
	
	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null if there is no student in the
	 *         given school
	 */
	
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) 
	{		
		ArrayList<String> all=new ArrayList<String>();
		ArrayList<ArrayList<String>> tdPath=new ArrayList<ArrayList<String>>();		
		for (int i=0;i<g.members.length;i++)
		{
				if (g.members[i].student==true && g.members[i].school.equals(school)&& (!all.contains(g.members[i].name))) 
				{ 
					ArrayList<String> path=new ArrayList<String>();
					path.add(g.members[i].name);
					ArrayList<String> temp=friendsT(g,path,school);
					tdPath.add(temp);
					all.addAll(temp);
				}	
		}
		if (tdPath.isEmpty())
		{
			return null;
		}
		return tdPath;
	}
	private static ArrayList<String> friendsT(Graph g,ArrayList<String> path, String school)
	{
		for (int i=0;i<path.size();i++)
		{
			int index=g.map.get(path.get(i));
			Friend ptr=g.members[index].first;
			while (ptr!=null)
			{
				if (g.members[ptr.fnum].student==true &&  g.members[ptr.fnum].school.equals(school)&& !path.contains(g.members[ptr.fnum].name)) 
				{ 
					path.add(g.members[ptr.fnum].name);
				}
				ptr=ptr.next;
			}	
		}
		return path;	
	}
	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
        int[] back = new int[g.members.length];
        int[] dfsnum = new int[g.members.length];
        boolean[] visited = new boolean[g.members.length];
        ArrayList<String> conList = new ArrayList<>();
        for (int i=0;i<g.members.length;i++) 
        {
            if (!visited[g.map.get(g.members[i].name)])
            {
                dfsnum = new int[g.members.length];
                dfs(g.map.get(g.members[i].name), g.map.get(g.members[i].name), g, visited, dfsnum, back, conList);
            }
        }
        for (int i = 0; i < conList.size(); i++)
        {
            Friend ptr = g.members[g.map.get(conList.get(i))].first;
            int count = 0;
            while (ptr != null) 
            {
                ptr = ptr.next;
                count++;
            }
            if (count == 0 || count == 1) {
                conList.remove(i);
            }
        }

        for (int i=0;i<g.members.length;i++) 
        {
            if ((g.members[i].first.next == null && !conList.contains(g.members[g.members[i].first.fnum].name))) {
                conList.add(g.members[g.members[i].first.fnum].name);
            }
        }

        return conList;
    }

    private static int resize(int[] arr) {
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != 0) {
                count++;
            }
        }
        return count;
    }

    private static void dfs(int v, int start, Graph g, boolean[] visited, int[] dfsnum, int[] back, ArrayList<String> answer){
        Person p = g.members[v];
        visited[g.map.get(g.members[v].name)] = true;
        int count = resize(dfsnum)+1;
        if (dfsnum[v] == 0 && back[v] == 0) 
        {
            dfsnum[v] = count;
            back[v] = dfsnum[v];
        }
        for (Friend ptr = p.first; ptr != null; ptr = ptr.next) 
        {
            if (!visited[ptr.fnum]) 
            {
                dfs(ptr.fnum, start, g, visited, dfsnum, back, answer);
                if (dfsnum[v] > back[ptr.fnum]) 
                {
                    back[v] = Math.min(back[v], back[ptr.fnum]);
                } 
                else 
                {
                    if (Math.abs(dfsnum[v]-back[ptr.fnum]) < 1 && Math.abs(dfsnum[v]-dfsnum[ptr.fnum]) <=1 && back[ptr.fnum] ==1 && v == start) 
                    {
                        continue;
                    }

                    if (dfsnum[v] <= back[ptr.fnum] && (v != start || back[ptr.fnum] == 1 )) 
                    { 
                        if (!answer.contains(g.members[v].name)) 
                        {
                            answer.add(g.members[v].name);
                        }
                    }

                }
            } 
            else 
            {
                back[v] = Math.min(back[v], dfsnum[ptr.fnum]);
            }
        }
        return;
    }
}

