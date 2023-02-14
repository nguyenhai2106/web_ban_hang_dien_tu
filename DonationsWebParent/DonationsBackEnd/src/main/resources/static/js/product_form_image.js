let extraImagesCount = 0;

$(document).ready(function() {
	$("input[name='extraImage']").each(function(index) {
		extraImagesCount++;
		$(this).change(function() {
			if (!checkFileSize) {
				return;
			}
			showExtraImageThumbnail(this, index);
		})
	})

	$("a[name='linkRemoveExtraImage'").each(function(index) {
		$(this).click(function() {
			removeExtraImage(index);
		});
	});
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
	fileName = file.name;
	imageNameHiddenField = $("#imageName" + index);
	if (imageNameHiddenField.length) {
		imageNameHiddenField.val(fileName);
	}

	var reader = new FileReader();
	reader.onload = function(e) {
		$("#extraThumbnail" + index).attr("src", e.target.result)
	}
	reader.readAsDataURL(file);
	console.log(index, extraImagesCount);
	if (index >= extraImagesCount - 1) {
		addNextExtraImageSection(index + 1);
	}

}

function addNextExtraImageSection(index) {
	html = `
			<div class="col" id="divExtraImage${index}">
					<div class="col my-3 border rounded px-3 pb-3 pt-2">
						<div class="align-middle" id="extraImageHeader${index}">
							<label
								style="text-overflow: ellipsis; white-space: nowrap; width: 90%; overflow: hidden;"
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
