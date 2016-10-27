package com.blog10_99.selenium;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class TestNetworkLog {

	public static void main(String args[]) throws Exception {
		WebDriver driver;

		DesiredCapabilities capabilities = DesiredCapabilities.chrome();

		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
		capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

		driver = new RemoteWebDriver(new URL("http://localhost:9515"), capabilities);

		System.out.println("Got the driver " + driver.getCurrentUrl());
		driver.manage().timeouts().setScriptTimeout(3, TimeUnit.SECONDS);

		driver.get("http://google.com");
		Thread.sleep(2000);
		WebElement element = driver.findElement(By.name("q"));
		element.sendKeys("Cheese!"); // search for this string in log output
		element.submit();
		Thread.sleep(2000);

		LogEntries les = driver.manage().logs().get(LogType.PERFORMANCE);

		File fout = new File("network.log");
		FileOutputStream fos = new FileOutputStream(fout);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		for (LogEntry le : les) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(le.getMessage());
			String prettyJsonString = gson.toJson(je);

			bw.write(prettyJsonString);
			bw.newLine();
		}

		bw.close();
		
		System.out.println("Test complete. please close browser and find network logs under <PROJECT-FOLDER>/network.log file");
	}
}
