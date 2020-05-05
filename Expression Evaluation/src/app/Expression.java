package app;

import java.io.*;

import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	String[] splitExpr = expr.split("[^a-zA-Z\\[]+");
        for (int i=0;i<splitExpr.length;i++)
        {
     	   System.out.println(splitExpr[i]);
        }
     	for (int x = 0; x < splitExpr.length; x++)
          {
             if (splitExpr[x].length()>0)
             {
                 if (splitExpr[x].contains("["))
                 {
                 	int y=0;
                     while(splitExpr[x].contains("[") && splitExpr[x].length()>y)
                     	{
                     			if (splitExpr[x].charAt(y) == '[')
                     			{
                     				String temp=splitExpr[x].substring(0,y);
                     				Array newArr=new Array(temp);
                     				if (arrays.indexOf(newArr) == -1 || arrays.isEmpty())
                     				{
                     					arrays.add(newArr);
                     				}
                     				splitExpr[x]=splitExpr[x].substring(y+1);
                     				y=0;
                     			}
                     			y++;
                     	}
                 }
                 else 
                 {
                    Variable newVar=new Variable(splitExpr[x]);
                    if (vars.isEmpty() || vars.indexOf(newVar) == -1)
                    {
                        vars.add(newVar);
                    }
                 }
             }
     	/** DO NOT create new vars and arrays - they are already created before being sent in
     	 ** to this method - you just need to fill them in.
     	 **/
          }
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    {
        Stack<String> arrStack = new Stack<>();
        Stack<Float> flStack = new Stack<>();
        Stack<Character> chStack = new Stack<>();
        StringBuffer buffer = new StringBuffer("");
        float quantity = 0;
        int i = 0;
        while (i < expr.length())
        {
            switch (expr.charAt(i))
            {
            case '(':
                chStack.push(expr.charAt(i));
                break;
            case ')':
                while (!chStack.isEmpty() && !flStack.isEmpty() && (chStack.peek() != '('))
                {
                    doMath(chStack, flStack);
                }
                if (chStack.peek() == '(')
                {
                    chStack.pop();
                }
                break;
            case '[':
                arrStack.push(buffer.toString());
                buffer.setLength(0);
                chStack.push(expr.charAt(i));
                break;
            case ']':
                while (!chStack.isEmpty() && !flStack.isEmpty()
                        && (chStack.peek() != '['))
                {
                    doMath(chStack, flStack);
                }
                if (chStack.peek() == '[')
                {
                    chStack.pop();
                }
                int idx = flStack.pop().intValue();
                Iterator<Array> itr = arrays.iterator();
                while (itr.hasNext())
                {
                    Array arr = itr.next();
                    if (arr.name.equals(arrStack.peek()))
                    {
                        flStack.push((float)arr.values[idx]);
                        arrStack.pop();
                        break;
                    }
                }

                break;
            case ' ':
                break;
            case '+':
            case '-':
            case '*':
            case '/':
                while (!chStack.isEmpty() && (chStack.peek() != '(')
                        && (chStack.peek() != '[')
                        && OrderOfOp(expr.charAt(i), chStack.peek()))
                {
                    doMath(chStack, flStack);
                }
                chStack.push(expr.charAt(i));
                break;
            default:
                if ((expr.charAt(i) >= 'a' && expr.charAt(i) <= 'z')
                        || (expr.charAt(i) >= 'A' && expr.charAt(i) <= 'Z'))
                {
                    buffer.append(expr.charAt(i));
                    if (i + 1 < expr.length())
                    {
                        if (expr.charAt(i + 1) == '+' || expr.charAt(i + 1) == '-' || expr.charAt(i + 1) == '*' || expr.charAt(i + 1) == '/' || expr.charAt(i + 1) == ')' || expr.charAt(i + 1) == ']' || expr.charAt(i + 1) == ' ')
                        {
                            Variable var = new Variable(buffer.toString());
                            int idxVar = vars.indexOf(var);
                            quantity = vars.get(idxVar).value;
                            flStack.push(quantity);
                            buffer.setLength(0);

                        }

                    }
                    else
                    {
                        Variable var = new Variable(buffer.toString());
                        int varIndex = vars.indexOf(var);
                        quantity = vars.get(varIndex).value;
                        flStack.push(quantity);
                        buffer.setLength(0);
                    }

                }
                else if (expr.charAt(i) >= '0' && expr.charAt(i) <= '9')
                {
                    buffer.append(expr.charAt(i));
                    if (i + 1 < expr.length())
                    {
                        if (expr.charAt(i + 1) == '+' || expr.charAt(i + 1) == '-'|| expr.charAt(i + 1) == '*'|| expr.charAt(i + 1) == '/'|| expr.charAt(i + 1) == ')'|| expr.charAt(i + 1) == ']'|| expr.charAt(i + 1) == ' ')
                        {
                            quantity = Integer.parseInt(buffer.toString());
                            flStack.push(quantity);
                            buffer.setLength(0);
                        }
                    }
                    else
                    {
                        quantity = Float.parseFloat(buffer.toString());
                        flStack.push(quantity);
                        buffer.setLength(0);
                    }
                }
                break;
            }
            i++;
        }
        Float result = Float.valueOf(0);
        if (i == expr.length())
        {
            while (chStack.size() > 0 && flStack.size() > 1)
            {
                doMath(chStack, flStack);
            }
            if (flStack.size() > 0)
            {
                result = flStack.pop();
            }
        }
        return result.floatValue();
    }
    
    private static boolean OrderOfOp(char c1, char c2)
    {
        if ((c1 == '*' || c1 == '/')&& (c2 == '+' || c2 == '-'))
        {
            return false;
        }
        return true;
    }private static void doMath(Stack<Character> chStack, Stack<Float> flStack)
    {
        Float res = Float.valueOf(0);
        if (chStack.size() > 0 && flStack.size() > 1)
        {
            Float quantity1 = flStack.pop().floatValue();
            Float quantity2 = flStack.pop().floatValue();
            switch (chStack.pop())
            {
            case '+':
                res=quantity2+quantity1;
                break;
            case '-':
                res=quantity2-quantity1;
                break;
            case '*':
                res=quantity2*quantity1;
                break;
            case '/':
                res=quantity2/quantity1;
                break;
            }
            flStack.push(res);

        }
        else if (flStack.size() > 0)
        {
            res = flStack.pop();
            flStack.push(flStack.pop().floatValue());
        }

    }
 }
    	       