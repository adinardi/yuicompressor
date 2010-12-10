package com.yahoo.platform.yui.compressor;

import jargs.gnu.CmdLineParser;

import java.io.*;
import java.net.*;

public class CssCompressorService {
    public static void main(String args[]) {
        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option portOpt = parser.addStringOption("port");

        try {
            parser.parse(args);
        } catch (Exception e) {
            System.out.println("Failed Parsing Args: " + e);
        }

        ServerSocket serverSocket = null;
        String portString = (String) parser.getOptionValue(portOpt);
        int port = 8888;
        if (portString != null) {
            try {
                port = Integer.parseInt(portString, 10);
            } catch (NumberFormatException e) {
                System.out.println("PORT FORMAT IS WRONG");
                System.exit(1);
            }
        }

        try {
            serverSocket = new ServerSocket(port);
        } catch (Exception e) {
            System.out.println("SOCKET FAILURE");
        }

        Socket clientSocket = null;

        String inputLine = null;

        String charset = "UTF-8";

        while (true) {
            try {
                clientSocket = serverSocket.accept();

                BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())
                );

                while ((inputLine = in.readLine()) != null) {
                    String[] names = inputLine.split(" ");
                    String inputFilename = names[0];
                    String outputFilename = names[1];

                    Reader fileIn = new InputStreamReader(new FileInputStream(inputFilename), charset);

                    CssCompressor compressor = new CssCompressor(fileIn);

                    fileIn.close(); fileIn = null;

                    Writer out = new OutputStreamWriter(new FileOutputStream(outputFilename), charset);

                    compressor.compress(out, -1);

                    out.close();
                }
            } catch (Exception e) {
                System.out.println("FAIL ACCEPTING CLIENT SOCKET " + e);
            }
        }
    }
}
