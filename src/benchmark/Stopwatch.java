package benchmark;

/**
   * A class to help benchmark code
   * It simulates a real stop watch
   */
public class Stopwatch {

    private long startTime = -1;
    private long stopTime = -1;
    private boolean running = false;

    public Stopwatch start() {
       startTime = System.nanoTime();
       running = true;
       return this;
    }
    public Stopwatch stop() {
       stopTime = System.nanoTime();
       running = false;
       return this;
    }
    /** returns elapsed time in milliseconds
      * if the watch has never been started then
      * return zero
      */
    public double getElapsedTime() {
       if (startTime == -1) {
          return 0;
       }
       if (running){
       return (System.nanoTime() - startTime)/ 1000000.0;
       } else {
       return (stopTime-startTime)/ 1000000.0;
       } 
    }

    public Stopwatch reset() {
       startTime = -1;
       stopTime = -1;
       running = false;
       return this;
    }
}
