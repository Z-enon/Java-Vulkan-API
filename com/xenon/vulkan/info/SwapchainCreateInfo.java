package com.xenon.vulkan.info;

import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_MAILBOX_KHR;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT;

/**
 * @author Zenon
 */
public record SwapchainCreateInfo(int preferredSurfaceFormat, int preferredSurfaceColorSpace, int preferredPresentMode,
                                  int additionalImageCount, int imageUsage, int pipelineCount) {

    public static SwapchainCreateInfo usual(int preferredSurfaceFormat, int preferredSurfaceColorSpace, int pipelineCount) {
        return new SwapchainCreateInfo(
                preferredSurfaceFormat,
                preferredSurfaceColorSpace,
                VK_PRESENT_MODE_MAILBOX_KHR,
                1,
                VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT,
                pipelineCount
        );
    }

}
