package de.hbt.kicker.web;

import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hsqldb.Server;
import org.hsqldb.ServerConstants;

public class HsqldbManager {

	private static final Logger LOG = Logger.getLogger(Server.class.getName());

	private String path;

	private String name;

	private int port = 9000;

	private Server server;

	public void setName(String name) {
		this.name = name;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void start() {
		server = new Server();
		server.setPort(port);
		server.setNoSystemExit(true);
		server.setDatabasePath(0, path);
		server.setDatabaseName(0, name);
		server.setErrWriter(new PrintWriter(System.out));
		server.setLogWriter(new PrintWriter(System.out));
		server.setSilent(true);
		server.setTrace(false);
		server.start();
		int maxWait = 5000 * 2;
		int currentWait = 0;
		while (server.getState() != ServerConstants.SERVER_STATE_ONLINE) {
			LOG.log(Level.INFO, "Starting hsqldb-server. State: " + server.getStateDescriptor());
			LOG.log(Level.INFO, name);
			LOG.log(Level.INFO, path);
			LOG.log(Level.INFO, "Waiting 5 seconds ...");
			try {
				Thread.sleep(5000);
				currentWait += 5000;
			} catch (InterruptedException interrupt) {
				LOG.log(Level.WARNING, "Start interrupted.", interrupt);
				break;
			}
			if(currentWait >= maxWait) {
				LOG.info("Giving up... ;-)");
				break;
			}
		}
		if(server.getState() == ServerConstants.SERVER_STATE_ONLINE) {
			LOG.log(Level.INFO, "Started!");
		}
	}

	public void stop() {
		server.stop();
		int maxWait = 5000 * 5;
		int currentWait = 0;
		while (server.getState() != ServerConstants.SERVER_STATE_SHUTDOWN) {
			LOG.log(Level.INFO, "Shutting down hsqldb-server. State: " + server.getStateDescriptor());
			LOG.log(Level.INFO, name);
			LOG.log(Level.INFO, path);
			LOG.log(Level.INFO, "Waiting 5 seconds ...");
			try {
				Thread.sleep(5000);
				currentWait += 5000;
			} catch (InterruptedException interrupt) {
				LOG.log(Level.WARNING, "Shutdown interrupted.", interrupt);
				break;
			}
			if(currentWait >= maxWait) {
				LOG.info("Giving up... ;-)");
				break;
			}
		}
		if(server.getState() == ServerConstants.SERVER_STATE_SHUTDOWN) {
			LOG.log(Level.INFO, "Shutdown complete!");
		}
		server = null;
	}

}
