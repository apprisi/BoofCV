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

package gecv.alg.transform.pyramid;

import gecv.alg.filter.convolve.FactoryKernelGaussian;
import gecv.core.image.ConvertBufferedImage;
import gecv.gui.image.ImagePyramidPanel;
import gecv.gui.image.ShowImages;
import gecv.io.image.UtilImageIO;
import gecv.struct.convolve.Kernel1D_F32;
import gecv.struct.image.ImageFloat32;
import gecv.struct.pyramid.ImagePyramid;
import gecv.struct.pyramid.ImagePyramidI;
import gecv.struct.pyramid.PyramidUpdater;

import java.awt.image.BufferedImage;

/**
 * Displays an image pyramid.
 *
 * @author Peter Abeles
 */
public class VisualizeImagePyramidApp {

	public static void main( String args[] ) {
		int scales[] = new int[]{1,2,2,2,2};

		BufferedImage input = UtilImageIO.loadImage("evaluation/data/standard/boat.png");

		Kernel1D_F32 gaussian = FactoryKernelGaussian.gaussian1D(ImageFloat32.class,2);
		PyramidUpdater<ImageFloat32> updater = new PyramidUpdateIntegerDown<ImageFloat32>(gaussian,ImageFloat32.class);
		ImagePyramid<ImageFloat32> pyramid = new ImagePyramidI<ImageFloat32>(true,updater,scales);

		ImageFloat32 inputF32 = ConvertBufferedImage.convertFrom(input,(ImageFloat32)null);

		pyramid.update(inputF32);

		ImagePyramidPanel gui = new ImagePyramidPanel(pyramid);
		gui.render();
		gui.repaint();

		ShowImages.showWindow(gui,"Image Pyramid");
	}
}