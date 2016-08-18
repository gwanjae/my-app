import java.awt.GraphicsEnvironment;
import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

@SuppressWarnings("restriction")
public class SystemInformation {

	public static void main(String[] args) {
		OperatingSystemMXBean t = (OperatingSystemMXBean) ManagementFactory
				.getPlatformMXBean(OperatingSystemMXBean.class);

		Runtime runtime = Runtime.getRuntime();

		System.out.println("---- SYSTEM INFO ----");
		System.out.println(String.format("OS: %s %s %s", new Object[] { t.getName(), t.getVersion(), t.getArch() }));
		System.out.println(String.format("Total physical memory: %s MB",
				new Object[] { Long.valueOf(t.getTotalPhysicalMemorySize() / 1024L / 1024L) }));
		System.out.println(String.format("Total virtual memory: %s MB",
				new Object[] { Long.valueOf(t.getTotalSwapSpaceSize() / 1024L / 1024L) }));
		System.out.println(String.format("Total video memory: %s MB",
				new Object[] { Long.valueOf(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
						.getAvailableAcceleratedMemory() / 1024L / 1024L) }));
		System.out.println(
				String.format("Processors : %d", new Object[] { Integer.valueOf(t.getAvailableProcessors()) }));
		System.out.println("---- SYSTEM INFO ----");

		System.out.println("----- SYSTEM LOAD -----");
		double loadAverage = t.getSystemLoadAverage();
		double processCpuLoad = t.getProcessCpuLoad();
		double systemCpuLoad = t.getSystemCpuLoad();
		long freePhysicalMemorySize = t.getFreePhysicalMemorySize();
		long totalPhysicalMemorySize = t.getTotalPhysicalMemorySize();
		long committedVirtualMemorySize = t.getCommittedVirtualMemorySize();
		long freeSwapSpaceSize = t.getFreeSwapSpaceSize();
		long totalSwapSpaceSize = t.getTotalSwapSpaceSize();
		long totalHeapSize = runtime.totalMemory();
		long maxHeapSize = runtime.maxMemory();
		long freeHeapSize = runtime.freeMemory();
		System.out.println(String.format("cpu : load avg : %.2f ; process ld : %.2f %%; system ld : %.2f %%",
				new Object[] { Double.valueOf(loadAverage), Double.valueOf(processCpuLoad * 100.0D),
						Double.valueOf(systemCpuLoad * 100.0D) }));
		System.out.println(String.format(
				bar(freePhysicalMemorySize, totalPhysicalMemorySize) + " phys mem : %d MB free / %d MB total",
				new Object[] { Long.valueOf(freePhysicalMemorySize / 1024L / 1024L),
						Long.valueOf(totalPhysicalMemorySize / 1024L / 1024L) }));
		System.out.println(String.format(
				bar(freeSwapSpaceSize, totalSwapSpaceSize) + " virt mem : %d MB commit; %d MB free / %d total",
				new Object[] { Long.valueOf(committedVirtualMemorySize / 1024L / 1024L),
						Long.valueOf(freeSwapSpaceSize / 1024L / 1024L),
						Long.valueOf(totalSwapSpaceSize / 1024L / 1024L) }));
		System.out.println(
				String.format(bar(freeHeapSize, totalHeapSize) + " java heap : %d MB max; %d MB free; %d MB total",
						new Object[] { Long.valueOf(maxHeapSize / 1024L / 1024L),
								Long.valueOf(freeHeapSize / 1024L / 1024L),
								Long.valueOf(totalHeapSize / 1024L / 1024L) }));
		System.out.println("----- SYSTEM LOAD -----");

	}

	public static String bar(long val, long max) {
		if (max == 0L) {
			return "[                              ]";
		} else {
			byte nchars = 30;
			int c = (int) ((long) nchars * val / max);
			StringBuffer sb = new StringBuffer("[");

			for (int i = 0; i < nchars; ++i) {
				sb.append(i == c ? "|" : (i < c ? "o" : "="));
			}

			sb.append("]");
			return sb.toString();
		}
	}
}
