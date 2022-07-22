package com.xenon.vulkan;

/**
 * Consumer of 2 integers
 * @author Zenon
 */
@FunctionalInterface
public interface BiIntConsumer {

    void consume(int x, int y);
}
