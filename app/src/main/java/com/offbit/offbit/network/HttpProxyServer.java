package com.offbit.offbit.network;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class HttpProxyServer extends NanoHTTPD {
    private static final String TAG = "HttpProxyServer";
    private static HttpProxyServer instance;
    private boolean isRunning = false;
    
    public HttpProxyServer(int port) {
        super(port);
    }
    
    public static synchronized HttpProxyServer getInstance(int port) {
        if (instance == null) {
            instance = new HttpProxyServer(port);
        }
        return instance;
    }
    
    public void startProxy() {
        if (!isRunning) {
            try {
                start();
                isRunning = true;
                Log.d(TAG, "HTTP Proxy Server started on port " + getListeningPort());
            } catch (IOException e) {
                Log.e(TAG, "Failed to start HTTP Proxy Server", e);
            }
        }
    }
    
    public void stopProxy() {
        if (isRunning) {
            stop();
            isRunning = false;
            Log.d(TAG, "HTTP Proxy Server stopped");
        }
    }
    
    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Method method = session.getMethod();
        
        Log.d(TAG, "Proxying request: " + method + " " + uri);
        
        try {
            // Handle the request by forwarding it
            return forwardRequest(uri, method, session.getHeaders());
        } catch (Exception e) {
            Log.e(TAG, "Error handling proxy request", e);
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Proxy Error");
        }
    }
    
    private Response forwardRequest(String uri, Method method, Map<String, String> headers) {
        try {
            URL url = new URL(uri.startsWith("http") ? uri : "http://" + uri);
            
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method.toString());
            
            // Copy headers
            for (Map.Entry<String, String> header : headers.entrySet()) {
                // Skip headers that shouldn't be forwarded
                if (!header.getKey().equalsIgnoreCase("host") && 
                    !header.getKey().equalsIgnoreCase("content-length")) {
                    connection.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            
            // Get response
            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();
            
            // Read response body
            java.io.InputStream inputStream = connection.getInputStream();
            byte[] responseBody = readFully(inputStream);
            inputStream.close();
            
            // Create response headers
            Map<String, String> responseHeaders = new HashMap<>();
            for (Map.Entry<String, java.util.List<String>> header : connection.getHeaderFields().entrySet()) {
                if (header.getKey() != null && !header.getValue().isEmpty()) {
                    responseHeaders.put(header.getKey(), header.getValue().get(0));
                }
            }
            
            // Determine MIME type
            String mimeType = connection.getContentType();
            if (mimeType == null) {
                mimeType = MIME_HTML;
            }
            
            Response.Status status = Response.Status.lookup(responseCode);
            if (status == null) {
                status = Response.Status.OK;
            }
            
            Response response = newFixedLengthResponse(status, mimeType, responseBody);
            
            // Add response headers
            for (Map.Entry<String, String> header : responseHeaders.entrySet()) {
                response.addHeader(header.getKey(), header.getValue());
            }
            
            return response;
            
        } catch (Exception e) {
            Log.e(TAG, "Error forwarding request", e);
            return newFixedLengthResponse(Response.Status.BAD_GATEWAY, MIME_PLAINTEXT, "Bad Gateway");
        }
    }
    
    private byte[] readFully(java.io.InputStream inputStream) throws IOException {
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
    
    public String getProxyUrl() {
        return "http://localhost:" + getListeningPort() + "/";
    }
}
