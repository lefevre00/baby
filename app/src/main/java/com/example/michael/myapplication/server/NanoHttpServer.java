package com.example.michael.myapplication.server;

import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

@WorkerThread
public class NanoHttpServer extends NanoHTTPD {


    public NanoHttpServer() {
        super(8080);
    }

    public void start() {
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            Log.i(getClass().getSimpleName(), "\nRunning! Point your browsers to http://localhost:8080/ \n");
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Couldn't start server", e);
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        Log.e(getClass().getSimpleName(), "Receive a request : " + session.getUri());
        String msg = "<html><body><h1>Hello from baby</h1>\n";
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username") + "!</p>";
        }
        return newFixedLengthResponse(msg + "</body></html>\n");
    }
}
