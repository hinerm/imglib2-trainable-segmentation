
package clij;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.CLIJ2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

/**
 * {@link CLIJLoopBuilder} provides a simple way to execute pixel wise
 * operations on images using CLIJ.
 */
public class CLIJLoopBuilder {

	private static final List<String> KERNEL_PARAMETER_NAMES = Arrays.asList("a", "b", "c", "d", "e",
		"f", "g");

	private final CLIJ2 clij;

	private final List<String> keys = new ArrayList<>();

	private final List<ClearCLBuffer> images = new ArrayList<>();

	private CLIJLoopBuilder(CLIJ2 clij) {
		this.clij = clij;
	}

	public static CLIJLoopBuilder clij(CLIJ2 clij) {
		return new CLIJLoopBuilder(clij);
	}

	public CLIJLoopBuilder setImage(String key, ClearCLBuffer image) {
		keys.add(key);
		images.add(image);
		return this;
	}

	public void forEachPixel(String operation) {
		int n = images.size();
		if (n > KERNEL_PARAMETER_NAMES.size())
			throw new IllegalArgumentException("Two many images.");
		long[] dims = checkDimensions(keys, images);
		HashMap<String, Object> parameters = new HashMap<>();
		HashMap<String, Object> defines = new HashMap<>();
		StringJoiner parameter_define = new StringJoiner(", ");
		StringJoiner operation_define = new StringJoiner(", ", "OPERATION_FUNCTION(", ")");
		for (int i = 0; i < n; i++) {
			String key = KERNEL_PARAMETER_NAMES.get(i);
			parameter_define.add("IMAGE_" + key + "_TYPE " + key);
			operation_define.add("PIXEL(" + key + ")");
			parameters.put(key, images.get(i));
		}
		defines.put("PARAMETER", parameter_define.toString());
		defines.put("OPERATION", operation_define.toString());
		defines.put("OPERATION_FUNCTION(" + commaSeparated(keys) + ")", operation);
		clij.execute(CLIJLoopBuilderTest.class, "binary_operation.cl", "operation",
			dims, dims, parameters, defines);
	}

	private static StringJoiner commaSeparated(List<String> keys) {
		StringJoiner p = new StringJoiner(",");
		for (String key : keys)
			p.add(key);
		return p;
	}

	private long[] checkDimensions(List<String> keys, List<ClearCLBuffer> images) {
		long[] dims = images.get(0).getDimensions();
		for (ClearCLBuffer image : images) {
			if (!Arrays.equals(dims, image.getDimensions()))
				wrongDimensionsError(keys, images);
		}
		return dims;
	}

	private void wrongDimensionsError(List<String> keys, List<ClearCLBuffer> images) {
		StringJoiner joiner = new StringJoiner(" ");
		for (int i = 0; i < keys.size(); i++) {
			joiner.add("size(" + keys.get(i) + ")=" + Arrays.toString(images.get(i).getDimensions()));
		}
		throw new IllegalArgumentException("Error the sizes of the input images don't match: " +
			joiner);
	}
}
