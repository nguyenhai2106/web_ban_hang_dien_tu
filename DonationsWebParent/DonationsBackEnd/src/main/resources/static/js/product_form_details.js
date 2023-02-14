addMoreDetailBtn = $("#addMoreDetail");
divProductDetails = $("#divProductDetails");

$(document).ready(function() {
	$("a[name='linkRemoveDetail']").each(function(index) {
		$(this).click(function() {
			removeDetailSectionByIndex(index);
		})
	});
});

function addNextDetailSection() {
	allDivDetails = $("[id^='divDetail']");
	divDetailsCount = allDivDetails.length;
	htmlNewDetails = `
		<div class="row align-items-center col-12 col-lg-6 mb-3" id="divDetail${divDetailsCount}">
				<div class="col-2">
					<label for="detailNames" class="col-form-label">Name</label>
				</div>
				<div class="col-4">
					<input type="text" class="form-control" maxlength="255"
						name="detailNames" id="detailNames" />
				</div>
				<div class="col-2">
					<label for="detailValues" class="col-form-label">Value</label>
				</div>
				<div class="col-3">
					<input type="text" class="form-control" maxlength="255"
						name="detailValues" id="detailValues" />
				</div>
				<input type="hidden" name="detailIDs" value="0" />
			</div>
		`;
	divProductDetails.append(htmlNewDetails);

	previousDivDetailSection = allDivDetails.last();
	previousDivDetailId = previousDivDetailSection.attr("id");
	htmlRemoveDetail = `
		<div class="col-1">
					<a class="btn fas fa-times-circle icon-dark ps-0" href="javascript:removeDetailSectionById('${previousDivDetailId}')"
						title="Remove this detail"></a>
				</div>
	`;
	previousDivDetailSection.append(htmlRemoveDetail);
	$("input[name='detailNames']").last().focus();
}

function removeDetailSectionById(id) {
	console.log(id + "remove");
	$("#" + id).remove();
}

function removeDetailSectionByIndex(index) {
	$("#divDetail" + index).remove();
}

