package org.nrg.xnatx.ohifviewer.event.listeners;

import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.services.SerializerService;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xft.event.XftItemEventI;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.entities.ResourceSurveyRequest;
import org.nrg.xnat.services.archive.ResourceSurveyService;
import org.nrg.xnat.services.messaging.archive.AbstractResourceMitigationEventHandlerMethod;
import org.nrg.xnat.services.messaging.archive.ResourceMitigationEventProperties;
import org.nrg.xnatx.ohifviewer.inputcreator.JsonMetadataHandler;
import org.nrg.xnatx.plugin.PluginException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class OhifViewerResourceMitigationEventHandlerMethod extends AbstractResourceMitigationEventHandlerMethod {
    private final ResourceSurveyService resourceSurveyService;
    private final JsonMetadataHandler   metadataHandler;

    public OhifViewerResourceMitigationEventHandlerMethod(final ResourceSurveyService resourceSurveyService,
                                                          final JsonMetadataHandler metadataHandler,
                                                          final SerializerService serializer,
                                                          final NamedParameterJdbcTemplate template) {
        super(serializer, template);
        this.resourceSurveyService = resourceSurveyService;
        this.metadataHandler       = metadataHandler;
    }

    @Override
    protected boolean handleMitigationEvent(final ResourceMitigationEventProperties properties, final XftItemEventI event) {
        final ResourceSurveyRequest request   = properties.getRequest();
        final UserI                 requester = properties.getRequester();

        final String sessionId = request.getExperimentId();

        // Delete any existing OHIF viewer metadata for the affected session
        final boolean deleted = metadataHandler.deleteSessionConfig(sessionId);
        if (deleted) {
            log.info("Found and deleted OHIF viewer metadata for imaging session ID {}", sessionId);
        } else {
            log.info("No OHIF viewer metadata found for imaging session ID {}", sessionId);
        }

        // Regenerate if there are no outstanding resource survey requests for the session
        final List<ResourceSurveyRequest> requests;
        try {
            requests = resourceSurveyService.getOpenBySessionId(requester, sessionId);
        } catch (InsufficientPrivilegesException e) {
            log.warn("Got insufficient privileges for user {} trying to check on session ID {}", properties.getUsername(), sessionId);
            return false;
        } catch (NotFoundException e) {
            log.warn("Got not found exception for user {} trying to check on session ID {}", properties.getUsername(), sessionId);
            return false;
        }

        if (!requests.isEmpty()) {
            log.info("There are {} outstanding resource survey requests for image session {}: OHIF metadata shouldn't be regenerated until all outstanding requests have completed", requests.size(), sessionId);
            return true;
        }

        log.info("There are no outstanding resource survey requests for image session {}: OHIF metadata can be safely regenerated now", sessionId);
        final XnatImagesessiondata session = XnatImagesessiondata.getXnatImagesessiondatasById(sessionId, requester, false);

        try {
            metadataHandler.createAndStoreJsonConfig(session, requester, true);
            return true;
        } catch (PluginException e) {
            log.warn("An error occurred trying to re-generate OHIF metadata for session {}", sessionId, e);
            return false;
        }
    }
}