package com.xenon.vulkan.boostrap;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Hardcoded all available features listed in {@link VkPhysicalDeviceFeatures}.
 * Java reflection could have prevented writing 55 different functions, but I don't know anymore whether it's allowed
 * by the JVM.
 * @author Zenon
 */
public class GPUFeatures {


    /**
     * Calloc a new GPUFeatures with its underlying VkPhysicalDeviceFeatures object on the stack.
     * GPUFeatures created using this method mustn't be freed manually, i.e. don't ever call {@link #free()}.
     * @param stack the stack
     * @return a new GPUFeatures object
     */
    public static GPUFeatures calloc(MemoryStack stack) {
        return new GPUFeatures(VkPhysicalDeviceFeatures.calloc(stack));
    }

    /**
     * Calloc a new GPUFeatures with its underlying VkPhysicalDeviceFeatures object.
     * GPUFeatures created using this method must be freed manually, by calling {@link #free()}.
     * @return a new GPUFeatures object
     */
    public static GPUFeatures calloc() {
        return new GPUFeatures(VkPhysicalDeviceFeatures.calloc());
    }

    /*All due checks to validate a VkPhysicalDevice*/
    protected final List<Function<VkPhysicalDeviceFeatures, Boolean>> checks = new ArrayList<>();
    /*underlying device features, used when creating a logical device*/
    protected final VkPhysicalDeviceFeatures underlyingFeatures;


    /**
     * Allows custom allocation for the features
     * @param features the GPU features
     * @see #calloc()
     * @see #calloc(MemoryStack)
     */
    protected GPUFeatures(VkPhysicalDeviceFeatures features) {
        underlyingFeatures = features;
    }

    /**
     * @return the underlying VkPhysicalDeviceFeatures object
     */
    public VkPhysicalDeviceFeatures underlying() {
        return underlyingFeatures;
    }

    /**
     * Validate the selected device features according to the application needs (set with the methods in this class).
     * @param features the selected device features
     */
    public void validateDeviceFeatures(VkPhysicalDeviceFeatures features) {
        for (Function<VkPhysicalDeviceFeatures, Boolean> f : checks)
            if (!f.apply(features))
                throw new AssertionError("Selected GPU didn't fulfill all the requirements.");
    }

    /**
     * Free the underlying VkPhysicalDeviceFeatures object
     */
    public void free() {
        underlyingFeatures.free();
    }


    public void require_robustBufferAccess() {
        underlyingFeatures.robustBufferAccess(true);
        checks.add(VkPhysicalDeviceFeatures::robustBufferAccess);
    }
    public void require_fullDrawIndexUint32() {
        underlyingFeatures.fullDrawIndexUint32(true);
        checks.add(VkPhysicalDeviceFeatures::fullDrawIndexUint32);
    }
    public void require_imageCubeArray() {
        underlyingFeatures.imageCubeArray(true);
        checks.add(VkPhysicalDeviceFeatures::imageCubeArray);
    }
    public void require_independentBlend() {
        underlyingFeatures.independentBlend(true);
        checks.add(VkPhysicalDeviceFeatures::independentBlend);
    }
    public void require_geometryShader() {
        underlyingFeatures.geometryShader(true);
        checks.add(VkPhysicalDeviceFeatures::geometryShader);
    }
    public void require_tessellationShader() {
        underlyingFeatures.tessellationShader(true);
        checks.add(VkPhysicalDeviceFeatures::tessellationShader);
    }
    public void require_sampleRateShading() {
        underlyingFeatures.sampleRateShading(true);
        checks.add(VkPhysicalDeviceFeatures::sampleRateShading);
    }
    public void require_dualSrcBlend() {
        underlyingFeatures.dualSrcBlend(true);
        checks.add(VkPhysicalDeviceFeatures::dualSrcBlend);
    }
    public void require_logicOp() {
        underlyingFeatures.logicOp(true);
        checks.add(VkPhysicalDeviceFeatures::logicOp);
    }
    public void require_multiDrawIndirect() {
        underlyingFeatures.multiDrawIndirect(true);
        checks.add(VkPhysicalDeviceFeatures::multiDrawIndirect);
    }
    public void require_drawIndirectFirstInstance() {
        underlyingFeatures.drawIndirectFirstInstance(true);
        checks.add(VkPhysicalDeviceFeatures::drawIndirectFirstInstance);
    }
    public void require_depthClamp() {
        underlyingFeatures.depthClamp(true);
        checks.add(VkPhysicalDeviceFeatures::depthClamp);
    }
    public void require_depthBiasClamp() {
        underlyingFeatures.depthBiasClamp(true);
        checks.add(VkPhysicalDeviceFeatures::depthBiasClamp);
    }
    public void require_fillModeNonSolid() {
        underlyingFeatures.fillModeNonSolid(true);
        checks.add(VkPhysicalDeviceFeatures::fillModeNonSolid);
    }
    public void require_depthBounds() {
        underlyingFeatures.depthBounds(true);
        checks.add(VkPhysicalDeviceFeatures::depthBounds);
    }
    public void require_wideLines() {
        underlyingFeatures.wideLines(true);
        checks.add(VkPhysicalDeviceFeatures::wideLines);
    }
    public void require_largePoints() {
        underlyingFeatures.largePoints(true);
        checks.add(VkPhysicalDeviceFeatures::largePoints);
    }
    public void require_alphaToOne() {
        underlyingFeatures.alphaToOne(true);
        checks.add(VkPhysicalDeviceFeatures::alphaToOne);
    }
    public void require_multiViewport() {
        underlyingFeatures.multiViewport(true);
        checks.add(VkPhysicalDeviceFeatures::multiViewport);
    }
    public void require_samplerAnisotropy() {
        underlyingFeatures.samplerAnisotropy(true);
        checks.add(VkPhysicalDeviceFeatures::samplerAnisotropy);
    }
    public void require_textureCompressionETC2() {
        underlyingFeatures.textureCompressionETC2(true);
        checks.add(VkPhysicalDeviceFeatures::textureCompressionETC2);
    }
    public void require_textureCompressionASTC_LDR() {
        underlyingFeatures.textureCompressionASTC_LDR(true);
        checks.add(VkPhysicalDeviceFeatures::textureCompressionASTC_LDR);
    }
    public void require_textureCompressionBC() {
        underlyingFeatures.textureCompressionBC(true);
        checks.add(VkPhysicalDeviceFeatures::textureCompressionBC);
    }
    public void require_occlusionQueryPrecise() {
        underlyingFeatures.occlusionQueryPrecise(true);
        checks.add(VkPhysicalDeviceFeatures::occlusionQueryPrecise);
    }
    public void require_pipelineStatisticsQuery() {
        underlyingFeatures.pipelineStatisticsQuery(true);
        checks.add(VkPhysicalDeviceFeatures::pipelineStatisticsQuery);
    }
    public void require_vertexPipelineStoresAndAtomics() {
        underlyingFeatures.vertexPipelineStoresAndAtomics(true);
        checks.add(VkPhysicalDeviceFeatures::vertexPipelineStoresAndAtomics);
    }
    public void require_fragmentStoresAndAtomics() {
        underlyingFeatures.fragmentStoresAndAtomics(true);
        checks.add(VkPhysicalDeviceFeatures::fragmentStoresAndAtomics);
    }
    public void require_shaderTessellationAndGeometryPointSize() {
        underlyingFeatures.shaderTessellationAndGeometryPointSize(true);
        checks.add(VkPhysicalDeviceFeatures::shaderTessellationAndGeometryPointSize);
    }
    public void require_shaderImageGatherExtended() {
        underlyingFeatures.shaderImageGatherExtended(true);
        checks.add(VkPhysicalDeviceFeatures::shaderImageGatherExtended);
    }
    public void require_shaderStorageImageExtendedFormats() {
        underlyingFeatures.shaderStorageImageExtendedFormats(true);
        checks.add(VkPhysicalDeviceFeatures::shaderStorageImageExtendedFormats);
    }
    public void require_shaderStorageImageMultisample() {
        underlyingFeatures.shaderStorageImageMultisample(true);
        checks.add(VkPhysicalDeviceFeatures::shaderStorageImageMultisample);
    }
    public void require_shaderStorageImageReadWithoutFormat() {
        underlyingFeatures.shaderStorageImageReadWithoutFormat(true);
        checks.add(VkPhysicalDeviceFeatures::shaderStorageImageReadWithoutFormat);
    }
    public void require_shaderStorageImageWriteWithoutFormat() {
        underlyingFeatures.shaderStorageImageWriteWithoutFormat(true);
        checks.add(VkPhysicalDeviceFeatures::shaderStorageImageWriteWithoutFormat);
    }
    public void require_shaderUniformBufferArrayDynamicIndexing() {
        underlyingFeatures.shaderUniformBufferArrayDynamicIndexing(true);
        checks.add(VkPhysicalDeviceFeatures::shaderUniformBufferArrayDynamicIndexing);
    }
    public void require_shaderSampledImageArrayDynamicIndexing() {
        underlyingFeatures.shaderSampledImageArrayDynamicIndexing(true);
        checks.add(VkPhysicalDeviceFeatures::shaderSampledImageArrayDynamicIndexing);
    }
    public void require_shaderStorageBufferArrayDynamicIndexing() {
        underlyingFeatures.shaderStorageBufferArrayDynamicIndexing(true);
        checks.add(VkPhysicalDeviceFeatures::shaderStorageBufferArrayDynamicIndexing);
    }
    public void require_shaderStorageImageArrayDynamicIndexing() {
        underlyingFeatures.shaderStorageImageArrayDynamicIndexing(true);
        checks.add(VkPhysicalDeviceFeatures::shaderStorageImageArrayDynamicIndexing);
    }
    public void require_shaderClipDistance() {
        underlyingFeatures.shaderClipDistance(true);
        checks.add(VkPhysicalDeviceFeatures::shaderClipDistance);
    }
    public void require_shaderCullDistance() {
        underlyingFeatures.shaderCullDistance(true);
        checks.add(VkPhysicalDeviceFeatures::shaderCullDistance);
    }
    public void require_shaderFloat64() {
        underlyingFeatures.shaderFloat64(true);
        checks.add(VkPhysicalDeviceFeatures::shaderFloat64);
    }
    public void require_shaderInt64() {
        underlyingFeatures.shaderInt64(true);
        checks.add(VkPhysicalDeviceFeatures::shaderInt64);
    }
    public void require_shaderInt16() {
        underlyingFeatures.shaderInt16(true);
        checks.add(VkPhysicalDeviceFeatures::shaderInt16);
    }
    public void require_shaderResourceResidency() {
        underlyingFeatures.shaderResourceResidency(true);
        checks.add(VkPhysicalDeviceFeatures::shaderResourceResidency);
    }
    public void require_shaderResourceMinLod() {
        underlyingFeatures.shaderResourceMinLod(true);
        checks.add(VkPhysicalDeviceFeatures::shaderResourceMinLod);
    }
    public void require_sparseBinding() {
        underlyingFeatures.sparseBinding(true);
        checks.add(VkPhysicalDeviceFeatures::sparseBinding);
    }
    public void require_sparseResidencyBuffer() {
        underlyingFeatures.sparseResidencyBuffer(true);
        checks.add(VkPhysicalDeviceFeatures::sparseResidencyBuffer);
    }
    public void require_sparseResidencyImage2D() {
        underlyingFeatures.sparseResidencyImage2D(true);
        checks.add(VkPhysicalDeviceFeatures::sparseResidencyImage2D);
    }
    public void require_sparseResidencyImage3D() {
        underlyingFeatures.sparseResidencyImage3D(true);
        checks.add(VkPhysicalDeviceFeatures::sparseResidencyImage3D);
    }
    public void require_sparseResidency2Samples() {
        underlyingFeatures.sparseResidency2Samples(true);
        checks.add(VkPhysicalDeviceFeatures::sparseResidency2Samples);
    }
    public void require_sparseResidency4Samples() {
        underlyingFeatures.sparseResidency4Samples(true);
        checks.add(VkPhysicalDeviceFeatures::sparseResidency4Samples);
    }
    public void require_sparseResidency8Samples() {
        underlyingFeatures.sparseResidency8Samples(true);
        checks.add(VkPhysicalDeviceFeatures::sparseResidency8Samples);
    }
    public void require_sparseResidency16Samples() {
        underlyingFeatures.sparseResidency16Samples(true);
        checks.add(VkPhysicalDeviceFeatures::sparseResidency16Samples);
    }
    public void require_sparseResidencyAliased() {
        underlyingFeatures.sparseResidencyAliased(true);
        checks.add(VkPhysicalDeviceFeatures::sparseResidencyAliased);
    }
    public void require_variableMultisampleRate() {
        underlyingFeatures.variableMultisampleRate(true);
        checks.add(VkPhysicalDeviceFeatures::variableMultisampleRate);
    }
    public void require_inheritedQueries() {
        underlyingFeatures.inheritedQueries(true);
        checks.add(VkPhysicalDeviceFeatures::inheritedQueries);
    }
}
