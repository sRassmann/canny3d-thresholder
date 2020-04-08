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
		imp.hide();

		pD.updateBarText("performing edge detection");
		IJ.run(imp, "Gaussian Blur...", "sigma=" + pS.gausSigma + " stack");
		IJ.run(imp, "3D Edge and Symmetry Filter", "alpha=5.00 radius=10 normalization=10 scaling=2 improved");
		ImagePlus edges = IJ.getImage();
		imp.changes = false;
		imp.close();
		edges.hide();
		
		pD.updateBarText("thresholding image");
		
		double lowThr, highThr;
		if(pS.lowThrAlgorithm == "Custom") {	
			lowThr = pS.lowThr;	// use custom value
		} else {
			IJ.setAutoThreshold(edges, pS.highThrAlgorithm + " dark stack");
			lowThr = edges.getProcessor().getMinThreshold(); // calculate from stack
		}
		if(pS.highThrAlgorithm == "Custom") {
			highThr = pS.highThr; // use custom value
		} else {
			IJ.setAutoThreshold(edges, pS.lowThrAlgorithm + " dark stack");
			highThr = edges.getProcessor().getMinThreshold(); // calculate from stack
		}
	
		IJ.run(edges, "3D Hysteresis Thresholding", "high=" + highThr + " low=" + lowThr);
		ImagePlus bin = IJ.getImage();
		edges.changes = false;
		edges.close();
		bin.hide();

		IJ.run(bin, "8-bit", "");
		IJ.run(bin, "3D Fill Holes", "");
		IJ.run(bin, "16-bit", "");

		IJ.save(bin, outputDir + System.getProperty("file.separator") + name + "_canny3d.tif");
		bin.changes = false;
		bin.close();
		
		return true;
	}

}
