package sarquis.effugium.behavior;

import sarquis.effugium.behavior.magicloto.v2.CorPontuacao;

public class Resultado {
    private String resultadoCalculado;
    private String resultadoAleatorio;
    private String corCalculado;
    private String corAleatorio;

    public String getResultadoCalculado() {
	return resultadoCalculado;
    }

    public void setResultadoCalculado(String resultadoCalculado) {
	this.resultadoCalculado = resultadoCalculado;
    }

    public String getResultadoAleatorio() {
	return resultadoAleatorio;
    }

    public void setResultadoAleatorio(String resultadoAleatorio) {
	this.resultadoAleatorio = resultadoAleatorio;
    }

    public String getCorCalculado() {
	return corCalculado;
    }

    public void setCorCalculado(int valor, int qtdAcertos) throws Exception {
	if (valor == 0) {
	    corCalculado = CorPontuacao.RED;
	} else {
	    corCalculado = CorPontuacao.obterCorPontuacao(qtdAcertos);
	}
    }

    public String getCorAleatorio() {
	return corAleatorio;
    }

    public void setCorAleatorio(int valor, int qtdAcertos) throws Exception {
	if (valor == 0) {
	    corAleatorio = CorPontuacao.RED;
	} else {
	    corAleatorio = CorPontuacao.obterCorPontuacao(qtdAcertos);
	}
    }

}
