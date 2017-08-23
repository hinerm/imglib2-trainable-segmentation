package net.imglib2.algorithm.features;

import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.features.ops.FeatureOp;
import net.imglib2.type.numeric.real.FloatType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by arzt on 23.08.17.
 */
public class FeatureJoiner {

	private final List<FeatureOp> features;

	private final int count;

	public FeatureJoiner(List<FeatureOp> features) {
		checkGlobalSettings(features);
		this.features = features.stream().flatMap(this::toStream).collect(Collectors.toList());
		this.count = this.features.stream().mapToInt(FeatureOp::count).sum();
	}

	private void checkGlobalSettings(List<FeatureOp> features) {
		if(features.isEmpty())
			return;
		GlobalSettings settings = features.get(0).globalSettings();
		boolean allEqual =
				features.stream().allMatch(f -> settings.equals(f.globalSettings()));
		if(!allEqual)
			throw new IllegalArgumentException("All features in a feature group must use the same global settings");
	}

	private Stream<? extends FeatureOp> toStream(FeatureOp f) {
		return (f instanceof FeatureGroup) ? ((FeatureJoiner) f).features.stream() : Stream.of(f);
	}

	public int count() {
		return count;
	}

	public void apply(RandomAccessible<FloatType> in, List<RandomAccessibleInterval<FloatType>> out) {
		if(out.size() != count)
			throw new IllegalArgumentException();
		int startIndex = 0;
		for(FeatureOp feature : features) {
			int count = feature.count();
			feature.apply(in, out.subList(startIndex, startIndex + count));
			startIndex += count;
		}
	}

	public List<String> attributeLabels() {
		List<String> labels = new ArrayList<>();
		features.stream().map(FeatureOp::attributeLabels).forEach(labels::addAll);
		return labels;
	}

	public List<FeatureOp> features() {
		return Collections.unmodifiableList(features);
	}
}
