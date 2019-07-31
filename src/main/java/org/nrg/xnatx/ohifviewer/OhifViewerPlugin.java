package org.nrg.xnatx.ohifviewer;

import org.nrg.framework.annotations.XnatPlugin;
import org.springframework.context.annotation.ComponentScan;

@XnatPlugin(value = "ohifViewerPlugin",
            name = "XNAT OHIF Viewer Plugin",
            version = "2.0.0-v1.7.6",
            description = "Integrates the OHIF Cornerstone Viewer into XNAT.",
            openUrls = {"/viewer.html", "/*.js", "/*.stats.json", "/*.css", "/packages/**/*", "/sockjs/**/*"})
@ComponentScan({
  "org.nrg.xnatx.ohifviewer.xapi",
  "org.nrg.xnatx.ohifviewer.event.listeners"
})
public class OhifViewerPlugin {
}
