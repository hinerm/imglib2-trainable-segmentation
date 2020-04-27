
package clij;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.CLIJ2;

import java.util.HashMap;

public class ClijRandomForestKernel {

	public static void randomForest(CLIJ2 clij,
		ClearCLBuffer distributions,
		ClearCLBuffer src,
		ClearCLBuffer thresholds,
		ClearCLBuffer probabilities,
		ClearCLBuffer indices,
		int numberOfTrees,
		int numberOfClasses,
		int numberOfFeatures)
	{
		long[] globalSizes = { src.getWidth(), src.getHeight(), src.getDepth() / numberOfFeatures };
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("src", src);
		parameters.put("dst", distributions);
		parameters.put("thresholds", thresholds);
		parameters.put("probabilities", probabilities);
		parameters.put("indices", indices);
		parameters.put("num_trees", numberOfTrees);
		parameters.put("num_classes", numberOfClasses);
		parameters.put("num_features", numberOfFeatures);
		clij.execute(ClijDemo.class, "random_forest.cl", "random_forest", globalSizes, globalSizes,
			parameters);
	}

	public static void findMax(CLIJ2 clij,
		ClearCLBuffer distributions,
		ClearCLBuffer dst,
		int numberOfClasses)
	{
		long[] globalSizes = { dst.getWidth(), dst.getHeight(), dst.getDepth() };
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("dst", dst);
		parameters.put("src", distributions);
		parameters.put("num_classes", numberOfClasses);
		clij.execute(ClijDemo.class, "find_max.cl", "find_max", globalSizes, globalSizes,
			parameters);
	}
}
