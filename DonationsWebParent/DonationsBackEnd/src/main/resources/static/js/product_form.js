dropdownBrands = $("#brand");
dropdownCategories = $("#category");
let extraImagesCount = 0;
addMoreDetailBtn = $("#addMoreDetail");
divProductDetails = $("#divProductDetails");


$(document).ready(function() {
	$("#shortDescription").richText();
	$("#fullDescription").richText();
	dropdownBrands.change(function() {
		dropdownCategories.empty();
		getCategories();
	});
	getCategories();
	$("input[name='extraImage']").each(function(index) {
		$(this).change(function() {
			showExtraImageThumbnail(this, index);
		});
	});

	addMoreDetailBtn.click(function() {
		addNewDetail();
	})
});
function checkFileSize(fileInput) {
	fileSize = fileInput.files[0].size;
	if (fileSize > MAX_FILE_SIZE) {
		this
			.setCustomValidity("You must choose an image less than " + MAX_FILE_SIZE);
		fileInput.reportValidity();
		return false;
	} else {
		fileInput.setCustomValidity("");
		return true;
	}
}

function showExtraImageThumbnail(fileInput, index) {
	var file = fileInput.files[0];
	var reader = new FileReader();
	reader.onload = function(e) {
		$("#extraThumbnail" + index).attr("src", e.target.result)
	}
	reader.readAsDataURL(file);
	if (index >= extraImagesCount) {
		addNextExtraImageSection(index + 1);
	}

}

function addNewDetail() {
	console.log("ABC");
	htmlNewDetails = `
			<div class="row align-items-center col mb-3">
			<div class="col-auto">
				<label for="detailName" class="col-form-label">Name</label>
			</div>
			<div class="col-auto">
				<input type="text" class="form-control" maxlength="255"
					name="detailName" id="detailName" />
			</div>
			<div class="col-auto">
				<label for="detailValue" class="col-form-label">Value</label>
			</div>
			<div class="col-auto">
				<input type="text" class="form-control" maxlength="255"
					name="detailValue" id="detailValue" />
			</div>
			<div class="col-auto">
				<a style="float: right;"
					class="btn fas fa-2x fa-times-circle icon-dark" href=""
					title="Remove this detail"></a>
			</div>
		</div>
		`;
	divProductDetails.append(htmlNewDetails);
}

function addNextExtraImageSection(index) {
	html = `
			<div class="col" id="divExtraImage${index}">
					<div class="col my-3 border rounded px-3 pb-3 pt-2">
						<div class="align-middle" id="extraImageHeader${index}">
							<label
								class="text-primary align-middle"
								for="extraImage${index + 1}">Extra Image #${index + 1}</label> 
						</div>
						<div class="align-middle text-center">
							<img alt="Extra images #${index + 1} previews" id="extraThumbnail${index}"
								src="${defaultImageThumbnailSrc}"
								class="mx-auto d-block img-fluid" />
						</div>
						<div class="input-group mt-2">
							<input type="file"
								onchange="showExtraImageThumbnail(this, ${index})"
								class="form-control"  name="extraImage" />
						</div>
					</div> 
				</div>
	`;

	htmlRemoveExtraImage = `
		<a style="float: right;" 
			class="btn fas fa-times-circle icon-dark pe-0"
			href="javascript:removeExtraImage(${index - 1})"
			title="Remove this image"></a>
	`;
	$("#divProductImages").append(html);
	$("#extraImageHeader" + (index - 1)).append(htmlRemoveExtraImage);
	extraImagesCount++;
}
function removeExtraImage(index) {
	$("#divExtraImage" + index).remove();
}

function getCategories() {
	brandId = dropdownBrands.val();
	url = brandModuleURL + "/" + brandId + "/categories";
	$.get(url, function(responseJson) {
		$.each(responseJson, function(index, category) {
			$("<option>").val(category.id).text(category.name)
				.appendTo(dropdownCategories);
		});
	});
}
function checkUnique(form) {
	productId = $("#id").val();
	productName = $("#name").val();
	csrfValue = $("input[name='_csrf']").val();
	params = {
		id: productId,
		name: productName,
		_csrf: csrfValue
	};
	$.post(checkUniqueUrl, params, function(response) {
		if (response == "OK") {
			form.submit();
		} else if (response == "Duplicate") {
			showWarningModal("There is another product having same name " + productName);
		} else {
			showErrorModal("Unknown response from server");
		}

	}).fail(function() {
		showErrorModal("Could not connect to the server");
	});
	return false;
}