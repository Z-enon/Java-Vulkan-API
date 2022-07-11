package com.xenon.vulkan.boostrap;

import com.xenon.vulkan.Disposable;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkAllocationCallbacks;
import org.lwjgl.vulkan.VkInstance;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

/**
 * @author Zenon
 */
public class VkWindow implements Disposable {

    public static VkWindow create(int width, int height, String name) {
        return new VkWindow(width, height, name);
    }

    protected final long handle;
    protected int width, height;
    protected long surface;
    protected VkAllocationCallbacks allocationCallbacks;

    protected VkWindow(int width, int height, String name) {
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        handle = glfwCreateWindow(width, height, name, 0, 0);
        if (handle == 0)
            throw new RuntimeException("Failed to create GLFW Window.");

        try (MemoryStack stack  = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            glfwGetFramebufferSize(handle, w, h);
            this.width = w.get(0);
            this.height = h.get(0);
        }
    }

    /**
     * Center the window
     */
    public void center(){
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        assert vidMode != null : "VidMode found to be null whilst centering the window "+handle;
        glfwSetWindowPos(
                handle,
                (vidMode.width() - width) / 2,
                (vidMode.height() - height) / 2
        );
    }

    /**
     * Set this window's icon
     * @param iconPath the path of the icon
     */
    public void setIcon(String iconPath){
        try ( MemoryStack stack = stackPush() ){
            IntBuffer w = stack.mallocInt(1);   // png width
            IntBuffer h = stack.mallocInt(1);   // png height
            IntBuffer comp = stack.mallocInt(1);// png components

            // desired_channels is 4 because we want to store Red, Green, Blue and Alpha components
            ByteBuffer icon = stbi_load(iconPath, w, h, comp, 4);
            assert icon != null : "stbi image loaded buffer found to be null whilst loading "+iconPath;

            glfwSetWindowIcon(handle, GLFWImage.malloc(1, stack)
                    .width(w.get(0))
                    .height(h.get(0))
                    .pixels(icon)
            );

            stbi_image_free(icon);
        }
    }

    public static long createSurface(VulkanBundle bundle, VkInstance instance) {
        try (MemoryStack stack = stackPush()) {
            LongBuffer lb = stack.longs(VK_NULL_HANDLE);
            if (glfwCreateWindowSurface(instance, bundle.window.handle, bundle.allocationCallbacks, lb) != VK_SUCCESS)
                throw VkError.log("Failed to create vulkan surface");
            return lb.get(0);
        }
    }


    public boolean live() {
        return !glfwWindowShouldClose(handle);
    }

    /**
     * The window MUST be destroyed before VkInstances is.
     */
    @Override
    public void dispose() {
        glfwDestroyWindow(handle);
    }
}
