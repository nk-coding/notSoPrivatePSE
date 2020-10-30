package de.unistuttgart.iste.rss.oo.hamstersimulator.server.http.server;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import io.javalin.http.Context;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

public class HamsTTPServer {
    public static final int PORT = 8008;
    private static final int HTTP_SERVER_PORT = 8080;

    private final ServerSocket serverSocket;
    private final Javalin httpServer;
    private final Map<Integer, HamsterSession> sessions = new ConcurrentHashMap<>();
    private int sessionIdCounter = 0;

    public HamsTTPServer(final ServerSocket serverSocket) throws IOException {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "off");
        this.serverSocket = serverSocket;
        startListenForSessions(serverSocket);
        this.httpServer = createHttpServer();
        this.httpServer.start(HTTP_SERVER_PORT);
        startLifetimeRefreshTimer();
    }

    public static void startIfNotRunning() {
        try {
            final ServerSocket serverSocket = new ServerSocket(PORT);
            try {
                new HamsTTPServer(serverSocket);
            } catch (IOException e) {
                throw new RuntimeException("failed to start server", e);
            }
        } catch (IOException e) {
            //already running
        }
    }

    private void startListenForSessions(final ServerSocket serverSocket) throws IOException {
        new Thread(() -> {
            try {
                while (!serverSocket.isClosed()) {
                    Socket socket = serverSocket.accept();
                    final HamsterSession session = new HamsterSession(socket, sessionIdCounter);
                    this.sessions.put(sessionIdCounter, session);
                    sessionIdCounter++;
                }
            } catch (IOException e) {
                //TODO shutdown
            }
        }).start();
    }

    private Javalin createHttpServer() {
        return Javalin.create(JavalinConfig::enableCorsForAllOrigins).routes(() -> {
            path("status", () -> {
                get(this::getStatus);
            });
            path("gamesList", () -> {
                get(this::getGamesList);
            });
            path("input", () -> {
                post(this::postInput);
            });
            path("speed", () -> {
                post(this::postSpeed);
            });
            path("action", () -> {
                post(this::postAction);
            });
        }).exception(StatusCodeException.class, (e, context) -> {
            context.result(e.getMessage());
            context.status(e.getStatusCode());
        }).exception(Exception.class, (e, context) -> {
            context.result(e.getMessage());
            context.status(500);
        });
    }

    private void startLifetimeRefreshTimer() {
        System.out.println("lol now start");
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("I want to shut down");
                sessions.values().forEach(session -> {
                    session.shutdownIfPossible();
                    if (!session.isAlive()) {
                        removeSession(session.getId());
                    }
                });
            }
        };

        final Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(task, 0, 3000);
    }


    private void getStatus(final Context context) {
        final HamsterSession session = getSession(context);
        final int since = getIntQueryParam(context, "since");

        Gson gson = new Gson();
        context.result(gson.toJson(session.getStatus(since)));
    }

    private void getGamesList(final Context context) {
        Gson gson = new Gson();
        context.result(gson.toJson(this.sessions.keySet()));
    }


    private void postInput(final Context context) {
        final HamsterSession session = getSession(context);
        final int inputId = getIntQueryParam(context, "inputId");
        final String input = getQueryParam(context, "input");
        if (inputId != session.getInputMessage().getInputId()) {
            throw new StatusCodeException(400, "outdated inputId");
        }

        session.setInputResult(inputId, input);
    }

    private void postAction(final Context context) {
        final HamsterSession session = getSession(context);
        final String action = getQueryParam(context, "action");

        switch (action) {
            case "resume" -> session.resume();
            case "pause" -> session.pause();
            case "undo" -> session.undo();
            case "redo" -> session.redo();
            default -> throw new StatusCodeException(400, "unknown action: " + action);
        }
    }

    private void postSpeed(final Context context) {
        final HamsterSession session = getSession(context);
        final double speed = getDoubleQueryParam(context, "speed");

        if (speed < 0 || speed > 10) {
            throw new StatusCodeException(400, "Provided speed is not in range [0, 10]");
        }
        session.changeSpeed(speed);
    }

    private String getQueryParam(final Context context, final String parameter) {
        final String queryParam = context.queryParam(parameter);
        if (queryParam == null) {
            throw new StatusCodeException(400, "necessary query parameter not provided: " + parameter);
        } else {
            return queryParam;
        }
    }

    private int getIntQueryParam(final Context context, final String parameter) {
        final String queryParam = getQueryParam(context, parameter);
        try {
            return Integer.parseInt(queryParam);
        } catch (NumberFormatException e) {
            throw new StatusCodeException(400, "illegal query parameter format: " + parameter + " was no legal int");
        }
    }

    private double getDoubleQueryParam(final Context context, final String parameter) {
        final String queryParam = getQueryParam(context, parameter);
        try {
            return Double.parseDouble(queryParam);
        } catch (NumberFormatException e) {
            throw new StatusCodeException(400, "illegal query parameter format: " + parameter + " was no legal double");
        }
    }

    private HamsterSession getSession(final Context context) {
        final int sessionId = getIntQueryParam(context, "id");
        if (this.sessions.containsKey(sessionId)) {
            final HamsterSession session = this.sessions.get(sessionId);
            if (session.isAlive()) {
                return session;
            } else {
                removeSession(sessionId);
                throw new StatusCodeException(400, "session no longer available");
            }
        } else {
            throw new StatusCodeException(400, "the provided id did not match any existing session");
        }
    }

    private void removeSession(final int sessionId) {
        sessions.remove(sessionId);
        if (sessions.size() == 0) {
            shutdown();
        }
    }

    private void shutdown() {
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            // ignore
        }
        this.httpServer.stop();
    }

}