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

/**
 * This class is used to represent a Segment of an NZB file.
 * The comparable interface is implemented to order the segments by
 * segment number ascending.
 * @author Sam Malone
 */
public class SegmentElement implements Comparable<SegmentElement> {
    
    private int segmentNumber;
    private long bytes;
    private String messageId;
    
    /**
     * Creates an empty segment
     */
    public SegmentElement() {
        
    }

    /**
     * Gets the Message Id that contains the location of the usenet message
     * e.g. 123456789abcdef@news.newzbin.com
     * @return Message Id
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Get the segment size in bytes
     * @return segment size in bytes
     */
    public long getBytes() {
        return bytes;
    }

    /**
     * Get the segment number
     * @return segment number
     */
    public int getSegmentNumber() {
        return segmentNumber;
    }

    /**
     * Set the segment number
     * @param segmentNumber segment number
     */
    public void setSegmentNumber(int segmentNumber) {
        this.segmentNumber = segmentNumber;
    }

    /**
     * Set the segment size in bytes
     * @param bytes segment size in bytes
     */
    public void setBytes(long bytes) {
        this.bytes = bytes;
    }

    /**
     * Set the Message Id
     * @param messageId Message Id e.g. 123456789abcdef@news.newzbin.com
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public int compareTo(SegmentElement o) {
        return Integer.compare(segmentNumber, o.getSegmentNumber());
    }
    
}
