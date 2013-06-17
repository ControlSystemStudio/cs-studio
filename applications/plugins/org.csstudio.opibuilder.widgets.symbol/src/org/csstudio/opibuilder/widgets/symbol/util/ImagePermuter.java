package org.csstudio.opibuilder.widgets.symbol.util;

import java.util.HashMap;
import java.util.Map;

public final class ImagePermuter {
	
	private static Map<String, PermutationMatrix> ops = initOperations();
			
	private static Map<String, PermutationMatrix> initOperations() {
		Map<String, PermutationMatrix> ops = new HashMap<String, PermutationMatrix>();
		ops.put("1234", PermutationMatrix.generateIdentityMatrix());
		ops.put("4123", PermutationMatrix.generateRotationMatrix(90));
		ops.put("3412", PermutationMatrix.generateRotationMatrix(180));
		ops.put("2341", PermutationMatrix.generateRotationMatrix(-90));
		ops.put("2143", PermutationMatrix.generateFlipHMatrix());
		ops.put("4321", PermutationMatrix.generateFlipVMatrix());
		ops.put("4312", PermutationMatrix.generateFlipHMatrix().multiply(PermutationMatrix.generateFlipVMatrix()));
		ops.put("2413", PermutationMatrix.generateFlipVMatrix().multiply(PermutationMatrix.generateFlipHMatrix()));
		ops.put("2413", PermutationMatrix.generateRotationMatrix(90).multiply(PermutationMatrix.generateFlipHMatrix()));
		ops.put("3214", PermutationMatrix.generateRotationMatrix(90).multiply(PermutationMatrix.generateFlipVMatrix()));
		return ops;
	}
	
	public static double[][] getMatrix(String pos) {
		return ops.get(pos).getMatrix();
	}
	
}
