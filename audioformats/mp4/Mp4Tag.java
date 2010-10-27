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
    protected String getArtistId() {
	    return "ART";
	}
    protected String getAlbumId() {
        return "alb";
    }
    protected String getTitleId() {
        return "nam";
    }
    protected String getTrackId() {
        return "trkn";
    }
    protected String getYearId() {
        return "day";
    }
    protected String getCommentId() {
        return "cmt";
    }
    protected String getGenreId() {
        return "gen";
    }
    
    protected TagField createArtistField(String content) {
        return new Mp4TagTextField("ART", content);
    }
    protected TagField createAlbumField(String content) {
        return new Mp4TagTextField("alb", content);
    }
    protected TagField createTitleField(String content) {
        return new Mp4TagTextField("nam", content);
    }
    protected TagField createTrackField(String content) {
        return new Mp4TagTextNumberField("trkn", content);
    }
    protected TagField createYearField(String content) {
        return new Mp4TagTextField("day", content);
    }
    protected TagField createCommentField(String content) {
        return new Mp4TagTextField("cmt", content);
    }
    protected TagField createGenreField(String content) {
        return new Mp4TagTextField("gen", content);
    }
	
    protected boolean isAllowedEncoding(String enc) {
        return enc.equals("ISO-8859-1");
    }
    
	public String toString() {
		return "Mpeg4 "+super.toString();
	}
}
