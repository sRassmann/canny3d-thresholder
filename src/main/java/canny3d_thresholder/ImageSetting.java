package canny3d_thresholder;

import java.awt.Font;

import ij.gui.GenericDialog;

/**
 * Class providing all necessary image Processing settings. Pass an instance of this class or an array of instances to the process() method.
 * Use the genericDialog() wrapper function in order to provide a GUI for choosing processing settings.
 * 
 * @author sebas
 * 
 */
public class ImageSetting {
	
	boolean valid = false;		// input is valid
	
	// add necessary Settings and defaults here
	
	int factor1 = 0;
	double factor2 = 1;
	boolean choice = false;
	String [] arr = {"choice0", "choice1", "choice2", "choice3"};
	String selectionFromArr = arr[0]; 		//default choice and later result from choice
	
	public ImageSetting() {
		super();
		this.valid = true;
	}
	
	/**
	 * Constructs new Object and triggers a GD for the user
	 * @return User-chosen ImageSettings
	 * @throws Exception 
	 */
	public static ImageSetting initByGD(String pluginName, String pluginVersion) throws Exception {
		
		ImageSetting inst = new ImageSetting();			//returned instance of ImageSettingClass
		
		final Font headingFont = new Font("Sansserif", Font.BOLD, 14);		
		final Font textFont = new Font("Sansserif", Font.PLAIN, 12);
				
		GenericDialog gd = new GenericDialog(pluginName + " - ImageProcessingSettings");			
		gd.setInsets(0,0,0);	gd.addMessage(pluginName + " - Version " + pluginVersion, headingFont);	
		gd.addMessage("Insert Image Processings Settings", textFont);
		
		
		// Change as necessary
		gd.setInsets(5,0,0);	gd.addChoice("Example Choice ", inst.arr, inst.selectionFromArr);
		gd.setInsets(0,0,0);	gd.addNumericField("Insert int number", inst.factor1, 0);
		gd.setInsets(0,0,0);	gd.addNumericField("Insert float number", inst.factor2, 3);
		gd.setInsets(0,0,0);	gd.addCheckbox("Yes or No?", inst.choice);

		//show Dialog-----------------------------------------------------------------
		gd.showDialog();

		//read and process variables--------------------------------------------------	
		
		inst.selectionFromArr = gd.getNextChoice();
		inst.factor1 = (int) gd.getNextNumber();
		inst.factor2 = gd.getNextNumber();
		inst.choice = gd.getNextBoolean();	
		
		if(gd.wasCanceled()) throw new Exception();
		
		return inst;		
	}
	
	// TODO Insert choices as needed
	public String toString() {
		return ("");
	}
	
	

}
