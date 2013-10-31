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

import nzbsplit.exception.ParseException;

/**
 *
 * @author Sam Malone
 */
public class FileSize {
    
    private final static String[] UNITS = { "K", "M", "G", "T", "P", "E" };
    private final static int UNIT = 1024;
    
    /**
     * Format the given byte count into a human readable String e.g. 1343686458 = "1.25 GB"
     * @param bytes file size in bytes
     * @return Formatted file size to two decimal places
     */
    public static String format(long bytes) {
        if(bytes < UNIT) {
            return String.format("%sB", bytes);
        }
        int exp = (int) (Math.log(bytes) / Math.log(UNIT));
        return String.format("%.2f %sB", bytes / Math.pow(UNIT, exp), UNITS[exp-1]);
    }
    
    /**
     * Get the amount of bytes that is represented by the size and unit in fileSize
     * @param fileSize String describing a file size e.g. "200MB", "1G"
     * @return amount of bytes that is represented by the size and unit in fileSize
     * @throws ParseException if unable to determine the size or unit
     */
    public static long parseBytes(String fileSize) throws ParseException {
        int size = 0;
        int unitStartIndex = findSizeEndIndex(fileSize) + 1;
        try {
            size = Integer.parseInt(fileSize.substring(0, unitStartIndex));
        } catch(NumberFormatException ex) {
            throw new ParseException("Unable to determine the size from " + fileSize);
        }
        if(unitStartIndex == fileSize.length()) {
            throw new ParseException("Unable to determine the unit from " + fileSize);
        }
        String unitValue = fileSize.substring(unitStartIndex).toUpperCase();
        return size * getUnitBytes(unitValue);
    }
    
    /**
     * Get the amount of bytes in the given unit e.g. "KB" = 1024, "GB" = 1048576
     * @param unit Unit with or without B suffix e.g. K, KB, G, GB etc..
     * @return amount of bytes in the given unit
     * @throws ParseException if unable to parse the unit
     */
    private static long getUnitBytes(String unit) throws ParseException {
        for(int i = 0; i < UNITS.length; i++) {
            if(unit.equals(UNITS[i]) || unit.equals(UNITS[i].concat("B"))) {
                return (long) Math.pow(UNIT, i+1);
            }
        }
        throw new ParseException("Unable to parse the unit " + unit);
    }
    
    /**
     * Find the index at the position where the file size ends e.g. "50MB" = 1
     * @param fileSize String describing a file size e.g. "200MB"
     * @return index at the position where the file size ends
     * @throws ParseException if a size cannot be found in the input string
     */
    private static int findSizeEndIndex(String fileSize) throws ParseException {
        final int unitStartIndex = 0;
        for(int i = 0; i < fileSize.length(); i++) {
            if(!Character.isDigit(fileSize.charAt(i))) {
                if(i == unitStartIndex) {
                    throw new ParseException("Unable to determine size from " + fileSize);
                }
                return i - 1;
            }
        }
        return fileSize.length() - 1;
    }
    
}
