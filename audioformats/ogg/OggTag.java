/*
 * Entagged Audio Tag library
 * Copyright (c) 2003-2005 Raphael Slinckx <raphael@slinckx.net>
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
package entagged.audioformats.ogg;

import entagged.audioformats.generic.AbstractTag;
import entagged.audioformats.generic.TagField;
import entagged.audioformats.ogg.util.OggTagField;

//FIXME: Handle previously handled DESCRIPTION|COMMENT and TRACK|TRACKNUMBER
public class OggTag extends AbstractTag {

    private String vendor = "";
    //This is the vendor string that will be written if no other is supplied
	public static final String DEFAULT_VENDOR = "Entagged - The Musical Box";

    protected TagField createAlbumField(String content) {
        return new OggTagField("ALBUM", content);
    }

    protected TagField createArtistField(String content) {
        return new OggTagField("ARTIST", content);
    }

    protected TagField createCommentField(String content) {
        return new OggTagField("DESCRIPTION", content);
    }

    protected TagField createGenreField(String content) {
        return new OggTagField("GENRE", content);
    }

    protected TagField createTitleField(String content) {
        return new OggTagField("TITLE", content);
    }

    protected TagField createTrackField(String content) {
        return new OggTagField("TRACKNUMBER", content);
    }

    protected TagField createYearField(String content) {
        return new OggTagField("DATE", content);
    }

    protected String getAlbumId() {
        return "ALBUM";
    }

    protected String getArtistId() {
        return "ARTIST";
    }

    protected String getCommentId() {
        return "DESCRIPTION";
    }

    protected String getGenreId() {
        return "GENRE";
    }

    protected String getTitleId() {
        return "TITLE";
    }

    protected String getTrackId() {
        return "TRACKNUMBER";
    }

    public String getVendor() {
		if( !this.vendor.trim().equals("") )
		    return vendor;
    
		return DEFAULT_VENDOR;
    }

    protected String getYearId() {
        return "DATE";
    }

    public void setVendor(String vendor) {
        if(vendor == null) {
            this.vendor = "";
            return;
        }
        this.vendor = vendor;
    }
    
    protected boolean isAllowedEncoding(String enc) {
        return enc.equals("UTF-8");
    }
	
    public String toString() {
        return "OGG " + super.toString();
    }
}

