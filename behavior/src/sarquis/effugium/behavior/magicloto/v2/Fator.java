package sarquis.effugium.behavior.magicloto.v2;

public class Fator {
    private Operacao op;
    private int valor;

    public Fator(Operacao op, int valor) throws Exception {
	super();
	this.op = op;
	this.valor = valor;
    }

    public Operacao getOp() {
	return op;
    }

    public void setOp(Operacao op) {
	this.op = op;
    }

    public int getValor() {
	return valor;
    }

    public void setValor(int valor) throws Exception {
	if (valor < 0 || valor > 10) {
	    throw new Exception();
	}
	this.valor = valor;
    }

    @Override
    public boolean equals(Object object) {
	Fator f = (Fator) object;
	return (f.op == op && f.valor == valor);
    }

}
