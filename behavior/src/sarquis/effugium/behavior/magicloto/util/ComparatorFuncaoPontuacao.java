package sarquis.effugium.behavior.magicloto.util;

import java.util.Comparator;

import sarquis.effugium.behavior.magicloto.v2.FuncaoV2;

public class ComparatorFuncaoPontuacao implements Comparator<FuncaoV2> {

    @Override
    public int compare(FuncaoV2 o1, FuncaoV2 o2) {
	if (o1.getPontuacao() > o2.getPontuacao())
	    return -1;
	else if (o1.getPontuacao() < o2.getPontuacao())
	    return +1;
	else
	    return 0;
    }

}
