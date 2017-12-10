package temporalStreaming.Model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
/*
 * This class implements the data model and the methods that access/ edit data
 */

public class Model {
	
	//variables
	int delta;
	ArrayList<HashMap<String, Integer>> possibleEdges = new ArrayList<HashMap<String, Integer>>();
	//1 for each edge - size of arraylist = l, stores map of nodes - count
	int l;
	String[] motifTemplate;		//[01,12,20]
	int motifsCount = 0;
	int numOfNodes = 0;
	
	//for analysis
	long maxTime = 0;
	int[] maxLength = {0,0,0};
	
	public void updateTime(long time){
		if(time > maxTime){
			maxTime = time;
		}
	}
	
	public void updateLength(){
		for(int i=0; i<3; i++){
			int newSize = possibleEdges.get(i).size();
			if(maxLength[i] < newSize){
				maxLength[i] = newSize;
			}
		}
	}
	
	public void writeAnalysis(String templ) throws IOException{
		BufferedWriter outputWriter = null;
	    outputWriter = new BufferedWriter(new FileWriter("analysis.txt"));
	    outputWriter.write("Motif Pattern : "+templ);
	    outputWriter.newLine();
	    outputWriter.write("Motif Count : "+motifsCount);
	    outputWriter.newLine();
	    outputWriter.write("Max time : "+maxTime);// Here I know i cant just write x[0] or anything. Do i need 
	                             //to loop in order to write the array?
	    outputWriter.newLine();
	    for(int i=0; i<3;i++){
	    	outputWriter.write("Max Length "+(i+1)+" : "+maxLength[i]);
	    	outputWriter.newLine();
	    }
	    outputWriter.flush();  
	    outputWriter.close();  
	}
	//constructor
	public Model(String motifTemplate, int delta, int numOfNodes){
		this.motifTemplate = motifTemplate.split(",");
		this.numOfNodes = numOfNodes;
		this.delta = delta;
		l = this.motifTemplate.length;
		for(int i = 0; i<l; i++){
			possibleEdges.add(new HashMap<String,Integer>());
		}
	}
	
	//properties
	public int getDelta() {
        return this.delta;
    }
 
    public void setDelta(int delta) {
        this.delta = delta;
    }
    public String getTemplateMap(int index){
    	return motifTemplate[index];
    }
    public int getL(){
    	return l;
    }
    public int getMotifsCount(){
    	return motifsCount;
    }
    //methods
    /*
     * Add to possible list of edge combinations
     */
    public void addToPossible(int edgeNum, String[] pattern, int count){
    	HashMap<String, Integer> edgeList = possibleEdges.get(edgeNum);
    	
    	if(edgeNum == l-1){
    		//this edge could be the last edge - update total motif count
        	motifsCount += count;    		
    	}
    	String nodeVals = String.join("|", pattern);
    	count = (edgeList.get(nodeVals) == null)?count:edgeList.get(nodeVals)+count;
    	edgeList.put(nodeVals, count);
    	possibleEdges.set(edgeNum, edgeList);
    }
    
    /*
     * Based on edgeNum list value (previous),  it updates if edge could be edgeNum+1 th edge
     */
    public void addEdgeToMap(String[] keyPattern, int edgeNum){
    	HashMap<String, Integer> prev = possibleEdges.get(edgeNum);
    	Iterator keys = prev.keySet().iterator();
    	while(keys.hasNext()){
    		String key = (String) keys.next();
    		String[] keyVals = key.split("\\|");
    		String[] mergedVal = matchPattern(keyVals, keyPattern);
    		if(mergedVal != null){    			
    			addToPossible(edgeNum+1,mergedVal,prev.get(key));
    		}
    	}
    }
    
    private String[] matchPattern(String[] key, String[] keyPattern){
    	String[] mergedVal = new String[numOfNodes];
    	for(int i=0; i<keyPattern.length;i++){
			if(keyPattern[i].equals("*")){
				mergedVal[i]= key[i];
			}else if(key[i].equals("*")){
				//this is a newly added node - so it should be different from others
				if(Arrays.asList(key).contains(keyPattern[i])){
					mergedVal = null;
					break;
				}else{
					mergedVal[i] = keyPattern[i];
				}
			
			}else if(!keyPattern[i].equals(key[i])){ 
				mergedVal = null;
				break;
			}
			if(key[i].equals(keyPattern[i]) && !key[i].equals("*")){
				mergedVal[i] = key[i];
			}
			
		}
    	return mergedVal;
    }
    
    /*
     * method to decrement count if the edge goes out of delta
     */
    public void removeEdgeFromMap(String[] keyPattern, int edgeNum){
    	
    	HashMap<String, Integer> current = possibleEdges.get(edgeNum);
    	if(current == null){
    		return;
    	}    	
		Iterator keys = current.keySet().iterator();
    	while(keys.hasNext()){
    		String key = (String) keys.next();
    		String[] keyVal = key.split("\\|");
    		String[] mergedVal = matchPattern(keyVal, keyPattern);        		
    		if(mergedVal != null){  
    			String currentMap = motifTemplate[edgeNum];
    			int[] mapToNode = {Character.getNumericValue(currentMap.charAt(0)),Character.getNumericValue(currentMap.charAt(1))};
    			String firstTemplate = motifTemplate[0];
    			int[] firstNode = {Character.getNumericValue(firstTemplate.charAt(0)),Character.getNumericValue(firstTemplate.charAt(1))};
    			String[] val = new String[numOfNodes];
    	    	Arrays.fill(val, "*");
    	    	val[firstNode[0]] = mergedVal[mapToNode[0]];
    	    	val[firstNode[1]] = mergedVal[mapToNode[1]];
    	    	String firstEdgeToCheck = String.join("|", val);
    	    	
    			int count = current.get(key)-possibleEdges.get(0).get(firstEdgeToCheck);
    			if(count > 0){
    				current.put(key, count);        				
    			}else{
    				keys.remove();
    			}        			
    		}
    	}
    	
    	
    }
    //Decrement count
    public void removeFromPossible(int edgeNum, String[] pattern){
    	HashMap<String, Integer> edgeList = possibleEdges.get(edgeNum);
    	String nodeVals = String.join("|", pattern);
    	int count = edgeList.get(nodeVals)-1;
    	if(count == 0){
    		edgeList.remove(nodeVals);
    	}else{
        	edgeList.put(nodeVals, count);    		
    	}
    	possibleEdges.set(edgeNum, edgeList);
    }
    
    //test methods
    public void printPossibleNodes(){
    	for(int i=0;i<l;i++){
    		System.out.println("--------------"+(i+1)+"   "+motifTemplate[i]);
    		HashMap<String, Integer> curr = possibleEdges.get(i);
    		Iterator iter = curr.keySet().iterator();
    		while(iter.hasNext()){
    			String key = (String) iter.next();
    			System.out.println(key + "   ---  "+curr.get(key));
    		}
    	}
    }
    
}
