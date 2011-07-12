/*
 * Copyright 2011 Peter Abeles
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gecv.alg.filter.convolve;

import gecv.core.image.GeneralizedImageOps;
import gecv.core.image.border.ImageBorder;
import gecv.core.image.border.ImageBorderValue;
import gecv.struct.convolve.Kernel1D_F32;
import gecv.struct.convolve.Kernel1D_I32;
import gecv.struct.image.ImageBase;
import gecv.testing.CompareEquivalentFunctions;
import gecv.testing.GecvTesting;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Random;

/**
 * @author Peter Abeles
 */
public class TestConvolveImageBox extends CompareEquivalentFunctions {


	Random rand = new Random(0xFF);

	static int width = 10;
	static int height = 12;
	static int kernelRadius = 2;

	public TestConvolveImageBox() {
		super(ConvolveImageBox.class, ConvolveWithBorder.class);
	}

	@Test
	public void compareToStandard() {
		performTests(8);
	}

	@Override
	protected boolean isTestMethod(Method m) {
		Class<?> params[] = m.getParameterTypes();

		if( params.length != 4)
			return false;

		return ImageBase.class.isAssignableFrom(params[0]);
	}

	@Override
	protected boolean isEquivalent(Method candidate, Method validation) {

		Class<?> v[] = candidate.getParameterTypes();
		Class<?> c[] = validation.getParameterTypes();

		if( v.length != 4 )
			return false;

		if( !candidate.getName().equals(validation.getName()))
			return false;

		return c[0] == v[1] && c[1] == v[2];
	}

	@Override
	protected Object[][] createInputParam(Method candidate, Method validation) {

		Class<?> c[] = candidate.getParameterTypes();

		ImageBase input = GecvTesting.createImage(c[0],width,height);
		ImageBase output = GecvTesting.createImage(c[1],width,height);

		GeneralizedImageOps.randomize(input,rand,0,20);

		Object[][] ret = new Object[2][];
		ret[0] = new Object[]{input,output,kernelRadius,true};
		ret[1] = new Object[]{input,output,kernelRadius,false};

		return ret;
	}

	@Override
	protected Object[] reformatForValidation(Method m, Object[] targetParam) {
		Class<?> params[] = m.getParameterTypes();
		Object kernel = TestConvolveImageBox.createTableKernel(params[0],kernelRadius,rand);

		ImageBase output = ((ImageBase)targetParam[1]).clone();

		ImageBorder border = output.isInteger() ? new ImageBorderValue.Value_I(0) : new ImageBorderValue.Value_F32(0);

		return new Object[]{kernel,targetParam[0],output,border };
	}

	@Override
	protected void compareResults(Object targetResult, Object[] targetParam, Object validationResult, Object[] validationParam) {
		ImageBase expected = (ImageBase)validationParam[2];
		ImageBase found = (ImageBase)targetParam[1];

		GecvTesting.assertEqualsGeneric(expected,found,0,1e-4);
	}

	public static Object createTableKernel(Class<?> kernelType, int kernelRadius, Random rand) {
		Object kernel;
		if (Kernel1D_F32.class == kernelType) {
			kernel = KernelFactory.table1D_F32(kernelRadius,false);
		} else if (Kernel1D_I32.class == kernelType) {
			kernel = KernelFactory.table1D_I32(kernelRadius);
		} else {
			throw new RuntimeException("Unknown kernel type");
		}
		return kernel;
	}

}