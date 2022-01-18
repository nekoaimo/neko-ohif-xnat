$(document).on('mousedown', '#ohifViewer', function (e) {
    if (e.button === 2) {
        return;
    }
    e.preventDefault();
    prepForViewer($(this).data('context'), e.button === 1);
});

//Right click.
$(document).on('contextmenu', '#ohifViewer', function (e) {
    e.preventDefault();
    prepForViewer($(this).data('context'), true);
});

function prepForViewer(context, newTab) {
    if (context === 'subject') {
        checkSubjectForSessionJSON(newTab, XNAT.data.context.projectID, XNAT.data.context.subjectID, XNAT.data.context.parentProjectID);
    } else {
        checkSessionJSON(newTab, XNAT.data.context.projectID, XNAT.data.context.subjectID, XNAT.data.context.ID, XNAT.data.context.parentProjectID);
    }
}