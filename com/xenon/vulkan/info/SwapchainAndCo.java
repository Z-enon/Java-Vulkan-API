package com.xenon.vulkan.info;

import com.xenon.vulkan.boostrap.*;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.function.IntToLongFunction;

import static com.xenon.vulkan.boostrap.XeUtils.checkVK;
import static org.joml.Math.clamp;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

/**
 * @author Zenon
 */
public abstract class SwapchainAndCo {

    /**
     * Creates a new PipelineInfo object. Either {@link SwapchainAndCoRenderPass} or {@link SwapchainAndCoDynamicRendering}
     * depending on {@link VkSettings#useDynamicRendering}.
     * @param img_format the format
     * @param img_colorSpace the color space
     * @param img_usage the image usage
     * @param present_mode the present mode
     * @param img_count the image count
     * @param bundle the bundle
     * @return a new PipelineInfo object
     */
    public static SwapchainAndCo build(
            int img_format, int img_colorSpace, int img_usage, int present_mode, int img_count, VulkanBundle bundle) {
        if (bundle.settings.useDynamicRendering)
            return new SwapchainAndCoDynamicRendering(
                            img_format,
                            img_colorSpace,
                            img_usage,
                            present_mode,
                            img_count,
                            bundle.swapchainCreateInfo.pipelineCount()
                    );

        assert bundle.renderPassCreation != null;
        return new SwapchainAndCoRenderPass(
                img_format,
                img_colorSpace,
                img_usage,
                present_mode,
                img_count,
                bundle.swapchainCreateInfo.pipelineCount(),
                bundle.renderPassCreation
        );
    }

    public final long[] pipelines, pipelineLayouts;
    public final long[] images, imageViews;
    public final int imgFormat, imgColorSpace, imgUsage, presentMode;
    public long swapchain = VK_NULL_HANDLE;
    public int extentWidth, extentHeight;

    /**
     * init final fields
     */
    protected SwapchainAndCo(int img_format, int img_colorSpace, int img_usage, int present_mode,
                             int img_count, int pipelineCount) {
        imgFormat = img_format;
        imgColorSpace = img_colorSpace;
        imgUsage = img_usage;
        presentMode = present_mode;
        images = new long[img_count];
        imageViews = new long[img_count];
        pipelines = new long[pipelineCount];
        pipelineLayouts = new long[pipelineCount];
    }

    /**
     * abstraction to avoid type cast
     * @return the framebuffers' handle
     */
    public abstract long[] framebuffers();

    /**
     * abstraction to avoid type cast
     * @return the renderpass handle
     */
    public abstract long renderpass();

    /**
     * Pipeline creation. Call this method upon window resize
     * @param physicalDevice the gpu
     * @param device the device
     * @param queues the queues
     * @param callbacks the allocation callbacks
     * @param surface_handle the surface
     * @param width the window's width
     * @param height the window's height
     */
    public void create(VkPhysicalDevice physicalDevice,
                       VkDevice device,
                       XeQueues queues,
                       @Nullable VkAllocationCallbacks callbacks,
                       long surface_handle,
                       final int width, final int height
    ) {
        try (MemoryStack stack = stackPush()) {
            final VkSurfaceCapabilitiesKHR caps = VkSurfaceCapabilitiesKHR.malloc(stack);
            vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice, surface_handle, caps);

            final VkExtent2D extent2D;
            {
                if (caps.currentExtent().width() != -1)
                    extent2D = caps.currentExtent();
                else {
                    extent2D = VkExtent2D.malloc(stack);

                    VkExtent2D minEx = caps.minImageExtent(),
                            maxEx = caps.maxImageExtent();

                    extent2D.width(extentWidth = clamp(minEx.width(), maxEx.width(), width));
                    extent2D.height(extentHeight = clamp(minEx.height(), maxEx.height(), height));
                }
            }

            VkSwapchainCreateInfoKHR swapchainCreateInfo = VkSwapchainCreateInfoKHR.malloc(stack);
            swapchainCreateInfo.sType$Default();
            swapchainCreateInfo.pNext(0);   // calloc stuff
            swapchainCreateInfo.flags(0);   // calloc stuff
            swapchainCreateInfo.surface(surface_handle);
            swapchainCreateInfo.minImageCount(images.length);
            swapchainCreateInfo.imageFormat(imgFormat);
            swapchainCreateInfo.imageColorSpace(imgColorSpace);
            swapchainCreateInfo.imageExtent(extent2D);
            swapchainCreateInfo.imageArrayLayers(1);
            swapchainCreateInfo.imageUsage(imgUsage);

            int[] familyIndices = queues.familyIndices();

            if (familyIndices[0] != familyIndices[1]) {
                swapchainCreateInfo.imageSharingMode(VK_SHARING_MODE_CONCURRENT);
                //createInfo.queueFamilyIndexCount(2);
                swapchainCreateInfo.pQueueFamilyIndices(stack.ints(
                        familyIndices[0],
                        familyIndices[1]
                ));
            } else swapchainCreateInfo.imageSharingMode(VK_SHARING_MODE_EXCLUSIVE);

            swapchainCreateInfo.preTransform(caps.currentTransform());
            swapchainCreateInfo.compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR);
            swapchainCreateInfo.presentMode(presentMode);
            swapchainCreateInfo.clipped(true);
            swapchainCreateInfo.oldSwapchain(swapchain);

            LongBuffer ptr = stack.longs(VK_NULL_HANDLE);
            checkVK(vkCreateSwapchainKHR(device, swapchainCreateInfo, callbacks, ptr), "Failed to create swap chain");

            swapchain = ptr.get(0);

            // hierarchy: swapchain -> images -> image views

            // images
            try (MemoryStack stack1 = stackPush()) {
                IntBuffer countB = stack1.callocInt(1);
                vkGetSwapchainImagesKHR(device, swapchain, countB, null);
                int count = countB.get(0);
                LongBuffer pSwapChainImages = stack1.mallocLong(count);
                vkGetSwapchainImagesKHR(device, swapchain, countB, pSwapChainImages);

                if (count != images.length)
                    throw VkError.impossible();

                for (int i = 0; i < count; i++)
                    images[i] = pSwapChainImages.get(i);
            }

            // image views
            try (MemoryStack stack1 = stackPush()) {
                ptr = stack.longs(VK_NULL_HANDLE);
                VkImageViewCreateInfo imageViewCreateInfo = VkImageViewCreateInfo.calloc(stack1)
                        .sType$Default()
                        .viewType(VK_IMAGE_VIEW_TYPE_2D)
                        .format(imgFormat);
                imageViewCreateInfo.subresourceRange()
                        .aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                        .baseMipLevel(0)
                        .levelCount(1)
                        .baseArrayLayer(0)
                        .layerCount(1);

                for (int i = 0; i < images.length; i++) {
                    imageViewCreateInfo.image(images[i]);
                    checkVK(vkCreateImageView(device, imageViewCreateInfo, callbacks, ptr),
                            "Failed to create image view n°" + i);

                    imageViews[i] = ptr.get(0);
                }
            }
        }
    }

    // swapchain handle is not destroyed in cleanOutOfDate to make use of oldSwapchain field in VkSwapchainCreateInfoKHR

    /**
     * Disposes of the pipeline objects depending on window size, for recreation
     * @param xe the Vulkan manager
     */
    public void cleanOutOfDate(VulkanisedXenon xe) {
        for (long view : imageViews)
            vkDestroyImageView(xe.device(), view, xe.allocationCallbacks());
    }

    /**
     * Disposes of the pipeline objects
     * @param xe the Vulkan manager
     */
    public void dispose(VulkanisedXenon xe) {
        cleanOutOfDate(xe);
        vkDestroySwapchainKHR(xe.device(), swapchain, xe.allocationCallbacks());

        for (long pipeline : pipelines)
            vkDestroyPipeline(xe.device(), pipeline, xe.allocationCallbacks());
        for (long pipelineLayout : pipelineLayouts)
            vkDestroyPipelineLayout(xe.device(), pipelineLayout, xe.allocationCallbacks());
    }



    /**
     * Swapchain&Co for RenderPass implementation
     */
    public static class SwapchainAndCoRenderPass extends SwapchainAndCo {

        private final long[] framebuffers;
        private final long renderpass;  // renderpass do not need to be recreated on resize

        protected SwapchainAndCoRenderPass(int img_format, int img_colorSpace, int img_usage, int present_mode,
                                           int img_count, int pipelineCount, IntToLongFunction renderpassCreation) {
            super(img_format, img_colorSpace, img_usage, present_mode, img_count, pipelineCount);
            framebuffers = new long[img_count];
            renderpass = renderpassCreation.applyAsLong(img_format);
        }

        /**
         * Pipeline creation. Call this method upon window resize
         * @param physicalDevice the gpu
         * @param device the device
         * @param queues the queues
         * @param callbacks the allocation callbacks
         * @param surface_handle the surface
         * @param width the window's width
         * @param height the window's height
         */
        @Override
        public void create(
                VkPhysicalDevice physicalDevice,
                VkDevice device,
                XeQueues queues,
                @Nullable VkAllocationCallbacks callbacks,
                long surface_handle,
                int width, int height
        ) {
            super.create(physicalDevice, device, queues, callbacks, surface_handle, width, height);
            // framebuffers
            try (MemoryStack stack1 = stackPush()) {
                VkFramebufferCreateInfo fbInfo = VkFramebufferCreateInfo.malloc(stack1)
                        .sType$Default()
                        .pNext(0)   // calloc stuff
                        .flags(0)   // calloc stuff
                        .renderPass(renderpass)
                        .width(width)
                        .height(height)
                        .layers(1);

                LongBuffer ptr = stack1.mallocLong(1);
                LongBuffer attachment = stack1.mallocLong(1);

                for (int i = 0; i < framebuffers.length; i++) {
                    attachment.put(0, imageViews[i]);
                    fbInfo.pAttachments(attachment);
                    checkVK(vkCreateFramebuffer(device, fbInfo, callbacks, ptr),
                            "Failed to create framebuffer n°"+i);
                    framebuffers[i] = ptr.get(0);
                }
            }
        }

        @Override
        public final long[] framebuffers() {
            return framebuffers;
        }

        @Override
        public final long renderpass() {
            return renderpass;
        }

        @Override
        public void cleanOutOfDate(VulkanisedXenon xe) {
            for (long framebuffer : framebuffers)
                vkDestroyFramebuffer(xe.device(), framebuffer, xe.allocationCallbacks());
            super.cleanOutOfDate(xe);
        }

        @Override
        public void dispose(VulkanisedXenon xe) {
            super.dispose(xe);
            vkDestroyRenderPass(xe.device(), renderpass, xe.allocationCallbacks()); // not destroy renderpass upon resize
        }
    }


    /**
     * Swapchain&Co for DynamicRendering implementation
     */
    public static class SwapchainAndCoDynamicRendering extends SwapchainAndCo {

        protected SwapchainAndCoDynamicRendering(
                int img_format, int img_colorSpace, int img_usage, int present_mode, int img_count, int pipelineCount) {
            super(img_format, img_colorSpace, img_usage, present_mode, img_count, pipelineCount);
        }

        @Override
        public final long[] framebuffers() {
            throw new UnsupportedOperationException();
        }

        @Override
        public final long renderpass() {
            throw new UnsupportedOperationException();
        }
    }

}




