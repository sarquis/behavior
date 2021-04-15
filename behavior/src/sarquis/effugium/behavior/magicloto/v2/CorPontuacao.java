package sarquis.effugium.behavior.magicloto.v2;

public class CorPontuacao {
    public static final String RED = "#e61e29";

    public static String obterCorPontuacao(int qtdAcertos) {
	switch (qtdAcertos) {
	case 11:
	    return "#faa429";
	case 12:
	    return "#ffcd1c";
	case 13:
	    return "#dee025";
	case 14:
	    return "#96cb3f";
	case 15:
	    return "#48b54b";
	default:
	    return RED;
	}
    }
}
