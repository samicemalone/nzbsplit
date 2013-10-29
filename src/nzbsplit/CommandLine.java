/*
 * Copyright (c) 2013, Sam Malone
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package nzbsplit;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import nzbsplit.exception.MissingArgumentException;
import nzbsplit.exception.ParseException;

/**
 *
 * @author Sam Malone
 */
public class CommandLine {
    
    private int splitNumber = 0;
    private long splitSize = 0;
    private boolean isHelpSet = false;
    private String nzbFile;
    
    private CommandLine() {
        
    }
    
    /**
     * Validate the CommandLine options. The CommandLine is considered valid if no exceptions are thrown
     * @param cmd CommandLine options
     * @throws ParseException if options/arguments are missing
     * @throws FileNotFoundException if unable to find the NZB file
     */
    public static void validate(CommandLine cmd) throws ParseException, FileNotFoundException {
        if(cmd.nzbFile == null) {
            throw new MissingArgumentException("No NZB file was detected");
        }
        if(cmd.nzbFile.startsWith(CygwinUtil.CYGWIN_PATH)) {
            cmd.nzbFile = CygwinUtil.toWindowsPath(cmd.nzbFile);
        }
        if(!Files.exists(Paths.get(cmd.nzbFile))) {
            throw new FileNotFoundException("Unable to find the NZB file at " + cmd.nzbFile);
        }
        if(!cmd.isSplitNumberSet() && !cmd.isSplitSizeSet()) {
            throw new MissingArgumentException("No split option was defined. Use the --help flag for more information");
        }
    }
    
    /**
     * Parse the program arguments into a CommandLine object. The CommandLine will only validate
     * basic types. For complete validation see {@link #validate(nzbsplit.CommandLine)}.
     * @param args Program arguments
     * @return CommandLine representing the options and arguments given to the program
     * @throws ParseException if unable to parse the arguments given or arguments are missing
     */
    public static CommandLine parse(String[] args) throws ParseException {
        CommandLine cmdLine = new CommandLine();
        for(String arg : args) {
            if(arg.equals("-h") || arg.equals("--help")) {
                cmdLine.isHelpSet = true;
                return cmdLine;
            }
        }
        boolean isArg = false;
        for(int i = 0; i < args.length; i++) {
            if(isArg) {
                isArg = false;
                continue;
            }
            try {
                isArg = parseOption(cmdLine, args, i);
            } catch(IndexOutOfBoundsException ex) {
                throw new MissingArgumentException("Missing argument for option " + args[i]);
            }
        }
        return cmdLine;
    }
    
    /**
     * Parses an option (and its arguments) from the program arguments given at the given index.
     * @param cmd CommandLine to store options and arguments
     * @param args Program arguments
     * @param curIndex index of the current option to parse
     * @return true if the next program argument (args[i+1]) is an argument value, false otherwise
     * @throws ParseException if unable to parse max split size
     */
    private static boolean parseOption(CommandLine cmd, String[] args, int curIndex) throws ParseException {
        switch(args[curIndex]) {
            case "-n":
            case "--number":
                cmd.splitNumber = Integer.valueOf(args[curIndex+1]);
                return true;
            case "-s":
            case "--max-split-size":
                cmd.splitSize = FileSize.parseBytes(args[curIndex+1]);
                return true;
            default:
                cmd.nzbFile = args[curIndex];
        }
        return false;
    }
    
    /**
     * Check if the help flag is set
     * @return true if the help flag is set, false otherwise
     */
    public boolean isHelpSet() {
        return isHelpSet;
    }
    
    /**
     * Check if the number of files to split has been set
     * @return true if the number of files to split has been set, false otherwise
     */
    public boolean isSplitNumberSet() {
        return splitNumber != 0;
    }
    
    /**
     * Check if the max split size is set
     * @return true if max split size is set, false otherwise
     */
    public boolean isSplitSizeSet() {
        return splitSize != 0;
    }
    
    /**
     * Get the number of files to split the NZB into
     * @return number of files to split the NZB into
     */
    public int getSplitNumber() {
        return splitNumber;
    }
    
    /**
     * Get the maximum split size in bytes
     * @return maximum split size in bytes
     */
    public long getMaxSplitSize() {
        return splitSize;
    }

    /**
     * Get the path to the NZB file
     * @return path to the NZB file
     */
    public String getNZBFile() {
        return nzbFile;
    }
    
}
