
package net.imglib2.trainable_segmentation.pixel_feature.filter.stats;

import net.imglib2.trainable_segmentation.pixel_feature.filter.AbstractGroupFeatureOp;
import net.imglib2.trainable_segmentation.pixel_feature.filter.FeatureOp;
import net.imglib2.trainable_segmentation.pixel_feature.filter.SingleFeatures;
import net.imglib2.trainable_segmentation.pixel_feature.settings.FeatureSetting;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;

@Plugin(type = FeatureOp.class, label = "statistic filters (group)")
public class StatisticsFeature extends AbstractGroupFeatureOp {

	@Parameter
	private boolean min = true;

	@Parameter
	private boolean max = true;

	@Parameter
	private boolean mean = true;

	@Parameter
	private boolean variance = true;

	@Override
	protected List<FeatureSetting> initFeatures() {
		return globalSettings().sigmas().stream()
			.map(r -> new FeatureSetting(SingleStatisticsFeature.class, "radius", r,
				"min", min, "max", max, "mean", mean, "variance", variance))
			.collect(Collectors.toList());
	}
}
