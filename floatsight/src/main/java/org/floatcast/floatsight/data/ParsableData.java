/*
 *
 *     FloatSight
 *     Copyright 2018 Thomas Hirsch
 *     https://github.com/84n4n4/FloatSight
 *
 *     This file is part of FloatSight.
 *     FloatSight is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FloatSight is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with FloatSight.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package org.floatcast.floatsight.data;

import android.support.annotation.LongDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@SuppressWarnings("PMD.FieldDeclarationsShouldBeAtStartOfClass")
public abstract class ParsableData {

    @Retention(SOURCE)
    @LongDef({PARSING_SUCCESS, PARSING_FAIL, PARSING_ERRORS})
    public @interface ParsingResult {}
    public static final long PARSING_SUCCESS = 0;
    public static final long PARSING_FAIL = -1;
    public static final long PARSING_ERRORS = 1;

    private long parsingStatus = PARSING_SUCCESS;
    private String sourceFileName = "";
    private boolean dirty = false;

    @ParsingResult
    public long getParsingStatus() {
        return parsingStatus;
    }

    public void setParsingStatus(@ParsingResult long parsingStatus) {
        this.parsingStatus = parsingStatus;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
