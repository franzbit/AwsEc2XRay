package com.aws.xray;

import com.amazonaws.xray.entities.Segment;
import com.amazonaws.xray.proxies.apache.http.HttpClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Subsegment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


@SpringBootApplication
public class Application implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(Application.class);
		application.setWebApplicationType(WebApplicationType.NONE);//.setWebEnvironment(false);
		application.run(args);
	}

	@Override
	public void run(String... args) throws Exception {

		System.out.println("run - start");
		AWSXRay.beginSegment("My-Custom-Segment");

		long timeBefore = System.currentTimeMillis();

		Subsegment subsegment1 = AWSXRay.beginSubsegment("Sleep 1");
		Segment segment = AWSXRay.getCurrentSegment();
		subsegment1.putMetadata("resources", "game", "JohnsGame");
		subsegment1.putAnnotation("gameid", "20031979");

		try {
			System.out.println("run - before sleep 1");
			Thread.sleep(2000);
			System.out.println("run - after sleep 1");

		} catch ( Exception ex ) {
			System.out.println("run - Error: "+ex);
			subsegment1.addException(ex);
		} finally {
			AWSXRay.endSubsegment();
		}

		Subsegment subsegment2 = AWSXRay.beginSubsegment("Sleep 2");
		try {
			System.out.println("run - before sleep 2");
			Thread.sleep(500);
			throw new RuntimeException("My Dummy Error");

		} catch ( Exception ex ) {
			System.out.println("run - Error: "+ex);
			subsegment2.addException(ex);
		} finally {
			AWSXRay.endSubsegment();
		}

		AWSXRay.endSegment();
		System.out.println("run - end - Time Took (ms): {}"+(System.currentTimeMillis() - timeBefore));


		AWSXRay.beginSegment("http://uinames.com/api/");
		httpRequest();
		AWSXRay.endSegment();
	}

	/*
	 * simulate outgoing HTTP request
	 */
	void httpRequest() throws IOException {

		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet("http://uinames.com/api/");
		CloseableHttpResponse response = httpclient.execute(httpGet);
		try {
			HttpEntity entity = response.getEntity();
			InputStream inputStream = entity.getContent();
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> jsonMap = mapper.readValue(inputStream, Map.class);
			String reg = jsonMap.get("region");
			EntityUtils.consume(entity);
			System.out.println("httpRequest response for region: "+reg+"\n");
		} finally {
			response.close();
		}
	}

}
