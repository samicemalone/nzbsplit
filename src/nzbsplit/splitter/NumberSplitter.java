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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import nzbsplit.SizeComparator;
import nzbsplit.exception.SplitException;
import nzbsplit.nzb.FileElement;
import nzbsplit.nzb.NZB;

/**
 *
 * @author Sam Malone
 */
public class NumberSplitter implements NZBSplitter {
    
    private final NZB nzb;
    private int numFiles;

    /**
     * Creates a new instance of NumberSplitter
     * @param nzb NZB to split
     * @param numFiles number of files to split the NZB into
     */
    public NumberSplitter(NZB nzb, int numFiles) {
        this.nzb = nzb;
        this.numFiles = numFiles;
    }

    /**
     * Set the number of files to split the NZB into
     * @param numFiles number of files to split the NZB into
     */
    public void setNumFiles(int numFiles) {
        this.numFiles = numFiles;
    }
    
    /**
     * Initialises an array of NZB objects of size numFiles, and fills each array index
     * with a new instance of NZB.
     * @return Array of NZB objects of size numFiles with each element initialised
     */
    private NZB[] initEmptyNZBList() {
        final NZB[] list = new NZB[numFiles];
        for(int i = 0; i < list.length; i++) {
            list[i] = new NZB(nzb.getMetadata());
        }
        return list;
    }
    
    /**
     * Gets the index of the maximum value in the array
     * @param array array
     * @return index of the maximum value in the array
     */
    private int getIndexOfMaxValue(long[] array) {
        long max = Long.MIN_VALUE;
        int index = -1;
        for(int i = 0; i < array.length; i++) {
            if(array[i] >= max) {
                max = array[i];
                index = i;
            }
        }
        return index;
    }
    
    /**
     * Gets the index in list that should be used to store the file with the given size.
     * This implementation uses the best fit bin-packing algorithm with modifications to
     * account for the amount of bins (nzb\'s) being finite, so the capacity can "overflow".
     * An overflow bin is chosen with the most amount of remaining space.
     * @param list Array of the NZBs that the split composes of
     * @param curFileSize the file size (in bytes) of the file to store
     * @param capacity The capacity of each bin (nzb) i.e. maximum byte size. This capacity
     * is not strict. This is because differing file sizes make it less likely to fill each
     * bin. This may result in files having to be added to (near) full bins. This overflow
     * allows the amount of bins to stay constant.
    * @return the index in list that should be used to store the file with the given size.
     */
    private int getBestFitIndex(NZB[] list, long curFileSize, long capacity) {
        final long[] remaining = new long[list.length];
        long leastRemaining = Long.MAX_VALUE;
        int leastRemainingIndex = -1;
        for(int i = 0; i < list.length; i++) {
            remaining[i] = capacity - list[i].getTotalFileSize() - curFileSize;
            if(remaining[i] >= 0 && remaining[i] < leastRemaining) {
                leastRemaining = remaining[i];
                leastRemainingIndex = i;
            }
        }
        if(leastRemainingIndex < 0) {
            return getIndexOfMaxValue(remaining);
        }
        return leastRemainingIndex;
    }
    
    /**
     * Split the NZB file into a number of NZB parts given by numFiles
     * @return List of split NZB parts of size numFiles
     * @throws SplitException will not be thrown in this implementation
     */
    @Override
    public List<NZB> split() throws SplitException {
        long maxSplitBytes = Math.max(nzb.getTotalFileSize() / numFiles, nzb.getLargestFileSize());
        final NZB[] list = initEmptyNZBList();
        List<FileElement> sortedFiles = new ArrayList<>(nzb.getFiles());
        Collections.sort(sortedFiles, new SizeComparator(SizeComparator.DESCENDING));
        for(FileElement file : sortedFiles) {
            list[getBestFitIndex(list, file.getFileSize(), maxSplitBytes)].addFile(file);
        }
        return Arrays.asList(list);
    }
    
}
