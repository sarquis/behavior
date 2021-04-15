package sarquis.effugium.behavior;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import sarquis.effugium.behavior.magicloto.util.SqsException;

class Util {
    public static void addMessage(String summary) {
	showMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null));
    }

    public static void addMessageError(String summary) {
	showMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null));
    }

    public static void handleBeanException(Exception e) {
	String msg = "...@#$%!...";
	if (e instanceof SqsException) {
	    msg = e.getMessage();
	} else {
	    e.printStackTrace();
	}
	showMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    private static void showMessage(FacesMessage message) {
	FacesContext.getCurrentInstance().addMessage(null, message);
    }
}
