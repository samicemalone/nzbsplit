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
public class NZB {
    
    private final List<Metadata> meta;
    private final List<FileElement> files;
    private long totalSize = 0;
    
    /**
     * Creates an empty NZB instance
     */
    public NZB() {
        this(new ArrayList<Metadata>());
    }
    
    /**
     * Creates an NZB instance with the given metadata
     * @param metadata List of metadata
     */
    public NZB(List<Metadata> metadata) {
        this(metadata, new ArrayList<FileElement>());
    }
    
    /**
     * Creates an NZB instance with the given metadata and files
     * @param metadata List of metadata
     * @param files List of FileElements
     */
    public NZB(List<Metadata> metadata, List<FileElement> files) {
        this.meta = metadata;
        this.files = files;
    }
    
    /**
     * Add a piece of metadata
     * @param metadata metadata
     */
    public void addMetadata(Metadata metadata) {
        meta.add(metadata);
    }
    
    /**
     * Adds a file to the NZB
     * @param file FileElement
     */
    public void addFile(FileElement file) {
        files.add(file);
        totalSize += file.getFileSize();
    }

    /**
     * Get list of metadata
     * @return list of metadata or empty list
     */
    public List<Metadata> getMetadata() {
        return meta;
    }

    /**
     * Gets an unmodifiable list of the files that make up the NZB
     * @return unmodifiable list of the files that make up the NZB
     */
    public List<FileElement> getFiles() {
        return Collections.unmodifiableList(files);
    }
    
    /**
     * Get the total file size (in bytes) of the files that make up the NZB
     * @return total file size (in bytes)
     */
    public long getTotalFileSize() {
        return totalSize;
    }
    
    /**
     * Get the largest file size (in bytes) of the files that make up the NZB
     * @return largest file size (in bytes)
     */
    public long getLargestFileSize() {
        long maxSize = 0;
        for(FileElement file : files) {
            if(file.getFileSize() > maxSize) {
                maxSize = file.getFileSize();
            }
        }
        return maxSize;
    }

    /**
     * Checks whether the NZB contains any files
     * @return true if NZB is empty (no files), false otherwise
     */
    public boolean isEmpty() {
        return files.isEmpty();
    }
    
}
