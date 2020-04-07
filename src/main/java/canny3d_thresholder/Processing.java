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

	static boolean doProcessing(String path, String name, String outputDir, ProcessSettings pS, ProgressDialog pD) {
		ImagePlus imp = pS.openImage(path + System.getProperty("file.separator") + name);

		pD.updateBarText("performing edge detection");
		IJ.run(imp, "Gaussian Blur...", "sigma=" + pS.gausSigma + " stack");
		IJ.run(imp, "3D Edge and Symmetry Filter", "alpha=5.00 radius=10 normalization=10 scaling=2 improved");
		ImagePlus edges = IJ.getImage();
		imp.close();
		
		pD.updateBarText("thresholding image");
		IJ.setAutoThreshold(edges, pS.highThrAlgorithm + " dark stack");
		double min_thresh = edges.getProcessor().getMinThreshold();

		IJ.setAutoThreshold(edges, pS.lowThrAlgorithm + " dark stack");
		double max_thresh = edges.getProcessor().getMinThreshold();

		IJ.run(edges, "3D Hysteresis Thresholding", "high=" + max_thresh + " low=" + min_thresh);
		ImagePlus bin = IJ.getImage();
		edges.close();

		IJ.run(bin, "8-bit", "");
		IJ.run(bin, "3D Fill Holes", "");

		IJ.save(bin, outputDir + System.getProperty("file.separator") + name + "_canny3d.tif");
		bin.close();
		
		return true;
	}

}
