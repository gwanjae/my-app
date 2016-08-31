import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class NanoHTTPDServer extends NanoHTTPD {

	private boolean https = true;
	private String token = "f911282557b31b87f247ef02f5ef55aceaf4af71";

	
	public NanoHTTPDServer(int port) throws IOException {
		super(port);
		start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
		System.out.println("\nRunning! Point your browsers to http://localhost:" + port + "/ \n");
	}

	public NanoHTTPDServer(String serverName, int port) {
		super(serverName, port);
	}
	
	public static void main(String[] args) {
		try {
			new NanoHTTPDServer(9090);
		} catch (Exception e) {
			System.err.println("Couldn't start server:\n" + e);
		}
	}

	private void setAuthHeaders(HttpURLConnection conn) {
		System.out.println("set auth token..");
		conn.setRequestProperty("Authorization", "Token " + token);
	}

	public Response serve(IHTTPSession session) {
		System.out.println("\n###################################################################################");
		
		HttpsURLConnection conn = null;
		Response res = newFixedLengthResponse("OK");
		
		String uri = session.getUri();
		System.out.println(String.format("uri : %s", uri));

		if (uri.equals("/test")) {
			res = newFixedLengthResponse("OK");
		} else if(uri.startsWith("/bam/")) {
			
			String[] uriArr = uri.split("/");
			String sample = uriArr[2];
			String file = uriArr[3];
			
			System.out.println(String.format("sample : %s, file : %s", sample, file));
			
			String apiURL = "https://221.149.97.143:9443/analysis_files/result_file_tag_filename/" + sample + "/BAM/" + file;
			System.out.println(String.format("api url : %s", apiURL));
			
			Long bytes = this._calculateBytes((String) session.getHeaders().get("range"));
			System.out.println("request for " + session.getUri() + " with range "
					+ (String) session.getHeaders().get("range") + ": " + "total " + bytes + " bytes requested");

			try {
				URL url = new URL(apiURL);
				Long e = Long.valueOf(System.currentTimeMillis());

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

				conn = (HttpsURLConnection) url.openConnection();
				conn.setDefaultSSLSocketFactory(sc.getSocketFactory());
				conn.setHostnameVerifier(new javax.net.ssl.HostnameVerifier() {
					public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
						return true;
					}
				});
				conn.setInstanceFollowRedirects(false);
				conn.setRequestMethod("GET");
				
				Iterator<String> time = session.getHeaders().keySet().iterator();

				System.out.println("[headers]");
				while (time.hasNext()) {
					String header = (String) time.next();
					System.out.println(String.format("\t%s = %s", header, session.getHeaders().get(header)));
					if("range".equals(header)) {
						conn.setRequestProperty("HTTP_RANGE", (String) session.getHeaders().get(header));
					} else {
						conn.setRequestProperty(header, (String) session.getHeaders().get(header));
					}
				}
				conn.setRequestProperty("Accept", "application/json");
				System.out.println("[end headers]");

				if (conn.getRequestProperty("range") == null) {
					res.setStatus(Status.PARTIAL_CONTENT);
				} else {
					res.setStatus(Status.OK);
				}

				conn.setUseCaches(false);
				this.setAuthHeaders(conn);
				conn.setDoOutput(true);
				conn.setDoInput(true);
				res.setChunkedTransfer(true);
				
				if (conn.getResponseCode() == 416) {
					res.setStatus(Status.OK);
					return res;
				}
				
				Map<String,List<String>> headerFieldMap = conn.getHeaderFields();
				if(headerFieldMap != null) {
					for (String key : headerFieldMap.keySet()) {
						System.out.println(String.format("\t%s : %s", key, headerFieldMap.get(key)));
					}
				}

				res.setData(conn.getInputStream());
				res.addHeader("Content-Length", conn.getHeaderField("Content-Length"));
				res.addHeader("range", (String) session.getHeaders().get("range"));
				res.addHeader("Content-Range", conn.getHeaderField("Content-Range"));
				
				System.out.println("Not closing the connection");
				Long time1 = Long.valueOf(System.currentTimeMillis() - e.longValue());
				
				System.out.println("Received " + conn.getContentLength() + " bytes in " + time1 + ": " + bytes.longValue() / time1.longValue() + " kbytes/sec");
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			res.setMimeType("application/octet-stream;charset=UTF-8");
			return res;
		}
		return res;
	}

	private Long _calculateBytes(String string) {
		if (string == null) {
			return Long.valueOf(0L);
		} else {
			String[] bits = string.replace("bytes=", "").split("-");
			Long bytes = Long.valueOf(Long.parseLong(bits[1]) - Long.parseLong(bits[0]));
			return bytes;
		}
	}
}
