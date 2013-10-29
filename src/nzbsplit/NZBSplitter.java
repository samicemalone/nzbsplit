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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import nzbsplit.exception.SplitException;
import nzbsplit.nzb.FileElement;
import nzbsplit.nzb.NZB;

/**
 *
 * @author Sam Malone
 */
public class NZBSplitter {
    
    private final NZB nzb;
    
    /**
     * Creates a new instance of NZBSplitter
     * @param nzb NZB to split
     */
    public NZBSplitter(NZB nzb) {
        this.nzb = nzb;
    }
    
    /**
     * Split the NZB file into smaller NZB parts where each part\'s size is no larger than splitMaxBytes
     * @param splitMaxBytes Maximum NZB split part file size
     * @return List of split NZB parts
     * @throws SplitException if the NZB is smaller is the maximum split size or if any individual
     * file is larger than the maximum split size
     */
    public List<NZB> splitBySize(long splitMaxBytes) throws SplitException {
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
    
    /**
     * Initialises an array of NZB objects of size numFiles, and fills each array index
     * with a new instance of NZB.
     * @param numFiles number of NZB objects to initialise
     * @return Array of NZB objects of size numFiles with each element initialised
     */
    private NZB[] initEmptyNZBList(int numFiles) {
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
     * @param numFiles number of NZB's to split the original NZB into
     * @return List of split NZB parts of size numFiles
     */
    public List<NZB> splitByNumber(int numFiles) {
        long maxSplitBytes = Math.max(nzb.getTotalFileSize() / numFiles, nzb.getLargestFileSize());
        System.out.println("MAX PER   : " + maxSplitBytes);
        final NZB[] list = initEmptyNZBList(numFiles);
        List<FileElement> sortedFiles = new ArrayList<>(nzb.getFiles());
        Collections.sort(sortedFiles, new SizeComparator(SizeComparator.DESCENDING));
        for(FileElement file : sortedFiles) {
            list[getBestFitIndex(list, file.getFileSize(), maxSplitBytes)].addFile(file);
        }
        return Arrays.asList(list);
    }
    
}
