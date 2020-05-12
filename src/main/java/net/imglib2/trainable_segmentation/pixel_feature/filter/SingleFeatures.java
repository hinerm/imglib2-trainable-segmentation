
package net.imglib2.trainable_segmentation.pixel_feature.filter;

import net.imglib2.trainable_segmentation.pixel_feature.filter.gradient.SingleGaussianGradientMagnitudeFeature;
import net.imglib2.trainable_segmentation.pixel_feature.filter.identity.IdentityFeature;
import net.imglib2.trainable_segmentation.pixel_feature.filter.dog2.SingleDifferenceOfGaussiansFeature;
import net.imglib2.trainable_segmention.pixel_feature.filter.gabor.SingleGaborFeature;
import net.imglib2.trainable_segmentation.pixel_feature.filter.gauss.SingleGaussianBlurFeature;
import net.imglib2.trainable_segmentation.pixel_feature.filter.hessian.SingleHessianEigenvaluesFeature;
import net.imglib2.trainable_segmentation.pixel_feature.filter.laplacian.SingleLaplacianOfGaussianFeature;
import net.imglib2.trainable_segmention.pixel_feature.filter.lipschitz.SingleLipschitzFeature;
import net.imglib2.trainable_segmentation.pixel_feature.filter.stats.SingleStatisticsFeature;
import net.imglib2.trainable_segmentation.pixel_feature.filter.structure.SingleStructureTensorEigenvaluesFeature;
import net.imglib2.trainable_segmentation.pixel_feature.settings.FeatureSetting;

/**
 * @author Matthias Arzt
 */
public class SingleFeatures {

	public static FeatureSetting identity() {
		return createFeature(IdentityFeature.class);
	}

	@Deprecated
	public static FeatureSetting gabor(double sigma, double gamma, double psi, double frequency,
		int nAngles)
	{
		return gabor(sigma, gamma, psi, frequency, nAngles, false);
	}

	@Deprecated
	public static FeatureSetting legacyGabor(double sigma, double gamma, double psi, double frequency,
		int nAngles)
	{
		return gabor(sigma, gamma, psi, frequency, nAngles, true);
	}

	@Deprecated
	private static FeatureSetting gabor(double sigma, double gamma, double psi, double frequency,
		int nAngles, boolean legacyNormalize)
	{
		return createFeature(SingleGaborFeature.class, "sigma", sigma, "gamma", gamma, "psi", psi,
			"frequency", frequency, "nAngles", nAngles, "legacyNormalize", legacyNormalize);
	}

	public static FeatureSetting gauss(double sigma) {
		return createFeature(SingleGaussianBlurFeature.class, "sigma", sigma);
	}

	public static FeatureSetting gradient(double sigma) {
		return createFeature(SingleGaussianGradientMagnitudeFeature.class, "sigma", sigma);
	}

	@Deprecated
	public static FeatureSetting lipschitz(double slope, long border) {
		return createFeature(SingleLipschitzFeature.class, "slope", slope, "border", border);
	}

	public static FeatureSetting hessian(double sigma) {
		return createFeature(SingleHessianEigenvaluesFeature.class, "sigma", sigma);
	}

	public static FeatureSetting differenceOfGaussians(double sigma1, double sigma2) {
		return createFeature(SingleDifferenceOfGaussiansFeature.class, "sigma1", sigma1, "sigma2",
			sigma2);
	}

	public static FeatureSetting structureTensor(double sigma, double integrationScale) {
		return createFeature(SingleStructureTensorEigenvaluesFeature.class, "sigma", sigma,
			"integrationScale", integrationScale);
	}

	public static FeatureSetting laplacian(double sigma) {
		return createFeature(SingleLaplacianOfGaussianFeature.class, "sigma", sigma);
	}

	public static FeatureSetting statistics(double radius) {
		return statistics(radius, true, true, true, true);
	}

	public static FeatureSetting min(double radius) {
		return statistics(radius, true, false, false, false);
	}

	public static FeatureSetting max(double radius) {
		return statistics(radius, false, true, false, false);
	}

	public static FeatureSetting mean(double radius) {
		return statistics(radius, false, false, true, false);
	}

	public static FeatureSetting variance(double radius) {
		return statistics(radius, false, false, false, true);
	}

	private static FeatureSetting statistics(double radius, boolean min, boolean max, boolean mean,
		boolean variance)
	{
		return createFeature(SingleStatisticsFeature.class, "radius", radius, "min", min, "max", max,
			"mean", mean, "variance", variance);
	}

	private static FeatureSetting createFeature(Class<? extends FeatureOp> aClass, Object... args) {
		return new FeatureSetting(aClass, args);
	}
}
