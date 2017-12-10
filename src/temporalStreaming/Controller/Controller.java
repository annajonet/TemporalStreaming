package temporalStreaming.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import temporalStreaming.Model.Model;

/*
 * This class is the main logic Controller
 * calls the Model class methods where the actual data is updated
 */
public class Controller {
	
	Model temporalModel;
	int motifCount = 0;
	int numOfNodes = 0;	//in motif
	int delta = 0;
	
	//edges - maintains a list of edges in current delta
	ArrayList<String> edges = new ArrayList<String>(); 
	int tstart = 0;
	
	public int getMotifCount(){		//returns the count of motifs as of now
		return this.motifCount;
	}
	public Controller(String motif, int numOfNodes, int delta){
		//creates a model
		temporalModel = new Model(motif, delta, numOfNodes);
		this.delta = delta;
		this.numOfNodes = numOfNodes; 		
	}
	
	public void saveAnalysis(String templ) throws IOException{
		temporalModel.writeAnalysis(templ);
	}
	
	public void addNewTemporalEdge(String edge){		//edge - (source,dest,timestamp)
    	//keep track of the max time taken to process an edge
		long start = System.currentTimeMillis();;  
		String[] e = edge.split(" ");
    	int l = temporalModel.getL();  	
		if(edges.size() == 0){ // first edge
			tstart = Integer.parseInt(e[2]);
		}
		edges.add(edge); //add to list of edges in current delta
		int tend = Integer.parseInt(e[2]);
		while(tstart + delta < tend){	
			
			//keep track of the max count of list for analysis			
			temporalModel.updateLength();
			
			//remove all the edges from till current edge is inside delta
			String removeE = edges.remove(0);
			// update the counts of everything with removeE as first edge
			String[] removeEArr = removeE.split(" ");
			String[] pattern = getKeyPattern(removeEArr, 0);	//pattern whr removeE is 1st edge
			
			//decrease from all levels
			temporalModel.removeFromPossible(0, pattern);
			for(int i=1; i<l-1;i++){
				temporalModel.removeEdgeFromMap(pattern, i);
			}
			tstart = Integer.parseInt(edges.get(0).split(" ")[2]);
		}
		//update the counts with current edge
		
    	for(int i=l-1; i>=0;i--){       
    		//get the pattern where edge is ith edge
        	String[] edgeToCheck = getKeyPattern(e,i);
    		//check if there exists a key in i-1th map(prev) that satifies current node mapping
        	if(i>0){
        		//this method gets the number of times edge can be ith edge based on i-1th list
        		temporalModel.addEdgeToMap(edgeToCheck, i-1);
        	}else{
        		//all edges can be 1st edge so just add it to list
        		temporalModel.addToPossible(0, edgeToCheck, 1);
        	}
    	}    	
    	long elapsedTime = System.currentTimeMillis() - start;
    	temporalModel.updateTime(elapsedTime);
    }
	
	/*
	 * getKeyPattern()
	 * based on the template pattern this method outputs a pattern where e is edgeNum' th edge
	 * eg: if there are 3 edges in motif - 01, 21, 01
	 * edge - 8, 5, t will give patterns:
	 * 85* - for edgeNum = 0 (8 in position 0, 5 in position 1, anything else in position 2)
	 * *58 - for edgeNum = 1
	 * 85* - for edgeNum = 2
	 */	
	private String[] getKeyPattern(String[] e, int edgeNum){
		/*String currentMap = temporalModel.getTemplateMap(edgeNum);	//01
    	int[] mapToNode = {Character.getNumericValue(currentMap.charAt(0)),Character.getNumericValue(currentMap.charAt(1))};
    	char[] key = new char[numOfNodes];
    	Arrays.fill(key, '*');
    	key[mapToNode[0]] = e[0].charAt(0);
    	key[mapToNode[1]] = e[1].charAt(0);
    	String edgeToCheck = new String(key);
    	return edgeToCheck;*/
		String currentMap = temporalModel.getTemplateMap(edgeNum);	//01
    	int[] mapToNode = {Character.getNumericValue(currentMap.charAt(0)),Character.getNumericValue(currentMap.charAt(1))};
    	String[] key = new String[numOfNodes];
    	Arrays.fill(key,"*");
    	key[mapToNode[0]] = e[0];
    	key[mapToNode[1]] = e[1];
    	return key;
	}
	
	public void printOutput(){
		
		System.out.print(temporalModel.getMotifsCount()+"    ");
	}
		
}
