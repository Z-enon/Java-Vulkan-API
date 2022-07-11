package com.xenon.vulkan;

/**
 * @author Zenon
 */
public interface Debuggable {

    default void println(Object... os) {
        for (var o : os)
            System.out.println(o);
    }

    default void print(Object... os) {
        var b = new StringBuilder();
        for (var o : os)
            b.append(o).append(',').append(' ');
        b.deleteCharAt(b.length() - 1);
        b.deleteCharAt(b.length() - 1);
        System.out.println(b);
    }
}
