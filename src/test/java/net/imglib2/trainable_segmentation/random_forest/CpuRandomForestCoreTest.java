
package net.imglib2.trainable_segmentation.random_forest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import hr.irb.fastRandomForest.FastRandomForest;
import net.imglib2.trainable_segmentation.utils.ArrayUtils;
import org.junit.Test;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import static org.junit.Assert.assertArrayEquals;

/**
 * Tests {@link CpuRandomForestCore} by comparing the results to
 * {@link FastRandomForest} on randomly generated datasets.
 */
public class CpuRandomForestCoreTest {

	private final Random random = new Random(42);

	@Test
	public void testLeafOnlyTree() {
		// Random forest implementations tend to fail for trees that consist
		// of only one leaf. That's why we test it separately.
		TransparentRandomTree tree = TransparentRandomTree.leaf(new double[] { 0.1, 0.9 });
		TransparentRandomForest forest = new TransparentRandomForest(Collections.singletonList(tree));
		CpuRandomForestCore prediction = new CpuRandomForestCore(forest);
		float[] distribution = new float[2];
		prediction.distributionForInstance(new float[1], distribution);
		assertArrayEquals(new float[] { 0.1f, 0.9f }, distribution, 0);
	}

	@Test
	public void testTwoClasses() throws Exception {
		trainAndCompareRandomForests(2);
	}

	@Test
	public void testThreeClasses() throws Exception {
		trainAndCompareRandomForests(3);
	}

	@Test
	public void testFourClasses() throws Exception {
		trainAndCompareRandomForests(4);
	}

	public void trainAndCompareRandomForests(int numberOfClasses) throws Exception {
		// test a random forest with small trees (tree heights roughly 0 and 1)
		trainAndCompareRandomForests(10, numberOfClasses, 4);
		// test a random forest with moderate trees (tree heights roughly 2, 3, 4, 5)
		trainAndCompareRandomForests(10, numberOfClasses, 16);
		// test a random forest with big trees (tree heights roughly 5 to 10 to)
		trainAndCompareRandomForests(10, numberOfClasses, 64);
	}

	/**
	 * Train {@link FastRandomForest} on a randomly generated training dataset.
	 * Apply it on a different randomly generated test dataset and compare the
	 * results to the distributions calculated by {@link CpuRandomForestCore}.
	 */
	private void trainAndCompareRandomForests(int numberOfFeatures, int numberOfClasses,
		int numberOfInstances)
		throws Exception
	{
		Instances trainingDataset = randomDataset(numberOfFeatures, numberOfClasses, numberOfInstances);
		Instances testDataset = randomDataset(numberOfFeatures, numberOfClasses, 100);
		FastRandomForest fastRf = trainFastRandomForest(trainingDataset);
		CpuRandomForestCore cpuRf = new CpuRandomForestCore(fastRf);
		compareRandomForests(testDataset, fastRf, cpuRf);
	}

	private Instances randomDataset(int numberOfFeatures, int numberOfClasses,
		int numInstances)
	{
		Instances dataset = emptyDataset(numberOfFeatures, numberOfClasses);
		for (int i = 0; i < numInstances; i++) {
			double[] values = new double[numberOfFeatures + 1];
			for (int j = 0; j < numberOfFeatures; j++)
				values[j] = random.nextDouble();
			values[numberOfFeatures] = random.nextInt(numberOfClasses);
			dataset.add(new DenseInstance(1.0, values));
		}
		return dataset;
	}

	private FastRandomForest trainFastRandomForest(Instances data)
		throws Exception
	{
		FastRandomForest rf = new FastRandomForest();
		rf.setSeed(2);
		rf.setNumTrees(100);
		rf.buildClassifier(data);
		return rf;
	}

	private void compareRandomForests(Instances data, FastRandomForest fastRf,
		CpuRandomForestCore cpuRf) throws Exception
	{
		for (Instance instance : data) {
			float[] expected = ArrayUtils.toFloats(fastRf.distributionForInstance(instance));
			float[] distribution = new float[cpuRf.numberOfClasses()];
			float[] featureVector = ArrayUtils.toFloats(instance.toDoubleArray());
			cpuRf.distributionForInstance(featureVector, distribution);
			assertArrayEquals(expected, distribution, 1e-6f);
		}
	}

	private Instances emptyDataset(int numberOfFeatures, int numberOfClasses) {
		final ArrayList<Attribute> attInfo = new ArrayList<>();
		for (int j = 0; j < numberOfFeatures; j++)
			attInfo.add(new Attribute("" + j));
		attInfo.add(new Attribute("class", IntStream.rangeClosed(1,
			numberOfClasses).mapToObj(Integer::toString).collect(
				Collectors.toList())));
		Instances dataset = new Instances("", attInfo, 1);
		dataset.setClassIndex(numberOfFeatures);
		return dataset;
	}
}
