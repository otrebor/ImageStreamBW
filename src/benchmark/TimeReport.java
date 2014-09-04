package benchmark;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

/**
 * 
 * @author Roberto Belli
 *
 */
public class TimeReport {
	/***
	 * This class is used to keep track of the elaboration time in each phase of elaboration
	 * 
	 */
	
private double pxratio = 0 , latency = 0 , splitting = 0, merging = 0, elaborationAVG = 0, pxTime =0 , loading = 0;
private String filename = "";
private Stopwatch timer = new Stopwatch();
public final static String fieldNames = "LOADING ; SPLITTING ; ELABAVG ; PXTIME ; MERGING; LATENCY ; PXRATIO ; FILENAME ";

public void setSplittingTime(double splitting) {
	this.splitting = splitting;
}

public double getSplittingTime() {
	return splitting;
}

public void setMergingTime(double merging) {
	this.merging = merging;
}

public double getMergingTime() {
	return merging;
}

public void setElaborationAVGTime(double elaborationAVG) {
	this.elaborationAVG = elaborationAVG;
}

public double getElaborationAVGTime() {
	return elaborationAVG;
}

public void setPxTime(double pxTime) {
	this.pxTime = pxTime;
}

public double getPxTime() {
	return pxTime;
}


public void setLoadingTime(double loading) {
	this.loading = loading;
}

public double getLoadingTime() {
	return loading;
}

public void setFilename(String name) {
	this.filename = name;
}

public String getFileName() {
	return filename;
}

/*public String toXML(){
	return "<LOADING>" + this.loading + "</LOADING>\n<SPLITTING>" + this.splitting + "</SPLITTING>\n<ELABSUM>" + this.elaborationSum + "</ELABSUM>\n<ELABAVG>" + this.elaborationAVG + "</ELABAVG>\n<PXTIME>" + this.pxTime + "</PXTIME>\n<MERGING>" + this.merging +"</MERGING>\n<FILENAME>" + this.filename +"</FILENAME>";
}

public static void writeXML(String path, Collection<TimeReport> tr){
	try {
		BufferedWriter out = new BufferedWriter(new FileWriter(path));
		out.write("<document>");
		out.newLine();
		for(TimeReport par : tr){
			out.write("<row>");
			out.newLine();
		out.write(par.toXML());
		out.newLine();
		out.write("</row>");
		out.newLine();
		}
		out.write("</document>");
		out.close();
	}
	catch (IOException e)
	{
		System.err.println("Exception in writing XML ");		
	}
}*/
public void printFormatted(){
	System.out.printf(Locale.ITALY, "%f; %f; %f; %f; %f; %f; %f; %s \n",  this.loading, this.splitting , this.elaborationAVG, this.pxTime, this.merging, this.latency, pxratio, this.filename);
}
public String toString(){
	return "" + this.loading + " ; " + this.splitting + " ; " + this.elaborationAVG + " ; " + this.pxTime + " ; " + this.merging +" ; "+this.latency+" ; "+pxratio+" ; " + this.filename ;
}

public static void writeCSV(String path, Collection<TimeReport> tr){
	try {
		BufferedWriter out = new BufferedWriter(new FileWriter(path));
		out.write(fieldNames);
		out.newLine();
		for(TimeReport par : tr){
		out.write(par.toString());
		out.newLine();
		}
		out.close();
	}
	catch (IOException e)
	{
		System.err.println("Exception in writing CSV ");		
	}
}

public void setLatency(double latency) {
	this.latency = latency;
}

public double getLatency() {
	return latency;
}

public void startLatencyTimer() {
	timer.start();
}

public void stopLatencyTimer() {
	timer.stop();
	this.latency = timer.getElapsedTime();
}

public void setRatioLatencyPX(double width , double height){
	this.pxratio = latency / (width * height);
}

public double getLatencyPxRatio(){
	return this.pxratio;
}
}
