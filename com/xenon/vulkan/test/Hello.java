package com.xenon.vulkan.test;

import com.xenon.vulkan.App;
import com.xenon.vulkan.boostrap.*;
import com.xenon.vulkan.info.GPUFeaturesCreateInfo;
import com.xenon.vulkan.info.QueueFeaturesCreateInfo;
import com.xenon.vulkan.info.SwapchainCreateInfo;
import com.xenon.vulkan.structs.VkApplicationInfos;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkApplicationInfo;

import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.vulkan.KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;

/**
 * @author Zenon
 */
public class Hello implements App {
    public static void main(String[] a) {
        new Hello().run();
    }


    VkWindow window;
    VulkanisedXenon xe;


    @Override
    public void init() {
        if (!GLFW.glfwInit())
            throw new RuntimeException();

        window = VkWindow.create(800, 500, "hello");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkApplicationInfo appInfo = VkApplicationInfos.usual(stack, "first",
                    VK_MAKE_VERSION(1, 0, 0),
                    "engine", VK_MAKE_VERSION(1, 0, 0), VK_API_VERSION_1_0);

            VulkanBundle bundle = VulkanBundle.alloc();
            bundle.window = window;
            bundle.appInfo = appInfo;
            bundle.requestedLayers = Set.of("VK_LAYER_KHRONOS_validation");
            bundle.deviceExtensions = Set.of(VK_KHR_SWAPCHAIN_EXTENSION_NAME);
            bundle.gpuFeatures = GPUFeaturesCreateInfo.calloc(stack);
            bundle.queueFeatures = QueueFeaturesCreateInfo.recommended();
            bundle.GPUVendor = "nvidia";
            bundle.GPUName = "GTX 1650";
            bundle.swapchainCreateInfo = SwapchainCreateInfo.usual(
                    VK_FORMAT_B8G8R8A8_SRGB,
                    VK_COLOR_SPACE_SRGB_NONLINEAR_KHR,
                    1
            );
            xe = VulkanisedXenon.light(bundle);
        }
    }

    @Override
    public void loop() {
        while(window.live()) {

            glfwSwapBuffers(window.handle);
            glfwPollEvents();
        }
    }

    @Override
    public void dispose() {
        xe.douse();
        glfwTerminate();
    }
}
