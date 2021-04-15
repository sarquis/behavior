package sarquis.effugium.behavior.magicloto;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import sarquis.effugium.behavior.BehaviorThread;
import sarquis.effugium.behavior.magicloto.util.ComparatorBolaSorteioInteger;
import sarquis.effugium.behavior.magicloto.util.ComparatorFuncaoPontuacao;
import sarquis.effugium.behavior.magicloto.util.TimeCounterAux;
import sarquis.effugium.behavior.magicloto.v2.Fator;
import sarquis.effugium.behavior.magicloto.v2.FuncaoV2;

public class MagicLoto {

    /*
     * Atividades do desenvolvimento do programa:
     */

    /*
     * Pensando em...
     * 
     * 99. Buscar uma forma de gerar um aposta de 16 numeros, pq eu posso pagar mais
     * caro e jogar com 16 numeros, em vez de 15.
     * 
     */

    /*
     * INFO RELEVANTES (Atualizado em 13/07/2020)
     * 
     * - Percentuais / Probabilidade normais de acerto:
     * 
     * 11 ~ 1/11 (9,17%) ; 12 ~ 1/59 (1,7%) ; 13 ~ 1/691 (0,1%)
     * 
     * 14 ~ 1/21.791 (0,005%) ; 15 ~ 1/3.268.760 (0,00003%)
     * 
     * - Custo das apostas:
     * 
     * { 15 números 2,50; 16 números 40,00; 17 números 340,00; 18 números 2.040,00 }
     * 
     * - Valor a receber (por quantidade de acertos):
     * 
     * 11 R$ 5,00 ; 12 R$ 10,00 ; 13 R$ 25,00
     * 
     * 15 R$ 500.000,00 ; 14 R$ 1.000,00
     * 
     */

    /**
     * Quantidade de simulações/tentativas.
     */
    private int qtdTentativas;

    /**
     * Tamanho da memória.
     */
    private int tamanhoDaMemoria;

    /**
     * Sorteios, histórico.
     */
    private List<List<String>> sorteios;

    /**
     * Utilizado para analisar o passado.
     */
    private List<List<String>> proximosSorteios;

    /*
     * Objetos utilitários:
     */

    private Random random = new Random();
    private TimeCounterAux utilTimeLoto;
    private static DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###,###");

    private SimuladorDeSorteios simuladorDeSorteios;

    private int qtdAcertosParaPontuar;

    private int qtdNumerosFuncao;

    List<String> ultimoSorteio;

    public MagicLoto(int qtdTentativas, int tamanhoDaMemoria, List<List<String>> sorteios, int qtdAcertosParaPontuar,
	    List<List<String>> proximosSorteios, int qtdNumerosFuncao) {
	this.qtdTentativas = qtdTentativas * 1000 * 1000;
	this.tamanhoDaMemoria = tamanhoDaMemoria;
	this.sorteios = sorteios;
	simuladorDeSorteios = new SimuladorDeSorteios(qtdAcertosParaPontuar);
	this.proximosSorteios = proximosSorteios;
	this.qtdAcertosParaPontuar = qtdAcertosParaPontuar;
	this.qtdNumerosFuncao = qtdNumerosFuncao;
	ultimoSorteio = sorteios.get(sorteios.size() - 1);
    }

    public void executarProcessamento(BehaviorThread behaviorThread) throws Exception {

	utilTimeLoto = new TimeCounterAux();

	/*
	 * Criar X funções randômicas iniciais:
	 */
	List<FuncaoV2> funcaoTopList = new ArrayList<FuncaoV2>();
	List<FuncaoV2> funcaoParaReferenciaList = new ArrayList<FuncaoV2>();

	for (int i = 0; i < tamanhoDaMemoria; i++) {
	    FuncaoV2 funcao = new FuncaoV2(qtdNumerosFuncao, sorteios.size());

	    simuladorDeSorteios.rodarFuncaoNosSorteios(funcao, sorteios);

	    funcaoTopList.add(funcao);
	    funcaoParaReferenciaList.add(funcao);
	}

	Collections.sort(funcaoTopList, new ComparatorFuncaoPontuacao());
	Collections.sort(funcaoParaReferenciaList, new ComparatorFuncaoPontuacao());

	behaviorThread.setFuncaoTopList(funcaoTopList);
	behaviorThread.setFuncaoParaReferenciaList(funcaoParaReferenciaList);

	/*
	 * Laço principal...
	 */
	for (int i = 0; i < qtdTentativas; i++) {
	    if (behaviorThread.isStop()) {
		return;
	    }

	    if ((i % 50000) == 0) {
		String progresso = "Execução: " + decimalFormat.format(i) + " de "
			+ decimalFormat.format(qtdTentativas);
		behaviorThread.setProgressString(progresso);
		behaviorThread.setProgress(i);
	    }

	    FuncaoV2 funcao = new FuncaoV2(qtdNumerosFuncao, sorteios.size());

	    pegarInstrucoesDasMelhoresFuncoes(funcao, funcaoTopList);

	    simuladorDeSorteios.rodarFuncaoNosSorteios(funcao, sorteios);

	    atualizarTop5List(funcao, funcaoTopList);

	    behaviorThread.setFuncaoTopList(funcaoTopList);
	}

	behaviorThread.setProgressString(
		"Execução: " + decimalFormat.format(qtdTentativas) + " de " + decimalFormat.format(qtdTentativas));

	behaviorThread.setFuncaoTopList(funcaoTopList);

	behaviorThread.setUltimoSorteio(montarStringDeSorteio(ultimoSorteio));

	for (FuncaoV2 funcao : funcaoTopList) {
	    funcao.setUltimoSorteio(ultimoSorteio);
	    funcao.setSorteiosParaAnalise(proximosSorteios);
	    funcao.setQtdAcertosObjetivo(qtdAcertosParaPontuar);
	}

	for (FuncaoV2 funcao : funcaoParaReferenciaList) {
	    funcao.setUltimoSorteio(ultimoSorteio);
	    funcao.setSorteiosParaAnalise(proximosSorteios);
	    funcao.setQtdAcertosObjetivo(qtdAcertosParaPontuar);
	}

	behaviorThread.setTempoTotalDecorrido("Tempo total: " + utilTimeLoto.tempoDecorrido());
    }

    private String montarStringDeSorteio(List<String> ultimoSorteio) {
	String sorteio = "";
	for (String string : ultimoSorteio) {
	    sorteio += string + " ";
	}
	return sorteio;
    }

    private void atualizarTop5List(FuncaoV2 novafuncao, List<FuncaoV2> funcaoTopList) throws Exception {
	int lastIndex = funcaoTopList.size() - 1;

	if (novafuncao.getPontuacao() > funcaoTopList.get(lastIndex).getPontuacao()) {

	    boolean funcaoEquivalenteEncontrada = verificarSeFuncaoJaExisteNaMemoria(novafuncao, funcaoTopList);

	    if (!funcaoEquivalenteEncontrada) {
		novafuncao.setTempoDecorrido(utilTimeLoto.tempoDecorrido());

		funcaoTopList.remove(lastIndex);
		funcaoTopList.add(novafuncao);

		Collections.sort(funcaoTopList, new ComparatorFuncaoPontuacao());
	    }
	}
    }

    private boolean verificarSeFuncaoJaExisteNaMemoria(FuncaoV2 novafuncao, List<FuncaoV2> funcaoTopList)
	    throws Exception {
	boolean funcaoEquivalenteEncontrada = false;

	List<Integer> prevNova = novafuncao.criarPrevisaoDoSorteioAplicandoFuncao(ultimoSorteio);
	Collections.sort(prevNova, new ComparatorBolaSorteioInteger());

	for (FuncaoV2 funcaoMemoria : funcaoTopList) {
	    List<Integer> prevMemoria = funcaoMemoria.criarPrevisaoDoSorteioAplicandoFuncao(ultimoSorteio);
	    Collections.sort(prevMemoria, new ComparatorBolaSorteioInteger());
	    if (prevMemoria.containsAll(prevNova)) {
		funcaoEquivalenteEncontrada = true;
		break;
	    }
	}
	return funcaoEquivalenteEncontrada;
    }

    private void pegarInstrucoesDasMelhoresFuncoes(FuncaoV2 funcao, List<FuncaoV2> funcaoTopList) throws Exception {

	/*
	 * Verifico se devo pegar algo do Top5 ou tentar uma completamente nova.
	 * Aplicando percentual de 50% para pegar algo dos melhores.
	 */

	if (random.nextBoolean()) {
	    // tentar algo completamente novo...
	    return;
	}

	// Pegar instrucoes dos melhores...
	int qtdInstrucoesPegar = (random.nextInt(qtdNumerosFuncao) + 1);
	int numeroDaFuncaoTopAleatorio = random.nextInt(funcaoTopList.size());

	aplicarInstrucoesTop5(funcao, funcaoTopList.get(numeroDaFuncaoTopAleatorio), qtdInstrucoesPegar);

	funcao.setOcorreuAprendizado(true);
    }

    private void aplicarInstrucoesTop5(FuncaoV2 funcao, FuncaoV2 funcaoTop, int qtdInstrucoesPegar) throws Exception {

	List<Integer> posJaEscolhidaList = new ArrayList<Integer>();
	for (int j = 0; j < qtdInstrucoesPegar; j++) {

	    int posicaoParaCopiar = 0;
	    boolean jaEscolhida = true;

	    while (jaEscolhida) {
		posicaoParaCopiar = random.nextInt(qtdNumerosFuncao);
		jaEscolhida = false;
		for (int posJaEscolhida : posJaEscolhidaList) {
		    if (posicaoParaCopiar == posJaEscolhida) {
			jaEscolhida = true;
			break;
		    }
		}
	    }

	    posJaEscolhidaList.add(posicaoParaCopiar);

	    // Copiando os valores...
	    Fator fatorFuncaoTop = funcaoTop.getFatores().get(posicaoParaCopiar);
	    Fator fatorNovaFuncao = funcao.getFatores().get(posicaoParaCopiar);
	    fatorNovaFuncao.setOp(fatorFuncaoTop.getOp());
	    fatorNovaFuncao.setValor(fatorFuncaoTop.getValor());

	    /*
	     * Ajuste de tom: para cima ou para baixo...
	     */
	    if (random.nextBoolean()) { // Verificando se ocorre ajuste de tom ou não.
		if (fatorFuncaoTop.getValor() == 10) {
		    fatorNovaFuncao.setValor(9);
		} else if (fatorFuncaoTop.getValor() == 0) {
		    fatorNovaFuncao.setValor(1);
		} else {
		    if (random.nextBoolean()) {
			// Tom para cima.
			fatorNovaFuncao.setValor(fatorNovaFuncao.getValor() + 1);
		    } else {
			// Tom para baixo.
			fatorNovaFuncao.setValor(fatorNovaFuncao.getValor() - 1);
		    }
		}
	    }
	}
    }

}
