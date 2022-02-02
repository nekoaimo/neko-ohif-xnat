package org.nrg.xnatx.ohifviewer;

import org.nrg.framework.annotations.XnatDataModel;
import org.nrg.framework.annotations.XnatPlugin;
import org.springframework.context.annotation.ComponentScan;

@XnatPlugin(
	value = "ohifViewerPlugin",
	name = "XNAT OHIF Viewer Plugin",
	version = "3.2.0-RC-1",
	description = "Integrates the OHIF Cornerstone Viewer into XNAT.",
	logConfigurationFile="/ohifviewer-logback.xml",
	dataModels={
		@XnatDataModel(
			value="icr:roiCollectionData",
			singular = "ROI Collection",
			plural = "ROI Collections",
			code = "ROIC")
	},
	entityPackages={"icr.xnat.plugin.roi.entity"},
	openUrls = {"/viewer.html", "/*.js", "/*.stats.json", "/*.css", "/packages/**/*", "/sockjs/**/*"})
@ComponentScan({
	"org.nrg.xnatx.ohifviewer.xapi",
	"org.nrg.xnatx.ohifviewer.event.listeners",
	"org.nrg.xnatx.ohifviewer.init",
	"org.nrg.xnatx.roi.data",
	"org.nrg.xnatx.roi.event.listeners",
	"org.nrg.xnatx.roi.service",
	"org.nrg.xnatx.roi.xapi"})
public class OhifViewerPlugin
{}
