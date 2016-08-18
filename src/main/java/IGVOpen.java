import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IGVOpen {

	public static void main(String[] args) {
		try {
			String result = "";
			URL url = new URL("http://localhost:60151/load?file=http://localhost:9090/bam/94/NA12878-2_S3_L001_final.bam&locus=BRCA1&genome=hg19&merge=false");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			
			BufferedReader rd;
			String line;
			for (rd = new BufferedReader(
					new InputStreamReader(conn.getInputStream())); (line = rd.readLine()) != null; result = result
					+ line) {
				;
			}
			
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
