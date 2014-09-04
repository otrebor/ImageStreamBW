package proc;

public class Counter {
private int count = 0;
private int max ;

public Counter(int max){
	this.setMax(max);
}

public synchronized void increment(){
	count++;
}

public synchronized void decrement(){
	count--;
}

public synchronized int getValue(){
	return count;
}

public synchronized void setMax(int max) {
	this.max = max;
}

public synchronized int getMax() {
	return max;
}
}
