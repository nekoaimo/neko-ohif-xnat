package org.nrg.xnatx.ohifviewer.init;

import org.nrg.xdat.collections.DisplayFieldRefCollection;
import org.nrg.xdat.display.*;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.nrg.xdat.schema.SchemaElement;
import org.nrg.xdat.security.ElementSecurity;
import org.nrg.xnat.initialization.tasks.AbstractInitializingTask;
import org.nrg.xnat.initialization.tasks.InitializingTaskException;
import org.nrg.xnat.services.XnatAppInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OhifSearchListingInitTask extends AbstractInitializingTask {
    private static final String OHIF = "OHIF";
    private static final String PROJECT_LISTING_PREFERENCE = "addOhifViewLinkToProjectListingDefaults";

    private final XnatAppInfo appInfo;
    private final SiteConfigPreferences siteConfigPreferences;

    @Autowired
    public OhifSearchListingInitTask(final XnatAppInfo appInfo,
                                     final SiteConfigPreferences siteConfigPreferences) {
        this.appInfo = appInfo;
        this.siteConfigPreferences = siteConfigPreferences;
    }

    @Override
    public String getTaskName() {
        return "OHIF search listing init task";
    }

    @Override
    protected void callImpl() throws InitializingTaskException {
        if (!appInfo.isInitialized()) {
            throw new InitializingTaskException(InitializingTaskException.Level.RequiresInitialization);
        }
        try {
            List<ElementSecurity> securityList = ElementSecurity.GetSecureElements();

            for (ElementSecurity es : securityList) {
                setupOhifDisplayField(es);
            }
        } catch (Exception e) {
            throw new InitializingTaskException(InitializingTaskException.Level.Warn,
                    "Unable to inject OHIF search listing customization", e);
        }
    }

    private void setupOhifDisplayField(ElementSecurity es) throws Exception {
        SchemaElement se = es.getSchemaElement();

        boolean isSubjectDisplay;
        if (XnatSubjectdata.SCHEMA_ELEMENT_NAME.equals(se.getFullXMLName())) {
            isSubjectDisplay = true;
        } else if (se.instanceOf(XnatImagesessiondata.SCHEMA_ELEMENT_NAME)) {
            isSubjectDisplay = false;
        } else {
            return;
        }

        // Add display field to the element display
        ElementDisplay ed = se.getDisplay();
        DisplayField df = makeDisplayField(ed, isSubjectDisplay);
        ed.addDisplayField(df);

        // If enabled, configure default project listings to include this display field
        Boolean addToProject = siteConfigPreferences.getBooleanValue(PROJECT_LISTING_PREFERENCE);
        if (addToProject != null && addToProject) {
            addDisplayFieldToProjectBundle(ed);
        }
    }

    private DisplayField makeDisplayField(ElementDisplay ed, boolean isSubjectDisplay) {
        DisplayField df = new DisplayField(ed);
        df.setId(OHIF);
        df.setHeader("View");
        df.setVisible(true);
        df.setSearchable(true);
        df.setHtmlContent(true); // this keeps the column from being included in spreadsheet downloads
        Map<String, String> content = new HashMap<>();
        content.put("sql", "'<i class=\"fa fa-eye\"</i>'");
        df.setContent(content);
        setupHtmlLink(df, isSubjectDisplay);
        return df;
    }

    private void setupHtmlLink(DisplayField df, boolean isSubjectDisplay) {
        HTMLLinkProperty onclick = new HTMLLinkProperty();
        onclick.addInsertedValue("Project", "PROJECT");
        onclick.addInsertedValue("Subject", "SUBJECT_ID");
        onclick.setName("ONCLICK");
        if (isSubjectDisplay) {
            onclick.setValue("checkSubjectForSessionJSON(true, '@Project', '@Subject'); return false;");
        } else {
            onclick.addInsertedValue("Session", "SESSION_ID");
            onclick.setValue("checkSessionJSON(true, '@Project', '@Subject', '@Session'); return false;");
        }

        HTMLLinkProperty title = new HTMLLinkProperty();
        title.setName("TITLE");
        title.setValue("Open in OHIF viewer");

        ArrayList<HTMLLinkProperty> properties = new ArrayList<>();
        properties.add(onclick);
        properties.add(title);

        HTMLLink link = new HTMLLink();
        link.setProperties(properties);
        df.setHtmlLink(link);
    }

    private void addDisplayFieldToProjectBundle(ElementDisplay ed)
            throws DisplayFieldRefCollection.DuplicateDisplayFieldRefException {
        DisplayVersion dv = ed.getVersion("project_bundle");
        DisplayFieldRef dfr = new DisplayFieldRef(dv);
        dfr.setId(OHIF);
        dv.addDisplayField(dfr);
    }
}
