package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.nativelibs4java.opencl.CLDevice;
import com.nativelibs4java.opencl.CLPlatform;
import com.nativelibs4java.opencl.JavaCL;

public class GPUtils {
	static private String kernelSource = null;
	static private String kernelPath = null ;
	
	public static List<CLDevice> getDevices(){
		List<CLDevice> devices = new ArrayList<CLDevice>();
        try {
                for (CLPlatform platform : JavaCL.listPlatforms()) {
                        for (CLDevice device : platform.listAllDevices(true)) {
                                devices.add(device);
                        }
                }
                if (!devices.isEmpty())
                        return devices;
                        // CLPlatform.getBestDevice(Arrays.asList(CLPlatform.DeviceFeature.MaxComputeUnits), devices);
        } catch (Exception ex) {
                ex.printStackTrace();
        }
        if (devices.isEmpty()) {
                System.out.println("No OpenCL Device detected");
        }
        return devices;
	}
	
    public static String readTextResource(String path) throws IOException {
    	System.out.println("reading -> " + path);
    	BufferedReader rin = new BufferedReader(new FileReader(path));
        String line;
        StringBuilder b = new StringBuilder();
        while ((line = rin.readLine()) != null) {
                b.append(line);
                b.append("\n");
        }
        return b.toString();
}
	private synchronized static void setKernelSource(String kernelPath) throws IOException {
		kernelSource  = util.GPUtils.readTextResource(kernelPath);
		GPUtils.kernelPath = kernelPath ;
	}

	public synchronized static String getKernelSource(String kernelPath) throws IOException {
		if(GPUtils.kernelSource ==null || !kernelPath.equals(GPUtils.kernelPath))
			setKernelSource(kernelPath);
		return new String(kernelSource);
	}
}
