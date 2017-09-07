package com.mx.lib.samba.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jcifs.smb.SmbFile;

/**
 * An example of subclassing NanoHTTPD to make a custom HTTP server.
 */
public class MyNanoHTTPD extends NanoHTTPD {
    private static final String TAG = MyNanoHTTPD.class.getName();

    public MyNanoHTTPD(int port) {
        super(port);
        init();
    }

    public MyNanoHTTPD(String hostname, int port) {
        super(hostname, port);
        init();
    }

    private ExecutorService executorService;// 连接线程池

    /**
     * Used to initialize and customize the server.
     */
    public void init() {
        executorService = Executors.newFixedThreadPool(20);
        setAsyncRunner(new MyAsyncRunner());
    }

    private class MyAsyncRunner implements AsyncRunner {
        @Override
        public void closeAll() {
        }

        @Override
        public void closed(ClientHandler clientHandler) {
        }

        @Override
        public void exec(ClientHandler code) {
            executorService.execute(code);
        }

    }

    @Override
    public void stop() {
        super.stop();
        executorService.shutdownNow();
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> header = session.getHeaders();
        String uri = session.getUri();
        return serveFile(uri, header, MIME_OBJECT);// (Collections.unmodifiableMap(header),
        // session, uri);
    }

    /**
     * Serves file from homeDir and its' subdirectories (only). Uses only URI,
     * ignores all headers and HTTP parameters.
     */
    private Response serveFile(String uri, Map<String, String> header, String mime) {
        Response res;
        try {
            if (uri.startsWith("/"))
                uri = uri.substring(1);
            SmbFile smbFile = new SmbFile(uri);
            // Calculate etag
            String etag = Integer.toHexString((smbFile.getPath() + smbFile.lastModified() + "" + smbFile.length()).hashCode());

            // Support (simple) skipping:
            long startFrom = 0;
            long endAt = -1;
            String range = header.get("range");
            if (range != null) {
                if (range.startsWith("bytes=")) {
                    range = range.substring("bytes=".length());
                    int minus = range.indexOf('-');
                    try {
                        if (minus > 0) {
                            startFrom = Long.parseLong(range
                                    .substring(0, minus));
                            endAt = Long.parseLong(range.substring(minus + 1));
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            // get if-range header. If present, it must match etag or else we
            // should ignore the range request
            String ifRange = header.get("if-range");
            boolean headerIfRangeMissingOrMatching = (ifRange == null || etag
                    .equals(ifRange));

            String ifNoneMatch = header.get("if-none-match");
            boolean headerIfNoneMatchPresentAndMatching = ifNoneMatch != null
                    && (ifNoneMatch.equals("*") || ifNoneMatch.equals(etag));

            // Change return code and add Content-Range header when skipping is
            // requested
            long fileLen = smbFile.length();

            if (headerIfRangeMissingOrMatching && range != null
                    && startFrom >= 0 && startFrom < fileLen) {
                // range request that matches current etag
                // and the startFrom of the range is satisfiable
                if (headerIfNoneMatchPresentAndMatching) {
                    // range request that matches current etag
                    // and the startFrom of the range is satisfiable
                    // would return range from file
                    // respond with not-modified
                    res = newFixedLengthResponse(Response.Status.NOT_MODIFIED,
                            mime, "");
                    res.addHeader("ETag", etag);
                } else {
                    if (endAt < 0) {
                        endAt = fileLen - 1;
                    }
                    long newLen = endAt - startFrom + 1;
                    if (newLen < 0) {
                        newLen = 0;
                    }

                    InputStream fis = smbFile.getInputStream();
                    fis.skip(startFrom);

                    res = newFixedLengthResponse(
                            Response.Status.PARTIAL_CONTENT, mime, fis, newLen);
                    res.addHeader("Accept-Ranges", "bytes");
                    res.addHeader("Content-Length", "" + newLen);
                    res.addHeader("Content-Range", "bytes " + startFrom + "-"
                            + endAt + "/" + fileLen);
                    res.addHeader("ETag", etag);
                }
            } else {

                if (headerIfRangeMissingOrMatching && range != null
                        && startFrom >= fileLen) {
                    // return the size of the file
                    // 4xx responses are not trumped by if-none-match
                    res = newFixedLengthResponse(
                            Response.Status.RANGE_NOT_SATISFIABLE,
                            NanoHTTPD.MIME_PLAINTEXT, "");
                    res.addHeader("Content-Range", "bytes */" + fileLen);
                    res.addHeader("ETag", etag);
                } else if (range == null && headerIfNoneMatchPresentAndMatching) {
                    // full-file-fetch request
                    // would return entire file
                    // respond with not-modified
                    res = newFixedLengthResponse(Response.Status.NOT_MODIFIED,
                            mime, "");
                    res.addHeader("ETag", etag);
                } else if (!headerIfRangeMissingOrMatching
                        && headerIfNoneMatchPresentAndMatching) {
                    // range request that doesn't match current etag
                    // would return entire (different) file
                    // respond with not-modified

                    res = newFixedLengthResponse(Response.Status.NOT_MODIFIED,
                            mime, "");
                    res.addHeader("ETag", etag);
                } else {
                    // supply the file
                    res = newFixedFileResponse(smbFile, mime);
                    res.addHeader("Content-Length", "" + fileLen);
                    res.addHeader("ETag", etag);
                }
            }
        } catch (IOException ioe) {
            res = getForbiddenResponse("Reading file failed.");
        }

        return res;
    }

    private Response newFixedFileResponse(SmbFile file, String mime)
            throws IOException {
        Response res;
        res = newFixedLengthResponse(Response.Status.OK, mime,
                file.getInputStream(), file.length());
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }

    protected Response getForbiddenResponse(String s) {
        return newFixedLengthResponse(Response.Status.FORBIDDEN,
                NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: " + s);
    }

    public class MyFileInputStream extends FileInputStream {
        File file;

        public MyFileInputStream(String path) throws FileNotFoundException {
            super(path);
            file = new File(path);
        }

        public MyFileInputStream(File file) throws FileNotFoundException {
            super(file);
            this.file = file;
        }

        @Override
        public int available() throws IOException {
            return (int) length();
        }

        public long length() {
            return (file == null && file.exists()) ? file.length() : 0;
        }
    }
}
