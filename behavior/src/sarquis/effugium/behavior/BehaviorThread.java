package sarquis.effugium.behavior;

import java.util.ArrayList;
import java.util.List;

import sarquis.effugium.behavior.magicloto.MagicLoto;
import sarquis.effugium.behavior.magicloto.v2.FuncaoV2;

public class BehaviorThread extends Thread {

    private MagicLoto magicLoto;
    private Exception e;
    private int progress;
    private String progressString;
    private boolean stop;
    private String tempoTotalDecorrido;
    private List<FuncaoV2> funcaoTopList;
    private List<FuncaoV2> funcaoParaReferenciaList;
    private String ultimoSorteio;

    public BehaviorThread(int qtdTentativas, int tamanhoDaMemoria, List<List<String>> sorteios,
	    int qtdAcertosParaPontuar, List<List<String>> proximosSorteios, int qtdNumerosFuncao) {
	super();
	this.magicLoto = new MagicLoto(qtdTentativas, tamanhoDaMemoria, sorteios, qtdAcertosParaPontuar,
		proximosSorteios, qtdNumerosFuncao);
	stop = false;
	progress = 0;
	progressString = "";
	tempoTotalDecorrido = "";
	funcaoTopList = new ArrayList<FuncaoV2>();
	funcaoParaReferenciaList = new ArrayList<FuncaoV2>();
	ultimoSorteio = "";
    }

    @Override
    public void run() {
	try {
	    magicLoto.executarProcessamento(this);
	} catch (Exception e) {
	    this.e = e;
	}
    }

    public Exception getE() {
	return e;
    }

    public int getProgress() {
	return progress;
    }

    public void setProgress(int progress) {
	this.progress = progress;
    }

    public String getProgressString() {
	return progressString;
    }

    public void setProgressString(String progressString) {
	this.progressString = progressString;
    }

    public boolean isStop() {
	return stop;
    }

    public void setStop(boolean stop) {
	this.stop = stop;
    }

    public String getTempoTotalDecorrido() {
	return tempoTotalDecorrido;
    }

    public void setTempoTotalDecorrido(String tempoTotalDecorrido) {
	this.tempoTotalDecorrido = tempoTotalDecorrido;
    }

    public List<FuncaoV2> getFuncaoTopList() {
	return funcaoTopList;
    }

    public void setFuncaoTopList(List<FuncaoV2> funcaoTopList) {
	this.funcaoTopList = funcaoTopList;
    }

    public String getUltimoSorteio() {
	return ultimoSorteio;
    }

    public void setUltimoSorteio(String ultimoSorteio) {
	this.ultimoSorteio = ultimoSorteio;
    }

    public List<FuncaoV2> getFuncaoParaReferenciaList() {
	return funcaoParaReferenciaList;
    }

    public void setFuncaoParaReferenciaList(List<FuncaoV2> funcaoParaReferenciaList) {
	this.funcaoParaReferenciaList = funcaoParaReferenciaList;
    }

}
