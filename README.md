# XNAT-OHIF Viewer Plugin 2.0

![OHIF-XNAT-logo](assets/Logo.png)

This plugin integrates the OHIF viewer into XNAT, 2.0 comes with a suite of annotation tools, and allows users to import/export ROI Contours and Segmentations to ROICollection Assessors on XNAT.
Up to date viewer jars are available in the dist directory.

## 2.0 Patch Notes:

Note full patch-by-patch changes are available in the [CHANGELOG](./CHANGELOG).

### Overview

This release is built around the XNAT 1.7.6 specification.

The main difference between 2.0 and and 1.0.X is the inclusion of a set of tools for annotating regions of interest within XNAT and storing these in the XNAT database for use in processing pipelines. Using the [ROI Upload Assistant](https://bitbucket.org/icrimaginginformatics/roiuploadassistant), one may also upload regions of interest stored in DICOM `RTSTRUCT`, DICOM `SEG` and `AIM` 4.0 Image Annotation Collection formats from external sources to XNAT. These will be automatically indexed and importable in the viewer.

### Annotation Tools

If a scan contains segments or ROIContours, appropriate icons will be displayed under the thumbnail in the scan list.

#### ROI Contour Tools

The ROI Contour tools can be used to create and modify contour-based regions of interest. Sets of 2D contours make up named 3D contours which specify a region. One of more 3D contours make up an `ROICollection` which can be imported/exported from/to XNAT.

- The `Draw` tool allows the user to draw a 2D contour via two methods:
  - Polygon mode: Clicking on several points with the `Draw` tool and then clicking back at the origin allows the user to draw an accurate polygon.
  - Freehand mode: Click-dragging with the freehand tool allows a user to draw a freehand contour.
- The `Sculpt` tool allows users to modify an existing ROI by _pushing_ and _pulling_ it with a disk.
  - Double click near a contour with the sculpt tool to select it for editing.
  - Move the mouse nearer/farther from the region to use a smaller/larger disk size for sculpting.
  - Click and drag to use a the selected size disk to sculpt the contour.

ROI Contours may be managed and imported/exported to XNAT via the `Contours` sidebar interface. The sidebar also allows you to toggle:

- `Interpolation` - Whether intermediate contours should be linearly interploated when drawing, such that you only need to manually delineate every few frames.
- `Stats` - Whether to display the labels and 2D statistics of each contour on the viewport.

#### Segmentation Tools

The Segmentation tools may be used to create and modify labelmap-based regions of interest. Collections of voxels marked with a particular label comprise 3D segments which specify a region. One or more segments make up an `ROICollection` which can be imported/exported from/to XNAT.

- The `Manual` brush tool allows the user to paint the labelmap using a circular brush.
- The `Smart CT` brush tool allows you to select a tissue type, or set a custom Hounsfield Unit gate, to quickly segment CT scans by roughly scribbling over a region whilst only painting in the desired tissue.
- The `Auto` brush tool sets the threshold for the gate of a brush stroke (mouse drag) by the minimum and maximum pixel values present within the brush cursor when the mouse is pressed down.

Segments may be managed and imported/exported to XNAT via the `Segments` sidebar interface. The sidebar also allows you to set:

- The tissue type (gate) used by the `Smart CT` brush tool. Or a custom gate.
- The hole-filling and island removal settings for the `Smart CT` and `Auto` brush tools.

### ROI Collections Assessors

The [xnat-roi-plugin](https://bitbucket.org/icrimaginginformatics/xnat-roi-plugin) (bundled with this plugin) adds a new assessor datatype, the `ROICollection`.

- `ROICollection`s are containers for common contour-based and labelmap-based segmentation formats.

  - Currently supports DICOM `RTSTRUCT`, DICOM `SEG` and `AIM` 4.0 Image Annotation Collection formats.
  - ROI Contour annotations drawn in the viewer may be exported to XNAT, generating an `ROICollection` containing an `AIM` representation and an `RTSTRUCT` representation.
  - Segment annotations drawn in the viewer may be exported to XNAT, generating an `ROICollection` containing a `SEG` representation.
  - The [ROI Upload Assistant](https://bitbucket.org/icrimaginginformatics/roiuploadassistant) may be used to upload `RTSTRUCT`, `SEG` and `AIM` to XNAT, which can then be viewed/modified in the viewer.

- `ROICollection`s have a custom datatype page, which list the individual regions within the collection.
- Individual regions contained within an `ROICollection` are stored in Hibernate tables within XNAT, and so are searchable.

### Full Window Viewer

- A revamped routing system for mapping XNAT concepts onto OHIF concepts means the viewer no longer needs to sit in an XNAT parent window.
- Left clicking `View Session`/`View Subject` will open the viewer in the active window.
- Middle/Right clicking `View Session`/`View Subject` will open the viewer in a new tab/window depending on browser configuration.

### Synchronization and Smoothing Options

- Using the `sync` toggles at the top right of each viewport, one can synchronize scans to scroll together.

  - The 'More/Sync Settings' menu has the option to set all viewports to be synced by default.
  - The 'More/Sync Settings' menu lets the user switch the synchronization strategy to operate via image position or by frame number. Image position is the default.

- The `Smooth` toggle at the top right of each viewport allows the user to toggle voxel interpolation (previousy always on).
  - Turning smoothing off for very low resolution images can sometimes help with labelmap delineation.
  - Smoothing is on by default.

### Additional Views

#### Subject View

- Clicking `View Subject` on a Subject page will now open up a subject-wide viewer session.

- In Subject view, the scan list contains a collapsable list of scans for each Session. You can view and annotate scans from multiple sessions simultaneously.
- When you annotate ROIs and export them back to XNAT, they will automatically be stored under the appropriate session.
- If metadata isn't present for all sessions, a dialog will appear telling the user its being generated. Once generated, the user will automatically be redirected to the subject view.

#### Shared Session/Subject Views

_Parent Project_ - the original source project.

_Target Project_ - the project the session is shared into.

- A session shared into a target project can now viewed by a user who only has access to the target project.
- If multiple sessions belonging to a particular subject are shared into a target project, a user in the target project may open a shared Subject view.
- `ROICollections` which are shared into the target project are able to be imported into the appropriate shared views.
- If a user in the target project has write access to the parent project, the export button will show in the Contour and Segment interfaces, and will push annotations back to the parent project.

### Navigation Bar

A navigation bar can now be accessed by opening the left-hand `XNAT Nav` tab in the viewer.

- The navigation bar allows the user to easily switch to a different XNAT project/subject/session, allowing one to segment many scans without leaving the viewer interface.
- Under 'This Project', the active project is displayed, with the active subject/session view highlighted yellow.
- You may navigate to subject/session views of other projects by expanding the project under 'Other Projects'.
- If a subject/session is shared from another project, the projectId of its parent is also displayed.
- If a XNAT Session contains `ROICollection`s, the quantity of ROIContour and Segment collections will be displayed under the session name within the tree view.
- If a user has unsaved annotations, they will get a warning confirmation before being able to navigate to a different view.
- The navigation bar fetches data about Projects/Subjects/Sessions on demand, resulting in a quick and responsive UI, often capable of navigating XNAT much faster than navigating through the standard XNAT interface.

### Simplified XAPI:

- The `/viewer` XAPI now only has these end points:
  - `GET /viewer/projects/{_projectId}/experiments/{_experimentId}/exists` - returns 200 if JSON metadata exists.
  - `GET /viewer/projects/{_projectId}/experiments/{_experimentId}` - Returns cached JSON metadata or generates it if it doesn't exist.
  - `POST /viewer/generate-all-metadata` - Admin only command to rebuild all JSON metadata on the XNAT.
  - `POST /viewer/projects/{_projectId}/experiments/{_experimentId}` - Forced rewrite of JSON metadata. A backdoor if automation is down for any reason.
- Missing JSON being generated and cached in the GET means a user with READ only permissions to a session can view a session without the need for admin/owner/member intervention.

### Improved Multiframe Support:

- Full multi-frame support for both primary and secondary multi-frame DICOM, and import/export of DICOM `RTSTRUCT` and DICOM `SEG` objects.
- Multiframe images now fetch their snapshot from a cached XNAT resource, avoiding timeout issues with large multiframes.

### Touch Support:

Touch support is available for easily segmentation of ROI Contours with a stylus/Apple pen, also including:

- Two finger drag-to-zoom
- Three finger scroll

Since the rest of XNAT currently has poor support for mobile browsers, if delineating ROI Contours on a tablet, its best to be linked straight to the first session to be segmented, the XNAT navigation bar within the viewer can then be used to switch views as needed without the need to use the XNAT interface.

### Misc UI:

- The viewer has been whitelabelled with the XNAT-OHIF logo and the ICR logo at the top left.
- The viewer displays the plugin version number at the top left.
- The Scans left side bar is now open by default.
- Scrollbars have been added to left and right sidebars for users without a mousewheel.
- The series list now displays full 64 character series descriptions and 10 digit series numbers cleanly, with line wrapping prioritising spaces, and otherwise linebreaking where needbe.
- The 'View Images' button has been changed to 'View Legacy XImgView' to avoid confusion with OHIF. Once XNAT is running on the new React-based OHIF 2.0 and has solid MPR, the old XImgViewer can be deprecated.

# Deploying the Pre-built plugin

1. Copy both plugins in the `dist` directory to the **plugins** directory of your XNAT installation. The location of the **plugins** folder varies based on how and where you have installed your XNAT. If using [`xnat-docker-compose`](https://github.com/NrgXnat/xnat-docker-compose), the plugins folder is located at `xnat-data/home/plugins` relative to the root directory.

2. Restart your Tomcat server with `sudo service tomcat7 restart`, or `docker-compose restart xnat-web` if using `xnat-docker-compose`.

# Initialising the viewer in a populated database

In the likely event you are installing this plugin on an XNAT with an already populated database, an admin may call the REST command **POST XNAT_ROOT_URL/xapi/viewer/generate-all-metadata** in order to initiate a process that will iterate through all the sessions in the XNAT and generate the required metadta, using as many threads as are available on the machine.

# Development

Development instructions for OHIF-Viewer-XNAT and this XNAT plugin [are avaiable here](https://bitbucket.org/icrimaginginformatics/ohif-viewer-xnat/).
