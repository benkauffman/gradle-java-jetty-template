package com.krashidbuilt.api.util;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Created by Ben Kauffman on 1/15/2017.
 */

public class ETagResponseWrapper extends HttpServletResponseWrapper {

    private ServletOutputStream servletOutputStream = null;
    private PrintWriter printWriter = null;

    public ETagResponseWrapper(HttpServletResponse response, OutputStream buffer) {
        super(response);
        servletOutputStream = new ETagOutputStream(buffer);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (printWriter == null) {
            printWriter = new PrintWriter(new OutputStreamWriter(servletOutputStream, "UTF8"), true);
        }
        return printWriter;
    }

    @Override
    public void flushBuffer() throws IOException {
        servletOutputStream.flush();
        if (printWriter != null) {
            printWriter.flush();
        }
    }

}
