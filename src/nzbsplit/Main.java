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

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import nzbsplit.exception.ParseException;
import nzbsplit.exception.SplitException;
import nzbsplit.nzb.FileElement;
import nzbsplit.nzb.NZB;
import nzbsplit.parser.NZBParser;
import nzbsplit.splitter.NZBSplitter;
import nzbsplit.splitter.NumberSplitter;
import nzbsplit.splitter.SizeSplitter;

/**
 *
 * @author Sam Malone
 */
public class Main {

    private final static String NZB_EXTENSION = ".nzb";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CommandLine cmd;
        try {
            cmd = CommandLine.parse(args);
            if(cmd.isHelpSet()) {
                printHelp();
                return;
            }
            CommandLine.validate(cmd);
            NZBParser parser = new NZBParser();
            NZB nzb = parser.parse(Paths.get(cmd.getNZBFile()));
            NZBSplitter splitter = cmd.isSplitSizeSet() ? new SizeSplitter(nzb, cmd.getMaxSplitSize())
                                                        : new NumberSplitter(nzb, cmd.getSplitNumber());
            List<NZB> splitNZBs = splitter.split();
            for(NZB curNZB : splitNZBs) {
                for(FileElement file : curNZB.getFiles()) {
                    file.sortSegments();
                }
            }
            FileNamer namer = new FileNamer(new File(cmd.getNZBFile()).getName(), NZB_EXTENSION);
            File destDir = new File(".");
            NZBWriter nzbWriter = new NZBWriter(cmd.isVerboseSet());
            for(int i = 0; i < splitNZBs.size(); i++) {
                nzbWriter.write(splitNZBs.get(i), namer.getPartFileName(destDir, i));
            }
        } catch (IOException | SplitException | ParseException ex) {
            System.err.println("ERROR: " + ex.getMessage());
        }
    }
    
    /**
     * Print the help message
     */
    public static void printHelp() {
        System.out.println("Usage:    nzbsplit [-s <MAX_SIZE>|-n <NUM_SPLIT>] [-hv] <NZB_FILE>");
        System.out.println();
        System.out.println("  -h, --help                       Displays this message then exits");
        System.out.println("  -n, --number <NUM_SPLIT>         Split <NZB_FILE> into at most <NUM_SPLIT> NZB parts");
        System.out.println("  -s, --max-size-split <MAX_SIZE>  Split <NZB_FILE> into at most <MAX_SIZE> NZB parts");
        System.out.println("  -v, --verbose                    Prints information about the split NZB files");
    }
    
}
