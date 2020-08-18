package canny3d_thresholder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.Duplicator;
import ij.text.TextPanel;

public class Processing {

	/**
	 * Wraps the logic and real processing of the generated plugin.
	 * 
	 * @param path      path to dir of the image
	 * @param name      name of the image - path/image should be exact path of the
	 *                  image to be processed
	 * @param outputDir Path to dir where the output should be saved
	 * @param pS        ProcessSettings of the task
	 * @param pD        Reference to ProgressDialog
	 * @return
	 */

	static boolean doProcessing(String path, String name, String outputDir, ProcessSettings pS, ProgressDialog pD) {
		ImagePlus imp;
		try {
			imp = pS.openImage(path + System.getProperty("file.separator") + name);
		} catch (IOException e) {
			IJ.log(e.getMessage() + " - skipped task");
			return false;
		}
		imp.hide();
		ImagePlus thrChannel = splitAndSaveChannels(imp, pS, name, outputDir); // does nothing if plain
																				// thresholding channel is input
		double[][] thresholds = new double[thrChannel.getNFrames()][2];
		for (int f = 1; f <= thrChannel.getNFrames(); f++) {
			thresholds[f - 1] = processSlice(pD, f, imp.getNFrames(), thrChannel, pS);
		}

		IJ.save(thrChannel,
				outputDir + System.getProperty("file.separator") + ProcessSettings.removeFileSuffix(name)
						+ ((pS.selectedInputFormat == ProcessSettings.INPUTFORMATS[0]) ? ""
								: ("_" + ProcessSettings.CHANNELSUFFIX + (pS.thrChannel + 1)))
						+ ("_canny3d.tif"));
		thrChannel.changes = false;
		thrChannel.close();
		saveLogFile(path, name, outputDir, pS, thresholds); // TODO modify log file
		return true;
	}

	/**
	 * performs the image processing logic (modified Canny object detection) on each
	 * frame, returns the obtained high and low threshold value
	 * 
	 * @param pD
	 * @param currentFrame
	 * @param nFrames
	 * @param thrChannel
	 * @param pS
	 * @return
	 */

	private static double[] processSlice(ProgressDialog pD, int currentFrame, int nFrames, ImagePlus thrChannel,
			ProcessSettings pS) {
		pD.updateBarText("Frame " + currentFrame + "/" + nFrames + " - performing edge detection");
		ImagePlus slice = new Duplicator().run(thrChannel, 1, 1, 1, thrChannel.getNSlices(), currentFrame,
				currentFrame);
		IJ.run(slice, "Gaussian Blur...", "sigma=" + pS.gaussSigma + " stack");
		IJ.run(slice, "3D Edge and Symmetry Filter",
				"alpha=" + pS.cannyAlpha + " radius=10 normalization=10 scaling=2 improved");
		slice.changes = false;
		slice.close();
		ImagePlus edges = IJ.getImage();
		thrChannel.changes = false;
		thrChannel.hide();
		edges.hide();

		pD.updateBarText("Frame " + currentFrame + "/" + nFrames + " - thresholding image");

		double[] thresholds = new double[2];
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
		thresholds[0] = lowThr;
		thresholds[1] = highThr;

		IJ.run(edges, "3D Hysteresis Thresholding", "high=" + highThr + " low=" + lowThr);
		ImagePlus mask = IJ.getImage();
		edges.changes = false;
		edges.close();
		mask.hide();

		IJ.run(mask, "8-bit", "");
		IJ.run(mask, "3D Fill Holes", "");

		rewriteOriginalIntensities(mask, thrChannel, currentFrame);

		mask.changes = false;
		mask.close();
		return thresholds;
	}

	/**
	 * @param mask       binarised image - intensities > 0 are considered positive
	 * @param thrChannel
	 * @param frameIndex index in IJ's indexing convention (1 based)
	 */
	private static void rewriteOriginalIntensities(ImagePlus mask, ImagePlus thrChannel, int frameIndex) {
		int z, x, y, stackindexThr, stackindexMask;
		for (z = 1; z <= mask.getNSlices(); z++) {
			stackindexThr = thrChannel.getStackIndex(1, z, frameIndex) - 1; // handling IJs indexing
			stackindexMask = mask.getStackIndex(1, z, 1) - 1;
			thrChannel.setSliceWithoutUpdate(stackindexThr);
			mask.setSliceWithoutUpdate(stackindexMask);
			for (y = 0; y < mask.getHeight(); y++) {
				for (x = 0; x < mask.getWidth(); x++) {
					if (mask.getStack().getVoxel(x, y, stackindexMask) == 0) {
						thrChannel.getStack().setVoxel(x, y, stackindexThr, 0);
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
				IJ.save(imps[c],
						outputDir + System.getProperty("file.separator") + ProcessSettings.removeFileSuffix(name) + "_"
								+ ProcessSettings.CHANNELSUFFIX + (c + 1) + ".tif");
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

	private static void saveLogFile(String path, String name, String outputDir, ProcessSettings pS,
			double[][] thresholds) {
		TextPanel sb = new TextPanel();
		sb.append("Log File for: " + path + name);
		sb.append("\nprocessed on " + new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date()) + "\n");

		sb.append(pS.toString());

		sb.append("\nFrame #		Low Threshold 		High Threshold");
		for (int i = 0; i < thresholds.length; i -= -1) {
			sb.append("\n" + (i+1) + "		" + thresholds[i][0] + "		" + thresholds[i][1]);
		}

		sb.saveAs(outputDir + System.getProperty("file.separator") + ProcessSettings.removeFileSuffix(name)
				+ ((pS.selectedInputFormat == ProcessSettings.INPUTFORMATS[0]) ? ""
						: ("_" + ProcessSettings.CHANNELSUFFIX + (pS.thrChannel + 1)))
				+ ("_canny3d_log.txt"));

	}

}
