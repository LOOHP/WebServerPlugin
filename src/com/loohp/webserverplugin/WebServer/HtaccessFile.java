package com.loohp.webserverplugin.WebServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.loohp.webserverplugin.Utils.CustomStringUtils;

public class HtaccessFile {

	Map<Integer, String> errorResponses;

	public HtaccessFile(File htaccess) throws IOException {
		errorResponses = new HashMap<>();
		
		if (!htaccess.exists()) {
			return;
		}

		BufferedReader reader = new BufferedReader(new FileReader(htaccess));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] args = CustomStringUtils.splitStringToArgs(line);
			if (args.length == 3 && args[0].equalsIgnoreCase("ErrorDocument")) {
				try {
					int code = Integer.parseInt(args[1]);

					errorResponses.put(code, args[2]);
				} catch (Exception e) {
					System.out.println("Error while parsing \".htaccess\", " + args[1] + " is not an integer!");
				}
			}
		}
		reader.close();
	}

	public Map<Integer, String> getErrorResponses() {
		return errorResponses;
	}

	public void setErrorResponses(Map<Integer, String> errorResponse) {
		this.errorResponses = errorResponse;
	}
	
	public void addErrorResponse(int code, String location) {
		errorResponses.put(code, location);
	}
	
	public String removeErrorResponse(int code) {
		return errorResponses.remove(code);
	}

}
