(function($) {
  var methods = {
    init: function(settings) {
      return this.each(function() {
        var self = this;
        if (typeof self.pleaseWait != "undefined") return;
        self.pleaseWait = {
          opt: $.extend(true, {}, $.fn.pleaseWait.defaults, settings),
          cog: document.createElement("img"),
          timer: false,
          overlay: document.createElement("div"),
          degrees: 1
        };

        var targetWidth = $(self).outerWidth();
        var targetHeight = $(self).outerHeight();
        var verticalMidpoint =
          targetHeight / 2 - self.pleaseWait.opt.height / 2;
        var horizontalMidpoint =
          targetWidth / 2 - self.pleaseWait.opt.width / 2;
        var offset = $(self).offset();

        $(self.pleaseWait.cog).css({
          position: "absolute",
          "z-index": "999999",
          left: offset.left + horizontalMidpoint,
          top: offset.top + verticalMidpoint
        });
        $(document.body).append(self.pleaseWait.cog);

        self.pleaseWait.overlay = document.createElement("div");
        $(self.pleaseWait.overlay).css({
          position: "absolute",
          "z-index": "999998",
          left: offset.left,
          top: offset.top,
          width: targetWidth,
          height: targetHeight,
          "background-color": "rgba(255,255,255,0.8)",
          display: "none"
        });
        $(document.body).append(self.pleaseWait.overlay);

        if (
          self.pleaseWait.opt.imageType != "encoded" &&
          self.pleaseWait.opt.image.length > 0
        ) {
          self.pleaseWait.cog.src = image;
        } else {
          self.pleaseWait.cog.src =
            "data:image/png;base64," + self.pleaseWait.opt.image;
        }

        $(self).attr("data-pleaseWait", "1");

        methods.start.call(self);
      });
    },
    start: function() {
      var self = $(this);
      if (typeof self.length != "undefined" && self.length > 0) self = self[0];
      if (typeof self.pleaseWait == "undefined") return;

      var targetWidth = $(self).outerWidth();
      var targetHeight = $(self).outerHeight();
      var verticalMidpoint = targetHeight / 2 - self.pleaseWait.opt.height / 2;
      var horizontalMidpoint = targetWidth / 2 - self.pleaseWait.opt.width / 2;
      var offset = $(self).offset();
      $(self.pleaseWait.cog).css({
        left: offset.left + horizontalMidpoint,
        top: offset.top + verticalMidpoint
      });
      $(self.pleaseWait.overlay).css({
        left: offset.left,
        top: offset.top,
        width: targetWidth,
        height: targetHeight
      });

      $(self.pleaseWait.cog).css("display", "");
      $(self.pleaseWait.overlay).css("display", "");
      if (self.pleaseWait.timer != false) clearTimeout(self.pleaseWait.timer);
      var drawMethod = methods.draw;
      var removeMethod = methods.remove;
      self.pleaseWait.timer = setInterval(function() {
        if (document.body.contains(self)) {
          drawMethod.call(self);
        } else {
          removeMethod.call(self);
        }
      }, self.pleaseWait.opt.speed);
      return self;
    },
    stop: function() {
      var self = $(this);
      if (typeof self.length != "undefined" && self.length > 0) self = self[0];
      if (typeof self.pleaseWait == "undefined") return;
      if (self.pleaseWait.timer != false) clearTimeout(self.pleaseWait.timer);
      self.pleaseWait.timer = false;
      $(self.pleaseWait.cog).css("display", "none");
      $(self.pleaseWait.overlay).css("display", "none");
      return self;
    },
    draw: function() {
      var self = this;
      if (typeof self.length != "undefined" && self.length > 0) self = self[0];
      if (typeof self.pleaseWait == "undefined") return;

      var rotateTarget = self.pleaseWait.cog;
      if (self.pleaseWait.opt.crazy) rotateTarget = self;

      if (navigator.userAgent.match("MSIE")) {
        rotateTarget.style.msTransform =
          "rotate(" + self.pleaseWait.degrees + "deg)";
      } else if (navigator.userAgent.match("Opera")) {
        rotateTarget.style.OTransform =
          "rotate(" + self.pleaseWait.degrees + "deg)";
      } else {
        rotateTarget.style.transform =
          "rotate(" + self.pleaseWait.degrees + "deg)";
      }
      self.pleaseWait.degrees =
        parseInt(self.pleaseWait.degrees) +
        parseInt(self.pleaseWait.opt.increment);
      if (self.pleaseWait.degrees > 359) {
        self.pleaseWait.degrees = 1;
      }
    },
    remove: function() {
      var self = this;
      if (typeof self.length != "undefined" && self.length > 0) self = self[0];
      if (typeof self.pleaseWait != "undefined") {
        if (self.pleaseWait.timer != false) clearTimeout(self.pleaseWait.timer);

        $(self.pleaseWait.cog).remove();
        $(self.pleaseWait.overlay).remove();

        $(self).removeAttr("data-pleaseWait");
        delete self.pleaseWait.overlay;
        delete self.pleaseWait.opt;
        delete self.pleaseWait.cog;
        delete self.pleaseWait.timer;
        delete self.pleaseWait;
      }
    }
  };
  $.fn.pleaseWait = function(method) {
    if (!methods[method]) {
      var attr = $(this).attr("data-pleaseWait");
      if (typeof attr == "undefined" || attr == false)
        return methods.init.apply(this, arguments);
      method = "start";
    }
    if (methods[method]) {
      return methods[method].apply(
        this,
        Array.prototype.slice.call(arguments, 1)
      );
    } else if (typeof method === "object" || !method) {
      return methods.init.apply(this, arguments);
    } else {
      $.error('pleaseWait plugin :: method "' + method + '" does not exist!');
    }
  };
  $.fn.pleaseWait.defaults = {
    crazy: false,
    speed: 8,
    increment: 2,
    image: "iVBORw0KGgoAAAANSUhEUgAAAEEAAABBCAYAAACO98lFAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAABK1JREFUeNrsXFtIFFEYPqsbZl7S1G5oqETgS2SghUUXIughCiuCHnrrAhFUD0EUUj1E9BBFVBC9RAWBD92IiF4iykCLlCCKkBLU0i5meU/b7f/b79DxtLo7t53dnf3hw5lxZ+bMN//9nF1fMBgUsRSfzxf2eKzHoUqaS/c9xM8NnBQuS5pIifDbqdKRxE2Vd1oT8ghbPKcJkHTCDsIBQhbhDeF1IpJgRRN+E9YQckHIiUTVBKvmcBxksFQTar1IAqv/dWX/IGGK10hgOUXoxXYpYbcXSegFEVL2EWZ7jQQBk5CRgSNFnRdJYOd4RNmvhaP0FAksTYSHyv7RhCOBU+FoMYkcI4xiuzJRQuZEmlBi8npthEvKfh18RHwLFzVaYbOc8AIPUGDikvzQLwkfgcNhCqhFhF1AtToON+CTA4Oac6JTr2hCP+Eyjo0aIILN4AK2PxOq+Py/N4zDpopOQj7e3Grtc+2Ec4RHBq5dj7B5hvAznktpnQQpVUiBy7XPPyOcJbxPpn7CRCRIp7mJsAeVopQA4SbhPMwlqUmQkk3YC0LUaMIqfhGEBOL9Qa2SIIVNYz+hRjvOpnGa0OgFEqSsQDdJzyW2c3fJKyTIULqVsBPm8hRaIrxEgpQCOM4rCKOeJCFhIkBUJHhZUiSkSAAJXvUDOgk8hbbM5PlcMl9LdBJ4Gq6IMN/k+Z3JYA6pqXmYA2vCdJPn9xG6k8EnpBxjKkSmSEjM2sGOcepjtIOEWYQvAt2lGJKQjnsG3SQhk7CesEqEOsuPY0xCERw7t/mG3SBhJWGj+De7NCBCkzUDMSIhSwvrI4QfhDEzJBhduLVAhDpKJWEyx0yQEQsJADLZyyDMxP37hNL4tdMxFqLGqNSO8+wSd5ubXXCMTECO+H+uMwAiBuwyB2Z4HWGtGL8WidXvgQhNxY+5HB38MI0M7fgYTGTECglLCZvF+IkXlgbCbTgkR8KnyUg1FWPVTXwYYx0zQkIpYRv+qtJGuIG/0Ug5zKXfIgl5UO1oJoR9MI9srTgMKv4iGImEGSK0KFO9QA/hlgitRIlWsuBA+aItQMAgCZlwwDkYwweD1TFrxTTt+BDhuz4OvZTmmz3BNjN/X4RWnzQZVM0l8CGsmmVqQmNg9Us6CJAvJ9tg9OhFEvdLOd4fbXTIgi+4B1KMCi/f26Ds3yV0SXPAfbiJU4H/twpt5koZSxkIYBkkvDWTIUKr/DAHEUkTZOJz1SQBPHq1VdcqCTApHUrMZ9XON3mdIUlAOPGbCW2TeG9+uwWKOTVajIijcK5ycWgx1DxgZ+FmZ3uNfcBiZb/ZpgzykxIZ+B5z7M497CShUvHG/PCvbEyRO7SqNSMeSeBwtFDZbzSav0eQHsWz+4T5JYaOklCjXKsLDtFuaVciw3QlfMYFCfMAmZk1OFQ2DGoRq1jY1Ci2SoIPNYYU/h7UNwfrp04tZBbGAwkVyO0FMrPnDvcRRhEtpMwV1r7MZhsJUlrCla0OSLdyH7/yElwj4Y4IrYPusTEkRqzI4STZR7wjfLXcozCTaVn5hqzB2sH2XoXTyVLCit/sG3VU32P9cwYu/X5CrlIRch+w183pwD8CDAC/RBaBiIoXIgAAAABJRU5ErkJggg==",
    imageType: "encoded",
    height: window.innerHeight / 2,
    width: 65
  };
})(jQuery);

function checkSessionJSON(newTab, projectId, subjectId, experimentId, parentProjectId) {
  $('body').pleaseWait();

  // JPETTS: Define a request to XNAT to check if specified JSON data exists
  var oReq = new XMLHttpRequest();
  var url = XNAT.url.rootUrl("/xapi/viewer/projects/" + projectId + "/experiments/" + experimentId + "/exists");

  // Listeners
  oReq.addEventListener('error', function () {
    $('body').pleaseWait('stop');
    console.error('Error in REST call!');
  });

  oReq.addEventListener('abort', function () {
    $('body').pleaseWait('stop');
    console.error('Request was aborted for some reason. Please contact your System Administrator.');
  });

  oReq.addEventListener('load', function () {
    if (oReq.status === 200) {
      // 200 === OK
      console.log('JSON for this session found!');
      console.log('Loading viewer with this JSON.');

      checkJSONAndOpenViewer(newTab, projectId, subjectId, experimentId, parentProjectId);
    } else if (oReq.status === 404) {
      // 404 === NOT_FOUND
      generateJSONOpenViewer(newTab, projectId, subjectId, experimentId, parentProjectId);
    } else if (oReq.status === 403) {
      $('body').pleaseWait('stop');
      console.log('Incorrect permissions');
    } else {
      $('body').pleaseWait('stop');
      console.log("unsuccessful, status: " + oReq.status);
    }
  });

  // REST GET call
  oReq.open('GET', url);
  oReq.setRequestHeader('Accept', 'application/json');
  oReq.send();

}

function checkSubjectForSessionJSON(newTab, projectId, subjectId, parentProjectId) {
  var subjectExperimentListUrl = XNAT.url.rootUrl('/data/archive/projects/'+ projectId + "/subjects/" + subjectId + "/experiments?format=json");

  console.log(subjectExperimentListUrl);

  var xhr = new XMLHttpRequest();

  xhr.addEventListener('error', function () {
    $('body').pleaseWait('stop');
    console.error('Error in REST call!');
  });

  xhr.addEventListener('abort', function () {
    $('body').pleaseWait('stop');
    console.error('Request was aborted for some reason. Please contact your System Administrator.');
  });

  xhr.onload = function () {
    var experimentList = xhr.response.ResultSet.Result;

    console.log("on load, experimentList:");
    console.log(experimentList);

    var sessionsChecked = 0;
    var sessionsThatNeedJSON = [];

    var xhrExists = [];
    for (var i = 0; i < experimentList.length; i++) {
      var experimentId = experimentList[i].ID;
      var experimentLabel = experimentList[i].label;
      var experimentExistsUrl = XNAT.url.rootUrl("/xapi/viewer/projects/" + projectId + "/experiments/" + experimentId + "/exists");

      xhrExists[i] = new XMLHttpRequest();

      console.log(experimentExistsUrl);

      xhrExists[i].onload = function () {
        console.log(this.status);

        if (this.status === 404) {
          sessionsThatNeedJSON.push({
            ID: experimentId,
            label: experimentLabel
          });
        }
        sessionsChecked++;
        console.log(sessionsChecked);
        if (sessionsChecked === experimentList.length) {
          generateJSONOpenSubjectViewer(sessionsThatNeedJSON, newTab, projectId, subjectId, parentProjectId);
        }
      }

      xhrExists[i].addEventListener('error', function () {
        $('body').pleaseWait('stop');
        console.error('Error in REST call!');
      });

      xhrExists[i].addEventListener('abort', function () {
        $('body').pleaseWait('stop');
        console.error('Request was aborted for some reason. Please contact your System Administrator.');
      });

      xhrExists[i].open("GET", experimentExistsUrl);
      xhrExists[i].responseType = "json";
      xhrExists[i].send();
    }

  };


  console.log("GET " + subjectExperimentListUrl);
  xhr.open("GET", subjectExperimentListUrl);
  xhr.responseType = "json";
  xhr.send();
}

function generateJSONOpenSubjectViewer(sessionsThatNeedJSON, newTab, projectId, subjectId, parentProjectId) {
  console.log(sessionsThatNeedJSON);
  console.log('TODO.. generate JSON and then open viewer.');

  if (sessionsThatNeedJSON.length === 0) {
    openSubjectView(newTab, projectId, subjectId, parentProjectId);
    return;
  }

  let message = "Generating missing viewer metadata for sessions:"

  for (let i = 0; i < sessionsThatNeedJSON.length; i++) {
    message = message + " " + sessionsThatNeedJSON[i].label;
  }

  var sessionsGenerated = 0;

  $('body').pleaseWait('stop');
  XNAT.dialog.message(
      'Generating Missing Viewer Data: ' + sessionsGenerated + "/" + sessionsThatNeedJSON.length,
      message + ". If the sessions are very large please wait a minute before attempting to open the viewer. This will only happen once."
  );

  var xhrGenerate = [];
  for (var i = 0; i < sessionsThatNeedJSON.length; i++) {
    var experimentId = sessionsThatNeedJSON[i].ID;
    var experimentExistsUrl = XNAT.url.rootUrl("/xapi/viewer/projects/" + projectId + "/experiments/" + experimentId);

    xhrGenerate[i] = new XMLHttpRequest();

    console.log(experimentExistsUrl);

    xhrGenerate[i].onload = function () {
      console.log(this.status);

      if (this.status === 200) {
        sessionsGenerated++;
      }

      console.log(sessionsGenerated);
      if (sessionsGenerated === sessionsThatNeedJSON.length) {
        openSubjectView(newTab, projectId, subjectId, parentProjectId);
      } else {
        $('body').pleaseWait('stop');

        XNAT.dialog.message(
            'Generating Missing Viewer Data: ' + sessionsGenerated + "/" + sessionsThatNeedJSON.length,
            message + ". If the sessions are very large please wait a minute before attempting to open the viewer. This will only happen once."
        );
      }
    }

    xhrGenerate[i].open("GET", experimentExistsUrl);
    xhrGenerate[i].responseType = "json";
    xhrGenerate[i].send();
  }
}

function openSubjectView(newTab, projectId, subjectId, parentProjectId) {
  var params = '?subjectId=' + subjectId + '&projectId=' + projectId;

  openViewer(params, newTab, parentProjectId);
}

function checkJSONAndOpenViewer(newTab, projectId, subjectId, experimentId, parentProjectId) {
  // Fetch JSON
  var oReq = new XMLHttpRequest();

  // Listeners
  oReq.addEventListener('error', function () {
    $('body').pleaseWait('stop');
    console.error('Error in REST call!');
  });

  oReq.addEventListener('abort', function () {
    $('body').pleaseWait('stop');
    console.error('Request was aborted for some reason. Please contact your System Administrator.');
  });

  oReq.addEventListener('load', function () {
    if (oReq.status === 200) {
      console.log('JSON found.. checking!');
      // TODO -> Check the json!
      console.log(oReq);

      var jsonString = oReq.responseText;
      var studyList = JSON.parse(jsonString); //parses the query result

      console.log(studyList);

      if (studyListEmpty(studyList)) {
        $('body').pleaseWait('stop');

        XNAT.dialog.message(
            'No viewable scans',
            "There are no scans in this session compatible with the OHIF Viewer."
        );
        return;
      }

      getLabelAndOpenViewer(newTab, projectId, subjectId, experimentId, parentProjectId);
    } else if (oReq.status === 403) {
      console.log('Incorrect permissions');

      $('body').pleaseWait('stop');

    } else {
      console.log("unsuccessful, status: " + oReq.status);

      $('body').pleaseWait('stop');
    }
  });

  var jsonRequestUrl = XNAT.url.rootUrl("/xapi/viewer/projects/" + projectId + "/experiments/" + experimentId);

  // REST GET call
  oReq.open('GET', jsonRequestUrl);
  oReq.setRequestHeader('Accept', 'application/json');
  oReq.send();
}

function studyListEmpty(studyList) {
  if (!studyList.studies) {
    console.log('invalid studyList object');
    return true;
  }

  var empty = true;

  for (var i = 0; i < studyList.studies.length; i++) {

    if (!studyList.studies[i] || !studyList.studies[i].series) {
      continue;
    }

    var series = studyList.studies[i].series;

    for (var j = 0; j < series.length; j++) {
      if (series[j].instances.length) {
        empty = false;
        break;
      }
    }
  }

  return empty;
}

function generateJSONOpenViewer(newTab, projectId, subjectId, experimentId, parentProjectId) {
  var oReq = new XMLHttpRequest();
  var url = XNAT.url.rootUrl("/xapi/viewer/projects/" + projectId + "/experiments/" + experimentId);
  console.log("Opening GET XMLHttpRequest to: " + url);

  oReq.addEventListener('load', function () {

    console.log("Request returned, status: " + oReq.status);

    if (oReq.status === 200) {
      console.log('JSON has been created!');
      checkJSONAndOpenViewer(newTab, projectId, subjectId, experimentId, parentProjectId);
    } else if (oReq.status === 403) {
      console.log('Incorrect permissions');

      $('body').pleaseWait('stop');

    } else {
      console.log("unsuccessful, status: " + oReq.status);

      $('body').pleaseWait('stop');
    }
  });

  // REST POST call
  oReq.open('GET', url);
  oReq.setRequestHeader('Accept', 'application/json');
  oReq.send();

  $('body').pleaseWait('stop');

  XNAT.dialog.static.wait(
      "Please wait... generating viewer metadata for session " + experimentLabel + ". This is a one-time operation that may take a few minutes if the session is very large. When complete, the viewer will open."
  );
}

function getLabelAndOpenViewer(newTab, projectId, subjectId, experimentId, parentProjectId) {
  var params = '?subjectId=' + subjectId + '&projectId=' + projectId + '&experimentId=' + experimentId;

  var oReq = new XMLHttpRequest();
  var sessionUrl = XNAT.url.rootUrl("/data/archive/projects/" + projectId + "/subjects/" + subjectId + "/experiments/" + experimentId + "?format=json");

  console.log("Opening GET XMLHttpRequest to: " + sessionUrl);

  oReq.addEventListener('load', function () {
    console.log("Request returned, status: " + oReq.status);

    if (oReq.status === 200) {
      var jsonString = oReq.responseText;

      var sessionJSON = JSON.parse(jsonString);

      params = params +  '&experimentLabel=' + sessionJSON.items[0].data_fields.label;
    }

    openViewer(params, newTab, parentProjectId);
  });

  // Fetch session info to grab label, as its often not in XNAT.data.context.
  oReq.open('GET', sessionUrl);
  oReq.send();
}


function openViewer(params, newTab, parentProjectId) {
  if (parentProjectId) {
    params = params + '&parentProjectId=' + parentProjectId;
  }

  var openViewerUrl = XNAT.url.rootUrl('/VIEWER' + params);

  if (newTab) {
    window.open(openViewerUrl);
    $('body').pleaseWait('stop');
  } else {
    window.location.href = openViewerUrl;
  }
}

