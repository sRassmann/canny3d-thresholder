# Canny 3D Thresholder
An ImageJ Plugin implementing a modified [Canny 3d Edge Detection](https://en.wikipedia.org/wiki/Canny_edge_detector) to threshold images stacks based on the [3D ImageJ Suite](https://imagejdocu.tudor.lu/plugin/stacks/3d_ij_suite/start) package.

The plugin implements a [custom batch processing handler](https://github.com/sRassmann/imageJ-plugin-template) and perform the follwoing steps:   
1. Smoothes the image stack to supress random noise via a 2D Gaussian blur filter (the z dimension is assumed to have a lower resolution and a blur efect due to confocal imaging)
2. Detect edges using a 3D Sobel kernel
3. Performing a 3D Hysterisis Threshold: All pixels above a defined high threshold are kept, pixel below the defined low threshold are neglected, pixels in between the low and high threshold are only kept if they are connected to pixels above the high threshold.   
High and low thresholds can be defined using either custom values or can be calculated using ImageJ's thresholding methods based on the histogram of the whole stack.
4. Holes encapsulated in all dimensions are filled
5. The image is saved with the suffix  '*\_canny3d.tif*'  as 16 bit image

__NOTE__: Right now, the plugin outputs a binarized image, thus, the intensities are overwritten and cannot be measured in downstream analysis (e.g. CiliaQ)
