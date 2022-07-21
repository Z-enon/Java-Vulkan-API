package com.xenon.vulkan.boostrap;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.function.Function;

import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

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

    // cuda style checks

    /**
     * Checks whether the function succeeded.
     * Literally
     * <code>if (func_result != VK_SUCCESS) throw RuntimeException</code>
     * @param func_result the function exit code
     * @param log the log to print
     */
    public static void checkVK(int func_result, String log) {
        if (func_result != VK_SUCCESS)
            throw VkError.log(log);
    }

    /**
     * Checks whether the pointer is valid.
     * Literally
     * <code>if (ptr == NULL) throw RuntimeException</code>
     * @param ptr the pointer
     * @param log the log to print
     */
    public static void checkPtr(long ptr, String log) {
        if (ptr == NULL)
            throw VkError.log(log);
    }

    /**
     * Checks whether the count is 0.
     * Literally
     * <code>if (count == 0) throw RuntimeException</code>
     * @param count the count
     * @param log the log to print
     */
    public static void checkCount(int count, String log) {
        if (count == 0)
            throw VkError.log(log);
    }
}
