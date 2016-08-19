import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.stop;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
public class SparkApp {

	public static void main(String[] args) {
		port(9090);
		
		get("/hello", (req, res) -> {
			return "Hello World1";
		});
		
		get("/bam/:sample/:name", (request, response) -> {
			
			String result = "";
			String sample = request.params(":sample");
			String name = request.params(":name");
			
			if(name.endsWith("bam.bai")) {
				name = name.replaceAll("bam.bai", "bai");
			}
			
			String url = "https://221.149.97.143:9443/analysis_files/result_file_tag_filename/" + sample + "/BAM/" + name;
			String token = "17838415575eff595d2626d3e19b7026182eace4";
			System.out.println(url);

			HttpsURLConnection conn = null;
			try {
				TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					public void checkClientTrusted(X509Certificate[] certs,
							String authType) {
					}

					public void checkServerTrusted(X509Certificate[] certs,
							String authType) {
					}
				} };

				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				
				URL obj = new URL(url);
				conn = (HttpsURLConnection) obj.openConnection();
				conn.setDefaultSSLSocketFactory(sc.getSocketFactory());
				conn.setHostnameVerifier(new javax.net.ssl.HostnameVerifier() {
					public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
						return true;
					}
				});
				conn.setDefaultUseCaches(false);
				conn.setUseCaches(false);
//				conn.setConnectTimeout(30000000);
//				conn.setReadTimeout(30000000);
				conn.setRequestMethod("GET");

				Long bytes = _calculateBytes((String) request.headers("Range"));
				System.out.println("request for " + url + " with range "
						+ (String) request.headers("Range") + ": " + "total " + bytes + " bytes requested");
				
				/**
					Accept : text/plain
					Cache-Control : no-cache
					Connection : keep-alive
					Host : 127.0.0.1:9090
					Pragma : no-cache
					Range : bytes=0-10
					User-Agent : IGV Version 2.3.74 (111)05/25/2016 04:14 PM
				 */
				if(request.headers() != null) {
					for(String key : request.headers()) {
						System.out.println(String.format("\t%s : %s", key ,request.headers(key)));
						if(key.toLowerCase().equals("range")) {
							conn.setRequestProperty("Range", request.headers(key));
						} else {
							conn.setRequestProperty(key, request.headers(key));
						}
					}
				}
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Authorization", "Token " + token);
				conn.setRequestProperty("Accept", "application/json");

				conn.setDoOutput(true);
				conn.setDoInput(true);
				response.header("Transfer-Encoding", "chunked");
				System.out.println("[connect response code] " + conn.getResponseCode());
				
				if(conn.getResponseCode() >= 400) {
					response.status(206);
				} else {
					response.status(conn.getResponseCode());
//				BufferedReader rd;
//				String line;
//				for (rd = new BufferedReader(
//						new InputStreamReader(conn.getInputStream())); (line = rd.readLine()) != null; result = result
//								+ line) {
//					;
//				}
//				rd.close();
//				System.out.println(result.length());
					
					try (OutputStream outputStream = new BufferedOutputStream(response.raw().getOutputStream());
							BufferedInputStream bufferedInputStream = new BufferedInputStream(conn.getInputStream())) {
						byte[] buffer = new byte[1024];
						int len;
						while ((len = bufferedInputStream.read(buffer)) > 0) {
							outputStream.write(buffer, 0, len);
						}
						outputStream.flush();
						outputStream.close();
					}
				}
				
				response.type("application/octet-stream;charset=UTF-8");
				response.header("Content-Transfer-Encoding", "binary");
				response.header("Content-Length", conn.getHeaderField("Content-Length"));
				response.header("Content-Range", conn.getHeaderField("Content-Range"));
				response.header("Range", (String) conn.getRequestProperty("Range"));
			} catch (Exception arg8) {
				arg8.printStackTrace();
			}
			
			/*
			if(conn != null) {
				conn.disconnect();
				System.out.println("disconnected..");
			}
			*/
			return response;
		});
		
		get("/stop", (req, res) -> {
			stop();
			System.exit(0);
			return null;
		});
	}
	
	private static Long _calculateBytes(String string) {
		if (string == null) {
			return Long.valueOf(0L);
		} else {
			String[] bits = string.replace("bytes=", "").split("-");
			Long bytes = Long.valueOf(Long.parseLong(bits[1]) - Long.parseLong(bits[0]));
			return bytes;
		}
	}

}
