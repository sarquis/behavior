package sarquis.effugium.behavior.magicloto.v2;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import sarquis.effugium.behavior.magicloto.SimuladorDeSorteios;
import sarquis.effugium.behavior.magicloto.util.ComparatorBolaSorteioInteger;

/**
 * OBS: Feito para lidar com sorteios ordenados; pois considera variação máxima
 * de 10 posições (no fator).
 */
public class FuncaoV2 {

    private Random random = new Random();

    private int pontuacao;
    private int qtdSorteios;

    private boolean ocorreuAprendizado = false;

    private int qtd11acertos;
    private int qtd12acertos;
    private int qtd13acertos;
    private int qtd14acertos;
    private int qtd15acertos;

    /**
     * Para gerar a sugestão de aposta.
     */
    private List<String> ultimoSorteio = null;

    /**
     * Utilizado para analisar o passado.
     */
    private List<List<String>> sorteiosParaAnalise = null;

    /**
     * Para analisar o passado.
     * 
     * Era utilizado no passado, mas no momento não está sendo utilizado. Não removi
     * pois pode ser útil no futuro.
     */
    @SuppressWarnings("unused")
    private int qtdAcertosObjetivo = 0;

    /**
     * Tempo decorrido até a função ser encontrada.
     */
    private String tempoDecorrido;

    /**
     * 15 à 18;
     */
    private int qtdNumeros;

    private List<Fator> fatores;

    /**
     * @param qtdNumeros  15 à 18.
     * @param qtdSorteios
     */
    public FuncaoV2(int qtdNumeros, int qtdSorteios) throws Exception {
	if (qtdNumeros < 15 || qtdNumeros > 18) {
	    throw new Exception();
	}

	this.qtdNumeros = qtdNumeros;

	/*
	 * -1 Por que o primeiro sorteio é utilizado como base e não é conferido.
	 */
	this.qtdSorteios = qtdSorteios - 1;

	inicializarFuncaoComValoresRandomicos();
    }

    private void inicializarFuncaoComValoresRandomicos() throws Exception {
	fatores = new ArrayList<Fator>();
	for (int i = 0; i < qtdNumeros; i++) {
	    fatores.add(gerarFatorRandomico());
	}
    }

    /**
     * Variação máxima de 10 posições.
     * 
     * Ex. número 15 + 10 = 25.
     */
    private Fator gerarFatorRandomico() throws Exception {
	Operacao op = Operacao.getEnumByIndex(random.nextInt(2));
	int intAux = random.nextInt(11);
	return new Fator(op, intAux);
    }

    @Override
    public String toString() {
	String aux = "";
	for (Fator fator : fatores) {
	    aux += fator.getOp().getDesc() + String.format("%02d", fator.getValor());
	}
	return aux;
    }

    public List<Fator> getFatores() {
	return fatores;
    }

    public String getTempoDecorrido() {
	return tempoDecorrido;
    }

    public void setTempoDecorrido(String tempoDecorrido) {
	this.tempoDecorrido = tempoDecorrido;
    }

    public int getPontuacao() {
	return pontuacao;
    }

    public String getPontuacaoDesc() {
	String pontuacaoDesc = String.format("%02d", pontuacao) + " / " + String.format("%02d", qtdSorteios);
	Double percAcerto = ((double) pontuacao / (double) qtdSorteios) * 100;
	return pontuacaoDesc + " (" + percAcerto.intValue() + "%)";
    }

    public String getFuncaoDesc() {
	return "{" + toString() + "} " + (ocorreuAprendizado ? "*" : "");
    }

    private String getPercentualSobreSorteios(int value) {
	double var = ((double) value / (double) qtdSorteios) * 100.00;
	if (var == 100.0d) {
	    return "(100%)";
	}
	DecimalFormat df = new DecimalFormat("#,#0.0");
	String aux = String.format("%4s", df.format(var));
	return "(" + aux + "%)";
    }

    private String format2d(int value) {
	if (value == 0) {
	    return "--";
	}
	return String.format("%02d", value);
    }

    public String getResult11Desc() {
	return format2d(qtd11acertos) + getPercentualSobreSorteios(qtd11acertos);
    }

    public String getResult12Desc() {
	return format2d(qtd12acertos) + getPercentualSobreSorteios(qtd12acertos);
    }

    public String getResult13Desc() {
	return format2d(qtd13acertos) + getPercentualSobreSorteios(qtd13acertos);
    }

    public String getResult14Desc() {
	return format2d(qtd14acertos) + getPercentualSobreSorteios(qtd14acertos);
    }

    public String getResult15Desc() {
	return format2d(qtd15acertos) + getPercentualSobreSorteios(qtd15acertos);
    }

    public void setPontuacao(int pontuacao) {
	this.pontuacao = pontuacao;
    }

    public int getQtd11acertos() {
	return qtd11acertos;
    }

    public void setQtd11acertos(int qtd11acertos) {
	this.qtd11acertos = qtd11acertos;
    }

    public int getQtd12acertos() {
	return qtd12acertos;
    }

    public void setQtd12acertos(int qtd12acertos) {
	this.qtd12acertos = qtd12acertos;
    }

    public int getQtd13acertos() {
	return qtd13acertos;
    }

    public void setQtd13acertos(int qtd13acertos) {
	this.qtd13acertos = qtd13acertos;
    }

    public int getQtd14acertos() {
	return qtd14acertos;
    }

    public void setQtd14acertos(int qtd14acertos) {
	this.qtd14acertos = qtd14acertos;
    }

    public int getQtd15acertos() {
	return qtd15acertos;
    }

    public void setQtd15acertos(int qtd15acertos) {
	this.qtd15acertos = qtd15acertos;
    }

    public void setUltimoSorteio(List<String> ultimoSorteio) {
	this.ultimoSorteio = ultimoSorteio;
    }

    public void setSorteiosParaAnalise(List<List<String>> sorteiosParaAnalise) {
	this.sorteiosParaAnalise = sorteiosParaAnalise;
    }

    public void setQtdAcertosObjetivo(int qtdAcertosObjetivo) {
	this.qtdAcertosObjetivo = qtdAcertosObjetivo;
    }

    private String getSugestaoDeAposta(List<String> sorteio) throws Exception {
	if (ultimoSorteio == null) {
	    return "";
	} else {
	    List<Integer> previsao = criarPrevisaoDoSorteioAplicandoFuncao(sorteio);
	    return formatarPrevisaoDoSorteio(previsao);
	}
    }

    private String formatarPrevisaoDoSorteio(List<Integer> previsao) {
	Collections.sort(previsao, new ComparatorBolaSorteioInteger());
	String var = "{ ";
	for (int integer : previsao) {
	    var += String.format("%02d", integer) + " ";
	}
	return (var + "}");
    }

    public List<Integer> criarPrevisaoDoSorteioAplicandoFuncao(List<String> sorteio) throws Exception {
	List<Integer> previsaoDoSorteio = new ArrayList<Integer>();

	int pos = 0;

	for (String bolaSorteada : sorteio) {
	    int bola = Integer.parseInt(bolaSorteada);

	    // Aplicando fator:
	    Fator f = fatores.get(pos);
	    bola = aplicarFatorNaBola(bola, f);

	    // Correção de limite:
	    bola = corrigirBolaForaDoLimite(bola);

	    // Resolver conflito de posição:
	    bola = corrigirConflito(bola, previsaoDoSorteio);

	    if (bola < 1 || bola > 25) {
		throw new Exception("Nova bola inválida.");
	    }

	    previsaoDoSorteio.add(bola);

	    pos++;
	}

	/*
	 * Números Extras - Aposta com mais de 15 números.
	 */
	int qtdExtras = qtdNumeros - 15;
	if (qtdExtras > 0) {
	    for (String bolaSorteada : sorteio) {
		int bola = Integer.parseInt(bolaSorteada);

		// Aplicando fator:
		Fator f = fatores.get(pos);
		bola = aplicarFatorNaBola(bola, f);

		// Correção de limite:
		bola = corrigirBolaForaDoLimite(bola);

		// Resolver conflito de posição:
		bola = corrigirConflito(bola, previsaoDoSorteio);

		if (bola < 1 || bola > 25) {
		    throw new Exception("Nova bola inválida.");
		}

		previsaoDoSorteio.add(bola);

		pos++;

		qtdExtras--;
		if (qtdExtras == 0) {
		    break;
		}
	    }
	}

	return previsaoDoSorteio;
    }

    private int aplicarFatorNaBola(int bola, Fator f) throws Exception {
	if (f.getOp() == Operacao.ADD) {
	    bola = bola + f.getValor();
	} else if (f.getOp() == Operacao.SUB) {
	    bola = bola - f.getValor();
	} else {
	    throw new Exception("Operação inválida.");
	}
	return bola;
    }

    private int corrigirBolaForaDoLimite(int bola) {
	if (bola > 25) {
	    bola = bola - 25;
	} else if (bola < 1) {
	    bola = bola + 25;
	}
	return bola;
    }

    private int corrigirConflito(int bola, List<Integer> bolasJaSalvas) {
	int mover = 1;
	boolean paraDireita = true;
	boolean conflito = true;
	while (conflito) {
	    conflito = false;
	    for (Integer bolaJaSalva : bolasJaSalvas) {
		if (bola == bolaJaSalva) {
		    conflito = true;
		    if (paraDireita) {
			bola = bola + mover;
			paraDireita = false;
		    } else {
			bola = bola - mover;
			paraDireita = true;
		    }

		    // Correção de limite:
		    bola = corrigirBolaForaDoLimite(bola);

		    mover++;
		    break;
		}
	    }
	}
	return bola;
    }

    public void setOcorreuAprendizado(boolean ocorreuAprendizado) {
	this.ocorreuAprendizado = ocorreuAprendizado;
    }

    public String getSugestaoDeAposta1() throws Exception {
	return getSugestaoDeAposta(ultimoSorteio);
    }

    public String getSugestaoDeAposta2() throws Exception {
	return getSugestaoDeAposta(sorteiosParaAnalise.get(0));
    }

    public String getSugestaoDeAposta3() throws Exception {
	return getSugestaoDeAposta(sorteiosParaAnalise.get(1));
    }

    public String getSugestaoDeAposta4() throws Exception {
	return getSugestaoDeAposta(sorteiosParaAnalise.get(2));
    }

    public String getSugestaoDeAposta5() throws Exception {
	return getSugestaoDeAposta(sorteiosParaAnalise.get(3));
    }

    public String getSugestaoDeAposta6() throws Exception {
	return getSugestaoDeAposta(sorteiosParaAnalise.get(4));
    }

    public String getSugestaoDeAposta7() throws Exception {
	return getSugestaoDeAposta(sorteiosParaAnalise.get(5));
    }

    public String getSugestaoDeAposta8() throws Exception {
	return getSugestaoDeAposta(sorteiosParaAnalise.get(6));
    }

    public String getSugestaoDeAposta9() throws Exception {
	return getSugestaoDeAposta(sorteiosParaAnalise.get(7));
    }

    public String getSugestaoDeAposta10() throws Exception {
	return getSugestaoDeAposta(sorteiosParaAnalise.get(8));
    }

    private int getResultadoProximoSorteio(List<String> proximoSorteio, List<String> ultimoSorteioVariavel)
	    throws Exception {
	List<Integer> previsao = criarPrevisaoDoSorteioAplicandoFuncao(ultimoSorteioVariavel);
	int qtdAcertos = SimuladorDeSorteios.verificarAcertosDaPrevisaoNoSorteio(previsao, proximoSorteio);
	return qtdAcertos;
    }

    public String getResultadoProximoSorteio1() throws Exception {
	return String.valueOf(getResultadoProximoSorteio(sorteiosParaAnalise.get(0), ultimoSorteio));
    }

    public String getResultadoProximoSorteio2() throws Exception {
	return String.valueOf(getResultadoProximoSorteio(sorteiosParaAnalise.get(1), sorteiosParaAnalise.get(0)));
    }

    public String getResultadoProximoSorteio3() throws Exception {
	return String.valueOf(getResultadoProximoSorteio(sorteiosParaAnalise.get(2), sorteiosParaAnalise.get(1)));
    }

    public String getResultadoProximoSorteio4() throws Exception {
	return String.valueOf(getResultadoProximoSorteio(sorteiosParaAnalise.get(3), sorteiosParaAnalise.get(2)));
    }

    public String getResultadoProximoSorteio5() throws Exception {
	return String.valueOf(getResultadoProximoSorteio(sorteiosParaAnalise.get(4), sorteiosParaAnalise.get(3)));
    }

    public String getResultadoProximoSorteio6() throws Exception {
	return String.valueOf(getResultadoProximoSorteio(sorteiosParaAnalise.get(5), sorteiosParaAnalise.get(4)));
    }

    public String getResultadoProximoSorteio7() throws Exception {
	return String.valueOf(getResultadoProximoSorteio(sorteiosParaAnalise.get(6), sorteiosParaAnalise.get(5)));
    }

    public String getResultadoProximoSorteio8() throws Exception {
	return String.valueOf(getResultadoProximoSorteio(sorteiosParaAnalise.get(7), sorteiosParaAnalise.get(6)));
    }

    public String getResultadoProximoSorteio9() throws Exception {
	return String.valueOf(getResultadoProximoSorteio(sorteiosParaAnalise.get(8), sorteiosParaAnalise.get(7)));
    }

    public String getResultadoProximoSorteio10() throws Exception {
	return String.valueOf(getResultadoProximoSorteio(sorteiosParaAnalise.get(9), sorteiosParaAnalise.get(8)));
    }

    private String getColor(List<String> proximoSorteio, List<String> ultimoSorteioVariavel) throws Exception {
	int qtdAcertos = getResultadoProximoSorteio(proximoSorteio, ultimoSorteioVariavel);
	return CorPontuacao.obterCorPontuacao(qtdAcertos);
    }

    public String getCorResultado1() throws Exception {
	return getColor(sorteiosParaAnalise.get(0), ultimoSorteio);
    }

    public String getCorResultado2() throws Exception {
	return getColor(sorteiosParaAnalise.get(1), sorteiosParaAnalise.get(0));
    }

    public String getCorResultado3() throws Exception {
	return getColor(sorteiosParaAnalise.get(2), sorteiosParaAnalise.get(1));
    }

    public String getCorResultado4() throws Exception {
	return getColor(sorteiosParaAnalise.get(3), sorteiosParaAnalise.get(2));
    }

    public String getCorResultado5() throws Exception {
	return getColor(sorteiosParaAnalise.get(4), sorteiosParaAnalise.get(3));
    }

    public String getCorResultado6() throws Exception {
	return getColor(sorteiosParaAnalise.get(5), sorteiosParaAnalise.get(4));
    }

    public String getCorResultado7() throws Exception {
	return getColor(sorteiosParaAnalise.get(6), sorteiosParaAnalise.get(5));
    }

    public String getCorResultado8() throws Exception {
	return getColor(sorteiosParaAnalise.get(7), sorteiosParaAnalise.get(6));
    }

    public String getCorResultado9() throws Exception {
	return getColor(sorteiosParaAnalise.get(8), sorteiosParaAnalise.get(7));
    }

    public String getCorResultado10() throws Exception {
	return getColor(sorteiosParaAnalise.get(9), sorteiosParaAnalise.get(8));
    }

}
