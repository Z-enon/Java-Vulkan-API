package com.xenon.vulkan.boostrap;

import java.util.Collection;

/**
 * @author Zenon
 */
public class VkError extends RuntimeException {

    public static VkError blank() {
        return new VkError();
    }

    public static VkError impossible() {
        return log("can't be happening");
    }

    public static VkError log(String msg) {
        return new VkError(msg);
    }

    public static VkError from(Throwable t) {
        return new VkError(t);
    }

    public static VkError from(String msg, Throwable t) {
        return new VkError(msg, t);
    }

    public static <T> VkError missing(Collection<? extends T> available, Collection<T> requested) {
        return new VkError("Some requested elements aren't available. Available: " + available +
                "\n; Requested: " + requested);
    }

    public static VkError format(String msg, Object... args) {
        return new VkError(String.format(msg, args));
    }

    protected VkError() {
        super();
    }

    protected VkError(String msg) {
        super(msg);
    }

    protected VkError(Throwable t) {
        super(t);
    }

    protected VkError(String msg, Throwable t) {
        super(msg, t);
    }

}
