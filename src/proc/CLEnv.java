package proc;

import java.io.IOException;

import util.GPUtils;

import com.nativelibs4java.opencl.CLBuildException;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLDevice;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLQueue;
import com.nativelibs4java.opencl.JavaCL;

public class CLEnv {
	private static CLContext context = null;
	private CLProgram program = null;
	private CLQueue queue = null;
	private CLKernel[] kernels = null;
	private static String kernelSource = null ;
	private CLDevice device = null ;
	private static String kernelPath = "rgb2array.cl";
	private boolean singleKernel = false;
	
	public CLEnv(CLDevice dev, boolean singleKernel) throws CLBuildException{
		//single context is necessary because creating too many context rise OutOfMemory exception
		if(CLEnv.getCLContext()==null)
			setCLContext(JavaCL.createContext(null, dev));
		if(kernelSource==null){
			try {
				setCLSource( GPUtils.getKernelSource(kernelPath));
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Problem loading GPU kernel : "+e);
				System.exit(1);
			}
		}
		this.setCLProgram(context.createProgram(kernelSource));
		this.setCLKernels(program.createKernels());
		this.setCLQueue(context.createDefaultQueue());
		this.setCLDevice(dev);
	}

	private static synchronized void setCLContext(CLContext context) {
		CLEnv.context = context;
	}

	public static synchronized CLContext getCLContext() {
		return context;
	}

	private void setCLProgram(CLProgram program) {
		this.program = program;
	}

	public CLProgram getCLProgram() {
		return program;
	}

	private void setCLQueue(CLQueue queue) {
		this.queue = queue;
	}

	public CLQueue getCLQueue() {
		return queue;
	}

	private void setCLKernels(CLKernel[] kernels) {
		this.kernels = kernels;
	}

	public CLKernel[] getCLKernels() {
		return kernels;
	}

	private static void setCLSource(String source) {
		kernelSource = source;
	}

	public static String getCLSource() {
		return kernelSource;
	}

	private void setCLDevice(CLDevice device) {
		this.device = device;
	}

	public CLDevice getCLDevice() {
		return device;
	}

	public void setSingleKernel(boolean singleKernel) {
		this.singleKernel = singleKernel;
	}

	public boolean isSingleKernel() {
		return singleKernel;
	}
}
