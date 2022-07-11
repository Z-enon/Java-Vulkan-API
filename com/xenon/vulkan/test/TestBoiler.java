package com.xenon.vulkan.test;

import com.xenon.vulkan.App;

import com.xenon.vulkan.Debuggable;
import com.xenon.vulkan.boostrap.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkApplicationInfo;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.vulkan.KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_MAILBOX_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;


/**
 * @author Zenon
 */
public class TestBoiler implements App, Debuggable {

    public static void main(String[] a) {
        new TestBoiler().run();
    }

    VulkanisedXenon xenon;

    @Override
    public void init() {
        if (!glfwInit())
            throw new RuntimeException();

        VkWindow w = VkWindow.create(800, 500, "");

        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkApplicationInfo appInfo = VkApplicationInfo.calloc(stack);
            appInfo.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO);
            appInfo.pApplicationName(stack.UTF8Safe("Hello Triangle"));
            appInfo.applicationVersion(VK_MAKE_VERSION(1, 0, 0));
            appInfo.pEngineName(stack.UTF8Safe("No Engine"));
            appInfo.engineVersion(VK_MAKE_VERSION(1, 0, 0));
            appInfo.apiVersion(VK_API_VERSION_1_0);

            VulkanBundle bundle = VulkanBundle.alloc();
            bundle.window = w;
            bundle.appInfo = appInfo;
            Set<String> layers = new HashSet<>();
            layers.add("VK_LAYER_KHRONOS_validation");
            bundle.requestedLayers = layers;
            bundle.deviceExtensions = Set.of(VK_KHR_SWAPCHAIN_EXTENSION_NAME);
            bundle.gpuFeatures = GPUFeatures.calloc(stack);
            bundle.queueFeatures = QueueFeatures.recommended();
            bundle.GPUVendor = "nvidia";
            bundle.GPUName = "GTX 1650";
            bundle.swapChain = SwapChain.def(
                    VK_FORMAT_B8G8R8A8_SRGB,
                    VK_COLOR_SPACE_SRGB_NONLINEAR_KHR,
                    VK_PRESENT_MODE_MAILBOX_KHR,
                    1,
                    VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT
            );

            xenon = VulkanisedXenon.light(bundle);
        }

    }

    @Override
    public void loop() {
        while(xenon.window().live())
            glfwPollEvents();
    }

    @Override
    public void dispose() {
        xenon.douse();
        glfwTerminate();
    }
}
