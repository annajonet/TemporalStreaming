package temporalStreaming;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import temporalStreaming.Controller.Controller;

/* This class is used to run the algorithm with some examples
 * the inputs string is set with the motif template that has to be counted
 * current implementation only counts one type of motif at a time
 */

public class TestInput {

	static HashMap<Integer, String> edges = new HashMap<Integer, String>();
	static TreeSet<Integer> timestamps = new TreeSet<Integer>();
	
	public static void checkForInput() throws IOException{
		readEdgeForAnalysis();
			/*readEdges();
			//these are the motifs given in the paper "Motifs in Temporal networks" - in the same order
			//Mismatch is for: 01,20,01 (manual counting gives my result)
			//String[][] inputs = {{"01,01,21"}};
			String[][] inputs = {{"01,21,01","01,21,10","01,21,02","01,21,20","01,21,12","01,21,21"},
					             {"01,12,01","01,12,10","01,12,02","01,12,20","01,12,12","01,12,21"},
					             {"01,20,01","01,20,10","01,20,02","01,20,20","01,20,12","01,20,21"},
					             {"01,02,01","01,02,10","01,02,02","01,02,20","01,02,12","01,02,21"},
					             {"01,10,01","01,10,10","01,10,02","01,10,20","01,10,12","01,10,21"},
					             {"01,01,01","01,01,10","01,01,02","01,01,20","01,01,12","01,01,21"}};//}};//
			//Controller objContr = new Controller("01,12,20",3);
			for(int i=0;i<inputs.length;i++){
				for(int j=0;j<inputs[0].length;j++){
					Controller objContr = new Controller(inputs[i][j],3, 90);
					Iterator sortedtime = timestamps.iterator();
					while(sortedtime.hasNext()){
						int key = (int) sortedtime.next();
						String edge = edges.get(key);
						//adding the current edge
						objContr.addNewTemporalEdge(edge);
					}
					objContr.printOutput();
				}
				System.out.println("\n");
			}
		*/
		
	}
	
	public static void readEdgeForAnalysis() throws IOException{
		//File file = new File("email-Eu-core-temporal-Dept3.txt"); //example-temporal-graph
		File file = new File("email-Eu-core-temporal.txt");
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		String templ = "01,01,01"; //"01,02,01" ,   
		Controller objContr = new Controller(templ,3,3600);
		while ((line = bufferedReader.readLine()) != null) {			
			objContr.addNewTemporalEdge(line);
		}
		objContr.saveAnalysis(templ);
		objContr.printOutput();
	}
	
	/*current example is a list of edges - not in sorted order
	 * so the readEdges() first sort the edges 
	 * in actual graph this can be skipped as there will only be one edge at a time
	 * that too in the correct order
	 */
	public static void readEdges() throws IOException{
		File file = new File("example-temporal-graph.txt"); //example-temporal-graph
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			String[] e = line.split(" ");
			int ti = Integer.parseInt(e[2]);			
			edges.put(ti, e[0]+","+e[1]+","+e[2]);
			timestamps.add(ti);
						
		}
		fileReader.close();
	}
}
