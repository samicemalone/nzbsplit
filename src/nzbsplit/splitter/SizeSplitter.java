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

package nzbsplit.splitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import nzbsplit.SizeComparator;
import nzbsplit.exception.SplitException;
import nzbsplit.nzb.FileElement;
import nzbsplit.nzb.NZB;

/**
 *
 * @author Sam Malone
 */
public class SizeSplitter implements NZBSplitter {
    
    private final NZB nzb;
    private long splitMaxBytes;
    
    /**
     * Creates a new instance of SizeSplitter
     * @param nzb NZB to split
     * @param splitMaxBytes maximum number of bytes that the NZB should be split into
     */
    public SizeSplitter(NZB nzb, long splitMaxBytes) {
        this.nzb = nzb;
        this.splitMaxBytes = splitMaxBytes;
    }

    /**
     * Set the maximum number of bytes that the NZB should be split into
     * @param splitMaxBytes maximum size in bytes
     */
    public void setSplitMaxBytes(long splitMaxBytes) {
        this.splitMaxBytes = splitMaxBytes;
    }
    
    /**
     * Split the NZB file into smaller NZB parts where each part\'s size is no larger than splitMaxBytes
     * @return List of split NZB parts
     * @throws SplitException if the NZB is smaller is the maximum split size or if any individual
     * file is larger than the maximum split size
     */
    @Override
    public List<NZB> split() throws SplitException {
        if(nzb.getTotalFileSize() < splitMaxBytes) {
            throw new SplitException("The size of the NZB is smaller than the maximum split size.");
        }
        final List<NZB> list = new ArrayList<>();
        final Set<FileElement> files = new TreeSet<>(new SizeComparator(SizeComparator.DESCENDING));
        files.addAll(nzb.getFiles());
        list.add(new NZB(nzb.getMetadata()));
        for(FileElement file : files) {
            if(file.getFileSize() > splitMaxBytes) {
                throw new SplitException(String.format("The file %s is larger than the maximum split size", file.getSubject()));
            }
            int firstFitIndex = getFirstFitIndex(list, file.getFileSize(), splitMaxBytes);
            if(firstFitIndex < 0) {
                list.add(new NZB(nzb.getMetadata()));
                firstFitIndex = list.size() - 1;
            }
            list.get(firstFitIndex).addFile(file);
        }
        return list;
    }
    
    
    /**
     * Gets the index in list that should be used to store the file with the given size.
     * This implementation uses the first fit bin-packing algorithm to provide as many
     * NZB's as necessary whilst keeping a constant max size per file 
     * @param list List of current NZB bins
     * @param curFileSize the file size (in bytes) of the file to store
     * @param capacity The capacity of each bin (nzb) i.e. maximum byte size. This capacity
     * IS strict. Files will not overflow the size maximum NZB size. Additional NZB's will
     * be created instead
     * @return index or -1 if there is not enough space in any NZB
     */
    private int getFirstFitIndex(List<NZB> list, long curFileSize, long capacity) {
        int index = -1;
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).getTotalFileSize() + curFileSize > capacity) {
                continue;
            }
            return i;
        }
        return index;
    }
    
}
