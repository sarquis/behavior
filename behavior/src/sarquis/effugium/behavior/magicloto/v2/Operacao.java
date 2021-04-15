package sarquis.effugium.behavior.magicloto.v2;

public enum Operacao {
    ADD("+"),
    SUB("-");

    private final String desc;

    Operacao(String desc) {
	this.desc = desc;
    }

    String getDesc() {
	return desc;
    }

    static Operacao getEnumByIndex(int i) throws Exception {
	int counter = 0;
	for (Operacao op : Operacao.values()) {
	    if (counter++ == i) {
		return op;
	    }
	}
	throw new Exception();
    }

}
