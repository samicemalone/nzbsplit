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

/**
 *
 * @author Sam Malone
 */
public class FileNamer {
    
    private final String fileNameBase;
    private String suffixDivider = "_";
    private final String extension;
    private int zeroPadding = 0;
    
    /**
     * Creates a new instance of FileNamer
     * @param originalFileName File name that should be used to base the file parts on
     * @param extension File extension including periods e.g. ".nzb"
     */
    public FileNamer(String originalFileName, String extension) {
        fileNameBase = originalFileName.substring(0, originalFileName.length() - extension.length());
        this.extension = extension;
    }
    
    /**
     * Get the file name that should be used to store the given part
     * @param destDir Destination directory to store the part
     * @param partNo file part number
     * @return file name that should be used to store the given part
     */
    public File getPartFileName(File destDir, int partNo) {
        return new File(destDir, String.format("%s%s%s%s", fileNameBase, suffixDivider, zeroPad(partNo), extension));
    }
    
    /**
     * Set the length of the each name part to be padded with zeroes if necessary
     * @param length length of each name part to be padded e.g. formatting 23:
     * with maxLength = 2 => 23, maxLength = 3, => 023
     */
    public void setZeroPadding(int length) {
        zeroPadding = length;
    }
    
    /**
     * Sets the divider between each part. Default divider is "_"
     * @param divider Divider between file name and part number
     */
    public void setPartDivider(String divider) {
        suffixDivider = divider;
    }
    
    /**
     * Pad the integer given with zeroes. The amount if zeroes is read from
     * {@link #zeroPadding}
     * @param toPad integer to pad
     * @return Zero padded string if {@link #zeroPadding} > 1. Otherwise no
     * padding is applied and a String representation of toPad is returned.
     */
    private String zeroPad(int toPad) {
        if(zeroPadding > 1) {
            return String.format("%0" + zeroPadding + "d", toPad);
        }
        return String.valueOf(toPad);
    }
    
}
