package com.xenon.vulkan.boostrap;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.util.Collection;
import java.util.Set;

import static com.xenon.vulkan.boostrap.XeUtils.checkVK;
import static java.util.stream.Collectors.toSet;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.VK10.*;

/**
 * Simple wrapper for VkInstance.
 * @author Zenon
 */
public final class VkInstances {


    /**
     *
     * @param bundle the bundle
     * @return a new VkInstance
     */
    @Once
    public static VkInstance create(VulkanBundle bundle) {
        Collection<String> layers                          = bundle.requestedLayers;
        VkApplicationInfo appInfo                          = bundle.appInfo;
        VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo = bundle.debugCreateInfo;
        VkAllocationCallbacks callbacks                    = bundle.allocationCallbacks;

        try (MemoryStack stack = stackPush()) {

            // available layers listing
            IntBuffer layerCount = stack.ints(0);
            vkEnumerateInstanceLayerProperties(layerCount, null);

            VkLayerProperties.Buffer availableLayers = VkLayerProperties.malloc(layerCount.get(0), stack);
            vkEnumerateInstanceLayerProperties(layerCount, availableLayers);

            Set<String> availableLayerNames = availableLayers.stream()
                    .map(VkLayerProperties::layerNameString)
                    .collect(toSet());

            System.out.println("Available layers:");
            System.out.println(availableLayerNames);
            // Requested layers validation
            if (!availableLayerNames.containsAll(layers))
                throw VkError.missing(availableLayerNames, layers);



            VkInstanceCreateInfo createInfo = VkInstanceCreateInfo.calloc(stack);
            createInfo.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO);
            createInfo.pApplicationInfo(appInfo);

            // extensions
            PointerBuffer glfwExtensions = glfwGetRequiredInstanceExtensions();
            PointerBuffer ext = glfwExtensions;

            if(VkDebug.ENABLED) {

                assert glfwExtensions != null;
                ext = stack.mallocPointer(glfwExtensions.capacity() + 1);
                ext.put(glfwExtensions).put(stack.UTF8(VK_EXT_DEBUG_UTILS_EXTENSION_NAME)).flip();
            }
            createInfo.ppEnabledExtensionNames(ext);

            // layers
            createInfo.ppEnabledLayerNames(XeUtils.pointerbuffer(stack, layers));
            createInfo.pNext(debugCreateInfo.address());

            // instance creation
            PointerBuffer ptr = stack.mallocPointer(1);

            checkVK(vkCreateInstance(createInfo, callbacks, ptr), "Failed to create a VkInstance");

            return new VkInstance(ptr.get(0), createInfo);
        }
    }


}
