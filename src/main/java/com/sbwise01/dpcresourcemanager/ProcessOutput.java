package com.sbwise01.dpcresourcemanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author bwise
 */
public class ProcessOutput {
    private final String stdout;
    private final String stderr;
    private final int returnCode;
    
    public ProcessOutput() {
        this.stdout = "";
        this.stderr = "";
        this.returnCode = 0;
    }
    
    public ProcessOutput(String stdout, String stderr, int returnCode) {
        this.stdout = stdout;
        this.stderr = stderr;
        this.returnCode = returnCode;
    }

    public static ProcessOutput runCommand(String... command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(command);
        Process pr = processBuilder.start();
        
        StringBuilder stdout = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            stdout.append(line)
                    .append("\n");
        }
        
        StringBuilder stderr = new StringBuilder();
        reader = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
        while ((line = reader.readLine()) != null) {
            stderr.append(line)
                    .append("\n");
        }
        int returnCode = pr.waitFor();
        
        return new ProcessOutput(stdout.toString(), stderr.toString(), returnCode);
    }

    /**
     * @return the stdout
     */
    public String getStdout() {
        return stdout;
    }

    /**
     * @return the stderr
     */
    public String getStderr() {
        return stderr;
    }

    /**
     * @return the returnCode
     */
    public int getReturnCode() {
        return returnCode;
    }
}
