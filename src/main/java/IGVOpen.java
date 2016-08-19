import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;

public class IGVOpen {

	public static void main(String[] args) throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder(
				"/Users/gjyoo/.ngenebio_analysys_gui/jre/jre1.8.0_92.jre/Contents/Home/bin/java", "-jar",
				"/Users/gjyoo/.ngenebio_analysys_gui/IGV_2.3.80/igv.jar");
		processBuilder.directory(new File("/Users/gjyoo/.ngenebio_analysys_gui/IGV_2.3.80/"));
		File log = new File("/Users/gjyoo/.ngenebio_analysys_gui/IGV_2.3.80/log.txt");
		processBuilder.redirectErrorStream(true);
		processBuilder.redirectOutput(Redirect.appendTo(log));
		Process p = processBuilder.start();
		p.waitFor();
		System.out.println("Done");
	}	
}
