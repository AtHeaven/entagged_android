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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import entagged.audioformats.Tag;
import entagged.audioformats.exceptions.CannotWriteException;
import entagged.audioformats.generic.Utils;
import entagged.audioformats.mp4.Mp4Tag;

import static java.lang.System.arraycopy;


public class Mp4TagWriter { 

    private int mDeltaILST;

    public void write(Tag tag, RandomAccessFile raf, RandomAccessFile rafTemp) throws CannotWriteException, IOException {
        mDeltaILST = 0;
        Mp4Tag mp4Tag = (Mp4Tag)tag;
        if (!writePreMeta(mp4Tag, raf, rafTemp) ||
            !writeMeta(mp4Tag, raf, rafTemp) ||
            !updateILST(raf, rafTemp, mp4Tag.getMetaPos()) ||
            !updateFREE(raf, rafTemp, mp4Tag.getMetaPos()) ||
            !writeRest(mp4Tag, raf, rafTemp))
            throw new IOException("Mp4Write error");
    }

    public void delete(RandomAccessFile raf, RandomAccessFile rafTemp) throws IOException {
        
    }

    private boolean writePreMeta(Mp4Tag tag, RandomAccessFile raf, RandomAccessFile rafTemp) {
        if (tag == null || tag.getMetaPos() == 0 || tag.getMetaLength() == 0)
            return false;
        try {
            raf.seek(0L);
            rafTemp.seek(0);
        } catch (IOException e) {
            return false;
        }
        byte[] b = new byte[(int)tag.getMetaPos()];
        try {
            raf.readFully(b);
            rafTemp.write(b);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private boolean writeMeta(Mp4Tag tag, RandomAccessFile raf, RandomAccessFile rafTemp) {
        if (tag == null || tag.getMetaPos() == 0 || tag.getMetaLength() == 0) {
            byte[] b = new byte[(int)tag.getMetaLength()];
            try {
                raf.readFully(b);
                rafTemp.write(b);
            } catch (IOException e) {
                return false;
            }
            return true;
        }
        Mp4Box box = new Mp4Box();
        int read = 0;
        byte[] bH = new byte[8];
        boolean hasAlbum    = false;
        boolean hasArtist   = false;
        boolean hasGenre    = false;
        boolean hasTitle    = false;
        boolean hasTrackNo  = false;
        boolean hasYear     = false;
        while (read < tag.getMetaLength()) {
            try {
                raf.read(bH);
            } catch (IOException e) {
                return false;
            }
            box.update(bH);
            List l = tag.get(box.getId());
            if (l == null || l.size() == 0) {
                try {
                    byte[] bTmp = new byte[box.getOffset() - 8];
                    raf.read(bTmp);
                    rafTemp.write(bTmp);
                } catch (IOException e) {
                    return false;
                }
                continue;
            }
            int fieldLength = box.getOffset() - 8;
            byte[] b = new byte[fieldLength];
            try {
                raf.read(b);
            } catch (IOException e) {
                return false;
            }
            read += 8+fieldLength;
            if (box.getId().endsWith("nam")) {
                hasTitle = true;
                mDeltaILST -= fieldLength;
                if (!writeTitleBox(rafTemp, tag.getFirstTitle()))
                    return false;
                continue;
            }
            if (box.getId().endsWith("alb")) {
                hasAlbum = true;
                mDeltaILST -= fieldLength;
                if (!writeAlbumBox(rafTemp, tag.getFirstAlbum()))
                    return false;
                continue;
            }
            if (box.getId().endsWith("ART")) {
                hasArtist = true;
                mDeltaILST -= fieldLength;
                if (!writeArtistBox(rafTemp, tag.getFirstArtist()))
                    return false;
                continue;
            }
            if (box.getId().compareTo("trkn") == 0) {
                hasTrackNo = true;
                mDeltaILST -= fieldLength;
                if (!writeTrackNoBox(rafTemp, tag.getFirstTrack()))
                    return false;
                continue;
            }
            if (box.getId().endsWith("day")) {
                hasYear = true;
                mDeltaILST -= fieldLength;
                if (!writeYearBox(rafTemp, tag.getFirstYear()))
                    return false;
                continue;
            }
            // specific gnre handling
            if (box.getId().compareTo("gnre") == 0) {
                hasGenre = true;
                if (writeNumericGenreBox(rafTemp, tag.getFirstGenre())) {
                    mDeltaILST -= fieldLength;
                    continue;
                }
            }
            if (box.getId().endsWith("gen")) {
                hasGenre = true;
                if (writeGenreBox(rafTemp, tag.getFirstGenre())) {
                    mDeltaILST -= fieldLength;
                    continue;
                }
            }
            try {
                rafTemp.write(bH);
                rafTemp.write(b);
            } catch (IOException e) {
                return false;
            }
        }
        if (!hasTitle && tag.getFirstTitle() != null && tag.getFirstTitle().length() > 0)
            if (!appendTitle(rafTemp, tag))
                return false;
        if (!hasAlbum && tag.getFirstAlbum() != null && tag.getFirstAlbum().length() > 0)
            if (!appendAlbum(rafTemp, tag))
                return false;
        if (!hasArtist && tag.getFirstArtist() != null && tag.getFirstArtist().length() > 0)
            if (!appendArtist(rafTemp, tag))
                return false;
        if (!hasYear && tag.getFirstYear() != null && tag.getFirstYear().length() > 0)
            if (!appendYear(rafTemp, tag))
                return false;
        if (!hasTrackNo && tag.getFirstTrack() != null && tag.getFirstTrack().length() > 0)
            if (!appendTrackNo(rafTemp, tag))
                return false;
        if (!hasGenre && tag.getFirstGenre() != null && tag.getFirstGenre().length() > 0)
            if (!appendGenre(rafTemp, tag))
                return false;
        return true;
    }

    private boolean writeRest(Mp4Tag tag, RandomAccessFile raf, RandomAccessFile rafTemp) {
        if (tag == null || tag.getMetaPos() == 0 || tag.getMetaLength() == 0)
            return false;
        byte[] b = new byte[16 * 1024];
        int len;
        while (true) {
            try {
                len = raf.read(b);
            } catch (IOException e) {
                return false;
            }
            if (len == -1)
                break;
            try {
                rafTemp.write(b, 0, len);
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }

    private boolean prepareBoxHeader(byte[] b, int length, String type) {
        if (b.length < 8)
            return false;
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(length);
        arraycopy(buffer.array(), 0, b, 0, 4);
        if (type.length() == 3) {
            b[4] = (byte)0xA9;
            arraycopy(type.getBytes(), 0, b, 5, type.length());
        }
        else
            arraycopy(type.getBytes(), 0, b, 4, type.length());
        return true;
    }

    private boolean prepareBoxTextData(byte[] b, String data) {
        if (b.length < 24 + data.length())
            return false;
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(data.length() + 16);
        arraycopy(buffer.array(), 0, b, 8, 4);
        arraycopy("data".getBytes(), 0, b, 12, 4);
        byte[] stub = new byte[8];
        stub[3] = 1;
        arraycopy(stub, 0, b, 16, 8);
        arraycopy(data.getBytes(), 0, b, 24, data.length());
        return true;
    }

    private boolean prepareBoxNumericData(byte[] b, String data) {
        if (b.length != 32)
            return false;
        b[11] = 0x18;
        arraycopy("data".getBytes(), 0, b, 12, 4);
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(Integer.valueOf(data));
        arraycopy(buffer.array(), 0, b, 24, 4);
        return true;
    }

    private boolean writeTitleBox(RandomAccessFile raf, String value) {
        int boxLen = 24 + value.length();
        mDeltaILST += boxLen - 8;
        byte[] b = new byte[boxLen];
        if (prepareBoxHeader(b, boxLen, "nam") && prepareBoxTextData(b, value))
            try {
                raf.write(b);
                return true;
            } catch (IOException e) {
                return false;
            }
        return false;
    }

    private boolean writeAlbumBox(RandomAccessFile raf, String value) {
        int boxLen = 24 + value.length();
        mDeltaILST += boxLen - 8;
        byte[] b = new byte[boxLen];
        if (prepareBoxHeader(b, boxLen, "alb") && prepareBoxTextData(b, value))
            try {
                raf.write(b);
                return true;
            } catch (IOException e) {
                return false;
            }
        return false;
    }

    private boolean writeArtistBox(RandomAccessFile raf, String value) {
        int boxLen = 24 + value.length();
        mDeltaILST += boxLen - 8;
        byte[] b = new byte[boxLen];
        if (prepareBoxHeader(b, boxLen, "ART") && prepareBoxTextData(b, value))
            try {
                raf.write(b);
                return true;
            } catch (IOException e) {
                return false;
            }
        return false;
    }

    private boolean writeGenreBox(RandomAccessFile raf, String value) {
        int boxLen = 24 + value.length();
        mDeltaILST += boxLen - 8;
        byte[] b = new byte[boxLen];
        if (prepareBoxHeader(b, boxLen, "gen") && prepareBoxTextData(b, value))
            try {
                raf.write(b);
                return true;
            } catch (IOException e) {
                return false;
            }
        return false;
    }

    private boolean writeTrackNoBox(RandomAccessFile raf, String value) {
        int boxLen = 32;
        mDeltaILST += boxLen - 8;
        byte[] b = new byte[boxLen];
        if (prepareBoxHeader(b, boxLen, "trkn") && prepareBoxNumericData(b, value))
            try {
                raf.write(b);
                return true;
            } catch (IOException e) {
                return false;
            }
        return false;
    }

    private boolean writeYearBox(RandomAccessFile raf, String value) {
        int boxLen = 24 + value.length();
        mDeltaILST += boxLen - 8;
        byte[] b = new byte[boxLen];
        if (prepareBoxHeader(b, boxLen, "day") && prepareBoxTextData(b, value))
            try {
                raf.write(b);
                return true;
            } catch (IOException e) {
                return false;
            }
        return false;
    }

    private boolean writeNumericGenreBox(RandomAccessFile raf, String value) {
        int genreId = getGenreIdByName(value);
        if (genreId == -1)
            return false;
        int boxLen = 26;
        mDeltaILST += boxLen - 8;
        byte[] b = new byte[boxLen];
        if (prepareBoxHeader(b, boxLen, "gnre"))
            try {
                b[11] = 0x12;
                arraycopy("data".getBytes(), 0, b, 12, 4);
                b[b.length - 1] = (byte)genreId;
                raf.write(b);
                return true;
            } catch (IOException e) {
                return false;
            }
        return false;
    }

    private boolean updateILST(RandomAccessFile raf, RandomAccessFile rafTemp, long pos) {
        try {
            raf.seek(pos - 8);
            byte[] b = new byte[4];
            raf.read(b);
            ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.order(ByteOrder.BIG_ENDIAN);
            buffer.putInt(Utils.getNumberBigEndian(b, 0, 3) + mDeltaILST);
            rafTemp.seek(pos - 8);
            rafTemp.write(buffer.array());
            rafTemp.seek(rafTemp.length());
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private boolean updateFREE(RandomAccessFile raf, RandomAccessFile rafTemp, long pos) {
        try {
            raf.seek(0);
            byte[] bH = new byte[8];
            raf.read(bH);
            Mp4Box box = new Mp4Box();
            box.update(bH);
            while (!box.getId().equals("moov")) {
                raf.read(bH);
                box.update(bH);
            }
            long moovPos = raf.getFilePointer() - 8;
            long moovBound = moovPos + box.getOffset();
            raf.seek(pos - 8);
            byte[] b = new byte[4];
            raf.read(b);
            raf.seek(pos - 8 + Utils.getNumberBigEndian(b, 0, 3));
            raf.read(bH);
            box.update(bH);
            while (!box.getId().equals("free")) {
                byte[] bData = new byte[box.getOffset()-8];
                raf.read(bData);
                rafTemp.write(bH);
                rafTemp.write(bData);
                raf.read(bH);
                box.update(bH);
            }
            if (raf.getFilePointer() - 8 >= moovBound) {
                raf.skipBytes(box.getOffset() - 8);
                return createInternalFREE(rafTemp, moovPos, moovBound - moovPos, box);
            }
            raf.skipBytes(box.getOffset() - 8);
            ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.order(ByteOrder.BIG_ENDIAN);
            int zerosLen = box.getOffset() - 8 - mDeltaILST;
            // is internal FREE too small?
            if (zerosLen <= 0)
                return false;
            buffer.putInt(zerosLen + 8);
            rafTemp.write(buffer.array());
            rafTemp.write("free".getBytes());
            rafTemp.write(new byte[zerosLen]);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private int getGenreIdByName(String genre) {
        for (int i = 0; i < Tag.DEFAULT_GENRES.length; ++i) {
            if (genre.compareToIgnoreCase(Tag.DEFAULT_GENRES[i]) == 0)
                return i;
        }
        return -1;
    }

    private boolean createInternalFREE(RandomAccessFile raf, long moovPos, long moovSize, Mp4Box freeBox) throws IOException {
        if (freeBox.getOffset() < mDeltaILST)
            return false;
        // create a "free" box inside "moov"
        long filePos = raf.getFilePointer();
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        int freeLen = freeBox.getOffset() - mDeltaILST;
        int free1Len = freeLen * 2 / 3;
        buffer.putInt(free1Len);
        raf.write(buffer.array());
        raf.write("free".getBytes());
        raf.write(new byte[freeLen - 8]);
        raf.seek(filePos + free1Len);
        buffer.clear();
        buffer.putInt(freeLen - free1Len);
        raf.write(buffer.array());
        raf.write("free".getBytes());
        // update "moov"; new size = "free" size + mDeltaILST
        raf.seek(moovPos);
        buffer.clear();
        buffer.putInt((int)moovSize + free1Len + mDeltaILST);
        raf.write(buffer.array());
        // update "moov"; new size = "free" size + mDeltaILST
        raf.seek(moovPos);
        buffer.clear();
        buffer.putInt((int)moovSize + free1Len + mDeltaILST);
        raf.write(buffer.array());
        // update "udta"; new size = "free" size + mDeltaILST
        raf.seek(moovPos + 8);
        byte[] bH = new byte[8];
        raf.read(bH);
        Mp4Box box = new Mp4Box();
        box.update(bH);
        while (!box.getId().equals("udta")) {
            raf.skipBytes(box.getOffset() - 8);
            raf.read(bH);
            box.update(bH);
        }
        raf.seek(raf.getFilePointer() - 8);
        buffer.clear();
        buffer.putInt(box.getOffset() + free1Len + mDeltaILST);
        raf.write(buffer.array());
        raf.skipBytes(4);
        // update "meta"; new size = "free" size + mDeltaILST
        raf.read(bH);
        box.update(bH);
        while (!box.getId().equals("meta")) {
            raf.skipBytes(box.getOffset() - 8);
            raf.read(bH);
            box.update(bH);
        }
        raf.seek(raf.getFilePointer() - 8);
        buffer.clear();
        buffer.putInt(box.getOffset() + free1Len + mDeltaILST);
        raf.write(buffer.array());
        raf.seek(raf.length());
        return true;
    }

    private boolean appendAlbum(RandomAccessFile raf, Mp4Tag tag) {
        mDeltaILST += 8;
        return writeAlbumBox(raf, tag.getFirstAlbum());
    }

    private boolean appendArtist(RandomAccessFile raf, Mp4Tag tag) {
        mDeltaILST += 8;
        return writeArtistBox(raf, tag.getFirstArtist());
    }

    private boolean appendGenre(RandomAccessFile raf, Mp4Tag tag) {
        mDeltaILST += 8;
        return writeGenreBox(raf, tag.getFirstGenre());
    }

    private boolean appendTitle(RandomAccessFile raf, Mp4Tag tag) {
        mDeltaILST += 8;
        return writeTitleBox(raf, tag.getFirstTitle());
    }

    private boolean appendTrackNo(RandomAccessFile raf, Mp4Tag tag) {
        mDeltaILST += 8;
        return writeTrackNoBox(raf, tag.getFirstTrack());
    }

    private boolean appendYear(RandomAccessFile raf, Mp4Tag tag) {
        mDeltaILST += 8;
        return writeYearBox(raf, tag.getFirstYear());
    }

}
