package net.cibernet.alchemancy.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class GlowingOrbParticle extends TextureSheetParticle
{

	protected GlowingOrbParticle(ClientLevel level, double x, double y, double z) {
		super(level, x, y, z);
		this.gravity = 0.0F;
		this.hasPhysics = false;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_LIT;
	}

	@Override
	public float getQuadSize(float scaleFactor) {
		return 0.5F;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Provider implements ParticleProvider<SimpleParticleType> {

		private final SpriteSet sprite;
		public Provider(SpriteSet sprites) {
			this.sprite = sprites;
		}

		@Nullable
		@Override
		public TextureSheetParticle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
		{
			GlowingOrbParticle particle = new GlowingOrbParticle(level, x, y, z);
			particle.pickSprite(sprite);
			return particle;
		}
	}
}
