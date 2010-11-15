/*
 * Entagged Audio Tag library
 * Copyright (c) 2003-2005 Raphaël Slinckx <raphael@slinckx.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package entagged.audioformats.mp4;

import entagged.audioformats.generic.AbstractTag;
import entagged.audioformats.generic.TagField;
import entagged.audioformats.mp4.util.Mp4TagTextField;
import entagged.audioformats.mp4.util.Mp4TagTextNumberField;

public class Mp4Tag extends AbstractTag {

    private long mMetaPos;
    private long mMetaLength;

    protected String getArtistId() {
	    return "\u00A9ART";
	}
    protected String getAlbumId() {
        return "\u00A9alb";
    }
    protected String getTitleId() {
        return "\u00A9nam";
    }
    protected String getTrackId() {
        return "trkn";
    }
    protected String getYearId() {
        return "\u00A9day";
    }
    protected String getCommentId() {
        return "\u00A9cmt";
    }
    protected String getGenreId() {
        return "\u00A9gen";
    }
    
    protected TagField createArtistField(String content) {
        return new Mp4TagTextField("\u00A9ART", content);
    }
    protected TagField createAlbumField(String content) {
        return new Mp4TagTextField("\u00A9alb", content);
    }
    protected TagField createTitleField(String content) {
        return new Mp4TagTextField("\u00A9nam", content);
    }
    protected TagField createTrackField(String content) {
        return new Mp4TagTextNumberField("trkn", content);
    }
    protected TagField createYearField(String content) {
        return new Mp4TagTextField("\u00A9day", content);
    }
    protected TagField createCommentField(String content) {
        return new Mp4TagTextField("\u00A9cmt", content);
    }
    protected TagField createGenreField(String content) {
        return new Mp4TagTextField("\u00A9gen", content);
    }
	
    protected boolean isAllowedEncoding(String enc) {
        return enc.equals("ISO-8859-1");
    }
    
	public String toString() {
		return "Mpeg4 "+super.toString();
	}

    public void setMetaInfo(long metaPos, long metaLength) {
        mMetaPos = metaPos;
        mMetaLength = metaLength;
    }

    public long getMetaPos() {
        return mMetaPos;
    }

    public long getMetaLength() {
        return mMetaLength;
    }

}
