package sarquis.effugium.behavior;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class IndexBean implements Serializable {

    private static final long serialVersionUID = 5458937534882689034L;

    private boolean showMainPanel = false;
    private String password = "";
    private int countLoginError;
    private MainPanel mainPanel;

    public IndexBean() {
	super();
	countLoginError = 0;
    }

    public boolean isShowMainPanel() {
	return showMainPanel;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public void login() {
	try {
	    /*
	     * TEMPORARIO LEMBRAR DE DESCOMENTAR;
	     */
//	    if (password.equals("Q=tDHI4rm&%6Z~w$}ySpF7Ksn#w$8R")) {
	    Util.addMessage("Token identificado com sucessso. ADMIM, seja bem-vindo ao Behavior...");
	    mainPanel = new MainPanel();
	    showMainPanel = true;
//	    } else {
//		countLoginError++;
//		if (countLoginError >= 4) {
//		    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
//		    externalContext.redirect("http://stackoverflow.com");
//		} else {
//		    if (countLoginError == 3) {
//			Util.addMessageError("Incorreto... última chance...");
//		    } else {
//			Util.addMessageError("Incorreto...");
//		    }
//		}
//	    }
	} catch (Exception e) {
	    Util.handleBeanException(e);
	}
    }

    public void sair() {
	countLoginError = 0;
	showMainPanel = false;
	mainPanel = null;
	Util.addMessage("Logoff...");
    }

    public MainPanel getMainPanel() {
	return mainPanel;
    }

    public void setMainPanel(MainPanel mainPanel) {
	this.mainPanel = mainPanel;
    }

}
