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

package nzbsplit.nzb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Sam Malone
 */
public class FileElement {
    
    private final List<String> groups;
    private final List<SegmentElement> segments;
    
    private long size = 0;
    private int date;
    private String poster;
    private String subject;
    
    public FileElement() {
        groups = new ArrayList<>();
        segments = new ArrayList<>();
    }
    
    /**
     * Add a segment that makes up this file
     * @param segment SegmentElement
     */
    public void addSegment(SegmentElement segment) {
        segments.add(segment);
        size += segment.getBytes();
    }
    
    /**
     * Add a usenet group that this file resides in e.g. alt.binaries.newzbin
     * @param group Usenet group
     */
    public void addUsenetGroup(String group) {
        groups.add(group);
    }
    
    public List<String> getUsenetGroups() {
        return groups;
    }
    
    /**
     * Gets an unmodifiable list of segments
     * @return 
     */
    public List<SegmentElement> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    /**
     * Get the date as a unix timestamp
     * @return date as a unix timestamp
     */
    public int getDate() {
        return date;
    }

    /**
     * Set the date
     * @param date Unix timestamp
     */
    public void setDate(int date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    /**
     * Get the file size in bytes
     * @return file size in bytes
     */
    public long getFileSize() {
        return size;
    }
    
    /**
     * Sort the segments by segment number
     */
    public void sortSegments() {
        Collections.sort(segments);
    }
    
}
