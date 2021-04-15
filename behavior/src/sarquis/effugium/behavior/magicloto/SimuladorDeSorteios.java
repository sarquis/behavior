package sarquis.effugium.behavior.magicloto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sarquis.effugium.behavior.magicloto.util.ComparatorBolaSorteioInteger;
import sarquis.effugium.behavior.magicloto.util.SqsException;
import sarquis.effugium.behavior.magicloto.v2.Fator;
import sarquis.effugium.behavior.magicloto.v2.FuncaoV2;
import sarquis.effugium.behavior.magicloto.v2.Operacao;

public class SimuladorDeSorteios {

    private int qtdAcertosParaPontuar;

    public SimuladorDeSorteios(int qtdAcertosParaPontuar) {
	this.qtdAcertosParaPontuar = qtdAcertosParaPontuar;
    }

    public void rodarFuncaoNosSorteios(FuncaoV2 funcao, List<List<String>> sorteios) throws Exception {
	int pontuacao = 0;
	List<Integer> previsaoDoSorteio = new ArrayList<Integer>();

	for (List<String> sorteio : sorteios) {

	    /*
	     * No primeiro sorteio não tem como pontuar, é utilizado apenas para criação da
	     * previsão. Por isso é desconsiderado.
	     */

	    if (!previsaoDoSorteio.isEmpty()) {

		int qtdAcertos = verificarAcertosDaPrevisaoNoSorteio(previsaoDoSorteio, sorteio);

		if (qtdAcertos < 5 || qtdAcertos > 15)
		    throw new Exception("Quantidade de acertos inválida.");

		aplicarPontosDeGanhador(qtdAcertos, funcao);

		/*
		 * PONTUAÇÃO:
		 * 
		 * Considerando apenas quem alcançou o mínimo acertos exigido no painel de
		 * controle.
		 */

		if (qtdAcertos >= qtdAcertosParaPontuar)
		    pontuacao++;

	    }

	    previsaoDoSorteio = funcao.criarPrevisaoDoSorteioAplicandoFuncao(sorteio);
	}

	funcao.setPontuacao(pontuacao);
    }

    /**
     * Método utilitário para ser usado como uma calculadora/validação. Será usado
     * na tela principal para validar resultados.
     * 
     * @throws Exception
     */
    public static String calcularFuncao(String funcaoStr, String sorteioStr) throws Exception {
	FuncaoV2 funcao = null;
	List<String> sorteio = new ArrayList<String>();

	try {

	    // Exemplo: {+02-20+02+15-02+13-11+14+14+04+24+04+07-15-13} *

	    funcaoStr = funcaoStr.replace("{", "");
	    funcaoStr = funcaoStr.replace("}", "");
	    funcaoStr = funcaoStr.replace("*", "");
	    funcaoStr = funcaoStr.trim();

	    if (funcaoStr.length() < 45 || funcaoStr.length() > 54) {
		throw new Exception();
	    }
	    int qtdNumeros = funcaoStr.length() / 3;
	    funcao = new FuncaoV2(qtdNumeros, 2);
	    for (int i = 0; i < 15; i++) {
		funcao.getFatores().set(i, getFatorValueFuncao(funcaoStr, i));
	    }
	} catch (Exception e) {
	    throw new SqsException("Não foi possível reconhecer a função.");
	}

	try {
	    // Exemplo: 14 07 09 02 06 22 21 24 17 01 15 12 25 05 18
	    sorteioStr = sorteioStr.trim();
	    String[] str = sorteioStr.split(" ");
	    for (int i = 0; i < str.length; i++) {
		sorteio.add(str[i]);
	    }
	    if (sorteio.isEmpty()) {
		throw new Exception();
	    }
	    if (sorteio.size() != 15) {
		throw new Exception();
	    }
	} catch (Exception e) {
	    throw new SqsException("Não foi possível reconhecer o sorteio.");
	}

	List<Integer> previsao = funcao.criarPrevisaoDoSorteioAplicandoFuncao(sorteio);
	Collections.sort(previsao, new ComparatorBolaSorteioInteger());
	String var = "{ ";
	for (int integer : previsao) {
	    var += String.format("%02d", integer) + " ";
	}
	return (var + "}");
    }

    private static Fator getFatorValueFuncao(String funcaoStr, int posicao) throws Exception {
	int begin = 3 * posicao;
	int end = begin + 3;
	int value = Integer.parseInt(funcaoStr.substring(begin, end));
	Operacao op = ((value < 0) ? Operacao.SUB : Operacao.ADD);
	Fator fator = new Fator(op, Math.abs(value));
	return fator;
    }

    private void aplicarPontosDeGanhador(long qtdAcertos, FuncaoV2 funcao) throws Exception {
	if (qtdAcertos < 11) {
	    return;
	} else if (qtdAcertos == 11) {
	    funcao.setQtd11acertos(funcao.getQtd11acertos() + 1);
	} else if (qtdAcertos == 12) {
	    funcao.setQtd12acertos(funcao.getQtd12acertos() + 1);
	} else if (qtdAcertos == 13) {
	    funcao.setQtd13acertos(funcao.getQtd13acertos() + 1);
	} else if (qtdAcertos == 14) {
	    funcao.setQtd14acertos(funcao.getQtd14acertos() + 1);
	} else { // if (qtdAcertos == 15)
	    funcao.setQtd15acertos(funcao.getQtd15acertos() + 1);
	}
    }

    public static int verificarAcertosDaPrevisaoNoSorteio(List<Integer> previsaoDoSorteio, List<String> sorteio)
	    throws Exception {

	if (previsaoDoSorteio.isEmpty())
	    throw new Exception("Previsão do sorteio inválida.");

	int acertos = 0;
	for (int bolaPrevista : previsaoDoSorteio) {
	    for (String bolaSorteada : sorteio) {
		if (bolaPrevista == Integer.parseInt(bolaSorteada)) {
		    acertos++;
		    break;
		}
	    }
	}
	return acertos;
    }

}
