package canny3d_thresholder; // TODO change package Name

import java.awt.event.WindowEvent;

import ij.IJ;
import ij.gui.WaitForUserDialog;
import ij.plugin.PlugIn;

public class Main implements PlugIn {

	static String pluginName = "plugin Name"; // TODO change Name
	static String pluginVersion = "0.0.2";
	ProgressDialog progressDialog;
	boolean processingDone = false;
	boolean continueProcessing = true;

	static ProcessSettings pS;

	/**
	 * Takes care of the plugin configuration, file selection, and looping over the
	 * images - normally does not require changes
	 */
	public void run(String arg) {
		pS = null;
		ImageSetting iS = null;
		try {
			pS = ProcessSettings.initByGD(pluginName, pluginVersion);
			iS = ImageSetting.initByGD(pluginName, pluginVersion);
		} catch (Exception e) {
			new WaitForUserDialog("GD canceled - end Plugin!").show();
			return;
		}
		if (pS.resultsToNewFolder) {
			pS.selectOutputDir();
		}
		startProgressDialog(pS.toArray(), pS.getNOfTasks());

		for (int task = 0; task < pS.getNOfTasks(); task++) {
			progressDialog.updateBarText("in progress...");
			Processing.doProcessing(pS.paths.get(task), pS.names.get(task), pS.getOutputDir(task), pS, iS, progressDialog);
			progressDialog.moveTask(task);
		}
		progressDialog.updateBarText("finished!");
	}

	private void startProgressDialog(String[] tasks, int nOfTasks) {
		progressDialog = new ProgressDialog(tasks, nOfTasks);
		progressDialog.setLocation(0, 0);
		progressDialog.setVisible(true);
		progressDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent winEvt) {
				if (processingDone == false) {
					IJ.error("Script stopped...");
				}
				continueProcessing = false;
				return;
			}
		});
	}

}
