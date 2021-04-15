package sarquis.effugium.behavior.magicloto.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.faces.context.FacesContext;

public class FileReaderLotoFacil {

    public static final String DEFAULT_PATH = "resources\\sorteios.csv";
    public static final String CSV_FILE_NAME = "sorteios.csv";
    public static final String UPLOADED_PATH = "/behaviortemp";

    /**
     * Responsável por criar a matriz com todos os sorteios lendo direto do arquivo
     * CSV com os resultados da Loteria da Caixa.
     * 
     * Why I choose use ArrayList: KEEPING THE ORDER
     * 
     * Both ArrayList and LinkedList are implementation of List interface. They both
     * maintain the elements insertion order which means while displaying ArrayList
     * and LinkedList elements the result set would be having the same order in
     * which the elements got inserted into the List.
     * 
     * https://beginnersbook.com/2013/12/difference-between-arraylist-and-linkedlist-in-java/
     * 
     * @throws Exception
     */
    public List<List<String>> readFileLoto(boolean uploadedfile) throws Exception {
	List<List<String>> records;
	if (uploadedfile) {
	    records = readFromTempUploadPath();
	} else {
	    records = readFromDefaultPath();
	}
	return records;
    }

    private List<List<String>> readFromDefaultPath() throws Exception {
	List<List<String>> records = new ArrayList<>();
	try (InputStream inputStream = FacesContext.getCurrentInstance().getExternalContext()
		.getResourceAsStream(DEFAULT_PATH)) {
	    if (inputStream == null || inputStream.available() == 0) {
		throw new SqsException("Arquivo de sorteios não encontrado.");
	    }
	    records = readSorteiosFromInputStream(inputStream);
	}
	return records;
    }

    private List<List<String>> readFromTempUploadPath() throws Exception {
	List<List<String>> records = new ArrayList<>();
	File file = new File(UPLOADED_PATH + "/" + CSV_FILE_NAME);
	try (InputStream inputStream = new FileInputStream(file)) {
	    if (inputStream == null || inputStream.available() == 0) {
		throw new SqsException("Arquivo de sorteios não encontrado.");
	    }
	    records = readSorteiosFromInputStream(inputStream);
	}
	return records;
    }

    private List<List<String>> readSorteiosFromInputStream(InputStream inputStream) throws Exception {
	String msgErro = "Abortando leitura do arquivo: ";
	List<List<String>> records = new ArrayList<>();
	int lineCounter = 0;
	try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
	    String line;
	    while ((line = br.readLine()) != null) {
		lineCounter++;
		String[] values = line.split(";");
		if (values.length != 15) {
		    throw new SqsException(
			    msgErro + "quantidade de números do sorteio é inválida. Linha: " + lineCounter);
		}
		for (String testeDeValor : values) {
		    if (testeDeValor.length() != 2) {
			throw new SqsException(msgErro + "valor inválido na linha: " + lineCounter);
		    }
		}
		records.add(Arrays.asList(values));
	    }
	}

	for (List<String> list : records) {
	    Collections.sort(list, new ComparatorBolaSorteioString());
	}

	return records;
    }
}
