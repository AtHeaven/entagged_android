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
package entagged.audioformats.mp4.util;

import entagged.audioformats.generic.Utils;

import java.io.UnsupportedEncodingException;

public class Mp4Box {
    
    private String id;
    private int offset;
        
    public void update(byte[] b) {
        this.offset = Utils.getNumberBigEndian(b, 0, 3);

        try {
            this.id = Utils.getString(b, 4, 4, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            this.id = "";
        }
    }
    
    public String getId() {
        return id;
    }
    
    public int getOffset() {
        return offset;
    }
    
    public String toString() {
        return "Box "+id+":"+offset;
    }
}
