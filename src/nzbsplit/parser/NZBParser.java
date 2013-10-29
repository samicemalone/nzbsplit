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

package nzbsplit.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import nzbsplit.exception.NZBParseException;
import nzbsplit.nzb.FileElement;
import nzbsplit.nzb.Metadata;
import nzbsplit.nzb.NZB;
import nzbsplit.nzb.SegmentElement;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author Sam Malone
 */
public class NZBParser extends DefaultHandler {
    
    private StringBuilder builder;
    private NZB nzb;
    private Metadata tmpMeta;
    private FileElement tmpFileElement;
    private SegmentElement tmpSegment;
    
    public NZBParser() {
        
    }
    
    /**
     * Parses the NZB file at the given path
     * @param nzbPath Path to the NZB file
     * @return NZB
     * @throws NZBParseException if unable to parse the NZB file
     */
    public NZB parse(Path nzbPath) throws NZBParseException {
        try {
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(this);
            reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            reader.parse(new InputSource(new InputStreamReader(new FileInputStream(nzbPath.toFile()))));
        } catch(IOException | SAXException ex) {
            throw new NZBParseException(ex.getMessage());
        }
        return nzb;
    }
    
    @Override
    public void startDocument() throws SAXException {
        builder = new StringBuilder();
        nzb = new NZB();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        switch(localName) {
            case "meta":
                tmpMeta = new Metadata();
                tmpMeta.setType(atts.getValue("type"));
                break;
            case "file":
                tmpFileElement = new FileElement();
                tmpFileElement.setPoster(atts.getValue("poster"));
                tmpFileElement.setDate(Integer.valueOf(atts.getValue("date")));
                tmpFileElement.setSubject(atts.getValue("subject"));
                break;
            case "segment":
                tmpSegment = new SegmentElement();
                tmpSegment.setBytes(Long.valueOf(atts.getValue("bytes")));
                tmpSegment.setSegmentNumber(Integer.valueOf(atts.getValue("number")));
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch(localName) {
            case "meta":
                tmpMeta.setValue(builder.toString().trim());
                nzb.addMetadata(tmpMeta);
                break;
            case "file":
                nzb.addFile(tmpFileElement);
                break;
            case "group":
                tmpFileElement.addUsenetGroup(builder.toString().trim());
                break;
            case "segment":
                tmpSegment.setMessageId(builder.toString().trim());
                tmpFileElement.addSegment(tmpSegment);
                break;
        }
        builder = new StringBuilder();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        builder.append(ch, start, length);
    }
    
}
