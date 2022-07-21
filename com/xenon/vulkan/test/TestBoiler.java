package com.xenon.vulkan.test;

import com.xenon.vulkan.App;

import com.xenon.vulkan.Debuggable;
import com.xenon.vulkan.boostrap.*;
import com.xenon.vulkan.info.GPUFeaturesCreateInfo;
import com.xenon.vulkan.info.QueueFeaturesCreateInfo;
import com.xenon.vulkan.info.SwapchainCreateInfo;
import com.xenon.vulkan.structs.VkGraphicsPipelineCreateInfos;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkGraphicsPipelineCreateInfo;
import org.lwjgl.vulkan.VkPipelineLayoutCreateInfo;

import java.nio.LongBuffer;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static com.xenon.vulkan.boostrap.XeUtils.checkVK;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.vulkan.KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR;
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
            bundle.settings = new VkSettings();

            xenon = VulkanisedXenon.light(bundle);

            VkPipelineLayoutCreateInfo ci = VkPipelineLayoutCreateInfo.calloc(stack)
                    .sType$Default();
            LongBuffer pL = stack.mallocLong(1);
            checkVK(vkCreatePipelineLayout(xenon.device(), ci, null, pL), "failed");

            xenon.swapchainAndCo().pipelineLayouts[0] = pL.get(0);


            VkShaderModule[] modules = {VkShaderModule.createShaderModule(
                    Paths.get("./assets/shaders/sv.vert.spv"),
                    VkShaderModule.Stage.VERTEX,
                    xenon.device(),
                    null,
                    0, 0,
                    null
            )};

            VkGraphicsPipelineCreateInfo.Buffer buffer = VkGraphicsPipelineCreateInfo.calloc(1, stack);
            buffer.put(0, VkGraphicsPipelineCreateInfos.usual(
                    stack,
                    modules,
                    VkGraphicsPipelineCreateInfos.vertexInputState(0, null, null),
                    VkGraphicsPipelineCreateInfos.viewportStateDynamic(0, 1, 1),
                    null,
                    pL.get(0),
                    xenon.swapchainAndCo().renderpass(),
                    0,
                    0,
                    0
            ));
            checkVK(vkCreateGraphicsPipelines(xenon.device(), 0, buffer, null, pL), "failed2");

            xenon.swapchainAndCo().pipelines[0] = pL.get(0);


            for (var module : modules)
                module.dispose();
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
