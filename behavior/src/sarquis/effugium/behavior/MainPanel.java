package sarquis.effugium.behavior;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import sarquis.effugium.behavior.magicloto.SimuladorDeSorteios;
import sarquis.effugium.behavior.magicloto.util.FileReaderLotoFacil;
import sarquis.effugium.behavior.magicloto.util.SqsException;
import sarquis.effugium.behavior.magicloto.v2.FuncaoV2;

public class MainPanel {

    /*
     * Arquivo dos sorteios:
     */

    private FileReaderLotoFacil fileReader = new FileReaderLotoFacil();
    private List<List<String>> sorteios = new ArrayList<List<String>>();
    private List<List<String>> sorteiosSelecionados = new ArrayList<List<String>>();
    private String sorteiosEmFormatoTexto;
    private String sorteiosSelecionadosEmFormatoTexto;
    private StreamedContent file;
    private int qtdSorteiosParaProcessamento = 20;
    private boolean analisarPassado = false;
    private String sorteioConsiderarUltimo;

    /*
     * Utilizado para analisar o passado.
     */

    private List<List<String>> proximosSorteios;

    /*
     * Parâmetros de Processamento:
     */

    private int qtdExecucoes = 8;
    private int tamanhoMemoria = 6;
    private int numAcertosParaPontuacao = 11;
    private int qtdNumerosApostados = 15;
    private Integer progress;
    private String andamento = "";
    private String tempoTotalDecorrido = "";

    private boolean rodando = false;

    private BehaviorThread beThread;

    /*
     * Calculador
     */

    private String calculadorFuncao;
    private String calculadorSorteio;
    private String calculadorResultado;

    public MainPanel() {
	lerArquivoDeSorteios(false);
    }

    private void lerArquivoDeSorteios(boolean uploadedfile) {
	try {
	    sorteios = fileReader.readFileLoto(uploadedfile);
	    montarSorteiosEmFormatoTexto();
	    Util.addMessage("Sucesso na leitura do arquivo, " + sorteios.size() + " sorteios carregados.");
	} catch (Exception e) {
	    Util.handleBeanException(e);
	}
    }

    public void iniciarProcessamento() {
	try {
	    progress = 0;
	    andamento = "Execução:";
	    tempoTotalDecorrido = "";
	    filtrarSorteios();
	    beThread = new BehaviorThread(qtdExecucoes, tamanhoMemoria, sorteiosSelecionados, numAcertosParaPontuacao,
		    proximosSorteios, qtdNumerosApostados);
	    beThread.start();
	    Util.addMessage("Processamento iniciado...");
	    rodando = true;
	} catch (Exception e) {
	    cancel();
	    Util.handleBeanException(e);
	}
    }

    public void cancel() {
	rodando = false;
	if (beThread != null && beThread.isAlive()) {
	    beThread.setStop(true);
	    beThread.setProgress(0);
	    beThread.setProgressString("");
	    beThread.setTempoTotalDecorrido("");
	}
	progress = 0;
	andamento = "";
	tempoTotalDecorrido = "";
    }

    public void onComplete() {
	rodando = false;
	tempoTotalDecorrido = beThread.getTempoTotalDecorrido();
	Util.addMessage("Processamento finalizado.");
    }

    public void change() {
	if (beThread != null) {
	    andamento = beThread.getProgressString();
	}
    }

    public Integer getProgress() {
	try {
	    if (beThread != null) {
		Double calculoDoProgresso = (double) beThread.getProgress();
		calculoDoProgresso = (calculoDoProgresso / (qtdExecucoes * 1000 * 1000)) * 100;
		progress = calculoDoProgresso.intValue();
		if (progress >= 100 && beThread.isAlive()) {
		    progress = 99;
		} else if (!beThread.isAlive()) {
		    progress = 100;
		}
	    }
	} catch (Exception e) {
	    Util.handleBeanException(e);
	}
	return progress;
    }

    private void filtrarSorteios() throws SqsException {
	sorteiosSelecionados = new ArrayList<List<String>>();
	sorteiosSelecionadosEmFormatoTexto = "";
	proximosSorteios = new ArrayList<List<String>>();
	int qtdSorteiosAnalise = 10;
	int counterProximosSorteios = 0;

	List<List<String>> sorteiosFiltradosFase01 = new ArrayList<List<String>>();

	if (analisarPassado) {
	    if (sorteioConsiderarUltimo == null || sorteioConsiderarUltimo.isEmpty()
		    || (Integer.parseInt(sorteioConsiderarUltimo) > (sorteios.size() - qtdSorteiosAnalise))) {
		throw new SqsException("Valor inválido para último sorteio.");
	    }
	    int counter = 0;
	    for (List<String> sorteio : sorteios) {
		counter++;
		if (counter <= Integer.parseInt(sorteioConsiderarUltimo)) {
		    sorteiosFiltradosFase01.add(sorteio);
		} else {
		    proximosSorteios.add(sorteio);
		    counterProximosSorteios++;
		    if (counterProximosSorteios >= qtdSorteiosAnalise) {
			break;
		    }
		}
	    }
	} else {
	    for (List<String> sorteio : sorteios) {
		sorteiosFiltradosFase01.add(sorteio);
	    }
	}

	if (qtdSorteiosParaProcessamento > sorteiosFiltradosFase01.size()) {
	    throw new SqsException("Valor inválido para seleção de sorteios.");
	}

	int counter = 0;
	int limit = sorteiosFiltradosFase01.size() - qtdSorteiosParaProcessamento;
	for (List<String> sorteio : sorteiosFiltradosFase01) {
	    counter++;
	    if (counter <= limit) {
		continue;
	    }
	    sorteiosSelecionados.add(sorteio);
	}

	montarSorteiosSelecionadosEmFormatoTexto();
    }

    public String montarSorteiosEmFormatoTexto() {
	sorteiosEmFormatoTexto = "";
	int counter = 0;
	for (List<String> sorteio : sorteios) {
	    sorteiosEmFormatoTexto += String.format("%04d", ++counter) + " : ";
	    for (String numeroSorteado : sorteio) {
		sorteiosEmFormatoTexto += numeroSorteado + " ";
	    }
	    sorteiosEmFormatoTexto += "\n";
	}
	return sorteiosEmFormatoTexto;
    }

    public String montarSorteiosSelecionadosEmFormatoTexto() {
	sorteiosSelecionadosEmFormatoTexto = "";
	int counter = 0;
	for (List<String> sorteio : sorteiosSelecionados) {
	    sorteiosSelecionadosEmFormatoTexto += String.format("%04d", ++counter) + " : ";
	    for (String numeroSorteado : sorteio) {
		sorteiosSelecionadosEmFormatoTexto += numeroSorteado + " ";
	    }
	    sorteiosSelecionadosEmFormatoTexto += "\n";
	}
	return sorteiosSelecionadosEmFormatoTexto;
    }

    /**
     * Download
     */
    public StreamedContent getFile() {
	file = null;
	try {
	    file = DefaultStreamedContent.builder().name(FileReaderLotoFacil.CSV_FILE_NAME).contentType("text/plain")
		    .stream(() -> FacesContext.getCurrentInstance().getExternalContext()
			    .getResourceAsStream(FileReaderLotoFacil.DEFAULT_PATH))
		    .build();
	} catch (Exception e) {
	    Util.handleBeanException(e);
	}
	return file;
    }

    /**
     * Upload
     */
    public void handleFileUpload(FileUploadEvent event) {
	try {
	    Util.addMessage("Arquivo recebido: " + event.getFile().getFileName());

	    File file = new File(FileReaderLotoFacil.UPLOADED_PATH);
	    file.mkdir();
	    String filePath = FileReaderLotoFacil.UPLOADED_PATH + "/" + FileReaderLotoFacil.CSV_FILE_NAME;
	    file = new File(filePath);

	    try (OutputStream outputStream = new FileOutputStream(file)) {
		InputStream inputStream = event.getFile().getInputStream();
		IOUtils.copy(inputStream, outputStream);
		inputStream.close();
	    }

	    lerArquivoDeSorteios(true);
	} catch (Exception e) {
	    Util.handleBeanException(e);
	}
    }

    public void calcularFuncao() {
	try {
	    calculadorResultado = "";
	    calculadorResultado = SimuladorDeSorteios.calcularFuncao(calculadorFuncao, calculadorSorteio);
	} catch (Exception e) {
	    Util.handleBeanException(e);
	}
    }

    public String getQtdSorteiosCarregados() {
	return String.valueOf(sorteios.size());
    }

    public String getSorteiosEmFormatoTexto() {
	return sorteiosEmFormatoTexto;
    }

    public String getSorteiosSelecionadosEmFormatoTexto() {
	return sorteiosSelecionadosEmFormatoTexto;
    }

    public int getQtdSorteiosParaProcessamento() {
	return qtdSorteiosParaProcessamento;
    }

    public void setQtdSorteiosParaProcessamento(int qtdSorteiosParaProcessamento) {
	this.qtdSorteiosParaProcessamento = qtdSorteiosParaProcessamento;
    }

    public boolean isAnalisarPassado() {
	return analisarPassado;
    }

    public void setAnalisarPassado(boolean analisarPassado) {
	this.analisarPassado = analisarPassado;
    }

    public String getSorteioConsiderarUltimo() {
	return sorteioConsiderarUltimo;
    }

    public void setSorteioConsiderarUltimo(String sorteioConsiderarUltimo) {
	this.sorteioConsiderarUltimo = sorteioConsiderarUltimo;
    }

    public int getQtdExecucoes() {
	return qtdExecucoes;
    }

    public void setQtdExecucoes(int qtdExecucoes) {
	this.qtdExecucoes = qtdExecucoes;
    }

    public int getTamanhoMemoria() {
	return tamanhoMemoria;
    }

    public void setTamanhoMemoria(int tamanhoMemoria) {
	this.tamanhoMemoria = tamanhoMemoria;
    }

    public int getNumAcertosParaPontuacao() {
	return numAcertosParaPontuacao;
    }

    public void setNumAcertosParaPontuacao(int numAcertosParaPontuacao) {
	this.numAcertosParaPontuacao = numAcertosParaPontuacao;
    }

    public int getQtdNumerosApostados() {
	return qtdNumerosApostados;
    }

    public void setQtdNumerosApostados(int qtdNumerosApostados) {
	this.qtdNumerosApostados = qtdNumerosApostados;
    }

    public String getAndamento() {
	return andamento;
    }

    public String getTempoTotalDecorrido() {
	return tempoTotalDecorrido;
    }

    /*
     * Painel de Resultados:
     */

    public List<FuncaoV2> getFuncaoTopList() {
	if (beThread != null) {
	    return beThread.getFuncaoTopList();
	} else {
	    return new ArrayList<FuncaoV2>();
	}
    }

    public List<FuncaoV2> getFuncaoParaReferenciaList() {
	if (beThread != null) {
	    return beThread.getFuncaoParaReferenciaList();
	} else {
	    return new ArrayList<FuncaoV2>();
	}
    }

    public String getUltimoSorteio() {
	if (beThread != null) {
	    return beThread.getUltimoSorteio();
	} else {
	    return "";
	}
    }

    public boolean isProcessoFinalizado() {
	return (tempoTotalDecorrido != null && !tempoTotalDecorrido.isEmpty());
    }

    private String listToString(List<List<String>> listaParam, int posicao) {
	String aux = "";
	if (listaParam != null && !listaParam.isEmpty()) {
	    List<String> listaAux = listaParam.get(posicao);
	    for (String string : listaAux) {
		aux += string + " ";
	    }
	}
	return aux;
    }

    public String getProximoSorteio1() {
	return listToString(proximosSorteios, 0);
    }

    public String getProximoSorteio2() {
	return listToString(proximosSorteios, 1);
    }

    public String getProximoSorteio3() {
	return listToString(proximosSorteios, 2);
    }

    public String getProximoSorteio4() {
	return listToString(proximosSorteios, 3);
    }

    public String getProximoSorteio5() {
	return listToString(proximosSorteios, 4);
    }

    public String getProximoSorteio6() {
	return listToString(proximosSorteios, 5);
    }

    public String getProximoSorteio7() {
	return listToString(proximosSorteios, 6);
    }

    public String getProximoSorteio8() {
	return listToString(proximosSorteios, 7);
    }

    public String getProximoSorteio9() {
	return listToString(proximosSorteios, 8);
    }

    public String getProximoSorteio10() {
	return listToString(proximosSorteios, 9);
    }

    public String getCalculadorFuncao() {
	return calculadorFuncao;
    }

    public void setCalculadorFuncao(String calculadorFuncao) {
	this.calculadorFuncao = calculadorFuncao;
    }

    public String getCalculadorSorteio() {
	return calculadorSorteio;
    }

    public void setCalculadorSorteio(String calculadorSorteio) {
	this.calculadorSorteio = calculadorSorteio;
    }

    public String getCalculadorResultado() {
	return calculadorResultado;
    }

    public boolean isRodando() {
	return rodando;
    }

    public List<Resultado> getResultadoList() {
	List<Resultado> list = new ArrayList<Resultado>();

	try {
	    List<FuncaoV2> refList = getFuncaoParaReferenciaList();
	    List<FuncaoV2> topList = getFuncaoTopList();

	    if (!isProcessoFinalizado()) {
		return list;
	    }

	    int qtd11acertosRefList = 0;
	    int qtd12acertosRefList = 0;
	    int qtd13acertosRefList = 0;
	    int qtd14acertosRefList = 0;
	    int qtd15acertosRefList = 0;

	    int qtd11acertosTopList = 0;
	    int qtd12acertosTopList = 0;
	    int qtd13acertosTopList = 0;
	    int qtd14acertosTopList = 0;
	    int qtd15acertosTopList = 0;

	    for (FuncaoV2 f : refList) {
		qtd11acertosRefList += getQtdAcertos(f, 11);
		qtd12acertosRefList += getQtdAcertos(f, 12);
		qtd13acertosRefList += getQtdAcertos(f, 13);
		qtd14acertosRefList += getQtdAcertos(f, 14);
		qtd15acertosRefList += getQtdAcertos(f, 15);
	    }

	    for (FuncaoV2 f : topList) {
		qtd11acertosTopList += getQtdAcertos(f, 11);
		qtd12acertosTopList += getQtdAcertos(f, 12);
		qtd13acertosTopList += getQtdAcertos(f, 13);
		qtd14acertosTopList += getQtdAcertos(f, 14);
		qtd15acertosTopList += getQtdAcertos(f, 15);
	    }

	    String strAux = "Quantidade de jogos com ";

	    Resultado r = new Resultado();
	    r.setResultadoAleatorio(strAux + "11 acertos: " + qtd11acertosRefList);
	    r.setResultadoCalculado(strAux + "11 acertos: " + qtd11acertosTopList);
	    r.setCorAleatorio(qtd11acertosRefList, 11);
	    r.setCorCalculado(qtd11acertosTopList, 11);
	    list.add(r);
	    r = new Resultado();
	    r.setResultadoAleatorio(strAux + "12 acertos: " + qtd12acertosRefList);
	    r.setResultadoCalculado(strAux + "12 acertos: " + qtd12acertosTopList);
	    r.setCorAleatorio(qtd12acertosRefList, 12);
	    r.setCorCalculado(qtd12acertosTopList, 12);
	    list.add(r);
	    r = new Resultado();
	    r.setResultadoAleatorio(strAux + "13 acertos: " + qtd13acertosRefList);
	    r.setResultadoCalculado(strAux + "13 acertos: " + qtd13acertosTopList);
	    r.setCorAleatorio(qtd13acertosRefList, 13);
	    r.setCorCalculado(qtd13acertosTopList, 13);
	    list.add(r);
	    r = new Resultado();
	    r.setResultadoAleatorio(strAux + "14 acertos: " + qtd14acertosRefList);
	    r.setResultadoCalculado(strAux + "14 acertos: " + qtd14acertosTopList);
	    r.setCorAleatorio(qtd14acertosRefList, 14);
	    r.setCorCalculado(qtd14acertosTopList, 14);
	    list.add(r);
	    r = new Resultado();
	    r.setResultadoAleatorio(strAux + "15 acertos: " + qtd15acertosRefList);
	    r.setResultadoCalculado(strAux + "15 acertos: " + qtd15acertosTopList);
	    r.setCorAleatorio(qtd15acertosRefList, 15);
	    r.setCorCalculado(qtd15acertosTopList, 15);
	    list.add(r);

	} catch (Exception e) {
	    Util.handleBeanException(e);
	}

	return list;
    }

    private int getQtdAcertos(FuncaoV2 f, int qtdAcertos) throws NumberFormatException, Exception {
	int i = 0;
	if (Integer.parseInt(f.getResultadoProximoSorteio1()) == qtdAcertos)
	    i++;
	if (Integer.parseInt(f.getResultadoProximoSorteio2()) == qtdAcertos)
	    i++;
	if (Integer.parseInt(f.getResultadoProximoSorteio3()) == qtdAcertos)
	    i++;
	if (Integer.parseInt(f.getResultadoProximoSorteio4()) == qtdAcertos)
	    i++;
	if (Integer.parseInt(f.getResultadoProximoSorteio5()) == qtdAcertos)
	    i++;
	if (Integer.parseInt(f.getResultadoProximoSorteio6()) == qtdAcertos)
	    i++;
	if (Integer.parseInt(f.getResultadoProximoSorteio7()) == qtdAcertos)
	    i++;
	if (Integer.parseInt(f.getResultadoProximoSorteio8()) == qtdAcertos)
	    i++;
	if (Integer.parseInt(f.getResultadoProximoSorteio9()) == qtdAcertos)
	    i++;
	if (Integer.parseInt(f.getResultadoProximoSorteio10()) == qtdAcertos)
	    i++;
	return i;
    }

}
