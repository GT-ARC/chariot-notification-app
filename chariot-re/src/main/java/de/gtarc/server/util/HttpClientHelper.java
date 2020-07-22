package de.gtarc.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.eclipse.jetty.client.util.InputStreamResponseListener;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientHelper.class);
	HttpClient client = null;

	public HttpClientHelper() {
		client = new HttpClient();

		try {
			client.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String createRequestPath(String url, String query) {
		// System.out.println("\n\n\n Request completed" + new String(url+query));
		return url + query;
	}

	public String createRequestPath(String url, String requesttype, String query, String token) {
		System.out.println("\n\n\n Request completed" + new String(url + requesttype + "/" + query));
		return url + requesttype + "/" + query + "?token=" + token;
	}

	public String sendByGet(String url) {
		// do a get request simple request max 2MB
		String response = null;
		try {
			response = client.GET(url).getContentAsString();
			// Issue a post request
			// System.out.println("Response: " + new String(response));
			return response;
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			this.stop();
		}
		return null;
	}

	public String sendByPut(String url, String value) {
		// do a get request simple request max 2MB
		String response = null;
		return null;
	}

	public void stop() {
		if (client.isRunning()) {
			try {
				client.stop();
				LOGGER.debug("http is being stopped!");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// Blocking request
	public String sendByPost(String url, String value) {
		String response = null;
		// do a post request simple request max 2MB
		try {
			// Issue a post request
			response = client.POST(url).content(new StringContentProvider(value), "application/json").send()
					.getContentAsString();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			this.stop();
		}
		return response;
	}

	// TODO: If asynchronous comm is foreseen, then the code structure need to be
	// changed and event-based code structure need to be written here.
	// Non-Blocking request (Each request is operated via a thread)
	public String sendAsynchronousOnContent(String url) {
		// asynchronous this works as well
		client.newRequest(url).send(new Response.Listener.Adapter() {
			@Override
			public void onContent(Response response, ByteBuffer buffer) {
				String s = StandardCharsets.UTF_8.decode(buffer).toString();
				System.out.println("response Listener Adapter completed" + s.toString());
			}
		});
		return null;
	}

	// Non-Blocking requests (Each request is operated via a thread)
	public void sendAsynchronousOnComplete(String url) {

		client.newRequest(url)
				// Buffer response content up to 8 MiB
				.send(new BufferingResponseListener(8 * 1024 * 1024) {
					@Override
					public void onComplete(Result result) {
						if (!result.isFailed()) {
							byte[] responseContent = getContent();
							String s = "";
							try {
								s = new String(responseContent, "US-ASCII");
								System.out.println("Request completed" + s);
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// Your logic here
						}
					}
				});
	}

	// Non-Blocking requests (Each request is operated via a thread)
	public void sendAsynchronousInputStream(String url) {

		// asynchronous inpuystreampResponseListener, this works!
		InputStreamResponseListener listener = new InputStreamResponseListener();
		client.newRequest(url).send(listener);

		// Wait for the response headers to arrive
		Response res = null;
		try {
			res = listener.get(5, TimeUnit.SECONDS);
			// Look at the response
			if (res.getStatus() == HttpStatus.OK_200) {
				// Use try-with-resources to close input stream.
				try (InputStream responseContent = listener.getInputStream()) {
					// String theString = IOUtils.toString(responseContent, StandardCharsets.UTF_8);
					String theString = IOUtils.toString(responseContent);
					System.out.println("Request completed" + theString.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			this.stop();
		}
	}

	public void hello() {
		// TODO Auto-generated method stub
		System.out.println("Hello");

	}
}
