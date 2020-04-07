package canny3d_thresholder;

import ij.IJ;
import ij.ImagePlus;

public class Processing {

	/**
	 * Wraps the logic and real processing of the generated plugin.
	 * 
	 * @param path      path to dir of the image
	 * @param name      name of the image - path/image should be exact path of the
	 *                  image to be processed
	 * @param impIn     ImagePlus to be processed - can be null if not used
	 * @param outputDir Path to dir where the output should be saved
	 * @return
	 */

	static boolean doProcessing(String path, String name, String outputDir, ProcessSettings pS, ImageSetting iS,
			ProgressDialog pD) {
		ImagePlus imp = pS.openImage(path + System.getProperty("file.separator") + name);
		pD.updateBarText("opened image"); // update progress during task as necessary
		IJ.save(imp, outputDir + System.getProperty("file.separator") + name + "_c.tif");
		return false;
	}

}
