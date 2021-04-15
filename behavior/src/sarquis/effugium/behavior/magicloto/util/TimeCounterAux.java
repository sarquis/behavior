package sarquis.effugium.behavior.magicloto.util;

import java.util.Date;

public class TimeCounterAux {
    private Date dataHoraInicio;

    public TimeCounterAux() {
	this.dataHoraInicio = new Date();
    }

    public String tempoDecorrido() {
	Date agora = new Date();
	long diffMilliseconds = agora.getTime() - dataHoraInicio.getTime();
	long diffSegundos = diffMilliseconds / 1000;
	long diffMinutos = diffSegundos / 60;
	long diffHoras = diffMinutos / 60;

	diffSegundos = diffSegundos - (diffMinutos * 60);
	diffMinutos = diffMinutos - (diffHoras * 60);

	return String.format("%02d", diffHoras) + "h " + String.format("%02d", diffMinutos) + "m "
		+ String.format("%02d", diffSegundos) + "s";
    }

}
