function behDisableAll() {
	PF('cancelar').enable();
	PF('iniciar').disable();
	PF('download').disable();
	disableUpload();
	PF('qtdSorteiosParaProcessamentoSlider').disable();
	PF('qtdDeExecucoesSlider').disable();
	PF('tamanhoDaMemoriaSlider').disable();
	PF('numAcertosPontuacaoSlider').disable();
	PF('qtdNumApostadosSlider').disable();
}

function behEnableAll() {
	PF('cancelar').disable();
	PF('iniciar').enable();
	PF('download').enable();
	enableUpload();
	PF('qtdSorteiosParaProcessamentoSlider').enable();
	PF('qtdDeExecucoesSlider').enable();
	PF('tamanhoDaMemoriaSlider').enable();
	PF('numAcertosPontuacaoSlider').enable();
	PF('qtdNumApostadosSlider').enable();
}


function disableUpload() {
	PF('uploadWV').disableButton(PF('uploadWV').chooseButton);
	PF('uploadWV').chooseButton.find('input[type="file"]').attr('disabled',
			'true');
}

function enableUpload() {
	if (!PF('uploadWV').files.length) {
		PF('uploadWV').enableButton(PF('uploadWV').chooseButton);
		PF('uploadWV').chooseButton.find('input[type="file"]').removeAttr(
				'disabled');
	}
}