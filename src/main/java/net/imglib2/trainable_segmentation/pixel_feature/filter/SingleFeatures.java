
package net.imglib2.trainable_segmentation.pixel_feature.filter;

import net.imglib2.trainable_segmentation.pixel_feature.filter.gradient.SingleGaussianGradientMagnitudeFeature;
import net.imglib2.trainable_segmentation.pixel_feature.filter.identity.IdentityFeature;
import net.imglib2.trainable_segmentation.pixel_feature.filter.dog2.SingleDifferenceOfGaussiansFeature;
import net.imglib2.trainable_segmentation.pixel_feature.filter.stats.SingleMaxFeature;
import net.imglib2.trainable_segmentation.pixel_feature.filter.stats.SingleMeanFeature;
import net.imglib2.trainable_segmentation.pixel_feature.filter.stats.SingleMinFeature;
import net.imglib2.trainable_segmentation.pixel_feature.filter.stats.SingleVarianceFeature;
import net.imglib2.trainable_segmention.pixel_feature.filter.gabor.SingleGaborFeature;
import net.imglib2.trainable_segmentation.pixel_feature.filter.gauss.SingleGaussianBlurFeature;
import net.imglib2.trainable_segmentation.pixel_feature.filter.hessian.SingleHessianEigenvaluesFeature;
import net.imglib2.trainable_segmentation.pixel_feature.filter.laplacian.SingleLaplacianOfGaussianFeature;
import net.imglib2.trainable_segmention.pixel_feature.filter.lipschitz.SingleLipschitzFeature;
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

	public static FeatureSetting min(double radius) {
		return createFeature(SingleMinFeature.class, "radius", radius);
	}

	public static FeatureSetting max(double radius) {
		return createFeature(SingleMaxFeature.class, "radius", radius);
	}

	public static FeatureSetting mean(double radius) {
		return createFeature(SingleMeanFeature.class, "radius", radius);
	}

	public static FeatureSetting variance(double radius) {
		return createFeature(SingleVarianceFeature.class, "radius", radius);
	}

	private static FeatureSetting createFeature(Class<? extends FeatureOp> aClass, Object... args) {
		return new FeatureSetting(aClass, args);
	}
}
