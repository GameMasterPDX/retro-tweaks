package io.github.poeticrainbow.retrotweaks.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import io.github.poeticrainbow.retrotweaks.mixin.client.required.SkyRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import org.joml.Vector3fc;
import org.joml.Vector4f;

import java.util.Optional;
import java.util.OptionalDouble;

public class BlueVoidRenderer {
    public static void renderBlueVoid(Vector3fc skyColor) {
        var skyRendererAccessor = (SkyRendererAccessor) Minecraft.getInstance().levelRenderer.skyRenderer();

        if (skyRendererAccessor != null) {
            var modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushMatrix();
            modelViewStack.translate(0.0F, -12.0F, 0.0F);

            // transformation to sky color from b1.7.3
            var voidColor = new Vector4f(skyColor.x(), skyColor.y(), skyColor.z(), 1.0F)
                                .mul(0.2F, 0.2F, 0.6F, 1.0F)
                                .add(0.04F, 0.04F, 0.1F, 0.0F);

            var dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(modelViewStack, voidColor);

            GpuTextureView colorTexture = skyRendererAccessor.retrotweaks$getRenderTarget().getColorTextureView();
            GpuTextureView depthTexture = skyRendererAccessor.retrotweaks$getRenderTarget().getDepthTextureView();

            try (var renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "RetroTweaks Blue Void", colorTexture, Optional.empty(), depthTexture, OptionalDouble.empty())) {
                renderPass.setPipeline(RenderSystem.getCompiledPipeline(RenderPipelines.SKY));
                RenderSystem.bindDefaultUniforms(renderPass);
                renderPass.setUniform("DynamicTransforms", dynamicTransforms);
                renderPass.setVertexBuffer(0, skyRendererAccessor.retrotweaks$getBottomSkyBuffer().slice());
                renderPass.draw(10, 1, 0, 0);
            }

            modelViewStack.popMatrix();
        }
    }
}
