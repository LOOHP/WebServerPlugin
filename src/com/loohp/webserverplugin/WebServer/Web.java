package com.loohp.webserverplugin.WebServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

public class Web {

	public static File WebFolder;
	public static Server server;

	private static HtaccessFile htaccessFile;

	public static void load(int port, File dataFolder, Class<?> mainClass) {
		System.out.println("[WebServerPlugin] Starting Web Server..");

		WebFolder = new File(dataFolder, "web");

		System.setProperty("org.eclipse.jetty.util.log.class", "OFF");
		System.setProperty("org.eclipse.jetty.LEVEL", "OFF");

		try {
			server = new Server();
			ServerConnector connector = new ServerConnector(server);
			connector.setPort(port);
			server.addConnector(connector);

			System.out.println("[WebServerPlugin] Web Server listening on port " + connector.getPort());

			if (!WebFolder.exists()) {
				WebFolder.mkdirs();
			}
			if (WebFolder.listFiles().length == 0) {
				String fileName = "index.html";
				File file = new File(WebFolder, fileName);
				try (InputStream in = mainClass.getClassLoader().getResourceAsStream(fileName)) {
					Files.copy(in, file.toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			URL file = WebFolder.toURI().toURL();
			if (file == null) {
				throw new RuntimeException("[WebServerPlugin] Unable to find resource directory");
			}

			URI webRootUri = file.toURI().resolve("./").normalize();
			System.out.println("[WebServerPlugin] WebRoot is " + webRootUri);
			
			File htaccess = new File(WebFolder, ".htaccess");
			htaccessFile = new HtaccessFile(htaccess);

			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath("/");
			context.setBaseResource(Resource.newResource(webRootUri));

			context.setErrorHandler(new ErrorDocumentHandler());

			server.setHandler(context);

			ServletHolder holderPwd = new ServletHolder("default", DefaultServlet.class);
			holderPwd.setInitParameter("dirAllowed", "true");
			holderPwd.setInitParameter("allowOverride", "true");
			context.addServlet(holderPwd, "/");

			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class ErrorDocumentHandler extends ErrorHandler {
		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {	
			String errorDocumentLocation = htaccessFile.getErrorResponses().get(response.getStatus());
			if (errorDocumentLocation != null) {
				response.sendRedirect(errorDocumentLocation);
			} else {
				super.handle(target, baseRequest, request, response);
			}
		}
	}

}