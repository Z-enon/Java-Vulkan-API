package com.xenon.vulkan.boostrap;


import com.xenon.vulkan.Disposable;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.xenon.vulkan.boostrap.XeUtils.checkVK;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

/**
 * @author Zenon
 */
public record VkShaderModule(
        Stage stage,
        long handle,
        VkDevice device,
        VkAllocationCallbacks callbacks,
        long pNextShaderSubGroup,
        int flags,
        @Nullable VkSpecializationInfo specs
)
        implements Disposable {

    /**
     * Creates a shader module from a SPIR-V-compiled shader file.
     * @param shader a path to the SPIR-V file
     * @param stage the shader stage
     * @param device the device representing the gpu
     * @param callbacks the allocation callbacks
     * @param pNextShaderSubGroup a pointer to a {@link VkPipelineShaderStageRequiredSubgroupSizeCreateInfo}
     * @param flags VkPipelineShaderStageCreateFlags
     * @param specs {@link VkSpecializationInfo}
     * @return a new Shader module object
     */
    public static VkShaderModule createShaderModule(
            Path shader,
            Stage stage,
            VkDevice device,
            VkAllocationCallbacks callbacks,
            long pNextShaderSubGroup,
            int flags,
            @Nullable VkSpecializationInfo specs
    ) {
        try (MemoryStack stack = stackPush()) {
            ByteBuffer code;

            try (SeekableByteChannel bytes = Files.newByteChannel(shader)) {
                code = stack.malloc((int) bytes.size());
                bytes.read(code);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read a SPIR-V file", e);
            }
            return createShaderModule(stack, code, stage, device, callbacks, pNextShaderSubGroup, flags, specs);
        }
    }

    /**
     * Creates a shader module from shader byte code.
     * @param stack the stack
     * @param spirVByteCode the shader byte code
     * @param stage the shader stage
     * @param device the device representing the gpu
     * @param callbacks the allocation callbacks
     * @param pNextShaderSubGroup a pointer to a {@link VkPipelineShaderStageRequiredSubgroupSizeCreateInfo}
     * @param flags VkPipelineShaderStageCreateFlags
     * @param specs {@link VkSpecializationInfo}
     * @return a new Shader module object
     */
    public static VkShaderModule createShaderModule(
            MemoryStack stack,
            ByteBuffer spirVByteCode,
            Stage stage,
            VkDevice device,
            VkAllocationCallbacks callbacks,
            long pNextShaderSubGroup,
            int flags,
            @Nullable VkSpecializationInfo specs
    ) {
        VkShaderModuleCreateInfo createInfo = VkShaderModuleCreateInfo.calloc(stack)
                .sType$Default()
                .pCode(spirVByteCode.flip());

        LongBuffer ptr = stack.mallocLong(1);

        checkVK(vkCreateShaderModule(device, createInfo, callbacks, ptr), "Failed to create shader module");
        return new VkShaderModule(stage, ptr.get(0), device, callbacks, pNextShaderSubGroup, flags, specs);
    }

    /**
     * Fills the given container with this shader module's createInfo.
     * The container can be calloc or malloc, since all the fields are being written to.
     * @param stack the stack
     * @param container the container
     */
    public void fillCreateInfo(MemoryStack stack, VkPipelineShaderStageCreateInfo.Buffer container) {
        container.sType$Default()
                .pNext(pNextShaderSubGroup)
                .flags(flags)
                .stage(stage.code)
                .module(handle)
                .pName(stack.UTF8("main"))
                .pSpecializationInfo(specs);
    }

    @Override
    public void dispose() {
        vkDestroyShaderModule(device, handle, callbacks);
    }


    /**
     * Represents the different shader stages
     */
    public enum Stage {
        VERTEX(VK_SHADER_STAGE_VERTEX_BIT),
        TESS_CTRL(VK_SHADER_STAGE_TESSELLATION_CONTROL_BIT),
        TESS_EVAL(VK_SHADER_STAGE_TESSELLATION_EVALUATION_BIT),
        GEOMETRY(VK_SHADER_STAGE_GEOMETRY_BIT),
        FRAGMENT(VK_SHADER_STAGE_FRAGMENT_BIT),
        COMPUTE(VK_SHADER_STAGE_COMPUTE_BIT);

        public final int code;
        Stage(int x) {
            code = x;
        }
    }

}
