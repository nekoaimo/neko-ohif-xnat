# XNAT-OHIF Viewer Plugin 2.0-RC5

![OHIF-XNAT-logo](assets/Logo.png)

This plugin integrates the OHIF viewer into XNAT, 2.0 comes with a suite of annotation tools, and allows users to import/export ROI Contours and Segmentations to ROICollection Assessors on XNAT.
Up to date viewer jars are available in the dist directory.

**PLEASE DO NOT ATTEMPT TO USE IN PRODUCTION AT THIS STAGE.**

**2.0 RC-5**

UX:

- The default ROICollection name on export is equal to the segment/contour name if there is only one, and blank otherwise, so some human readable label is enforced to some degree.

A full list of changes are available in the `CHANGELOG`.

Please check the issues page. Label new issues with the "XNAT-ROI Beta" tag as well as relevant tags (e.g. enhancement, bug, etc).
Please check that the issue does not already have an existing thread.

**This plugin is in the testing phase and not yet meant to be used in production**

# A) Deploying the Pre-built plugin

**New since 1.0.0: single plugin deployment.**

1. Stop your tomcat with "sudo service tomcat7 stop"

2. Copy both plugins in the `dist` directory to the **plugins** directory of your XNAT installation. The location of the
   **plugins** folder varies based on how and where you have installed your XNAT. If you are running
   a virtual machine created through the [XNAT Vagrant project](https://bitbucket/xnatdev/xnat-vagrant.git),
   you can copy the plugin to the appropriate configuration folder and then copy it within the VM from
   **/vagrant** to **/data/xnat/home/plugins**.

3. `sudo service tomcat7 start`

# B) (Optional) Initialising the viewer in a populated database

In the likely event you are installing this plugin on an XNAT with an already populated database, an admin may call the REST command \*\*POST XNAT_ROOT_URL/xapi/viewer/generate-all-metadata\*\* in order to initiate a process that will scour the database.

# Development

Development instructions for OHIF-Viewer-XNAT and this XNAT plugin [are avaiable here](https://bitbucket.org/icrimaginginformatics/ohif-viewer-xnat/).
