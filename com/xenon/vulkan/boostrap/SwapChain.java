package com.xenon.vulkan.boostrap;

import org.lwjgl.vulkan.VkExtent2D;

import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;


/**
 * @author Zenon
 */
public class SwapChain implements ISwapChain{

    public static SwapChain def(int preferredSurfaceFormat, int preferredSurfaceColorSpace, int preferredPresentMode,
                                int additionalImageCount, int imageUsage) {
        return new SwapChain(
                preferredSurfaceFormat,
                preferredSurfaceColorSpace,
                preferredPresentMode,
                additionalImageCount,
                imageUsage
        );
    }

    private int img_format, img_colorSpace, present_mode, img_count;
    /**
     *
     */
    private VkExtent2D extent2D;
    public final int additional_img_count;
    /**
     * One of <code>VkImageUsageFlagBits</code>.<br>
     * In LWJGL, one of the following:
     * <ul>
     *     <li>{@link org.lwjgl.vulkan.VK10#VK_IMAGE_USAGE_TRANSFER_SRC_BIT}</li>
     *     <li>{@link org.lwjgl.vulkan.VK10#VK_IMAGE_USAGE_TRANSFER_DST_BIT}</li>
     *     <li>{@link org.lwjgl.vulkan.VK10#VK_IMAGE_USAGE_SAMPLED_BIT}</li>
     *     <li>{@link org.lwjgl.vulkan.VK10#VK_IMAGE_USAGE_STORAGE_BIT}</li>
     *     <li>{@link org.lwjgl.vulkan.VK10#VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT}</li>
     *     <li>{@link org.lwjgl.vulkan.VK10#VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT}</li>
     *     <li>{@link org.lwjgl.vulkan.VK10#VK_IMAGE_USAGE_TRANSIENT_ATTACHMENT_BIT}</li>
     *     <li>{@link org.lwjgl.vulkan.VK10#VK_IMAGE_USAGE_INPUT_ATTACHMENT_BIT}</li>
     * </ul>
     */
    public final int image_usage;
    public long handle;
    public long[] images, imagesView;

    protected SwapChain(int preferredSurfaceFormat, int preferredSurfaceColorSpace, int preferredPresentMode,
                        int additionalImageCount, int imageUsage) {
        format(preferredSurfaceFormat);
        colorSpace(preferredSurfaceColorSpace);
        presentMode(preferredPresentMode);
        additional_img_count = additionalImageCount;
        image_usage = imageUsage;
    }



    @Override
    public int format() {
        return img_format;
    }

    /**
     * Sets the surface image format
     * @param format the new format
     */
    public void format(int format) {
        img_format = format;
    }


    @Override
    public int colorSpace() {
        return img_colorSpace;
    }

    /**
     * Sets the surface image color space
     * @param colorSpace the new color space
     */
    public void colorSpace(int colorSpace) {
        img_colorSpace = colorSpace;
    }


    @Override
    public int presentMode() {
        return present_mode;
    }

    /**
     * Sets the surface present mode
     * @param mode the new mode
     */
    public void presentMode(int mode) {
        present_mode = mode;
    }


    @Override
    public VkExtent2D extent2D() {
        assert extent2D != null;    // avoid requireNonNull in hot loop
        return extent2D;
    }

    /**
     * Sets the VkExtent2D for this swap chain
     * @param extent the new extent
     */
    public void extent2D(VkExtent2D extent) {
        extent2D = extent;
    }


    @Override
    public int imageCount() {
        return img_count;
    }

    /**
     * Sets this swap chain's image count
     * @param count the new count
     */
    public void imageCount(int count) {
        img_count = count;
    }

    @Override
    public int imageUsage() {
        return image_usage;
    }

    @Override
    public long handle() {
        return handle;
    }

    @Override
    public long[] images() {
        return images;
    }

    @Override
    public long[] imageViews() {
        return imagesView;
    }

    public void sanity() {
        if (
                extent2D == null ||
                images == null || images.length == 0 ||
                imagesView == null || imagesView.length == 0 ||
                handle == VK_NULL_HANDLE
        )
            throw VkError.log("Some fields left null. SwapChain didn't pass the sanity check");
    }

    public ISwapChain immutablecopy() {
        return new Snapshot(
                format(),
                colorSpace(),
                presentMode(),
                imageCount(),
                imageUsage(),
                extent2D(),
                handle(),
                images(),
                imageViews()
        );
    }


    public record Snapshot(
            int format, int colorSpace, int presentMode, int imageCount, int imageUsage,
            VkExtent2D extent2D, long handle, long[] images, long[] imageViews) implements ISwapChain {
    }
}
