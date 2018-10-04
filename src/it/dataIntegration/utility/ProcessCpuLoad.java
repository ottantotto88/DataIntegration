package it.dataIntegration.utility;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;

import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/*
 * Questa classe implementa dei metodi utilizzati per analizzare l'utilizzo della cpu
 *  da parte della JVM durante l'esecuzione del programma
 */
public class ProcessCpuLoad {
	private static ArrayList<Double> cpuLoads = new ArrayList<Double>();
	
	public static void getProcessCpuLoad() throws Exception {

		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
		javax.management.AttributeList list = mbs.getAttributes(name, new String[] { "ProcessCpuLoad" });

		if (list.isEmpty()) {
			cpuLoads.add(Double.NaN);
		} else {
			Attribute att = (Attribute) list.get(0);
			Double value = (Double) att.getValue();

			// usually takes a couple of seconds before we get real values
			if (value == -1.0) {
				cpuLoads.add(Double.NaN);
			} else {
				cpuLoads.add((int) (value * 1000) / 10.0);
			}
		}
	}

	public static double getMaxProcessLoad() {
		return Collections.max(cpuLoads);
	}
	
	public static double getAverageProcessLoad() {
	      double sum = 0;
	      for (int i=0; i< cpuLoads.size(); i++) {
	            sum += cpuLoads.get(i);
	      }
	    return  Math.floor((sum / cpuLoads.size()) * 100) / 100;
	  }
	
	public static void clearArrayList() {
		cpuLoads.clear();
	}
}
