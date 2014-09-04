package util;

public class Utils {
private static boolean gcEnable = false ;

public static void gcCall(){
	if(gcEnable){
	System.gc();
	}
}

public synchronized static void enableGC(){
	gcEnable=true;
}

public static boolean gcIsEnabled(){
	return gcEnable;
}
}
