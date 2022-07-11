package com.xenon.vulkan.boostrap;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.function.Function;

/**
 * @author Zenon
 */
public class XeUtils {

    /**
     * Creates a PointerBuffer out of a collection of strings, using {@link MemoryStack#UTF8(CharSequence)}.
     * @param stack the stack
     * @param strings the strings
     * @return the resulting PointerBuffer
     */
    public static PointerBuffer pointerbuffer(MemoryStack stack, Collection<String> strings) {
        return pointerbuffer(stack, strings, stack::UTF8);
    }

    /**
     * Generic methods for converting a collection to a PointerBuffer through a mapping function of the generic into
     * ByteBuffer.
     * @param stack the stack
     * @param collection the collection
     * @param map the mapping function
     * @return the resulting PointerBuffer
     * @param <T> generic collection
     */
    public static <T> PointerBuffer pointerbuffer(MemoryStack stack,
                                                  Collection<T> collection,
                                                  Function<? super T, ? extends ByteBuffer> map) {
        PointerBuffer pb = stack.mallocPointer(collection.size());
        collection.stream().map(map).forEach(pb::put);
        return pb.flip();
    }

}
