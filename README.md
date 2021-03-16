# Canny 3D Thresholder

An ImageJ Plugin implementing a modified [Canny 3d Edge Detection](https://en.wikipedia.org/wiki/Canny_edge_detector) to threshold images stacks based on the [3D ImageJ Suite](https://imagejdocu.tudor.lu/plugin/stacks/3d_ij_suite/start) package.

Developed and optimized for [CiliaQ](https://github.com/hansenjn/CiliaQ).

## Plugin Description

The plugin implements a [custom batch processing handler](https://github.com/sRassmann/imageJ-plugin-template) and performs the following steps:   
1. Smoothes the image stack to suppress random noise via a 2D Gaussian blur filter (the z dimension is assumed to have a lower resolution and a blur effect due to confocal imaging).
2. Detects edges using a 3D Sobel kernel.
3. Performs a 3D Hysterisis Threshold: All pixels above a defined high threshold are kept, pixel below the defined low threshold are neglected, pixels in between the low and high threshold are only kept if they are connected to pixels above the high threshold.   
High and low thresholds can be defined using either custom values or can be calculated using ImageJ's thresholding methods based on the histogram of the whole stack.
4. Holes encapsulated in all dimensions are filled.
5. The original intensity values within the detected objects are kept, whereas the remaining voxels are set to 0. Thus, the output image is pseudo binarized and intensity values within the objects can be evaluated in downstream analysis (e.g. CiliaQ).

The plugin can also save the remaining channels of the stack, thus, can act as an additional channel splitter.

## User Guide

### installation

1. This plugin requires packages included in the [Fiji release](https://imagej.net/Fiji/Downloads) of ImageJ. Core ImageJ might work if the dependencies are installed manually but is not recommended!
2. Download the latest version of the Canny 3D [Releases](https://github.com/sRassmann/canny3d-thresholder/releases)') and copy the .jar file to the '*plugins*' directory in the ImageJ folder ('*Fiji.app*').
3. Download the core library (['*mcib3d-core-3.96.jar*'](https://imagejdocu.tudor.lu/_media/plugin/stacks/3d_ij_suite/mcib3d-core-3.96.jar)) __and__ the plugins (['*mcib3d_plugins-3.96.jar*'](https://imagejdocu.tudor.lu/_media/plugin/stacks/3d_ij_suite/mcib3d_plugins-3.96.jar)) from 3D ImageJ Suite's [download section](https://imagejdocu.tudor.lu/plugin/stacks/3d_ij_suite/start#download) and copy to the .jar files to the '*plugins*' directory in the ImageJ folder ('*Fiji.app*').
4. Restart ImageJ


### input
The plugin can handle multi-channel (up to 4 channels) timelapse image stacks ("4D") provided either as TIFF stack or as a common Bioformat (e.g. Nikon's .nd2 or Olympus' .oib).

### processing
1. Open ImageJ and select *Plugin*>*SR*>*Canny 3D Thresholder*
2. In the user dialog the following options are available:
  * File selection mode: Allows to use the active (=last used) image in ImageJ (*active image in FIJI*), all images open images (*all images open in FIJI*), or to load a previously defined set of files from .txt containing the full paths to the images in separate lines (*use list*). The pre-selected option (*manual file selection*) lets the user select files in a subsequent dialog (*Multi-File-Manager*).
  * Select image input image format: Choose between single channel stack, multi channel stack, or a raw microscopy file. If multi channel stack or raw microscopy file is chosen another dialog the thresholding channel and the set of addtionally saved channels need to be defined in a subsequent dialog.
  * Sigma for Gaussian blur: Defines the amount of blur added to the image as the sigma ("radius") of the Gaussian distribution in pixels.
  In the test data a sigma of 1 and 0.5 pixels yielded good results for images with 0.1 and 0.2 µm/pixel, respectively. This values might vary with the quality (less noise might allow for less blur and, thus a lower value for sigma).
  * Alpha: The parameters defines the sensitivity of the edge detection.
  * Method for low and high threshold: Select one of ImageJ's core thresholding algorithms to calculate the low and high threshold or choose *Custom Value* to define a fixed value. 
  * Output to new Folder: Allows to select a folder where all output data is saved. If this option is not chosen, each output file will be saved in the same directory as the corresponding raw input file.
3. If the manual file selection mode was choose a file selection dialog will apear:
  * Click on *select files individually* to select single files from the file system. 
  * The option *select files by pattern* allows to load all files containing a specific character pattern within a defined root directory. Select a directory to start the search in the *Choose directory to start pattern matching* dialog and define the pattern which the name of each file should contain (e.g. '.nd2' for all microscopy files in the folder) - all files containing this character string at least on time will be added to the file list. Further, it is possible to neglect all files containing a certain pattern (e.g. a date) or which are located in directory containing a defined pattern in the name (e.g. directories marked with 'processed'). For more advanced selection (e.g. any date followed by an specific experiment ID) you can choose *Input as Regex* and specify patterns as regular expressions (regex). See [here](https://www.vogella.com/tutorials/JavaRegularExpressions/article.html) for an introduction.
  * Within the Multi-File-Manager selection dialog individual files can be manually excluded by selecting the name of the file and pressing *remove selected file*.
  
### Installation

1. This plugin requires packages included in the [Fiji release](https://imagej.net/Fiji/Downloads) of ImageJ. Core ImageJ might work if the dependencies are installed manually but is not recommended!
2. Download the latest version of the Canny 3D [Releases](https://github.com/sRassmann/canny3d-thresholder/releases)') and copy the .jar file to the '*plugins*' directory in the ImageJ folder ('*Fiji.app*').
3. Download the core libary (['*mcib3d-core-3.96.jar*'](https://imagejdocu.tudor.lu/_media/plugin/stacks/3d_ij_suite/mcib3d-core-3.96.jar)) __and__ the plugins (['*mcib3d_plugins-3.96.jar*'](https://imagejdocu.tudor.lu/_media/plugin/stacks/3d_ij_suite/mcib3d_plugins-3.96.jar)) from 3D ImageJ Suite's [download section](https://imagejdocu.tudor.lu/plugin/stacks/3d_ij_suite/start#download) and copy to the .jar files to the '*plugins*' directory in the ImageJ folder ('*Fiji.app*').
4. Restart ImageJ


### input
As of v0.0.2 the plugin can handle multi-channel (up to 4 channels) TIFF stacks as well as common Bioformats (e.g. Nikon's .nd2 or Olympus' .oib). The tool extracts a defined channel and performs the thresholding and can also save the remaining channels. Thus, the tool can be used as a channel splitter. 

### processing
1. Open ImageJ and select *Plugin*>*SR*>*Canny 3D Thresholder*
2. In the user dialog the following options are available:
  * File selection mode: Allows to use the active (=last used) image in ImageJ (*active image in FIJI*), all images open images (*all images open in FIJI*), or to load a previously defined set of files from .txt containing the full paths to the images in separate lines (*use list*). The pre-selected option (*manual file selection*) lets the user select files in a subsequent dialog (*Multi-File-Manager*).
  * Select image input image format: Choose between single channel stack, multi channel stack, or a raw microscopy file. If multi channel stack or raw microscopy file is chosen another dialog the thresholding channel and the set of additionaly saved channels need to be defined in a subsequent dialog.
  * Sigma for Gaussian blur: Defines the amount of blur added to the image as the sigma ("radius") of the Gaussian distribution in pixels.
  In the test data a sigma of 1 and 0.5 pixels yielded good results for images with 0.1 and 0.2 µm/pixel, respectively. This values might vary with the quality (less noise might allow for less blur and, thus a lower value for sigma).
  * Alpha: The parameters defines the sensitivity of the edge detection.
  * Method for low and high threshold: Select one of ImageJ's core thresholding algorithms to calculate the low and high threshold or choose *Custom Value* to define a fixed value.
  * Output to new Folder: Allows to select a folder where all output data is saved. If this option is not chosen, each output file will be saved in the same directory as the corresponding raw input file.
3. If the manual file selection mode was choose a file selection dialog will apear:
  * Click on *select files individually* to select single files from the file system. 
  * The option *select files by pattern* allows to load all files containing a specific character pattern within a defined root directory. Select a directory to start the search in the *Choose directory to start pattern matching* dialog and define the pattern which the name of each file should contain (e.g. '.nd2' for all microscopy files in the folder) - all files containing this character string at least on time will be added to the file list. Further, it is possible to neglect all files containing a certaing pattern (e.g. a date) or which are located in directory containg a defined pattern in the name (e.g. directories marked with 'processed'). For more advanced selection (e.g. any date followed by an specific experiment ID) you can choose *Input as Regex* and specify patterns as regular expressions (regex). See [here](https://www.vogella.com/tutorials/JavaRegularExpressions/article.html) for an introduction.
  * Within the Multi-File-Manager selection dialog individual files can be manually excluded by selecting the name of the file and pressing *remove selected file*.
  * Press '*start processing*' to confirm the set of selected files.
4.  A progress bar will appear to inform over the progress in processing. Note: During processing some windows might pop up for some microseconds and the log dialog from ImageJ will appear. Unfortunatly, this happens within the used plugins, so for now there is no way to turn it off. 
  
### output
If single channel images are processed the thresholded images are saved with suffix  '*_canny3d.tif*' as a 16 bit grayscale image.

If multi channel images are processed the separated channels are stored with the suffix  '*_C*<*channel index*>*.tif*'. The thresholded images are stored as '*_C*<*channel index*>*_canny3d.tif*'.

A log file (suffix '*_C*<*channel index*>*_canny3d_log.txt*') is saved jointly with the output files.
  
  
# Citation

Hansen JN, Rassmann S, Stüven B, Jurisch-Yaksi N, Wachten D. CiliaQ: a simple, open-source software for automated quantification of ciliary morphology and fluorescence in 2D, 3D, and 4D images. Eur Phys J E Soft Matter. 2021 Mar 8;44(2):18. doi: 10.1140/epje/s10189-021-00031-y. PMID: [33683488](https://pubmed.ncbi.nlm.nih.gov/33683488/).
