package com.horizen.box;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.horizen.ScorexEncoding;
import com.horizen.box.data.AbstractNoncedBoxData;
import com.horizen.proposition.Proposition;
import scorex.crypto.hash.Blake2b256;

import java.util.Arrays;
import java.util.Objects;

public abstract class AbstractNoncedBox<P extends Proposition, BD extends AbstractNoncedBoxData<P, B, BD>, B extends AbstractNoncedBox<P, BD, B>>
        extends ScorexEncoding implements NoncedBox<P>
{
    protected final BD boxData;
    protected final long nonce;

    private byte[] id;
    private Integer hashcode;

    public AbstractNoncedBox(BD boxData, long nonce) {
        Objects.requireNonNull(boxData, "boxData must be defined");

        this.boxData = boxData;
        this.nonce = nonce;
    }

    @Override
    public final long value() {
        return boxData.value();
    }

    @Override
    public final P proposition() { return boxData.proposition(); }

    @Override
    public final long nonce() { return nonce; }

    @Override
    public final byte[] id() {
        if(id == null) {
            byte coinsByteFlag = this instanceof CoinsBox ? (byte)1 : (byte)0;
            id = Blake2b256.hash(Bytes.concat(
                    new byte[]{ coinsByteFlag },
                    Longs.toByteArray(value()),
                    proposition().bytes(),
                    Longs.toByteArray(nonce()),
                    boxData.customFieldsHash()));

        }
        return id;
    }

    @Override
    public abstract byte[] bytes();

    @Override
    public int hashCode() {
        if(hashcode == null)
            hashcode = boxData.proposition().hashCode();
        return hashcode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(this.getClass().equals(obj.getClass())))
            return false;
        if (obj == this)
            return true;
        return Arrays.equals(id(), ((AbstractNoncedBox) obj).id())
                && value() == ((AbstractNoncedBox) obj).value();
    }

    @Override
    public String toString() {
        return String.format("%s(id: %s, proposition: %s, value: %d, nonce: %d)", this.getClass().toString(), encoder().encode(id()), proposition(), value(), nonce());
    }
}


