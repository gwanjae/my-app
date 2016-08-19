public class ClientOpen {

	public static void main(String[] args) throws Exception {
		ProcessBuilder processBuilder = new ProcessBuilder("/Applications/NGeneAnalySys.app/Contents/MacOS/NGeneAnalySys");
		Process p = processBuilder.start();
//		p.waitFor();
		System.exit(0);
	}
	
}
