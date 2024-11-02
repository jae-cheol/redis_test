import java.io.File;

public class Main {
	// [2024-10-23 JC]

	public static void main(String[] args) throws Exception {
		RdbFilePrinter r = new RdbFilePrinter();
		r.printRdbFile(new File("F:\\경력관리\\회사생활\\한화\\2024연구과제\\dump.rdb"));
	}

}