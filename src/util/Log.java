package util;

public class Log {

	private boolean active = false ;

	public void setOn() {
		active = true;
	}
	
	public void setOff() {
		active = false;
	}

	public boolean isActive() {
		return active;
	}
	
	public void println(String s){
		if(active)
			System.out.println(s);
		
	}
}
