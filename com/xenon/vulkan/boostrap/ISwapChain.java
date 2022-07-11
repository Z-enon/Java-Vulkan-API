package com.xenon.vulkan.boostrap;

import org.lwjgl.vulkan.VkExtent2D;

import static org.lwjgl.vulkan.KHRSwapchain.vkDestroySwapchainKHR;
import static org.lwjgl.vulkan.VK10.vkDestroyImageView;

/**
 * @author Zenon
 */
public interface ISwapChain {


    /**
     * @return this swap chain's surface image format
     */
    int format();
    /**
     * @return this swap chain's surface image color space
     */
    int colorSpace();
    /**
     * @return this swap chain's surface present mode
     */
    int presentMode();
    /**
     * @return this swap chain's image count
     */
    int imageCount();

    /**
     * @return this swap chain's image usage
     */
    int imageUsage();
    /**
     * @return this swap chain's VkExtent2D
     */
    VkExtent2D extent2D();

    /**
     * @return this swap chain's handle
     */
    long handle();

    /**
     * @return this swap chain's images
     */
    long[] images();

    /**
     * @return this swap chain's image views
     */
    long[] imageViews();

    /**
     * Free this swap chain's VkExtent2D
     */
    @SuppressWarnings("resource")
    default void free() {
        extent2D().free();
    }

    /**
     * Dispose of this swap chain
     * @param xe the vulkan manager
     */
    default void dispose(VulkanisedXenon xe) {
        for (long view : imageViews())
            vkDestroyImageView(xe.device(), view, xe.allocationCallbacks());
        vkDestroySwapchainKHR(xe.device(), handle(), xe.allocationCallbacks());
    }
}
