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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import nzbsplit.nzb.FileElement;
import nzbsplit.nzb.Metadata;
import nzbsplit.nzb.NZB;
import nzbsplit.nzb.SegmentElement;

/**
 *
 * @author Sam Malone
 */
public class NZBWriter {
    
    private final static String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    private final static String XML_DOCTYPE = "<!DOCTYPE nzb PUBLIC \"-//newzBin//DTD NZB 1.1//EN\" \"http://www.newzbin.com/DTD/nzb/nzb-1.1.dtd\">\n";
    private final static String START_ROOT = "<nzb xmlns=\"http://www.newzbin.com/DTD/2003/nzb\">\n";
    private final static String END_ROOT = "</nzb>"; 
    
    private boolean log = false;
    
    public NZBWriter(boolean log) {
        this.log = log;
    }
    
    /**
     * Write the given NZB object the the destination File given
     * @param nzb NZB to write
     * @param dest NZB destination path
     * @throws IOException if unable to write to the file
     */
    public void write(NZB nzb, File dest) throws IOException {
        if(log) {
            System.out.println(String.format("Writing \"%s\" containing %d files totalling %s", dest.getName(), nzb.getFiles().size(), FileSize.format(nzb.getTotalFileSize())));
        }
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest), "UTF-8"))) {
            writer.write(XML_DECLARATION);
            writer.write(XML_DOCTYPE);
            writer.write(START_ROOT);
            if(!nzb.getMetadata().isEmpty()) {
                writeMetadata(writer, nzb.getMetadata());
            }
            writeFiles(writer, nzb.getFiles());
            writer.write(END_ROOT);
            writer.flush();
        }
    }
    
    /**
     * Write the list of file elements to the given writer
     * @param writer NZB file writer
     * @param files List of file elements to write
     * @throws IOException if unable to write
     */
    private static void writeFiles(Writer writer, List<FileElement> files) throws IOException {
        for(FileElement file : files) {
            writer.write(String.format("  <file poster=\"%s\" date=\"%d\" subject=\"%s\">\n", escape(file.getPoster()), file.getDate(), escape(file.getSubject())));
            writeGroups(writer, file.getUsenetGroups());
            writeSegments(writer, file.getSegments());
            writer.write("  </file>\n");
        }
    }
    
    /**
     * Write the list of segments elements to the given writer
     * @param writer NZB file writer
     * @param segments List of segment elements
     * @throws IOException if unable to write
     */
    private static void writeSegments(Writer writer, List<SegmentElement> segments) throws IOException {
        writer.write("    <segments>\n");
        for(SegmentElement segment : segments) {
            writer.write(String.format("      <segment number=\"%d\" bytes=\"%d\">%s</segment>\n", segment.getSegmentNumber(), segment.getBytes(), segment.getMessageId()));
        }
        writer.write("    </segments>\n");
    }
    
    /**
     * Write the list of group elements to the given writer
     * @param writer NZB file writer
     * @param groups list of group elements
     * @throws IOException if unable to write
     */
    private static void writeGroups(Writer writer, List<String> groups) throws IOException {
        writer.write("    <groups>\n");
        for(String group : groups) {
            writer.write(String.format("      <group>%s</group>\n", group));
        }
        writer.write("    </groups>\n");
    }

    /**
     * Write the list of metadata to the given writer
     * @param writer NZB file writer
     * @param metadata list of metadata items
     * @throws IOException if unable to write
     */
    private static void writeMetadata(Writer writer, List<Metadata> metadata) throws IOException {
        writer.write("  <head>\n");
        for(Metadata meta : metadata) {
            writer.write(String.format("    <meta type=\"%s\">%s</meta>\n", meta.getType(), meta.getValue()));
        }
        writer.write("  </head>\n");
    }
    
    /**
     * Escape the given string of basic XML entity references
     * @param s String to escape
     * @return escaped string
     */
    private static String escape(String s) {
        return s.replaceAll("&", "&amp;")
                .replaceAll(">", "&gt;")
                .replaceAll("<", "&lt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&apos;");
    }
    
}
