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

import java.util.Comparator;
import nzbsplit.nzb.FileElement;

/**
 *
 * @author Sam Malone
 */
public class SizeComparator implements Comparator<FileElement> {
    
    /**
     * Sort file elements in ascending order of size
     */
    public final static int ASCENDING = 0;
    /**
     * Sort file elements in descending order of size
     */
    public final static int DESCENDING = 1;
    
    /**
     * The sort order
     */
    private int order = ASCENDING;
    
    /**
     * Creates a new instance of SizeComparator with the given order
     * @param order Sort Size Order. Either {@link SizeComparator#ASCENDING}
     * or {@link SizeComparator#DESCENDING}
     */
    public SizeComparator(int order) {
        this.order = order;
    }

    @Override
    public int compare(FileElement o1, FileElement o2) {
        return order == ASCENDING ? Long.compare(o1.getFileSize(), o2.getFileSize())
                                  : Long.compare(o2.getFileSize(), o1.getFileSize());
    }
    
}
