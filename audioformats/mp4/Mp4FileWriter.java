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
package entagged.audioformats.mp4;

import java.io.IOException;
import java.io.RandomAccessFile;

import entagged.audioformats.Tag;
import entagged.audioformats.exceptions.CannotWriteException;
import entagged.audioformats.generic.AudioFileWriter;
import entagged.audioformats.mp4.util.Mp4TagWriter;

public class Mp4FileWriter extends AudioFileWriter {
	
    private Mp4TagWriter tw = new Mp4TagWriter();


	protected void writeTag(Tag tag, RandomAccessFile raf, RandomAccessFile rafTemp) throws CannotWriteException, IOException {
	    tw.write(tag, raf, rafTemp); 
	}
	
	protected void deleteTag(RandomAccessFile raf, RandomAccessFile rafTemp) throws IOException {
	    tw.delete(raf, rafTemp);
	}
}
