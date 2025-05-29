package net.cibernet.alchemancy.util;

import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class ColorUtils
{
	public static int interpolateColorsOverTime(float timePerColor, int... colors)
	{
		float progress = ((System.currentTimeMillis() % ((long) (timePerColor * 1000) * (long) colors.length)) / 1000f) / timePerColor;
		return FastColor.ARGB32.lerp(progress % 1f, colors[(int)progress % colors.length], colors[((int)progress + 1) % colors.length]);
	}

	public static int flashColorsOverTime(double time, int... colors)
	{
		return colors[(int) Math.abs((System.currentTimeMillis() / time) % colors.length)];
	}

	public static int sineColorsOverTime(float time, int colorA, int colorB)
	{
		float partialSecond = ((System.currentTimeMillis() % (1000L * (long) time)) / 1000f);
		return FastColor.ARGB32.lerp(Mth.sin((Mth.DEG_TO_RAD * 360 * (partialSecond / time))) * 0.5f + 0.5f, colorA, colorB);
	}

	public static String colorToHexString(int color) {
		return String.format("%06X", color).toUpperCase();
	}
}
