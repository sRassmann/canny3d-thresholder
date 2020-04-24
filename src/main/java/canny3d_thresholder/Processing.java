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
		ImagePlus thrChannel = splitAndSaveChannels(imp, pS, name, outputDir); // does nothing if plain
																					// thresholding channel is input

		pD.updateBarText("performing edge detection");
		IJ.run(thrChannel, "Gaussian Blur...", "sigma=" + pS.gausSigma + " stack");
		IJ.run(thrChannel, "3D Edge and Symmetry Filter",
				"alpha=" + pS.cannyAlpha + " radius=10 normalization=10 scaling=2 improved");
		ImagePlus edges = IJ.getImage();
		thrChannel.changes = false;
		thrChannel.hide();
		edges.hide();

		pD.updateBarText("thresholding image");

		double lowThr, highThr;
		if (pS.lowThrAlgorithm == "Custom") {
			lowThr = pS.lowThr; // use custom value
		} else {
			IJ.setAutoThreshold(edges, pS.highThrAlgorithm + " dark stack");
			lowThr = edges.getProcessor().getMinThreshold(); // calculate from stack
		}
		if (pS.highThrAlgorithm == "Custom") {
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

		rewriteOriginalIntensities(bin, thrChannel);
		thrChannel.close();
		IJ.save(bin, outputDir + System.getProperty("file.separator") + ProcessSettings.removeFileSuffix(name) + "_" +
				ProcessSettings.CHANNELSUFFIX + (pS.thrChannel+1) + "_canny3d.tif");
		bin.changes = false;
		bin.close();
		return true;
	}

	private static void rewriteOriginalIntensities(ImagePlus bin, ImagePlus thrChannel) {
		int t, z, x, y, stackindex;
		for (t = 0; t < bin.getNFrames(); t++) {
			for (z = 0; z < bin.getNSlices(); z++) {
				stackindex = bin.getStackIndex(1,z + 1, t + 1) - 1; // handling IJs indexing
				bin.setSliceWithoutUpdate(stackindex);
				thrChannel.setSliceWithoutUpdate(stackindex);
				for (y = 0; y < bin.getHeight(); y++) {
					for (x = 0; x < bin.getWidth(); x++) {
						if (bin.getProcessor().get(x, y) != 0) {
							bin.getProcessor().set(x, y, thrChannel.getProcessor().get(x, y));
						}
					}
				}
			}
		}
	}

	/**
	 * Splits and saves the images to the output dir defined in the @param pS
	 * 
	 * @param imp
	 * @param pS
	 * @param chSuffix Suffix added to saved channels
	 * @return Channel specified in @param pS for further processing (thresholding)
	 */
	private static ImagePlus splitAndSaveChannels(ImagePlus imp, ProcessSettings pS, String name, String outputDir) {
		if (pS.selectedInputFormat == ProcessSettings.INPUTFORMATS[0]) {
			return imp;
		}
		ImagePlus[] imps = ij.plugin.ChannelSplitter.split(imp);
		ImagePlus thrChannel = null;
		
		for (int c = 0; c < Math.min(pS.saveChannels.length, imps.length); c -= -1) { // only consider existing and
																						// defined channels (nChannels
																						// in ProcessingSettings)
			if (pS.saveChannels[c]) {
				IJ.save(imps[c], outputDir + System.getProperty("file.separator") + 
						ProcessSettings.removeFileSuffix(name) + "_" + ProcessSettings.CHANNELSUFFIX + (c+1)  + ".tif");
			}
			if (pS.thrChannel == c) {
				thrChannel = imps[c];
			} else {
				imps[c].close();
			}
		}
		imp.close();
		return thrChannel;
	}

}
