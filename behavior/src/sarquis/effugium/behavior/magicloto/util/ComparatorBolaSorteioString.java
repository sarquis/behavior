package sarquis.effugium.behavior.magicloto.util;

import java.util.Comparator;

public class ComparatorBolaSorteioString implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
	if (Integer.parseInt(o1) < Integer.parseInt(o2))
	    return -1;
	else if (Integer.parseInt(o1) > Integer.parseInt(o2))
	    return +1;
	else
	    return 0;
    }

}
