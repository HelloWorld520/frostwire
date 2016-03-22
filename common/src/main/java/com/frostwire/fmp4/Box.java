/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011-2016, FrostWire(R). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.frostwire.fmp4;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author gubatron
 * @author aldenml
 */
public class Box {

    public static final int uuid = Bits.make4cc("uuid");
    public static final int mdat = Bits.make4cc("mdat");
    public static final int ftyp = Bits.make4cc("ftyp");
    public static final int moov = Bits.make4cc("moov");
    public static final int mvhd = Bits.make4cc("mvhd");
    public static final int iods = Bits.make4cc("iods");
    public static final int trak = Bits.make4cc("trak");
    public static final int tkhd = Bits.make4cc("tkhd");
    public static final int edts = Bits.make4cc("edts");
    public static final int elst = Bits.make4cc("elst");
    public static final int udta = Bits.make4cc("udta");
    public static final int mdia = Bits.make4cc("mdia");

    private static final Map<Integer, BoxLambda> mapping = buildMapping();

    protected int size;
    protected int type;
    protected Long largesize;
    protected byte[] usertype;

    protected LinkedList<Box> boxes;

    Box() {
    }

    void read(InputChannel ch, ByteBuffer buf) throws IOException {
        throw new UnsupportedOperationException();
    }

    void write(OutputChannel ch, ByteBuffer buf) throws IOException {
        throw new UnsupportedOperationException();
    }

    final long length() {
        long n = size - 8;
        if (size == 1) {
            n = largesize - 16;
        } else if (size == 0) {
            n = -1;
        }
        if (type == uuid) {
            n -= 16;
        }

        return n >= 0 ? n : -1;
    }

    @Override
    public String toString() {
        return Bits.make4cc(type);
    }

    static Box empty(int type) throws IOException {
        BoxLambda p = mapping.get(type);
        if (p != null) {
            return p.empty();
        } else {
            return new UnknownBox();
        }
    }

    private static Map<Integer, BoxLambda> buildMapping() {
        Map<Integer, BoxLambda> map = new HashMap<>();

        map.put(mdat, new BoxLambda() {
            @Override
            public Box empty() {
                return new MediaDataBox();
            }
        });
        map.put(ftyp, new BoxLambda() {
            @Override
            public Box empty() {
                return new FileTypeBox();
            }
        });
        map.put(moov, new BoxLambda() {
            @Override
            public Box empty() {
                return new MovieBox();
            }
        });
        map.put(mvhd, new BoxLambda() {
            @Override
            public Box empty() {
                return new MovieHeaderBox();
            }
        });
        map.put(iods, new BoxLambda() {
            @Override
            public Box empty() {
                return new ObjectDescriptorBox();
            }
        });
        map.put(trak, new BoxLambda() {
            @Override
            public Box empty() {
                return new TrackBox();
            }
        });
        map.put(tkhd, new BoxLambda() {
            @Override
            public Box empty() {
                return new TrackHeaderBox();
            }
        });
        map.put(edts, new BoxLambda() {
            @Override
            public Box empty() {
                return new EditBox();
            }
        });
        map.put(elst, new BoxLambda() {
            @Override
            public Box empty() {
                return new EditListBox();
            }
        });
        map.put(udta, new BoxLambda() {
            @Override
            public Box empty() {
                return new UserDataBox();
            }
        });
        map.put(mdia, new BoxLambda() {
            @Override
            public Box empty() {
                return new MediaBox();
            }
        });

        return map;
    }

    private interface BoxLambda {
        Box empty();
    }
}